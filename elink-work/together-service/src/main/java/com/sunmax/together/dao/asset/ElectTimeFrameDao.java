package com.sunmax.together.dao.asset;

import com.sunmax.together.entity.assets.ElectTimeFrameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface ElectTimeFrameDao extends JpaRepository<ElectTimeFrameEntity, String>, JpaSpecificationExecutor<ElectTimeFrameEntity> {

    //根据多个电价配置id查询电价时段信息数据
    List<ElectTimeFrameEntity> findAllByElectConfigIdIn(Collection<String> electConfigIds);

}
