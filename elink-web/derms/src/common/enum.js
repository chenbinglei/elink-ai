import { color } from "echarts";
import { getImageUrl } from "@/utils";

// 站点运行状态
export const SiteStateList = [
  { id: "1", name: "正常", color: "#11AB84" },
  // {id: 2, name: "预警", color: "#FAAD14"},
  { id: "3", name: "通信异常", color: "#FF7D1E" },
  { id: "4", name: "故障", color: "#FD393A" },
];

// 站点运营类型
export const SiteScenarioTypeList = [
  { id: "1", name: "光伏", icon: "icon-guangfu" },
  { id: "2", name: "储能", icon: "icon-chuneng" },
  { id: "3", name: "电桩", icon: "icon-dianzhuang" },
  { id: "4", name: "用能", icon: "icon-dianwang" },
  { id: "5", name: "变配电", icon: "icon-bianyaqi" },
  { id: "6", name: "换电", icon: "icon-fuzai" },
];
// 站点拓扑节点的数据
export const TpjdTypeList = [
  { id: "1", name: "光伏"  },
  { id: "2", name: "储能" },
  { id: "3", name: "电桩" },
  { id: "6", name: "换电" },
  { id: "7", name: "换电" },
];

//事件级别 
export const EventLevelList = [
  {
    value: 1,
    label: "次要告警",
    icon: getImageUrl('icon-warning-secImpor'),
    color: '#3BAAF5'
  },
  {
    value: 2,
    label: "重要告警",
    icon: getImageUrl('icon-warning-impor'),
    color: '#FFBB00'
  },
  {
    value: 3,
    label: "紧急告警",
    icon: getImageUrl('icon-warning-energy'),
    color: '#FF0000',
  },
  {
    value: 4,
    label: "提示告警",
    icon: getImageUrl('icon-warning-tip'),
    color: '#34E800'
  },
  {
    value: 5,
    label: "离线告警",
    icon: getImageUrl('icon-warning-lixian'),
    color: '#aaa'
  }
];
//告警状态
export const AlarmStatusList = [
  {
    value: 0,
    label: "未修复",
  },
  {
    value: 1,
    label: "已修复",
  },
];

// 曲线数据类型映射
export const CurveDataTypeMap = {
  1: "光伏系统功率",
  2: "光伏系统发电量",
  3: "光伏逆变器功率",
  4: "光伏逆变器发电量",
  5: "光伏气象站辐照度",
  6: "光伏气象站温度",
  7: "光伏气象站辐照累积量",
  8: "储能系统功率",
  9: "储能系统发电量",
  10: "储能PCS功率",
  11: "储能PCS充放电量",
  12: "储能电池簇SOC",
  13: "储能电池簇总电压",
  14: "储能辅助设备温度",
  15: "储能辅助设备湿度",
  16: "电桩系统功率",
  17: "电桩系统充放电量",
  18: "电桩功率",
  19: "电桩充放电量",
  20: "储能电池簇电芯电压",
  21: "储能电池簇电芯温度",
};

//通信状态
export const TxStatusEnum = {
  0: "未注册",
  1: "在线",
  2: "维护",
  3: "故障",
  88: "离线",
  other: "未知",
};


//电桩工作状态 1-在线 2-维护 3-故障 88-离线
export const WorkStatusEnum = {
  1: "在线",
  2: "维护",
  3: "故障",
  88: "离线",
  other: "未知",
};

//运行状态
export const RunStateEnum = {
  0: "待机",
  1: "运行",
  2: "运行",
  3: "运行",
  other: "故障",
};

const createLinearColor = (endColor = "#66FFCC", startColor = "#ffffff") => ({
  type: "linear",
  x: 0,
  y: 0,
  x2: 0,
  y2: 1,
  colorStops: [
    {
      offset: 0,
      color: startColor, // 0% 处的颜色
    },
    {
      offset: 1,
      color: endColor, // 100% 处的颜色
    },
  ],
});

// 曲线颜色映射
export const CurveColorMap = {
  actualPower: "#34E800",
  theoryPower: "#00CCFF",
  actualQt: "#66FFCC",
  chargeQt: createLinearColor("#66FFCC"),
  dischargeQt: createLinearColor("#FFA366"),
  chargePower: "#66FFCC",
  dischargePower: "#FFA366",
  horizontalRadiation: "#66FFCC",
  inclinedRadiation: "#FFA366",
  radiantExposure: createLinearColor("#66FFCC"),
  obliqueIrradiation: createLinearColor("#FFA366"),
  ambientTemperature: "#34E800",
  outPower: "#34E800",
  reqPower: "#3BAAF5",
  cellVoltage: "#E99D45",
  cellTemperature: "#34E800",
};

