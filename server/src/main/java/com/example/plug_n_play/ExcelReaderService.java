package com.example.plug_n_play;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class ExcelReaderService {

    public void readExcelFile() throws IOException {
        String filePath = "server/src/main/resources/static/files/pnp.xlsx";

        try (FileInputStream file = new FileInputStream(filePath)) {
            Workbook workbook = WorkbookFactory.create(file);

            int numberOfSheets = workbook.getNumberOfSheets();
            System.out.println("Number of sheets: " + numberOfSheets);

            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                System.out.println("Sheet name: " + sheet.getSheetName());

                for (Row row : sheet) {

                    for (Cell cell : row) {

                        switch (cell.getCellType()) {
                            case STRING -> System.out.print(cell.getStringCellValue() + "\t");
                            case NUMERIC -> System.out.print(cell.getNumericCellValue() + "\t");
                            case BOOLEAN -> System.out.print(cell.getBooleanCellValue() + "\t");
                            default -> System.out.print(" \t");
                        }
                    }
                    System.out.println();
                }
                System.out.println();
            }

            workbook.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
            e.printStackTrace();
        }
    }
}