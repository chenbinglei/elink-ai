package com.sunmax.together.dao;

import com.sunmax.together.entity.GroupBeSiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GroupBeSiteDao extends JpaRepository<GroupBeSiteEntity, String>, JpaSpecificationExecutor<GroupBeSiteEntity> {

    //根据分组id删除所有关联站点数据
    void deleteAllByGroupId(String groupId);

    //根据多个分组id，查询关联站点信息
    List<GroupBeSiteEntity> findAllByGroupIdIn(List<String> groupIdList);

    //根据站点id删除所有关联的数据
    void deleteAllBySiteId(String siteId);
}
