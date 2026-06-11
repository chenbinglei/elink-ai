package com.sunmax.common.feign.devops;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.operate.OrderCountDto;
import com.sunmax.common.dto.operate.OrderQtDto;
import com.sunmax.common.dto.together.ElectConfigDto;
import com.sunmax.common.dto.together.ops.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.AppInspectSiteVo;
import com.sunmax.common.vo.together.ops.InspectionTaskUpdateVo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 获取认证服务提供的接口
 */
@FeignClient(value = "together-service", path = "/together/feign/devops", fallbackFactory = GenericFeignFallbackFactory.class)
public interface DevopsTogetherFeignClient {

    @PostMapping("findAppInspectHandTaskList")
    @Operation(summary = "根据用户id查询巡检进行中的任务列表")
    
    ResponseResult<List<AppInspectHandTaskListDto>> findAppInspectHandTaskList(@RequestParam String userId);

    @PostMapping("updateInspectionTask")
    @Operation(summary = "修改巡检任务")
    
    ResponseResult<Void> updateInspectionTask(@RequestBody InspectionTaskUpdateVo inspectionTaskVo);

    @PostMapping("findInspectionTaskDetailById")
    @Operation(summary = "根据巡检任务id查询巡检任务详情")
    
    @Parameter(name = "id", description = "巡检任务id")
    ResponseResult<InspectionTaskDetailDto> findInspectionTaskDetailById(@RequestParam String id);

    @PostMapping("findInspectionSiteListById")
    @Operation(summary = "根据巡检站点主键id查询巡检站点详情数据")
    
    @Parameter(name = "inspectionSiteId", description = "巡检站点主键id")
    ResponseResult<InspectionSiteListDto> findInspectionSiteListById(@RequestParam String inspectionSiteId);

    @PostMapping("findInspectionUserList")
    @Operation(summary = "根据用户id和节点类型查询巡检节点人员用户名称")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "type", description = "节点类型 1-启动巡检 2-现场巡检 3-巡检结果确认")
    })
    public ResponseResult<List<InspectionUserNameDto>> findInspectionUserList(@RequestParam String userId, @RequestParam Integer type);

    @PostMapping("findInspectionItemListBySiteId")
    @Operation(summary = "根据站点id查询巡检项数据列表")
    
    @Parameter(name = "siteId", description = "站点id")
    ResponseResult<List<AppInspectItemDto>> findInspectionItemListBySiteId(@RequestParam String siteId);

    @PostMapping(value = "updateInspectSite")
    @Operation(summary = "更新巡检站点报表")
    
    ResponseResult<Void> updateInspectSite(@RequestBody AppInspectSiteVo inspectSiteVo);

    @PostMapping(value = "uploadInspectSiteFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传巡检站点附件")
    
    ResponseResult<Void> uploadInspectSiteFile(@RequestParam String id, @RequestPart(value = "annexFile") MultipartFile annexFile);

    @PostMapping("findAppInspectSiteHistoryList")
    @Operation(summary = "根据用户id查询历史巡检任务电站列表")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    ResponseResult<PageDto<AppInspectSiteHistoryDto>> findAppInspectSiteHistoryList(@RequestParam String userId, @RequestParam Integer page, @RequestParam Integer size);

    @PostMapping("findAppInspectTaskHistoryList")
    @Operation(summary = "根据用户id查询巡检历史任务列表")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    ResponseResult<PageDto<AppInspectTaskHistoryDto>> findAppInspectTaskHistoryList(@RequestParam String userId, @RequestParam Integer page, @RequestParam Integer size);

    @PostMapping("findOrderRecordListByPileCodes")
    @Operation(summary = "根据多个电桩编号和时间查询站点订单列表数据")
    
    ResponseResult<Map<String, OrderCountDto>> findOrderRecordListByPileCodes(@RequestBody List<String> pileCodes, @RequestParam(required = false) String startTime,
                                                                              @RequestParam(required = false) String endTime);

    @PostMapping("findOrderQtListByPileCodes")
    @Operation(summary = "根据多个电桩编号和时间查询设备电量数据")
    
    ResponseResult<Map<String, OrderQtDto>> findOrderQtListByPileCodes(@RequestBody List<String> pileCodes, @RequestParam(required = false) String startTime,
                                                                       @RequestParam(required = false) String endTime, @RequestParam Integer dateType);

    @PostMapping("findElectConfigListBySiteIds")
    @Operation(summary = "根据多个站点id和类型和日期查询站点电价配置数据")
    
    ResponseResult<Map<String, List<ElectConfigDto>>> findElectConfigListBySiteIds(@RequestBody List<String> siteIds, @RequestParam String moduleTypes,
                                                                                   @RequestParam String startDate, @RequestParam String endDate);

}
