package com.kfgs.aotc.ceshi.classifier.service;

import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.util.CopyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ClassifierServiceImpl implements ClassifierService {

    @Autowired
    private ClassifierInfoRepository classifierInfoRepository;

    @Override
    public Result<List<ClassifierInfo>> getClassifiers() {
        return Result.of(CopyUtil.copyList(classifierInfoRepository.findAll(),ClassifierInfo.class));
    }
}
