package com.kfgs.aotc.ceshi.classifier.service;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;

import java.util.List;

public interface ClassifierService {
    Result<List<ClassifierInfo>> getClassifiers();
}
