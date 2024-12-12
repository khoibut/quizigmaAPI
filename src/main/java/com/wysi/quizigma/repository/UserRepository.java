package com.wysi.quizigma.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wysi.quizigma.model.User;


public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByEmail(String email);
    public User findByUsername(String username);
}
