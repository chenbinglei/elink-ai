package com.sunmax.together.controller.feign;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.operate.OrderCountDto;
import com.sunmax.common.dto.operate.OrderQtDto;
import com.sunmax.common.dto.together.ElectConfigDto;
import com.sunmax.common.dto.together.ops.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.AppInspectSiteVo;
import com.sunmax.common.vo.together.ops.InspectionTaskUpdateVo;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.service.DeviceFeignService;
import com.sunmax.together.service.DevopsFeignService;
import com.sunmax.together.service.asset.InspectionService;
import com.sunmax.together.service.operation.OperationAnalysisService;
import com.sunmax.together.service.operation.OrderRecordService;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/feign/devops")
@Api(tags = "提供给设备管理服务调用的远程接口")
@ApiIgnore()
public class DevopsFeignController {

    @Resource
    private DevopsFeignService devopsFeignService;

    @Resource
    private InspectionService inspectionService;

    @Resource
    private DeviceFeignService deviceFeignService;

    @Resource
    private OperationAnalysisService operationAnalysisService;

    @Resource
    private OrderRecordService orderRecordService;

    @PostMapping("findAppInspectHandTaskList")
    @ApiOperation("根据用户id查询巡检进行中的任务列表")
    @ApiOperationSupport(order = 1)
    public ResponseResult<List<AppInspectHandTaskListDto>> findAppInspectHandTaskList(@RequestParam String userId) {
        return devopsFeignService.findAppInspectHandTaskList(userId);
    }

    @PostMapping("updateInspectionTask")
    @ApiOperation("修改巡检任务")
    @WebLog("巡检管理-巡检任务-修改巡检任务")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Void> updateInspectionTask(@RequestBody InspectionTaskUpdateVo inspectionTaskVo) {
        return inspectionService.updateInspectionTask(inspectionTaskVo);
    }

    @PostMapping("findInspectionTaskDetailById")
    @ApiOperation("根据巡检任务id查询巡检任务详情")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParam(name = "id", value = "巡检任务id", dataType = "String", required = true)
    public ResponseResult<InspectionTaskDetailDto> findInspectionTaskDetailById(@RequestParam String id) {
        return inspectionService.findInspectionTaskDetailById(id);
    }

    @PostMapping("findInspectionSiteListById")
    @ApiOperation("根据巡检站点主键id查询巡检站点详情数据")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParam(name = "inspectionSiteId", value = "巡检站点主键id", dataType = "String", required = true)
    public ResponseResult<InspectionSiteListDto> findInspectionSiteListById(@RequestParam String inspectionSiteId) {
        return inspectionService.findInspectionSiteListById(inspectionSiteId);
    }

    @PostMapping("findInspectionUserList")
    @ApiOperation("根据用户id和节点类型查询巡检节点人员用户名称")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "节点类型 0-创建任务 1-启动巡检 2-现场巡检 3-巡检结果确认", dataType = "int", required = true)
    })
    public ResponseResult<List<InspectionUserNameDto>> findInspectionUserList(@RequestParam String userId, @RequestParam Integer type) {
        return inspectionService.findInspectionUserList(userId, type);
    }

    @PostMapping("findInspectionItemListBySiteId")
    @ApiOperation("根据站点id查询巡检项数据列表")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    public ResponseResult<List<AppInspectItemDto>> findInspectionItemListBySiteId(@RequestParam String siteId) {
        return devopsFeignService.findInspectionItemListBySiteId(siteId);
    }

    @PostMapping(value = "updateInspectSite")
    @ApiOperation("更新巡检站点报表")
    @ApiOperationSupport(order = 7)
    ResponseResult<Void> updateInspectSite(@RequestBody AppInspectSiteVo inspectSiteVo) {
        return devopsFeignService.updateInspectSite(inspectSiteVo);
    }

    @PostMapping(value = "uploadInspectSiteFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation("上传巡检站点附件")
    @ApiOperationSupport(order = 8)
    public ResponseResult<Void> uploadInspectSiteFile(@RequestParam String id, @RequestPart(value = "annexFile") MultipartFile annexFile) {
        return devopsFeignService.uploadInspectSiteFile(id, annexFile);
    }

    @PostMapping("findAppInspectSiteHistoryList")
    @ApiOperation("根据用户id查询历史巡检任务电站列表")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "int", required = true)
    })
    public ResponseResult<PageDto<AppInspectSiteHistoryDto>> findAppInspectSiteHistoryList(@RequestParam String userId, @RequestParam Integer page, @RequestParam Integer size) {
        return devopsFeignService.findAppInspectSiteHistoryList(userId, page, size);
    }

    @PostMapping("findAppInspectTaskHistoryList")
    @ApiOperation("根据用户id查询巡检历史任务列表")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "int", required = true)
    })
    public ResponseResult<PageDto<AppInspectTaskHistoryDto>> findAppInspectTaskHistoryList(@RequestParam String userId, @RequestParam Integer page, @RequestParam Integer size) {
        return devopsFeignService.findAppInspectTaskHistoryList(userId, page, size);
    }

    @PostMapping("findOrderRecordListByPileCodes")
    @ApiOperation("根据多个电桩编号和时间查询电桩订单列表数据")
    @ApiOperationSupport(order = 11)
    public ResponseResult<Map<String, OrderCountDto>> findOrderRecordListByPileCodes(@RequestBody List<String> pileCodes,
                                                                                     @RequestParam(required = false) String startTime,
                                                                                     @RequestParam(required = false) String endTime) {
        return deviceFeignService.findOrderRecordListByPileCodes(pileCodes, startTime, endTime);
    }

    @PostMapping("findOrderQtListByPileCodes")
    @ApiOperation("根据多个电桩编号和时间查询设备电量数据")
    @ApiOperationSupport(order = 12)
    public ResponseResult<Map<String, OrderQtDto>> findOrderQtListByPileCodes(@RequestBody List<String> pileCodes,
                                                                              @RequestParam(required = false) String startTime,
                                                                              @RequestParam(required = false) String endTime,
                                                                              @RequestParam Integer dateType) {
        return deviceFeignService.findOrderQtListByPileCodes(pileCodes, startTime, endTime, dateType);
    }

    @PostMapping("findElectConfigListBySiteIds")
    @ApiOperation("根据多个站点id和类型和日期查询站点电价配置数据")
    @ApiOperationSupport(order = 13)
    public ResponseResult<Map<String, List<ElectConfigDto>>> findElectConfigListBySiteIds(@RequestBody List<String> siteIds,
                                                                                    @RequestParam String moduleTypes,
                                                                                    @RequestParam String startDate,
                                                                                    @RequestParam String endDate) {
        return devopsFeignService.findElectConfigListBySiteIds(siteIds, moduleTypes, startDate, endDate);
    }

