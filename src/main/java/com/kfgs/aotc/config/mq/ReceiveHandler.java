package com.kfgs.aotc.config.mq;

import com.alibaba.fastjson.JSON;
import com.kfgs.aotc.config.mq.config.RabbitmqConfig;
import com.kfgs.aotc.importdata.service.impl.ImportDataService;
import com.kfgs.aotc.util.ExcelFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

@Component
@Slf4j
public class ReceiveHandler {

    @Autowired
    ImportDataService importDataService;

    @Value("${FILE_SAVE.tpd}")
    String tpd;


    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_PARSE_EXCEL})
    public void receive_excel(String msg){
        System.out.println("-------------------------------------------------------------");
        //解析消息
        Map map = JSON.parseObject(msg, Map.class);
        String path = map.get("file").toString();
        log.info("receive excel post :{}",path);
        File exceFile = new File(path);
        Workbook wb = ExcelFileUtils.getWorkBook(exceFile);
        boolean flag = false;
        if(wb != null){
            flag = importDataService.parsingTransferProcessExcel(wb);
        }
        log.info("文件："+exceFile.getName() + "上传完成，开始删除");
        //删除需要排除的发送者 20个
        boolean deleteSendNme = importDataService.deleteSendNme();
        if(flag && deleteSendNme){
            log.info("文件："+exceFile.getName() + "上传完成，删除完成。");
        }else if(flag && !deleteSendNme){
            log.info("文件："+exceFile.getName() + "上传完成，删除失败。");
        }else {
            log.info("文件："+exceFile.getName() + "上传失败。");
        }
    }
}

