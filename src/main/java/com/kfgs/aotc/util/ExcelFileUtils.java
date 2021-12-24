package com.kfgs.aotc.util;

import com.kfgs.aotc.pojo.business.DetailsOfTheCase;
import com.kfgs.aotc.pojo.business.TransferProcess;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class ExcelFileUtils {

    public static void parsingExcelDetailsOfTheCase(Row row, List list){
        String ipcmi = row.getCell(10).getRichStringCellValue().getString();
        if(!StringUtils.isEmpty(ipcmi)){
            String classifiersCode = row.getCell(1).getRichStringCellValue().getString();
            String classifiersName = row.getCell(2).getRichStringCellValue().getString();
            String caseId = row.getCell(3).getRichStringCellValue().getString();
            String inTime = row.getCell(4).getRichStringCellValue().getString();
            String outTime = row.getCell(5).getRichStringCellValue().getString();
            String caseTitle = row.getCell(6).getRichStringCellValue().getString();
            String caseType = row.getCell(7).getRichStringCellValue().getString();
            String state = row.getCell(8).getRichStringCellValue().getString();
            String simpleCode = row.getCell(9).getRichStringCellValue().getString();
            String ipcoi = row.getCell(11).getRichStringCellValue().getString();
            String ipca = row.getCell(12).getRichStringCellValue().getString();
            String cci = row.getCell(13).getRichStringCellValue().getString();
            String cca = row.getCell(14).getRichStringCellValue().getString();
            String cset = row.getCell(15).getRichStringCellValue().getString();
            list.add(new DetailsOfTheCase(UUIDUtil.getUUID(),classifiersCode,classifiersName,caseId
                    ,inTime,outTime,caseTitle,caseType,state,simpleCode,ipcmi,ipcoi,ipca,cci,cca,cset));
        }
    }

    public static void parsingExcelTransferCase(Row row, List list){
        String caseId = row.getCell(1).getRichStringCellValue().getString();
        String sendId = row.getCell(2).getRichStringCellValue().getString();
        String sendName = row.getCell(3).getRichStringCellValue().getString();
        String receiveId = row.getCell(4).getRichStringCellValue().getString();
        String receiveName = row.getCell(5).getRichStringCellValue().getString();
        String sendTime = row.getCell(6).getRichStringCellValue().getString();
        String receiveTime = row.getCell(7).getRichStringCellValue().getString();
        String tipsState = row.getCell(8).getRichStringCellValue().getString();
        String tipsContent = row.getCell(9).getRichStringCellValue().getString();
        list.add(new TransferProcess(UUIDUtil.getUUID(),caseId,sendId,sendName,sendTime,receiveId,receiveName,receiveTime,"",tipsContent,tipsState));
    }

    public static void parsingExcel(Row row, List list){

    }


    public static Workbook getWorkBook(MultipartFile file){
        Workbook wb = null;
        try {
            if(file.getOriginalFilename().endsWith(".xls")){
                wb = new HSSFWorkbook(file.getInputStream());
            }else if(file.getOriginalFilename().endsWith(".xlsx")){
                wb = new XSSFWorkbook(file.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }
    public static Workbook getWorkBook(File file){
        Workbook wb = null;
        try {
            if(file.getName().endsWith(".xls")){
                wb = new HSSFWorkbook(new FileInputStream(file));
            }else if(file.getName().endsWith(".xlsx")){
                wb = new XSSFWorkbook(new FileInputStream(file));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }
}
