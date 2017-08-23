package com.keessi.auth.oauth2.security;

import com.keessi.auth.oauth2.entity.Role;
import com.keessi.auth.oauth2.entity.User;
import com.keessi.auth.oauth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.HashSet;

public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名：" + username + "不存在");
        }
        Collection<SimpleGrantedAuthority> collection = new HashSet<>();
        for (Role role : user.getList()) {
            collection.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), collection);
    }
}
