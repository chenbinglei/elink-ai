package com.sunmax.together.controller.asset;

import com.alibaba.fastjson.JSON;
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
import io.swagger.annotations.*;
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
@Api(tags = "巡检管理")
public class InspectionController {

    @Autowired
    private InspectionService inspectionService;

    @PostMapping("saveInspectionItem")
    @ApiOperation("新增或编辑巡检项配置")
    @WebLog("巡检管理-巡检项配置-新增或编辑巡检项配置")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveInspectionItem(InspectionItemVo inspectionItemVo, MultipartFile iconFile) {
        return inspectionService.saveInspectionItem(inspectionItemVo, iconFile);
    }

    @PostMapping("deleteAllInspectionItemByIds")
    @ApiOperation("批量删除巡检项配置")
    @WebLog("巡检管理-巡检项配置-批量删除巡检项配置")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParam(name = "ids", value = "多个巡检项配置id 例如[巡检项配置1,巡检项配置2]", required = true)
    public ResponseResult<Void> deleteAllInspectionItemByIds(String ids) {
        return inspectionService.deleteAllInspectionItemByIds(JSON.parseArray(ids, String.class));
    }

    @PostMapping("queryInspectionItemList")
    @ApiOperation("根据站点id查询巡检项配置列表")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", required = true),
            @ApiImplicitParam(name = "name", value = "巡检项名称"),
            @ApiImplicitParam(name = "page", value = "当前页", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", required = true)
    })
    public ResponseResult<PageDto<InspectionItemListDto>> queryInspectionItemList(String siteId, String name, Integer page, Integer size) {
        return inspectionService.queryInspectionItemList(siteId, name, page, size);
    }

    @PostMapping("importInspectionItem")
    @ApiOperation("导入巡检项配置数据")
    @WebLog("巡检管理-巡检项配置-导入巡检项配置数据")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true),
            @ApiImplicitParam(name = "itemFile", value = "巡检项配置文件", dataType = "File", required = true),
    })
    public ResponseResult<List<String>> importInspectionItem(String siteId, MultipartFile itemFile) {
        return inspectionService.importInspectionItem(siteId, itemFile);
    }

    @PostMapping("getInspectionUserList")
    @ApiOperation("获取节点人员设置列表")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParam(name = "tenantId", value = "租户id", dataType = "String", required = true)
    public ResponseResult<List<InspectionUserListDto>> getInspectionUserList(String tenantId) {
        return inspectionService.getInspectionUserList(tenantId);
    }

    @PostMapping("saveInspectionUser")
    @ApiOperation("新增或编辑节点人员")
    @WebLog("巡检管理-巡检任务-新增或编辑节点人员设置")
    @ApiOperationSupport(order = 6)
    public ResponseResult<Void> saveInspectionUser(InspectionUserVo inspectionUserVo) {
        return inspectionService.saveInspectionUser(inspectionUserVo);
    }

    @PostMapping("queryInspectionTaskList")
    @ApiOperation("查询巡检任务列表")
    @ApiOperationSupport(order = 7)
    public ResponseResult<PageDto<InspectionTaskListDto>> queryInspectionTaskList(InspectionTaskQueryVo taskQueryVo) {
        return inspectionService.queryInspectionTaskList(taskQueryVo);
    }

    @PostMapping("saveInspectionTask")
    @ApiOperation("新增巡检任务")
    @WebLog("巡检管理-巡检任务-新增巡检任务")
    @ApiOperationSupport(order = 8)
    public ResponseResult<Void> saveInspectionTask(InspectionTaskSaveVo inspectionTaskVo) {
        return inspectionService.saveInspectionTask(inspectionTaskVo);
    }

    @PostMapping("getSiteSaveTaskList")
    @ApiOperation("获取新增任务站点列表")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "siteName", value = "站点id", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "int", required = true),
    })
    public ResponseResult<PageDto<SiteSaveTaskListDto>> getSiteSaveTaskList(String userId, String siteName, Integer page, Integer size) {
        return inspectionService.getSiteSaveTaskList(userId, siteName, page, size);
    }

    @PostMapping("updateInspectionTask")
    @ApiOperation("修改巡检任务")
    @WebLog("巡检管理-巡检任务-修改巡检任务")
    @ApiOperationSupport(order = 10)
    public ResponseResult<Void> updateInspectionTask(InspectionTaskUpdateVo inspectionTaskVo) {
        return inspectionService.updateInspectionTask(inspectionTaskVo);
    }

    @PostMapping("findInspectionTaskDetailById")
    @ApiOperation("根据巡检任务id查询巡检任务详情")
    @ApiOperationSupport(order = 11)
    @ApiImplicitParam(name = "id", value = "巡检任务id", dataType = "String", required = true)
    public ResponseResult<InspectionTaskDetailDto> findInspectionTaskDetailById(String id) {
        return inspectionService.findInspectionTaskDetailById(id);
    }

    @PostMapping("deleteInspectionTaskByIds")
    @ApiOperation("批量删除巡检任务数据")
    @WebLog("巡检管理-巡检任务-批量删除巡检任务数据")
    @ApiOperationSupport(order = 12)
    @ApiImplicitParam(name = "ids", value = "多个巡检任务id 例如[巡检任务id1,巡检任务id2]", required = true)
    public ResponseResult<Void> deleteInspectionTaskByIds(String ids) {
        return inspectionService.deleteInspectionTaskByIds(JSON.parseArray(ids, String.class));
    }

    @PostMapping("findInspectionSiteListById")
    @ApiOperation("根据巡检站点主键id查询巡检站点详情数据")
    @ApiOperationSupport(order = 13)
    @ApiImplicitParam(name = "inspectionSiteId", value = "巡检站点主键id", dataType = "String", required = true)
    public ResponseResult<InspectionSiteListDto> findInspectionSiteListById(String inspectionSiteId) {
        return inspectionService.findInspectionSiteListById(inspectionSiteId);
    }

    @PostMapping("findInspectionUserList")
    @ApiOperation("根据用户id和节点类型查询巡检节点人员用户名称")
    @ApiOperationSupport(order = 14)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "节点类型 0-创建任务 1-启动巡检 2-现场巡检 3-巡检结果确认", dataType = "int", required = true)
    })
    public ResponseResult<List<InspectionUserNameDto>> findInspectionUserList(String userId, Integer type) {
        return inspectionService.findInspectionUserList(userId, type);
    }

}
