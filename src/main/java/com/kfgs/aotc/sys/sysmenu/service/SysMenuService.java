package com.kfgs.aotc.sys.sysmenu.service;


import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.common.service.CommonService;
import com.kfgs.aotc.sys.sysmenu.pojo.SysMenu;
import com.kfgs.aotc.sys.sysmenu.vo.SysMenuVo;

import java.util.List;

public interface SysMenuService extends CommonService<SysMenuVo, SysMenu, String> {
    Result<List<SysMenuVo>> listByTier(SysMenuVo entityVo);
}
