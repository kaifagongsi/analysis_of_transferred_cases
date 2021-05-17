package com.kfgs.aotc.repository;

import com.kfgs.aotc.common.repository.CommonRepository;
import com.kfgs.aotc.pojo.business.TransferProcess;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferProcessRepository extends CommonRepository<TransferProcess, String> {

    @Query(value = "select count (case_id) from aotc_transfer_process where send_id=?1 and tips_state='接收转案'", nativeQuery = true)
    public int getSumOfReceiveBySendID(String classifierID);

    @Query(value = "select count(t.case_id) from aotc_transfer_process t inner join aotc_detailsofthecase d on t.case_id=d.case_id and t.tips_state='拒绝转案' and t.send_id=?1",nativeQuery = true)
    public int getSumOFRejectBySendID(String classifierID);



}
