package com.kfgs.aotc.sys.sysuserauthority.service;


import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.common.service.CommonService;
import com.kfgs.aotc.sys.sysuserauthority.pojo.SysUserAuthority;
import com.kfgs.aotc.sys.sysuserauthority.vo.SysUserAuthorityVo;

import java.util.List;

public interface SysUserAuthorityService extends CommonService<SysUserAuthorityVo, SysUserAuthority, String> {
    Result<List<SysUserAuthorityVo>> findByUserId(String userId);

    Result<Boolean> saveAllByUserId(String userId, String authorityIdList);
}
