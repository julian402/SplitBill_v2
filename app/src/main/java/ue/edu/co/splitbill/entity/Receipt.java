package ue.edu.co.splitbill.entity;

// Recibo guardado SOLO en el celular (SQLite): foto tomada con la camara, descripcion y valor
public class Receipt {

    private long id;
    // Usuario dueño del recibo
    private long userId;
    private String description;
    private long amount;
    // Ruta del archivo de la foto dentro de la app
    private String photoPath;
    private String date;
    // 1 activo, 0 borrado
    private byte status;

    // Constructor vacio: se usa al leer los recibos de SQLite
    public Receipt() {
    }

    // Para crear un recibo nuevo
    public Receipt(long userId, String description, long amount, String photoPath, String date) {
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.photoPath = photoPath;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    // Para ver el objeto completo en el Logcat
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Receipt{");
        sb.append("id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", description='").append(description).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", photoPath='").append(photoPath).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
