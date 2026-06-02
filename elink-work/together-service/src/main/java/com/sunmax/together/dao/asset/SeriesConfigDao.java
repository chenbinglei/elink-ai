package com.sunmax.together.dao.asset;
import com.sunmax.together.entity.assets.SeriesConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SeriesConfigDao extends JpaRepository<SeriesConfigEntity, String>, JpaSpecificationExecutor<SeriesConfigEntity> {
    List<SeriesConfigEntity> findAllByDeviceId(String deviceId);

    List<SeriesConfigEntity> findAllByDeviceIdIn(List<String> deviceIdList);
}
