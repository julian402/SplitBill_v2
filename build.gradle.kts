// Top-level build file where you can add configuration options common to all sub-projects/modules.
import java.util.Properties

plugins {
    alias(libs.plugins.android.application) apply false
}

// OneDrive bloquea los archivos de build mientras Gradle los escribe. Quien tenga el proyecto dentro de
// OneDrive puede mandar las salidas a otra carpeta con una linea en su local.properties:
//     splitbill.buildDir=C:/Temp/splitbill2-build
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}
localProperties.getProperty("splitbill.buildDir")?.let { buildRoot ->
    allprojects {
        layout.buildDirectory.set(file("$buildRoot/${project.name}"))
    }
}
