package com.wysi.quizigma.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wysi.quizigma.model.Question;
import com.wysi.quizigma.model.Set;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findBySet(Set set);
}
