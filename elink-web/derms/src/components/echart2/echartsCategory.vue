<template>
  <div class="echarts-container" ref="chartRef" @click="handleClick" :style="{
    width: typeof width === 'number' ? width + 'px' : width,
    height: typeof height === 'number' ? height + 'px' : height,
  }"></div>
</template>
<script setup>
import {
  ref,
  onMounted,
  onUnmounted,
  defineProps,
  watch,
  defineExpose,
  defineEmits,
  reactive,
  defineOptions,
  toRaw,
} from "vue";
import * as echarts from "echarts";
import { hexToRgba } from "./utils";

defineOptions({
  name: "component_chart_chartsCategory",
});
// 图表DOM引用
const chartRef = ref(null);
const viewData = reactive({
  data: null,
});
const props = defineProps({
  data: {
    type: Array,
    default: () => {
      let names = [];
      let data1 = {
        color: "#00FFF7",
        name: "发电能力",
        lineType: "dotted",
        datas: [],
      };
      let data2 = {
        color: "#FF6600",
        name: "预测负荷",
        lineType: "dotted",
        datas: [],
      };
      let data3 = {
        color: "#00FF00",
        name: "历史发电",
        lineType: "",
        datas: [],
      };
      let data4 = {
        color: "#00CCFF",
        name: "历史负荷",
        lineType: "",
        datas: [],
      };
      //渐变色定义
      data1.areaColor = {
        linear: [0, 0, 0, 1],
        colors: [
          { color: data1.color, offset: 0, alpha: 0.4 },
          { color: data1.color, offset: 0.5, alpha: 0.1 },
          { color: data1.color, offset: 1, alpha: 0 },
        ],
      };
      let datalist = [data1, data2, data3, data4];
      let time = new Date();
      // console.log(time.getHours())
      for (let i = 0; i <= 24; i += 4) {
        names.push(i + ":00");
        let num = ((Math.random() * 500) >> 0) + 500;

        data1.datas.push(num);
        data1.needArea = true;
        data2.datas.push(num - 200);

        if (i <= time.getHours()) {
          data3.datas.push(num - 140);
          data4.datas.push(num - 80);
        }
      }
      return {
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
  unit: {
    type: String,
    default: "kW",
  },
  chartOptions: {
    type: Object,
    default: () => ({}),
  },
  fontSize: {
    type: [String, Number],
    default: 12,
  },
  width: {
    type: [String, Number],
    default: "400px",
  },
  height: {
    type: [String, Number],
    default: "150px",
  },
});
// ECharts实例

const emit = defineEmits(["chartPieItem-click"]);
const handleClick = () => {
  emit("chartPieItem-click");
};
let chartInstance = null;
// EChartsCategory.vue 内部方法
const highlight = (index) => {
  if (!chartInstance) return;
  chartInstance.dispatchAction({
    type: 'highlight',
    seriesIndex: 0,
    dataIndex: index
  });
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
      fontSize: props.fontSize,
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

const getSeries = (
  datas,
  name,
  lineType,
  color,
  needArea,
  areaColor,
  symbol = null,
  symbolSize = 5,
  originData = null
) => {
  let s = {
    name: name,
    data: datas,
    itemStyle: {
      color: color,
    },
    showSymbol:
      originData.showSymbol === undefined ? false : originData.showSymbol,
    symbol: symbol ?? "emptyCircle",
    symbolSize: symbolSize,
    smooth: originData.smooth === undefined ? true : originData.smooth,
    width: 3,
    step: originData.step === undefined ? false : originData.step,
    label: {
      // show: true,
      formatter: `{c}${props.unit}`,
      fontSize: 18,
      fontFamily: "DINAlternate, DINAlternate-Bold",
      fontWeight: 300,
    },
    lineStyle: {
      width: 2,
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
  if (originData && originData.series) {
    if (originData.series.lineStyle != null) {
      for (let lineStyleKey in originData.series.lineStyle) {
        // console.log(lineStyleKey);
        s.lineStyle[lineStyleKey] = originData.series.lineStyle[lineStyleKey];
      }
    }
    // console.log(originData.series, originData.series.lineStyle);
  }
  // console.log("getSeries", s, originData);
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
  // let legend= originData.Legend;
  let o = {
    name: name,
    // icon: "rect",
    // symbol:  'circle',
    symbolSize: 3,
    textStyle: {
      color: color,
      fontSize: props.fontSize,
    },
  };
  if (originData.lineType != "") {
    // o.icon = "path://M10,10L30,10L30,20L10,20zM40,10L60,10L60,20L40,20zM70,10L90,10L90,20L70,20z";
  }
  return o;
};
const getOption2 = (datas) => {
  let option = {
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: datas.names,
      axisLine: {
        lineStyle: {
          color: "#063670",
        },
      },
      axisLabel: {
        fontSize: 14,
        color: "#00CCFF",
      },
    },
    yAxis: {
      type: "value",
      name: props.unit,
      nameTextStyle: {
        fontSize: props.fontSize,
        color: "#00CCFF",
      },
      axisLabel: {
        fontSize: props.fontSize,
        color: "#00CCFF",
      },
      axisLine: {
        lineStyle: {
          color: "#005B8B",
        },
      },
      splitLine: {
        lineStyle: {
          type: "dotted",
          color: "#005B8B",
        },
      },
    },
    // tooltip: {
    //   trigger: "axis",
    //   // triggerOn: 'item',
    //   textStyle: {
    //     fontSize: props.fontSize,
    //   },
    //   axisPointer: null,
    //   // axisPointer: {
    //   //   type: 'cross',
    //   //   label: {
    //   //     backgroundColor: '#6a7985'
    //   //   }
    //   // }
    // },
    tooltip: {
      trigger: "axis",
      className: "custom-tooltip-box",

      formatter: (params) => {
        const xAxisValue = params[0]?.name || ""; // 获取 x 轴值

        let html = `<strong style="color:#fff;font-weight:bold;">${xAxisValue}</strong><br/>`;
        params.forEach((p) => {
          html += `
       <div style="display:flex;align-items:center;"> 
        <div style="
          display:inline-block;
          margin:4px 4px 2px 0;
          border-radius:50%;
          width:10px;
          height:10px;
          background:${p.color};
        "></div>
        <div style="color:#fff;font-size:14px;">${p.seriesName}： ${p.value??'--'}</div>
       </div>
      `;
        });
        let htmlText = `<div class='custom-tooltip-style'><div class="custom-tooltip-title">${html}</div>`;
        return htmlText;
      },
    },
    legend: {
      itemHeight: 1,
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
  if (datas.legend) {
    for (let lineStyleKey in datas.legend) {
      if (lineStyleKey == "right") {
        delete option.legend["left"];
      }
      option.legend[lineStyleKey] = datas.legend[lineStyleKey];
    }
  }
  for (let i = 0; i < datas.datas.length; i++) {
    let o = datas.datas[i];
    if (!datas.hideLegend)
      option.legend.data.push(getLegend(o.name, "#ffffff", o));

    option.series.push(
      getSeries(
        o.datas,
        o.name,
        o.lineType,
        o.color,
        o.needArea,
        o.areaColor,
        o.symbol,
        o.symbolSize,
        o
      )
    );
  }
  if (datas.pointerInfo) {
    //pointerInfo value 保存x轴的值，marin 坐标

    option.xAxis.axisPointer = getAxisPointer(
      datas.pointerInfo.value,
      datas.pointerInfo.margin
    );
    //显示竖线，默认不显示tooltips
    option.tooltip.triggerOn = "none";
  }
  if (datas.tooltip) {
    for (let lineStyleKey in datas.tooltip) {
      option.tooltip[lineStyleKey] = datas.tooltip[lineStyleKey];
    }
  }
  //如果设置了tooltips触发，则竖线无法固定；
  //mousemove|click
  if (datas.triggerOn) {
    option.tooltip.triggerOn = datas.triggerOn;
  }

  return option;
};
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
  console.log(data);
  if (chartInstance == null) {
    initChart(data);
  } else {
    viewData.data = data;
    chartInstance.setOption(getOption2(data));
  }
};
defineExpose({ loadData, chartInstance, highlight });

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
.echarts-container {
  width: 100%;
  height: 180px;
}

::v-deep .custom-tooltip-box {
  padding: 0 !important;
  border: none !important;
  background-color: transparent !important;
  color: #ffffff;

  .custom-tooltip-style {

    background: rgba(0, 47, 78, 0.9);
    box-shadow: inset 0px 0px 8px 1px #00ccff;
    border-radius: 3px 3px 3px 3px;
    padding: 5px 15px;

    .custom-tooltip-title {
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      font-size: 14px;
      color: #ffffff;
      text-align: left;
      font-style: normal;
    }

    .custom-tooltip-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }


    .custom-radio {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      display: inline-block;
      margin-right: 5px;
      box-sizing: border-box;

    }



    .custom-tooltip-value {
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      margin-left: 20px;
      font-size: 14px;
      color: #ffffff;
      text-align: left;
      font-style: normal;
    }
  }
}
</style>