package ue.edu.co.splitbill.entity;

// Un pago para quedar a paz y salvo: fromName le paga amount a toName
public class Transfer {

    private Long fromMemberId;
    private String fromName;
    private Long toMemberId;
    private String toName;
    private long amount;

    // Constructor vacio: Gson lo necesita para crear el objeto a partir del JSON
    public Transfer() {
    }

    public Long getFromMemberId() {
        return fromMemberId;
    }

    public void setFromMemberId(Long fromMemberId) {
        this.fromMemberId = fromMemberId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public Long getToMemberId() {
        return toMemberId;
    }

    public void setToMemberId(Long toMemberId) {
        this.toMemberId = toMemberId;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    // Para ver el objeto completo en el Logcat
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Transfer{");
        sb.append("fromMemberId=").append(fromMemberId);
        sb.append(", fromName='").append(fromName).append('\'');
        sb.append(", toMemberId=").append(toMemberId);
        sb.append(", toName='").append(toName).append('\'');
        sb.append(", amount=").append(amount);
        sb.append('}');
        return sb.toString();
    }
}
