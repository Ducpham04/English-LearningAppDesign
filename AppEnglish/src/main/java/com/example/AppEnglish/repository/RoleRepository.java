package com.example.appenglish.repository;

import com.example.appenglish.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAll() ;
    Role findByName(String name);
}
