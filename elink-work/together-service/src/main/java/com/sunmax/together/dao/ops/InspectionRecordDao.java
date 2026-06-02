package com.sunmax.together.dao.ops;

import com.sunmax.together.entity.ops.InspectionRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface InspectionRecordDao extends JpaRepository<InspectionRecordEntity, String>, JpaSpecificationExecutor<InspectionRecordEntity> {

    //根据巡检任务id查询巡检任务记录数据
    List<InspectionRecordEntity> findAllByTaskId(String taskId);

    //根据多个任务id删除巡检任务记录数据
    void deleteAllByTaskIdIn(Collection<String> taskIds);

}
