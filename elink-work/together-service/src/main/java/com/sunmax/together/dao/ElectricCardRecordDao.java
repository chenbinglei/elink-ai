package com.sunmax.together.dao;


import com.sunmax.together.entity.ElectricCardRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElectricCardRecordDao extends JpaRepository<ElectricCardRecordEntity, String > {

    List<ElectricCardRecordEntity> findByCarId(String carId);
}