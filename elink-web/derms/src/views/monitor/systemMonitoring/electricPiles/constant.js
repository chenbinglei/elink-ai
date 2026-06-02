// 后端接口字段和前端字段的映射
const keyReflectMap = {
  dcGunNum: (data) => {
    return {
      key: `dcGunNum_dcPileNum`,
      value: `${data.dcPileNum}/${data.dcGunNum}`,
    };
  },
  acGunNum: (data) => {
    return {
      key: `acGunNum_acPileNum`,
      value: `${data.acPileNum}/${data.acGunNum}`,
    };
  },
};
// 运行指标中的指标列表
const runningQuotaList = [
  {
    id: "capacity",
    name: "装机容量",
    unit: "kW",
  },
  {
    id: "pileNum",
    name: "电桩数量",
    unit: "个",
  },
  {
    id: "dcGunNum_dcPileNum",
    name: "直流桩/枪数",
    unit: "",
  },
  {
    id: "acGunNum_acPileNum",
    name: "交流桩/枪数",
    unit: "",
  },
  {
    id: "sumChargeQt",
    name: "累计充电量",
    unit: "kWh",
  },
  {
    id: "sumV2gQt",
    name: "累计放电量",
    unit: "kWh",
  },
  {
    id: "dayChargeQt",
    name: "今日充电量",
    unit: "kWh",
  },
  {
    id: "dayV2gQt",
    name: "今日放电量",
    unit: "kWh",
  },
  {
    id: "lastDayChargeQt",
    name: "昨日充电量",
    unit: "kWh",
  },
  {
    id: "lastDayV2gQt",
    name: "昨日放电量",
    unit: "kWh",
  },
];
//充电枪展示块中的指标列表

export const gunQuotaListLeft = [
  {
    id: "outPower",
    name: "有功功率",
    unit: "kW",
  },
  {
    id: "outVolt",
    name: "电压",
    unit: "V",
  },
  {
    id: "outCurrent",
    name: "电流",
    unit: "A",
  },
  {
    id: "batterySoc",
    name: "当前车辆SOC",
    unit: "%",
  },
  {
    id: "runTime",
    name: "当前充放时长",
    unit: "min",
  },
  {
    id: "totalQt",
    name: "当前充/放电量",
    unit: "kWh",
  },
];
export const gunQuotaListRight = [
  {
    id: "dayChargeQt",
    name: "今日充电量",
    unit: "kWh",
  },
  {
    id: "dayV2gQt",
    name: "今日放电量",
    unit: "kWh",
  },
];
//枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障
export const gunStatusMap = {
  "-1": {
    color: "#B0B0B0", // Gray for unknown
    text: "未知",
  },
  0: {
    color: "#8BC34A", // Light green for idle
    text: "空闲",
  },
  1: {
    color: "#FF9800", // Orange for charging preparation
    text: "充电准备",
  },
  2: {
    color: "#4CAF50", // Green for charging
    text: "充电中",
  },
  3: {
    color: "#FFC107", // Amber for occupied (connected)
    text: "占用(已连接)",
  },
  4: {
    color: "#2196F3", // Blue for discharging preparation
    text: "放电准备",
  },
  5: {
    color: "#03A9F4", // Light blue for discharging
    text: "放电中",
  },
  7: {
    color: "#FF5722", // Deep orange for reserving
    text: "预约",
  },
  8: {
    color: "#9E9E9E", // Dark gray for paused
    text: "暂停",
  },
  88: {
    color: "#9E9E9E", // Dark gray for offline
    text: "离线",
  },
  255: {
    color: "#F44336", // Red for fault
    text: "故障",
  },
};
//状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
export const gunWorkStateMap = {
  "-1": {
    color: "#B0B0B0", // Gray for unknown
    text: "未知",
  },
  1: {
    color: "#4CAF50", // Green for charging
    text: "充电",
  },
  2: {
    color: "#2196F3", // Blue for discharging
    text: "放电",
  },
  3: {
    color: "#8BC34A", // Light green for idle
    text: "空闲",
  },
  4: {
    color: "#FFC107", // Amber for occupied
    text: "占用",
  },
  5: {
    color: "#F44336", // Red for fault
    text: "故障",
  },
  6: {
    color: "#9E9E9E", // Dark gray for offline
    text: "离线",
  },
  7: {
    color: "#FF5722", // Deep orange for unregistered
    text: "未注册",
  },
  8: {
    color: "#3F51B5", // Indigo for reserving
    text: "预约中",
  },
};
/**
 * 启动充电 启动放电
 * 功率控制 停止充放
 */
export const gunOperationList = [
  {
    id: "startCharge",
    name: "启动充电",
    icon: "capacity",
  },
  {
    id: "startDischarge",
    name: "启动放电",
    icon: "capacity",
  },
  {
    id: "powerControl",
    name: "功率控制",
    icon: "capacity",
  },
  {
    id: "stopOperation",
    name: "停止充放",
    icon: "capacity",
  },
];

export { keyReflectMap, runningQuotaList };
