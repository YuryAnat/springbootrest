package com.anat.springboot.controllers;

import com.anat.springboot.model.Role;
import com.anat.springboot.model.User;
import com.anat.springboot.services.RoleService;
import com.anat.springboot.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class WebController {
    private final UserService userService;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebController(UserService userService, RoleService roleService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping(value = "/")
    public String homePage(Authentication authentication) {
        if (null != authentication && authentication.isAuthenticated()) {
            boolean isAdmin = AuthorityUtils.authorityListToSet(authentication.getAuthorities()).contains("ROLE_Administrators");
            if (isAdmin) {
                return "redirect:/admin";
            } else {
                return "redirect:/user";
            }
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping(value = "/user")
    public String userPage(ModelMap model, Authentication authentication) {
        if (authentication.isAuthenticated()) {
            User currentUser = userService.getUserByEmail(authentication.getName());
            model.addAttribute("user", currentUser);
            return "user";
        } else {
            return "redirect:/login";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage() {
        return "login";
    }

    @GetMapping(value = "/admin")
    public String adminPage(ModelMap modelMap, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        List<User> users = userService.getUsers();
        Set<Role> roles = roleService.getRoles();
        modelMap.put("user", user);
        modelMap.put("users", users);
        modelMap.put("roles", roles);
        return "admin";
    }


    @PostMapping(value = "/admin/editUser")
    public String editUserSubmit(@RequestParam Map<String, String> allParams) {
        User user = userService.getUser(Integer.parseInt(allParams.get("id")));
        user.setName(allParams.get("fname"));
        user.setLastName(allParams.get("lname"));
        user.setEmail(allParams.get("email"));
        if (!allParams.get("pass").equals("************")){
            user.setPassword(bCryptPasswordEncoder.encode(allParams.get("pass")));
        }
        user.getRoles().clear();
        user.getRoles().addAll(allParams.entrySet().stream()
                .filter(e -> e.getKey().equals("roles[]"))
                .map(e -> roleService.getRole(Integer.parseInt(e.getValue())))
                .collect(Collectors.toSet()));
        userService.editUser(user);
        return "redirect:/admin";
    }

    @PostMapping(value = "/admin/deleteUser")
    public String removeUser(@RequestParam Map<String, String> allParams) {
        userService.removeUser(Integer.parseInt(allParams.get("id")));
        return "redirect:/admin";
    }

    @PostMapping("/admin/newUser")
    public String newUser(@RequestParam Map<String, String> allParams) {
        Set<Role> roles = allParams.entrySet().stream()
                .filter(e -> e.getKey().equals("roles[]"))
                .map(e -> roleService.getRole(Integer.parseInt(e.getValue())))
                .collect(Collectors.toSet());
        User user = new User(allParams.get("fname"), allParams.get("lname"), allParams.get("email"), allParams.get("pass"), roles);
        userService.saveUser(user);
        return "redirect:/admin";
    }
}
