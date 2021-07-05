package com.domain.auth.privilege;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.common.jpa.api.PureJpaRepository;

@RepositoryRestResource(exported = false)
public interface PrivilegeRepository extends PureJpaRepository<Privilege, Long>  {
}
