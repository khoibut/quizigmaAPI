package com.wysi.quizigma.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wysi.quizigma.DTO.OptionDTO;
import com.wysi.quizigma.DTO.QuestionDTO;
import com.wysi.quizigma.model.Option;
import com.wysi.quizigma.model.Question;
import com.wysi.quizigma.model.Set;
import com.wysi.quizigma.repository.QuestionRepository;
import com.wysi.quizigma.repository.SetRepository;

import jakarta.transaction.Transactional;

@Service
public class QuestionService {

    @Autowired
    private final QuestionRepository questionRepository;
    @Autowired
    private final SetRepository setRepository;
    @Autowired
    private final UserService userService;

    public QuestionService(QuestionRepository questionRepository, SetRepository setRepository, UserService userService) {
        this.questionRepository = questionRepository;
        this.setRepository = setRepository;
        this.userService = userService;
    }

    public void createNewQuestion(QuestionDTO question, String token) {
        if (!userService.getUser(token).getId().equals(setRepository.findById(question.getSetId()).orElse(null).getOwner().getId())) {
            throw new IllegalArgumentException("You are not the owner of this set");
        }
        List<Option> options = new ArrayList<>();
        List<Integer> answers = question.getAnswers();
        Set set = setRepository.findById(question.getSetId()).orElse(null);
        Question newQuestion = new Question();
        for (OptionDTO optionDTO : question.getOptions()) {
            Option option = new Option(optionDTO.getOption(), optionDTO.getImage());
            option.setQuestion(newQuestion);
            options.add(option);
        }
        newQuestion.setQuestion(question.getQuestion());
        newQuestion.setType(question.getType());
        newQuestion.setImage(question.getImage());
        newQuestion.setSet(set);
        newQuestion.setOptions(options);
        newQuestion.setAnswers(answers);
        questionRepository.save(newQuestion);
    }

    @Transactional
    public List<QuestionDTO> getQuestionsBySet(Integer setId) {
        Set set = setRepository.findById(setId).orElseThrow(() -> new IllegalArgumentException("Set not found"));
        List<Question> questions = questionRepository.findBySet(set);
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (Question question : questions) {
            List<Integer> answers = question.getAnswers();
            answers.size(); // Ensure this is fetched
            List<OptionDTO> optionDTOs = question.getOptions()
                    .stream()
                    .map(opt -> new OptionDTO(opt.getId(), opt.getOption(), opt.getImage()))
                    .collect(Collectors.toList());
            questionDTOs.add(new QuestionDTO(
                    question.getId(),
                    question.getQuestion(),
                    question.getType(),
                    question.getImage(),
                    question.getSet().getId(),
                    optionDTOs,
                    answers
            ));
        }
        return questionDTOs;
    }

    public void deleteQuestion(Integer id, String token) {
        if (!userService.getUser(token).getId().equals(questionRepository.findById(id).orElse(null).getSet().getOwner().getId())) {
            throw new IllegalArgumentException("You are not the owner of this set");
        }
        questionRepository.deleteById(id);
    }

    public void editQuestion(QuestionDTO question, String token) {
        if (!userService.getUser(token).getId().equals(questionRepository.findById(question.getId()).orElse(null).getSet().getOwner().getId())) {
            throw new IllegalArgumentException("You are not the owner of this set");
        }
        Set set = setRepository.findById(question.getSetId()).orElse(null);
        Question newQuestion = questionRepository.findById(question.getId()).orElse(null);
        newQuestion.setQuestion(question.getQuestion());
        newQuestion.setImage(question.getImage());
        newQuestion.setSet(set);
        newQuestion.setAnswers(question.getAnswers());
        List<Option> existingOptions = newQuestion.getOptions();
        existingOptions.clear();
        for (OptionDTO optionDTO : question.getOptions()) {
            Option option = new Option(optionDTO.getId(), optionDTO.getOption(), optionDTO.getImage());
            option.setQuestion(newQuestion);
            existingOptions.add(option);
        }
        questionRepository.save(newQuestion);
    }
}
