package ue.edu.co.splitbill.entity;

import java.util.List;

// Liquidacion de un grupo, tal como la calcula el servidor
public class Settlement {

    private long total;
    private int memberCount;
    private long perPerson;
    private List<Balance> balances;
    private List<Transfer> transfers;

    // Constructor vacio: Gson lo necesita para crear el objeto a partir del JSON
    public Settlement() {
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public long getPerPerson() {
        return perPerson;
    }

    public void setPerPerson(long perPerson) {
        this.perPerson = perPerson;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    public List<Transfer> getTransfers() {
        return transfers;
    }

    public void setTransfers(List<Transfer> transfers) {
        this.transfers = transfers;
    }

    // Para ver el objeto completo en el Logcat
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Settlement{");
        sb.append("total=").append(total);
        sb.append(", memberCount=").append(memberCount);
        sb.append(", perPerson=").append(perPerson);
        sb.append(", balances=").append(balances);
        sb.append(", transfers=").append(transfers);
        sb.append('}');
        return sb.toString();
    }
}
