package ru.kata.spring.boot_security.demo.controllers;

/*
AdminController предназначен для пользователей с ролью ROLE_ADMIN
Админ может менять свои данные по желанию
Админ может создавать, удалять, изменять новых или других user'ов
*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.hiber.models.User;
import ru.kata.spring.boot_security.demo.hiber.services.RoleService;
import ru.kata.spring.boot_security.demo.hiber.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger LOGGER = Logger.getLogger(AdminController.class.getName());
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("")
    public String allUser(Model model) {
        List<User> userList = userService.getAllUser();
        model.addAttribute("userList", userList);
        if (userList.isEmpty()) {
            model.addAttribute("isEmpty", true);
        }
        return "admin/allUsers";
    }

    @GetMapping("/info")
    public String userInfo(@RequestParam("id") Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return "userNotFound";
        }
        model.addAttribute("user", user.get());
        return "admin/userInfo";
    }

    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/userForm";
    }

    @GetMapping("/update")
    public String updateUserForm(@RequestParam("id") Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            LOGGER.warning(String.format("User id = {%d} not found", id));
            return "userNotFound";
        }
        model.addAttribute("user", user.get());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/userForm";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user) {

        if (user.getId() == null) {
            userService.save(user);
            LOGGER.info("User create: " + user);
        } else {
            userService.update(user);
            LOGGER.info("User update: " + user);
        }

        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id, Principal principal) {
        User userInSession = userService.getUserByName(principal.getName()).orElseThrow();
        userService.delete(id);
        LOGGER.info(String.format("User with id = {%d} was deleted", id));
        if (userInSession.getId().equals(id)) {
            return "redirect:/login";
        }
        return "redirect:/admin";
    }
}
