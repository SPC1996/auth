package com.keessi.auth.oauth2.controller.api;

import com.keessi.auth.oauth2.entity.User;
import com.keessi.auth.oauth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<User> listAllUser() {
        List<User> users=userService.findAll();
        if (users.isEmpty()) {
            return null;
        }
        return users;
    }
}
