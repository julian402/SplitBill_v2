package ue.edu.co.splitbill.manager;

import android.content.Context;
import android.content.SharedPreferences;

import ue.edu.co.splitbill.entity.User;

// Guarda en SharedPreferences quien inicio sesion, para no pedir el login cada vez que se abre la app
public class SessionManager {

    private static final String PREFERENCES_NAME = "splitbill_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final long NO_USER = -1;
    private final SharedPreferences preferences;

    // Abre el archivo de preferencias de la app
    public SessionManager(Context context) {
        this.preferences = context.getApplicationContext()
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    // Se llama despues del login o el registro
    public void saveUser(User user) {
        this.preferences.edit()
                .putLong(KEY_USER_ID, user.getId())
                .putString(KEY_USER_NAME, user.getName())
                .putString(KEY_USER_EMAIL, user.getEmail())
                .apply();
    }

    // Si hay un id guardado es porque hay sesion
    public boolean isLoggedIn() {
        return getUserId() != NO_USER;
    }

    // Id del usuario logueado (-1 si no hay nadie)
    public long getUserId() {
        return this.preferences.getLong(KEY_USER_ID, NO_USER);
    }

    public String getUserName() {
        return this.preferences.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return this.preferences.getString(KEY_USER_EMAIL, "");
    }

    // Borra todo al cerrar sesion
    public void logout() {
        this.preferences.edit().clear().apply();
    }
}
