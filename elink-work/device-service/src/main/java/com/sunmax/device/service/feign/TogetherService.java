package com.sunmax.device.service.feign;

import com.sunmax.common.dto.operate.OrderCountDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 接收聚合服务提供的接口
 */
@FeignClient(value = "together-service")
@RestController
@RequestMapping("/together/feign/device")
public interface TogetherService {

    @PostMapping("deleteAllGroupBeSiteBySiteId")
    @ApiOperation("根据站点id删除所有用户分组关联的站点数据")
    @ApiOperationSupport(order = 1)
    ResponseResult<String> deleteAllGroupBeSiteBySiteId(@RequestParam String siteId);

    @PostMapping("saveRosterMode")
    @ApiOperation("保存站点名单模式")
    @ApiOperationSupport(order = 2)
    ResponseResult<String> saveRosterMode(@RequestParam String siteId, @RequestParam Integer rosterMode);

    @PostMapping("findOrderRecordListBySiteIds")
    @ApiOperation("根据多个站点id和时间查询站点订单列表数据")
    @ApiOperationSupport(order = 3)
    ResponseResult<Map<String, List<OrderCountDto>>> findOrderRecordListBySiteIds(@RequestBody List<String> siteIds,
                                                                                  @RequestParam(required = false) String startTime,
                                                                                  @RequestParam(required = false) String endTime);

    @PostMapping("findOrderRecordListByPileCodes")
    @ApiOperation("根据多个电桩编号和时间查询站点订单列表数据")
    @ApiOperationSupport(order = 4)
    ResponseResult<Map<String, OrderCountDto>> findOrderRecordListByPileCodes(@RequestBody List<String> pileCodes,
                                                                              @RequestParam(required = false) String startTime,
                                                                              @RequestParam(required = false) String endTime);
}
