package com.sunmax.together.controller.feign;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sunmax.common.feign.webapp.WebAppTogetherFeignClient;

@RestController
@CrossOrigin
@RequestMapping("/feign/swebapp")
@Tag(name = "提供给webapp服务的接口")
@Hidden()
public class WebAppFeignEndpoint implements WebAppTogetherFeignClient {

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
    @Operation(summary = "生成订单号")
    
    @Override
    public ResponseResult<String> generateOrderNum(@RequestParam String pileCode, @RequestParam String serialNumber) {
        return webAppFeignService.generateOrderNum(pileCode, serialNumber);
    }

    @PostMapping(value = "findAppletUserByIds")
    @Operation(summary = "根据多个小程序用户id查询小程序用户信息")
    
    public ResponseResult<Map<String, AppletUserInfoDto>> findAppletUserByIds(@RequestBody Set<String> appletUserIds) {
        return webAppFeignService.findAppletUserByIds(appletUserIds);
    }

    @PostMapping(value = "createPileOrder")
    @Operation(summary = "创建电桩充放电订单")
    
    @Override
    public ResponseResult<Void> createPileOrder(@RequestBody OrderChangeVo orderChangeVo) {
        return webAppFeignService.createPileOrder(orderChangeVo);
    }

    @PostMapping(value = "findSiteAccountListBySiteIds")
    @Operation(summary = "根据多个站点id和类型查询站点账户信息")
    
    @Override
    public ResponseResult<Map<String, List<SiteAccountDto>>> findSiteAccountListBySiteIds(@RequestBody Set<String> siteIds, @RequestParam Integer type) {
        return webAppFeignService.findSiteAccountListBySiteIds(siteIds, type);
    }

    @PostMapping(value = "updatePileFailOrder")
    @Operation(summary = "更新电桩充放电失败订单")
    
    @Override
    public ResponseResult<Void> updatePileFailOrder(@RequestBody OrderUpdateVo orderUpdateVo) {
        return webAppFeignService.updatePileFailOrder(orderUpdateVo);
    }

    @PostMapping("findChargerRateListBySiteIds")
    @Operation(summary = "根据多个站点id查询充放电费率列表")
    
    @Parameters({
            @Parameter(name = "siteIds", description = "多个站点id"),
            @Parameter(name = "priceType", description = "价格类型 1-充电 2-放电"),
            @Parameter(name = "deviceType", description = "设备类型 1-直流 2-交流")
    })
    public ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListBySiteIds(@RequestBody List<String> siteIds, @RequestParam Integer priceType, @RequestParam Integer deviceType) {
        return webAppFeignService.findChargerRateListBySiteIds(siteIds, priceType, deviceType);
    }

