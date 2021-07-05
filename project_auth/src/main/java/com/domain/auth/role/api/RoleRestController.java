package com.domain.auth.role.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/roles")
public class RoleRestController {
    
    @Autowired
    private RoleService roleService;
    
}