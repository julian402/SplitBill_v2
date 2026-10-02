package ue.edu.co.splitbill.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

// Crear cuenta. Al terminar deja la sesion iniciada y abre el inicio
public class RegisterActivity extends AppCompatActivity {

    private static final int MIN_PASSWORD_LENGTH = 6;
    private ImageButton btnBack;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private LinearProgressIndicator pbLoading;
    private AuthRepository authRepository;
    private SessionManager sessionManager;
    private String name;
    private String email;
    private String password;

    // Arma la pantalla y conecta los botones
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initObjects();
        this.btnBack.setOnClickListener(view -> finish());
        this.btnRegister.setOnClickListener(this::register);
    }

    // Crea la cuenta en el servidor y deja la sesion iniciada
    private void register(View view) {
        if (!getData()) {
            return;
        }
        showLoading(true);
        this.authRepository.register(this.name, this.email, this.password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    sessionManager.saveUser(response.body());
                    showToast(getString(R.string.msgWelcome, response.body().getName()));
                    // Cierra el login que quedo atras: el inicio queda como unica pantalla
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    showToast(RetrofitClient.getErrorMessage(response, getString(R.string.msgRegisterError)));
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
        this.name = this.etName.getText().toString().trim();
        this.email = this.etEmail.getText().toString().trim();
        this.password = this.etPassword.getText().toString();
        String confirmPassword = this.etConfirmPassword.getText().toString();
        if (this.name.isEmpty()) {
            this.etName.setError(getString(R.string.errNameRequired));
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(this.email).matches()) {
            this.etEmail.setError(getString(R.string.errEmail));
            return false;
        }
        if (this.password.length() < MIN_PASSWORD_LENGTH) {
            this.etPassword.setError(getString(R.string.errPasswordLength));
            return false;
        }
        if (!this.password.equals(confirmPassword)) {
            this.etConfirmPassword.setError(getString(R.string.errPasswordMatch));
            return false;
        }
        return true;
    }

    // Muestra la ruedita de carga y bloquea el boton mientras responde el servidor
    private void showLoading(boolean loading) {
        this.pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        this.btnRegister.setEnabled(!loading);
    }

    // Mensajito corto en la parte de abajo
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Conecta las variables con las vistas del XML (findViewById)
    private void initObjects() {
        this.btnBack = findViewById(R.id.btnBack);
        this.etName = findViewById(R.id.etName);
        this.etEmail = findViewById(R.id.etEmail);
        this.etPassword = findViewById(R.id.etPassword);
        this.etConfirmPassword = findViewById(R.id.etConfirmPassword);
        this.btnRegister = findViewById(R.id.btnRegister);
        this.pbLoading = findViewById(R.id.pbLoading);
        this.authRepository = new AuthRepository();
        this.sessionManager = new SessionManager(this);
    }
}
