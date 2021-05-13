package com.kfgs.aotc.sys.sysshortcutmenu.controller;

import com.kfgs.aotc.common.controller.CommonController;
import com.kfgs.aotc.pojo.sys.SysShortcutMenu;
import com.kfgs.aotc.sys.sysshortcutmenu.service.SysShortcutMenuService;
import com.kfgs.aotc.sys.sysshortcutmenu.vo.SysShortcutMenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/sysShortcutMenu/")
public class SysShortcutMenuController extends CommonController<SysShortcutMenuVo, SysShortcutMenu, String> {
    @Autowired
    private SysShortcutMenuService sysShortcutMenuService;
}
