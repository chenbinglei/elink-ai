/**
 * A utility type to extract the value types of an object.
 *
 * @template T - The object type from which to extract value types.
 * @typedef {T[keyof T]} ValueOf
 */
/**
 * @typedef {Object} ChartSearchPreset
 * @property {String} id   预设的id
 * @property {String} name  预设的名称
 * @property {[Number|String,String]|[[Number|String,String],[Number|String,String]]} defaultValue 默认值
 * @property {keyof PickerTypeFormatMap} pickerType  日期选择器类型
 * @property { ValueOf<typeof PickerTypeFormatMap>} format 前端日期展示格式 不设置的话默认从pickerType中映射
 * @property {keyof FormatIntervalMap} formatInterval 传给后端接口的时间格式间隔
 * @property {String} timeInterval 传给后端接口的数据时间间隔
 * @property {Number} chartAxisInterval chart的axis轴的刻度间隔点位数量
 *
 *
 *
 * @typedef {Object} BlockChartConfig
 * @property {Array<ChartSearchPreset>} presetList  曲线块的预设列表
 * @property {String} unit  曲线数据，如果不设置会使用曲线id code等字段去枚举中拿
 * @property {String} seriesType 曲线类型： line bar
 * @property {Boolean?} hidePicker 是否隐藏日期选择器，设置true  直接通过tab去操作
 */

const FormatIntervalMap = {
  1: "yyyy-MM-dd HH:mm:ss", // Full date and time
  2: "yyyy-MM-dd", // Date only
  3: "yyyy-MM", // Year and month
  4: "yyyy", // Year only
  5: "HH:mm:ss", // Time with seconds
  6: "HH:mm", // Time without seconds
};

const PickerTypeFormatMap = {
  year: "YYYY",
  years: "YYYY",
  month: "YYYY-MM",
  months: "YYYY-MM",
  date: "YYYY-MM-DD",
  dates: "YYYY-MM-DD",
  datetime: "YYYY-MM-DD HH:mm:ss",
  week: "YYYY-WW",
  datetimerange: "YYYY-MM-DD HH:mm:ss",
  daterange: "YYYY-MM-DD",
  monthrange: "YYYY-MM",
  yearrange: "YYYY",
};

//formatInterval时间格式间隔 1-(yyyy-MM-dd HH:mm:ss) 2-(yyyy-MM-dd) 3-(yyyy-MM) 4-(yyyy) 5-(HH:mm:ss) 6-(HH:mm)
//timeInterval数据时间间隔 s-秒 m-分钟 h-小时 d-天 n-月 y-年

const commonPresetMap = {
  date: [
    {
      id: "day",
      name: "单日",
      pickerType: "date",
      defaultValue: [0, "s"],
      formatInterval: 6,
      timeInterval: "1m",
      chartAxisInterval: 119,
      disabledConfig: {
        preset: "disableAfterToday",
      },
    },
  ],
  range: [
    {
      id: "day",
      name: "日",
      pickerType: "daterange",
      defaultValue: [
        [-29, "day"],
        [0, "day"],
      ],
      formatInterval: 2,
      timeInterval: "1d",
      chartAxisInterval: "auto",
      disabledConfig: {
        preset: "disableAfterToday",
        duration: [100, "day"],
      },
    },
    {
      id: "month",
      name: "月",
      pickerType: "monthrange",
      defaultValue: [
        [-5, "month"],
        [0, "month"],
      ],
      formatInterval: 3,
      timeInterval: "1n",
      chartAxisInterval: "auto",
      disabledConfig: {
        preset: "disableAfterToday",
        duration: [100, "month"],
      },
    },
    {
      id: "year",
      name: "年",
      pickerType: "yearrange",
      defaultValue: [
        [-2, "year"],
        [0, "year"],
      ],
      formatInterval: 4,
      timeInterval: "1y",
      chartAxisInterval: "auto",
      disabledConfig: {
        preset: "disableAfterToday",
        duration: [100, "year"],
      },
    },
  ],
  sevenDayAndHalfYear: [
    {
      id: "sevenDay",
      name: "近7日",
      pickerType: false,
      defaultValue: [
        [-6, "day"],
        [0, "day"],
      ],
      formatInterval: 2,
      timeInterval: "1d",
      chartAxisInterval: "auto",
    },
    {
      id: "halfYear",
      name: "近半年",
      pickerType: false,
      defaultValue: [
        [-5, "month"],
        [0, "month"],
      ],
      formatInterval: 3,
      timeInterval: "1n",
      chartAxisInterval: "auto",
    },
  ],
  powerTab:[
    {
      id: "25",
      name: "有功功率",
    },
    {
      id: "26",
      name: "无功功率",
    },

  ]
};

/**
 * @type {{[string]: BlockChartConfig}}
 */
