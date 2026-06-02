package com.sunmax.crontab.dao;

import com.sunmax.crontab.entity.MqttClientLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MqttClientLogDao extends JpaRepository<MqttClientLogEntity, String>, JpaSpecificationExecutor<MqttClientLogEntity> {

}
