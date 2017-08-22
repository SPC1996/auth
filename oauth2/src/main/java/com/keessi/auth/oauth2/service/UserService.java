package com.keessi.auth.oauth2.service;

import com.keessi.auth.oauth2.entity.User;

import java.util.List;

public interface UserService {
    User findByUsername(String name);

    List<User> findAll();
}
