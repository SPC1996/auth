package com.keessi.auth.oauth2.service;

import java.util.List;

public interface UserRoleService {
    List<String> findRoles(int uid);
}
