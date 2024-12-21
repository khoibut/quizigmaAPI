package com.wysi.quizigma.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wysi.quizigma.DTO.QuestionDTO;
import com.wysi.quizigma.Security.JwtUtil;
import com.wysi.quizigma.service.QuestionService;



@RestController
@RequestMapping("/api/v1")
public class QuestionController {
    @Autowired
    private final QuestionService questionService;
    private final JwtUtil jwtUtil;
    private final static Logger logger = LoggerFactory.getLogger(QuestionController.class);
    public QuestionController(QuestionService questionService, JwtUtil jwtUtil) {
        this.questionService = questionService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/{setId}/question")
    public ResponseEntity<Object> createNewQuestion(@RequestHeader("Authorization") String token, @PathVariable int setId, @RequestBody QuestionDTO question ) {
        try{
            question.setSetId(setId);
            questionService.createNewQuestion(question, token);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e) {
            logger.error("Error creating question by user Id {}",jwtUtil.getUserId(token) );
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{setId}/question")
    public ResponseEntity<Object> getQuestionBySetId(@PathVariable("setId") int id) {
        return new ResponseEntity<>(questionService.getQuestionsBySet(id), HttpStatus.OK);
    }

    @PatchMapping("/{setId}/question")
    public ResponseEntity<Object> editQuestion(@RequestHeader("Authorization") String token, @PathVariable int setId, @RequestBody QuestionDTO question) {
        try{
            question.setSetId(setId);
            questionService.editQuestion(question,token);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e) {
            logger.error("Error editing question by user Id {}",jwtUtil.getUserId(token));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/{setId}/question")
    public ResponseEntity<Object> deleteQuestion(@RequestHeader("Authorization") String token,@PathVariable int setId, @RequestBody QuestionDTO question) {
        try{
            question.setSetId(setId);
            questionService.deleteQuestion(question.getId(),token);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error deleting question by user Id {}",jwtUtil.getUserId(token));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
}
