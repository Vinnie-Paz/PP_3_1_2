package ru.kata.spring.boot_security.demo.hiber.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.hiber.dao.RoleRepository;
import ru.kata.spring.boot_security.demo.hiber.models.Role;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.getRoleByName(name);
    }

    @Override
    public void save(Role role) {
        roleRepository.save(role);
    }
}
