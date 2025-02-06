package com.wysi.quizigma.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wysi.quizigma.service.QuizService;




@RestController
@RequestMapping("/api/v1/import")
public class ImportController {
    @Autowired
    private final QuizService quizService;

    public ImportController(QuizService quizService) {
        this.quizService = quizService;
    }
    
    @PostMapping("/quizizz")
    public ResponseEntity<String> getQuizizz(
            @RequestParam("id") String id,
            @RequestHeader("Authorization") String token,
            @RequestBody HashMap<String, String> body) {  

        quizService.fetchQuizData(id, token, body.get("url"));
        return ResponseEntity.ok("Success");
    }
    
    @PostMapping("/blooket")
    public ResponseEntity<String> getBlooket(
            @RequestParam("id") String id,
            @RequestHeader("Authorization") String token,
            @RequestBody HashMap<String, String> body) {  

        quizService.fetchBlooketData(id, token, body.get("url"));
        return ResponseEntity.ok("Success");
    }
}
