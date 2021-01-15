package com.anat.springboot.dao;

import com.anat.springboot.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    @PersistenceContext
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    public UserDaoImpl(EntityManagerFactory entityManagerFactory, PasswordEncoder passwordEncoder) {
        this.entityManager = entityManagerFactory.createEntityManager();
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getUsers() {
        TypedQuery<User> from_user = entityManager.createQuery("From User", User.class);
        return from_user.getResultList();
    }

    @Override
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        entityManager.persist(user);
    }

    @Override
    public void editUser(User user) {
        entityManager.merge(user);
    }

    @Override
    public void removeUser(long id) {
        Query query = entityManager.createQuery("Delete FROM User u where u.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public User getUser(long id) {
        Query query = entityManager.createQuery("From User u where u.id = :id", User.class);
        query.setParameter("id", id);
        return (User) query.getSingleResult();
    }

    @Override
    public User getUserByEmail(String email) {
        return entityManager.createQuery("From User u Where u.email = :email", User.class).setParameter("email", email).getSingleResult();
    }
}
