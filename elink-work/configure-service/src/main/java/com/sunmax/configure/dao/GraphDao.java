package com.sunmax.configure.dao;

import com.sunmax.common.dao.base.BaseDao;
import com.sunmax.configure.entity.GraphEntity;

import java.util.List;

public interface GraphDao extends BaseDao<GraphEntity, String> {

    //根据域名id查询图模数据
    List<GraphEntity> findAllByDomainId(String domainId);

}
