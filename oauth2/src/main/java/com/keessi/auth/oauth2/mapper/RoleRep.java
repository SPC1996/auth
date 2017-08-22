package com.keessi.auth.oauth2.mapper;

import com.keessi.auth.oauth2.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRep extends JpaRepository<Role, Long>{
}
