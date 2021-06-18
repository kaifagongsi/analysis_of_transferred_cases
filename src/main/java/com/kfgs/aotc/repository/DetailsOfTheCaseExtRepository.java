package com.kfgs.aotc.repository;

import com.kfgs.aotc.common.repository.CommonRepository;
import com.kfgs.aotc.pojo.business.ext.DetailsOfTheCaseExt;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * lxl
 */
public interface DetailsOfTheCaseExtRepository extends CommonRepository<DetailsOfTheCaseExt,String> {



    @Query(nativeQuery = true,value = " select  dotc.ipcmi || ',' || dotc.ipcoi || ',' || dotc.ipca  as ipc,tp.receive_id as receiveId, dotc.case_id as caseId  from aotc_detailsofthecase dotc  left join    aotc_transfer_process  tp " +
            " on dotc.case_id = tp.case_id  " +
            " where tp.receive_time between ?1 and ?2 " +
            " and tp.tips_state = ?3 " +
            " and dotc.case_id = tp.case_id  " +
            " and tp.receive_id in (?4)  ")
    List<Object[]> getRefuseReferralByReceiveTimeBetweenAndTipeStateAndReceiveIds(String startDate, String endDate, String tipsState, List classifierInfoCode);

    /**
     * 获取出案案件数
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param classifiersCode  用户的6位ID
     * @return
     */
    @Query(nativeQuery = true,value = "select count(distinct case_id) " +
            "  from aotc_detailsofthecase " +
            " where out_time between ?1 and ?2 " +
            "   and " +
            "   classifiers_code = ?3 ")
    public Integer getSumNumberOfCaseByClassifiersCodeAndOutTime(String startDate,String endDate,String classifiersCode);

    @Query(nativeQuery = true,value = " select  dotc.ipcmi || ',' || dotc.ipcoi || ',' || dotc.ipca  as ipc,tp.receive_id as receiveId, dotc.case_id as caseId  from aotc_detailsofthecase dotc  left join    aotc_transfer_process  tp " +
            " on dotc.case_id = tp.case_id  " +
            " where tp.send_time between ?1 and ?2 " +
            " and tp.tips_state = ?3 " +
            " and dotc.case_id = tp.case_id  " +
            " and tp.send_id in (?4)  ")
    List<Object[]> getRefuseReferralBySendTimeBetweenAndTipeStateAndSendIds(String startDate, String endDate, String tipsState, List classifierInfoCode);

    @Query(nativeQuery = true,value = "select * " +
            "  from aotc_detailsofthecase " +
            " where out_time between ?1 and ?2 " +
            "   and " +
            "   classifiers_code in (?3) ")
    List<Object[]> getSumAllNumberOfCaseByClassifiersCodeAndOutTime(String startDate, String endDate, List classifierInfoCode);
}
