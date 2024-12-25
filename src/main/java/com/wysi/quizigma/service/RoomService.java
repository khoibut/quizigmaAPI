package com.wysi.quizigma.service;

import org.springframework.stereotype.Service;

import com.wysi.quizigma.DTO.RoomDTO;
import com.wysi.quizigma.model.Room;
import com.wysi.quizigma.model.Set;
import com.wysi.quizigma.model.User;
import com.wysi.quizigma.repository.RoomRepository;
import com.wysi.quizigma.repository.SetRepository;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserService userService;
    private final SetRepository setRepository;

    public RoomService(RoomRepository roomRepository, UserService userService, SetRepository setRepository) {
        this.roomRepository = roomRepository;
        this.userService = userService;
        this.setRepository = setRepository;
    }

    public Integer createRoom(RoomDTO roomDTO, String token) {
        if (roomDTO == null || token == null || roomDTO.getSetId() == null || roomDTO.getTimeLimit() == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        User user = userService.getUser(token);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        Set set = setRepository.findById(roomDTO.getSetId()).orElse(null);
        if(set == null) {
            throw new IllegalArgumentException("Set not found");
        }
        Room room = new Room(set, user, roomDTO.getTimeLimit());
        return roomRepository.save(room).getRoomId();
    }
}
