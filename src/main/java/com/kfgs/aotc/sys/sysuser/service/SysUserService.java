package com.kfgs.aotc.sys.sysuser.service;


import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.common.service.CommonService;
import com.kfgs.aotc.pojo.sys.SysUser;
import com.kfgs.aotc.sys.sysuser.vo.SysUserVo;

public interface SysUserService extends CommonService<SysUserVo, SysUser, String> {
    Result<SysUserVo> findByLoginName(String username);
    Result<SysUserVo> resetPassword(String userId);
}
