package com.kfgs.aotc.sys.syssetting.service;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.common.service.CommonServiceImpl;
import com.kfgs.aotc.sys.syssetting.pojo.SysSetting;
import com.kfgs.aotc.sys.syssetting.repository.SysSettingRepository;
import com.kfgs.aotc.sys.syssetting.vo.SysSettingVo;
import com.kfgs.aotc.util.SysSettingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
@Transactional
public class SysSettingServiceImpl extends CommonServiceImpl<SysSettingVo, SysSetting, String> implements SysSettingService{

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SysSettingRepository sysSettingRepository;

    @Override
    public Result<SysSettingVo> save(SysSettingVo entityVo) {
        //调用父类
        Result<SysSettingVo> result = super.save(entityVo);

        //更新系统设置时同步更新公用静态集合sysSettingMap
        SysSettingUtil.setSysSettingMap(result.getData());

        return result;
    }
}
