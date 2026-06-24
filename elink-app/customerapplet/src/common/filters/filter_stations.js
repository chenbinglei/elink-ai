
// 场站监控过滤器
export default {
  // 表格电桩状态
  pileWorkState(status) {
    switch (String(status)) {
      case "1":
        return "待机";
      case "2":
        return "工作";
      case "3":
        return "故障";
      case "4":
        return "离线";
      case "5":
        return "未注册";
      default:
        return "--";
    }
  },
  // 表格充电枪状态
  gunWorkState(status) {
    switch (String(status)) {
      case "1":
        return "充电中";
      case "2":
        return "放电中";
      case "3":
        return "空闲中";
      case "4":
        return "占用中";
      case "5":
        return "故障";
      case "6":
        return "离线";
      case "7":
        return "未注册";
      case "8":
        return "暂停";
      default:
        return "--";
    }
  },
  // 固件类型
  station_firmwareType(status) {
    switch (String(status)) {
      case '1':
        return 'V2G_1.0 控制板';
      case '2':
        return 'V2G_2.0 控制板';
      case '3':
        return 'V2G_3.0 TCU 控制板';
      case '4':
        return 'V2G_3.0 CCU 控制板';
      case '5':
        return 'V2G_3.0 PCU 控制板';
      default:
        return status
    }
  },
  // 记录类型
  station_itfRunPattern(status) {
    switch (String(status)) {
      case "0":
        return "充电记录";
      case "1":
        return "放电记录";
      default:
        return status;
    }
  },
  // 启动方式
  station_startMode(status) {
    switch (String(status)) {
      case "0":
        return "立即启动";
      case "1":
        return "延时启动";
      default:
        return status;
    }
  },
  // 启动方式
  station_starter(status) {
    switch (String(status)) {
      case "1":
        return "App";
      case "2":
        return "第三方平台";
      case "3":
        return "电卡";
      case "4":
        return "VIN码";
      case "5":
        return "电桩屏幕强制启动";
      case "6":
        return "有序控制";
      default:
        return status;
    }
  },
  // 设备类型
  station_electricPileType(value){
    switch (String(value)) {
      case "1":
        return "直流";
      case "2":
        return "交流";
      case "3":
        return "V2G";
      default:
        return "--";
    }
  },
  //  工作类型
  station_electricPileDirection(status) {
    switch (String(status)) {
      case "1":
        return "单向";
      case "2":
        return "双向";
      default:
        return "--";
    }
  },
  //  安装类型
  station_fixType(status) {
    switch (String(status)) {
      case "1":
        return "壁挂式";
      case "2":
        return "立地式";
      default:
        return "--";
    }
  },
  // 资产状态
  station_assetStatus(status) {
    switch (String(status)) {
      case "1":
        return "投运";
      case "2":
        return "检修";
      case "3":
        return "退役";
      default:
        return "--";
    }
  },
  // 电站类型
  station_type(status) {
    switch (String(status)) {
      case "1":
        return "充电桩电站";
      case "2":
        return "光伏电站";
      case "3":
        return "储能电站";
      case "4":
        return "光储充一体化电站";
      case "5":
        return "光储电站";
      case "6":
        return "光充电站";
      case "7":
        return "储充电站";
      default:
        return "--";
    }
  },
  // 电站状态
  station_status(status) {
    switch (String(status)) {
      case "1":
        return "投运";
      case "2":
        return "检修";
      case "3":
        return "停运";
      case "4":
        return "退役";
      default:
        return "--";
    }
  },
  // 服务类型
  station_serviceType(status) {
    switch (String(status)) {
      case "1":
        return "充电";
      case "2":
        return "充电/放电";
      default:
        return "--";
    }
  },
  // 运营状态
  station_operationalStatus(status) {
    switch (String(status)) {
      case "1":
        return "开放运营";
      case "2":
        return "非开放运营";
      default:
        return "--";
    }
  },
  // 控制模式
  controlMode(status){
    switch (String(status)) {
      case "1":
        return "自动模式";
      case "2":
        return "代理模式";
      default:
        return "--";
    }
  },
  // 策略管理 -- 执行状态
  executeStatus(status){
    switch (String(status)) {
      case "0":
        return "未执行";
      case "1":
        return "执行中";
      case "2":
        return "已结束";
      default:
        return status;
    }
  },
  // 控制模式
  controlModel(status){
    switch (String(status)) {
      case "1":
        return "手动";
      case "2":
        return "自动";
      case "3":
        return "代理";
      default:
        return "--";
    }
  },
  // 电压等级
  voltageGrade(status){
    switch (String(status)) {
      case "1":
        return "--";
      case "2":
        return "0.4kV";
      case "3":
        return "10kV";
      case "4":
        return "20kV";
      default:
        return "--";
    }
  },
  // 视频设备状态
  videoWorkState(status) {
    switch (String(status)) {
      case "0":
        return "未注册";
      case "1":
        return "在线";
      case "88":
        return "离线";
      default:
        return status;
    }
  },
  /**
   * 文件大小单位转换
   * a 参数：表示要被转化的容量大小，以字节为单
   * b 参数：表示如果转换时出小数，四舍五入保留多少位 默认为2位小数
   */
  formatBytes(a, b) {
    if (0 === a) return "0 B";
    let c = 1024, d = b || 2, e = ["B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"],
        f = Math.floor(Math.log(a) / Math.log(c));
    return parseFloat((a / Math.pow(c, f)).toFixed(d)) + " " + e[f];
  },
}
