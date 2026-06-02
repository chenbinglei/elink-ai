package com.sunmax.together.dao.ops;

import com.sunmax.together.entity.ops.InspectionTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface InspectionTaskDao extends JpaRepository<InspectionTaskEntity, String>, JpaSpecificationExecutor<InspectionTaskEntity> {

    //根据多个主键id删除巡检任务数据
    void deleteAllByIdIn(Collection<String> ids);

    //根据任务状态查询巡检任务数据
    List<InspectionTaskEntity> findAllByTaskStatusNot(Integer taskStatus);

}
