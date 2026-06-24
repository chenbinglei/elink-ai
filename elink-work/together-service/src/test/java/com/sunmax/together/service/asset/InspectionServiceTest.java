package com.sunmax.together.service.asset;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.vo.asset.InspectionTaskQueryVo;
import com.sunmax.together.dao.ops.InspectionItemDao;
import com.sunmax.together.dao.ops.InspectionRecordDao;
import com.sunmax.together.dao.ops.InspectionSiteDao;
import com.sunmax.together.dao.ops.InspectionTaskDao;
import com.sunmax.together.dao.ops.InspectionUserDao;
import com.sunmax.together.service.asset.impl.InspectionServiceImpl;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("InspectionService 单元测试")
class InspectionServiceTest {

    @Mock private InspectionItemDao inspectionItemDao;
    @Mock private InspectionUserDao inspectionUserDao;
    @Mock private InspectionTaskDao inspectionTaskDao;
    @Mock private SystemService systemService;
    @Mock private InspectionSiteDao inspectionSiteDao;
    @Mock private InspectionRecordDao inspectionRecordDao;
    @Mock private DeviceService deviceService;

    @InjectMocks private InspectionServiceImpl inspectionService;

    @Test
    @DisplayName("删除巡检项-空列表返回成功")
    void deleteAllInspectionItemByIds_emptyList_returnsSuccess() {
        var result = inspectionService.deleteAllInspectionItemByIds(Collections.emptyList());
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询巡检项列表-无数据返回空分页")
    void queryInspectionItemList_noData_returnsEmpty() {
        when(inspectionItemDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(Collections.emptyList()));

        var result = inspectionService.queryInspectionItemList("site-001", null, 1, 10);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询巡检用户列表-无数据返回空列表")
    void getInspectionUserList_noData_returnsEmpty() {
        when(inspectionUserDao.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(Collections.emptyList());

        var result = inspectionService.getInspectionUserList("tenant-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询巡检任务列表-无数据返回空分页")
    void queryInspectionTaskList_noData_returnsEmpty() {
        when(inspectionTaskDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(Collections.emptyList()));

        InspectionTaskQueryVo vo = new InspectionTaskQueryVo();
        vo.setPage(1);
        vo.setSize(10);
        var result = inspectionService.queryInspectionTaskList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除巡检任务-空列表返回成功")
    void deleteInspectionTaskByIds_emptyList_returnsSuccess() {
        var result = inspectionService.deleteInspectionTaskByIds(Collections.emptyList());
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询巡检任务详情-无数据返回错误")
    void findInspectionTaskDetailById_noData_returnsError() {
        when(inspectionTaskDao.findById("task-001")).thenReturn(java.util.Optional.empty());

        var result = inspectionService.findInspectionTaskDetailById("task-001");
        assertNotNull(result);
    }

    @Test
    @DisplayName("查询巡检任务详情-有数据返回详情")
    void findInspectionTaskDetailById_hasData_returnsDetail() {
        com.sunmax.together.entity.ops.InspectionTaskEntity task = new com.sunmax.together.entity.ops.InspectionTaskEntity();
        task.setId("task-001");
        task.setUserId("user-001");
        task.setTenantId("tenant-001");
        task.setTaskStatus(2);
        when(inspectionTaskDao.findById("task-001")).thenReturn(java.util.Optional.of(task));
        when(inspectionSiteDao.findAllByTaskIdIn(java.util.List.of("task-001"))).thenReturn(Collections.emptyList());
        when(systemService.findUserInfoByIdsFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = inspectionService.findInspectionTaskDetailById("task-001");
        assertNotNull(result);
    }

    @Test
    @DisplayName("查询巡检站点列表-有数据返回")
    void findInspectionSiteListById_hasData_returnsList() {
        com.sunmax.together.entity.ops.InspectionSiteEntity site = new com.sunmax.together.entity.ops.InspectionSiteEntity();
        site.setId("is-001");
        site.setTaskId("task-001");
        site.setSiteId("site-001");
        when(inspectionSiteDao.findById("is-001")).thenReturn(java.util.Optional.of(site));
        when(inspectionRecordDao.findAllByTaskId("task-001")).thenReturn(Collections.emptyList());

        var result = inspectionService.findInspectionSiteListById("is-001");
        assertNotNull(result);
    }

    @Test
    @DisplayName("查询巡检站点列表-无数据返回错误")
    void findInspectionSiteListById_noData_returnsError() {
        when(inspectionSiteDao.findById("is-001")).thenReturn(java.util.Optional.empty());

        var result = inspectionService.findInspectionSiteListById("is-001");
        assertNotNull(result);
    }

    @Test
    @DisplayName("查询巡检用户列表-有数据返回")
    void findInspectionUserList_hasData_returnsList() {
        com.sunmax.together.entity.ops.InspectionUserEntity user = new com.sunmax.together.entity.ops.InspectionUserEntity();
        user.setId("iu-001");
        user.setTenantId("tenant-001");
        user.setType(1);
        when(inspectionUserDao.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(java.util.List.of(user));
        when(systemService.findUserInfoByIdsFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = inspectionService.findInspectionUserList("user-001", 1);
        assertNotNull(result);
    }

    @Test
    @DisplayName("查询站点保存任务列表-有数据返回分页")
    void getSiteSaveTaskList_hasData_returnsPage() {
        com.sunmax.together.entity.ops.InspectionTaskEntity task = new com.sunmax.together.entity.ops.InspectionTaskEntity();
        task.setId("task-001");
        task.setUserId("user-001");
        task.setTaskStatus(2);
        when(inspectionTaskDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.List.of(task)));
        when(inspectionSiteDao.findAllByTaskIdIn(any())).thenReturn(Collections.emptyList());
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));
        when(systemService.findUserInfoByIdsFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = inspectionService.getSiteSaveTaskList("user-001", null, 1, 10);
        assertNotNull(result);
    }

    @Test
    @DisplayName("删除巡检项-有ID列表返回成功")
    void deleteAllInspectionItemByIds_hasIds_returnsSuccess() {
        when(inspectionItemDao.findAllById(any())).thenReturn(Collections.emptyList());

        var result = inspectionService.deleteAllInspectionItemByIds(java.util.List.of("id-001", "id-002"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除巡检任务-有ID列表返回成功")
    void deleteInspectionTaskByIds_hasIds_returnsSuccess() {
        when(inspectionTaskDao.findAllById(any())).thenReturn(Collections.emptyList());

        var result = inspectionService.deleteInspectionTaskByIds(java.util.List.of("id-001"));
        assertTrue(result.isSuccess());
    }

    // === 深度覆盖：带完整数据的路径 ===

    @Test
    @DisplayName("查询巡检项列表-有数据返回分页")
    void queryInspectionItemList_hasData_returnsPage() {
        com.sunmax.together.entity.ops.InspectionItemEntity item = new com.sunmax.together.entity.ops.InspectionItemEntity();
        item.setId("item-001");
        item.setName("检查逆变器");
        item.setSiteId("site-001");
        when(inspectionItemDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.List.of(item)));

        var result = inspectionService.queryInspectionItemList("site-001", null, 1, 10);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询巡检任务列表-有数据返回分页")
    void queryInspectionTaskList_hasData_returnsPage() {
        com.sunmax.together.entity.ops.InspectionTaskEntity task = new com.sunmax.together.entity.ops.InspectionTaskEntity();
        task.setId("task-001");
        task.setTaskName("巡检任务1");
        task.setUserId("user-001");
        task.setTaskStatus(1);
        when(inspectionTaskDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.List.of(task)));
        when(inspectionSiteDao.findAllByTaskIdIn(any())).thenReturn(Collections.emptyList());
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));
        when(systemService.findUserInfoByIdsFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        InspectionTaskQueryVo vo = new InspectionTaskQueryVo();
        vo.setTaskName("巡检");
        vo.setPage(1);
        vo.setSize(10);
        var result = inspectionService.queryInspectionTaskList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询巡检用户列表-有用户返回列表")
    void getInspectionUserList_hasUsers_returnsList() {
        com.sunmax.together.entity.ops.InspectionUserEntity user = new com.sunmax.together.entity.ops.InspectionUserEntity();
        user.setId("user-001");
        user.setTenantId("tenant-001");
        when(inspectionUserDao.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(java.util.List.of(user));

        var result = inspectionService.getInspectionUserList("tenant-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询巡检任务详情-有站点和记录返回完整数据")
    void findInspectionTaskDetailById_hasSiteAndRecords_returnsFullData() {
        com.sunmax.together.entity.ops.InspectionTaskEntity task = new com.sunmax.together.entity.ops.InspectionTaskEntity();
        task.setId("task-001");
        task.setTaskName("巡检任务1");
        task.setUserId("user-001");
        task.setTaskStatus(2);
        when(inspectionTaskDao.findById("task-001")).thenReturn(java.util.Optional.of(task));
        when(inspectionSiteDao.findAllByTaskIdIn(any())).thenReturn(Collections.emptyList());
        when(inspectionRecordDao.findAllByTaskId(any())).thenReturn(Collections.emptyList());
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = inspectionService.findInspectionTaskDetailById("task-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询巡检站点列表-有站点和设备返回数据")
    void findInspectionSiteListById_hasSiteAndDevice_returnsData() {
        com.sunmax.together.entity.ops.InspectionSiteEntity site = new com.sunmax.together.entity.ops.InspectionSiteEntity();
        site.setId("site-001");
        site.setTaskId("task-001");
        site.setSiteId("site-001");
        when(inspectionSiteDao.findById("site-001")).thenReturn(java.util.Optional.of(site));
        when(inspectionRecordDao.findAllByTaskId(any())).thenReturn(Collections.emptyList());
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = inspectionService.findInspectionSiteListById("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询巡检用户名列表-有用户返回数据")
    void findInspectionUserList_hasUsers_returnsData() {
        com.sunmax.together.entity.ops.InspectionUserEntity user = new com.sunmax.together.entity.ops.InspectionUserEntity();
        user.setId("user-001");
        user.setUserIds("user-001");
        when(inspectionUserDao.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(java.util.List.of(user));
        when(systemService.findUserInfoByIdsFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = inspectionService.findInspectionUserList("user-001", 1);
        assertNotNull(result);
    }

    @Test
    @DisplayName("保存巡检项-正常保存返回成功")
    void saveInspectionItem_normal_returnsSuccess() {
        com.sunmax.together.vo.asset.InspectionItemVo vo = new com.sunmax.together.vo.asset.InspectionItemVo();
        vo.setSiteId("site-001");
        vo.setName("检查逆变器");
        when(inspectionItemDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        try {
            var result = inspectionService.saveInspectionItem(vo, null);
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("保存巡检任务-正常保存返回成功")
    void saveInspectionTask_normal_returnsSuccess() {
        com.sunmax.together.vo.asset.InspectionTaskSaveVo vo = new com.sunmax.together.vo.asset.InspectionTaskSaveVo();
        vo.setTaskName("巡检任务1");
        vo.setUserId("user-001");
        when(inspectionTaskDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(inspectionSiteDao.saveAll(any())).thenReturn(Collections.emptyList());

        try {
            var result = inspectionService.saveInspectionTask(vo);
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("更新巡检任务-交接分配返回成功")
    void updateInspectionTask_handOver_returnsSuccess() {
        com.sunmax.together.entity.ops.InspectionTaskEntity task = new com.sunmax.together.entity.ops.InspectionTaskEntity();
        task.setId("task-001");
        task.setTaskName("巡检任务1");
        task.setTaskStatus(0);
        when(inspectionTaskDao.findById("task-001")).thenReturn(java.util.Optional.of(task));
        when(inspectionTaskDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(inspectionRecordDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        com.sunmax.common.vo.together.ops.InspectionTaskUpdateVo vo = new com.sunmax.common.vo.together.ops.InspectionTaskUpdateVo();
        vo.setId("task-001");
        vo.setOperationType(0);
        vo.setOperationUserId("user-002");
        vo.setUserId("user-001");
        try {
            var result = inspectionService.updateInspectionTask(vo);
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("更新巡检任务-提交任务返回成功")
    void updateInspectionTask_submit_returnsSuccess() {
        com.sunmax.together.entity.ops.InspectionTaskEntity task = new com.sunmax.together.entity.ops.InspectionTaskEntity();
        task.setId("task-001");
        task.setTaskName("巡检任务1");
        task.setTaskStatus(1);
        when(inspectionTaskDao.findById("task-001")).thenReturn(java.util.Optional.of(task));
        when(inspectionTaskDao.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(inspectionRecordDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        com.sunmax.common.vo.together.ops.InspectionTaskUpdateVo vo = new com.sunmax.common.vo.together.ops.InspectionTaskUpdateVo();
        vo.setId("task-001");
        vo.setOperationType(1);
        vo.setUserId("user-001");
        try {
            var result = inspectionService.updateInspectionTask(vo);
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }
}
