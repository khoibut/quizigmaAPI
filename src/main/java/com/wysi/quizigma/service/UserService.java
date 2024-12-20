package com.wysi.quizigma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wysi.quizigma.DTO.UserDTO;
import com.wysi.quizigma.Security.JwtUtil;
import com.wysi.quizigma.model.User;
import com.wysi.quizigma.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, JwtUtil JwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = JwtUtil;
    }

    public String authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return jwtUtil.generateToken(user);
    }

    public String addNewUser(UserDTO user) {
        String username = user.getUsername();
        String email = user.getEmail();
        String password = user.getPassword();
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        User newUser = new User(username, email, password);
        userRepository.save(newUser);
        return jwtUtil.generateToken(newUser);
    }

    public User getUser(String token) {
        return userRepository.findById(jwtUtil.getUserId(token)).orElse(null);
    }
    
    public String editUser(UserDTO user, String token) {
        User currentUser = userRepository.findById(jwtUtil.getUserId(token)).orElse(null);
        if (user.getUsername() != null) {
            currentUser.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            currentUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            currentUser.setPassword(user.getPassword());
        }

        try{
            userRepository.save(currentUser);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid input");
        }
        return jwtUtil.generateToken(currentUser);
    }
}
