package com.sunmax.together.service.operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.operate.OccupyPilePriceDto;
import com.sunmax.common.dto.operate.OccupyPileRateDto;
import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.dto.together.SiteRosterInfoDto;
import com.sunmax.common.dto.together.SystemDeviceListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PilePowerCtrlVo;
import com.sunmax.common.vo.protocol.PileResetVo;
import com.sunmax.common.vo.protocol.PileStartVo;
import com.sunmax.common.vo.protocol.PileStopVo;
import com.sunmax.common.vo.protocol.mqtt.web.response.EventRateReqPublicVo;
import com.sunmax.together.dto.operation.siteInfo.SiteRosterModeDto;
import com.sunmax.together.dto.operation.siteInfo.*;
import com.sunmax.together.vo.operation.siteInfo.PvSiteListQueryVo;
import com.sunmax.together.vo.operation.siteInfo.SiteInfoListQueryVo;
import com.sunmax.together.vo.operation.siteInfo.ChargerPriceInfoChangeVo;
import com.sunmax.together.vo.operation.siteInfo.OccupyPilePriceChangeVo;
import com.sunmax.together.vo.operation.siteInfo.SiteWhiteRosterChangeVo;

import java.util.List;
import java.util.Map;

public interface SiteInfoService {

    /**
     * 分页查询站点信息列表
     *
     * @param siteInfoListQueryVo
     * @param userId
     * @return
     */
    ResponseResult<PageDto<SiteInfoListDto>> findSiteInfoListByPage(SiteInfoListQueryVo siteInfoListQueryVo, String userId);

    /**
     * 根据站点id查询网关状态列表数据
     * @param siteId
     * @return
     */
    ResponseResult<List<GatewayStatusDto>> findGatewayStatusListById(String siteId);

    /**
     * 根据网关id查询网关子设备列表数据
     * @param gatWayId
     * @return
     */
    ResponseResult<List<GatewayChildDeviceDto>> findGatewayChildDeviceById(String gatWayId);

    /**
     * 添加网关关联平台信息
     * @param gatWayId 网关唯一id
     * @param gatWayCode 网关编码
     * @param platformIds 关联平台id(多个以逗号分割,空值代表删除所有)
     * @return
     */
    ResponseResult<String> saveGatWayPlatformInfo(String gatWayId, String gatWayCode, String platformIds);

    /**
     * 根据网关id查询关联所有平台信息
     *
     * @param gatWayId
     * @param gatWayCode
     * @return
     */
    ResponseResult<List<GatWayPlatformDto>> findGatWayPlatformInfo(String gatWayId, String gatWayCode);

    /**
     * 添加充放电价格信息
     * @param chargerPriceInfoChangeVo
     * @return
     */
    ResponseResult<String> saveChargerPriceInfo(ChargerPriceInfoChangeVo chargerPriceInfoChangeVo);

    /**
     * 根据站点id查询定价记录和生效中的价格配置
     * @param siteId 站点id
     * @param priceType 价格类型 1-充电 2-放电
     * @return
     */
    ResponseResult<ChargerPriceInfoListDto> findFixPriceRecordList(String siteId, Integer priceType);

    /**
     * 根据价格id查询价格详情
     * @param pirceId
     * @return
     */
    ResponseResult<ChargerPriceDetailsDto> findPriceDetailsById(String pirceId);

    /**
     * 根据价格id取消待生效价格信息
     * @param pirceId
     * @return
     */
    ResponseResult<String> deletePriceInfoById(String pirceId);

    /**
     * 添加占桩价格信息
     * @param occupyPilePriceChangeVo
     * @return
     */
    ResponseResult<String> saveOccupyPilePriceInfo(OccupyPilePriceChangeVo occupyPilePriceChangeVo);

    /**
     * 根据站点id查询占桩价格信息
     * @param siteId
     * @return
     */
    ResponseResult<OccupyPilePriceDto> findOccupyPilePriceInfoById(String siteId);

    /**
     * 根据占桩id删除指定占桩费率信息
     * @param pirceId
     * @return
     */
    ResponseResult<String> deleteOccupyPilePriceById(String pirceId);

    /**
     * 新增或编辑白名单信息
     * @param siteWhiteRosterChangeVo
     * @return
     */
    ResponseResult<String> saveWhiteRosterInfo(SiteWhiteRosterChangeVo siteWhiteRosterChangeVo);

    /**
     * 根据站点id查询白名单信息列表
     * @param siteId
     * @return
     */
    ResponseResult<List<com.sunmax.common.dto.operate.SiteWhiteRosterDto>> findSiteWhiteRosterList(String siteId);

    /**
     * 根据白名单id删除相关信息
     * @param whiteId
     * @return
     */
    ResponseResult<String> deleteSiteWhiteRosterById(String whiteId);

    /**
     * 校验账号是否在白名单中
     * @param pileCode
     * @param accountCode
     * @return
     */
    ResponseResult<Boolean> checkAccountCode(String pileCode, String accountCode);

