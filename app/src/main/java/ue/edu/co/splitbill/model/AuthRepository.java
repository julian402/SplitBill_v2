package ue.edu.co.splitbill.model;

import retrofit2.Call;
import ue.edu.co.splitbill.entity.User;
import ue.edu.co.splitbill.model.remote.ApiService;
import ue.edu.co.splitbill.model.remote.RetrofitClient;

// Las Activities le piden los datos a los repositorios y no saben nada de Retrofit
public class AuthRepository {

    private final ApiService service;

    // Toma el servicio de Retrofit que ya esta armado
    public AuthRepository() {
        this.service = RetrofitClient.getService();
    }

    // Manda correo y contrasena al servidor
    public Call<User> login(String email, String password) {
        return this.service.login(new User(email, password));
    }

    // El servidor guarda la contrasena cifrada con BCrypt
    public Call<User> register(String name, String email, String password) {
        return this.service.register(new User(name, email, password));
    }
}
