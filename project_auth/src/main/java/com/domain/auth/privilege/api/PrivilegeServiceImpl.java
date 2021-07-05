package com.domain.auth.privilege.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.domain.auth.privilege.PrivilegeRepository;

@Service("privilegeService")
public class PrivilegeServiceImpl  implements PrivilegeService {
    
    @Autowired
    private PrivilegeRepository privilegeRepository;
    
}