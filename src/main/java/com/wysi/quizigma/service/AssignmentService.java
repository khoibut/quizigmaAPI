package com.wysi.quizigma.service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.wysi.quizigma.DTO.AnswerDTO;
import com.wysi.quizigma.DTO.AssignmentDTO;
import com.wysi.quizigma.DTO.PlayerDTO;
import com.wysi.quizigma.DTO.QuestionDTO;
import com.wysi.quizigma.model.User;

@Service
public class AssignmentService {

    ConcurrentHashMap<String, AssignmentDTO> assignments = new ConcurrentHashMap<>();
    private final QuestionService questionService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public AssignmentService(QuestionService questionService, UserService userService,
            SimpMessagingTemplate messagingTemplate) {
        this.questionService = questionService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    private String generatedAssignmentId() {
        Random random = new Random();
        while (true) {
            String assignmentId = String.valueOf(random.nextInt(999999));
            if (!assignments.containsKey(assignmentId)) {
                return assignmentId;
            }
        }
    }

    public String addAssignment(AssignmentDTO assignmentDTO, String token) {

        try {
            User user = userService.getUser(token);
            assignmentDTO.setCreator(user.getUsername());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Token is invalid");
        }
        String assignmentId = generatedAssignmentId();
        assignmentDTO.setId(assignmentId);
        assignments.put(assignmentId, assignmentDTO);
        return assignmentId;
    }

    public void addPlayer(String assignmentId, String player) {
        if (!assignments.containsKey(assignmentId)) {
            throw new IllegalArgumentException("Assignment does not exist");
        }

        AssignmentDTO assignment = assignments.get(assignmentId);
        PlayerDTO playerDTO = new PlayerDTO(player);
        if (assignment.getPlayers().contains(playerDTO)) {
            throw new IllegalArgumentException("Player already exists");
        }
        assignment.addPlayer(playerDTO);
    }

    public boolean answerQuestion(String assignmentId, AnswerDTO answer) {
        if (!assignments.containsKey(assignmentId)) {
            throw new IllegalArgumentException("Assignment does not exist");
        }
        AssignmentDTO assignment = assignments.get(assignmentId);
        if (assignment.getPlayers().stream().noneMatch(player -> player.getName().equals(answer.getPlayer()))) {
            throw new IllegalArgumentException("Player does not exist");
        }
        if (assignment.getDeadline().before(new java.util.Date())) {
            throw new IllegalArgumentException("Assignment has expired");
        }

        List<QuestionDTO> questions = questionService.getQuestionsBySet(assignment.getSetId());

        PlayerDTO player = assignment.getPlayers().stream().filter(p -> p.getName().equals(answer.getPlayer())).findFirst()
                .get();

        for (QuestionDTO question : questions) {
            if (question.getId().equals(answer.getQuestionId())) {
                if (question.getType().equals("MCQ")) {
                    if (question.getAnswers().contains(Integer.valueOf(answer.getAnswer()))) {
                        player.incrementScore();
                        player.incrementCorrect();
                        if (player.getIncorrect() + player.getCorrect() >= assignment.getMaxQuestions()) {

                        }
                        return true;
                    }
                } else {
                    if (question.getOptions().stream().anyMatch(option -> option.getOption().equals(answer.getAnswer()))) {
                        player.incrementScore();
                        player.incrementCorrect();
                        if (player.getIncorrect() + player.getCorrect() >= assignment.getMaxQuestions()) {

                        }
                        return true;
                    }
                }
            }
        }
        player.incrementIncorrect();
        if (player.getIncorrect() + player.getCorrect() >= assignment.getMaxQuestions()) {

        }
        return false;
    }

    public List<QuestionDTO> getQuestions(String assignmentId) {
        if (!assignments.containsKey(assignmentId)) {
            throw new IllegalArgumentException("Assignment does not exist");
        }
        AssignmentDTO assignment = assignments.get(assignmentId);
        List<QuestionDTO> questions = questionService.getQuestionsBySet(assignment.getSetId());
        for (QuestionDTO question : questions) {
            if (question.getType().equals("MCQ")) {
                question.setAnswers(null);
            } else {
                question.setOptions(null);
            }
        }
        return questions;
    }
}
