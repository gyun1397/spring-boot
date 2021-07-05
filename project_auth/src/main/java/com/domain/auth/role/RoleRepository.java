package com.domain.auth.role;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.common.jpa.api.PureJpaRepository;

@RepositoryRestResource(exported = false)
public interface RoleRepository extends PureJpaRepository<Role, Long> {
}
