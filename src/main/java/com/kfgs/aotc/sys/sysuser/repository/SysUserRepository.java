package com.kfgs.aotc.sys.sysuser.repository;

import com.kfgs.aotc.common.repository.CommonRepository;
import com.kfgs.aotc.sys.sysuser.pojo.SysUser;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserRepository extends CommonRepository<SysUser, String> {
    SysUser findByLoginName(String username);
}
