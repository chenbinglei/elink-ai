package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.GatewaySubDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GatewaySubDeviceDao extends JpaRepository<GatewaySubDeviceEntity, String>, JpaSpecificationExecutor<GatewaySubDeviceEntity> {

    //根据网关id查询关联的子设备数据
    List<GatewaySubDeviceEntity> findAllByGatewayId(String gatewayId);

    //根据多个网关id查询关联的子设备数据
    List<GatewaySubDeviceEntity> findAllByGatewayIdIn(Set<String> gatewayId);

    //根据网关子设备id查询关联的网关数据
    Optional<GatewaySubDeviceEntity> findBySubDeviceId(String subDeviceId);

    //根据多个网关子设备id查询关联的网关数据
    List<GatewaySubDeviceEntity> findAllBySubDeviceIdIn(Collection<String> subDeviceId);

    //根据网关子设备id删除关联的数据
    void deleteAllByGatewayIdIn(Set<String> gatewayIds);

    //根据多个网关子设备id查询关联的网关数据
    List<GatewaySubDeviceEntity> findAllByGatewayIdAndSubDeviceIdIn(String gatewayId, Collection<String> subDeviceId);

}
