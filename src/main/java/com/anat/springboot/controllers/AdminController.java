package com.anat.springboot.controllers;


import com.anat.springboot.model.Role;
import com.anat.springboot.model.User;
import com.anat.springboot.services.RoleService;
import com.anat.springboot.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(value = "/admin/admin")
    public String adminPage() {
        return "admin";
    }

    @GetMapping(value = "/admin/users")
    public String listUser(ModelMap model, Authentication authentication) {
        if (authentication.isAuthenticated()) {
            List<User> users = userService.getUsers();
            model.addAttribute("users", users);
            return "users";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping(value = "/admin/editUser")
    public String editUser(ModelMap model, @RequestParam long id) {
        User editUser = userService.getUser(id);
        Set<Role> roles = roleService.getRoles();
        model.addAttribute("user", editUser);
        model.addAttribute("roles", roles);
        return "edituser";
    }

    @PostMapping(value = "/admin/editUser")
    public String editUserSubmit(@ModelAttribute User user, @RequestParam Map<String, String> allParams) {
        user.getRoles().clear();
        Set<Role> roles = allParams.entrySet().stream()
                .filter(e -> e.getKey().startsWith("role_"))
                .filter(e -> e.getValue().equals("on"))
                .map(e -> roleService.getRole(Integer.parseInt(e.getKey().replace("role_", ""))))
                .collect(Collectors.toSet());
        user.getRoles().addAll(roles);
        userService.editUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping(value = "/admin/removeUser")
    public String removeUser(@RequestParam int id) {
        userService.removeUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/newUser")
    public String newUserForm(ModelMap modelMap) {
        Set<Role> roles = roleService.getRoles();
        User newUser = new User();
        modelMap.addAttribute("user", newUser);
        modelMap.addAttribute("roles", roles);
        return "newUser";
    }

    @PostMapping("/admin/newUser")
    public String newUser(@ModelAttribute User user, @RequestParam Map<String, String> allParams) {
        Set<Role> roles = allParams.entrySet().stream()
                .filter(e -> e.getKey().startsWith("role_"))
                .filter(e -> e.getValue().equals("on"))
                .map(e -> roleService.getRole(Integer.parseInt(e.getKey().replace("role_", ""))))
                .collect(Collectors.toSet());
        userService.saveUser(user);
        user.setRoles(roles);
        userService.editUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/roles")
    public String listUsers(ModelMap map) {
        Set<Role> roles = roleService.getRoles();
        map.addAttribute("roles", roles);
        return "roles";
    }

    @GetMapping("/admin/newRole")
    public String newRole(ModelMap modelMap) {
        Role role = new Role();
        modelMap.put("role", role);
        return "newRole";
    }

    @PostMapping("/admin/newRole")
    public String newRoleSubmit(@ModelAttribute Role role) {
        if (!role.getRoleName().startsWith("ROLE_")) role.setRoleName("ROLE_" + role.getRoleName());
        roleService.addRole(role);
        return "redirect:/admin/roles";
    }

    @GetMapping(value = "/admin/editRole")
    public String editRole(ModelMap model, @RequestParam long id) {
        Role role = roleService.getRole(id);
        model.addAttribute("role", role);
        return "editRole";
    }

    @PostMapping(value = "/admin/editRole")
    public String editRoleSubmit(@ModelAttribute Role role) {
        roleService.editRole(role);
        return "redirect:/admin/roles";
    }
}
