package com.sunmax.together.controller.operation;

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
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("siteInfo")
@Api(tags = "站点管理")
public class SiteInfoController {

    @Autowired
    private SiteInfoService siteInfoService;

    @PostMapping("findSiteInfoListByPage")
    @ApiOperation("分页查询站点信息列表")
    @ApiOperationSupport(order = 1)
    public ResponseResult<PageDto<SiteInfoListDto>> findSiteInfoListByPage(SiteInfoListQueryVo siteInfoListQueryVo, String userId) {
        return siteInfoService.findSiteInfoListByPage(siteInfoListQueryVo, userId);
    }

    @PostMapping("findGatewayStatusListById")
    @ApiOperation("根据站点id查询网关状态列表数据")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点唯一id", paramType = "query", required = true)
    })
    public ResponseResult<List<GatewayStatusDto>> findGatewayStatusListById(String siteId) {
        return siteInfoService.findGatewayStatusListById(siteId);
    }

    @PostMapping("findGatewayChildDeviceById")
    @ApiOperation("根据网关id查询网关子设备列表数据")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gatWayId", value = "网关唯一id", paramType = "query", required = true)
    })
    public ResponseResult<List<GatewayChildDeviceDto>> findGatewayChildDeviceById(String gatWayId) {
        return siteInfoService.findGatewayChildDeviceById(gatWayId);
    }

    @PostMapping("saveGatWayPlatformInfo")
    @ApiOperation("添加网关关联平台信息")
    @WebLog("站点管理-添加网关关联平台信息")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gatWayId", value = "网关唯一id", paramType = "query", required = true),
            @ApiImplicitParam(name = "gatWayCode", value = "网关编码", paramType = "query", required = true),
            @ApiImplicitParam(name = "platformIds", value = "关联平台id(多个以逗号分割,空值代表删除所有)", paramType = "query")
    })
    public ResponseResult<String> saveGatWayPlatformInfo(String gatWayId, String gatWayCode, String platformIds) {
        return siteInfoService.saveGatWayPlatformInfo(gatWayId, gatWayCode, platformIds);
    }

    @PostMapping("findGatWayPlatformInfo")
    @ApiOperation("根据网关id查询关联所有平台信息")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gatWayId", value = "网关唯一id", paramType = "query", required = true),
            @ApiImplicitParam(name = "gatWayCode", value = "网关编码", paramType = "query", required = true)
    })
    public ResponseResult<List<GatWayPlatformDto>> findGatWayPlatformInfo(String gatWayId, String gatWayCode) {
        return siteInfoService.findGatWayPlatformInfo(gatWayId, gatWayCode);
    }

    @PostMapping("saveChargerPriceInfo")
    @ApiOperation("添加充放电价格信息")
    @WebLog("站点管理-添加充放电价格信息")
    @ApiOperationSupport(order = 7)
    public ResponseResult<String> saveChargerPriceInfo(ChargerPriceInfoChangeVo chargerPriceInfoChangeVo) {
        return siteInfoService.saveChargerPriceInfo(chargerPriceInfoChangeVo);
    }

    @PostMapping("findFixPriceRecordList")
    @ApiOperation("根据站点id查询定价记录和生效中的价格配置")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true),
            @ApiImplicitParam(name = "priceType", value = "价格类型 1-充电 2-放电", paramType = "query", required = true)
    })
    public ResponseResult<ChargerPriceInfoListDto> findFixPriceRecordList(String siteId, Integer priceType) {
        return siteInfoService.findFixPriceRecordList(siteId, priceType);
    }

    @PostMapping("findPriceDetailsById")
    @ApiOperation("根据价格id查询价格详情")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pirceId", value = "价格id", paramType = "query", required = true)
    })
    public ResponseResult<ChargerPriceDetailsDto> findPriceDetailsById(String pirceId) {
        return siteInfoService.findPriceDetailsById(pirceId);
    }

    @PostMapping("deletePriceInfoById")
    @ApiOperation("根据价格id取消待生效价格信息")
    @WebLog("站点管理-根据价格id取消待生效价格信息")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pirceId", value = "价格id", paramType = "query", required = true)
    })
    public ResponseResult<String> deletePriceInfoById(String pirceId) {
        return siteInfoService.deletePriceInfoById(pirceId);
    }

    @PostMapping("saveOccupyPilePriceInfo")
    @ApiOperation("添加修改占桩价格信息")
    @WebLog("站点管理-添加或修改占桩价格信息")
    @ApiOperationSupport(order = 11)
    public ResponseResult<String> saveOccupyPilePriceInfo(OccupyPilePriceChangeVo occupyPilePriceChangeVo) {
        return siteInfoService.saveOccupyPilePriceInfo(occupyPilePriceChangeVo);
    }

    @PostMapping("findOccupyPilePriceInfoById")
    @ApiOperation("根据站点id查询占桩价格信息")
    @ApiOperationSupport(order = 12)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    })
    public ResponseResult<OccupyPilePriceDto> findOccupyPilePriceInfoById(String siteId) {
        return siteInfoService.findOccupyPilePriceInfoById(siteId);
    }

    @PostMapping("deleteOccupyPilePriceById")
    @ApiOperation("根据占桩id删除指定占桩费率信息")
    @WebLog("站点管理-根据占桩id删除指定占桩费率信息")
    @ApiOperationSupport(order = 13)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pirceId", value = "占桩价格id", paramType = "query", required = true)
    })
    public ResponseResult<String> deleteOccupyPilePriceById(String pirceId) {
        return siteInfoService.deleteOccupyPilePriceById(pirceId);
    }

    @PostMapping("saveWhiteRosterInfo")
    @ApiOperation("新增或编辑白名单信息")
    @WebLog("站点管理-新增或编辑白名单信息")
    @ApiOperationSupport(order = 14)
    public ResponseResult<String> saveWhiteRosterInfo(SiteWhiteRosterChangeVo siteWhiteRosterChangeVo) {
        return siteInfoService.saveWhiteRosterInfo(siteWhiteRosterChangeVo);
    }

    @PostMapping("findSiteWhiteRosterList")
    @ApiOperation("根据站点id查询白名单信息列表")
    @ApiOperationSupport(order = 15)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    })
    public ResponseResult<List<SiteWhiteRosterDto>> findSiteWhiteRosterList(String siteId) {
        return siteInfoService.findSiteWhiteRosterList(siteId);
    }

    @PostMapping("deleteSiteWhiteRosterById")
    @ApiOperation("根据白名单id删除相关信息")
    @WebLog("站点管理-根据白名单id删除相关信息")
    @ApiOperationSupport(order = 16)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "whiteId", value = "白名单id", paramType = "query", required = true)
    })
    public ResponseResult<String> deleteSiteWhiteRosterById(String whiteId) {
        return siteInfoService.deleteSiteWhiteRosterById(whiteId);
    }

    @PostMapping("pileStart")
    @ApiOperation("启动充放电")
    @WebLog("电桩-启动充放电")
    @ApiOperationSupport(order = 17)
    public ResponseResult<PileResultDto> pileStart(PileStartVo pileStartVo) {
        return siteInfoService.pileStart(pileStartVo);
    }

    @PostMapping("pileStop")
    @ApiOperation("停止充放电/停止预约")
    @WebLog("电桩-停止/取消预约充放电")
    @ApiOperationSupport(order = 18)
    public ResponseResult<PileResultDto> pileStop(PileStopVo pileStopVo) {
        return siteInfoService.pileStop(pileStopVo);
    }

    @PostMapping("powerCtrl")
    @ApiOperation("功率控制")
    @ApiOperationSupport(order = 19)
    public ResponseResult<PileResultDto> powerCtrl(PilePowerCtrlVo pilePowerCtrlVo) {
        return siteInfoService.pilePowerCtrl(pilePowerCtrlVo);
    }

    @PostMapping("rebootGateWey")
    @ApiOperation("网关重启")
    @WebLog("站点管理-网关重启")
    @ApiOperationSupport(order = 21)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceCode", value = "设备编码", paramType = "query", required = true)
    })
    public ResponseResult<String> rebootGateWey(String deviceCode) {
        return siteInfoService.rebootGateWey(deviceCode);
    }

    @PostMapping("updateSiteStatusById")
    @ApiOperation("根据站点id修改站点状态")
    @WebLog("站点管理-根据站点id修改站点状态")
    @ApiOperationSupport(order = 22)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "所属站点id", paramType = "query", required = true),
            @ApiImplicitParam(name = "siteStatus", value = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中", paramType = "query", required = true)
    })
    public ResponseResult<String> updateSiteStateById(String siteId, Integer siteStatus) {
        return siteInfoService.updateSiteStateById(siteId, siteStatus);
    }

    @PostMapping("saveOrUpdateRosterMode")
    @ApiOperation("保存或编辑白名单模式")
    @WebLog("站点管理-保存或编辑白名单模式")
    @ApiOperationSupport(order = 23)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "模式唯一id", paramType = "query"),
            @ApiImplicitParam(name = "siteId", value = "所属站点id", paramType = "query", required = true),
            @ApiImplicitParam(name = "rosterMode", value = "名单模式 1-仅白名单用户可用 2-白名单用户免费充电", paramType = "query", required = true)
    })
    public ResponseResult<String> saveOrUpdateRosterMode(String id, String siteId, Integer rosterMode) {
        return siteInfoService.saveOrUpdateRosterMode(id, siteId, rosterMode);
    }

    @PostMapping("findRosterModeBySiteId")
    @ApiOperation("查询站点白名单模式")
    @ApiOperationSupport(order = 24)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    })
    public ResponseResult<SiteRosterModeDto> findRosterModeBySiteId(String siteId) {
        return siteInfoService.findRosterModeBySiteId(siteId);
    }

    @PostMapping("findSiteInfoByUserId")
    @ApiOperation("根据登录用户id查询站点列表")
    @ApiOperationSupport(order = 25)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前登录用户id", paramType = "query", required = true),
            @ApiImplicitParam(name = "scenarioTypes", value = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电", paramType = "query")
    })
    public ResponseResult<List<SiteInfoDto>> findSiteInfoByUserId(String userId, Integer scenarioTypes) {
        return siteInfoService.findSiteInfoByUserId(userId, scenarioTypes);
    }

    @PostMapping("findPvSiteListByPage")
    @ApiOperation("分页查询光伏站点列表")
    @ApiOperationSupport(order = 26)
    public ResponseResult<PageDto<PvSiteListDto>> findPvSiteListByPage(String userId, PvSiteListQueryVo pvSiteListQueryVo) {
        return siteInfoService.findPvSiteListByPage(userId, pvSiteListQueryVo);
    }

    @PostMapping("updatePriceStateById")
    @ApiOperation("根据价格id修改价格状态")
    @WebLog("站点管理-根据价格id修改价格状态")
    @ApiOperationSupport(order = 27)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pirceId", value = "价格id", paramType = "query", required = true),
            @ApiImplicitParam(name = "priceState", value = "价格状态 1-生效中 2-待生效 3-已失效", paramType = "query", required = true)
    })
    public ResponseResult<String> updatePriceStateById(String pirceId, Integer priceState) {
        return siteInfoService.updatePriceStateById(pirceId, priceState);
    }

    @PostMapping("applyPriceInfoById")
    @ApiOperation("应用充放电价格信息到指定站点下")
    @WebLog("站点管理-应用充放电价格信息到指定站点下")
    @ApiOperationSupport(order = 28)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pirceId", value = "价格id", paramType = "query", required = true),
            @ApiImplicitParam(name = "siteIds", value = "多个站点id(['aaa','bbb'])", paramType = "query", required = true)
    })
    public ResponseResult<String> applyPriceInfoById(String pirceId, String siteIds) {
        return siteInfoService.applyPriceInfoById(pirceId, siteIds);
    }

    @PostMapping("pileReset")
    @ApiOperation("充电桩复位")
    @ApiOperationSupport(order = 29)
    public ResponseResult<Void> pileReset(PileResetVo pileResetVo) {
        return siteInfoService.pileReset(pileResetVo);
    }
}
