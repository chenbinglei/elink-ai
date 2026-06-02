package com.sunmax.together.dao;

import com.sunmax.together.entity.ElectricCardBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ElectricCardBalanceDao extends JpaRepository<ElectricCardBalanceEntity, String> , JpaSpecificationExecutor<ElectricCardBalanceEntity> {

}




