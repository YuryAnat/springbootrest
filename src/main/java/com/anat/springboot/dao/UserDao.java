package com.anat.springboot.dao;



import com.anat.springboot.model.User;

import java.util.List;

public interface UserDao {
    List<User> getUsers();

    void saveUser(User user);

    void editUser(User user);

    void removeUser(long id);

    User getUser(long id);

    User getUserByEmail(String email);
}