export const TypeBlockChartConfig = {
  // 光伏系统功率
  1: {
    presetList: commonPresetMap.date,
    unit: "kW",
    seriesType: "line",
    title: "功率曲线",
    icon: "powerCurve",
  },
  // 光伏系统发电量
  2: {
    presetList: commonPresetMap.range,
    unit: "kWh",
    seriesType: "bar",
    title: "发电量统计",
    icon: "eleGenStat",
  },
  // 光伏逆变器功率
  3: {
    presetList: commonPresetMap.date,
    unit: "kW",
    seriesType: "line",
    title: "功率曲线",
    icon: "powerCurve",
  },
  // 光伏逆变器发电量
  4: {
    presetList: commonPresetMap.range,
    unit: "kWh",
    seriesType: "bar",
    title: "发电量统计",
    icon: "eleGenStat",
  },
  // 光伏气象站辐照度
  5: {
    presetList: commonPresetMap.date,
    unit: "W/m²",
    seriesType: "line",
    title: "辐照度曲线",
    icon: "irradianceCurve",
  },
  // 光伏气象站温度
  6: {
    presetList: commonPresetMap.date,
    unit: "℃",
    seriesType: "line",
    title: "温度曲线",
    icon: "temperatureCurve",
  },
  // 光伏气象站辐照累积量
  7: {
    presetList: commonPresetMap.sevenDayAndHalfYear,
    unit: "MJ/m²",
    seriesType: "line",
    hidePicker: true,
    title: "辐照累积量",
    icon: "irradianceCumulative",
  },
  // 储能系统功率
  8: {
    presetList: commonPresetMap.date,
    unit: "kW",
    seriesType: "line",
    title: "功率曲线",
    icon: "powerCurve",
  },
  // 储能系统充放电量
  9: {
    presetList: commonPresetMap.range,
    unit: "kWh",
    seriesType: "bar",
    title: "充放电量",
    icon: "eleGenStat",
  },
  // 储能PCS功率
  10: {
    presetList: commonPresetMap.date,
    unit: "kW",
    seriesType: "line",
    title: "功率曲线",
    icon: "powerCurve",
  },
  // 储能PCS充放电量
  11: {
    presetList: commonPresetMap.range,
    unit: "kWh",
    seriesType: "bar",
    title: "充放电量",
    icon: "chargeAndDischargeAmount",
  },
  // 储能电池簇SOC
  12: {
    presetList: commonPresetMap.date,
    unit: "",
    seriesType: "line",
    title: "SOC曲线",
    icon: "SOCCurve",
  },
  // 储能电池簇总电压
  13: {
    presetList: commonPresetMap.date,
    unit: "V",
    seriesType: "line",
    title: "电池总电压",
    icon: "totalBatteryVoltage",
  },
  // 储能辅助设备温度
  14: {
    presetList: commonPresetMap.date,
    unit: "℃",
    seriesType: "line",
    title: "温度曲线",
    icon: "temperatureCurve",
  },
  // 储能辅助设备湿度
  15: {
    presetList: commonPresetMap.date,
    unit: "%RH",
    seriesType: "line",
    title: "湿度曲线",
    icon: "temperatureCurve",
  },
  // 电桩系统功率
  16: {
    presetList: commonPresetMap.date,
    unit: "kW",
    seriesType: "line",
    title: "功率曲线",
    icon: "powerCurve",
  },
  // 电桩系统充放电量
  17: {
    presetList: commonPresetMap.range,
    unit: "kWh",
    seriesType: "bar",
    title: "充放电量",
    icon: "chargeAndDischargeAmount",
  },
  // 电桩功率
  18: {
    presetList: commonPresetMap.date,
    unit: "kW",
    seriesType: "line",
    title: "功率曲线",
    icon: "powerCurve",
  },
  // 电桩充放电量
  19: {
    presetList: commonPresetMap.range,
    unit: "kWh",
    seriesType: "bar",
    title: "充放电量",
    icon: "chargeAndDischargeAmount",
  },
  // 储能电池簇电芯电压
  20: {
    presetList: commonPresetMap.date,
    unit: "mV",
    seriesType: "line",
    title: "电芯电压曲线",
    icon: "singleCellInformation",
  },
  // 储能电池簇电芯温度
  21: {
    presetList: commonPresetMap.date,
    unit: "℃",
    seriesType: "line",
    title: "电芯温度曲线",
    icon: "singleCellInformation",
  },
  // 电桩电枪功率曲线
  gun: {
    presetList: commonPresetMap.date,
    unit: "kW",
    seriesType: "line",
    title: "功率曲线",
    icon: "powerCurve",
  },
  24:{
    presetList: commonPresetMap.date,
    unit: "kW",
    seriesType: "line",
    title: "功率因数曲线",
    icon: "powerCurve",
  },
  meterPower:{
    presetList: commonPresetMap.date,
    unit: "kW",
    seriesType: "line",
    title: "功率曲线",
    icon: "powerCurve",
  },
   27:{
    presetList: commonPresetMap.range,
    unit: "kW",
    seriesType: "bar",
    title: "分时电量",
   icon: "chargeAndDischargeAmount",
  },
  // 3: {
  //   presetList: commonPresetMap.date,
  //   unit: "kW",
  //   seriesType: "line",
  //   title: "功率曲线",
  //   icon: "powerCurve",
  // },
};
