package ue.edu.co.splitbill;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ue.edu.co.splitbill.controller.HomeActivity;
import ue.edu.co.splitbill.controller.LoginActivity;
import ue.edu.co.splitbill.manager.SessionManager;

// Pantalla principal: es la que abre la app. Muestra el logo y manda al inicio si ya hay sesion, o al login si no
public class MainActivity extends AppCompatActivity {

    // Tiempo que se ve el logo antes de pasar a la siguiente pantalla
    private static final long SPLASH_DELAY = 1200;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private SessionManager sessionManager;

    // Arma la pantalla y programa el cambio a la siguiente
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initObjects();
        this.handler.postDelayed(this::openNextScreen, SPLASH_DELAY);
    }

    // Si se sale de la app antes de que pase el tiempo, no se abre nada
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.handler.removeCallbacksAndMessages(null);
    }

    // Con sesion guardada va directo al inicio; si no, al login
    private void openNextScreen() {
        Class<?> next = this.sessionManager.isLoggedIn() ? HomeActivity.class : LoginActivity.class;
        startActivity(new Intent(this, next));
        // Se cierra para que el boton atras no vuelva a esta pantalla
        finish();
    }

    // Prepara lo que usa la pantalla
    private void initObjects() {
        this.sessionManager = new SessionManager(this);
    }
}
