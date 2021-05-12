package com.kfgs.aotc.sys.sysshortcutmenu.service;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.common.service.CommonServiceImpl;
import com.kfgs.aotc.sys.sysshortcutmenu.pojo.SysShortcutMenu;
import com.kfgs.aotc.sys.sysshortcutmenu.repository.SysShortcutMenuRepository;
import com.kfgs.aotc.sys.sysshortcutmenu.vo.SysShortcutMenuVo;
import com.kfgs.aotc.util.CopyUtil;
import com.kfgs.aotc.util.MenuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
@Transactional
public class SysShortcutMenuServiceImpl extends CommonServiceImpl<SysShortcutMenuVo, SysShortcutMenu, String> implements SysShortcutMenuService{

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SysShortcutMenuRepository sysShortcutMenuRepository;

    @Override
    public Result<String> delete(String id) {
        //先删除子节点
        SysShortcutMenuVo sysShortcutMenuVo = new SysShortcutMenuVo();
        sysShortcutMenuVo.setShortcutMenuParentId(id);
        super.list(sysShortcutMenuVo).getData().forEach((menuVo)->{
            super.delete(menuVo.getShortcutMenuId());
        });
        //再删除自己
        return super.delete(id);
    }

    @Override
    public Result<List<SysShortcutMenuVo>> findByUserId(String userId) {
        List<SysShortcutMenuVo> sysShortcutMenuVoList = CopyUtil.copyList(sysShortcutMenuRepository.findByUserId(userId), SysShortcutMenuVo.class);
        //mysql 这样写
        //return Result.of(MenuUtil.getChildBySysShortcutMenuVo("",sysShortcutMenuVoList));
        // oracle 这样写
        return Result.of(MenuUtil.getChildBySysShortcutMenuVo(null,sysShortcutMenuVoList));
    }
}
