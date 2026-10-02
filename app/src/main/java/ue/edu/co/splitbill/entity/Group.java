package ue.edu.co.splitbill.entity;

// Grupo de gastos. memberCount y total los calcula el servidor
public class Group {

    private Long id;
    private String name;
    private String description;
    // Usuario que creo el grupo
    private Long ownerId;
    private Long memberCount;
    // Suma de los gastos del grupo, en pesos
    private Long total;

    // Constructor vacio: Gson lo necesita para crear el objeto a partir del JSON
    public Group() {
    }

    // Para crear o editar: los campos en null no se envian
    public Group(String name, String description, Long ownerId) {
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Long memberCount) {
        this.memberCount = memberCount;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    // Para ver el objeto completo en el Logcat
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Group{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", ownerId=").append(ownerId);
        sb.append(", memberCount=").append(memberCount);
        sb.append(", total=").append(total);
        sb.append('}');
        return sb.toString();
    }
}
