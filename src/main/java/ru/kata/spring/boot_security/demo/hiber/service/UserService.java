package ru.kata.spring.boot_security.demo.hiber.services;


import ru.kata.spring.boot_security.demo.hiber.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void save(User user);

    void update(User user);

    List<User> getAllUser();

    Optional<User> getUserById(Long id);

    Optional<User> getUserByName(String name);

    void delete(Long id);
}
