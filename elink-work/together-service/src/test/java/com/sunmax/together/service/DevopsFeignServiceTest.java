package com.sunmax.together.service;

import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dao.asset.ElectConfigDao;
import com.sunmax.together.dao.asset.ElectTimeFrameDao;
import com.sunmax.together.dao.ops.InspectionItemDao;
import com.sunmax.together.dao.ops.InspectionSiteDao;
import com.sunmax.together.dao.ops.InspectionTaskDao;
import com.sunmax.together.entity.assets.ElectConfigEntity;
import com.sunmax.together.entity.assets.ElectTimeFrameEntity;
import com.sunmax.together.entity.ops.InspectionItemEntity;
import com.sunmax.together.entity.ops.InspectionSiteEntity;
import com.sunmax.together.entity.ops.InspectionTaskEntity;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.impl.DevopsFeignServiceImpl;
import com.sunmax.common.vo.together.AppInspectSiteVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DevopsFeignService 单元测试")
class DevopsFeignServiceTest {

    @Mock
    private InspectionTaskDao inspectionTaskDao;

    @Mock
    private InspectionItemDao inspectionItemDao;

    @Mock
    private InspectionSiteDao inspectionSiteDao;

    @Mock
    private SystemService systemService;

    @Mock
    private DeviceService deviceService;

    @Mock
    private ElectConfigDao electConfigDao;

    @Mock
    private ElectTimeFrameDao electTimeFrameDao;

    @InjectMocks
    private DevopsFeignServiceImpl devopsFeignService;

