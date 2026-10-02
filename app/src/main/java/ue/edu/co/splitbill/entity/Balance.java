package ue.edu.co.splitbill.entity;

// Saldo de un integrante en la liquidacion. Positivo = le deben; negativo = debe
public class Balance {

    private Long memberId;
    private String name;
    // Lo que pago
    private long paid;
    // Lo que le tocaba
    private long share;
    // paid - share
    private long balance;

    // Constructor vacio: Gson lo necesita para crear el objeto a partir del JSON
    public Balance() {
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPaid() {
        return paid;
    }

    public void setPaid(long paid) {
        this.paid = paid;
    }

    public long getShare() {
        return share;
    }

    public void setShare(long share) {
        this.share = share;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    // Para ver el objeto completo en el Logcat
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Balance{");
        sb.append("memberId=").append(memberId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", paid=").append(paid);
        sb.append(", share=").append(share);
        sb.append(", balance=").append(balance);
        sb.append('}');
        return sb.toString();
    }
}
