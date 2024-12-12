package com.wysi.quizigma.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wysi.quizigma.JwtUtil;
import com.wysi.quizigma.model.User;
import com.wysi.quizigma.service.UserService;

@RestController
@RequestMapping("/api/acc")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/")
    public String getMethodName() {
        return "Test";
    }

    @PostMapping("/auth")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            userService.authenticate(user.getEmail(), user.getPassword());
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        response.put("token", jwtUtil.generateToken(user));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> addNewUser(@RequestBody User user) {
        userService.addNewUser(user.getUsername(), user.getEmail(), user.getPassword());
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtUtil.generateToken(user));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
