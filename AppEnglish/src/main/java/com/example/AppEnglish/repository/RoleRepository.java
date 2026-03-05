package com.example.AppEnglish.repository;

import com.example.AppEnglish.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAll() ;
    Optional<Role> findByName(String name);
}
