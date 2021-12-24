package com.kfgs.aotc.config.async;

import com.kfgs.aotc.importdata.service.IImportDataService;
import com.kfgs.aotc.util.ExcelFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.Future;

@Slf4j
@Component
public class AsyncFileParsing<E>  {

    @Autowired
    IImportDataService importDataService;

    /**
     * 最简单的异步调用，返回值为void
     */
    @Async("asyncTaskExecutor")
    public void asyncInvokeSimplest() {
        log.info("asyncSimplest");
    }

    /**
     * 带参数的异步调用 异步方法可以传入参数
     *
     * @param s
     */
    @Async("asyncTaskExecutor")
    public void asyncInvokeWithParameter(String s) {
        log.info("asyncInvokeWithParameter, parementer={}", s);
    }


    @Async("asyncTaskExecutor")
    public Future<String> doTask1(String filePath) throws InterruptedException{
        long start = System.currentTimeMillis();
        log.info(filePath + "--------------------------------------" + Thread.currentThread().getName());
        Thread.sleep(500);
        long end = System.currentTimeMillis();
        return new AsyncResult<>("Task1 accomplished!");
    }

    /**
     * 异常调用返回Future
     * 对于返回值是Future，不会被AsyncUncaughtExceptionHandler处理，需要我们在方法中捕获异常并处理
     * 或者在调用方在调用Futrue.get时捕获异常进行处理
     *
     * @param i
     * @return
     */
    @Async("asyncTaskExecutor")
    public Future<String> asyncInvokeReturnFuture(int i) {
        log.info("asyncInvokeReturnFuture, parementer={}", i);
        Future<String> future;
        try {
            Thread.sleep(1000 * 1);
            future = new AsyncResult<String>("success:" + i);
            throw new IllegalArgumentException("a");
        } catch (InterruptedException e) {
            future = new AsyncResult<String>("error");
        } catch(IllegalArgumentException e){
            future = new AsyncResult<String>("error-IllegalArgumentException");
        }
        return future;
    }

    @Async("asyncTaskExecutor")
    public Future<String> excelFileParsing(File exceFile) {
        Future<String> future;
        try {
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
                future = new AsyncResult<String>("success");
            }else if(flag && !deleteSendNme){
                log.info("文件："+exceFile.getName() + "上传完成，删除失败。");
                future = new AsyncResult<String>("解析完成，删除失败。");
            }else {
                log.info("文件："+exceFile.getName() + "解析失败。");
                future = new AsyncResult<String>(exceFile.getName()+"解析失败。");
            }

        } catch(IllegalArgumentException e){
            future = new AsyncResult<String>("error-IllegalArgumentException");
        } catch (Exception e){
            future = new AsyncResult<String>("error");
        }
        return future;
    }


}
