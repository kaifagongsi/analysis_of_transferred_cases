package com.kfgs.aotc.repository;

import com.kfgs.aotc.common.repository.CommonRepository;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassifierInfoRepository extends CommonRepository<ClassifierInfo, String> {

    @Query(value = "select distinct field_group from aotc_classifier_info order by field_group" , nativeQuery = true)
    public List<String> findDistinctByFieldGroup();

    @Query( value="select * from AOTC_CLASSIFIER_INFO t where dep1 = ?1 ",
            countQuery = " select count(classifiers_code) from AOTC_CLASSIFIER_INFO t where dep1 = ?1  ",
            nativeQuery = true)
    public Page<ClassifierInfo> findClassifiersCodeByDep1WithPageable(String dep1, Pageable pageable);

    @Query(value = "select * from AOTC_CLASSIFIER_INFO t where dep1 = ?1 and dep2 = ?2",
           countQuery = "select count (classifiers_code) from  AOTC_CLASSIFIER_INFO t where dep1 = ?1 and dep2 = ?2",
            nativeQuery = true)
    public Page<ClassifierInfo> findClassifiersCodeByDep1AndDep2(String dep1,String dep2,Pageable pageable);

    @Query( value="select * from AOTC_CLASSIFIER_INFO t where classifiers_code = ?1 ",
            countQuery = " select count(classifiers_code) from AOTC_CLASSIFIER_INFO t where classifiers_code = ?1  ",
            nativeQuery = true)
    Page<ClassifierInfo> findClassifiersCodeByClassifierCode(String secondClassify, Pageable pageable);

    @Query( value="select * from AOTC_CLASSIFIER_INFO t where dep1 = ?1 and dep2 = ?2 ",
            countQuery = " select count(classifiers_code) from AOTC_CLASSIFIER_INFO t where dep1 = ?1 and dep2 = ?2  ",
            nativeQuery = true)
    Page<ClassifierInfo> findClassifiersCodeByDep2WithPageable(String dep1, String dep2, Pageable pageable);

    @Query( value="select * from AOTC_CLASSIFIER_INFO t where field_group = ?1   ",
            countQuery = " select count(classifiers_code) from AOTC_CLASSIFIER_INFO where field_group = ?1   ",
            nativeQuery = true)
    Page<ClassifierInfo> findClassifiersCodeByFieldWithPageable(String fieldGroup, Pageable pageable);

    ClassifierInfo findClassifierInfoByClassifiersCode(String classifiersCode);

}
