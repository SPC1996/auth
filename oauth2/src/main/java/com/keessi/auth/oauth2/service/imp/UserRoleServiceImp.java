package com.keessi.auth.oauth2.service.imp;

import com.keessi.auth.oauth2.mapper.UserRoleRep;
import com.keessi.auth.oauth2.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleServiceImp implements UserRoleService {
    private final UserRoleRep userRoleRep;

    @Autowired
    public UserRoleServiceImp(UserRoleRep userRoleRep) {
        this.userRoleRep = userRoleRep;
    }

    @Override
    public List<String> findRoles(int uid) {
        return userRoleRep.findRoleName(uid);
    }
}
