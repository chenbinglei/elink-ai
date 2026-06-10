package com.sunmax.together.controller.feign;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

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

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Hidden;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/feign/devops")
@Tag(name = "提供给设备管理服务调用的远程接口")
@Hidden()
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
    @Operation(summary = "根据用户id查询巡检进行中的任务列表")
    
    public ResponseResult<List<AppInspectHandTaskListDto>> findAppInspectHandTaskList(@RequestParam String userId) {
        return devopsFeignService.findAppInspectHandTaskList(userId);
    }

    @PostMapping("updateInspectionTask")
    @Operation(summary = "修改巡检任务")
    @WebLog("巡检管理-巡检任务-修改巡检任务")
    
    public ResponseResult<Void> updateInspectionTask(@RequestBody InspectionTaskUpdateVo inspectionTaskVo) {
        return inspectionService.updateInspectionTask(inspectionTaskVo);
    }

    @PostMapping("findInspectionTaskDetailById")
    @Operation(summary = "根据巡检任务id查询巡检任务详情")
    
    @Parameter(name = "id", description = "巡检任务id")
    public ResponseResult<InspectionTaskDetailDto> findInspectionTaskDetailById(@RequestParam String id) {
        return inspectionService.findInspectionTaskDetailById(id);
    }

    @PostMapping("findInspectionSiteListById")
    @Operation(summary = "根据巡检站点主键id查询巡检站点详情数据")
    
    @Parameter(name = "inspectionSiteId", description = "巡检站点主键id")
    public ResponseResult<InspectionSiteListDto> findInspectionSiteListById(@RequestParam String inspectionSiteId) {
        return inspectionService.findInspectionSiteListById(inspectionSiteId);
    }

    @PostMapping("findInspectionUserList")
    @Operation(summary = "根据用户id和节点类型查询巡检节点人员用户名称")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "type", description = "节点类型 0-创建任务 1-启动巡检 2-现场巡检 3-巡检结果确认")
    })
    public ResponseResult<List<InspectionUserNameDto>> findInspectionUserList(@RequestParam String userId, @RequestParam Integer type) {
        return inspectionService.findInspectionUserList(userId, type);
    }

    @PostMapping("findInspectionItemListBySiteId")
    @Operation(summary = "根据站点id查询巡检项数据列表")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<List<AppInspectItemDto>> findInspectionItemListBySiteId(@RequestParam String siteId) {
        return devopsFeignService.findInspectionItemListBySiteId(siteId);
    }

    @PostMapping(value = "updateInspectSite")
    @Operation(summary = "更新巡检站点报表")
    
    ResponseResult<Void> updateInspectSite(@RequestBody AppInspectSiteVo inspectSiteVo) {
        return devopsFeignService.updateInspectSite(inspectSiteVo);
    }

    @PostMapping(value = "uploadInspectSiteFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传巡检站点附件")
    
    public ResponseResult<Void> uploadInspectSiteFile(@RequestParam String id, @RequestPart(value = "annexFile") MultipartFile annexFile) {
        return devopsFeignService.uploadInspectSiteFile(id, annexFile);
    }

    @PostMapping("findAppInspectSiteHistoryList")
    @Operation(summary = "根据用户id查询历史巡检任务电站列表")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    public ResponseResult<PageDto<AppInspectSiteHistoryDto>> findAppInspectSiteHistoryList(@RequestParam String userId, @RequestParam Integer page, @RequestParam Integer size) {
        return devopsFeignService.findAppInspectSiteHistoryList(userId, page, size);
    }

    @PostMapping("findAppInspectTaskHistoryList")
    @Operation(summary = "根据用户id查询巡检历史任务列表")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    public ResponseResult<PageDto<AppInspectTaskHistoryDto>> findAppInspectTaskHistoryList(@RequestParam String userId, @RequestParam Integer page, @RequestParam Integer size) {
        return devopsFeignService.findAppInspectTaskHistoryList(userId, page, size);
    }

    @PostMapping("findOrderRecordListByPileCodes")
    @Operation(summary = "根据多个电桩编号和时间查询电桩订单列表数据")
    
    public ResponseResult<Map<String, OrderCountDto>> findOrderRecordListByPileCodes(@RequestBody List<String> pileCodes,
                                                                                     @RequestParam(required = false) String startTime,
                                                                                     @RequestParam(required = false) String endTime) {
        return deviceFeignService.findOrderRecordListByPileCodes(pileCodes, startTime, endTime);
    }

    @PostMapping("findOrderQtListByPileCodes")
    @Operation(summary = "根据多个电桩编号和时间查询设备电量数据")
    
    public ResponseResult<Map<String, OrderQtDto>> findOrderQtListByPileCodes(@RequestBody List<String> pileCodes,
                                                                              @RequestParam(required = false) String startTime,
                                                                              @RequestParam(required = false) String endTime,
                                                                              @RequestParam Integer dateType) {
        return deviceFeignService.findOrderQtListByPileCodes(pileCodes, startTime, endTime, dateType);
    }

    @PostMapping("findElectConfigListBySiteIds")
    @Operation(summary = "根据多个站点id和类型和日期查询站点电价配置数据")
    
    public ResponseResult<Map<String, List<ElectConfigDto>>> findElectConfigListBySiteIds(@RequestBody List<String> siteIds,
                                                                                    @RequestParam String moduleTypes,
                                                                                    @RequestParam String startDate,
                                                                                    @RequestParam String endDate) {
        return devopsFeignService.findElectConfigListBySiteIds(siteIds, moduleTypes, startDate, endDate);
    }

