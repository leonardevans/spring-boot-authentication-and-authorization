package com.lemutugi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lemutugi.audit.Auditable;
import com.lemutugi.payload.request.RoleRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Role extends Auditable<String> {
    @Column(unique = true, nullable = false)
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Collection<User> users;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.MERGE
    })
    @JoinTable(
            name = "roles_privileges",
            joinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "privilege_id", referencedColumnName = "id"))
    private Set<Privilege> privileges = new HashSet<>();

    public Role(String name){
        this.name = name;
    }

    public Role(RoleRequest roleRequest) {
        this.name = roleRequest.getName();
        this.privileges = roleRequest.getPrivileges();
    }
}
