package com.sunmax.devops.controller;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.together.ops.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.AppInspectSiteVo;
import com.sunmax.common.vo.together.ops.InspectionTaskUpdateVo;
import com.sunmax.devops.service.InspectAppService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("inspect")
@Api(tags = "巡检App管理")
public class InspectAppController {

    @Autowired
    private InspectAppService inspectAppService;

    @PostMapping("findAppInspectHandTaskList")
    @ApiOperation("根据用户id查询巡检进行中的任务列表")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true)
    public ResponseResult<List<AppInspectHandTaskListDto>> findAppInspectHandTaskList(String userId) {
        return inspectAppService.findAppInspectHandTaskList(userId);
    }

    @PostMapping("updateInspectionTask")
    @ApiOperation("修改巡检任务")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Void> updateInspectionTask(InspectionTaskUpdateVo inspectionTaskVo) {
        return inspectAppService.updateInspectionTask(inspectionTaskVo);
    }

    @PostMapping("findInspectionTaskDetailById")
    @ApiOperation("根据巡检任务id查询巡检任务详情")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParam(name = "id", value = "巡检任务id", dataType = "String", required = true)
    public ResponseResult<InspectionTaskDetailDto> findInspectionTaskDetailById(String id) {
        return inspectAppService.findInspectionTaskDetailById(id);
    }

    @PostMapping("findInspectionSiteListById")
    @ApiOperation("根据巡检站点主键id查询巡检站点详情数据")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParam(name = "inspectionSiteId", value = "巡检站点主键id", dataType = "String", required = true)
    public ResponseResult<InspectionSiteListDto> findInspectionSiteListById(String inspectionSiteId) {
        return inspectAppService.findInspectionSiteListById(inspectionSiteId);
    }

    @PostMapping("findInspectionUserList")
    @ApiOperation("根据用户id和节点类型查询巡检节点人员用户名称")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "节点类型 1-启动巡检 2-现场巡检 3-巡检结果确认", dataType = "int", required = true)
    })
    public ResponseResult<List<InspectionUserNameDto>> findInspectionUserList(String userId, Integer type) {
        return inspectAppService.findInspectionUserList(userId, type);
    }

    @PostMapping("findInspectionItemListBySiteId")
    @ApiOperation("根据站点id查询巡检项数据列表")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    public ResponseResult<List<AppInspectItemDto>> findInspectionItemListBySiteId(String siteId) {
        return inspectAppService.findInspectionItemListBySiteId(siteId);
    }

    @PostMapping("updateInspectSite")
    @ApiOperation("更新巡检站点报表")
    @ApiOperationSupport(order = 7)
    public ResponseResult<Void> updateInspectSite(AppInspectSiteVo inspectSiteVo) {
        return inspectAppService.updateInspectSite(inspectSiteVo);
    }

    @PostMapping("uploadInspectSiteFile")
    @ApiOperation("上传巡检站点附件")
    @ApiOperationSupport(order = 8)
    public ResponseResult<Void> uploadInspectSiteFile(String id, MultipartFile annexFile) {
        return inspectAppService.uploadInspectSiteFile(id, annexFile);
    }

    @PostMapping("findAppInspectSiteHistoryList")
    @ApiOperation("根据用户id查询历史巡检任务电站列表")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "int", required = true)
    })
    public ResponseResult<PageDto<AppInspectSiteHistoryDto>> findAppInspectSiteHistoryList(String userId, Integer page, Integer size) {
        return inspectAppService.findAppInspectSiteHistoryList(userId, page, size);
    }

    @PostMapping("findAppInspectTaskHistoryList")
    @ApiOperation("根据用户id查询巡检历史任务列表")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "int", required = true)
    })
    public ResponseResult<PageDto<AppInspectTaskHistoryDto>> findAppInspectTaskHistoryList(String userId, Integer page, Integer size) {
        return inspectAppService.findAppInspectTaskHistoryList(userId, page, size);
    }


}
