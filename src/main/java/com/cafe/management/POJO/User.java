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



}