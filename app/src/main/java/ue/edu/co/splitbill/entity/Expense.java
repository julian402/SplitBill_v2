package ue.edu.co.splitbill.entity;

// Gasto de un grupo: lo pago un integrante y se reparte en partes iguales entre todos
public class Expense {

    private Long id;
    private Long groupId;
    private String description;
    // Pesos enteros
    private Long amount;
    // Integrante que pago
    private Long payerId;
    private String payerName;
    // Fecha en formato 2026-10-02
    private String date;
    // Cuanto le toca a cada uno (lo calcula el servidor)
    private Long perPerson;

    // Constructor vacio: Gson lo necesita para crear el objeto a partir del JSON
    public Expense() {
    }

    // Para crear o editar
    public Expense(String description, Long amount, Long payerId) {
        this.description = description;
        this.amount = amount;
        this.payerId = payerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getPayerId() {
        return payerId;
    }

    public void setPayerId(Long payerId) {
        this.payerId = payerId;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getPerPerson() {
        return perPerson;
    }

    public void setPerPerson(Long perPerson) {
        this.perPerson = perPerson;
    }

    // Para ver el objeto completo en el Logcat
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Expense{");
        sb.append("id=").append(id);
        sb.append(", groupId=").append(groupId);
        sb.append(", description='").append(description).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", payerId=").append(payerId);
        sb.append(", payerName='").append(payerName).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append(", perPerson=").append(perPerson);
        sb.append('}');
        return sb.toString();
    }
}
