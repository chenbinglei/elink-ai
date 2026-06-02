package com.sunmax.configure.dao;

import com.sunmax.common.dao.base.BaseDao;
import com.sunmax.configure.entity.VariableEntity;

import java.util.Collection;
import java.util.List;

public interface VariableDao extends BaseDao<VariableEntity, String> {

    //根据多个图模id查询图模变量数据
    List<VariableEntity> findAllByGraphIdIn(List<String> graphIds);

    //根据多个图模关联数据源id查询图模变量数据
    List<VariableEntity> findAllByGraphSourceIdIn(Collection<String> graphSourceId);

    //根据图模id删除图模变量数据
    void deleteAllByGraphId(String graphId);

}
