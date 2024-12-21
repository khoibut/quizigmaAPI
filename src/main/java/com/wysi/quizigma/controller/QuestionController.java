package com.wysi.quizigma.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wysi.quizigma.DTO.QuestionDTO;
import com.wysi.quizigma.service.QuestionService;



@RestController
@RequestMapping("/api/v1")
public class QuestionController {
    @Autowired
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/{setId}/question")
    public ResponseEntity<Object> createNewQuestion(@PathVariable int setId, @RequestBody QuestionDTO question ) {
        question.setSetId(setId);
        questionService.createNewQuestion(question);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{setId}/question")
    public ResponseEntity<Object> getQuestionBySetId(@PathVariable("setId") int id) {
        return new ResponseEntity<>(questionService.getQuestionsBySet(id), HttpStatus.OK);
    }

    @PatchMapping("/{setId}/question")
    public ResponseEntity<Object> editQuestion(@PathVariable int setId, @RequestBody QuestionDTO question) {
        questionService.editQuestion(question);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @DeleteMapping("/{setId}/question")
    public ResponseEntity<Object> deleteQuestion(@PathVariable int setId, @RequestBody QuestionDTO question) {
        questionService.deleteQuestion(question.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}
