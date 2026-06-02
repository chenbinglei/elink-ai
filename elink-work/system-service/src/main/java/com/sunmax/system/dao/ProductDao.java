package com.sunmax.system.dao;

import com.sunmax.system.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductDao extends JpaRepository<ProductEntity, String>, JpaSpecificationExecutor<ProductEntity> {

    //根据父节点id删除产品数据
    void deleteAllByParentIdEquals(String parentId);

}
