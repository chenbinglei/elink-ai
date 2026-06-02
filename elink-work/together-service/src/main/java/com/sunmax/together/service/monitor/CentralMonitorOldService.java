package com.sunmax.together.service.monitor;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.crontab.SystemVarDataDto;
import com.sunmax.common.dto.device.DeviceAlarmEventListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.VarNodeValueVo;
import com.sunmax.common.vo.together.FunctionValueVo;
import com.sunmax.together.dto.monitor.centralMonitorOld.*;
import com.sunmax.together.dto.monitor.assetOverview.PileFunCurveDto;
import com.sunmax.together.vo.monitor.centralMonitorOld.ElecCountQueryVo;

import java.util.List;
import java.util.Map;

public interface CentralMonitorOldService {

    /**
     * 查询光伏站点监测数据
     * @param siteId
     * @return
     */
    ResponseResult<PvSiteMonitorDto> findPvSiteMonitorData(String siteId);

    /**
     * 查询系统变量曲线数据
     *
     * @param varNodeValueVo
     * @param isCurrent 结束时间是否为当前时间 1-是
     * @return 设备/站点id -> 变量编码(包含x时间轴xAxisList) -> 数据
     */
    ResponseResult<Map<String, Map<String, Object>>> findSystemVarCurveData(VarNodeValueVo varNodeValueVo, Integer isCurrent);

    /**
     * 查询光伏逆变器列表
     * @param siteId
     * @return
     */
    ResponseResult<List<PvInverterListDto>> findPvInverterList(String siteId);

    /**
     * 根据设备id查询设备所有未恢复事件告警数据
     * @param deviceId
     * @return
     */
    ResponseResult<List<DeviceAlarmEventListDto>> findNotRecoveEventList(String deviceId);

    /**
     * 查询充电站监测数据
     * @param siteId
     * @return
     */
    ResponseResult<ChargeSiteMonitorDto> findChargeSiteMonitorData(String siteId);

    /**
     * 查询充电站电量统计曲线数据
     * @param elecCountQueryVo
     * @return
     */
    ResponseResult<ElecCountCurveDto> findElecCountCurveData(ElecCountQueryVo elecCountQueryVo);

    /**
     * 查询电桩设备列表
     * @param siteId
     * @return
     */
    ResponseResult<List<PileDeviceListDto>> findPileDeviceList(String siteId);

    /**
     * 添加设备字段设置
     * @param deviceId
     * @param functionFields 多个功能点字段id 例如['1','2','3']
     * @return
     */
    ResponseResult<Void> saveDeviceDeviceFieldSet(String deviceId, String functionFields);

    /**
     * 查询设备遥测字段数据列表
     * @param deviceId
     * @return
     */
    ResponseResult<List<DeviceTelemetryDto>> findDeviceTelemetryList(String deviceId);

    /**
     * 根据站点/设备id查询所关联所有系统变量数据列表
     * @param varCodes 多个系统变量编码(以逗号分割，不传则查询所关联全部系统变量)
     * @param queryId 设备/站点id
     * @return
     */
    ResponseResult<List<SystemVarDataDto>> findSystemVarDataListById(String varCodes, String queryId);

    /**
     * 查询设备功能点曲线值数据
     * @param functionValueVo
     * @return 设备 -> 功能点标识(包含x时间轴xAxisList) -> 数据
     */
    ResponseResult<Map<String, Map<String, Object>>> queryDeviceFunctionCurveData(FunctionValueVo functionValueVo);

    /**
     * 查询储能站点监测数据
     * @param siteId
     * @return
     */
    ResponseResult<StorageMonitorDto> findStorageMonitorData(String siteId);

    /**
     * 查询PCS设备监测列表数据
     * @param siteId
     * @return
     */
    ResponseResult<List<PcsMonitorListDto>> findPcsMonitorList(String siteId);

    /**
     * 查询电池簇设备监测列表数据
     * @param siteId
     * @return
     */
    ResponseResult<List<BatteryMonitorListDto>> findBatteryMonitorList(String siteId);

    /**
     * 分页查询电芯列表
     * @param deviceId 设备id
     * @param queryType 查询类型 1-温度 2-电压
     * @param page 页数
     * @param size 条数
     * @return
     */
    ResponseResult<PageDto<Object>> findCellListByPage(String deviceId, Integer queryType, Integer page, Integer size);

    /**
     * 查询辅助设备监测列表数据
     * @param siteId
     * @return
     */
    ResponseResult<List<AuxiliaryMonitorListDto>> findAuxiliaryMonitorList(String siteId);

    /**
     * 查询光伏站点功率曲线数据
     * @param siteIds
     * @param startTime
     * @param endTime
     * @return
     */
    ResponseResult<PileFunCurveDto> findPvSitePowerCurve(String siteIds, String startTime, String endTime);

    /**
     * 查询光伏站点发电量曲线数据
     * @param siteIds 多个站点id
     * @param queryType 查询类型(1-日 2-月 3-年)
     * @param startTime 开始时间(yyyy-MM-dd HH:mm:ss)
     * @param endTime 结束时间(yyyy-MM-dd HH:mm:ss)
     * @return
     */
    ResponseResult<PileFunCurveDto> findPvSiteQtCurve(String siteIds, Integer queryType, String startTime, String endTime);

    /**
     * 查询充电站点功率曲线数据
     * @param siteIds
     * @param startTime
     * @param endTime
     * @return
     */
    ResponseResult<PileFunCurveDto> findChargeSitePowerCurve(String siteIds, String startTime, String endTime);

    /**
     * 查询储能站点功率曲线数据
     * @param siteIds
     * @param startTime
     * @param endTime
     * @return
     */
    ResponseResult<PileFunCurveDto> findStorageSitePowerCurve(String siteIds, String startTime, String endTime);

    /**
     * 查询储能站点充放电量曲线数据
     * @param siteIds    多个站点id
     * @param queryType 查询类型(1-日 2-月 3-年)
     * @param startTime 开始时间(yyyy-MM-dd HH:mm:ss)
     * @param endTime   结束时间(yyyy-MM-dd HH:mm:ss)
     * @return
     */
    ResponseResult<PileFunCurveDto> findStorageSiteQtCurve(String siteIds, Integer queryType, String startTime, String endTime);

    /**
     * 查询PCS设备充放电量曲线数据
     * @param pcsDeviceId pcs设备唯一id
     * @param queryType 查询类型(1-日 2-月 3-年)
     * @param startTime 开始时间(yyyy-MM-dd HH:mm:ss)
     * @param endTime   结束时间(yyyy-MM-dd HH:mm:ss)
     * @return
     */
    ResponseResult<PileFunCurveDto> findPcsChargeQtCurve(String pcsDeviceId, Integer queryType, String startTime, String endTime);
}
