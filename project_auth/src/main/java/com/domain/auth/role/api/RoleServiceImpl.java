package com.domain.auth.role.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.domain.auth.role.Role;
import com.domain.auth.role.RoleRepository;

@Service("roleService")
public class RoleServiceImpl implements RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
}