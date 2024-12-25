package com.wysi.quizigma.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import com.wysi.quizigma.DTO.AnswerDTO;
import com.wysi.quizigma.service.GameService;

@Controller
public class GameController {
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/player/{roomId}/answer")
    public void answerQuestion(@PathVariable String roomId, AnswerDTO answerDTO) {
        System.out.println("Answering question: " + answerDTO.getAnswer());
    }
}