    /**
     * 启动充放电
     * @param pileStartVo
     * @return
     */
    ResponseResult<PileResultDto> pileStart(PileStartVo pileStartVo);

    /**
     * 停止充放电/停止预约
     * @param pileStopVo
     * @return
     */
    ResponseResult<PileResultDto> pileStop(PileStopVo pileStopVo);

    /**
     * 功率控制
     * @param pilePowerCtrlVo
     * @return
     */
    ResponseResult<PileResultDto> pilePowerCtrl(PilePowerCtrlVo pilePowerCtrlVo);

    /**
     * 网关重启
     * @param deviceCode
     * @return
     */
    ResponseResult<String> rebootGateWey(String deviceCode);

    /**
     * 根据站点id修改站点状态
     * @param siteId 所属站点id
     * @param siteStatus 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     * @return
     */
    ResponseResult<String> updateSiteStateById(String siteId, Integer siteStatus);
    /**
     * 根据多个站点id查询充放电费率列表
     * @param siteIds 多个站点id
     * @param priceType 价格类型 1-充电 2-放电
     * @return 站点id -> 充放电费率列表
     */
    ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListBySiteIds(List<String> siteIds, Integer priceType);

    /**
     * 根据多个电桩编码查询占桩计费数据
     * @param pileCodes
     * @return
     */
    ResponseResult<Map<String, OccupyPileRateDto>> findOccupyPileRateByPileCodes(List<String> pileCodes);

    /**
     * 根据多个电桩编码查询充放电费率列表
     *
     * @param pileCodes
     * @param priceType
     * @return
     */
    ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListByPileCodes(List<String> pileCodes, Integer priceType);

    /**
     * 根据电桩编码查询站点充放电费率
     * @param pileCodes
     * @return 电桩编码 -> 费率信息
     */
    ResponseResult<Map<String, EventRateReqPublicVo>> findSiteRateInfoByPileCodes(List<String> pileCodes);

    /**
     * 保存或编辑白名单模式
     * @param id  白名单模式唯一id
     * @param siteId 站点id
     * @param rosterMode 名单模式 1-仅白名单用户可用 2-白名单用户免费充电
     * @return
     */
    ResponseResult<String> saveOrUpdateRosterMode(String id, String siteId, Integer rosterMode);

    /**
     * 查询站点白名单模式
     * @param siteId
     * @return
     */
    ResponseResult<SiteRosterModeDto> findRosterModeBySiteId(String siteId);

    /**
     * 根据多个费率id查询充放电费率列表
     * @param priceInfoIds 多个费率id
     * @return 费率id -> 充放电费率列表
     */
    ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListByPriceInfoIds(List<String> priceInfoIds);

    /**
     * 根据站点id查询白名单信息
     * @param siteId 站点id
     * @param queryType 查询类型 1-用户手机号 2-车辆vin码(可为空，和查询数据参数联动)
     * @param queryData 查询数据(可为空，和查询类型参数联动)
     * @return
     */
    ResponseResult<SiteRosterInfoDto> findSiteWhiteRosterById(String siteId, Integer queryType, String queryData);

    /**
     * 保存站点名单模式
     * @param siteId 站点id
     * @param rosterMode 名单模式 1-仅白名单用户可用 2-白名单用户免费充电
     * @return
     */
    ResponseResult<String> saveRosterMode(String siteId, Integer rosterMode);

    /**
     * 查询系统设备列表
     * @param siteId 站点唯一id
     * @return
     */
    ResponseResult<SystemDeviceListDto> querySystemDeviceList(String siteId);

    /**
     * 根据登录用户id查询站点列表
     * @param userId
     * @param scenarioTypes 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电
     * @return
     */
    ResponseResult<List<SiteInfoDto>> findSiteInfoByUserId(String userId, Integer scenarioTypes);

    /**
     * 分页查询光伏站点列表
     * @param userId
     * @param pvSiteListQueryVo
     * @return
     */
    ResponseResult<PageDto<PvSiteListDto>> findPvSiteListByPage(String userId, PvSiteListQueryVo pvSiteListQueryVo);

    /**
     * 根据价格id修改价格状态
     * @param pirceId 价格id
     * @param priceState 价格状态 1-生效中 2-待生效 3-已失效
     * @return
     */
    ResponseResult<String> updatePriceStateById(String pirceId, Integer priceState);

    /**
     * 应用充放电价格信息到指定站点下
     * @param pirceId
     * @param siteIds 多个站点id(['aaa','bbb'])
     * @return
     */
    ResponseResult<String> applyPriceInfoById(String pirceId, String siteIds);

    /**
     * 电桩复位
     *
     * @param pileResetVo 电桩复位参数
     * @return 复位状态码
     */
    ResponseResult<Void> pileReset(PileResetVo pileResetVo);

}
