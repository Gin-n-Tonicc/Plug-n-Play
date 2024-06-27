package com.example.plug_n_play;

import com.example.plug_n_play.model.Site;
import com.example.plug_n_play.repository.SiteRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ExcelReaderService {

    @Autowired
    private SiteRepository siteRepository;

    public void readExcelFile() throws IOException {
        String filePath = "server/src/main/resources/static/files/pnp.xlsx";

        try (FileInputStream file = new FileInputStream(filePath)) {
            Workbook workbook = WorkbookFactory.create(file);

            int numberOfSheets = 2;
            System.out.println("Number of sheets: " + numberOfSheets);

            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                System.out.println("Sheet name: " + sheet.getSheetName());

                for (Row row : sheet) {
                    // Skip header row
                    if (row.getRowNum() == 0) continue;

                    Site site = new Site();
                    site.setType(getCellValueAsString(row.getCell(0)));
                    site.setName(getCellValueAsString(row.getCell(1)));
                    site.setUrl(getCellValueAsString(row.getCell(5)));
                    site.setEmail(getCellValueAsString(row.getCell(4)));
                    site.setCountry(getCellValueAsString(row.getCell(3)));
                    site.setTitle(getCellValueAsString(row.getCell(2)));

                    siteRepository.save(site);
                }
            }
            workbook.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
            e.printStackTrace();
        }
    }

    private String getCellValueAsString(Cell cell) {
        if(cell == null){
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}