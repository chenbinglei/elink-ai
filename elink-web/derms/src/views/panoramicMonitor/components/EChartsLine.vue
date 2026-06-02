<template>
  <div
    class="echarts-container"
    ref="chartRef"
    @click="handleClick"
    :style="{
      width: typeof width === 'number' ? width + 'px' : width,
      height: typeof height === 'number' ? height + 'px' : height,
    }"
  ></div>
</template>
<script setup>
import {
  ref,
  onMounted,
  onBeforeUnmount,
  defineProps,
  watch,
  defineEmits,
  reactive,
  defineOptions,
} from "vue";
import * as echarts from "echarts";
defineOptions({
  name: "component_chart_chartsCategory",
});
// 图表使用
const chartRef = ref(null);
let chartInstance = null;
let resizeObserver = null;
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
  fontSize: {
    type: String,
    default: "12",
  },
  chartOptions: {
    type: Object,
    default: () => ({}),
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
const hexToRgba = (color, alpha) => {
  if (!color) {
    return "rgba(0,0,0,1)";
  }
  let c1 = parseInt(color.substr(1, 2), 16);
  let c2 = parseInt(color.substr(3, 2), 16);
  let c3 = parseInt(color.substr(5, 2), 16);
  return `rgba(${c1},${c2},${c3},${alpha})`;
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
    width: 1,
    step: originData.step === undefined ? false : originData.step,
    label: {
      formatter: `{c}${props.unit}`,
      fontSize: 12,
      fontFamily: "DINAlternate, DINAlternate-Bold",
      fontWeight: 700,
    },
    lineStyle: {
      width: 1,
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
        s.lineStyle[lineStyleKey] = originData.series.lineStyle[lineStyleKey];
      }
    }
  }
  return s;
};
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
    icon: "rect",
    // symbol:  'rect',
    // symbolSize: 20,
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
          color: "#D4DBE266",
        },
      },
      axisLabel: {
        fontSize: props.fontSize,
        color: "#ffffff",
      },
      axisTick: {
        show: false,
      },
    },
    yAxis: {
      type: "value",
      name: props.unit,
      nameTextStyle: {
        fontSize: props.fontSize,
        color: "#ffffff",
        align: "right",
      },
      axisLabel: {
        fontSize: props.fontSize,
        color: "#ffffff",
      },
      axisLine: {
        show: true,
        lineStyle: {
          width: 1,
          type: "solid",
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
    tooltip: {
      trigger: "axis",
      // triggerOn: 'item',
      textStyle: {
        fontSize: props.fontSize,
      },
      axisPointer: null,
      // axisPointer: {
      //   type: 'cross',
      //   label: {
      //     backgroundColor: '#6a7985'
      //   }
      // }
    },
    legend: {
      itemHeight: 2,
      top: "0%",
      right: "5%",
      data: [],
    },
    grid: datas.grid ?? {
      left: "15%",
      right: "5%",
      top: "20%",
      bottom: "16%",
    },
    series: [],
  };
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
  //如果设置了tooltips触发，则竖线无法固定；
  //mousemove|click
  if (datas.triggerOn) {
    option.tooltip.triggerOn = datas.triggerOn;
  }

  return option;
};
const initChart = (data) => {
  if (!chartRef.value) return;
  if (chartInstance) {
    chartInstance.dispose();
  }
  chartInstance = echarts.init(chartRef.value, props.theme);
  viewData.data = data;
  chartInstance.setOption(getOption2(data));
};

// 监听props变化，更新图表
watch(
  [() => props.data, () => props.name],
  () => {
    initChart(props.data);
  },
  { deep: true }
);
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize();
  }
};
// 组件挂载时初始化图表
onMounted(() => {
  initChart(props.data);
  resizeObserver = new ResizeObserver(handleResize);
  if (chartRef.value) {
    resizeObserver.observe(chartRef.value);
  }
});
// 销毁监听
onBeforeUnmount(() => {
  window.removeEventListener("resize", handleResize);
  if (resizeObserver) resizeObserver.disconnect();
  if (chartInstance) chartInstance.dispose();
});
</script>


<style scoped lang="scss">
</style>