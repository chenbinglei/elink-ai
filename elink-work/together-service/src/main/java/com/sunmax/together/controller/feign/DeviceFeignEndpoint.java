package com.sunmax.together.controller.feign;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.operate.OrderCountDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.service.operation.AppletUserService;
import com.sunmax.together.service.DeviceFeignService;
import com.sunmax.together.service.operation.SiteInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Map;
import com.sunmax.common.feign.device.DeviceTogetherFeignClient;

/**
 * @Author: yqz
 * @注释: 提供给设备管理服务调用的远程接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/device")
@Tag(name = "提供给设备管理服务调用的远程接口")
@Hidden()
public class DeviceFeignEndpoint implements DeviceTogetherFeignClient {

    @Autowired
    private AppletUserService appletUserService;

    @Autowired
    private SiteInfoService siteInfoService;

    @Autowired
    private DeviceFeignService deviceFeignService;

    @PostMapping("deleteAllGroupBeSiteBySiteId")
    @Operation(summary = "根据站点id删除所有用户分组关联的站点数据")
    
    @Override
    public ResponseResult<String> deleteAllGroupBeSiteBySiteId(@RequestParam String siteId) {
        return appletUserService.deleteAllGroupBeSiteBySiteId(siteId);
    }

    @PostMapping("saveRosterMode")
    @Operation(summary = "保存站点名单模式")
    
    @Override
    public ResponseResult<String> saveRosterMode(@RequestParam String siteId, @RequestParam Integer rosterMode) {
        return siteInfoService.saveRosterMode(siteId, rosterMode);
    }

    @PostMapping("findOrderRecordListBySiteIds")
    @Operation(summary = "根据多个站点id和时间查询站点订单列表数据")
    
    public ResponseResult<Map<String, List<OrderCountDto>>> findOrderRecordListBySiteIds(@RequestBody List<String> siteIds,
                                                                                         @RequestParam(required = false) String startTime,
                                                                                         @RequestParam(required = false) String endTime) {
        return deviceFeignService.findOrderRecordListBySiteIds(siteIds, startTime, endTime);
    }

    @PostMapping("findOrderRecordListByPileCodes")
    @Operation(summary = "根据多个电桩编号和时间查询站点订单列表数据")
    
    public ResponseResult<Map<String, OrderCountDto>> findOrderRecordListByPileCodes(@RequestBody List<String> pileCodes,
                                                                                     @RequestParam(required = false) String startTime,
                                                                                     @RequestParam(required = false) String endTime) {
        return deviceFeignService.findOrderRecordListByPileCodes(pileCodes, startTime, endTime);
    }

}
