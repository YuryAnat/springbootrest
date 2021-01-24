package com.anat.springboot.controllers;

import com.anat.springboot.model.Role;
import com.anat.springboot.model.User;
import com.anat.springboot.services.RoleService;
import com.anat.springboot.services.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class UserRestController {
    private final UserService userService;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserRestController(UserService userService, RoleService roleService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(value = "/admin/rest/user/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping(value = "/admin/rest/users")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(value = "/admin/rest/roles")
    public Set<Role> getRoles() {
        return roleService.getRoles();
    }

    @PostMapping(value = "/admin/rest/newUser")
    public void newUser(@RequestBody User newUser) {
        newUser.setRoles(newUser.getRoles().stream().map(role -> roleService.getRoleByName(role.getRoleName())).collect(Collectors.toSet()));
        userService.saveUser(newUser);
    }

    @PostMapping("/admin/rest/editUser")
    public void editUser(@RequestBody User user) {
        user.setRoles(user.getRoles().stream().map(role -> roleService.getRoleByName(role.getRoleName())).collect(Collectors.toSet()));
        if (user.getPassword().isEmpty()){
            user.setPassword(userService.getUser(user.getId()).getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.editUser(user);
    }

    @GetMapping(value = "/admin/rest/deleteUser/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.removeUser(id);
    }
}
