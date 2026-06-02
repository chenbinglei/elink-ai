package com.sunmax.auth.dao;

import com.sunmax.auth.entity.ProductEntity;
import com.sunmax.common.dao.base.BaseDao;

import java.util.List;

public interface ProductDao extends BaseDao<ProductEntity, String> {

    //根据父节点id删除产品数据
    void deleteAllByParentIdEquals(String parentId);

    /**
     * 查询指定客户端下所有菜单数据
     * @param parentId
     * @param isDelete
     * @return
     */
    List<ProductEntity> findAllByParentIdAndIsDelete(String parentId, int isDelete);

    //根据客户端id和是否删除查询菜单数据
    ProductEntity findByClientIdAndIsDelete(String clientId, int isDelete);
}
