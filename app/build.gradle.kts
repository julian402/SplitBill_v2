import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

android {
    namespace = "ue.edu.co.splitbill"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        // Distinto del SplitBill original para poder tener las dos apps instaladas a la vez
        applicationId = "ue.edu.co.splitbill.v2"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Direccion del backend. Por defecto 10.0.2.2, que es el computador visto desde el emulador.
        // En un celular fisico: splitbill.apiBaseUrl=http://localhost:8080/ en local.properties
        // y "adb reverse tcp:8080 tcp:8080".
        val apiBaseUrl = localProperties.getProperty("splitbill.apiBaseUrl", "http://10.0.2.2:8080/")
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.recyclerview)

    // Retrofit arma las peticiones HTTP a partir de una interface y Gson convierte el JSON en objetos
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp.logging)

    // Room: capa sobre SQLite con anotaciones. Guarda el historial de la cuenta rapida
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    testImplementation(libs.junit)
}

// Con el backend en el PC, cada celular o emulador necesita "adb reverse" para que su localhost llegue
// al PC, y se pierde cada vez que el dispositivo se reconecta. Si local.properties trae
//     splitbill.adbReversePort=8080
// se hace solo en todos los dispositivos conectados antes de cada compilacion (incluido el boton Run).
// Si no hay dispositivos o no se encuentra adb, no pasa nada: la compilacion sigue igual.
val adbReversePort: String? = localProperties.getProperty("splitbill.adbReversePort")
if (adbReversePort != null) {
    val sdkDir = localProperties.getProperty("sdk.dir") ?: System.getenv("ANDROID_HOME") ?: ""
    val adbName = if (System.getProperty("os.name").lowercase().contains("windows")) "adb.exe" else "adb"
    val adbPath = File(sdkDir, "platform-tools/$adbName").absolutePath
    //copia local: la tarea no puede guardar referencias al script (configuration cache)
    val port: String = adbReversePort
    val adbReverse = tasks.register("adbReverse") {
        description = "Ejecuta adb reverse tcp:$port en todos los dispositivos conectados"
        doLast {
            try {
                val list = ProcessBuilder(adbPath, "devices").redirectErrorStream(true).start()
                val devices = list.inputStream.bufferedReader().readLines().drop(1)
                list.waitFor()
                for (line in devices) {
                    val parts = line.trim().split(Regex("\\s+"))
                    if (parts.size < 2 || parts[1] != "device") {
                        continue
                    }
                    ProcessBuilder(adbPath, "-s", parts[0], "reverse", "tcp:$port", "tcp:$port")
                        .redirectErrorStream(true).start().waitFor()
                    logger.lifecycle("adb reverse tcp:$port -> ${parts[0]}")
                }
            } catch (e: Exception) {
                logger.lifecycle("adb reverse no se pudo ejecutar: ${e.message}")
            }
        }
    }
    tasks.matching { it.name == "preBuild" }.configureEach { dependsOn(adbReverse) }
}
