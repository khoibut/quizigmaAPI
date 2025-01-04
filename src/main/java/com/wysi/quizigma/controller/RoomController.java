package com.wysi.quizigma.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wysi.quizigma.DTO.RoomDTO;
import com.wysi.quizigma.service.GameService;

@RestController
@RequestMapping("/api/v1")
public class RoomController {

    private final GameService gameService;

    public RoomController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/room")
    public ResponseEntity<Object> createNewRoom(@RequestHeader("Authorization") String token, @RequestBody RoomDTO roomDTO) {
        Map<String, Object> response = new HashMap<>();
        try{
            response.put("roomId", gameService.addRoom(roomDTO));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
