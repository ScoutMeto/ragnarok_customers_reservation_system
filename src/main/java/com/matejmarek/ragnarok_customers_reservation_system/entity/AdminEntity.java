package com.matejmarek.ragnarok_customers_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity(name = "admin")
@Table(name = "admin")
@Getter
@Setter
public class AdminEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("admin_id")
    private Long adminId;

    @Column(nullable = false, unique = true)
    private String adminEmail;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private boolean admin = false;

    @Column(nullable = false)
    private String password;





    @Override
    public String getUsername() {
        return adminEmail;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { //return role and rights
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_" + (admin ? "ADMIN" : "USER")); //minimal one role for each user. Best way is get roles to chosen accounts manually.
        return List.of(grantedAuthority);
    }
}
