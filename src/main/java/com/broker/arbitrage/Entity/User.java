package com.broker.arbitrage.Entity;


import com.broker.arbitrage.Entity.Type.Roles;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name="users")
public class User implements UserDetails {


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    int ID;
    @Column(nullable = false)
    String name;

    @Column(nullable=false, unique=true)
    String email;

    @Column(nullable=false)
    String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    Set<Roles> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(roles==null){
            return List.of();

        }
        return roles.stream().map(role->new SimpleGrantedAuthority("ROLE_" + role.name())).toList();
    }

    @Override
    public String getUsername() {
        return this.email;
    }


}