//    @PostMapping("countOperationOverview")
//    @ApiOperation("统计充电站运营总览数据")
//    @ApiOperationSupport(order = 14)
//    public ResponseResult<OperationOverviewDto> countOperationOverview(@RequestBody OperationOverviewVo operationOverviewVo) {
//        return operationAnalysisService.countOperationOverview(operationOverviewVo);
//    }
//
//    @PostMapping("countOperationCurve")
//    @ApiOperation("统计充电站运营总览曲线数据")
//    @ApiOperationSupport(order = 15)
//    public ResponseResult<OperationCurveDto> countOperationCurve(@RequestBody OperationCurveVo operationCurveVo) {
//        return operationAnalysisService.countOperationCurve(operationCurveVo);
//    }
//
//    @PostMapping("findOrderRecordListByPage")
//    @ApiOperation("分页查询订单记录列表")
//    @ApiOperationSupport(order = 16)
//    public ResponseResult<OrderRecordListDto> findOrderRecordListByPage(@RequestBody OrderRecordQueryVo orderRecordQueryVo) {
//        return orderRecordService.findOrderRecordListByPage(orderRecordQueryVo);
//    }
//
//    @PostMapping("findOrderRecordInfoById")
//    @ApiOperation("根据订单id查询基本信息")
//    @ApiOperationSupport(order = 17)
//    @ApiImplicitParam(name = "orderId", value = "订单id", dataType = "String", required = true)
//    public ResponseResult<OrderRecordInfoDto> findOrderRecordInfoById(@RequestParam String orderId) {
//        return orderRecordService.findOrderRecordInfoById(orderId);
//    }
//
//    @PostMapping("checkSitePassword")
//    @ApiOperation("校验站点密码是否正确")
//    @WebLog("订单管理-校验站点密码是否正确")
//    @ApiOperationSupport(order = 18)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true),
//            @ApiImplicitParam(name = "password", value = "站点密码", paramType = "query", required = true)
//    })
//    public ResponseResult<Integer> checkSitePassword(@RequestParam String siteId, @RequestParam String password) {
//        return orderRecordService.checkSitePassword(siteId, password);
//    }
//
//    @PostMapping("findOrderTradeMoneyById")
//    @ApiOperation("根据订单id查询订单交易金额")
//    @ApiOperationSupport(order = 19)
//    @ApiImplicitParam(name = "orderId", value = "站点id", paramType = "query", required = true)
//    public ResponseResult<OrderTradeMoneyDto> findOrderTradeMoneyById(@RequestParam String orderId) {
//        return orderRecordService.findOrderTradeMoneyById(orderId);
//    }
//
//    @PostMapping("orderRefund")
//    @ApiOperation("人工退款/补单")
//    @WebLog("订单管理-人工退款/补单")
//    @ApiOperationSupport(order = 20)
//    public ResponseResult<Void> orderRefund(@RequestBody OrderRefundVo orderRefundVo) {
//        return orderRecordService.orderRefund(orderRefundVo);
//    }
//
//    @PostMapping("updateOrderStatus")
//    @ApiOperation("修改订单状态")
//    @WebLog("订单管理-修改订单状态")
//    @ApiOperationSupport(order = 21)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "orderId", value = "订单id", required = true),
//            @ApiImplicitParam(name = "userId", value = "用户id", required = true)
//    })
//    public ResponseResult<Void> updateOrderStatus(@RequestParam String orderId, @RequestParam String userId) {
//        return orderRecordService.updateOrderStatus(orderId, userId);
//    }

}