export const FieldUnitMap = {
  chargePower: "kW",
  dischargePower: "kW",
  actualPower: "kW",
  theoryPower: "kW",
  actualQt: "kWh",
  chargeQt: "kWh",
  dischargeQt: "kWh",
  horizontalRadiation: "W/m²",
  inclinedRadiation: "W/m²",
  radiantExposure: "kWh/m²",
  obliqueIrradiation: "kWh/m²",
  ambientTemperature: "°C",
  outPower: "kW",
  reqPower: "kW",
  cellVoltage: "V",
  cellTemperature: "°C",
  batteryVoltage: "V",
  batterySoc: "%",
  batteryCurrent: "A",
  
};
// 电池簇 20
export const deviceDCCStatus = {
  "-1": {
    name: "未知",
    bac: "gray",
    icon: "",
  },
  0: {
    name: "待机",
    bac: "gray",
    icon: "",
  },
  1: {
    name: "禁充",
    bac: "red",
    icon: "",
  },
  2: {
    name: "禁放",
    bac: "red",
    icon: "",
  },
  3: {
    name: "故障",
    bac: "red",
    icon: "",
  },
  4: {
    name: "告警",
    bac: "red",
    icon: "",
  },
  5: {
    name: "充电",
    bac: "green",
    icon: "",
  },
  6: {
    name: "放电",
    bac: "green",
    icon: "",
  }
}
export const deviceDCCStatusText = {
  "未知": {
    bac: "gray",
    icon: "",
  },
  "待机": {
    bac: "gray",
    icon: "",
  },
  "禁充": {
    bac: "red",
    icon: "",
  },
  "禁放": {
    bac: "red",
    icon: "",
  },
  "故障": {
    bac: "red",
    icon: "",
  },
  "告警": {
    bac: "red",
    icon: "",
  },
  "充电": {
    bac: "green",
    icon: "",
  },
 "放电": {
    bac: "green",
    icon: "",
  },
  "停机": {
    bac: "red",
    icon: "",
  },
  "休眠": {
    bac: "gray",
    icon: "",
  },
}
// 电枪 28-30
export const deviceDQStatus = {
  "-1": {
    name: "未知",
    bac: "gray",
    icon: "",
  },
  1: {
    name: "充电",
    bac: "green",
    icon: "",
  },
  2: {
    name: "放电",
    bac: "yellow",
    icon: "",
  },
  3: {
    name: "空闲",
    bac: "skyblue",
    icon: "",
  },
  4: {
    name: "占用",
    bac: "yellow",
    icon: "",
  },
  5: {
    name: "故障",
    bac: "red",
    icon: "",
  },
  6: {
    name: "离线",
    bac: "gray",
    icon: "",
  },
  7: {
    name: "未注册",
    bac: "gray",
    icon: "",
  },
  8: {
    name: "预约中",
    bac: "skyblue",
    icon: "",
  }
}
export const deviceDQStatusText = {
  "未知": {
    bac: "gray",
    icon: "",
  },
  "充电": {
    bac: "green",
    icon: "",
  },
  "放电": {
    bac: "yellow",
    icon: "",
  },
  "空闲": {
    bac: "skyblue",
    icon: "",
  },
  "占用": {
    bac: "yellow",
    icon: "",
  },
  "故障": {
    bac: "red",
    icon: "",
  },
  "离线": {
    bac: "gray",
    icon: "",
  },
  "未注册": {
    bac: "gray",
    icon: "",
  },
  "预约中": {
    bac: "skyblue",
    icon: "",
  }
}
// 其他设备状态  -1-未知 0-未注册 1-在线 2-故障 88-离线
export const deviceStatus = {
  "-1": {
    name: "未知",
    bac: "gray",
    icon: "",
  },
  0: {
    name: "未注册",
    bac: "gray",
    icon: "",
  },
  1: {
    name: "在线",
    bac: "green",
    icon: "",
  },
  2: {
    name: "故障",
    bac: "red",
    icon: "",
  },
  88: {
    name: "离线",
    bac: "gray",
    icon: "",
  }
}
export const deviceStatusText = {
  "未知": {
    bac: "gray",
    icon: "",
  },
  "未注册": {
    bac: "gray",
    icon: "",
  },
  "在线": {
    bac: "green",
    icon: "",
  },
  "故障": {
    bac: "red",
    icon: "",
  },
  "离线": {
    bac: "gray",
    icon: "",
  }
}