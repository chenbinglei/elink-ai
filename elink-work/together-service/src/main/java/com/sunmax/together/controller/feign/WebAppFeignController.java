package com.sunmax.together.controller.feign;

import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.together.*;
import com.sunmax.common.dto.webapp.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.InvoiceTitleVo;
import com.sunmax.common.vo.together.*;
import com.sunmax.common.vo.webapp.OrderInvoicesVo;
import com.sunmax.together.service.*;
import com.sunmax.together.service.operation.AppletUserService;
import com.sunmax.together.service.operation.InvoiceService;
import com.sunmax.together.service.operation.OrderRecordService;
import com.sunmax.together.service.operation.SiteInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/feign/swebapp")
@Api(tags = "提供给webapp服务的接口")
@ApiIgnore()
public class WebAppFeignController {

    @Autowired
    private WebAppFeignService webAppFeignService;

    @Autowired
    private AppletUserService appletUserService;

    @Autowired
    private OrderRecordService orderRecordService;

    @Autowired
    private SiteInfoService siteInfoService;
    @Autowired
    private InvoiceService invoiceService;

    @PostMapping(value = "generateOrderNum")
    @ApiOperation("生成订单号")
    @ApiOperationSupport(order = 1)
    public ResponseResult<String> generateOrderNum(@RequestParam String pileCode, @RequestParam String serialNumber) {
        return webAppFeignService.generateOrderNum(pileCode, serialNumber);
    }

    @PostMapping(value = "findAppletUserByIds")
    @ApiOperation("根据多个小程序用户id查询小程序用户信息")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Map<String, AppletUserInfoDto>> findAppletUserByIds(@RequestBody Set<String> appletUserIds) {
        return webAppFeignService.findAppletUserByIds(appletUserIds);
    }

    @PostMapping(value = "createPileOrder")
    @ApiOperation("创建电桩充放电订单")
    @ApiOperationSupport(order = 3)
    public ResponseResult<Void> createPileOrder(@RequestBody OrderChangeVo orderChangeVo) {
        return webAppFeignService.createPileOrder(orderChangeVo);
    }

    @PostMapping(value = "findSiteAccountListBySiteIds")
    @ApiOperation("根据多个站点id和类型查询站点账户信息")
    @ApiOperationSupport(order = 4)
    ResponseResult<Map<String, List<SiteAccountDto>>> findSiteAccountListBySiteIds(@RequestBody Set<String> siteIds, @RequestParam Integer type) {
        return webAppFeignService.findSiteAccountListBySiteIds(siteIds, type);
    }

    @PostMapping(value = "updatePileFailOrder")
    @ApiOperation("更新电桩充放电失败订单")
    @ApiOperationSupport(order = 5)
    public ResponseResult<Void> updatePileFailOrder(@RequestBody OrderUpdateVo orderUpdateVo) {
        return webAppFeignService.updatePileFailOrder(orderUpdateVo);
    }

