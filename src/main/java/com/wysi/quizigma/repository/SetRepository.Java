package com.wysi.quizigma.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.wysi.quizigma.model.Set;
import com.wysi.quizigma.model.User;

public interface SetRepository extends JpaRepository<Set, Integer> {

    @Override
    public void deleteById(@NonNull Integer id);

    public List<Set> findByOwner(User owner);

}
