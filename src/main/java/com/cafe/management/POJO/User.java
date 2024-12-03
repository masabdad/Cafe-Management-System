package com.cafe.management.POJO;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NamedQuery(name = "User.findByEmailId",query = "select u from User u where u.email=:email")

@NamedQuery(name = "User.getAllUser", query = "select new com.cafe.management.wrapper.UserWrapper(u.id,u.name,u.email,u.contactNumber,u.status)from User u where u.role = 'user'")


@NamedQuery(name = "User.getAllAdmin", query = "select u.email from User u where u.role = 'admin'")


@NamedQuery(name = "User.update.Status" , query = "update User u set u.status=:status where u.id=:id")
@NamedQuery(name = "User.getAllEmployee", query = "select new com.cafe.management.wrapper.UserWrapper(u.id,u.name,u.email,u.contactNumber,u.status) from User u where u.role ='Employee'")


@Entity
@Data
@Table(name = "user")
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
public class User implements Serializable {


    private static final long serialVersionUId = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "contactNumber")
    private String contactNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "status")
    private String status;

    @Getter
    @Setter
    @Column(name = "verification_code")
    private String verificationCode; // Add this field for storing the verification code

    // ... getters and setters ...


    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role)); // Assuming roles are prefixed with "ROLE_"
        return authorities;
    }

    public String getAuthority() {
        return role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}