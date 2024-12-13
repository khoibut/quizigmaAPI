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

import com.wysi.quizigma.DTO.UserDTO;
import com.wysi.quizigma.JwtUtil;
import com.wysi.quizigma.service.UserService;

@RestController
@RequestMapping("/api/acc")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String getMethodName() {
        return "Test";
    }

    @PostMapping("/auth")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserDTO user) {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = userService.authenticate(user.getEmail(), user.getPassword());
            response.put("token", token);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> addNewUser(@RequestBody UserDTO user) {
        try {
            String token = userService.addNewUser(user);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
