package com.sunmax.together.controller.operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.operate.OccupyPilePriceDto;
import com.sunmax.common.dto.operate.SiteWhiteRosterDto;
import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PilePowerCtrlVo;
import com.sunmax.common.vo.protocol.PileResetVo;
import com.sunmax.common.vo.protocol.PileStartVo;
import com.sunmax.common.vo.protocol.PileStopVo;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.operation.siteInfo.SiteRosterModeDto;
import com.sunmax.together.dto.operation.siteInfo.*;
import com.sunmax.together.service.operation.SiteInfoService;
import com.sunmax.together.vo.operation.siteInfo.PvSiteListQueryVo;
import com.sunmax.together.vo.operation.siteInfo.SiteInfoListQueryVo;
import com.sunmax.together.vo.operation.siteInfo.ChargerPriceInfoChangeVo;
import com.sunmax.together.vo.operation.siteInfo.OccupyPilePriceChangeVo;
import com.sunmax.together.vo.operation.siteInfo.SiteWhiteRosterChangeVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("siteInfo")
@Tag(name = "站点管理")
public class SiteInfoController {

    @Autowired
    private SiteInfoService siteInfoService;

    @PostMapping("findSiteInfoListByPage")
    @Operation(summary = "分页查询站点信息列表")
    
    public ResponseResult<PageDto<SiteInfoListDto>> findSiteInfoListByPage(SiteInfoListQueryVo siteInfoListQueryVo, String userId) {
        return siteInfoService.findSiteInfoListByPage(siteInfoListQueryVo, userId);
    }

