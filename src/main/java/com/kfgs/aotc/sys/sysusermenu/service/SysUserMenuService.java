package com.kfgs.aotc.sys.sysusermenu.service;


import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.common.service.CommonService;
import com.kfgs.aotc.sys.sysmenu.vo.SysMenuVo;
import com.kfgs.aotc.sys.sysusermenu.pojo.SysUserMenu;
import com.kfgs.aotc.sys.sysusermenu.vo.SysUserMenuVo;

import java.util.List;

public interface SysUserMenuService extends CommonService<SysUserMenuVo, SysUserMenu, String> {
    Result<List<SysMenuVo>> findByUserId(String userId);

    Result<Boolean> saveAllByUserId(String userId, String menuIdList);
}
