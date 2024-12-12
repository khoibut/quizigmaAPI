package com.wysi.quizigma.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wysi.quizigma.model.Set;
import com.wysi.quizigma.model.User;

public interface SetRepository extends JpaRepository<Set, Integer> {

    public void deleteById (Integer id);

    public List<Set> findByOwner(User owner);

}
