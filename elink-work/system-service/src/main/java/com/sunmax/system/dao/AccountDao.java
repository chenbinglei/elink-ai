package com.sunmax.system.dao;

import com.sunmax.system.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AccountDao extends JpaRepository<AccountEntity, String>, JpaSpecificationExecutor<AccountEntity> {

    //根据商户id和平台类型查询账户信息
    List<AccountEntity> findAllByMchIdAndPlatformTypeAndIsDelete(String mchId, Integer platformType, Integer isDelete);

    //根据租户id查询账户信息
    List<AccountEntity> findAllByTenantIdAndIsDelete(String tenantId, Integer isDelete);

    //根据租户id和平台类型查询账户信息
    List<AccountEntity> findAllByTenantIdAndPlatformTypeAndIsDelete(String tenantId, Integer platformType, Integer isDelete);

    //根据平台类型和商户证书序列号查询账户信息
    List<AccountEntity> findByPlatformTypeAndRsaSerialNoAndIsDelete(Integer platformType, String rsaSerialNo, Integer isDelete);

}
