package com.data.project.entity;

import javax.persistence.*;

@Entity
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, columnDefinition = "varchar(100)")
    private String username;
    @Column(unique = true, nullable = false, columnDefinition = "varchar(100)")
    private String email;
    @Column(nullable = false, columnDefinition = "varchar(100)")
    private String password;
    @Column(columnDefinition = "boolean default true")
    private boolean role;
    @Column(columnDefinition = "boolean default true")
    private boolean status;
    @Column(nullable = true, columnDefinition = "varchar(20)")
    private String phone;
    public Auth(){
    }
    public Auth(String username, String email, String password, boolean role, boolean status, String phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRole() {
        return role;
    }

    public void setRole(boolean role) {
        this.role = role;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
