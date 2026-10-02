package ue.edu.co.splitbill.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

// Calculo de cuenta rapida guardado con Room. Cada anotacion reemplaza una parte del SQL a mano
@Entity(tableName = "quick_split_history")
public class QuickSplitHistory {

    // Llave primaria autoincrementable, como un AUTOINCREMENT
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "qsh_id")
    private long id;

    @ColumnInfo(name = "qsh_user_id")
    private long userId;

    @ColumnInfo(name = "qsh_subtotal")
    private long subtotal;

    @ColumnInfo(name = "qsh_tip_percent")
    private int tipPercent;

    @ColumnInfo(name = "qsh_people")
    private int people;

    @ColumnInfo(name = "qsh_total")
    private long total;

    @ColumnInfo(name = "qsh_per_person")
    private long perPerson;

    // Fecha y hora en formato 2026-10-02 11:30
    @ColumnInfo(name = "qsh_date")
    private String date;

    // Constructor vacio: Room lo usa para crear los objetos al leer la tabla
    public QuickSplitHistory() {
    }

    // Para crear uno nuevo; @Ignore le dice a Room que use el constructor vacio
    @Ignore
    public QuickSplitHistory(long userId, long subtotal, int tipPercent, int people, long total, long perPerson,
                             String date) {
        this.userId = userId;
        this.subtotal = subtotal;
        this.tipPercent = tipPercent;
        this.people = people;
        this.total = total;
        this.perPerson = perPerson;
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

    public long getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(long subtotal) {
        this.subtotal = subtotal;
    }

    public int getTipPercent() {
        return tipPercent;
    }

    public void setTipPercent(int tipPercent) {
        this.tipPercent = tipPercent;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPerPerson() {
        return perPerson;
    }

    public void setPerPerson(long perPerson) {
        this.perPerson = perPerson;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Para ver el objeto completo en el Logcat
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QuickSplitHistory{");
        sb.append("id=").append(id);
        sb.append(", subtotal=").append(subtotal);
        sb.append(", tipPercent=").append(tipPercent);
        sb.append(", people=").append(people);
        sb.append(", total=").append(total);
        sb.append(", perPerson=").append(perPerson);
        sb.append(", date='").append(date).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
