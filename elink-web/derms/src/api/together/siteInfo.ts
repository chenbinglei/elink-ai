import request from "@/utils/request";

export default class SiteInfoController {
  /**
   * 停止充放电/停止预约
   * @typedef {Object} PileStopParam
   * @property {String} gunCode 充电枪编号
   *  @property {String} pileCode 充电桩编号
   * @property {String} serialNum 交易流水号
   * @property {Number} type 停止方式 1-停止充/放电 2-取消预约
   * @property {boolean} isStore 是否存控制记录
   *
   * @param {PileStopParam} data
   */
  static pileStop(data) {
    return request({
      url: `/together/siteInfo/pileStop`,
      portNum: 60009,
      method: "post",
      data,
    });
  }

  /**
   * 功率控制
   * @typedef {Object} PowerCtrlParam
   * @property {Number} ctrlType 控制类型 0-绝对控制 1-相对控制
   * @property {String} gunCode 充电枪编号
   * @property {Number} outPower 输出功率
   * @property {String} ctrlType 充电桩编号
   * @property {Number} runMode 运行模式 0-充电模式 1-放电模式
   *
   * @param {PowerCtrlParam} data
   */
  static powerCtrl(data) {
    return request({
      url: `/together/siteInfo/powerCtrl`,
      portNum: 60009,
      method: "post",
      data,
    });
  }

  /**
   * 启动充放电
   * @typedef {Object} PileStartParam
   * @property {String} accountData 账户数据
   * @property {Number} accountType 账户类型 1-充/放电卡 2-VIN码 3-手机号
   * @property {String} gunCode 充电桩枪编号
   * @property {String} pileCode 充电桩编号
   * @property {Number} prepayMoney 预付金额
   * @property {Number} runMode 运行模式 -1-未知 0-充电模式 1-放电模式
   * @property {String} siteId 所属站点id
   * @property {Number} starter 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制
   * @property {Number} strategy 充放电策略 0-自动充满 1-soc电量 2-金额 3-电量
   * @property {Number} strategyCfg 充放电策略参数
   * @property {Number} type 充电方式 0-立即充电 1-定时充电 2-自动充电
   * @property {String} clockingTime 预约时间
   * @property {Boolean} isStore 是否存控制记录
   * @property {String} serialNum 交易流水号
   * 
   * @param {PileStartParam} data
   */
  static pileStart(data) {
    return request({
      url: `/together/siteInfo/pileStart`,
      portNum: 60009,
      method: "post",
      data,
    });
  }

}
