package com.anat.springboot.controllers;

import com.anat.springboot.model.Role;
import com.anat.springboot.model.User;
import com.anat.springboot.services.RoleService;
import com.anat.springboot.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
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

    public WebController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(value = "/")
    public String homePage(Authentication authentication) {
        if (null != authentication && authentication.isAuthenticated()) {
            return "redirect:/admin";
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
        User user = null;
        try {
            user = userService.getUserByEmail(principal.getName());
        } catch (Exception e) {
            return "redirect:logout";
        }
        List<User> users = userService.getUsers();
        Set<Role> roles = roleService.getRoles();
        modelMap.put("user", user);
        modelMap.put("users", users);
        modelMap.put("roles", roles);
        return "admin";
    }
}
