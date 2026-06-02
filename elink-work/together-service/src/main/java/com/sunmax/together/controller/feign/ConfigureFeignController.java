package com.sunmax.together.controller.feign;

import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.operate.InterflowOrderRecordDto;
import com.sunmax.common.dto.operate.OrderRecordDto;
import com.sunmax.common.dto.together.SystemDeviceListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.service.operation.OrderRecordService;
import com.sunmax.together.service.operation.SiteInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/feign/configure")
@Api(tags = "提供给配置服务调用的远程接口")
@ApiIgnore()
public class ConfigureFeignController {

    @Autowired
    private OrderRecordService orderRecordService;

    @Autowired
    private SiteInfoService siteInfoService;

    @PostMapping("queryOrderRecordByOrderCode")
    @ApiOperation("根据订单号查询订单信息")
    @ApiOperationSupport(order = 1)
    public ResponseResult<InterflowOrderRecordDto> queryOrderRecordByOrderCode(@RequestParam String orderNum) {
        return orderRecordService.queryOrderRecordByOrderCode(orderNum);
    }

    @PostMapping("saveOrUpdateOrderRecord")
    @ApiOperation("保存或编辑订单记录信息")
    @ApiOperationSupport(order = 2)
    public ResponseResult<String> saveOrUpdateOrderRecord(@RequestBody InterflowOrderRecordDto interflowOrderRecordDto) {
        return orderRecordService.saveOrUpdateOrderRecord(interflowOrderRecordDto);
    }

    @PostMapping("queryOrderRecordByPileCodes")
    @ApiOperation("根据多个电桩编码查询订单记录数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<Map<String, List<OrderRecordDto>>> queryOrderRecordByPileCodes(@RequestBody List<String> pileCodeList,
                                                                                         @RequestParam(required = false) String startTime,
                                                                                         @RequestParam(required = false) String endTime) {
        return orderRecordService.queryOrderRecordByPileCodes(pileCodeList, startTime, endTime);
    }

    @PostMapping("findChargerRateListBySiteIds")
    @ApiOperation("根据多个站点id查询充放电费率列表")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteIds", value = "多个站点id", dataType = "String", required = true),
            @ApiImplicitParam(name = "priceType", value = "价格类型 1-充电 2-放电", paramType = "query", required = true)
    })
    public ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListBySiteIds(@RequestBody List<String> siteIds, @RequestParam Integer priceType) {
        return siteInfoService.findChargerRateListBySiteIds(siteIds, priceType);
    }

    @PostMapping("querySystemDeviceList")
    @ApiOperation("查询系统设备列表")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    })
    public ResponseResult<SystemDeviceListDto> querySystemDeviceList(@RequestParam String siteId) {
        return siteInfoService.querySystemDeviceList(siteId);
    }
}
