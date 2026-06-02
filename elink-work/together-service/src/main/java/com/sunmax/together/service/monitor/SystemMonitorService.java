package com.sunmax.together.service.monitor;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.monitor.systemMonitor.*;
import com.sunmax.together.vo.monitor.systemMonitor.FaultAlarmQueryVo;
import com.sunmax.together.vo.monitor.systemMonitor.HistoryDataQueryVo;
import com.sunmax.together.vo.monitor.systemMonitor.SystemQueryVo;

import java.util.List;

public interface SystemMonitorService {

    /**
     * 根据站点id和类型查询系统监控树形数据
     * @param siteId 站点id
     * @param type 类型 1-电桩监控 2-光伏监控 3-储能监控 4-变配电系统
     * @return 系统监控树形数据
     */
    ResponseResult<List<SystemTreeDto>> getSystemTreeList(String siteId, Integer type);

    /**
     * 查询系统监控数据
     * @param dataId 数据id
     * @return 系统监控数据
     */
    ResponseResult<SystemPvDto> findSystemPvData(String dataId);

    /**
     * 查询系统监控光伏逆变器数据
     * @param dataId 数据id
     * @return 系统监控光伏逆变器数据
     */
    ResponseResult<SystemPvInverterDto> findPvInverterData(String dataId);

    /**
     * 查询系统监控光伏气象数据
     * @param dataId 数据id
     * @return 系统监控光伏气象数据
     */
    ResponseResult<SystemPvWeatherDto> findPvWeatherData(String dataId);

    /**
     * 查询储能系统数据
     * @param dataId 数据id
     * @return 储能系统数据
     */
    ResponseResult<SystemSeDto> findSystemSeData(String dataId);

    /**
     * 查询储能PCS数据
     * @param dataId 数据id
     * @return 储能系统PCS数据
     */
    ResponseResult<SystemSePcsDto> findSePcsData(String dataId);

    /**
     * 查询储能电池簇数据
     * @param dataId 数据id
     * @return 储能电池簇数据
     */
    ResponseResult<SystemSeBatteryDto> findSeBatteryData(String dataId);

    /**
     * 查询储能辅助设备数据
     * @param dataId 数据id
     * @return 储能辅助设备数据
     */
    ResponseResult<SystemSeAuxEquipmentDto> findSeAuxEquipmentData(String dataId);

    /**
     * 查询电桩系统数据
     * @param dataId 数据id
     * @return 电桩系统数据
     */
    ResponseResult<SystemPileDto> findSystemPileData(String dataId);

    /**
     * 查询电桩数据
     * @param dataId 数据id
     * @return 电桩数据
     */
    ResponseResult<PileDto> findPileData(String dataId);

    /**
     * 查询超充桩数据
     * @param dataId 数据id
     * @return 超充桩数据
     */
    ResponseResult<SuperPileDto> findSuperPileData(String dataId);

    /**
     * 查询电桩枪数据
     * @param dataId 数据id
     * @return 电桩枪数据
     */
    ResponseResult<List<PileGunPowerDto>> findPileGunPowerList(String dataId);

    /**
     * 查询系统曲线数据
     * @param systemQueryVo 系统曲线查询条件
     * @return 系统监控曲线数据
     */
    ResponseResult<SystemCurveDto> findSystemCurve(SystemQueryVo systemQueryVo);

    /**
     * 查询故障告警列表
     * @param faultAlarmVo 故障告警查询条件
     * @return 故障告警列表
     */
    ResponseResult<PageDto<FaultAlarmListDto>> findAllFaultAlarmList(FaultAlarmQueryVo faultAlarmVo);

    /**
     * 查询设备字段列表
     * @param siteId 站点id
     * @return 设备字段列表
     */
    ResponseResult<DeviceFieldDto> getDeviceFieldList(String siteId);

    /**
     * 查询历史数据列表
     * @param historyDataVo 历史数据查询条件
     * @return 历史数据列表
     */
    ResponseResult<HistoryDataDto> findAllHistoryDataList(HistoryDataQueryVo historyDataVo);

    /**
     * 根据站点id查询站点详情数据
     * @param siteId 站点id
     * @return 站点详情数据
     */
    ResponseResult<SiteDetailDto> findSiteDetailById(String siteId);

    /**
     * 分页查询电芯列表
     * @param dataId 数据id
     * @param queryType 查询类型 1-温度 2-电压
     * @param page 页数
     * @param size 条数
     * @return 数据列表
     */
    ResponseResult<PageDto<Object>> findAllCellList(String dataId, Integer queryType, Integer page, Integer size);

    /**
     * 查询换电系统数据
     * @param dataId 数据id
     * @return 换电系统数据
     */
    ResponseResult<SystemChangeDto> findSystemChangeData(String dataId);

    /**
     * 查询电表数据
     * @param dataId 数据id
     * @return 系统电表数据
     */
    ResponseResult<SystemMeterDto> findSystemMeterData(String dataId);

}