    @PostMapping("findDevicePriceById")
    @Operation(summary = "根据设备id查询充放电和占桩价格信息")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id"),
            @Parameter(name = "priceType", description = "价格类型 1-充电 2-放电")
    })
    @Override
    public ResponseResult<ChargePriceInfoDto> findDevicePriceById(@RequestParam String deviceId, @RequestParam Integer priceType) {
        return webAppFeignService.findDevicePriceById(deviceId, priceType);
    }

    @PostMapping("updateAppletUser")
    @Operation(summary = "编辑小程序用户")
    
    @Override
    public ResponseResult<String> updateAppletUser(@RequestBody AppletUserChangeVo appletUserChangeVo) {
        return appletUserService.updateAppletUser(appletUserChangeVo);
    }

    @PostMapping("findOrderDetailByOrderNum")
    @Operation(summary = "根据订单编号查询订单详情信息")
    
    @Override
    public ResponseResult<OrderDetailDto> findOrderDetailByOrderNum(@RequestParam String orderNum) {
        return orderRecordService.findOrderDetailByOrderNum(orderNum);
    }

    @PostMapping("findOrderListByAccountData")
    @Operation(summary = "根据账号数据查询指定类型订单列表")
    
    public ResponseResult<List<OrderInfoDto>> findOrderListByAccountData(@RequestBody List<String> accountDataList, @RequestParam(required = false) Integer runMode) {
        return orderRecordService.findOrderListByAccountData(accountDataList, runMode);
    }

    @PostMapping("findAppInHandOrderListByAppletUserId")
    @Operation(summary = "根据小程序用户id查询进行中的订单列表")
    
    public ResponseResult<List<AppInHandOrderDto>> findAppInHandOrderListByAppletUserId(@RequestParam String appletUserId, @RequestParam(required = false) Integer orderType) {
        return orderRecordService.findAppInHandOrderListByAppletUserId(appletUserId, orderType);
    }

    @PostMapping("findChargerRateListByPriceInfoIds")
    @Operation(summary = "根据多个费率id查询充放电费率列表")
    
    @Parameters({
            @Parameter(name = "priceInfoIds", description = "多个费率id")
    })
    public ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListByPriceInfoIds(@RequestBody List<String> priceInfoIds) {
        return siteInfoService.findChargerRateListByPriceInfoIds(priceInfoIds);
    }

    @PostMapping("findSiteWhiteRosterById")
    @Operation(summary = "根据站点id查询白名单信息")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "queryType", description = "查询类型 1-用户手机号 2-车辆vin码"),
            @Parameter(name = "queryData", description = "查询数据")
    })
    @Override
    public ResponseResult<SiteRosterInfoDto> findSiteWhiteRosterById(@RequestParam String siteId, @RequestParam(required = false) Integer queryType, @RequestParam(required = false) String queryData) {
        return siteInfoService.findSiteWhiteRosterById(siteId, queryType, queryData);
    }

    @PostMapping("queryAppletUserInfoByPhoneNum")
    @Operation(summary = "根据小程序用户手机号查询用户信息")
    
    @Override
    public ResponseResult<AppletUserInfoDto> queryAppletUserInfoByPhoneNum(@RequestParam String phoneNum) {
        return appletUserService.queryAppletUserInfoByPhoneNum(phoneNum);
    }

    @PostMapping(value = "updateUserDisWallet")
    @Operation(summary = "更新用户V2G钱包")
    
    @Override
    public ResponseResult<BigDecimal> updateUserDisWallet(@RequestBody UserDisWalletChangeVo userDisWalletVo) {
        return webAppFeignService.updateUserDisWallet(userDisWalletVo);
    }

    @PostMapping(value = "saveRefundRecord")
    @Operation(summary = "保存退款记录")
    
    @Override
    public ResponseResult<String> saveRefundRecord(@RequestBody RefundRecordChangeVo refundRecordChangeVo) {
        return webAppFeignService.saveRefundRecord(refundRecordChangeVo);
    }

    @PostMapping("findUserDisWalletListById")
    @Operation(summary = "根据小程序用户id查询用户V2G钱包列表")
    
    @Parameter(name = "appletUserId", description = "小程序用户唯一id")
    public ResponseResult<List<UserDisWalletDto>> findUserDisWalletListById(@RequestParam String appletUserId) {
        return appletUserService.findUserDisWalletListById(appletUserId);
    }

    @PostMapping("submitAppletCancel")
    @Operation(summary = "提交小程序用户注销申请")
    
    @Parameters({
            @Parameter(name = "appletUserId", description = "小程序用户id"),
            @Parameter(name = "appletName", description = "小程序名称")
    })
    @Override
    public ResponseResult<String> submitAppletCancel(@RequestParam String appletUserId, @RequestParam String appletName) {
        return appletUserService.submitAppletCancel(appletUserId, appletName);
    }

    @PostMapping("updateAppletUserPhoneById")
    @Operation(summary = "根据小程序用户id更新手机号")
    
    @Override
    public ResponseResult<String> updateAppletUserPhoneById(@RequestParam String appletUserId, @RequestParam String phoneNum) {
        return appletUserService.updateAppletUserPhoneById(appletUserId, phoneNum);
    }

    @PostMapping("findOrderListBySiteIdS")
    @Operation(summary = "根据多个站点id查询订单数据")
    
    public ResponseResult<List<OrderInfoDto>> findOrderListBySiteIdS(@RequestBody List<String> siteIdList, @RequestParam(required = false) String accountData, @RequestParam(required = false) Integer runMode) {
        return orderRecordService.findOrderListBySiteIdS(siteIdList, accountData, runMode);
    }

    @PostMapping("saveInvoiceTitle")
    @Operation(summary = "添加或编辑发票抬头")
    
    @Override
    public ResponseResult<Void> saveInvoiceTitle(@RequestBody InvoiceTitleVo invoiceTitleVo) {
        return invoiceService.saveInvoiceTitle(invoiceTitleVo);
    }

    @PostMapping("deleteInvoiceTitleById")
    @Operation(summary = "根据主键id删除发票抬头")
    
    @Override
    public ResponseResult<Void> deleteInvoiceTitleById(@RequestParam String id) {
        return invoiceService.deleteInvoiceTitleById(id);
    }

    @PostMapping("findInvoiceTitleList")
    @Operation(summary = "根据小程序用户id查询发票抬头列表")
    
    public ResponseResult<List<InvoiceTitleDto>> findInvoiceTitleList(@RequestParam String appletUserId) {
        return invoiceService.findInvoiceTitleList(appletUserId);
    }

    @PostMapping("findOrderAppShowList")
    @Operation(summary = "根据查询条件查询订单展示列表")
    
    @Override
    public ResponseResult<List<OrderAppShowDto>> findOrderAppShowList(@RequestParam String appletUserId, @RequestParam(required = false) String queryDate) {
        return invoiceService.findOrderAppShowList(appletUserId, queryDate);
    }

    @PostMapping("applyInvoice")
    @Operation(summary = "申请开票")
    
    @Override
    public ResponseResult<Void> applyInvoice(@RequestBody OrderInvoicesVo orderInvoicesVo) {
        return invoiceService.applyInvoice(orderInvoicesVo);
    }

    @PostMapping("findInvoiceRecordList")
    @Operation(summary = "根据小程序用户id查询开票记录列表")
    
    public ResponseResult<List<AppInvoiceListDto>> findInvoiceRecordList(@RequestParam String appletUserId) {
        return invoiceService.findInvoiceRecordList(appletUserId);
    }

    @PostMapping("findInvoiceDetailById")
    @Operation(summary = "根据发票申请单号查询开票详情数据")
    
    @Override
    public ResponseResult<AppInvoiceDetailDto> findInvoiceDetailById(@RequestParam String id) {
        return invoiceService.findAppInvoiceDetailById(id);
    }

    @PostMapping("revokeInvoice")
    @Operation(summary = "撤销开票")
    
    @Override
    public ResponseResult<Void> revokeInvoice(@RequestParam String id) {
        return invoiceService.revokeInvoice(id);
    }

}
