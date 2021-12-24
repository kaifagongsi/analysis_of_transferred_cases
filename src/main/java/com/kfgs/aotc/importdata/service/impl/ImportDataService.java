package com.kfgs.aotc.importdata.service.impl;

import com.kfgs.aotc.importdata.service.IImportDataService;
import com.kfgs.aotc.repository.DetailsOfTheCaseExtRepository;
import com.kfgs.aotc.repository.TransferProcessRepository;
import com.kfgs.aotc.util.CountUtil;
import com.kfgs.aotc.util.ExcelFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ImportDataService implements IImportDataService {


    @Autowired
    TransferProcessRepository transferProcessRepository;

    @Autowired
    DetailsOfTheCaseExtRepository  detailsOfTheCaseExtRepository;


    @Override
    public Map getMaxTransferProcessAndMaxDetailsOfTheCase() {
        String maxTransferProcess = transferProcessRepository.getMaxTransferProcess();
        String maxDetailsOfTheCase = detailsOfTheCaseExtRepository.getMaxDetailsOfTheCase();
        Map map = new HashMap();
        map.put("maxTransferProcess",maxTransferProcess);
        map.put("maxDetailsOfTheCase",maxDetailsOfTheCase);
        return map;
    }

    @Override
    //@Transactional(rollbackFor = Exception.class )
    public boolean parsingTransferProcessExcel( Workbook wb) {
        try{
            List list = new ArrayList<>();
            int numberOfSheets = wb.getNumberOfSheets();
            if(numberOfSheets > 1){
                return false;
            }
            Sheet sheet = wb.getSheetAt(0);
            for( int i = 1; i < sheet.getPhysicalNumberOfRows(); i++){
                ExcelFileUtils.parsingExcelTransferCase(sheet.getRow(i),list);
            }
            log.info("解析成功个数：" + list.size());
            transferProcessRepository.saveAll(list);
            log.info("数据保存成功");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean deleteSendNme() {
        try{
            transferProcessRepository.deleteByNeedToExclude(CountUtil.SEND_NAME_NEED_TO_EXCLUDE);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean parsingDetailsOfTheCaseExcel(Workbook wb) {
        try{
            List list = new ArrayList<>();
            int numberOfSheets = wb.getNumberOfSheets();
            if(numberOfSheets > 1){
                return false;
            }
            Sheet sheet = wb.getSheetAt(0);
            for( int i = 1; i < sheet.getPhysicalNumberOfRows(); i++){
                ExcelFileUtils.parsingExcelDetailsOfTheCase(sheet.getRow(i),list);
            }
            log.info("解析成功个数：" + list.size());
            detailsOfTheCaseExtRepository.saveAll(list);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
