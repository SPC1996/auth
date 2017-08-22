package com.keessi.auth.oauth2.mapper;

import com.keessi.auth.oauth2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRep extends JpaRepository<User, Long>{
    User findByUsername(String name);
}
