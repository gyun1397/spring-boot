package com.domain.auth.role;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import com.common.anotation.CodeGenLimitter;
import com.common.jpa.domain.BasicEntity;
import com.domain.auth.privilege.Privilege;
import lombok.Data;

@Data
@CodeGenLimitter
@Entity
@Table(name = "AUTH_ROLE")
public class Role extends BasicEntity<Long> {
    @NotNull
    @Column(name = "AUTH_ROLE")
    private String         role;
    
    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "AUTH_ROLE_PRIV", 
        joinColumns = @JoinColumn(name = "role_id"), 
        inverseJoinColumns = @JoinColumn(name = "resource_id"))
    private Set<Privilege> Privileges = new HashSet<>();

//    public Collection<SimpleGrantedAuthority> getAuthorities() {
//        return this.privileges.stream().map(privilege -> new SimpleGrantedAuthority(privilege.getPrivilege())).collect(Collectors.toList());
//    }

    public Set<String> getAuthorities() {
        return this.Privileges.stream().map(privilege -> privilege.getPrivilege()).collect(Collectors.toSet());
    }
}