    @PostMapping("findChargerRateListBySiteIds")
    @ApiOperation("根据多个站点id查询充放电费率列表")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteIds", value = "多个站点id", dataType = "String", required = true),
            @ApiImplicitParam(name = "priceType", value = "价格类型 1-充电 2-放电", paramType = "query", required = true),
            @ApiImplicitParam(name = "deviceType", value = "设备类型 1-直流 2-交流", paramType = "query", required = true)
    })
    public ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListBySiteIds(@RequestBody List<String> siteIds, @RequestParam Integer priceType, @RequestParam Integer deviceType) {
        return webAppFeignService.findChargerRateListBySiteIds(siteIds, priceType, deviceType);
    }

    @PostMapping("findDevicePriceById")
    @ApiOperation("根据设备id查询充放电和占桩价格信息")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true),
            @ApiImplicitParam(name = "priceType", value = "价格类型 1-充电 2-放电", paramType = "query", required = true)
    })
    public ResponseResult<ChargePriceInfoDto> findDevicePriceById(@RequestParam String deviceId, @RequestParam Integer priceType) {
        return webAppFeignService.findDevicePriceById(deviceId, priceType);
    }

    @PostMapping("updateAppletUser")
    @ApiOperation("编辑小程序用户")
    @ApiOperationSupport(order = 8)
    public ResponseResult<String> updateAppletUser(@RequestBody AppletUserChangeVo appletUserChangeVo) {
        return appletUserService.updateAppletUser(appletUserChangeVo);
    }

    @PostMapping("findOrderDetailByOrderNum")
    @ApiOperation("根据订单编号查询订单详情信息")
    @ApiOperationSupport(order = 9)
    public ResponseResult<OrderDetailDto> findOrderDetailByOrderNum(@RequestParam String orderNum) {
        return orderRecordService.findOrderDetailByOrderNum(orderNum);
    }

    @PostMapping("findOrderListByAccountData")
    @ApiOperation("根据账号数据查询指定类型订单列表")
    @ApiOperationSupport(order = 10)
    public ResponseResult<List<OrderInfoDto>> findOrderListByAccountData(@RequestBody List<String> accountDataList, @RequestParam(required = false) Integer runMode) {
        return orderRecordService.findOrderListByAccountData(accountDataList, runMode);
    }

    @PostMapping("findAppInHandOrderListByAppletUserId")
    @ApiOperation("根据小程序用户id查询进行中的订单列表")
    @ApiOperationSupport(order = 11)
    public ResponseResult<List<AppInHandOrderDto>> findAppInHandOrderListByAppletUserId(@RequestParam String appletUserId, @RequestParam(required = false) Integer orderType) {
        return orderRecordService.findAppInHandOrderListByAppletUserId(appletUserId, orderType);
    }

    @PostMapping("findChargerRateListByPriceInfoIds")
    @ApiOperation("根据多个费率id查询充放电费率列表")
    @ApiOperationSupport(order = 12)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "priceInfoIds", value = "多个费率id", dataType = "String", required = true)
    })
    public ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListByPriceInfoIds(@RequestBody List<String> priceInfoIds) {
        return siteInfoService.findChargerRateListByPriceInfoIds(priceInfoIds);
    }

    @PostMapping("findSiteWhiteRosterById")
    @ApiOperation("根据站点id查询白名单信息")
    @ApiOperationSupport(order = 13)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true),
            @ApiImplicitParam(name = "queryType", value = "查询类型 1-用户手机号 2-车辆vin码", paramType = "query"),
            @ApiImplicitParam(name = "queryData", value = "查询数据", paramType = "query")
    })
    public ResponseResult<SiteRosterInfoDto> findSiteWhiteRosterById(@RequestParam String siteId, @RequestParam(required = false) Integer queryType, @RequestParam(required = false) String queryData) {
        return siteInfoService.findSiteWhiteRosterById(siteId, queryType, queryData);
    }

    @PostMapping("queryAppletUserInfoByPhoneNum")
    @ApiOperation("根据小程序用户手机号查询用户信息")
    @ApiOperationSupport(order = 14)
    public ResponseResult<AppletUserInfoDto> queryAppletUserInfoByPhoneNum(@RequestParam String phoneNum) {
        return appletUserService.queryAppletUserInfoByPhoneNum(phoneNum);
    }

    @PostMapping(value = "updateUserDisWallet")
    @ApiOperation("更新用户V2G钱包")
    @ApiOperationSupport(order = 15)
    public ResponseResult<BigDecimal> updateUserDisWallet(@RequestBody UserDisWalletChangeVo userDisWalletVo) {
        return webAppFeignService.updateUserDisWallet(userDisWalletVo);
    }

    @PostMapping(value = "saveRefundRecord")
    @ApiOperation("保存退款记录")
    @ApiOperationSupport(order = 16)
    public ResponseResult<String> saveRefundRecord(@RequestBody RefundRecordChangeVo refundRecordChangeVo) {
        return webAppFeignService.saveRefundRecord(refundRecordChangeVo);
    }

    @PostMapping("findUserDisWalletListById")
    @ApiOperation("根据小程序用户id查询用户V2G钱包列表")
    @ApiOperationSupport(order = 17)
    @ApiImplicitParam(name = "appletUserId", value = "小程序用户唯一id", paramType = "query", required = true)
    public ResponseResult<List<UserDisWalletDto>> findUserDisWalletListById(@RequestParam String appletUserId) {
        return appletUserService.findUserDisWalletListById(appletUserId);
    }

    @PostMapping("submitAppletCancel")
    @ApiOperation("提交小程序用户注销申请")
    @ApiOperationSupport(order = 18)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appletUserId", value = "小程序用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "appletName", value = "小程序名称", paramType = "query", required = true)
    })
    public ResponseResult<String> submitAppletCancel(@RequestParam String appletUserId, @RequestParam String appletName) {
        return appletUserService.submitAppletCancel(appletUserId, appletName);
    }

    @PostMapping("updateAppletUserPhoneById")
    @ApiOperation("根据小程序用户id更新手机号")
    @ApiOperationSupport(order = 19)
    public ResponseResult<String> updateAppletUserPhoneById(@RequestParam String appletUserId, @RequestParam String phoneNum) {
        return appletUserService.updateAppletUserPhoneById(appletUserId, phoneNum);
    }

    @PostMapping("findOrderListBySiteIdS")
    @ApiOperation("根据多个站点id查询订单数据")
    @ApiOperationSupport(order = 20)
    public ResponseResult<List<OrderInfoDto>> findOrderListBySiteIdS(@RequestBody List<String> siteIdList, @RequestParam(required = false) String accountData, @RequestParam(required = false) Integer runMode) {
        return orderRecordService.findOrderListBySiteIdS(siteIdList, accountData, runMode);
    }

    @PostMapping("saveInvoiceTitle")
    @ApiOperation(value = "添加或编辑发票抬头")
    @ApiOperationSupport(order = 21)
    public ResponseResult<Void> saveInvoiceTitle(@RequestBody InvoiceTitleVo invoiceTitleVo) {
        return invoiceService.saveInvoiceTitle(invoiceTitleVo);
    }

    @PostMapping("deleteInvoiceTitleById")
    @ApiOperation(value = "根据主键id删除发票抬头")
    @ApiOperationSupport(order = 22)
    public ResponseResult<Void> deleteInvoiceTitleById(@RequestParam String id) {
        return invoiceService.deleteInvoiceTitleById(id);
    }

    @PostMapping("findInvoiceTitleList")
    @ApiOperation(value = "根据小程序用户id查询发票抬头列表")
    @ApiOperationSupport(order = 23)
    public ResponseResult<List<InvoiceTitleDto>> findInvoiceTitleList(@RequestParam String appletUserId) {
        return invoiceService.findInvoiceTitleList(appletUserId);
    }

    @PostMapping("findOrderAppShowList")
    @ApiOperation("根据查询条件查询订单展示列表")
    @ApiOperationSupport(order = 24)
    ResponseResult<List<OrderAppShowDto>> findOrderAppShowList(@RequestParam String appletUserId, @RequestParam(required = false) String queryDate) {
        return invoiceService.findOrderAppShowList(appletUserId, queryDate);
    }

    @PostMapping("applyInvoice")
    @ApiOperation(value = "申请开票")
    @ApiOperationSupport(order = 25)
    public ResponseResult<Void> applyInvoice(@RequestBody OrderInvoicesVo orderInvoicesVo) {
        return invoiceService.applyInvoice(orderInvoicesVo);
    }

    @PostMapping("findInvoiceRecordList")
    @ApiOperation(value = "根据小程序用户id查询开票记录列表")
    @ApiOperationSupport(order = 26)
    public ResponseResult<List<AppInvoiceListDto>> findInvoiceRecordList(@RequestParam String appletUserId) {
        return invoiceService.findInvoiceRecordList(appletUserId);
    }

    @PostMapping("findInvoiceDetailById")
    @ApiOperation(value = "根据发票申请单号查询开票详情数据")
    @ApiOperationSupport(order = 27)
    public ResponseResult<AppInvoiceDetailDto> findInvoiceDetailById(@RequestParam String id) {
        return invoiceService.findAppInvoiceDetailById(id);
    }

    @PostMapping("revokeInvoice")
    @ApiOperation(value = "撤销开票")
    @ApiOperationSupport(order = 28)
    public ResponseResult<Void> revokeInvoice(@RequestParam String id) {
        return invoiceService.revokeInvoice(id);
    }

}
