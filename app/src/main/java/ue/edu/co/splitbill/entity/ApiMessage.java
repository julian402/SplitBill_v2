package ue.edu.co.splitbill.entity;

// Respuesta {"message": "..."} del servidor: confirmaciones de borrado y errores
public class ApiMessage {

    private String message;

    // Constructor vacio: Gson lo necesita para crear el objeto a partir del JSON
    public ApiMessage() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
