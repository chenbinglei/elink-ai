package com.sunmax.devops.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.together.ops.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.AppInspectSiteVo;
import com.sunmax.common.vo.together.ops.InspectionTaskUpdateVo;
import com.sunmax.devops.service.InspectAppService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("inspect")
@Tag(name = "巡检App管理")
public class InspectAppController {

    @Autowired
    private InspectAppService inspectAppService;

    @PostMapping("findAppInspectHandTaskList")
    @Operation(summary = "根据用户id查询巡检进行中的任务列表")
    
    @Parameter(name = "userId", description = "用户id")
    public ResponseResult<List<AppInspectHandTaskListDto>> findAppInspectHandTaskList(String userId) {
        return inspectAppService.findAppInspectHandTaskList(userId);
    }

    @PostMapping("updateInspectionTask")
    @Operation(summary = "修改巡检任务")
    
    public ResponseResult<Void> updateInspectionTask(InspectionTaskUpdateVo inspectionTaskVo) {
        return inspectAppService.updateInspectionTask(inspectionTaskVo);
    }

    @PostMapping("findInspectionTaskDetailById")
    @Operation(summary = "根据巡检任务id查询巡检任务详情")
    
    @Parameter(name = "id", description = "巡检任务id")
    public ResponseResult<InspectionTaskDetailDto> findInspectionTaskDetailById(String id) {
        return inspectAppService.findInspectionTaskDetailById(id);
    }

    @PostMapping("findInspectionSiteListById")
    @Operation(summary = "根据巡检站点主键id查询巡检站点详情数据")
    
    @Parameter(name = "inspectionSiteId", description = "巡检站点主键id")
    public ResponseResult<InspectionSiteListDto> findInspectionSiteListById(String inspectionSiteId) {
        return inspectAppService.findInspectionSiteListById(inspectionSiteId);
    }

    @PostMapping("findInspectionUserList")
    @Operation(summary = "根据用户id和节点类型查询巡检节点人员用户名称")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "type", description = "节点类型 1-启动巡检 2-现场巡检 3-巡检结果确认")
    })
    public ResponseResult<List<InspectionUserNameDto>> findInspectionUserList(String userId, Integer type) {
        return inspectAppService.findInspectionUserList(userId, type);
    }

    @PostMapping("findInspectionItemListBySiteId")
    @Operation(summary = "根据站点id查询巡检项数据列表")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<List<AppInspectItemDto>> findInspectionItemListBySiteId(String siteId) {
        return inspectAppService.findInspectionItemListBySiteId(siteId);
    }

    @PostMapping("updateInspectSite")
    @Operation(summary = "更新巡检站点报表")
    
    public ResponseResult<Void> updateInspectSite(AppInspectSiteVo inspectSiteVo) {
        return inspectAppService.updateInspectSite(inspectSiteVo);
    }

    @PostMapping("uploadInspectSiteFile")
    @Operation(summary = "上传巡检站点附件")
    
    public ResponseResult<Void> uploadInspectSiteFile(String id, MultipartFile annexFile) {
        return inspectAppService.uploadInspectSiteFile(id, annexFile);
    }

    @PostMapping("findAppInspectSiteHistoryList")
    @Operation(summary = "根据用户id查询历史巡检任务电站列表")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    public ResponseResult<PageDto<AppInspectSiteHistoryDto>> findAppInspectSiteHistoryList(String userId, Integer page, Integer size) {
        return inspectAppService.findAppInspectSiteHistoryList(userId, page, size);
    }

    @PostMapping("findAppInspectTaskHistoryList")
    @Operation(summary = "根据用户id查询巡检历史任务列表")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    public ResponseResult<PageDto<AppInspectTaskHistoryDto>> findAppInspectTaskHistoryList(String userId, Integer page, Integer size) {
        return inspectAppService.findAppInspectTaskHistoryList(userId, page, size);
    }


}
