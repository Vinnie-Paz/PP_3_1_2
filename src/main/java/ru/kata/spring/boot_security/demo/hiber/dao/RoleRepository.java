package ru.kata.spring.boot_security.demo.hiber.dao;
/*
Используется для загрузки ролей в форму для User
*/

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.hiber.model.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> getRoleByName(String name);
}
