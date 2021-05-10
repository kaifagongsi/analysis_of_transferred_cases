package com.kfgs.aotc.sys.sysshortcutmenu.repository;

import com.kfgs.aotc.common.repository.CommonRepository;
import com.kfgs.aotc.sys.sysshortcutmenu.pojo.SysShortcutMenu;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysShortcutMenuRepository extends CommonRepository<SysShortcutMenu, String> {
    List<SysShortcutMenu> findByUserId(String userId);
}
