package ru.kata.spring.boot_security.demo.hiber.dao;

/*
Присутствует жадная загрузка @EntityGraph(attributePaths = "roles") на всех методах получения пользователя
*/

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.hiber.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    @EntityGraph(attributePaths = "roles")
    Optional<User> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "roles")
    List<User> findAll();

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByName(String name);
}
