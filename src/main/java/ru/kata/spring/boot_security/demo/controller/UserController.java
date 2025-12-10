package ru.kata.spring.boot_security.demo.controllers;

/*
UserController предназначен для доступа пользователей с ролью ROLE_USER
Пользователь может менять свои данные по желанию (кроме присвоенной роли)
Пользователь НЕ может создавать, удалять, изменять новых или других user'ов
*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.hiber.models.User;
import ru.kata.spring.boot_security.demo.hiber.services.UserService;

import java.util.Optional;
import java.util.logging.Logger;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    @Autowired
    UserService userService;

    @GetMapping("/info")
    public String userInfo(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String name = ((UserDetails) principal).getUsername();
            Optional<User> user = userService.getUserByName(name);

            if (user.isEmpty()) {
                return "userNotFound";
            }

            model.addAttribute("user", user.get());
            return "user/userInfo";
        }
        return "userNotFound";
    }

    @GetMapping("/update")
    public String updateUserForm(@RequestParam("id") Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            LOGGER.warning(String.format("User id = {%d} not found", id));
            return "userNotFound";
        }
        model.addAttribute("user", user.get());
        return "user/userForm";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user) {
        userService.update(user);
        LOGGER.info("User update: " + user);
        return "redirect:/users/info";
    }

}
