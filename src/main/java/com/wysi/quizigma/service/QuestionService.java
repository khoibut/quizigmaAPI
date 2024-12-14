package com.wysi.quizigma.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wysi.quizigma.DTO.OptionDTO;
import com.wysi.quizigma.DTO.QuestionDTO;
import com.wysi.quizigma.model.Option;
import com.wysi.quizigma.model.Question;
import com.wysi.quizigma.model.Set;
import com.wysi.quizigma.repository.QuestionRepository;
import com.wysi.quizigma.repository.SetRepository;

@Service
public class QuestionService {

    @Autowired
    private final QuestionRepository questionRepository;
    @Autowired
    private final SetRepository setRepository;

    public QuestionService(QuestionRepository questionRepository, SetRepository setRepository) {
        this.questionRepository = questionRepository;
        this.setRepository = setRepository;
    }

    public void createNewQuestion(QuestionDTO question) {
        List<Option> options = new ArrayList<>();
        System.out.println(question.getSetId());
        Set set = setRepository.findById(question.getSetId()).orElse(null);
        Question newQuestion = new Question(
                question.getQuestion(),
                question.getImage(),
                set,
                options,
                question.getAnswers()
        );
        for (OptionDTO optionDTO : question.getOptions()) {
            Option option = new Option(optionDTO.getOption(), optionDTO.getImage());
            option.setQuestion(newQuestion);
            options.add(option);
        }
        questionRepository.save(newQuestion);
    }

    public List<QuestionDTO> getQuestionsBySet(Integer setId) {
        Set set = setRepository.findById(setId).orElse(null);
        List<Question> questions = questionRepository.findBySet(set);
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (Question question : questions) {
            List<OptionDTO> optionDTOs = new ArrayList<>();
            for (Option option : question.getOptions()) {
                optionDTOs.add(new OptionDTO(option.getOption(), option.getImage()));
            }
            questionDTOs.add(new QuestionDTO(
                    question.getId(),
                    question.getQuestion(),
                    question.getImage(),
                    question.getSet().getId(),
                    optionDTOs,
                    question.getAnswers()
            ));
        }
        return questionDTOs;
    }

    public void deleteQuestion(Integer id) {
        questionRepository.deleteById(id);
    }

    public void editQuestion(QuestionDTO question) {
        List<Option> options = new ArrayList<>();
        Set set = setRepository.findById(question.getSetId()).orElse(null);
        Question newQuestion = questionRepository.findById(question.getId()).orElse(null);
        newQuestion.setQuestion(question.getQuestion());
        newQuestion.setImage(question.getImage());
        newQuestion.setSet(set);
        newQuestion.setOptions(options);
        newQuestion.setAnswers(question.getAnswers());
        for (OptionDTO optionDTO : question.getOptions()) {
            Option option = new Option(optionDTO.getOption(), optionDTO.getImage());
            option.setQuestion(newQuestion);
            options.add(option);
        }
        questionRepository.save(newQuestion);
    }
}
