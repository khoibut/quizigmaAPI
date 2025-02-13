package com.wysi.quizigma.controller;

import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/spreadsheet")
    public ResponseEntity<String> getSpreadsheet(
            @RequestParam("id") String id,
            @RequestHeader("Authorization") String token,
            @RequestBody MultipartFile file) throws IOException {
        try(XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            quizService.fetchSpreadsheetData(id, token, sheet);
            workbook.close();
        } catch (IOException e) {
                return ResponseEntity.status(500).body("Error processing file");
            }
            return ResponseEntity.ok("Success");
        }
}
