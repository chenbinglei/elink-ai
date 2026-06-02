package com.sunmax.together.dao.ops;

import com.sunmax.together.entity.ops.InspectionSiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface InspectionSiteDao extends JpaRepository<InspectionSiteEntity, String>, JpaSpecificationExecutor<InspectionSiteEntity> {

    //根据多个任务id查询巡检任务下面的站点数据
    List<InspectionSiteEntity> findAllByTaskIdIn(Collection<String> taskIds);

    //根据多个站点id查询巡检任务下面的站点数据
    List<InspectionSiteEntity> findAllBySiteIdInAndFinishTimeNotNull(Collection<String> siteIds);

    //根据多个任务id删除巡检任务下面的站点数据
    void deleteAllByTaskIdIn(Collection<String> taskIds);

}
