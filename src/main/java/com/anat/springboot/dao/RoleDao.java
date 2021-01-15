package com.anat.springboot.dao;

import com.anat.springboot.model.Role;

import java.util.Set;

public interface RoleDao {
    void addRole(Role role);

    Role getRole(long id);

    void deleteRole(Role role);

    void editRole(Role role);

    Role getRoleByName(String name);

    Set<Role> getRoles();
}
