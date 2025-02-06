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
import com.wysi.quizigma.security.JwtUtil;
import com.wysi.quizigma.service.QuestionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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

    @Operation(summary = "Create a new question", description = "Create a new question for a set")
    @ApiResponse(responseCode = "201", description = "Question created")
    @ApiResponse(responseCode = "400", description = "User is not the owner of this set", content = @Content(
            mediaType = "application/json",
            schema = @Schema(type = "object", example = "{\"error\":\"You are not the owner of this set\"}")
    ))
    @PostMapping("/{setId}/question")
    public ResponseEntity<Object> createNewQuestion(
            @Parameter(description = "User Id", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Set Id", required = true)
            @PathVariable int setId,
            @Parameter(description = "Question details", required = true)
            @RequestBody QuestionDTO question) {
        try {
            question.setSetId(setId);
            questionService.createNewQuestion(question, token);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating question by user Id {}", jwtUtil.getUserId(token));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get questions by set Id", description = "Get all questions for a set")
    @ApiResponse(responseCode = "200", description = "Questions returned")
    @GetMapping("/{setId}/question")
    public ResponseEntity<Object> getQuestionBySetId(
            @Parameter(description = "Set Id", required = true)
            @PathVariable("setId") int id) {
        return new ResponseEntity<>(questionService.getQuestionsBySet(id), HttpStatus.OK);
    }

    @Operation(summary = "Edit question", description = "Edit a question for a set")
    @ApiResponse(responseCode = "200", description = "Question edited")
    @ApiResponse(responseCode = "400", description = "User is not the owner of this set", content = @Content(
            mediaType = "application/json",
            schema = @Schema(type = "object", example = "{\"error\":\"You are not the owner of this set\"}")
    ))
    @PatchMapping("/{setId}/question")
    public ResponseEntity<Object> editQuestion(
            @Parameter(description = "User Id", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Set Id", required = true)
            @PathVariable int setId,
            @Parameter(description = "Question details", required = true)
            @RequestBody QuestionDTO question) {
        try {
            question.setSetId(setId);
            questionService.editQuestion(question, token);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Delete question", description = "Delete a question for a set")
    @ApiResponse(responseCode = "200", description = "Question deleted")
    @ApiResponse(responseCode = "400", description = "User is not the owner of this set", content = @Content(
            mediaType = "application/json",
            schema = @Schema(type = "object", example = "{\"error\":\"You are not the owner of this set\"}")
    ))
    @DeleteMapping("/{setId}/question")
    public ResponseEntity<Object> deleteQuestion(
            @Parameter(description = "User Id", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Set Id", required = true)
            @PathVariable int setId,
            @Parameter(description = "Question details", required = true)
            @RequestBody QuestionDTO question) {
        try {
            question.setSetId(setId);
            questionService.deleteQuestion(question.getId(), token);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error deleting question by user Id {}", jwtUtil.getUserId(token));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
