package com.kfgs.aotc.sys.sysmenu.repository;

import com.kfgs.aotc.common.repository.CommonRepository;
import com.kfgs.aotc.sys.sysmenu.pojo.SysMenu;
import org.springframework.stereotype.Repository;

@Repository
public interface SysMenuRepository extends CommonRepository<SysMenu, String> {
}
