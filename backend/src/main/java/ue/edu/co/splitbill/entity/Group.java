package ue.edu.co.splitbill.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Tabla groups: un grupo de gastos (un paseo, el apartamento, etc.)
@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grp_id")
    private Long id;

    @Column(name = "grp_name")
    private String name;

    @Column(name = "grp_description")
    private String description;

    // Usuario que creo el grupo
    @Column(name = "grp_owner_id")
    private Long ownerId;

    @Column(name = "grp_status")
    private Integer status = 1;

    // Constructor vacio, JPA lo necesita
    public Group() {
    }

    // Para crear un grupo nuevo
    public Group(String name, String description, Long ownerId) {
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
    }

    public Long getId() {
        return id;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
