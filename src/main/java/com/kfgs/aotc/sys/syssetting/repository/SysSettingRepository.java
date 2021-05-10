package com.kfgs.aotc.sys.syssetting.repository;

import com.kfgs.aotc.common.repository.CommonRepository;
import com.kfgs.aotc.sys.syssetting.pojo.SysSetting;
import org.springframework.stereotype.Repository;

@Repository
public interface SysSettingRepository extends CommonRepository<SysSetting, String> {
}
