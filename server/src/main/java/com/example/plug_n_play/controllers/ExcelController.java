package com.example.plug_n_play.controllers;

import com.example.plug_n_play.services.ExcelReaderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/excel")
public class ExcelController {

    private final ExcelReaderService excelReaderService;

    public ExcelController(ExcelReaderService excelReaderService) {
        this.excelReaderService = excelReaderService;
    }

    @PostMapping("/upload")
    public void uploadExcelFile() throws IOException {
        excelReaderService.readExcelFile();
    }
}

