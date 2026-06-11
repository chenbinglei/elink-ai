package com.sunmax.together.controller.asset;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.together.ops.InspectionSiteListDto;
import com.sunmax.common.dto.together.ops.InspectionTaskDetailDto;
import com.sunmax.common.dto.together.ops.InspectionUserNameDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.ops.InspectionTaskUpdateVo;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.asset.inspection.InspectionItemListDto;
import com.sunmax.together.dto.asset.inspection.InspectionTaskListDto;
import com.sunmax.together.dto.asset.inspection.InspectionUserListDto;
import com.sunmax.together.dto.asset.inspection.SiteSaveTaskListDto;
import com.sunmax.together.service.asset.InspectionService;
import com.sunmax.together.vo.asset.InspectionItemVo;
import com.sunmax.together.vo.asset.InspectionTaskQueryVo;
import com.sunmax.together.vo.asset.InspectionTaskSaveVo;
import com.sunmax.together.vo.asset.InspectionUserVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("inspection")
@Tag(name = "巡检管理")
public class InspectionController {

    @Autowired
    private InspectionService inspectionService;

    @PostMapping("saveInspectionItem")
    @Operation(summary = "新增或编辑巡检项配置")
    @WebLog("巡检管理-巡检项配置-新增或编辑巡检项配置")
    
    public ResponseResult<Void> saveInspectionItem(InspectionItemVo inspectionItemVo, MultipartFile iconFile) {
        return inspectionService.saveInspectionItem(inspectionItemVo, iconFile);
    }

    @PostMapping("deleteAllInspectionItemByIds")
    @Operation(summary = "批量删除巡检项配置")
    @WebLog("巡检管理-巡检项配置-批量删除巡检项配置")
    
    @Parameter(name = "ids", description = "多个巡检项配置id 例如[巡检项配置1,巡检项配置2]")
    public ResponseResult<Void> deleteAllInspectionItemByIds(String ids) {
        return inspectionService.deleteAllInspectionItemByIds(JSON.parseArray(ids, String.class));
    }

    @PostMapping("queryInspectionItemList")
    @Operation(summary = "根据站点id查询巡检项配置列表")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "name", description = "巡检项名称"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    public ResponseResult<PageDto<InspectionItemListDto>> queryInspectionItemList(String siteId, String name, Integer page, Integer size) {
        return inspectionService.queryInspectionItemList(siteId, name, page, size);
    }

    @PostMapping("importInspectionItem")
    @Operation(summary = "导入巡检项配置数据")
    @WebLog("巡检管理-巡检项配置-导入巡检项配置数据")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "itemFile", description = "巡检项配置文件"),
    })
    public ResponseResult<List<String>> importInspectionItem(String siteId, MultipartFile itemFile) {
        return inspectionService.importInspectionItem(siteId, itemFile);
    }

    @PostMapping("getInspectionUserList")
    @Operation(summary = "获取节点人员设置列表")
    
    @Parameter(name = "tenantId", description = "租户id")
    public ResponseResult<List<InspectionUserListDto>> getInspectionUserList(String tenantId) {
        return inspectionService.getInspectionUserList(tenantId);
    }

    @PostMapping("saveInspectionUser")
    @Operation(summary = "新增或编辑节点人员")
    @WebLog("巡检管理-巡检任务-新增或编辑节点人员设置")
    
    public ResponseResult<Void> saveInspectionUser(InspectionUserVo inspectionUserVo) {
        return inspectionService.saveInspectionUser(inspectionUserVo);
    }

    @PostMapping("queryInspectionTaskList")
    @Operation(summary = "查询巡检任务列表")
    
    public ResponseResult<PageDto<InspectionTaskListDto>> queryInspectionTaskList(InspectionTaskQueryVo taskQueryVo) {
        return inspectionService.queryInspectionTaskList(taskQueryVo);
    }

    @PostMapping("saveInspectionTask")
    @Operation(summary = "新增巡检任务")
    @WebLog("巡检管理-巡检任务-新增巡检任务")
    
    public ResponseResult<Void> saveInspectionTask(InspectionTaskSaveVo inspectionTaskVo) {
        return inspectionService.saveInspectionTask(inspectionTaskVo);
    }

    @PostMapping("getSiteSaveTaskList")
    @Operation(summary = "获取新增任务站点列表")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "siteName", description = "站点id"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数"),
    })
    public ResponseResult<PageDto<SiteSaveTaskListDto>> getSiteSaveTaskList(String userId, String siteName, Integer page, Integer size) {
        return inspectionService.getSiteSaveTaskList(userId, siteName, page, size);
    }

    @PostMapping("updateInspectionTask")
    @Operation(summary = "修改巡检任务")
    @WebLog("巡检管理-巡检任务-修改巡检任务")
    
    public ResponseResult<Void> updateInspectionTask(InspectionTaskUpdateVo inspectionTaskVo) {
        return inspectionService.updateInspectionTask(inspectionTaskVo);
    }

    @PostMapping("findInspectionTaskDetailById")
    @Operation(summary = "根据巡检任务id查询巡检任务详情")
    
    @Parameter(name = "id", description = "巡检任务id")
    public ResponseResult<InspectionTaskDetailDto> findInspectionTaskDetailById(String id) {
        return inspectionService.findInspectionTaskDetailById(id);
    }

    @PostMapping("deleteInspectionTaskByIds")
    @Operation(summary = "批量删除巡检任务数据")
    @WebLog("巡检管理-巡检任务-批量删除巡检任务数据")
    
    @Parameter(name = "ids", description = "多个巡检任务id 例如[巡检任务id1,巡检任务id2]")
    public ResponseResult<Void> deleteInspectionTaskByIds(String ids) {
        return inspectionService.deleteInspectionTaskByIds(JSON.parseArray(ids, String.class));
    }

    @PostMapping("findInspectionSiteListById")
    @Operation(summary = "根据巡检站点主键id查询巡检站点详情数据")
    
    @Parameter(name = "inspectionSiteId", description = "巡检站点主键id")
    public ResponseResult<InspectionSiteListDto> findInspectionSiteListById(String inspectionSiteId) {
        return inspectionService.findInspectionSiteListById(inspectionSiteId);
    }

    @PostMapping("findInspectionUserList")
    @Operation(summary = "根据用户id和节点类型查询巡检节点人员用户名称")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "type", description = "节点类型 0-创建任务 1-启动巡检 2-现场巡检 3-巡检结果确认")
    })
    public ResponseResult<List<InspectionUserNameDto>> findInspectionUserList(String userId, Integer type) {
        return inspectionService.findInspectionUserList(userId, type);
    }

}
