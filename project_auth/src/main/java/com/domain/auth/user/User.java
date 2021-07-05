package com.domain.auth.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.common.anotation.CodeGenLimitter;
import com.common.jpa.domain.BasicEntity;
import com.domain.auth.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@CodeGenLimitter
@Entity
@Table(name = "AUTH_USER")
@JsonIgnoreProperties(value = { "password" }, allowSetters = true, allowGetters = false)
public class User extends BasicEntity<Long> {
    @NotNull
    @Column(name = "USER_ID", length = 50, unique = true)
    @Size(min = 5, max = 50)
    private String    userId;
    @NotNull
    @Column(name = "USER_NAME", length = 100)
    @Size(max = 100)
    private String    userName;
    @NotNull
    @Column(name = "PASSWORD", length = 100)
    @Size(max = 100)
    private String    password;
    // @NotNull
    // @JsonIgnore
    // @Column(name = "EMAIL", length = 100)
    // @Pattern(regexp = RegularExpression.EMAIL)
    // @Size(max = 100)
    // private String email;
    
    // @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    // @JoinTable(name = "AUTH_USER_PRIV",
    // joinColumns = @JoinColumn(name = "user_id"),
    // inverseJoinColumns = @JoinColumn(name = "resources_id"))
    // private Set<privilege> privileges = new HashSet<>();
    
    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "AUTH_USER_ROLE", 
        joinColumns = @JoinColumn(name = "user_id"), 
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Transient
    private Set<String>                        privileges  = new HashSet<>();
    @Transient
    private Collection<SimpleGrantedAuthority> authorities = new HashSet<>();
    
    public Set<String> getPrivileges() {
        if (this.privileges.isEmpty()) {
            this.roles.stream().map(role -> this.privileges.addAll(role.getAuthorities()));
        }
        return this.privileges;
    }

    public Collection<SimpleGrantedAuthority> getAuthorities() {
        if (this.authorities.isEmpty()) {
            getPrivileges().stream().map(privilege -> authorities.add(new SimpleGrantedAuthority(privilege)));
        }
        return this.authorities;
    }
    
}
