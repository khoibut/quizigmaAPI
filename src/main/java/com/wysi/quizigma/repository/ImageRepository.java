package com.wysi.quizigma.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wysi.quizigma.model.Image;

public interface ImageRepository extends JpaRepository<Image, Integer> {

}