    @PostMapping("findGatewayStatusListById")
    @Operation(summary = "根据站点id查询网关状态列表数据")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点唯一id")
    })
    public ResponseResult<List<GatewayStatusDto>> findGatewayStatusListById(String siteId) {
        return siteInfoService.findGatewayStatusListById(siteId);
    }

    @PostMapping("findGatewayChildDeviceById")
    @Operation(summary = "根据网关id查询网关子设备列表数据")
    
    @Parameters({
            @Parameter(name = "gatWayId", description = "网关唯一id")
    })
    public ResponseResult<List<GatewayChildDeviceDto>> findGatewayChildDeviceById(String gatWayId) {
        return siteInfoService.findGatewayChildDeviceById(gatWayId);
    }

    @PostMapping("saveGatWayPlatformInfo")
    @Operation(summary = "添加网关关联平台信息")
    @WebLog("站点管理-添加网关关联平台信息")
    
    @Parameters({
            @Parameter(name = "gatWayId", description = "网关唯一id"),
            @Parameter(name = "gatWayCode", description = "网关编码"),
            @Parameter(name = "platformIds", description = "关联平台id(多个以逗号分割,空值代表删除所有)")
    })
    public ResponseResult<String> saveGatWayPlatformInfo(String gatWayId, String gatWayCode, String platformIds) {
        return siteInfoService.saveGatWayPlatformInfo(gatWayId, gatWayCode, platformIds);
    }

    @PostMapping("findGatWayPlatformInfo")
    @Operation(summary = "根据网关id查询关联所有平台信息")
    
    @Parameters({
            @Parameter(name = "gatWayId", description = "网关唯一id"),
            @Parameter(name = "gatWayCode", description = "网关编码")
    })
    public ResponseResult<List<GatWayPlatformDto>> findGatWayPlatformInfo(String gatWayId, String gatWayCode) {
        return siteInfoService.findGatWayPlatformInfo(gatWayId, gatWayCode);
    }

    @PostMapping("saveChargerPriceInfo")
    @Operation(summary = "添加充放电价格信息")
    @WebLog("站点管理-添加充放电价格信息")
    
    public ResponseResult<String> saveChargerPriceInfo(ChargerPriceInfoChangeVo chargerPriceInfoChangeVo) {
        return siteInfoService.saveChargerPriceInfo(chargerPriceInfoChangeVo);
    }

    @PostMapping("findFixPriceRecordList")
    @Operation(summary = "根据站点id查询定价记录和生效中的价格配置")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "priceType", description = "价格类型 1-充电 2-放电")
    })
    public ResponseResult<ChargerPriceInfoListDto> findFixPriceRecordList(String siteId, Integer priceType) {
        return siteInfoService.findFixPriceRecordList(siteId, priceType);
    }

    @PostMapping("findPriceDetailsById")
    @Operation(summary = "根据价格id查询价格详情")
    
    @Parameters({
            @Parameter(name = "pirceId", description = "价格id")
    })
    public ResponseResult<ChargerPriceDetailsDto> findPriceDetailsById(String pirceId) {
        return siteInfoService.findPriceDetailsById(pirceId);
    }

    @PostMapping("deletePriceInfoById")
    @Operation(summary = "根据价格id取消待生效价格信息")
    @WebLog("站点管理-根据价格id取消待生效价格信息")
    
    @Parameters({
            @Parameter(name = "pirceId", description = "价格id")
    })
    public ResponseResult<String> deletePriceInfoById(String pirceId) {
        return siteInfoService.deletePriceInfoById(pirceId);
    }

    @PostMapping("saveOccupyPilePriceInfo")
    @Operation(summary = "添加修改占桩价格信息")
    @WebLog("站点管理-添加或修改占桩价格信息")
    
    public ResponseResult<String> saveOccupyPilePriceInfo(OccupyPilePriceChangeVo occupyPilePriceChangeVo) {
        return siteInfoService.saveOccupyPilePriceInfo(occupyPilePriceChangeVo);
    }

    @PostMapping("findOccupyPilePriceInfoById")
    @Operation(summary = "根据站点id查询占桩价格信息")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<OccupyPilePriceDto> findOccupyPilePriceInfoById(String siteId) {
        return siteInfoService.findOccupyPilePriceInfoById(siteId);
    }

    @PostMapping("deleteOccupyPilePriceById")
    @Operation(summary = "根据占桩id删除指定占桩费率信息")
    @WebLog("站点管理-根据占桩id删除指定占桩费率信息")
    
    @Parameters({
            @Parameter(name = "pirceId", description = "占桩价格id")
    })
    public ResponseResult<String> deleteOccupyPilePriceById(String pirceId) {
        return siteInfoService.deleteOccupyPilePriceById(pirceId);
    }

    @PostMapping("saveWhiteRosterInfo")
    @Operation(summary = "新增或编辑白名单信息")
    @WebLog("站点管理-新增或编辑白名单信息")
    
    public ResponseResult<String> saveWhiteRosterInfo(SiteWhiteRosterChangeVo siteWhiteRosterChangeVo) {
        return siteInfoService.saveWhiteRosterInfo(siteWhiteRosterChangeVo);
    }

    @PostMapping("findSiteWhiteRosterList")
    @Operation(summary = "根据站点id查询白名单信息列表")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<List<SiteWhiteRosterDto>> findSiteWhiteRosterList(String siteId) {
        return siteInfoService.findSiteWhiteRosterList(siteId);
    }

    @PostMapping("deleteSiteWhiteRosterById")
    @Operation(summary = "根据白名单id删除相关信息")
    @WebLog("站点管理-根据白名单id删除相关信息")
    
    @Parameters({
            @Parameter(name = "whiteId", description = "白名单id")
    })
    public ResponseResult<String> deleteSiteWhiteRosterById(String whiteId) {
        return siteInfoService.deleteSiteWhiteRosterById(whiteId);
    }

    @PostMapping("pileStart")
    @Operation(summary = "启动充放电")
    @WebLog("电桩-启动充放电")
    
    public ResponseResult<PileResultDto> pileStart(PileStartVo pileStartVo) {
        return siteInfoService.pileStart(pileStartVo);
    }

    @PostMapping("pileStop")
    @Operation(summary = "停止充放电/停止预约")
    @WebLog("电桩-停止/取消预约充放电")
    
    public ResponseResult<PileResultDto> pileStop(PileStopVo pileStopVo) {
        return siteInfoService.pileStop(pileStopVo);
    }

    @PostMapping("powerCtrl")
    @Operation(summary = "功率控制")
    
    public ResponseResult<PileResultDto> powerCtrl(PilePowerCtrlVo pilePowerCtrlVo) {
        return siteInfoService.pilePowerCtrl(pilePowerCtrlVo);
    }

    @PostMapping("rebootGateWey")
    @Operation(summary = "网关重启")
    @WebLog("站点管理-网关重启")
    
    @Parameters({
            @Parameter(name = "deviceCode", description = "设备编码")
    })
    public ResponseResult<String> rebootGateWey(String deviceCode) {
        return siteInfoService.rebootGateWey(deviceCode);
    }

    @PostMapping("updateSiteStatusById")
    @Operation(summary = "根据站点id修改站点状态")
    @WebLog("站点管理-根据站点id修改站点状态")
    
    @Parameters({
            @Parameter(name = "siteId", description = "所属站点id"),
            @Parameter(name = "siteStatus", description = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中")
    })
    public ResponseResult<String> updateSiteStateById(String siteId, Integer siteStatus) {
        return siteInfoService.updateSiteStateById(siteId, siteStatus);
    }

    @PostMapping("saveOrUpdateRosterMode")
    @Operation(summary = "保存或编辑白名单模式")
    @WebLog("站点管理-保存或编辑白名单模式")
    
    @Parameters({
            @Parameter(name = "id", description = "模式唯一id"),
            @Parameter(name = "siteId", description = "所属站点id"),
            @Parameter(name = "rosterMode", description = "名单模式 1-仅白名单用户可用 2-白名单用户免费充电")
    })
    public ResponseResult<String> saveOrUpdateRosterMode(String id, String siteId, Integer rosterMode) {
        return siteInfoService.saveOrUpdateRosterMode(id, siteId, rosterMode);
    }

    @PostMapping("findRosterModeBySiteId")
    @Operation(summary = "查询站点白名单模式")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<SiteRosterModeDto> findRosterModeBySiteId(String siteId) {
        return siteInfoService.findRosterModeBySiteId(siteId);
    }

    @PostMapping("findSiteInfoByUserId")
    @Operation(summary = "根据登录用户id查询站点列表")
    
    @Parameters({
            @Parameter(name = "userId", description = "当前登录用户id"),
            @Parameter(name = "scenarioTypes", description = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电")
    })
    public ResponseResult<List<SiteInfoDto>> findSiteInfoByUserId(String userId, Integer scenarioTypes) {
        return siteInfoService.findSiteInfoByUserId(userId, scenarioTypes);
    }

    @PostMapping("findPvSiteListByPage")
    @Operation(summary = "分页查询光伏站点列表")
    
    public ResponseResult<PageDto<PvSiteListDto>> findPvSiteListByPage(String userId, PvSiteListQueryVo pvSiteListQueryVo) {
        return siteInfoService.findPvSiteListByPage(userId, pvSiteListQueryVo);
    }

    @PostMapping("updatePriceStateById")
    @Operation(summary = "根据价格id修改价格状态")
    @WebLog("站点管理-根据价格id修改价格状态")
    
    @Parameters({
            @Parameter(name = "pirceId", description = "价格id"),
            @Parameter(name = "priceState", description = "价格状态 1-生效中 2-待生效 3-已失效")
    })
    public ResponseResult<String> updatePriceStateById(String pirceId, Integer priceState) {
        return siteInfoService.updatePriceStateById(pirceId, priceState);
    }

    @PostMapping("applyPriceInfoById")
    @Operation(summary = "应用充放电价格信息到指定站点下")
    @WebLog("站点管理-应用充放电价格信息到指定站点下")
    
    @Parameters({
            @Parameter(name = "pirceId", description = "价格id"),
            @Parameter(name = "siteIds", description = "多个站点id(['aaa','bbb'])")
    })
    public ResponseResult<String> applyPriceInfoById(String pirceId, String siteIds) {
        return siteInfoService.applyPriceInfoById(pirceId, siteIds);
    }

    @PostMapping("pileReset")
    @Operation(summary = "充电桩复位")
    
    public ResponseResult<Void> pileReset(PileResetVo pileResetVo) {
        return siteInfoService.pileReset(pileResetVo);
    }
}
