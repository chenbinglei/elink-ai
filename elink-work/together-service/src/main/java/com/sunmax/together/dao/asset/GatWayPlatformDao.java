package com.sunmax.together.dao.asset;

import com.sunmax.together.entity.assets.GatWayPlatformEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GatWayPlatformDao extends JpaRepository<GatWayPlatformEntity, String>, JpaSpecificationExecutor<GatWayPlatformEntity> {

    //根据网关id删除所有关联平台信息
    void deleteAllByGatewayId(String gatWayId);

    //根据网关id查询所关联所有平台id
    List<GatWayPlatformEntity> findAllByGatewayId(String gatWayId);

    /**
     * 根据平台id查询和网关关联关系
     * @param platformId
     * @return
     */
    List<GatWayPlatformEntity> findAllByPlatformId(String platformId);

    /**
     * 根据多个网关id查询关联平台信息
     * @param gatewayIdList
     * @return
     */
    List<GatWayPlatformEntity> findAllByGatewayIdIn(List<String> gatewayIdList);

    /**
     * 根据平台id删除和所有网关关联关系
     * @param platformId
     */
    void deleteAllByPlatformId(String platformId);
}
