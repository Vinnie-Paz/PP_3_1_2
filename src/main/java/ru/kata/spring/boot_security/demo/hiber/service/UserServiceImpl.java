package ru.kata.spring.boot_security.demo.hiber.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.kata.spring.boot_security.demo.hiber.dao.UserRepository;
import ru.kata.spring.boot_security.demo.hiber.models.User;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           CustomUserDetailsService customUserDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    @Transactional
    public void save(User user) {
        if (!StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void update(User user) {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth == null || !currentAuth.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }
        boolean isAdmin = currentAuth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        User userInBase = getUserById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        String oldName = userInBase.getName();
        String oldPassword = userInBase.getPassword();
        User currentUser = getUserByName(currentAuth.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!StringUtils.isEmpty(user.getName())) {
            userInBase.setName(user.getName());
        }
        if (!StringUtils.isEmpty(user.getSex())) {
            userInBase.setSex(user.getSex());
        }
        if (!StringUtils.isEmpty(user.getAge())) {
            userInBase.setAge(user.getAge());
        }
        if (!StringUtils.isEmpty(user.getPassword())) {
            userInBase.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (!StringUtils.isEmpty(user.getRoles()) && isAdmin) {
            userInBase.setRoles(user.getRoles());
        }
        userRepository.save(userInBase);

        boolean isUpdateUsername = oldName.equals(currentAuth.getName())
                && !oldName.equals(user.getName());
        boolean isUpdatePassword = !StringUtils.isEmpty(user.getPassword())
                && !passwordEncoder.matches(user.getPassword(), oldPassword);

        if (currentUser.getId().equals(user.getId()) && (isUpdateUsername || isUpdatePassword)) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userInBase.getUsername());

            UsernamePasswordAuthenticationToken newAuth =
                    new UsernamePasswordAuthenticationToken(userDetails,
                            userDetails.getPassword(),
                            userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = getUserById(id).orElseThrow(EntityNotFoundException::new);
        user.getRoles().clear();
        userRepository.deleteById(id);
    }
}
