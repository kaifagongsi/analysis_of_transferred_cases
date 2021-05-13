package com.kfgs.aotc.ceshi.classifier.service;

import com.kfgs.aotc.ceshi.classifier.repository.ClassifierRepository;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.util.CopyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ClassifierServiceImpl implements ClassifierService {

    @Autowired
    private ClassifierRepository classifierRepository;

    @Override
    public Result<List<ClassifierInfo>> getClassifiers() {
        return Result.of(CopyUtil.copyList(classifierRepository.findAll(),ClassifierInfo.class));
    }
}
