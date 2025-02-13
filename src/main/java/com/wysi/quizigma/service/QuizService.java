package com.wysi.quizigma.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.wysi.quizigma.DTO.OptionDTO;
import com.wysi.quizigma.DTO.QuestionDTO;

@Service
public class QuizService {

    private final WebClient webClient;
    private final QuestionService questionService;

    public QuizService(WebClient.Builder webClientBuilder, QuestionService questionService) {
        this.webClient = webClientBuilder.build();
        this.questionService = questionService;
    }

    public void fetchSpreadsheetData(String setId, String token, XSSFSheet sheet) {
        List<QuestionDTO> newQuestions = new ArrayList<>();
        for (int row = 1; row < sheet.getLastRowNum(); row++) {
            if (sheet.getRow(row) == null) {
                continue;
            }
            XSSFRow currentRow = sheet.getRow(row);
            QuestionDTO newQuestion = new QuestionDTO();
            newQuestion.setSetId(Integer.valueOf(setId));
            newQuestion.setQuestion(currentRow.getCell(0).getStringCellValue());
            newQuestion.setType(currentRow.getCell(1).getStringCellValue());
            List<OptionDTO> newOptions = new ArrayList<>();
            for (int col=2; col<currentRow.getLastCellNum()-1; col++) {
                XSSFCell currentCell = sheet.getRow(row).getCell(col);
                if(currentCell == null) {
                    continue;
                }
                OptionDTO newOption = new OptionDTO();
                newOption.setOption(currentCell.getStringCellValue());
                newOption.setImage("null");
                newOptions.add(newOption);
            }
            newQuestion.setOptions(newOptions);
            if("TA".equals(newQuestion.getType())){
                newQuestion.setAnswers(new ArrayList<>());
                newQuestion.setImage(null);
                newQuestions.add(newQuestion);
                continue;
            }
            List<Integer> newAnswers = new ArrayList<>();
            String answer = currentRow.getCell(currentRow.getLastCellNum()-1).getStringCellValue();
            String[] answers = answer.split(",");
            for (String ans : answers) {
                newAnswers.add(Integer.valueOf(ans));
            }
            newQuestion.setAnswers(newAnswers);
            newQuestion.setImage(null);
            newQuestions.add(newQuestion);
        }
        newQuestions.forEach(question -> {
            questionService.createNewQuestion(question, token);
            System.out.println(question.getQuestion());
            System.out.println(question.getType());
            question.getOptions().forEach(option -> {
                System.out.println(option.getOption());
            });
            System.out.println(question.getAnswers());
        });
    }

