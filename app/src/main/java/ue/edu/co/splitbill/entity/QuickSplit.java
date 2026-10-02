package ue.edu.co.splitbill.entity;

// Cuenta rapida: la app manda subtotal, propina y personas; el servidor devuelve el resto
public class QuickSplit {

    private Long subtotal;
    private Integer tipPercent;
    private Integer people;
    private Long tip;
    private Long total;
    private Long perPerson;
    // Pesos que sobran: los primeros pagan 1 peso mas
    private Long remainder;

    // Constructor vacio: Gson lo necesita para crear el objeto a partir del JSON
    public QuickSplit() {
    }

    // Datos que se envian para calcular
    public QuickSplit(Long subtotal, Integer tipPercent, Integer people) {
        this.subtotal = subtotal;
        this.tipPercent = tipPercent;
        this.people = people;
    }

    public Long getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Long subtotal) {
        this.subtotal = subtotal;
    }

    public Integer getTipPercent() {
        return tipPercent;
    }

    public void setTipPercent(Integer tipPercent) {
        this.tipPercent = tipPercent;
    }

    public Integer getPeople() {
        return people;
    }

    public void setPeople(Integer people) {
        this.people = people;
    }

    public Long getTip() {
        return tip;
    }

    public void setTip(Long tip) {
        this.tip = tip;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPerPerson() {
        return perPerson;
    }

    public void setPerPerson(Long perPerson) {
        this.perPerson = perPerson;
    }

    public Long getRemainder() {
        return remainder;
    }

    public void setRemainder(Long remainder) {
        this.remainder = remainder;
    }

    // Para ver el objeto completo en el Logcat
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QuickSplit{");
        sb.append("subtotal=").append(subtotal);
        sb.append(", tipPercent=").append(tipPercent);
        sb.append(", people=").append(people);
        sb.append(", tip=").append(tip);
        sb.append(", total=").append(total);
        sb.append(", perPerson=").append(perPerson);
        sb.append(", remainder=").append(remainder);
        sb.append('}');
        return sb.toString();
    }
}
