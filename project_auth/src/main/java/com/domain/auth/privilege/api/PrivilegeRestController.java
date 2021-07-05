package com.domain.auth.privilege.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/privileges")
public class PrivilegeRestController {
    
    @Autowired
    private PrivilegeService privilegeService;
    
}