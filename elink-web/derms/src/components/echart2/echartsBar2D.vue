<template>
  <div
    class="echarts-container"
    ref="chartRef"
    :style="{
      width: typeof width === 'number' ? width + 'px' : width,
      height: typeof height === 'number' ? height + 'px' : height,
    }"
  ></div>
</template>


<script setup>
// https://www.makeapie.cn/echarts_content/xrkLGo_5dM.html
import {
  ref,
  onMounted,
  onUnmounted,
  defineProps,
  watch,
  defineExpose,
  nextTick,
  reactive,
} from "vue";
import * as echarts from "echarts";
import { hexToRgba } from "./utils";
// 图表DOM引用
const chartRef = ref(null);
const viewData = reactive({
  data: null,
});
const props = defineProps({
  data: {
    type: Array,
    required: true,
    default: () => {
      let color1 = "#FFFF00";
      let names = ["1", "2", "3"];
      let data1 = {
        color: color1,
        name: "发电能力",
        datas: [],
        barWidth: "15%",
      };
      // let data2 = { color: color2, name: "预测负荷", datas: [] ,barWidth:"15%"};
      //渐变色定义
      data1.areaColor = {
        linear: [0, 0, 0, 1],
        colors: [
          { color: data1.color, offset: 0, alpha: 0.4 },
          { color: data1.color, offset: 0.5, alpha: 0.1 },
          { color: data1.color, offset: 1, alpha: 0 },
        ],
      };
      data1.datas = [52.8, 56.92, 99.79];
      let datalist = [data1];
      return {
        hideLegend: true,
        names: names,
        datas: datalist,
      };
    },
  },
  name: {
    type: String,
    default: "功率数据",
  },
  theme: {
    type: String,
    default: "default",
  },
  chartOptions: {
    type: Object,
    default: () => ({}),
  },
  unit: {
    type: [String, Number],
    default: "kW",
  },
  unitx: {
    type: [String, Number],
    default: "",
  },
  width: {
    type: [String, Number],
    default: "400px",
  },
  height: {
    type: [String, Number],
    default: "180px",
  },
});

// ECharts实例
let chartInstance = null;

const getBarSeries = (datas, name, color, barWidth) => {
  return {
    name: name,
    type: "bar",
    barWidth: barWidth ?? "15%",
    showBackground: true,
    itemStyle: {
      color: color,
    },
    data: datas,
  };
};
const getSeries = (
  datas,
  name,
  lineType,
  color,
  needArea,
  areaColor,
  symbol = null,
  symbolSize = 5
) => {
  let s = {
    name: name,
    data: datas,
    itemStyle: {
      color: color,
    },
    symbol: symbol ?? "circle",
    symbolSize: symbolSize,
    smooth: true,
    width: 3,
    label: {
      // show: true,
      formatter: `{c}${props.unit}`,
      fontSize: 14,
      fontFamily: "DINAlternate, DINAlternate-Bold",
      fontWeight: 700,
    },
    lineStyle: {
      width: 3,
    },
    type: "line",
  };
  if (needArea) {
    s.areaStyle = {
      color: getAreaColor(color, areaColor),
    };
  }
  if (lineType) {
    s.lineStyle.type = lineType;
  }
  return s;
};
/**
 *
 * @param color 默认颜色
 * @param areaColor 区间颜色定义
 * @returns {LinearGradient | string}
 */
const getAreaColor = (color, areaColor) => {
  let o;
  if (areaColor) {
    if (typeof areaColor == "string") {
      o = areaColor;
    } else {
      let arr = [];
      for (let c of areaColor.colors) {
        arr.push({ color: hexToRgba(c.color, c.alpha), offset: c.offset });
      }
      o = new echarts.graphic.LinearGradient(
        areaColor.linear[0],
        areaColor.linear[1],
        areaColor.linear[2],
        areaColor.linear[3],
        arr
      );
    }
  } else {
    o = new echarts.graphic.LinearGradient(0, 0, 0, 1, [
      {
        offset: 0,
        color: hexToRgba(color, 0.5),
      },
      {
        offset: 0.5,
        color: hexToRgba(color, 0.2),
      },
      {
        offset: 1,
        color: hexToRgba(color, 0),
      },
    ]);
  }

  return o;
};

const getLegend = (name, color, originData) => {
  let o = {
    name: name,
    icon: "rect",
    textStyle: {
      color: color,
      fontSize: 14,
    },
  };
  if (originData.lineType != null) {
    o.icon =
      "path://M10,10L30,10L30,20L10,20zM40,10L60,10L60,20L40,20zM70,10L90,10L90,20L70,20z";
  }
  return o;
};

