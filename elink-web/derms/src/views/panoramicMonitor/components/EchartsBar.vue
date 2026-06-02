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
import { ref, onMounted,onBeforeUnmount, defineProps, watch, reactive} from "vue";
import * as echarts from "echarts";
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
    required: true
  },
  name: {
    type: String,
    default: "",
  },
  theme: {
    type: String,
    default: "default",
  },
  chartOptions: {
    type: Object,
    default: () => ({}),
  },
  fontSize: {
    type: String,
    default: "12",
  },
  unit: {
    type: [String, Number],
    default: "kWh",
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
const getBarSeries = (datas, name, color, barWidth, originData) => {
  return {
    name: name,
    type: "bar",
    barWidth: barWidth ?? "15%",
    showBackground:originData.showBackground == undefined ? true : originData.showBackground,
    itemStyle: {
      barBorderRadius: [15, 15, 0, 0],
      color: color,
    },
    data: datas,
  };
};
const getLegend = (name, color, originData) => {
  let o = {
    name: name,
    icon: "rect",
    textStyle: {
      color: color,
      fontSize: props.fontSize,
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
      fontSize: props.fontSize,
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
      axisLabel: {
        fontSize: props.fontSize,
        color: "#FFFFFF",
      },
      axisLine: {
        lineStyle: {
          color: "#D4DBE266",
        },
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
          type: "dashed",
        },
      },
      nameTextStyle: {
        fontSize: props.fontSize,
        color: "#FFFFFF",
      },
      axisLine: {
        show: true,
        lineStyle: {
          width: 1,
          color: "#D4DBE266",
        },
      },
      axisLabel: {
        fontSize: props.fontSize,
        color: "#FFFFFF",
        position: "left",
      },
    },
    tooltip: {
      trigger: "axis",
      textStyle: {
        fontSize: 14,
      },
      axisPointer: null
    },
    dataZoom: [
      {
        type: "inside",
      },
    ],
    legend: {
      show: true,
      itemHeight: 14,
      itemWidth: 14,
      top: "0%",
      right: "0",
      data: [],
    },
    grid: datas.grid ?? {
      left: "15%",
      right: "0",
      top: "20%",
      bottom: "20%",
    },
    series: [],
  };
  for (let i = 0; i < datas.datas.length; i++) {
    let o = datas.datas[i];
    if (!datas.hideLegend) {
      option.legend.show = true;
      option.legend.data.push(getLegend(o.name, "#ffffff", o));
    }

    option.series.push(getBarSeries(o.datas, o.name, o.color, o.barWidth, o));
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
  return option;
};
const initChart = (data) => {
  if (!chartRef.value) return;
  if (chartInstance) chartInstance.dispose();
  chartInstance = echarts.init(chartRef.value, props.theme);
  viewData.data = data;
  chartInstance.setOption(getOption2(data));
};
// 监听props变化，更新图表
watch(
  [() => props.data, () => props.name],
  () => {
    initChart(props.data); // 完全重新初始化图表
  },
  { deep: true }
);
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize();
  }
}
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
  window.removeEventListener('resize', handleResize);
    if (resizeObserver)  resizeObserver.disconnect();
    if (chartInstance) chartInstance.dispose();
});
</script>