package com.kfgs.aotc.repository;

import com.kfgs.aotc.common.repository.CommonRepository;
import com.kfgs.aotc.pojo.business.DetailsOfTheCase;
import com.kfgs.aotc.pojo.business.ext.DetailsOfTheCaseExt;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetailsOfTheCaseRepository extends CommonRepository<DetailsOfTheCase,String> {

    /*@Query(nativeQuery = true, value = "select dotc.* from aotc_detailsofthecase dotc where exists   " +
            "(" +
            "select tp.case_id  from aotc_transfer_process tp where tp.receive_time between ?1 and ?2 " +
            "and tp.tips_state = ?3 " +
            "and dotc.case_id = tp.case_id " +
            "and tp.receive_id in (?4) " +
            ")")*/

    @Query(nativeQuery = true,value = " select dotc.*,tp.receive_id, ipcmi || ',' || ipcoi || ',' || ipca  as ipc from aotc_detailsofthecase dotc  left join    aotc_transfer_process  tp " +
            " on dotc.case_id = tp.case_id  " +
            " where tp.receive_time between ?1 and ?2 " +
            " and tp.tips_state = ?3 " +
            " and dotc.case_id = tp.case_id  " +
            " and tp.receive_id in (?4)  ")
    List<DetailsOfTheCaseExt> getRefuseReferralByReceiveTimeBetweenAndTipeStateAndReceiveIds(String startDate, String endDate, String tipsState, List classifierInfoCode);


}
