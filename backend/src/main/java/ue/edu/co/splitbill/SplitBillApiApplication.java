package ue.edu.co.splitbill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Clase principal del backend: arranca Spring Boot y el servidor en el puerto 8080
@SpringBootApplication
public class SplitBillApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SplitBillApiApplication.class, args);
    }
}
