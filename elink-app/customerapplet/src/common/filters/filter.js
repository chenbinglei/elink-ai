export default {

  // 计划类型
  planType(status) {
    switch (String(status)) {
      case "1":
        return "充电";
      case "2":
        return "放电";
      default:
        return status;
    }
  },
  // 计划状态
  planState(status) {
    switch (String(status)) {
      case "1":
        return "未开始";
      case "2":
        return "执行中";
      case "3":
        return "已结束";
      case "4":
        return "已取消";
      default:
        return status;
    }
  },
  accountStatus(status) {
    switch (String(status)) {
      case "0":
        return "启用";
      case "1":
        return "禁用";
      case "2":
        return "已删除";
      default:
        return status;
    }
  },
  modelStatus(status) {
    switch (String(status)) {
      case "1":
        return "未提交";
      case "2":
        return "已提交";
      default:
        return status;
    }
  },
  energyType(status) {
    switch (String(status)) {
      case "1":
        return "纯电动";
      case "2":
        return "燃料电池";
      case "3":
        return "混合动力";
      default:
        return "--";
    }
  },
  modelType(status) {
    switch (String(status)) {
      case "1":
        return "基本信息";
      case "2":
        return "物理属性";
      case "3":
        return "资产属性";
      default:
        return status;
    }
  },
  batteryType(status) {
    switch (String(status)) {
      case "0":
        status = "--";
        break;
      case "1":
        status = "铅酸电池";
        break;
      case "2":
        status = "氢电池";
        break;
      case "3":
        status = "磷酸铁锂电池";
        break;
      case "4":
        status = "锰酸锂电池";
        break;
      case "5":
        status = "钴酸锂电池";
        break;
      case "6":
        status = "三元电池";
        break;
      case "7":
        status = "聚合物锂电池";
        break;
      case "8":
        status = "钛酸锂电池";
        break;
      case "255":
        status = "其他";
        break;
      case "null":
        status = "--";
        break;
      default:
        return "--";
    }
    return status;
  },
  measurementType(status) {
    switch (String(status)) {
      case "1":
        return "遥测";
      case "2":
        return "遥信";
      case "3":
        return "遥控";
      case "4":
        return "遥脉";
      default:
        return status;
    }
  },
  channelProtocol(status) {
    switch (String(status)) {
      case "1":
        return "IEC104";
      case "2":
        return "SMMQTT";
      default:
        return status;
    }
  },
  graphType(status) {
    switch (String(status)) {
      case "1":
        return "电气连接图";
      case "2":
        return "系统拓扑图";
      case "3":
        return "网络通信图";
      default:
        return status;
    }
  },
  // 站点类型
  typeDetail(status) {
    switch (String(status)) {
      case "1":
        return "实体站点";
      case "2":
        return "虚拟站点";
      default:
        return status;
    }
  },
  // 站点运营状态
  operationalStatus(status,isSplicing = true) {
    switch (String(status)) {
      case "1":
        return `开放${ isSplicing ? '运营' : ''}`;
      case "2":
        return `非开放${ isSplicing ? '运营' : ''}`;
      case "3":
        return `半开放${ isSplicing ? '运营' : ''}`;
      case "":
      case "null":
      case "undefined":
        return "--";
      default:
        return status;
    }
  },
  graphStatus(status) {
    switch (String(status)) {
      case "1":
        return "未发布";
      case "2":
        return "正在编辑";
      case "3":
        return "已发布";
      default:
        return status;
    }
  },
  operationType(status) {
    switch (String(status)) {
      case "1":
        return "新增";
      case "2":
        return "编辑";
      case "3":
        return "删除";
      case "4":
        return "发布";
      case "5":
        return "导入";
      case "6":
        return "导出";
      case "7":
        return "保存";
      default:
        return status;
    }
  },
  hintWay(status) {
    switch (String(status)) {
      case "1":
        return "弹窗跳转";
      case "2":
        return "弹窗提示";
      case "3":
        return "仅接收不提示";
      default:
        return status;
    }
  },
  noticeWay(status) {
    switch (String(status)) {
      case "1":
        return "平台通知";
      case "2":
        return "APP通知";
      case "3":
        return "手机短信通知";
      default:
        return status;
    }
  },
  playTime(status) {
    switch (String(status)) {
      case "loop":
        return "循环";
      default:
        return status;
    }
  },
  issuedStatus(status) {
    switch (String(status)) {
      case "1":
        return "未下发";
      case "2":
        return "已下发";
      default:
        return status;
    }
  },
  // 计费下发状态
  billIssuedStatus(status) {
    switch (String(status)) {
      case "0":
        return "下发成功";
      case "1":
        return "下发失败";
      default:
        return status;
    }
  },
  isVinsuedStatus(status) {
    switch (String(status)) {
      case "1":
        return "保存未应用";
      case "2":
        return "已应用";
      default:
        return status;
    }
  },
  // 充电枪工作状态  vehicleConnectState: 车辆连接状态
  chargerGunStatus(status,vehicleConnectState = 0) {
    // 枪状态为 已连接
    if(status<=0 && String(vehicleConnectState) === "2")status = 666;

    switch (String(status)) {
      case "0":
        return "空闲中";
      case "1":
        return "充电准备";
      case "2":
        return "充电中";
      case "3":
        return "充电完成";
      case "4":
        return "放电准备";
      case "5":
        return "放电中";
      case "6":
        return "放电完成";
      case "7":
        return "预约";
      case "88":
        return "离线";
      case "FF":
        return "枪禁用";
      case "255":
        return "故障";
      case "666":
        return "已连接";
      default:
        return status ? status : '--';
    }
  },
  // 根据充电桩状态判断充电枪数据是否展示
  chargeGunInfoShow(status) {
    return status === 1 || status === 2 || status === 4 || status === 5;
  },
  //启动方式
  startType(status) {
    switch (String(status)) {
      case "1":
        return "手机终端";
      default:
        return status;
    }
  },
  vehicleConnectState(status) {
    switch (String(status)) {
      case "0":
        return "未连接";
      case "1":
        return "未连接";
      case "2":
        return "已连接";
      default:
        return '--';
    }
  },
  // 确认状态
  affirmStatus(status) {
    switch (String(status)) {
      case "0":
        return "未确认";
      case "1":
        return "已确认";
      default:
        return status;
    }
  },
  // 告警修复状态
  alarmStatus(status) {
    switch (String(status)) {
      case "1":
        return "未修复";
      case "0":
        return "已修复";
      default:
        return status;
    }
  },
  //  空数据处理
  nullData(data) {
    if (data !== null && data || data === 0) {
      return data;
    } else {
      return "--";
    }
  },
  //  空数据处理
  moreData(data) {
    switch (String(data)) {
      case "":
      case "null":
      case "undefined":
        return "--";
      // case "0":
      //   return "0.00";
      case !isNaN(data * 1):
        return Number(data).toFixed(2);
      default:
        return data;
    }
  },
  // 变压器冷却方式
  cooling(status) {
    switch (String(status)) {
      case "1":
        return "油浸自冷(ONAN)";
      case "2":
        return "油浸风冷(ONAF)";
      case "3":
        return "强迫油循环风冷(OFAF)";
      case "4":
        return "强迫油循环风冷(OFAF)";
      case "5":
        return "强迫油循环水冷(OFWF)";
      case "6":
        return "强迫导向油循环水冷(ODWF)";
      case "7":
        return "AF-吹风冷却";
      case "8":
        return "AN-空气(循环)自然冷却";
      default:
        return "--";
    }
  },
  // 连结组别
  bond(status) {
    switch (String(status)) {
      case "1":
        return "Dyn11";
      case "2":
        return "Yyn0";
      case "3":
        return "Yd1";
      default:
        return "--";
    }
  },
  // 电桩状态
  chargerStatus(status) {
    switch (String(status)) {
      case "0":
        return "待机";
      case "1":
        return "在线";
      case "2":
        return "维护";
      case "3":
        return "故障";
      case "88":
        return "离线";
      case "null":
        return "未注册";
      default:
        return status;
    }
  },
  configType(status) {
    switch (String(status)) {
      case "1":
        return "充电计费项";
      case "2":
        return "放电计费项";
      default:
        return status;
    }
  },
  // 网关设备状态
  openAndClose(status) {
    switch (String(status)) {
      case "0":
        return "关闭";
      case "1":
        return "开启";
      default:
        return "--";
    }
  },
  // 网关 控制精度
  controlAccuracy(status) {
    switch (String(status)) {
      case "5":
        return "I级";
      case "15":
        return "Ⅱ级";
      case "30":
        return "Ⅲ级";
      case "60":
        return "Ⅳ级";
      case "120":
        return "Ⅴ级";
      case "300":
        return "Ⅵ级";
      default:
        return "--";
    }
  },
  billingItemType(status) {
    switch (String(status)) {
      case "1":
        return "支出";
      case "2":
        return "收入";
      case "3":
        return "浮动";
      default:
        return status;
    }
  },
  periodType(status) {
    switch (String(status)) {
      case "0":
        return "无";
      case "1":
        return "尖时";
      case "2":
        return "峰时";
      case "3":
        return "平时";
      case "4":
        return "谷时";
      case "5":
        return "全时段统一定价";
      default:
        return status;
    }
  },
  orderType(status) {
    switch (String(status)) {
      case "1":
        return "充电订单";
      case "2":
        return "放电订单";
      default:
        return status;
    }
  },
  isOrderly(status) {
    switch (String(status)) {
      case "1":
        return "是";
      case "2":
        return "否";
      default:
        return status;
    }
  },
  userType(status) {
    switch (String(status)) {
      case "1":
        return "个人用户";
      case "2":
        return "企业用户";
      default:
        return status;
    }
  },
  stopReason(status) {
    switch (String(status)) {
      case "1":
        return "正常停止";
      case "2":
        return "紧急停止";
      case "3":
        return "通用故障";
      case "4":
        return "上报故障";
      case "5":
        return "绝缘检测故障";
      case "6":
        return "超时类故障";
      case "7":
        return "平台交互类故障";
      default:
        return status;
    }
  },
  orderLogo(status) {
    switch (String(status)) {
      case "0":
        return "未进行";
      case "1":
        return "在途";
      case "2":
        return "异常终止";
      case "3":
        return "已完成";
      case "8":
        return "已取消";
      default:
        return status;
    }
  },
  settlementState(status) {
    switch (String(status)) {
      case "0":
        return "未结算";
      case "1":
        return "结算关闭";
      case "2":
        return "结算失败";
      case "3":
        return "结算成功";
      default:
        return status;
    }
  },
  startMode(status) {
    switch (String(status)) {
      case "1":
        return "APP启动";
      case "2":
        return "平台启动";
      case "3":
        return "电卡启动";
      case "4":
        return "VIN码启动";
      case "5":
        return "电桩屏幕强制启动";
      case "6":
        return "有序控制启动";
      default:
        return status;
    }
  },
  powerWay(status,chargingDisType) {
    switch (String(status)) {
      case "1":
        return chargingDisType === 1 ? "立即充电" : chargingDisType === 2 ? "立即放电" : "立即充/放电";
      case "2":
        return chargingDisType === 1 ? "定时充电" : chargingDisType === 2 ? "定时放电" : "定时充/放电";
      case "3":
        return chargingDisType === 1 ? "自动充电" : chargingDisType === 2 ? "自动放电" : "自动充/放电";
      default:
        return status;
    }
  },
  powerWay1(status) {
    switch (String(status)) {
      case "1":
        return "立即充/放电";
      case "2":
        return "定时充/放电";
      case "3":
        return "自动充/放电";
      default:
        return status;
    }
  },
  // 启动方式
  startMethod(startMethod,direction) {
    switch (String(startMethod)) {
      case "0":
        return direction === null ? "--" : direction === 0 ? "立即充电" : "立即放电";
      case "1":
        return direction === null ? "--" : direction === 0 ? "定时充电" : "定时放电";
      case "2":
        return direction === null ? "--" : direction === 0 ? "自动充电" : "自动放电";
      default:
        return "--";
    }
  },
  vehicleVin(status) {
    switch (String(status)) {
      case "":
        return "--";
      case "null":
        return "--";
      case "undefined":
        return "--";
      default:
        return status;
    }
  },
  strategyType(status,chargingDisType) {
    switch (String(status)) {
      case "0":
        return chargingDisType === 1 ? "自动充满" : chargingDisType === 2 ? "自动放空" : "满充/放空";
      case "1":
        return "定SOC";
      case "2":
        return "定金额";
      case "3":
        return "定电量";
      default:
        return status;
    }
  },
  startUpStrategy(status,chargingDisType) {
    switch (String(status)) {
      case "0":
        return chargingDisType === 2 ? "自动放空" : "自动充满";
      case "1":
        return "定SOC";
      case "2":
        return "定金额";
      case "3":
        return "定电量";
      default:
        return status;
    }
  },
  orderStatus(status) {
    switch (String(status)) {
      case "0":
        return "创建订单";
      case "1":
        return "在途";
      case "2":
        return "异常-数据中断";
      case "3":
        return "订单完成";
      case "4":
        return "预计完成时间";
      case "5":
        return "异常中断-订单完成";
      case "7":
        return "预约中";
      case "8":
        return "已取消-订单完成";
      case "9":
        return "订单未结算";
      default:
        return status;
    }
  },
  //  充电桩工作类型
  pileWorkType(status) {
    switch (String(status)) {
      case "1":
        return "双向";
      case "2":
        return "单向";
      default:
        return "--";
    }
  },
  //车辆类型
  carType(status) {
    switch (String(status)) {
      case "1":
        return "运营车辆";
      case "2":
        return "私人车辆";
      case "3":
        return "其他";
      default:
        return "其他";
    }
  },
  //  性别
  sex(status) {
    switch (String(status)) {
      case "1":
        return "男";
      case "2":
        return "女";
      default:
        return "--";
    }
  },
  //  交易明细
  detailType(status) {
    switch (String(status)) {
      case "1":
        return "充电消费";
      case "2":
        return "放电收益";
      case "3":
        return "余额充值";
      case "4":
        return "余额退费";
      case "5":
        return "收益提现";
      default:
        return "--";
    }
  },
  //  订单状态
  orderState(status) {
    switch (String(status)) {
      case "1":
        return "在途";
      case "2":
        return "已完成";
      case "3":
        return "异常终止";
      case "4":
        return "已取消";
      default:
        return status;
    }
  },
  // 占用订单装填
  occupyOrderState(status){
    switch (String(status)) {
      case "1":
        return "在途";
      case "2":
        return "待支付";
      case "3":
        return "已完成";
      case "9":
        return "异常";
      default:
        return status;
    }
  },
  //  职业
  professionId(status) {
    switch (String(status)) {
      case "1":
        return "事业单位";
      case "2":
        return "互联网科技";
      case "3":
        return "服务业";
      case "4":
        return "医疗健康";
      case "5":
        return "教育";
      case "6":
        return "金融";
      case "7":
        return "文化传媒/娱乐";
      case "8":
        return "工程建设";
      case "9":
        return "交通运输";
      case "10":
        return "工业制造";
      case "11":
        return "自由职业";
      case "12":
        return "退休/下岗/失业";
      case "13":
        return "农林牧副渔";
      case "14":
        return "学生";
      case "15":
        return "其他";
      default:
        return "--";
    }
  },
  // 资产状态
  assetStatus(status) {
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
  chargeAndDisCharge(status) {
    switch (String(status)) {
      case "1":
        return "充电";
      case "2":
        return "放电";
      default:
        return "--";
    }
  },
  //是否结转
  isCarry(type) {
    switch (String(type)) {
      case "0":
        return "否";
      case "1":
        return "是";
      default:
        return type;
    }
  },

  //结转类型
  carryType(type) {
    switch (String(type)) {
      case "1":
        return "单次充电结转";
      case "2":
        return "单次放电结转";
      default:
        return type;
    }
  },
  // 交易状态
  tradeStatus(status) {
    switch (String(status)) {
      case "1":
        return "处理中";
      case "2":
        return "处理成功";
      case "3":
        return "取消支付";
      default:
        return "--";
    }
  },
  // 交易类型
  tradeType(status) {
    switch (String(status)) {
      case "1":
        return "充电预付";
      case "2":
        return "充电退款";
      case "3":
        return "放电支出";
      case "4":
        return "分账收入";
      case "5":
        return "分账支出";
      case "6":
        return "占位收入";
      case "7":
        return "占位退款";
      default:
        return "--";
    }
  },
  // 支付方式
  tradeWay(status) {
    switch (String(status)) {
      case "1":
        return "微信";
      case "2":
        return "支付宝";
      case "3":
        return "放电收益转入";
      case "4":
        return "对公转账";
      case "5":
        return "线下支付";
      default:
        return "--";
    }
  },
  chargingTypes(type) {
    switch (String(type)) {
      case "0":
        return "pileTextColor0"; // 空闲中
      case "2":
        return "pileTextColor1"; // 充电中
      case "5":
        return "pileTextColor2"; // 放电中
      case "255":
        return "pileTextColor3"; // 故障
      case "1": // 充电准备
      case "3": // 充电结束
      case "4": // 放电准备
      case "6": // 放电完成
      case "7": // 预约
        return "pileTextColor4";
      case "88":
        return "pileTextColor6"; // 离线
      default:
        return "unregistered";
    }
  },
  // 停车费用类型
  parkCostType(type) {
    switch (String(type)) {
      case "0":
      case "":
      case "null":
      case "undefined":
        return "--";
      case "1":
        return "免费停车";
      case "2":
        return "限时免费";
      case "3":
        return "充电限免";
      case "4":
        return "停车收费";
      default:
        return type;
    }
  },
  // 设备工作状态
  deviceState(type) {
    switch (String(type)) {
      case "1":
        return "pileTextColor5";
      case "88":
        return "pileTextColor6";
      case "255":
        return "pileTextColor3";
      default:
        return "unregistered";
    }
  },
  // 站点状态
  pileState(type) {
    switch (String(type)) {
      case "投运":
      case "开放运营":
      case "非开放运营":
        return "pileTextColor5";
      case "检修":
        return "pileTextColor6";
      default:
        return type;
    }
  },
  // 运营状态
  operateState(type) {
    switch (String(type)) {
      case "1":
        return "开放运营";
      case "2":
        return "非开放运营";
      case "3":
        return "半开放运营";
      default:
        return type;
    }
  },
  //通信链路 ，图模状态转换文字
  deviceStatusFilters(type) {
    switch (String(type)) {
      case "0":
        return "异常";
      case "1":
        return "正常";
      default:
        return type;
    }
  },
  // 是否加入充电联盟 0-不加入 1-加入
  isChargeAlliance(type) {
    switch (String(type)) {
      case "0":
        return "不加入";
      case "1":
        return "已加入";
      default:
        return "--";
    }
  },
  // 是否启用特权名单 0-不启用 1-启用
  isPrivilegeList(type) {
    switch (String(type)) {
      case "0":
        return "不启用";
      case "1":
        return "已启用";
      default:
        return "--";
    }
  },
  // 是否启用黑白名单 0-不启用 1-白名单模式 2-黑名单模式
  isStartList(type) {
    switch (String(type)) {
      case "0":
        return "不启用";
      case "1":
        return "白名单模式";
      case "2":
        return "黑名单模式";
      default:
        return "--";
    }
  },
  // 固件类型
  firmwareType(status) {
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
        return "--"
    }
  },
  // 固件升级失败提示语
  firmwareUpdateCode(type) {
    switch (String(type)) {
      case "1":
        return "数据校验失败";
      case "2":
        return "电桩应答超时";
      case "3":
        return "其他原因";
      case "4":
        return "其他原因";
      case "5":
        return "同版本不升级";
      case "6":
        return "平台应答超时";
      case "7":
        return "电桩未在线";
      case "255":
        return "其他原因";
      default:
        return type;
    }
  },
  // 千克，吨数值转换
  kgOrTValue(value) {
    if (value || value === 0) {
      const unitValue = Number(value);
      if (unitValue >= 1000) {
        return (unitValue / 1000).toFixed(2);
      } else {
        return unitValue.toFixed(2);
      }
    } else {
      return "--";
    }
  },
  // 数量转换
  numberValue(value) {
    if (value || value === 0) {
      const unitValue = Number(value);
      if (unitValue >= 10000) {
        return (unitValue / 10000).toFixed(2) + "万";
      } else {
        return unitValue;
      }
    } else {
      return "--";
    }
  },
  // 数值转换
  kgValue(value) {
    if (value || value === 0) {
      const unitValue = Number(value);
      if (unitValue >= 10000) {
        return (unitValue / 10000).toFixed(2) + "万";
      } else {
        return unitValue.toFixed(2);
      }
    } else {
      return "--";
    }
  },
  // 千瓦时——千米——小时数值转换
  kWh_km_h_Value(value) {
    if (value || value === 0) {
      const unitValue = Number(value);
      if (unitValue >= 10000) {
        return (unitValue / 10000).toFixed(2) + "万";
      } else {
        return unitValue.toFixed(2);
      }
    } else {
      return "--";
    }
  },
  // 元数值转换
  yuanValue(value, fixed=2) {
    if (value || value === 0) {
      const unitValue = Number(value);
      if (unitValue >= 10000){
        return (unitValue / 10000).toFixed(fixed) + "万";
      } else if (unitValue >= 100000000){
        return (unitValue / 100000000).toFixed(fixed) + "亿";
      } else {
        return unitValue.toFixed(fixed);
      }
    } else {
      return "--";
    }
  },
  // 流量数值转换
  mbValue(value, fixed=2) {
    if (value || value === 0) {
      const unitValue = Number(value);
      if (unitValue >= 1024){
        return (unitValue / 1024).toFixed(fixed);
      } else {
        return unitValue;
      }
    } else {
      return "--";
    }
  },
  // M，G单位流量转换
  unitMOrG(value) {
    if (value || value === 0) {
      const unitValue = Number(value);
      if (unitValue >= 1024){
        return "G";
      } else {
        return "M";
      }
    }
  },
  // 千瓦时单位转换
  unitKwh(value) {
    if (value || value === 0) {
      return "kW·h";
    }
  },
  // 千克，吨单位转换
  unitKgOrT(value) {
    if (value || value === 0) {
      const unitValue = Number(value);
      if (unitValue >= 1000){
        return "吨";
      } else {
        return "千克";
      }
    }
  },
  // 千米单位转换
  unitKm(value) {
    if (value || value === 0) {
      return "km";
    }
  },
  // 设备类型
  deviceType(value) {
    switch (String(value)) {
      case "12":
        return "充电桩";
      case "14":
        return "变压器";
      default:
        return "--";
    }
  },
  // 元单位转换
  unitYuan(value) {
    if (value || value === 0) {
      return "元";
    }
  },
  // 时间转换
  unitH(value) {
    if (value || value === 0) {
      return "h";
    }
  },
  // 文字替换  充/放  替换  充  放  单个字
  chargingDisText(value,powerWay = 1){
    let text = powerWay === 1 ? "充" : "放";
    if(value)return String(value).replaceAll("充/放",text);
    return value
  },
  // 手机号处理 隐藏中间四位
  phoneCenterFour(phone) {
    let phoneAll = phone ? phone : "";
    if(phoneAll){
      return phoneAll.substring(0,3) + "****" + phoneAll.substring(7);
    } else {
      return "--"
    }
  },
  // 商户Id处理 隐藏末尾四位
  mchLastId(mchId) {
    let mchIdAll = mchId ? mchId : "";
    if(mchIdAll){
      return mchIdAll.substring(0,mchIdAll.length - 4) + "****";
    } else {
      return "--"
    }
  },
  // 根据交易类型添加金额符号类型(+ -)
  setSymbolByTradeType(row) {
    switch (row.tradeType) {
      case 1:
      case 4:
      case 6:
        return "+" + row.tradeMoney
      case 2:
      case 3:
      case 5:
      case 7:
        return "-" + row.tradeMoney
    }
  },
  // 占用计费规则 --》 计费机制
  billingCdmType(value){
    switch (String(value)) {
      case "1":
        return "自然天计费制";
      case "2":
        return "24小时计费制";
      default:
        return "--";
    }
  },
  // 占用计费规则 --》 计费机制
  electricPileType(value){
    switch (String(value)) {
      case "1":
        return "直流";
      case "2":
        return "交流";
      case "3":
        return "V2G";
      default:
        return value;
    }
  },
  // 策略操作类型
  operateType(value){
    switch (String(value)) {
      case "1":
        return "创建策略";
      case "2":
        return "编辑策略";
      case "3":
        return "删除策略";
      case "4":
        return "应用策略";
      default:
        return value;
    }
  },
  // 策略日志分类
  recordSort(value){
    switch (String(value)) {
      case "1":
        return "场站级";
      case "2":
        return "设备级";
      default:
        return value;
    }
  },
  // 计费策略类型
  billingStrategyType(value){
    switch (String(value)) {
      case "1":
        return "充电计费策略";
      case "2":
        return "放电计费策略";
      case "3":
        return "占桩计费策略";
      default:
        return value;
    }
  },
  // 是否启用
  enableOrNot(value){
    switch (String(value)) {
      case "1":
        return "启用";
      case "2":
        return "禁用";
      default:
        return value;
    }
  },
  // 设备工作状态
  workState(value){
    switch (String(value)) {
      case "1":
        return "在线";
      case "2":
        return "未注册";
      case "3":
        return "故障";
      case "88":
        return "离线";
      default:
        return value;
    }
  },
  // 设备工作图标
  iconWorkState(value){
    switch (String(value)) {
      case "1":
        return "icon-zaixian";
      case "2":
        return "icon-weizhuce";
      case "3":
        return "icon-guzhang";
      case "88":
        return "icon-lixian";
      default:
        return value;
    }
  },
  // 设备工作图标颜色
  workStateColor(value){
    switch (String(value)) {
      case "1":
        return "normal";
      case "2":
        return "noRegister";
      case "3":
        return "fault";
      case "88":
        return "offline";
      default:
        return value;
    }
  },
  //  许可状态
  licenseStatus(data) {
    switch (String(data)) {
      case "0":
        return "未认证";
      case "1":
        return "已认证";
      case "2":
        return "已过期";
      default:
        return "--";
    }
  },
  // 报表状态
  reportStatus(data) {
    switch (String(data)) {
      case "1":
        return "进行中";
      case "2":
        return "已结束";
      default:
        return "--";
    }
  },
  //  工单级别
  devOpsWorkLevel(data) {
    switch (String(data)) {
      case "1":
        return "低";
      case "2":
        return "中";
      case "3":
        return "高";
      default:
        return "--";
    }
  },
  //  工单状态
  devOpsWorkState(data) {
    switch (String(data)) {
      case "1":
        return "未完成";
      case "2":
        return "已完成";
      default:
        return "--";
    }
  },
  //  工单类型
  devOpsWorkType(data) {
    switch (String(data)) {
      case "1":
        return "巡检";
      case "2":
        return "消缺";
      case "3":
        return "消警";
      case "4":
        return "抢修";
      default:
        return "--";
    }
  },
  //  事项状态
  matterState(data) {
    switch (String(data)) {
      case "1":
        return "未处理";
      case "2":
        return "已处理";
      default:
        return "--";
    }
  },
};
