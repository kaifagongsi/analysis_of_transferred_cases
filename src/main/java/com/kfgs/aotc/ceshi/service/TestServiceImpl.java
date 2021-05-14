package com.kfgs.aotc.ceshi.service;

import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TestServiceImpl implements TestService {

    @Autowired
    private ClassifierInfoRepository classifierInfoRepository;

    @Override
    public List<ClassifierInfo> getAllUser() {
        return classifierInfoRepository.findAll();
    }

    @Override
    public List<String> getFieldGroup() {
        return classifierInfoRepository.findDistinctByFieldGroup();
    }
}
