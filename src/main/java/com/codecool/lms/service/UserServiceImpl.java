package com.codecool.lms.service;

import com.codecool.lms.exception.UserAlreadyRegisteredException;
import com.codecool.lms.exception.UserNotFoundException;
import com.codecool.lms.exception.WrongPasswordException;
import com.codecool.lms.model.Mentor;
import com.codecool.lms.model.Student;
import com.codecool.lms.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    private static UserServiceImpl userService = new UserServiceImpl();
    private List<User> users = new ArrayList<>();
    private User currentUser;


    private UserServiceImpl() {}

    public static UserServiceImpl getUserService() {
        return userService;
    }

    public List<User> getUsers() {
        return users;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean containsUser(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    public void register(User user) throws UserAlreadyRegisteredException {
        if (!containsUser(user.getEmail())) {
            users.add(user);
        } else {
            throw new UserAlreadyRegisteredException();
        }
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User findUserByEmail(String email, String password) throws UserNotFoundException, WrongPasswordException {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                if (user.getPassword().equals(password)) {
                    return user;
                }
                throw new WrongPasswordException();
            }
        }
        throw new UserNotFoundException();
    }

    public User createUser(String email, String name, String password, String type) {
        if (type.equals("Mentor")) {
            return new Mentor(name, email, password);
        } else {
            return new Student(name, email, password);
        }
    }
}