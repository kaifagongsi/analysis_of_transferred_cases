package com.kfgs.aotc.importdata.controller;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.importdata.service.IImportDataService;
import com.kfgs.aotc.util.ExcelFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
@RequestMapping("/importdata/")
@Slf4j
public class ImportDataController {

    @Autowired
    IImportDataService importDataService;


    @GetMapping("")
    public ModelAndView authority(){
        Map map = importDataService.getMaxTransferProcessAndMaxDetailsOfTheCase();
        return new ModelAndView("ceshi/importdata","data",map);
    }



    @PostMapping("importFileZ")
    public Result importTransferProcessDataByMutilsFiles(MultipartFile file){
       Workbook wb = ExcelFileUtils.getWorkBook(file);
       boolean flag = false;
       if(wb != null){
           flag = importDataService.parsingTransferProcessExcel(wb);
       }
       log.info("文件："+file.getOriginalFilename() + "上传完成，开始删除");
        //删除需要排除的发送者 20个
       boolean deleteSendNme = importDataService.deleteSendNme();
       if(flag && deleteSendNme){
           log.info("文件："+file.getOriginalFilename() + "上传完成，删除完成。");
           return Result.of(null);
       }else if(flag && !deleteSendNme){
           log.info("文件："+file.getOriginalFilename() + "上传完成，删除失败。");
           return Result.of(null,false,"删除多余数据失败，请联系管理员进行删除");
       }else {
           log.info("文件："+file.getOriginalFilename() + "上传失败。");
           return Result.of(null,false,"请检查数据excel是否符合规范（仅有一个sheet文件）");
       }
    }



    @PostMapping("importFileC")
    public Result importDetailsOfTheCaseDataByMutilsFiles(MultipartFile file){
        log.info("接收文件：" + file.getOriginalFilename());
        Workbook wb = ExcelFileUtils.getWorkBook(file);
        boolean flag = false;
        if(wb != null){
            flag = importDataService.parsingDetailsOfTheCaseExcel(wb);
        }
        if(flag){
            return Result.of(null);
        }else{
            return Result.of(null,false,"请检查数据excel是否符合规范（仅有一个sheet文件）");
        }
    }

}
