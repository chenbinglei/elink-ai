export default {
    //  空数据处理
    moreData (data) {
        switch (String(data)) {
            case "":
                return "--";
            case "null":
                return "--";
            case "undefined":
                return "--";
            case !isNaN(data * 1):
                return Number(data).toFixed(2);
            default:
                return data;
        }
    },
    // 租户状态
    tenantState (data) {
        switch (String(data)) {
            case "0":
                return "关闭";
            case "1":
                return "开启";
            default:
                return "--";
        }
    },
    // 角色状态
    userRole (data) {
        switch (String(data)) {
            case "1":
                return "管理员";
            case "2":
                return "普通用户";
            default:
                return "--";
        }
    },
    // 功能类型
    functionType (data) {
        switch (String(data)) {
            case "1":
                return "遥测";
            case "2":
                return "遥信";
            case "3":
                return "遥脉";
            case "4":
                return "遥控";
            case "5":
                return "遥调";
            default:
                return "--";
        }
    },
    // 事件来源类型
    functionSourceType (data) {
        switch (String(data)) {
            case "1":
                return "模型事件";
            case "2":
                return "故障定义";
            default:
                return "--";
        }
    },
    // 数据类型
    dataType (data) {
        switch (String(data)) {
            case "1":
                return "int32(整数)";
            case "2":
                return "int64(长整数型)";
            case "3":
                return "float(单精度浮点型)";
            case "4":
                return "double(双精度浮点型)";
            case "5":
                return "enum(枚举)";
            case "6":
                return "bool(布尔)";
            case "7":
                return "string(字符串)";
            case "8":
                return "array(数组) ";
            case "9":
                return "date(时间)";
            default:
                return "--";
        }
    },
    // 扩展属性类型
    reaType (data) {
        switch (String(data)) {
            case "1":
                return "数值";
            case "2":
                return "文字";
            case "3":
                return "选项";
            case "4":
                return "位置";
            case "5":
                return "开关";
            case "6":
                return "时间";
            case "7":
                return "文本";
            default:
                return "--";
        }
    },
    // 读写类型
    readWriteType (data) {
        switch (String(data)) {
            case "1":
                return "只读";
            case "2":
                return "读写";
            default:
                return "--";
        }
    },
    // 是否必填
    required (data) {
        switch (String(data)) {
            case "false":
                return "否";
            case "true":
                return "是";
            default:
                return "--";
        }
    },
    // 模型状态
    modelStatus (data) {
        switch (String(data)) {
            case "0":
                return "开发中";
            case "1":
                return "已发布";
            default:
                return "--";
        }
    },
    // 事件级别
    eventLevel (data) {
        switch (String(data)) {
            case "1":
                return "次要告警";
            case "2":
                return "重要告警";
            case "3":
                return "紧急告警";
            case "4":
                return "提示告警";
            case "5":
                return "离线告警";
            default:
                return data;
        }
    },
    // 事件状态
    eventStatus (data) {
        switch (String(data)) {
            case "0":
                return "未恢复";
            case "1":
                return "已恢复";
            default:
                return "--";
        }
    },
    // 设备状态
    deviceStatus (data) {
        switch (String(data)) {
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
                return "--";
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
    // 告警状态
    alarmStatus (data) {
        switch (String(data)) {
            case "1":
                return "无告警";
            case "2":
                return "有告警";
            case "3":
                return "普通告警";
            case "4":
                return "重要告警";
            case "5":
                return "紧急告警";
            default:
                return "--";
        }
    },
    // 电压等级
    voltageGrade (status) {
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
    // 存储策略
    strategyType (status) {
        switch (String(status)) {
            case "1":
                return "每次存储";
            case "2":
                return "变化存储";
            case "3":
                return "不存储";
            default:
                return '--';
        }
    },
    // 变量类型
    variableType (status) {
        switch (String(status)) {
            case "1":
                return "设备变量";
            case "2":
                return "场站变量";
            default:
                return '--';
        }
    },
    // 变量 数据来源 类型
    varDataSource (status) {
        switch (String(status)) {
            case "1":
                return "计算节点";
            case "2":
                return "模型功能点";
            default:
                return '--';
        }
    },
    // 节点日志类型
    nodeLogType (status) {
        switch (String(status)) {
            case "1":
                return "定时任务";
            case "2":
                return "数据补录";
            default:
                return '--';
        }
    },
    // 任务状态
    taskStatus (status) {
        switch (String(status)) {
            case "0":
                return "未开始";
            case "1":
                return "进行中";
            case "2":
                return "已完成";
            case "3":
                return "正常停止";
            case "4":
                return "异常停止";
            default:
                return status;
        }
    },
    // 站点状态
    siteStatus (status) {
        switch (String(status)) {
            case "1":
                return "正常投运";
            case "2":
                return "关闭下线";
            case "3":
                return "维护中";
            case "4":
                return "建设中";
            default:
                return '--';
        }
    },
    // 充电枪类型
    chargingGunType (status) {
        switch (String(status)) {
            case "1":
                return "家用插座";
            case "2":
                return "交流接口插座";
            case "3":
                return "交流接口插头";
            case "4":
                return "直流接口枪头";
            default:
                return '--';
        }
    },
    // 充电枪 国家标准
    nationalStandard (status) {
        switch (String(status)) {
            case "1":
                return "2011";
            case "2":
                return "2015";
            case "3":
                return "兼容2011和2015";
            case "4":
                return "2023";
            default:
                return '--';
        }
    },
    // 辅助电源
    auxPower (status) {
        switch (String(status)) {
            case "1":
                return "12V";
            case "2":
                return "24V";
            case "3":
                return "兼容12V和24V";
            default:
                return '--';
        }
    },
    // 商户类型
    platformType (status) {
        switch (String(status)) {
            case "1":
                return "微信平台";
            case "2":
                return "支付宝平台";
            default:
                return '--';
        }
    },
    // 资产权限类型
    authorityType (status) {
        switch (String(status)) {
            case "0":
                return "无权限";
            case "1":
                return "只读";
            case "2":
                return "读写";
            default:
                return '--';
        }
    },
    // 小程序类型
    appletType (status) {
        switch (String(status)) {
            case "1":
                return "微信";
            case "2":
                return "支付宝";
            default:
                return status;
        }
    },
    // 节点类型 0-计量节点 1-设备节点
    nodeType (status) {
        switch (String(status)) {
            case "0":
                return "计量节点";
            case "1":
                return "设备节点";
            default:
                return '--';
        }
    },
    // 拓扑节点类型
    toolNodeType (status) {
        switch (String(status)) {
            case "1":
                return "拓扑点";
            case "2":
                return "电网";
            case "3":
                return "变压器";
            case "4":
                return "关口点";
            case "5":
                return "计量点";
            case "6":
                return "逆变器";
            case "7":
                return "光伏组件";
            case "8":
                return "储能柜";
            case "9":
                return "负荷";
            case "10":
                return "充电桩";
            case "11":
                return "开关";
            case "12":
                return "车辆";
            default:
                return status;
        }
    },
    // 拓扑节点 --- 设备类型
    deviceNodeType (status) {
        switch (String(status)) {
            case "1":
                return "光伏";
            case "2":
                return "储能";
            case "3":
                return "电桩";
            case "4":
                return "其他";
            default:
                return status;
        }
    },
    //位置信息
    positionType (status) {
        switch (String(status)) {
            case "1":
                return "上";
            case "2":
                return "下";
            case "3":
                return "左";
            case "4":
                return "右";
            default:
                return status;
        }
    },
    // 电桩--- 固件类型
    pileFirmwareType (status) {
        switch (String(status)) {
            case '1':
                return 'V2G_1.0 TCP 控制板';
            case '2':
                return 'V2G_2.0 TCP 控制板';
            case '3':
                return 'V2G_3.0 TCP 控制板';
            case '4':
                return 'V2G_4.0 TPU 控制板';
            case '5':
                return 'V2G_4.0 CCU 控制板';
            case '6':
                return 'V2G_6.0 TCP 控制板';
            case '7':
                return 'V2G_7.0 TPU 控制板';
            case '8':
                return 'V2G_8.0 CCU 控制板';
            case '9':
                return 'V2G_9.0 TCP_BOOT 控制板';
            case '10':
                return 'V2G_10.0 TPU_BOOT 控制板';
            case '11':
                return 'V2G_11.0 CCU_BOOT 控制板';

            default:
                return status
        }
    },
    // 设备升级 状态
    deviceUpdateStatus (data) {
        switch (String(data)) {
            case "1":
                return "可用";
            case "2":
                return "不可用";
            default:
                return "--";
        }
    },
    // 设备任务升级状态
    taskUpgradeStatus (data) {
        switch (String(data)) {
            case "1":
                return "待执行";
            case "2":
                return "执行中";
            case "3":
                return "执行关闭";
            default:
                return "--";
        }
    },
    // 状态
    deviceTaskRecordStatus (data) {
        switch (String(data)) {
            case "0":
                return "无需升级";
            case "1":
                return "等待启动";
            case "2":
                return "下载中";
            case "3":
                return "下载失败";
            case "4":
                return "升级中";
            case "5":
                return "升级失败";
            case "6":
                return "升级成功";
            default:
                return "--";
        }
    },
}
