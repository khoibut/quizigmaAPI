package com.wysi.quizigma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wysi.quizigma.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    
}