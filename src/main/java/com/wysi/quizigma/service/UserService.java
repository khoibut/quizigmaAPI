package com.wysi.quizigma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wysi.quizigma.model.User;
import com.wysi.quizigma.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    public void authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid email or password");
        }
    }
    public void addNewUser(String username, String email, String password) {
        User user = new User(username, email, password);
        userRepository.save(user);
    }
}
