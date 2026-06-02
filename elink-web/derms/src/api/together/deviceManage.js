import request from "@/utils/request";

export default class DeviceManageController {
  /**
   * 分页查询电桩列表信息
   *
   * @typedef {Object} FindPileListByPageParam
   * @property {number} page 当前页
   * @property {number} size 当前页条数
   * @property {string} area 区域
   * @property {number} areaType 区域类型 1-省级 2-市级
   * @property {number} gunWorkState 枪状态 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
   * @property {string} manufacturersName 厂商名称
   * @property {string} pileName 桩名称/编码
   * @property {string} siteId 所属站点id
   * @property {string} typeId 设备类型 28-交流 29-直流 30-V2G
   * @property {string} userId 用户ID
   * @property {number} workStatus 电桩工作状态 1-在线 2-维护 3-故障 88-离线
   *
   * @param {FindPileListByPageParam} data 查询参数
   * @param {Number} ts
   * @returns {Promise<Object>} 请求结果
   */
  static findPileListByPage(data, ts = 0) {
    return request({
      url: `/together/deviceManage/findPileListByPage?ts=${ts}`,
      portNum: 60009,
      method: "post",
      data,
    });
  }
}
