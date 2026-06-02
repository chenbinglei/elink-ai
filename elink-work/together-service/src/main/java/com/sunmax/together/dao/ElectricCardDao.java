package com.sunmax.together.dao;

import com.sunmax.together.entity.ElectricCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ElectricCardDao extends JpaRepository<ElectricCardEntity, String>, JpaSpecificationExecutor<ElectricCardEntity> {

    boolean existsByPhysicalCard(String physicalCard);

    boolean existsByCardNumber(String cardNumber);

    //根据卡面号查询电卡数据
    List<ElectricCardEntity> findAllByCardNumber(String cardNumber);

    //根据物理卡号查询电卡数据
    List<ElectricCardEntity> findAllByPhysicalCard(String physicalCard);

}


