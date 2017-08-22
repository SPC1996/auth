package com.keessi.auth.oauth2.service.imp;

import com.keessi.auth.oauth2.entity.User;
import com.keessi.auth.oauth2.mapper.UserRep;
import com.keessi.auth.oauth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImp implements UserService {
    private final UserRep userRep;

    @Autowired
    public UserServiceImp(UserRep userRep) {
        this.userRep = userRep;
    }

    @Override
    public User findByUsername(String name) {
        return userRep.findByUsername(name);
    }

    @Override
    public List<User> findAll() {
        return userRep.findAll();
    }
}
