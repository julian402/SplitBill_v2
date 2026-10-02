package ue.edu.co.splitbill.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ue.edu.co.splitbill.R;
import ue.edu.co.splitbill.entity.User;
import ue.edu.co.splitbill.manager.SessionManager;
import ue.edu.co.splitbill.model.AuthRepository;
import ue.edu.co.splitbill.model.remote.RetrofitClient;

// Iniciar sesion con correo y contrasena. MainActivity la abre cuando no hay sesion guardada
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnGoToRegister;
    private LinearProgressIndicator pbLoading;
    private AuthRepository authRepository;
    private SessionManager sessionManager;
    private String email;
    private String password;

    // Arma la pantalla y conecta los botones
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initObjects();
        this.btnLogin.setOnClickListener(this::login);
        this.btnGoToRegister.setOnClickListener(this::goToRegister);
    }

    // Manda el correo y la contrasena al servidor y guarda la sesion si todo sale bien
    private void login(View view) {
        if (!getData()) {
            return;
        }
        showLoading(true);
        // enqueue hace la peticion en segundo plano; la respuesta llega a onResponse u onFailure
        this.authRepository.login(this.email, this.password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    sessionManager.saveUser(response.body());
                    goToHome();
                } else {
                    showToast(RetrofitClient.getErrorMessage(response, getString(R.string.msgLoginError)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                showLoading(false);
                showToast(getString(R.string.msgConnectionError));
            }
        });
    }

    //metodo para capturar la data de la pantalla y validarla
    private boolean getData() {
        this.email = this.etEmail.getText().toString().trim();
        this.password = this.etPassword.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(this.email).matches()) {
            this.etEmail.setError(getString(R.string.errEmail));
            return false;
        }
        if (this.password.isEmpty()) {
            this.etPassword.setError(getString(R.string.errPasswordRequired));
            return false;
        }
        return true;
    }

    // Abre la pantalla de crear cuenta
    private void goToRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    // Abre el inicio y cierra el login para que no se pueda volver con atras
    private void goToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    // Muestra la ruedita de carga y bloquea el boton mientras responde el servidor
    private void showLoading(boolean loading) {
        this.pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        this.btnLogin.setEnabled(!loading);
    }

    // Mensajito corto en la parte de abajo
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Conecta las variables con las vistas del XML (findViewById)
    private void initObjects() {
        this.etEmail = findViewById(R.id.etEmail);
        this.etPassword = findViewById(R.id.etPassword);
        this.btnLogin = findViewById(R.id.btnLogin);
        this.btnGoToRegister = findViewById(R.id.btnGoToRegister);
        this.pbLoading = findViewById(R.id.pbLoading);
        this.authRepository = new AuthRepository();
        this.sessionManager = new SessionManager(this);
    }
}
