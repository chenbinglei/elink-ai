import request from "@/utils/request";

export default class SystemMonitorController {
  /**
   * 根据站点id查询站点详情数据
   * @param {String} siteId 站点id
   */
  static findSiteDetailById(siteId) {
    return request({
      url: `/together/systemMonitor/findSiteDetailById`,
      portNum: 60009,
      method: "post",
      data: { siteId },
    });
  }

  /**
   * 获取系统监控树形列表
   * @typedef {Object} GetSystemTreeListParams
   * @property {String} siteId 站点id
   * @property {String} type 类型 1-光伏监控 2-储能监控 3-电桩监控 4-变配电系统
   * @param {GetSystemTreeListParams} data
   */
  static getSystemTreeList(data,ts=0) {
    return request({
      url: `/together/systemMonitor/getSystemTreeList?ts${ts}`,
      portNum: 60009,
      method: "post",
      data: data,
    });
  }

  /**
   * 查询光伏系统数据
   * @param {String} dataId 数据id
   */
  static findSystemPvData(dataId) {
    return request({
      url: `/together/systemMonitor/findSystemPvData`,
      portNum: 60009,
      method: "post",
      data: { dataId },
    });
  }

  /**
   * 查询光伏逆变器数据
   * @param {String} dataId
   */
  static findPvInverterData(dataId) {
    return request({
      url: `/together/systemMonitor/findPvInverterData`,
      portNum: 60009,
      method: "post",
      data: { dataId },
    });
  }

  /**
   * 查询光伏气象站数据
   * @param {String} dataId
   */
  static findPvWeatherData(dataId) {
    return request({
      url: `/together/systemMonitor/findPvWeatherData`,
      portNum: 60009,
      method: "post",
      data: { dataId },
    });
  }

  /**
   * 查询储能系统数据
   * @param {String} dataId
   */
  static findSystemSeData(dataId) {
    return request({
      url: `/together/systemMonitor/findSystemSeData`,
      portNum: 60009,
      method: "post",
      data: { dataId },
    });
  }
  static findSystemChangeData(dataId) {
    return request({
      url: `/together/systemMonitor/findSystemChangeData`,
      portNum: 60009,
      method: "post",
      data: { dataId },
    });
  }
  /**
   * 查询储能PCS数据
   * @param {String} dataId
   */
  static findSePcsData(dataId) {
    return request({
      url: `/together/systemMonitor/findSePcsData`,
      portNum: 60009,
      method: "post",
      data: { dataId },
    });
  }

  /**
   * 查询储能电池簇数据
   * @param {String} dataId
   */
  static findSeBatteryData(dataId) {
    return request({
      url: `/together/systemMonitor/findSeBatteryData`,
      portNum: 60009,
      method: "post",
      data: { dataId },
    });
  }

  /**
   * 查询储能辅助设备数据
   * @param {String} dataId
   */
  static findSeAuxEquipmentData(dataId) {
    return request({
      url: `/together/systemMonitor/findSeAuxEquipmentData`,
      portNum: 60009,
      method: "post",
      data: { dataId },
    });
  }

  /**
   * 查询电桩系统数据
   * @param {String} dataId
   */
  static findSystemPileData(dataId) {
    return request({
      url: `/together/systemMonitor/findSystemPileData`,
      portNum: 60009,
      method: "post",
      data: { dataId },
    });
  }

  /**
   * 查询电桩数据
   * @param {String} dataId
   */
  static findPileData(dataId) {
    return request({
      url: `/together/systemMonitor/findPileData`,
      portNum: 60009,
      method: "post",
      data: { dataId },
    });
  }
    static findSuperPileData(dataId) {
    return request({
      url: `/together/systemMonitor/findSuperPileData`,
      portNum: 60009,
      method: "post",
      data: { dataId },
    });
  }
  static findSystemMeterData(dataId) {
    return request({
      url: `/together/systemMonitor/findSystemMeterData`,
      portNum: 60009,
      method: "post",
      data: { dataId },
    });
  }

