package com.wysi.quizigma.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.wysi.quizigma.DTO.PlayerDTO;
import com.wysi.quizigma.DTO.RoomDTO;
import com.wysi.quizigma.model.Player;
import com.wysi.quizigma.model.Room;
import com.wysi.quizigma.model.Set;
import com.wysi.quizigma.model.User;
import com.wysi.quizigma.repository.RoomRepository;
import com.wysi.quizigma.repository.SetRepository;

import jakarta.transaction.Transactional;

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

    @Transactional
    public void saveRoom(RoomDTO roomDTO) {
        if (roomDTO == null || roomDTO.getSetId() == null || roomDTO.getTimeLimit() == null || roomDTO.getCreator() == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        System.out.println("User: " + roomDTO.getCreator());
        User user = userService.getUserByUsername(roomDTO.getCreator());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        Set set = setRepository.findById(roomDTO.getSetId()).orElse(null);
        if (set == null) {
            throw new IllegalArgumentException("Set not found");
        }
        Room room = new Room(set, user, roomDTO.getTimeLimit(), new ArrayList<>());
        List<Player> players = new ArrayList<>();
        for(PlayerDTO playerDTO : roomDTO.getPlayers()) {
            players.add(new Player(playerDTO.getName(), room, playerDTO.getCorrect(), playerDTO.getIncorrect(), playerDTO.getScore()));
        }
        room.setPlayers(players);
        roomRepository.save(room);
    }
}