//    @PostMapping("countOperationOverview")
//    @Operation(summary = "统计充电站运营总览数据")
//    
//    public ResponseResult<OperationOverviewDto> countOperationOverview(@RequestBody OperationOverviewVo operationOverviewVo) {
//        return operationAnalysisService.countOperationOverview(operationOverviewVo);
//    }
//
//    @PostMapping("countOperationCurve")
//    @Operation(summary = "统计充电站运营总览曲线数据")
//    
//    public ResponseResult<OperationCurveDto> countOperationCurve(@RequestBody OperationCurveVo operationCurveVo) {
//        return operationAnalysisService.countOperationCurve(operationCurveVo);
//    }
//
//    @PostMapping("findOrderRecordListByPage")
//    @Operation(summary = "分页查询订单记录列表")
//    
//    public ResponseResult<OrderRecordListDto> findOrderRecordListByPage(@RequestBody OrderRecordQueryVo orderRecordQueryVo) {
//        return orderRecordService.findOrderRecordListByPage(orderRecordQueryVo);
//    }
//
//    @PostMapping("findOrderRecordInfoById")
//    @Operation(summary = "根据订单id查询基本信息")
//    
//    @Parameter(name = "orderId", description = "订单id")
//    public ResponseResult<OrderRecordInfoDto> findOrderRecordInfoById(@RequestParam String orderId) {
//        return orderRecordService.findOrderRecordInfoById(orderId);
//    }
//
//    @PostMapping("checkSitePassword")
//    @Operation(summary = "校验站点密码是否正确")
//    @WebLog("订单管理-校验站点密码是否正确")
//    
//    @Parameters({
//            @Parameter(name = "siteId", description = "站点id"),
//            @Parameter(name = "password", description = "站点密码")
//    })
//    public ResponseResult<Integer> checkSitePassword(@RequestParam String siteId, @RequestParam String password) {
//        return orderRecordService.checkSitePassword(siteId, password);
//    }
//
//    @PostMapping("findOrderTradeMoneyById")
//    @Operation(summary = "根据订单id查询订单交易金额")
//    
//    @Parameter(name = "orderId", description = "站点id")
//    public ResponseResult<OrderTradeMoneyDto> findOrderTradeMoneyById(@RequestParam String orderId) {
//        return orderRecordService.findOrderTradeMoneyById(orderId);
//    }
//
//    @PostMapping("orderRefund")
//    @Operation(summary = "人工退款/补单")
//    @WebLog("订单管理-人工退款/补单")
//    
//    public ResponseResult<Void> orderRefund(@RequestBody OrderRefundVo orderRefundVo) {
//        return orderRecordService.orderRefund(orderRefundVo);
//    }
//
//    @PostMapping("updateOrderStatus")
//    @Operation(summary = "修改订单状态")
//    @WebLog("订单管理-修改订单状态")
//    
//    @Parameters({
//            @Parameter(name = "orderId", description = "订单id"),
//            @Parameter(name = "userId", description = "用户id")
//    })
//    public ResponseResult<Void> updateOrderStatus(@RequestParam String orderId, @RequestParam String userId) {
//        return orderRecordService.updateOrderStatus(orderId, userId);
//    }

}
