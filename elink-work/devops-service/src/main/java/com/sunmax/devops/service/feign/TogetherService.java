package com.sunmax.devops.service.feign;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.operate.OrderCountDto;
import com.sunmax.common.dto.operate.OrderQtDto;
import com.sunmax.common.dto.together.ElectConfigDto;
import com.sunmax.common.dto.together.ops.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.AppInspectSiteVo;
import com.sunmax.common.vo.together.ops.InspectionTaskUpdateVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 获取认证服务提供的接口
 */
@FeignClient(value = "together-service")
@RestController
@RequestMapping("/together/feign/devops")
public interface TogetherService {

    @PostMapping("findAppInspectHandTaskList")
    @ApiOperation("根据用户id查询巡检进行中的任务列表")
    @ApiOperationSupport(order = 1)
    ResponseResult<List<AppInspectHandTaskListDto>> findAppInspectHandTaskList(@RequestParam String userId);

    @PostMapping("updateInspectionTask")
    @ApiOperation("修改巡检任务")
    @ApiOperationSupport(order = 2)
    ResponseResult<Void> updateInspectionTask(@RequestBody InspectionTaskUpdateVo inspectionTaskVo);

    @PostMapping("findInspectionTaskDetailById")
    @ApiOperation("根据巡检任务id查询巡检任务详情")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParam(name = "id", value = "巡检任务id", dataType = "String", required = true)
    ResponseResult<InspectionTaskDetailDto> findInspectionTaskDetailById(@RequestParam String id);

    @PostMapping("findInspectionSiteListById")
    @ApiOperation("根据巡检站点主键id查询巡检站点详情数据")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParam(name = "inspectionSiteId", value = "巡检站点主键id", dataType = "String", required = true)
    ResponseResult<InspectionSiteListDto> findInspectionSiteListById(@RequestParam String inspectionSiteId);

    @PostMapping("findInspectionUserList")
    @ApiOperation("根据用户id和节点类型查询巡检节点人员用户名称")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "节点类型 1-启动巡检 2-现场巡检 3-巡检结果确认", dataType = "int", required = true)
    })
    public ResponseResult<List<InspectionUserNameDto>> findInspectionUserList(@RequestParam String userId, @RequestParam Integer type);

    @PostMapping("findInspectionItemListBySiteId")
    @ApiOperation("根据站点id查询巡检项数据列表")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    ResponseResult<List<AppInspectItemDto>> findInspectionItemListBySiteId(@RequestParam String siteId);

    @PostMapping(value = "updateInspectSite")
    @ApiOperation("更新巡检站点报表")
    @ApiOperationSupport(order = 7)
    ResponseResult<Void> updateInspectSite(@RequestBody AppInspectSiteVo inspectSiteVo);

    @PostMapping(value = "uploadInspectSiteFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation("上传巡检站点附件")
    @ApiOperationSupport(order = 8)
    ResponseResult<Void> uploadInspectSiteFile(@RequestParam String id, @RequestPart(value = "annexFile") MultipartFile annexFile);

    @PostMapping("findAppInspectSiteHistoryList")
    @ApiOperation("根据用户id查询历史巡检任务电站列表")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "int", required = true)
    })
    ResponseResult<PageDto<AppInspectSiteHistoryDto>> findAppInspectSiteHistoryList(@RequestParam String userId, @RequestParam Integer page, @RequestParam Integer size);

    @PostMapping("findAppInspectTaskHistoryList")
    @ApiOperation("根据用户id查询巡检历史任务列表")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "int", required = true)
    })
    ResponseResult<PageDto<AppInspectTaskHistoryDto>> findAppInspectTaskHistoryList(@RequestParam String userId, @RequestParam Integer page, @RequestParam Integer size);

    @PostMapping("findOrderRecordListByPileCodes")
    @ApiOperation("根据多个电桩编号和时间查询站点订单列表数据")
    @ApiOperationSupport(order = 11)
    ResponseResult<Map<String, OrderCountDto>> findOrderRecordListByPileCodes(@RequestBody List<String> pileCodes, @RequestParam(required = false) String startTime,
                                                                              @RequestParam(required = false) String endTime);

    @PostMapping("findOrderQtListByPileCodes")
    @ApiOperation("根据多个电桩编号和时间查询设备电量数据")
    @ApiOperationSupport(order = 12)
    ResponseResult<Map<String, OrderQtDto>> findOrderQtListByPileCodes(@RequestBody List<String> pileCodes, @RequestParam(required = false) String startTime,
                                                                       @RequestParam(required = false) String endTime, @RequestParam Integer dateType);

    @PostMapping("findElectConfigListBySiteIds")
    @ApiOperation("根据多个站点id和类型和日期查询站点电价配置数据")
    @ApiOperationSupport(order = 13)
    ResponseResult<Map<String, List<ElectConfigDto>>> findElectConfigListBySiteIds(@RequestBody List<String> siteIds, @RequestParam String moduleTypes,
                                                                                   @RequestParam String startDate, @RequestParam String endDate);

}
