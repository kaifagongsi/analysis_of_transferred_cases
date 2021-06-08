package com.kfgs.aotc.repository;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import com.kfgs.aotc.annotation.In;
import com.kfgs.aotc.common.repository.CommonRepository;
import com.kfgs.aotc.pojo.business.TransferProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * in 和 exists的区别: 如果子查询得出的结果集记录较少，主查询中的表较大且又有索引时应该用in, 反之如果外层的主查询记录较少，子查询中的表大，又有索引时使用exists。
 */
@Repository
public interface TransferProcessRepository extends CommonRepository<TransferProcess, String> {

    @Query(value = "select count (case_id) from aotc_transfer_process where send_id=?1 and tips_state='接收转案'", nativeQuery = true)
    public int getSumOfReceiveBySendID(String classifierID);

    @Query(value = "select count(t.case_id) from aotc_transfer_process t inner join aotc_detailsofthecase d on t.case_id=d.case_id and t.tips_state='拒绝转案' and send_time between ?1 and ?2 and t.send_id=?3",nativeQuery = true)
    public int getSumOFRejectBySendID(String startDate,String endDate,String classifierID);

    @Query(value = "select  * from aotc_transfer_process where receive_time between ?1 and ?2 and tips_state = ?3 and receive_id = ?4 ",nativeQuery = true)
    public List<TransferProcess> getAllConditionsByReceiveTimeAndTipeTitleAndReceiveId( String startDate,  String endDate,String tips_title,String receive_id );


    @Query(value = "select  count(CASE_ID) from aotc_transfer_process where receive_time between ?1 and ?2 and tips_state = ?3 and receive_id = ?4 ",nativeQuery = true)
    public Integer getAcceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveId( String startDate,  String endDate,String tips_title,String receive_id );

    @Query(value = "select  * from aotc_transfer_process where receive_time between ?1 and ?2 and tips_state = ?3 ",nativeQuery = true)
    public Page<TransferProcess> getAllConditionsByReceiveTimeAndTipeTitle(String startDate, String endDate, String tips_state, Pageable pageable);

    @Query(value= "select count(CASE_ID) from aotc_transfer_process where receive_time between ?1 and ?2",nativeQuery = true)
    public Integer getCountNumberByReceiveTimeBetween(String startDate,String endDate);

    @Query(value= "select count(CASE_ID) from aotc_transfer_process where receive_time between ?1 and ?2 and receive_id = ?3",nativeQuery = true)
    public Integer getAcceptReferralCountNumberByReceiveTimeBetweenAndReceiveId(String startDate,String endDate,String receiveId);


    @Query(value = " select ipcmi || ',' || ipcoi || ',' || ipca " +
            "    from aotc_detailsofthecase dotc" +
            "   where EXISTS (select case_id " +
            "            from aotc_transfer_process tp " +
            "           where receive_id = ?4 " +
            "             and receive_time between  ?1 and ?2 " +
            "             and tips_state =  ?3 " +
            "             and dotc.case_id = tp.case_id)", nativeQuery = true)
    List<String> getRefuseReferralByReceiveTimeBetweenAndTipeTitleAndReceiveId(String startDate, String endDate, String tips_state, String secondClassify);

    @Query( nativeQuery = true,value =  "select count(tp.case_id)  from aotc_transfer_process tp where tp.receive_time between ?1 and ?2 " +
            "and receive_id in ( ?3 ) " )
    int getSumOfTheDateAndReceiveIdList(String startDate, String endDate, List<String> codeInfo);

    @Query(nativeQuery = true, value = " select count(tp.case_id) from aotc_transfer_process tp where tp.receive_time between ?1 and ?2 " +
            "and tp.tips_state = ?3 " +
            "and tp.receive_id in (?4) " )
    int getAcceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveIds(String startDate, String endDate, String tipsState, List classifierInfoCode);

    @Query(nativeQuery = true, value = " select count(tp.case_id) from aotc_transfer_process tp where tp.send_time between ?1 and ?2 " +
            "and tp.tips_state = ?3 " +
            "and tp.send_id in (?4) " )
    int getAcceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds(String startDate,String endDate,String tipsState,List classifierInfoCode);


    @Query(value = " select ipcmi || ',' || ipcoi || ',' || ipca " +
            "    from aotc_detailsofthecase dotc" +
            "   where EXISTS (select case_id " +
            "            from aotc_transfer_process tp " +
            "           where send_id = ?4 " +
            "             and send_time between  ?1 and ?2 " +
            "             and tips_state =  ?3 " +
            "             and dotc.case_id = tp.case_id)", nativeQuery = true)
    List<String> getRefuseReferralBySendTimeBetweenAndTipeTitleAndSendId(String startDate,String endDate,String tips_state,String secondClassify);
    //public List<TransferProcess> findAllConditionsByReceiveTimeAndTipeTitleAAndReceiveId(String startDate,String endDate,String tips_state,String receiveId);

}
