package com.online.food.services.excel;

import com.online.food.modal.City;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
@Service
public class CityExcelService {
    Logger logger = LoggerFactory.getLogger(CityExcelService.class);
    public static String[] HEADERS = {
            "City_ID", "City_Name", "City_Discription"};
    public static String SHEET_NAME = "City_Details";

    public Workbook dataToExcel(List<City> list) throws IOException {
        //create work
        Workbook workbook = new XSSFWorkbook();


        try {

            //create sheet
            Sheet sheet = workbook.createSheet(SHEET_NAME);
            //create Row : Header Raw
            Row row = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(HEADERS[i]);
            }

            //Value raw

            int rowIndex = 1;
            for (City city : list) {
                Row dataRow = sheet.createRow(rowIndex);
                rowIndex++;
                dataRow.createCell(0).setCellValue("#CITY-" + city.getCityId());
                dataRow.createCell(1).setCellValue(city.getCityName());
                dataRow.createCell(2).setCellValue(city.getCityDiscription());
            }


            logger.info("Downloaded");
            return workbook;

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("fail to input data to excel");
            return null;
        }

    }

}
