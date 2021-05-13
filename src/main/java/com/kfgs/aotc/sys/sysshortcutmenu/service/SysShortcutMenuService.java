package com.kfgs.aotc.sys.sysshortcutmenu.service;


import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.common.service.CommonService;
import com.kfgs.aotc.pojo.sys.SysShortcutMenu;
import com.kfgs.aotc.sys.sysshortcutmenu.vo.SysShortcutMenuVo;

import java.util.List;

public interface SysShortcutMenuService extends CommonService<SysShortcutMenuVo, SysShortcutMenu, String> {
    Result<List<SysShortcutMenuVo>> findByUserId(String userId);
}
