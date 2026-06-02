package com.sunmax.configure.dao;

import com.sunmax.common.dao.base.BaseDao;
import com.sunmax.configure.entity.GraphSourceEntity;

import java.util.List;

public interface GraphSourceDao extends BaseDao<GraphSourceEntity, String> {

    //根据多个图模id查询图模数据源数据
    List<GraphSourceEntity> findAllByGraphIdIn(List<String> graphIds);
}
