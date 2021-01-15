package com.anat.springboot.services;

import com.anat.springboot.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    void saveUser(User user);

    void editUser(User user);

    void removeUser(long id);

    User getUser(long id);

    User getUserByEmail(String email);
}
