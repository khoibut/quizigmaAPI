package com.wysi.quizigma.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.wysi.quizigma.DTO.QuestionDTO;
import com.wysi.quizigma.service.QuestionService;



@RestController
@RequestMapping("/api")
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

    @GetMapping("")
    public ResponseEntity<Object> getQuestionBySetId(@RequestParam("id") int id) {
        return new ResponseEntity<>(questionService.getQuestionsBySet(id), HttpStatus.OK);
    }

    @PatchMapping("")
    public ResponseEntity<Object> editQuestion(@RequestParam("id") int id, QuestionDTO question) {
        questionService.editQuestion(question);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @DeleteMapping("")
    public ResponseEntity<Object> deleteQuestion(@RequestParam("id") int id, QuestionDTO question) {
        questionService.deleteQuestion(question.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}
