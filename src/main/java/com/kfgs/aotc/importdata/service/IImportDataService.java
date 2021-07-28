package com.kfgs.aotc.importdata.service;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.Map;

public interface IImportDataService {

    public boolean parsingTransferProcessExcel(Workbook wb);

    public Map getMaxTransferProcessAndMaxDetailsOfTheCase();

    boolean parsingDetailsOfTheCaseExcel(Workbook wb);

    public boolean deleteSendNme();
}