  /**
   * 查询电桩充电枪功率曲线数据
   * @param {String} dataId
   */
  static findPileGunPowerList(dataId) {
    return request({
      url: `/together/systemMonitor/findPileGunPowerList`,
      portNum: 60009,
      method: "post",
      data: { dataId },
    });
  }
  

  /**
   * 查询系统曲线数据
   * @typedef {Object} FindSystemCurveParams
   * @property {String} startTime 开始时间(yyyy-MM-dd HH:mm:ss)
   * @property {String} endTime 结束时间(yyyy-MM-dd HH:mm:ss)
   * @property {String} timeInterval 数据时间间隔 s-秒 m-分钟 h-小时 d-天 n-月 y-年
   * @property {Number} formatInterval 时间格式间隔 1-(yyyy-MM-dd HH:mm:ss) 2-(yyyy-MM-dd) 3-(yyyy-MM) 4-(yyyy) 5-(HH:mm:ss) 6-(HH:mm)
   * @property {String} dataId 数据id
   * @property {Number} type 类型 1-光伏系统功率 2-光伏系统发电量 3-光伏逆变器功率 4-光伏逆变器发电量 5-光伏气象站辐照度 6-光伏气象站温度 7-光伏气象站辐照累积量 8-储能系统功率 9-储能系统发电量 10-储能PCS功率 11-储能PCS充放电量 12-储能电池簇SOC 13-储能电池簇总电压 14-储能辅助设备温度 15-储能辅助设备湿度 16-电桩系统功率 17-电桩系统充放电量 18-电桩功率 19-电桩充放电量
   * @param {FindSystemCurveParams} data
   */
  static findSystemCurve(data) {
    return request({
      url: `/together/systemMonitor/findSystemCurve`,
      portNum: 60009,
      method: "post",
      data,
    });
  }

  /**
   * 分页查询故障告警列表数据
   * @typedef {Object} FindFaultAlarmListByPageParams
   * @property {Number} page 当前页
   * @property {Number} size 当前页条数
   * @property {String} siteId 站点id
   * @property {String} startDate 开始时间(yyyy-MM-dd)
   * @property {String} endDate 结束时间(yyyy-MM-dd)
   * @property {String} alarmStatus 告警状态 0-未修复 1-已修复
   * @property {String} eventLevel 事件级别 0-提示告警 1-普通告警 2-重要告警 3-紧急告警
   * @property {String} typeId 设备类型id
   * @param {FindFaultAlarmListByPageParams} data
   */
  static findFaultAlarmListByPage(data,ts) {
    return request({
      url: `/together/systemMonitor/findAllFaultAlarmList?ts=${ts}`,
      portNum: 60009,
      method: "post",
      data,
    });
  }

  /**
   * 获取站点及站点下面的设备数据字段
   * @param {String} siteId
   */
  static getDeviceFieldList(siteId) {
    return request({
      url: `/together/systemMonitor/getDeviceFieldList`,
      portNum: 60009,
      method: "post",
      data: { siteId },
    });
  }

  /**
   * 查询站点设备历史数据
   * @typedef {Object} FindAllHistoryDataListParams
   * @property {String} endTime 结束时间(yyyy-MM-dd HH:mm:ss)
   * @property {String} startTime 开始时间(yyyy-MM-dd HH:mm:ss)
   * @property {String} timeInterval 数据时间间隔 s-秒 m-分钟 h-小时 d-天 n-月 y-年
   * @property {String?} functions 多个功能点数据
   * @property {String?} nodes 多个计算节点数据
   * @param {FindAllHistoryDataListParams} data
   */
  static findAllHistoryDataList(data) {
    return request({
      url: `/together/systemMonitor/findAllHistoryDataList`,
      portNum: 60009,
      method: "post",
      data,
    });
  }

   /**
   * 根据数据id和类型查询电池簇电芯列表
   * @typedef {Object} FindAllCellListParams
   * @property {String} dataId 数据id
   * @property {String} page 页数
   * @property {String} queryType 查询类型 1-温度 2-电压
   * @property {String?} size 条数
   * @param {FindAllCellListParams} data
   */
   static findAllCellList(data) {
    return request({
      url: `/together/systemMonitor/findAllCellList`,
      portNum: 60009,
      method: "post",
      data,
    });
  }
}