    public void fetchBlooketData(String setId, String token, String url) {
        URI uri = URI.create(url);
        String[] parts = uri.getPath().split("/");
        url = "https://dashboard.blooket.com/api/games?gameId=" + parts[2];
        try {
            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();

            if (response != null) {
                parseBlooketData(response, setId, token);
            }
        } catch (WebClientResponseException e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void parseBlooketData(Map<String, Object> response, String setId, String token) {
        List<Map<String, Object>> questions = (List<Map<String, Object>>) response.get("questions");
        List<QuestionDTO> newQuestions = new ArrayList<>();
        questions.sort((a, b) -> {
            Integer aIndex = (Integer) a.get("number");
            Integer bIndex = (Integer) b.get("number");
            return aIndex.compareTo(bIndex);
        });
        questions.forEach(question -> {
            QuestionDTO newQuestion = new QuestionDTO();
            newQuestion.setSetId(Integer.valueOf(setId));
            newQuestion.setQuestion((String) question.get("question"));
            if (question.get("qType").equals("mc")) {
                newQuestion.setType("MCQ");
            } else if (question.get("qType").equals("typing")) {
                newQuestion.setType("TA");
            } else {
                return;
            }
            System.out.println(question.get("question"));
            System.out.println(question.get("qType"));
            List<OptionDTO> newOptions = new ArrayList<>();
            List<String> options = (List<String>) question.get("answers");
            List<Integer> newAnswers = new ArrayList<>();
            List<String> answers = (List<String>) question.get("correctAnswers");
            for (int i = 0; i < options.size(); i++) {
                OptionDTO newOption = new OptionDTO();
                newOption.setOption(options.get(i));
                newOption.setImage("null");
                newOptions.add(newOption);
                if (answers.contains(options.get(i))) {
                    newAnswers.add(i + 1);
                }
            }
            newQuestion.setOptions(newOptions);
            newQuestion.setAnswers(newAnswers);
            newQuestion.setImage(null);
            newQuestions.add(newQuestion);
        });
        newQuestions.forEach(question -> {
            questionService.createNewQuestion(question, token);
            System.out.println(question.getQuestion());
            System.out.println(question.getType());
            question.getOptions().forEach(option -> {
                System.out.println(option.getOption());
            });
            question.getAnswers().forEach(answer -> {
                System.out.println(answer);
            });
        });
    }

    public void fetchQuizData(String setId, String token, String url) {
        URI uri = URI.create(url);
        String[] parts = uri.getPath().split("/");
        url = "https://quizizz.com/_quizserver/main/v2/quiz/" + parts[3] + "?convertQuestions=false&includeFsFeatures=true&sanitize=read&questionMetadata=true";
        System.out.println(url);
        try {
            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();

            if (response != null) {
                parseQuizData(response, setId, token);
            }
        } catch (WebClientResponseException e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void parseQuizData(Map<String, Object> response, String setId, String token) {
        List<QuestionDTO> questions = new ArrayList<>();
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        Map<String, Object> quiz = (Map<String, Object>) data.get("quiz");

        Map<String, Object> info = (Map<String, Object>) quiz.get("info");
        List<Map<String, Object>> questionsQuizizz = (List<Map<String, Object>>) info.get("questions");
        questionsQuizizz.forEach(question -> {
            QuestionDTO newQuestion = new QuestionDTO();
            Map<String, Object> structure = (Map<String, Object>) question.get("structure");
            Map<String, Object> query = (Map<String, Object>) structure.get("query");
            String questiontext = (String) query.get("text");
            String type = (String) structure.get("kind");
            if ("BLANK".equals(type)) {
                type = "TA";
            } else if ("MCQ".equals(type)) {
                type = "MCQ";
            } else {
                return;
            }
            newQuestion.setQuestion(questiontext);
            newQuestion.setType(type);
            newQuestion.setImage("null");
            newQuestion.setSetId(Integer.valueOf(setId));
            List<OptionDTO> newOptions = new ArrayList<>();
            List<Map<String, Object>> options = (List<Map<String, Object>>) structure.get("options");
            Integer answers = null;
            if (type.equals("MCQ")) {
                answers = structure.get("answer") != null ? (Integer) structure.get("answer") : null;
                answers = answers != null ? answers + 1 : null;
            }
            options.forEach(option -> {
                OptionDTO newOption = new OptionDTO();
                String optiontext = (String) option.get("text");
                newOption.setOption(optiontext);
                newOption.setImage("null");
                newOptions.add(newOption);
            });
            List<Integer> newAnswers = new ArrayList<>();
            if (answers != null) {
                newAnswers.add(answers);
            }
            newQuestion.setOptions(newOptions);
            newQuestion.setAnswers(newAnswers);
            if (!"MCQ".equals(type) || !"TA".equals(type)) {
                questions.add(newQuestion);
            }
        });
        questions.forEach(question -> {
            questionService.createNewQuestion(question, token);
            System.out.println(question.getQuestion());
            System.out.println(question.getType());
            question.getOptions().forEach(option -> {
                System.out.println(option.getOption());
            });
            question.getAnswers().forEach(answer -> {
                System.out.println(answer);
            });
        });
    }
}
