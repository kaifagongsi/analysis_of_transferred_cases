package com.kfgs.aotc.repository;

import com.kfgs.aotc.common.repository.CommonRepository;
import com.kfgs.aotc.pojo.business.DetailOfCaseFinished;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetailOfCaseFinishedRepository extends CommonRepository<DetailOfCaseFinished, String> {

    @Query(value = "select t.case_id as id,t.receive_id as classifiersCode,d.ipcmi as ipcmi,d.ipcoi as ipcoi,d.ipca as ipca"+
    " from aotc_transfer_process t inner join aotc_detailsofthecase d on t.case_id=d.case_id and t.tips_state='拒绝转案' and send_time between ?1 and ?2 and t.send_id=?3",nativeQuery = true)
    public List<Object[]> getValidTransferCases(String startDate,String endDate,String classifierID);

    @Query( value= " " +
            "select count(tp.case_id) from aotc_transfer_process tp where tp.receive_time between ?1 and ?2 and tp.receive_id = ?3 " +
            " ", nativeQuery = true)
    int getSumTransferIn(String startDate, String endDate, String classifierID);
}
