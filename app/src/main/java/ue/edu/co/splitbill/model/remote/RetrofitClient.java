package ue.edu.co.splitbill.model.remote;

import android.util.Log;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ue.edu.co.splitbill.BuildConfig;
import ue.edu.co.splitbill.entity.ApiMessage;

// Prepara Retrofit una sola vez y lo reusa en toda la app
public class RetrofitClient {

    private static final String TAG = "RetrofitClient";
    private static Retrofit retrofit;

    // Constructor privado: todo se usa de forma estatica
    private RetrofitClient() {
    }

    // Crea Retrofit la primera vez y devuelve el ApiService listo para usar
    public static ApiService getService() {
        if (retrofit == null) {
            // Muestra en el Logcat cada peticion y su respuesta
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

            // Tiempos amplios: un servidor gratuito puede tardar en despertar
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    // La direccion sale de local.properties (ver app/build.gradle.kts) y termina en /
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    // Cuando el servidor responde con error (400, 404, 409...) manda {"message": "..."}: se saca ese texto
    public static String getErrorMessage(Response<?> response, String defaultMessage) {
        try (ResponseBody errorBody = response.errorBody()) {
            if (errorBody != null) {
                ApiMessage apiMessage = new Gson().fromJson(errorBody.charStream(), ApiMessage.class);
                if (apiMessage != null && apiMessage.getMessage() != null) {
                    return apiMessage.getMessage();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "ERROR AL LEER EL MENSAJE DE ERROR DEL SERVIDOR", e);
        }
        return defaultMessage;
    }
}