const getAxisPointer = (val, margin) => {
  let axisPointer = {
    value: val,
    snap: false,
    triggerTooltip: false,
    lineStyle: {
      color: "#00CCFF",
      type: "dotted",
      width: 2,
    },
    label: {
      show: true,
      color: "#00CCFF",
      fontSize: 14,
      // formatter: function (params) {
      //   return "";
      // },
      margin: margin,
      backgroundColor: "rgba(0,0,0,0)",
    },
    handle: {
      size: 0,
      show: true,
      color: "#7581BD",
    },
  };
  return axisPointer;
};

const getOption2 = (datas) => {
  let option = {
    xAxis: {
      type: "category",
      name: props.unitx,
      data: datas.names,
      axisTick: {
        show: false,
      },
      // axisLine: {
      //   show: false
      // },
      // axisLine: {
      //   show: false,
      //   lineStyle: {
      //     color: '#005B8B'
      //   }
      // },
      axisLabel: {
        fontSize: 13,
        color: "#00CCFF",
      },
      z: 10,
    },
    yAxis: {
      type: "value",
      name: props.unit,
      axisTick: {
        show: false,
      },
      splitLine: {
        lineStyle: {
          color: "#005B8B",
          type: "dashed", //设置网格线类型 dotted：虚线   solid:实线
        },
      },
      nameTextStyle: {
        fontSize: 14,
        color: "#00CCFF",
      },
      axisLabel: {
        fontSize: 12,
        color: "#00CCFF",
      },
      axisLine: {
        show: false,
        // lineStyle: {
        //   color: '#063670'
        // }
      },
    },
    tooltip: {
      trigger: "axis",
      // triggerOn: 'item',
      textStyle: {
        fontSize: 14,
      },
      axisPointer: null,
      // axisPointer: {
      //   type: 'cross',
      //   label: {
      //     backgroundColor: '#6a7985'
      //   }
      // }
    },
    dataZoom: [
      {
        type: "inside",
      },
    ],
    legend: {
      show: false,
      itemHeight: 14,
      top: "10%",
      left: "10%",
      data: [],
    },
    grid: datas.grid ?? {
      left: "5%",
      right: "3%",
      top: "25%",
      bottom: "15%",
    },
    series: [],
  };
  // console.log(datas);
  for (let i = 0; i < datas.datas.length; i++) {
    let o = datas.datas[i];
    if (!datas.hideLegend) {
      option.legend.show = true;
      option.legend.data.push(getLegend(o.name, "#ffffff", o));
    }

    option.series.push(getBarSeries(o.datas, o.name, o.color, o.barWidth));
  }
  // if(!datas.hideTooltip){
  //   option["tooltip"] = null;
  // }
  if (datas.pointerInfo) {
    //pointerInfo value 保存x轴的值，marin 坐标
    option.xAxis.axisPointer = getAxisPointer(
      datas.pointerInfo.value,
      datas.pointerInfo.margin
    );
    //显示竖线，默认不显示tooltips
    option.tooltip.triggerOn = "none";
  }
  // console.log("getOption2")
  // console.log(option)
  return option;
};

// // 从父组件数据中提取x轴和y轴数据
// // 提取多系列数据
// const extractChartData = () => {
//   const seriesData = props.data.map((series) => ({
//     name: series.name,
//     type: "line",
//     data: series.data.map((item) => item.value),
//     smooth: true,
//     symbol: "circle",
//     symbolSize: 8,
//   }));
//
//   const xAxisData = props.data[0]?.data.map((item) => item.time) || [];
//   return { seriesData, xAxisData };
// };

const initChart = (data) => {
  if (!chartRef.value) return;
  // 销毁旧实例避免内存泄漏
  if (chartInstance) {
    chartInstance.dispose();
  }
  // 创建新实例
  chartInstance = echarts.init(chartRef.value, props.theme);
  viewData.data = data;
  // 应用配置
  chartInstance.setOption(getOption2(data));

  // 监听窗口大小变化
  const resizeHandler = () => {
    if (chartInstance) {
      chartInstance.resize();
    }
  };

  window.removeEventListener("resize", resizeHandler);

  window.addEventListener("resize", resizeHandler);

  // 组件卸载时移除监听
  onUnmounted(() => {
    if (chartInstance) {
      chartInstance.dispose();
      chartInstance = null;
    }
    window.removeEventListener("resize", resizeHandler);
  });
};
const loadData = (data) => {
  // console.log(data);
  if (chartInstance == null) {
    initChart(data);
  } else {
    viewData.data = data;
    chartInstance.setOption(getOption2(data));
  }
};
defineExpose({ loadData });

// 监听props变化，更新图表
watch(
  [() => props.data, () => props.name],
  () => {
    initChart(props.data); // 完全重新初始化图表
  },
  { deep: true }
);

// 组件挂载时初始化图表
onMounted(() => {
  initChart(props.data);
});
</script>


<style scoped lang="scss">
</style>