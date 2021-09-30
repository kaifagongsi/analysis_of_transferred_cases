package com.kfgs.aotc.repository;

import com.kfgs.aotc.common.repository.CommonRepository;
import com.kfgs.aotc.pojo.business.TransferProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     *  根据tips_state 来获取该状态下总共有多少案件数量
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param tips_state 接收案件或者拒绝案件
     * @param receive_id 接收人
     * @return
     */
    @Query(value = "select  count(CASE_ID) from aotc_transfer_process where receive_time between ?1 and ?2 and tips_state = ?3 and receive_id = ?4 ",nativeQuery = true)
    public Integer getAcceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveId( String startDate,  String endDate,String tips_state,String receive_id );

    @Query(value = "select  * from aotc_transfer_process where receive_time between ?1 and ?2 and tips_state = ?3 ",nativeQuery = true)
    public Page<TransferProcess> getAllConditionsByReceiveTimeAndTipeTitle(String startDate, String endDate, String tips_state, Pageable pageable);

    /**
     * 根据传入的开始时间和结束时间开统计aotc_transfer_process 表中转案次数
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    @Query(value= "select count(CASE_ID) from aotc_transfer_process where receive_time between ?1 and ?2",nativeQuery = true)
    public Integer getCountNumberByReceiveTimeBetween(String startDate,String endDate);

    /**
     * 根据传入的开始时间、结束时间、转案人员 统计aotc_transfer_process 表中转案次数
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param receiveId 接收转案人员ID
     * @return
     */
    @Query(value= "select count(CASE_ID) from aotc_transfer_process where receive_time between ?1 and ?2 and receive_id = ?3",nativeQuery = true)
    public Integer getCountNumberByReceiveTimeBetweenAndReceiveId(String startDate,String endDate,String receiveId);

    @Query( value= "select count(tp.case_id) from aotc_transfer_process tp where tp.receive_time between ?1 and ?2 and tp.receive_id = ?3 " +
            " ", nativeQuery = true)
    public Integer getSumTransferIn(String startDate, String endDate, String classifierID);

    @Query(value= "select count(CASE_ID) from aotc_transfer_process where send_time between ?1 and ?2 and send_id = ?3 and tips_state='接收转案'",nativeQuery = true)
    public Integer getAcceptReferralCountNumberBySendTimeBetweenAndSendId(String startDate,String endDate,String sendId);

    //某个时间段转出案件数
    @Query(value = "select count(CASE_ID) from aotc_transfer_process where send_time between ?1 and ?2 and send_id = ?3",nativeQuery = true)
    public Integer getCountNumberBySendTimeBetweenAndSendId(String startDate,String endDate,String sendId);

    @Query(value = " select ipcmi || ',' || ipcoi || ',' || ipca " +
            "    from aotc_detailsofthecase dotc" +
            "   where EXISTS (select case_id " +
            "            from aotc_transfer_process tp " +
            "           where receive_id = ?4 " +
            "             and receive_time between  ?1 and ?2 " +
            "             and tips_state =  ?3 " +
            "             and dotc.case_id = tp.case_id)", nativeQuery = true)
    List<String> getRefuseReferralByReceiveTimeBetweenAndTipeTitleAndReceiveId(String startDate, String endDate, String tips_state, String secondClassify);

    /**
     * 在某一个接收时间段内，某些人的转入总次数
     * @param startDate 接收的开始时间
     * @param endDate 接收的结束时间
     * @param codeInfo 分类员代码
     * @return
     */
    @Query( nativeQuery = true,value =  "select count(tp.case_id)  from aotc_transfer_process tp where tp.receive_time between ?1 and ?2 " +
            "and receive_id in ( ?3 ) " )
    int getSumOfTheDateAndReceiveIdList(String startDate, String endDate, List<String> codeInfo);


    @Query( nativeQuery = true,value = "select count(tp.case_id) from aotc_transfer_process tp where tp.send_time between ?1 and ?2 " +
            "and send_id in ( ?3 ) ")
    int getSumOfTheDateAndSendIdList(String startDate,String endDate,List<String> codeInfo);
    /**
     *  根据tips_state 来获取该状态下总共有多少案件数量
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param tipsState 接收案件或者拒绝案件
     * @param classifierInfoCode 接收人的List集合
     * @return
     */
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

    @Query(nativeQuery = true, value = " select count(tp.case_id) from aotc_transfer_process tp where tp.receive_time between ?1 and ?2 " +
            "and tp.tips_state = ?3 " +
            "and tp.receive_id in (?4) " )
    int getAcceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndReciveIds(String startDate, String endDate, String tips_state, List<String> classifierInfoCode);

    @Query(nativeQuery = true, value = " select count(tp.case_id) from aotc_transfer_process tp where tp.receive_time between ?1 and ?2 " +
            "and tp.receive_id in (?3) " )
    int getAcceptReferralCountNumberBySendTimeBetweenAndReciveIds(String startDate, String endDate, List<String> asList);

    //某天某分类员转出案件数
    @Query(value = "select count(CASE_ID) from aotc_transfer_process where send_time = ?1 and send_id = ?2",nativeQuery = true)
    public Integer getDayCountNumberBySendTimeAndSendId(String sendtime,String sendId);

    //某周某分类员转出案件数
    @Query(value = "select count(CASE_ID) from aotc_transfer_process where send_time between ?1 and ?2 and send_id = ?3",nativeQuery = true)
    Integer getWeekCountNumberBySendTimeAndSendId(String Monday,String Sunday,String sendId);

    //某月某分类员转出案件数
    @Query(value = "select count(CASE_ID) from aotc_transfer_process where send_time between ?1 and ?2 and send_id = ?3",nativeQuery = true)
    Integer getMonthCountNumberBySendTimeAndSendId(String startDay,String lastDay,String sendId);

    //某天某部门计算转出案件数
    @Query(value = "select count(tp.case_id) from aotc_transfer_process tp,aotc_classifier_info ci where tp.send_time =?1 and tp.send_id=ci.classifiers_code and ci.dep1 = ?2",nativeQuery = true)
    Integer getCountNumberBySendTimeAndDep1(String date,String dep1);

    //某周某部门转出案件数
    @Query(value = "select count(tp.case_id) from aotc_transfer_process tp,aotc_classifier_info ci where tp.send_time between ?1 and ?2 and tp.send_id=ci.classifiers_code and ci.dep1 = ?3",nativeQuery = true)
    Integer getWeekCountNumberBySendTimeAndDep1(String Monday,String Sunday,String dep1);

    //某月某部门转出案件数
    @Query(value = "select count(tp.case_id) from aotc_transfer_process tp,aotc_classifier_info ci where tp.send_time between ?1 and ?2 and tp.send_id=ci.classifiers_code and ci.dep1 = ?3",nativeQuery = true)
    Integer getMonthCountNumberBySendTimeAndDep1(String startDay,String lastDay,String dep1);

    //某天某科室计算转出案件数
    @Query(value = "select count(tp.case_id) from aotc_transfer_process tp,aotc_classifier_info ci where tp.send_time =?1 and tp.send_id=ci.classifiers_code and ci.dep1 = ?2 and ci.dep2 = ?3",nativeQuery = true)
    Integer getCountNumberBySendTimeAndDep2(String date,String dep1,String dep2);

    //某周某科室计算转出案件数
    @Query(value = "select count(tp.case_id) from aotc_transfer_process tp,aotc_classifier_info ci where tp.send_time between ?1 and ?2 and tp.send_id=ci.classifiers_code and ci.dep1 = ?3 and ci.dep2 = ?4",nativeQuery = true)
    Integer getWeekCountNumberBySendTimeAndDep2(String Monday,String Sunday,String dep1,String dep2);

    //某月某科室计算转出案件数
    @Query(value = "select count(tp.case_id) from aotc_transfer_process tp,aotc_classifier_info ci where tp.send_time between ?1 and ?2 and tp.send_id=ci.classifiers_code and ci.dep1 = ?3 and ci.dep2 = ?4",nativeQuery = true)
    Integer getMonthCountNumberBySendTimeAndDep2(String startDay,String lastDay,String dep1,String dep2);

    //某天某领域计算转出案件数
    @Query(value = "select count(tp.case_id) from aotc_transfer_process tp,aotc_classifier_info ci where tp.send_time =?1 and tp.send_id=ci.classifiers_code and ci.field_group = ?2",nativeQuery = true)
    Integer getCountNumberBySendTimeAndFiled(String date,String filed);

    //某周某领域计算转出案件数
    @Query(value = "select count(tp.case_id) from aotc_transfer_process tp,aotc_classifier_info ci where tp.send_time between ?1 and ?2 and tp.send_id=ci.classifiers_code and ci.field_group = ?3",nativeQuery = true)
    Integer getWeekCountNumberBySendTimeAndFiled(String Monday,String Sunday,String filed);

    //某月某领域计算转出案件数
    @Query(value = "select count(tp.case_id) from aotc_transfer_process tp,aotc_classifier_info ci where tp.send_time between ?1 and ?2 and tp.send_id=ci.classifiers_code and ci.field_group = ?3",nativeQuery = true)
    Integer getMonthCountNumberBySendTimeAndFiled(String startDay,String lastDay,String filed);


    /**
     * 在某天，分类员的转入总次数
     * @param day  接收时间
     * @param classifiersCode  分类员代码
     * @return
     */
    @Query(nativeQuery = true,value=" select  count(1) from aotc_transfer_process where receive_time = ?1  and  receive_id = ?2" )
    int getSumOfTheDateAndReceiveIdEqualsAndReceiveTimeEquals(String day, String classifiersCode);


    @Query(nativeQuery = true,value=" select  count(1) from aotc_transfer_process where receive_time = ?1  and  receive_id in (?2) " )
    int getSumOfTheDateAndReceiveIdListAndReceiveTimeEquals(String day, List classifiers);

    @Query(nativeQuery = true,value=" select   MAX(RECEIVE_TIME)  from aotc_transfer_process  " )
    String getMaxTransferProcess();

    @Modifying
    @Transactional
    @Query(nativeQuery = true,value="delete from aotc_transfer_process where send_name in (?1)" )
    void deleteByNeedToExclude(List sendNameNeedToExclude);
}
