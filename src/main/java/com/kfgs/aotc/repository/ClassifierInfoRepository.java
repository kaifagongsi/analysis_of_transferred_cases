package com.kfgs.aotc.repository;

import com.kfgs.aotc.common.repository.CommonRepository;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassifierInfoRepository extends CommonRepository<ClassifierInfo, String> {

    @Query(value = "select distinct field_group from aotc_classifier_info" , nativeQuery = true)
    public List<String> findDistinctByFieldGroup();
}
