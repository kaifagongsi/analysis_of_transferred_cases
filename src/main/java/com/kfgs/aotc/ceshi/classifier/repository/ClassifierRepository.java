package com.kfgs.aotc.ceshi.classifier.repository;

import com.kfgs.aotc.common.repository.CommonRepository;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassifierRepository extends CommonRepository<ClassifierInfo, String> {
    List<ClassifierInfo> findAll();
}
