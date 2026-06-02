// 后端接口字段和前端字段的映射
const keyReflectMap = {
  capacity: "capacity", // 装机容量 (kWh)
  inverterNum: "inverterCount", // 逆变器数量 (个)
  arrayArea: "arrayArea", // 阵列面积 (㎡)
  arrayInclination: "arrayTilt", // 阵列倾角 (°)
  accTotalQt: "totalGeneration", // 累计发电量 (万kWh)
  lastDayEff: "yesterdayEfficiency", // 昨日系统效率 (%)
  lastDayHours: "yesterdayUtilizationHours", // 昨日等效利用小时数 (h)
  lastDayLossDayQt: "yesterdayLossPower", // 昨日损失电量 (万kWh)
  totalDowntime: "totalDowntime", // 累计停机时长 (h)
  lastDayMaxPower: "yesterdayPeakPower", // 昨日峰值发电功率 (kW)
  co2Reduction: "co2Reduction", // 二氧化碳减排量 (千克)
  standardCoalReduction: "coalSaving", // 节约标煤量 (千克)
  treeReduction: "treeEquivalent", // 等效植树 (棵)
  operationStartTime: "operationStartTime", // 投运时间
};
// 运行指标中的指标列表
const runningQuotaList = [
  {
    id: "capacity",
    name: "装机容量",
    unit: "kWp",
  },
  {
    id: "inverterCount",
    name: "逆变器数量",
    unit: "个",
  },
  {
    id: "arrayArea",
    name: "阵列面积",
    unit: "㎡",
  },
  {
    id: "arrayTilt",
    name: "阵列倾角",
    unit: "°",
  },
  {
    id: "totalGeneration",
    name: "累计发电量",
    unit: "kWh",
  },
  {
    id: "yesterdayEfficiency",
    name: "昨日系统效率",
    unit: "%",
  },
  {
    id: "yesterdayUtilizationHours",
    name: "昨日等效利用小时数",
    unit: "h",
  },
  {
    id: "yesterdayLossPower",
    name: "昨日损失电量",
    unit: "kWh",
  },
  // {
  //   id: "totalDowntime",
  //   name: "累计停机时长",
  //   unit: "h",
  // },
  {
    id: "yesterdayPeakPower",
    name: "昨日峰值发电功率",
    unit: "kW",
  },
  {
    id: "co2Reduction",
    name: "二氧化碳减排量",
    unit: "千克",
  },
  {
    id: "coalSaving",
    name: "节约标煤量",
    unit: "千克",
  },
  {
    id: "treeEquivalent",
    name: "等效植树",
    unit: "棵",
  },
  // {
  //   id: "operationStartTime",
  //   name: "投运时间",
  //   unit: "",
  // },
];

export { keyReflectMap, runningQuotaList };
