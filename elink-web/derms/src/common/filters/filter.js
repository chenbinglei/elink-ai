export default {
    //  空数据处理
    moreData (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "":
                return "--";
            case "null":
                return "--";
            case "undefined":
                return "--";
            default:
                return filterStr;
        }
    },
    moreZero (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "":
                return "0";
            case "null":
                return "0";
            case "undefined":
                return "0";
            default:
                return filterStr;
        }
    },
    //  站点状态
    siteStatus (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "正常投运";
            case "2":
                return "关闭下线";
            case "3":
                return "维护中";
            case "4":
                return "建设中";
            default:
                return "未知";
        }
    },
    //  白名单 类型
    authorityType (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "用户";
            case "2":
                return "车辆";
            default:
                return "未知";
        }
    },
    // 建筑场所
    buildSite (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "居民区";
            case "2":
                return "公共机构";
            case "3":
                return "企事业单位";
            case "4":
                return "写字楼";
            case "5":
                return "工业园区";
            case "6":
                return "交通枢纽";
            case "7":
                return "大型文体设施";
            case "8":
                return "城市绿地";
            case "9":
                return "大型建筑配建停车场";
            case "10":
                return "路边停车位";
            case "11":
                return "城际高速服务区";
            case "12":
                return "国省道路沿线";
            case "13":
                return "城际快速公路沿线";
            case "14":
                return "其他";
            default:
                return "--";
        }
    },
    siteOpenType (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "对外开放";
            case "2":
                return "专用站点";
            default:
                return "--";
        }
    },
    // 订单状态
    orderStatus (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "0":
                return "未进行";
            case "1":
                return "充/放电中";
            case "2":
                return "充/放电完成";
            case "3":
                return "启动失败";
            case "4":
                return "订单挂起";
            case "5":
                return "订单取消";
            case "6":
                return "预约中";
            default:
                return "未知";
        }
    },
    // 订单异常类型
    abnormalType (status) {
        switch (String(status)) {
            case "0":
                return "无异常";
            case "1":
                return "时间异常";
            case "2":
                return "大额订单";
            case "3":
                return "电量异常";
            case "4":
                return "无效订单";
            case "5":
                return "费用异常";
            default:
                return status;
        }
    },
    // 订单异常类型 提示语
    abnormalTypeAlterText (status) {
        switch (String(status)) {
            case "1":
                return "订单时长大于24h";
            case "2":
                return "订单总金额大于1000元";
            case "3":
                return "电量大于500度";
            case "4":
                return "电量小于1度";
            case "5":
                return "订单金额为0元";
            default:
                return status;
        }
    },
    // 是否
    whetherOrNot (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "是";
            case "2":
                return "否";
            default:
                return '--';
        }
    },
    // 占用订单状态
    occupyState (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "在途";
            case "2":
                return "待支付";
            case "3":
                return "已完成";
            case "4":
                return "异常";
            case "5":
                return "异常";
            default:
                return "未知";
        }
    },
    // 占位 支付状态
    occupyPaidMoney (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "已支付";
            case "2":
                return "未支付";
            default:
                return "未知";
        }
    },
    // 电桩类型
    pileType (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "28":
                return "交流";
            case "29":
                return "直流";
            case "30":
                return "V2G";
            default:
                return "--";
        }
    },
    // 账号类型
    accountType (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "充/放电卡";
            case "2":
                return "VIN码";
            case "3":
                return "手机号";
            default:
                return "--";
        }
    },
    // 开票状态
    invoicingState (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "已开发票";
            case "2":
                return "未开发票";
            default:
                return "--";
        }
    },
    // 启动方式
    pileRunMode (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
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
            case "7":
                return "离线卡启动";
            default:
                return "未知";
        }
    },
    // 设备连接状态
    deviceStatus (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "0":
                return "未注册";
            case "1":
                return "在线";
            case "2":
                return "维护";
            case "3":
                return "故障";
            case "88":
                return "离线";
            default:
                return "未知";
        }
    },
    // 电桩工作状态
    pileWorkStatus (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "-1":
                return "未知";
            case "1":
                return "运行";
            case "2":
                return "维护";
            case "3":
                return "故障";
            case "88":
                return "离线";
            default:
                return "未知";
        }
    },
    // 变压器 运行状态
    inverterRunState (status) {
        switch (String(status)) {
            case "0":
                return "待机";
            case "1":
                return "运行";
            case "2":
                return "运行";
            case "3":
                return "运行";
            case "4":
                return "故障";
            default:
                return "未知";
        }
    },
    // pcs工作状态
    pcsWorkStatus (status) {
        switch (String(status)) {
            case "0":
                return "停机";
            case "1":
                return "待机";
            case "2":
                return "运行";
            case "3":
                return "故障";
            default:
                return "未知";
        }
    },
    // 电池工作状态
    batteryWorkStatus (status) {
        switch (String(status)) {
            case "0":
                return "静置";
            case "1":
                return "放电";
            case "2":
                return "充电";
            default:
                return "未知";
        }
    },
    // 充电桩 类型
    pileDeviceType (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "直流";
            case "2":
                return "交流";
            default:
                return filterStr;
        }
    },
    // 枪工作状态
    gunWorkState (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "充电";
            case "2":
                return "放电";
            case "3":
                return "空闲";
            case "4":
                return "占用";
            case "5":
                return "故障";
            case "6":
                return "离线";
            case "7":
                return "未注册";
            case "8":
                return "预约";
            case "-1":
                return "未知";
            default:
                return "未知";
        }
    },
    // 枪原始工作状态
    gunOriginalWorkState (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "0":
                return "空闲";
            case "1":
                return "充电准备";
            case "2":
                return "充电中";
            case "3":
                return "占用";
            case "4":
                return "放电准备";
            case "5":
                return "放电中";
            case "7":
                return "预约";
            case "8":
                return "暂停";
            case "88":
                return "离线";
            case "255":
                return "故障";
            case "-1":
                return "未知";
            default:
                return "未知";
        }
    },
    // 接入类型
    accessType (data) {
        switch (String(data)) {
            case "1":
                return "直连设备";
            case "2":
                return "网关设备";
            case "3":
                return "网关子设备";
            default:
                return "--";
        }
    },
    // 设备运营状态
    operateStatus (data) {
        switch (String(data)) {
            case "0":
                return "未知";
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
    // 网关 工作状态
    gatewayWorkState (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "在线";
            case "2":
                return "故障";
            case "3":
                return "未注册";
            case "88":
                return "离线";
            default:
                return "未知";
        }
    },
    // 网关 子 设备类型
    gatewayDeviceType (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "充电桩设备";
            case "2":
                return "通信设备";
            case "3":
                return "光伏设备";
            case "4":
                return "储能设备";
            case "5":
                return "计量设备";
            case "6":
                return "配电设备";
            case "7":
                return "感知设备";
            case "8":
                return "智慧设备";
            case "9":
                return "视频监控设备";
            default:
                return filterStr;
        }
    },
    // 价格状态
    priceState (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "生效中";
            case "2":
                return "待生效";
            case "3":
                return "已失效";
            default:
                return filterStr;
        }
    },
    // 定价类型
    fixedType (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "全天同价";
            case "2":
                return "分时段定价";
            default:
                return filterStr;
        }
    },
    // 时段类型
    periodType (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "尖时";
            case "2":
                return "峰时";
            case "3":
                return "平时";
            case "4":
                return "谷时";
            case "5":
                return "深谷";
            case "6":
                return "全天";
            default:
                return filterStr ?? '--';
        }
    },
    // 生效结果
    takeResult (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "-1":
                return "执行超时";
            case "0":
                return "执行成功";
            case "1":
                return "执行失败";
            case "255":
                return "其他原因";
            case "500":
                return "平台处理报错";
            default:
                return filterStr ?? '--';
        }
    },
    // 国家标准
    nationalStandard (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "2011";
            case "2":
                return "2015";
            case "3":
                return "2011&2015";
            case "4":
                return "2023";
            default:
                return filterStr ?? '--';
        }
    },
    // 桩枪型号
    pileGunModel (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "家用插座（模式 2）";
            case "2":
                return "交流接口插座（模式 3，连接方式 B）";
            case "3":
                return "交流接口插头（带枪线，模式 3，连接方式C）";
            case "4":
                return "直流接口枪头（带枪线，模式 4）";
            default:
                return filterStr ?? '--';
        }
    },
    // 告警等级
    eventLevel (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "普通告警";
            case "2":
                return "重要告警";
            case "3":
                return "紧急告警";
            case "4":
                return "提示告警";
            case "5":
                return "离线告警";
            default:
                return filterStr ?? '--';
        }
    },
    // 告警状态
    alarmStatus (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "0":
                return "未修复";
            case "1":
                return "已修复";
            default:
                return filterStr ?? '--';
        }
    },
    // 反馈类型
    feedbackType (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "充电过程";
            case "2":
                return "发票开具";
            case "3":
                return "占位费";
            case "4":
                return "信息不符";
            case "5":
                return "事故";
            default:
                return filterStr ?? '--';
        }
    },
    // 反馈状态
    feedbackStatus (data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "待处理";
            case "2":
                return "处理中";
            case "3":
                return "已处理";
            default:
                return filterStr ?? '--';
        }
    },
    // 商户类型
    platformType (status) {
        switch (String(status)) {
            case "1":
                return "微信平台";
            case "2":
                return "支付宝平台";
            case "3":
                return "银联商户";
            default:
                return status ?? '--';
        }
    },
    // 用户状态
    userState (status) {
        switch (String(status)) {
            case "1":
                return "正常";
            case "2":
                return "冻结";
            case "3":
                return "注销";
            default:
                return '未知';
        }
    },
    // 申请状态
    applyState (status) {
        switch (String(status)) {
            case "1":
                return "申请注销";
            case "2":
                return "已注销";
            default:
                return status ?? '--';
        }
    },
    // 交易状态
    tradeStatus (status) {
        switch (String(status)) {
            case "1":
                return "处理中";
            case "2":
                return "处理成功";
            case "3":
                return "处理失败";
            default:
                return '未知';
        }
    },
    // 交易类型 --- 充电订单
    tradeCoType (status) {
        switch (String(status)) {
            case "1":
                return "充电预付";
            case "2":
                return "充电退款";
            default:
                return status ?? '--';
        }
    },
    // 交易类型 --- V2G钱包交易
    tradeV2GType (status) {
        switch (String(status)) {
            case "1":
                return "V2G收益存入";
            case "2":
                return "余额提现";
            default:
                return status ?? '--';
        }
    },
    // 补单状态
    repairStatus (status) {
        switch (String(status)) {
            case "0":
                return "挂单";
            case "1":
                return "自动恢复";
            case "2":
                return "人工恢复";
            default:
                return status ?? '--';
        }
    },
    // 支付方式
    payWay (status) {
        switch (String(status)) {
            case "1":
                return "免支付";
            case "2":
                return "微信支付";
            case "3":
                return "支付宝支付";
            case "4":
                return "钱包余额";
            default:
                return status ?? '--';
        }
    },
    // 放电支付方式
    disPayWay (status) {
        return "V2G钱包";
    },
    // 交易方式 1- 2- 3-
    tradeWay (status) {
        switch (String(status)) {
            case "1":
                return "微信支付";
            case "2":
                return "支付宝支付";
            case "3":
                return "银联商户";
            default:
                return status ?? '--';
        }
    },
    // 结算状态
    settlementState (status) {
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
                return status ?? '--';
        }
    },
    // 退款状态
    refundStatus (status) {
        switch (String(status)) {
            case "1":
                return "正常退款";
            case "2":
                return "退款异常";
            default:
                return status ?? '--';
        }
    },
    // 策略类型
    strategyType (status) {
        switch (String(status)) {
            case "1":
                return "综合智能策略";
            case "2":
                return "峰谷套利策略";
            case "3":
                return "削峰策略";
            case "4":
                return "定时策略";
            case "5":
                return "限电策略";
            case "6":
                return "变压器扩容策略";
            case "7":
                return "负载扩容策略";
            case "8":
                return "备用电源策略";
            default:
                return status ?? '--';
        }
    },
    // 协议类型
    protocolType (status) {
        switch (String(status)) {
            case "1":
                return "启动命令";
            case "2":
                return "启动响应";
            case "3":
                return "启动事件";
            case "4":
                return "停止命令";
            case "5":
                return "停止响应";
            case "6":
                return "停止事件";
            case "7":
                return "记录上报";
            case "8":
                return "日志数据上报";
            case "9":
                return "复位命令";
            case "10":
                return "复位响应";
            case "11":
                return "设置二维码命令";
            case "12":
                return "设置二维码响应";

            default:
                return status ?? '--';
        }
    },
    // 光伏类型
    pvType (status) {
        switch (String(status)) {
            case "0":
                return "分布式商业";
            case "1":
                return "分布式户用";
            case "2":
                return "集中式";
            default:
                return status ?? '--';
        }
    },
    // 消纳方式
    consumMode (status) {
        switch (String(status)) {
            case "1":
                return "自发自用余电上网";
            case "2":
                return "全额上网";
            case "3":
                return "离网自用";
            default:
                return status ?? '--';
        }
    },
    // 并网等级
    tiedGrade (status) {
        switch (String(status)) {
            case "0":
                return "0.4kV";
            case "1":
                return "10kV";
            case "2":
                return "20kV";
            case "3":
                return "35kV";
            case "4":
                return "110kV";
            case "5":
                return "220kV";
            default:
                return status ?? '--';
        }
    },
    // 组件类型
    moduleType (status) {
        switch (String(status)) {
            case "1":
                return "多晶";
            case "2":
                return "单晶";
            case "3":
                return "叠瓦";
            case "4":
                return "P型双面";
            case "5":
                return "N型双面";
            default:
                return status ?? '--';
        }
    },
    // 组串配置状态
    configStatus (status) {
        switch (String(status)) {
            case "1":
                return "未配置";
            case "2":
                return "已配置";
            default:
                return status ?? '--';
        }
    },
    // 文字替换  充/放  替换  充  放  单个字
    chargingDisText (value, powerWay = 1) {
        let text = powerWay === 1 ? "充" : "放";
        let text1 = powerWay === 1 ? "充满" : "放空";
        if (value) value = String(value).replaceAll("充/放", text);
        if (value) value = String(value).replaceAll("充满", text1);

        return value;
    },
};
