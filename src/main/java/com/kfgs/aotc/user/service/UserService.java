package com.kfgs.aotc.user.service;


import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.sys.sysuser.vo.SysUserVo;

public interface UserService {
    Result<SysUserVo> updatePassword(String oldPassword, String newPassword);

    Result<SysUserVo> updateUser(SysUserVo sysUserVo);
}
