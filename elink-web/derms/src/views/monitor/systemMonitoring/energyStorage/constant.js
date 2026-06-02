// 后端接口字段和前端字段的映射
const keyReflectMap = {};
// 运行指标中的指标列表
const runningQuotaList = [
  {
    id: "capacity",
    name: "装机容量",
    unit: "kWh",
  },
  {
    id: "pcsNum",
    name: "PCS数量",
    unit: "台",
  },
  {
    id: "batteryNum",
    name: "电池簇数量",
    unit: "簇",
  },
  {
    id: "batteryPackNum",
    name: "电池包数量",
    unit: "包",
  },
  {
    id: "batteryCellNum",
    name: "电芯数量",
    unit: "个",
  },
  {
    id: "chargeMagnification",
    name: "充放电倍率",
    unit: "",
  },
  {
    id: "lastDayEff",
    name: "昨日系统效率",
    unit: "%",
  },
  {
    id: "ratedPower",
    name: "额定功率",
    unit: "kW",
  },
  {
    id: "sumChargeQt",
    name: "累计充电量",
    unit: "kWh",
  },
  {
    id: "sumDischargeQt",
    name: "累计放电量",
    unit: "kWh",
  },
  {
    id: "sumChargeCycleNum",
    name: "充放电循环次数",
    unit: "次",
  },
];

export { keyReflectMap, runningQuotaList };
