package com.domain.auth.privilege;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import com.common.anotation.CodeGenLimitter;
import com.common.jpa.domain.BasicEntity;
import lombok.Data;

@Data
@CodeGenLimitter
@Entity
@Table(name = "AUTH_PRIV")
public class Privilege extends BasicEntity<Long> {
    @NotNull
    @Column(name = "PRIV")
    private String privilege;
}
