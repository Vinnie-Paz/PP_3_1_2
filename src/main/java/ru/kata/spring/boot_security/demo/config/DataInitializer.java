package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.hiber.models.Role;
import ru.kata.spring.boot_security.demo.hiber.models.User;
import ru.kata.spring.boot_security.demo.hiber.services.RoleService;
import ru.kata.spring.boot_security.demo.hiber.services.UserService;

import java.util.logging.Logger;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;
    private final RoleService roleService;

    private static final Logger LOGGER = Logger.getLogger(DataInitializer.class.getName());

    @Autowired
    public DataInitializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userService.getUserByName("admin").isEmpty()) {
            User admin = new User();
            admin.setName("admin");
            admin.setPassword("admin");

            Role adminRole = roleService.getRoleByName("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role role = new Role("ROLE_ADMIN");
                        roleService.save(role);
                        return role;
                    });
            admin.getRoles().add(adminRole);

            userService.save(admin);
            LOGGER.info("Admin created.");
        }

        if (userService.getUserByName("user").isEmpty()) {
            User user = new User();
            user.setName("user");
            user.setPassword("user");

            Role userRole = roleService.getRoleByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role role = new Role("ROLE_USER");
                        roleService.save(role);
                        return role;
                    });
            user.getRoles().add(userRole);

            userService.save(user);
            LOGGER.info("User created.");
        }
    }
}
