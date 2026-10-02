package ue.edu.co.splitbill.entity;

// Usuario con cuenta. La contrasena solo viaja al registrarse o iniciar sesion; el servidor nunca la devuelve
public class User {

    private Long id;
    private String name;
    private String email;
    private String password;

    // Constructor vacio: Gson lo necesita para crear el objeto a partir del JSON
    public User() {
    }

    // Para iniciar sesion
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Para registrarse
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Para ver el objeto completo en el Logcat
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
