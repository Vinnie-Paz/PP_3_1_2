package ru.kata.spring.boot_security.demo.hiber.services;
/*
загрузка данных о пользователе на основе username из базы данных
*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.hiber.dao.UserRepository;
import ru.kata.spring.boot_security.demo.hiber.models.User;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger LOGGER = Logger.getLogger(CustomUserDetailsService.class.getName());
    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByName(username);
        if (userOptional.isPresent()) {
            LOGGER.info(String.format("User '%s' loaded successfully", username));
            return userOptional.get();
        } else {
            LOGGER.warning(String.format("User '%s' not found", username));
            throw new UsernameNotFoundException("User not found");
        }
    }
}
