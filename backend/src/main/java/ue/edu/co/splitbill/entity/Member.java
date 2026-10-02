package ue.edu.co.splitbill.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Persona de un grupo. No necesita cuenta: basta con el nombre (y el telefono si viene de contactos)
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mem_id")
    private Long id;

    @Column(name = "mem_group_id")
    private Long groupId;

    @Column(name = "mem_name")
    private String name;

    @Column(name = "mem_phone")
    private String phone;

    @Column(name = "mem_status")
    private Integer status = 1;

    // Constructor vacio, JPA lo necesita
    public Member() {
    }

    // Para agregar un integrante nuevo
    public Member(Long groupId, String name, String phone) {
        this.groupId = groupId;
        this.name = name;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    // Solo se usa en las pruebas, normalmente el id lo pone la base
    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
