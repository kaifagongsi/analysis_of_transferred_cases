package com.kfgs.aotc.ceshi.count.service;

import com.kfgs.aotc.common.pojo.PageInfo;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.pojo.business.DetailOfCaseFinished;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.repository.DetailOfCaseFinishedRepository;
import com.kfgs.aotc.repository.TransferProcessRepository;
import com.kfgs.aotc.util.CountUtil;
import com.kfgs.aotc.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;


@Service
@Transactional
public class CountServiceImpl implements CountService {

    @Autowired
    private ClassifierInfoRepository classifierInfoRepository;
    @Autowired
    private TransferProcessRepository transferProcessRepository;
    @Autowired
    private DetailOfCaseFinishedRepository detailOfCaseFinishedRepository;


    @Override
    public Result getEffectiveTransferRate(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1: //按人计算
                LinkedHashMap<String,String> result = countAccuracyByWorkerID(parameterVo.getStartDate(),parameterVo.getEndDate(),parameterVo.getSecondClassify());
                List<Map<String,String>> resultList = new ArrayList<>();
                PageInfo pageInfo = new PageInfo();
                resultList.add(result);
                pageInfo.setPage(parameterVo.getPage()); //当前页
                pageInfo.setPageSize(parameterVo.getRows()); //每页条数
                pageInfo.setRecords(resultList.size()); //总记录数
                pageInfo.setTotal(1); //总页数
                pageInfo.setRows(resultList);
                return Result.of(pageInfo);
            case 2: //按部计算
                return countAccuracyByDepartment(parameterVo.getRows(),parameterVo.getPage(),parameterVo.getStartDate(),parameterVo.getEndDate(),parameterVo.getSecondClassify());
            case 3: //按室计算
                return countAccuracyByBranch(parameterVo.getRows(),parameterVo.getPage(),parameterVo.getStartDate(),parameterVo.getEndDate(),parameterVo.getSecondClassify());
            case 4: //按领域计算
                return countAccuracyByFiled(parameterVo.getRows(),parameterVo.getPage(),parameterVo.getStartDate(),parameterVo.getEndDate(),parameterVo.getSecondClassify()); }
        return null;
    }

    @Override
    public Result getAllFieldGroup() {
        List<String> filedslist = new ArrayList<>();
        filedslist = classifierInfoRepository.findDistinctByFieldGroup();
        return Result.of(filedslist);
    }

    /*@Override
    public Result<List<Map<String,String>>> countAccuracy(List<String> list, ParameterVo parameterVo) {
        List<Map<String,String>> resultList = new ArrayList<>();
        String startDate = parameterVo.getStartDate();
        String endDate = parameterVo.getEndDate();

        for (int i=0;i<list.size();i++){
            Map<String,String> map = new HashMap<>();
            String classifierCode = list.get(i);
            map = countAccuracyByWorkerID(classifierCode,startDate,endDate);
            resultList.add(map);
        }
        return Result.of(resultList);
    }*/

    /**
     * 按照分类员领域计算
     * @param rows
     * @param pageNum
     * @param startDate
     * @param endDate
     * @param filed
     * @return
     */
    private Result countAccuracyByFiled(int rows,int pageNum,String startDate,String endDate,String filed){
        PageInfo pageInfo = new PageInfo();
        List<Map<String,String>> resultList = new ArrayList<>();
        Pageable pageable = new PageRequest(pageNum,rows, Sort.Direction.DESC,"dep1");
        Page<ClassifierInfo> page = classifierInfoRepository.findClassifiersCodeByFieldGroup(filed,pageable);
        pageInfo.setPage(pageNum); //当前页
        pageInfo.setPageSize(rows); //每页条数
        pageInfo.setRecords((int)page.getTotalElements()); //总记录数
        pageInfo.setTotal(page.getTotalPages()); //总页数
        LinkedHashMap<String,String> workerResult = new LinkedHashMap<>();
        for (ClassifierInfo classifierInfo:page.getContent()){
            //System.out.println(classifierInfo.toString());
            workerResult = countAccuracyByWorkerID(startDate,endDate,classifierInfo.getClassifiersCode());
            resultList.add(workerResult);
        }
        pageInfo.setRows(resultList);
        return Result.of(pageInfo);
    }
    /**
     * 按科室计算
     * @param rows
     * @param pageNum
     * @param startDate
     * @param endDate
     * @param branch
     * @return
     */
    private Result countAccuracyByBranch(int rows,int pageNum,String startDate,String endDate,String branch){
        PageInfo pageInfo = new PageInfo();
        List<Map<String,String>> resultList = new ArrayList<>();
        Pageable pageable = new PageRequest(pageNum,rows, Sort.Direction.DESC,"dep1");
        String dep1 = branch.substring(0,2);
        String dep2 = branch.substring(2,4);
        Page<ClassifierInfo> page = classifierInfoRepository.findClassifiersCodeByDep1AndDep2(dep1,dep2,pageable);
        pageInfo.setPage(pageNum); //当前页
        pageInfo.setPageSize(rows); //每页条数
        pageInfo.setRecords((int)page.getTotalElements()); //总记录数
        pageInfo.setTotal(page.getTotalPages()); //总页数
        LinkedHashMap<String,String> workerResult = new LinkedHashMap<>();
        for (ClassifierInfo classifierInfo:page.getContent()){
            //System.out.println(classifierInfo.toString());
            workerResult = countAccuracyByWorkerID(startDate,endDate,classifierInfo.getClassifiersCode());
            resultList.add(workerResult);
        }
        pageInfo.setRows(resultList);
        return Result.of(pageInfo);
    }
    /**
     * 按部门计算
     * @param rows
     * @param pageNum
     * @param startDate
     * @param endDate
     * @param department
     * @return
     */
    private Result countAccuracyByDepartment(int rows, int pageNum, String startDate, String endDate, String department){
        PageInfo pageInfo = new PageInfo();
        List<Map<String,String>> resultList = new ArrayList<>();
        //LinkedHashMap<String,String> result = new LinkedHashMap<>();
        Pageable pageable = new PageRequest(pageNum,rows, Sort.Direction.DESC,"dep2");
        Page<ClassifierInfo> page = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(department,pageable);
        //查询结果总行数
        //System.out.println(page.getTotalElements());
        //按照当前分页大小，总页数
        //System.out.println(page.getTotalPages());
        pageInfo.setPage(pageNum); //当前页
        pageInfo.setPageSize(rows); //每页条数
        pageInfo.setRecords((int)page.getTotalElements()); //总记录数
        pageInfo.setTotal(page.getTotalPages()); //总页数
        LinkedHashMap<String,String> workerResult = new LinkedHashMap<>();
        for (ClassifierInfo classifierInfo:page.getContent()){
            //System.out.println(classifierInfo.toString());
            workerResult = countAccuracyByWorkerID(startDate,endDate,classifierInfo.getClassifiersCode());
            resultList.add(workerResult);
        }
        pageInfo.setRows(resultList);
        return Result.of(pageInfo);
        //return Result.of(resultList);
    }

    //private Result countAccuracyByWorkerID(String startDate,String endDate,String classifierID) {
    private LinkedHashMap<String, String> countAccuracyByWorkerID(String startDate, String endDate, String classifierID) {
        double accuracy_num = 0;//有效转案率
        //List<Map<String,String>> resultList = new ArrayList<>();
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //分子1:获取接收转案案件数量
        int receiveTotals = transferProcessRepository.getSumOfReceiveBySendID(classifierID);
        //分子2:拒绝转案案件且已出案中案件数量
        //int rejectTotals = transferProcessRepository.getSumOFRejectBySendID(startDate,endDate,classifierID);
        //拒绝转案且在已出案案件列表中
        List<Object[]> transferProcessList = detailOfCaseFinishedRepository.getValidTransferCases(startDate,endDate,classifierID);
        List<DetailOfCaseFinished> detailOfCaseFinisheds = EntityUtils.castEntity(transferProcessList,DetailOfCaseFinished.class);
        int rejectTotals = transferProcessList.size();
        int totalTrans = receiveTotals + rejectTotals;
        int validTrans = 0; //有效转案
        if (totalTrans == 0){ //分母不能为0
            result.put("分类员代码",classifierID);
            result.put("转案总次数","0");
            result.put("转案接收总次数","0");
            result.put("转案退回且出案数","0");
            result.put("转案退回有效次数","0");
            result.put("有效转案率","0%");
        }else{
            //2.拒绝转案并且在已出案中的所有ipc
            validTrans = 0; //有效转案
            List<String> ipc =  transferProcessRepository.getRefuseReferralBySendTimeBetweenAndTipeTitleAndSendId(startDate, endDate, "拒绝转案", classifierID);

            //获取接收分类员分类号CLASSIFIERS_AND_CODE
            List<String> detailCodes = CountUtil.CLASSIFIERS_AND_CODE.get(classifierID);
            // 如何同一个案子前四位均是一样的，那么就会添加两次，那么在，主分和副分均是H01R,那么finishCodes会有两个H01R
            for(String ipc_str : ipc){
                // 当前案子的所有前四位分类号
                String[] finishCodes = ipc_str.split(",");
                for(int j=0;j<finishCodes.length;j++) {
                    if (finishCodes[j] != null && finishCodes[j].length()>=4){
                        finishCodes[j] = finishCodes[j].substring(0, 4);
                    }
                }
                //比对
                if (!Collections.disjoint(Arrays.asList(finishCodes),detailCodes)){
                    validTrans ++;
                }

            }
            accuracy_num = (receiveTotals + validTrans)*100/totalTrans;
            result.put("分类员代码",classifierID);
            result.put("转案总次数",Integer.toString(totalTrans));
            result.put("转案接收总次数", Integer.toString(receiveTotals));
            result.put("转案退回且出案数",Integer.toString(rejectTotals));
            result.put("转案退回有效次数",Integer.toString(validTrans));
            result.put("有效转案率",df.format(accuracy_num)+"%");
            System.out.println(result);
        }
        return result;
        /*resultList.add(result);
        return Result.of(resultList);*/
    }
}