    @Test
    @DisplayName("查询巡检手持任务列表-无数据返回空列表")
    void findAppInspectHandTaskList_noData_returnsEmpty() {
        when(inspectionTaskDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());

        var result = devopsFeignService.findAppInspectHandTaskList("user-001");

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("查询巡检手持任务列表-有任务返回数据")
    void findAppInspectHandTaskList_hasTasks_returnsData() {
        InspectionTaskEntity task = new InspectionTaskEntity();
        task.setId("task-001");
        task.setUserId("user-001");
        task.setTaskStatus(2);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        when(inspectionTaskDao.findAll(any(Example.class))).thenReturn(List.of(task));

        var result = devopsFeignService.findAppInspectHandTaskList("user-001");

        assertTrue(result.isSuccess());
        assertFalse(result.getData().isEmpty());
    }

    @Test
    @DisplayName("查询巡检手持任务列表-已完成任务被过滤")
    void findAppInspectHandTaskList_completedTask_filtered() {
        InspectionTaskEntity task1 = new InspectionTaskEntity();
        task1.setId("task-001");
        task1.setUserId("user-001");
        task1.setTaskStatus(1); // 已完成
        InspectionTaskEntity task2 = new InspectionTaskEntity();
        task2.setId("task-002");
        task2.setUserId("user-001");
        task2.setTaskStatus(5); // 已完结
        when(inspectionTaskDao.findAll(any(Example.class))).thenReturn(List.of(task1, task2));

        var result = devopsFeignService.findAppInspectHandTaskList("user-001");

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("查询巡检项列表-无数据返回空列表")
    void findInspectionItemList_noData_returnsEmpty() {
        when(inspectionItemDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());

        var result = devopsFeignService.findInspectionItemListBySiteId("site-001");

        assertNotNull(result);
    }

    @Test
    @DisplayName("查询巡检项列表-有数据返回列表")
    void findInspectionItemList_hasData_returnsList() {
        InspectionItemEntity item = new InspectionItemEntity();
        item.setId("item-001");
        item.setSiteId("site-001");
        item.setName("检查逆变器");
        when(inspectionItemDao.findAll(any(Example.class))).thenReturn(List.of(item));

        var result = devopsFeignService.findInspectionItemListBySiteId("site-001");

        assertTrue(result.isSuccess());
        assertFalse(result.getData().isEmpty());
    }

    @Test
    @DisplayName("更新巡检站点-站点不存在返回错误")
    void updateInspectSite_notFound_returnsError() {
        when(inspectionSiteDao.findById(any())).thenReturn(Optional.empty());

        AppInspectSiteVo vo = new AppInspectSiteVo();
        vo.setId("site-001");
        var result = devopsFeignService.updateInspectSite(vo);

        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("更新巡检站点-更新状态为完成")
    void updateInspectSite_updateStatusToFinished_returnsOk() {
        InspectionSiteEntity entity = new InspectionSiteEntity();
        entity.setId("site-001");
        when(inspectionSiteDao.findById(any())).thenReturn(Optional.of(entity));
        when(inspectionSiteDao.save(any())).thenReturn(entity);

        AppInspectSiteVo vo = new AppInspectSiteVo();
        vo.setId("site-001");
        vo.setStatus(3);
        vo.setRemark("正常");
        var result = devopsFeignService.updateInspectSite(vo);

        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新巡检站点-更新巡检项状态")
    void updateInspectSite_updateItemStates_returnsOk() {
        InspectionSiteEntity entity = new InspectionSiteEntity();
        entity.setId("site-001");
        when(inspectionSiteDao.findById(any())).thenReturn(Optional.of(entity));
        when(inspectionSiteDao.save(any())).thenReturn(entity);

        AppInspectSiteVo vo = new AppInspectSiteVo();
        vo.setId("site-001");
        vo.setItemStates("{\"1\":2,\"2\":3}");
        var result = devopsFeignService.updateInspectSite(vo);

        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("上传巡检站点附件-站点不存在返回错误")
    void uploadInspectSiteFile_notFound_returnsError() {
        when(inspectionSiteDao.findById(any())).thenReturn(Optional.empty());

        var result = devopsFeignService.uploadInspectSiteFile("site-001", null);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询巡检站点历史-空userId返回错误")
    void findAppInspectSiteHistoryList_emptyUserId_returnsError() {
        var result = devopsFeignService.findAppInspectSiteHistoryList(null, 1, 10);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询巡检站点历史-用户无租户返回错误")
    void findAppInspectSiteHistoryList_noTenant_returnsError() {
        UserDto user = new UserDto();
        user.setId("user-001");
        when(systemService.findUserInfoByIdsFeign(any())).thenReturn(ResponseResult.ok(Map.of("user-001", user)));

        var result = devopsFeignService.findAppInspectSiteHistoryList("user-001", 1, 10);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询巡检站点历史-有数据返回分页")
    void findAppInspectSiteHistoryList_hasData_returnsPage() {
        UserDto user = new UserDto();
        user.setId("user-001");
        user.setTenantId("tenant-001");
        when(systemService.findUserInfoByIdsFeign(any())).thenReturn(ResponseResult.ok(Map.of("user-001", user)));

        InspectionTaskEntity task = new InspectionTaskEntity();
        task.setId("task-001");
        task.setTenantId("tenant-001");
        when(inspectionTaskDao.findAll(any(Example.class), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(task));

        InspectionSiteEntity site = new InspectionSiteEntity();
        site.setId("is-001");
        site.setSiteId("site-001");
        site.setTaskId("task-001");
        site.setUserId("user-001");
        site.setFinishTime("2026-01-15 10:00:00");
        site.setStatus(4);
        site.setItemStates("{\"1\":2,\"2\":3}");
        when(inspectionSiteDao.findAllByTaskIdIn(any())).thenReturn(List.of(site));

        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        siteInfo.setSiteName("测试站点");
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Map.of("site-001", siteInfo)));

        var result = devopsFeignService.findAppInspectSiteHistoryList("user-001", 1, 10);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询巡检任务历史-空userId返回错误")
    void findAppInspectTaskHistoryList_emptyUserId_returnsError() {
        var result = devopsFeignService.findAppInspectTaskHistoryList(null, 1, 10);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询巡检任务历史-用户无租户返回错误")
    void findAppInspectTaskHistoryList_noTenant_returnsError() {
        UserDto user = new UserDto();
        user.setId("user-001");
        when(systemService.findUserInfoByIdsFeign(any())).thenReturn(ResponseResult.ok(Map.of("user-001", user)));

        var result = devopsFeignService.findAppInspectTaskHistoryList("user-001", 1, 10);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询巡检任务历史-有数据返回分页")
    void findAppInspectTaskHistoryList_hasData_returnsPage() {
        UserDto user = new UserDto();
        user.setId("user-001");
        user.setTenantId("tenant-001");
        user.setFullName("张三");
        when(systemService.findUserInfoByIdsFeign(any())).thenReturn(ResponseResult.ok(Map.of("user-001", user)));

        InspectionTaskEntity task = new InspectionTaskEntity();
        task.setId("task-001");
        task.setUserId("user-001");
        task.setTenantId("tenant-001");
        task.setTaskStatus(4);
        task.setCreateTime(LocalDateTime.now());
        Page<InspectionTaskEntity> taskPage = new PageImpl<>(List.of(task));
        when(inspectionTaskDao.findAll(any(Specification.class), any(Pageable.class))).thenReturn(taskPage);

        InspectionSiteEntity site = new InspectionSiteEntity();
        site.setId("is-001");
        site.setTaskId("task-001");
        site.setItemStates("{\"1\":3}");
        when(inspectionSiteDao.findAllByTaskIdIn(any())).thenReturn(List.of(site));

        var result = devopsFeignService.findAppInspectTaskHistoryList("user-001", 1, 10);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询电费配置-有数据返回Map")
    void findElectConfigListBySiteIds_hasData_returnsMap() {
        ElectConfigEntity config = new ElectConfigEntity();
        config.setId("ec-001");
        config.setSiteId("site-001");
        config.setModuleType(1);
        when(electConfigDao.findElectConfigListBySiteIds(any(), any(), any(), any()))
                .thenReturn(List.of(config));

        ElectTimeFrameEntity frame = new ElectTimeFrameEntity();
        frame.setId("etf-001");
        frame.setElectConfigId("ec-001");
        when(electTimeFrameDao.findAllByElectConfigIdIn(any())).thenReturn(List.of(frame));

        var result = devopsFeignService.findElectConfigListBySiteIds(
                List.of("site-001"), "1", "2026-01-01", "2026-01-31");

        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询电费配置-无数据返回空Map")
    void findElectConfigListBySiteIds_noData_returnsEmpty() {
        when(electConfigDao.findElectConfigListBySiteIds(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        var result = devopsFeignService.findElectConfigListBySiteIds(
                List.of("site-001"), "1", "2026-01-01", "2026-01-31");

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }
}
