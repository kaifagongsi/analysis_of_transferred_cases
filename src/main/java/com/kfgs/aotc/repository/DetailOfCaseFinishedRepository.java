package com.kfgs.aotc.repository;

import com.kfgs.aotc.common.repository.CommonRepository;
import com.kfgs.aotc.pojo.business.DetailOfCaseFinished;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetailOfCaseFinishedRepository extends CommonRepository<DetailOfCaseFinished, String> {

    @Query(value = "select t.case_id as id,t.receive_id as classifiersCode,d.ipcmi as ipcmi,d.ipcoi as ipcoi,d.ipca as ipca,c.classification_field as classificationField"+
            " from (aotc_transfer_process t inner join aotc_detailsofthecase d on t.case_id=d.case_id and t.tips_state='拒绝转案' and t.send_id=?1) inner join"+
            " aotc_classifier_info c on t.receive_id=c.classifiers_code",nativeQuery = true)
    public List<Object[]> getValidTransferCases(String classifierID);
}
