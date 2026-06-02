<template>
  <div class="echarts-wrapper">
    <!-- 图表容器 -->
    <div v-if="hasData" class="echarts-container" ref="chartRef" :style="{
      width: typeof width === 'number' ? width + 'px' : width,
      height: typeof height === 'number' ? height + 'px' : height,
    }"></div>

    <!-- 无数据提示 -->
    <div v-else class="no-data-placeholder">暂无数据</div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed, nextTick } from "vue";
import * as echarts from "echarts";

const props = defineProps({
  data: {
    type: Array,
    required: true,
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
  width: {
    type: [String, Number],
    default: "400px",
  },
  height: {
    type: [String, Number],
    default: "180px",
  },
});

// 图表DOM引用
const chartRef = ref(null);
// ECharts实例
let chartInstance = null;

// 从父组件数据中提取x轴和y轴数据
// 提取多系列数据
const extractChartData = () => {
  const seriesData = props.data.map((series) => ({
    name: series.name,
    type: "line",
    data: series.data.map((item) => ({
      ...item,
      value: parseFloat(item.value).toFixed(2),
    })),
  }));
  const formatTime = (timeStr) => {
    const date = new Date(timeStr);
    if (isNaN(date.getTime())) return timeStr; // 防止非法时间
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    return `${hours}:${minutes}`;
  };
  const xAxisData =
    props.data[0]?.data.map((item) => formatTime(item.time)) || [];
  return { seriesData, xAxisData };
};

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return;

  // 销毁旧实例避免内存泄漏
  if (chartInstance) {
    chartInstance.dispose();
  }

  // 创建新实例
  chartInstance = echarts.init(chartRef.value, props.theme);

  // 提取图表数据
  const { xAxisData, seriesData } = extractChartData();

  // 构建基础配置
  const baseOption = {
    tooltip: {
      trigger: "axis",
      className: "custom-tooltip-box",
      formatter: (params) => {
        const xAxisValue = params[0]?.name || ""; // 获取 x 轴值

        let html = `<strong style="color:#fff;font-weight:bold;">${xAxisValue}</strong><br/>`;

        params.forEach((p) => {
          const color = getColorBySeriesName(p.seriesName);
          html += `
       <div><span style="
            display:inline-block;
            margin:2px 8px 2px 0;
            border-radius:50%;
            width:6px;
            height:6px;
            background:${color};
          "></span>
          <span style="color:#fff;font-size:14px;">${p.seriesName}： ${p.value}</span></div>
        `;
        });
        let htmlText = `<div class='custom-tooltip-style'><div class="custom-tooltip-title">${html}</div>`;
        return htmlText;
        //  return (
        //   `<span style="display:inline-block;margin:2px 8px 2px 0;border-radius:50%;width:6px;height:6px;"></span>` +
        //   `<strong>${xAxisValue}</strong><br/>` +
        //   params
        //     .map((p) => {
        //       const color = getColorBySeriesName(p.seriesName); // 正确获取系列颜色
        //       return `
        //     <span style="
        //       display:inline-block;
        //       margin:2px 8px 2px 0;
        //       border-radius:50%;
        //       width:6px;
        //       height:6px;
        //       background:${color};
        //     "></span>
        //     ${p.seriesName}: ${p.value}`;
        //     })
        //     .join("<br>")
        // );
      },
    },
    legend: {
      top: "-5",
      right: "10%",
      textStyle: {
        color: "#ffffff",
      },
      rich: props.data.reduce((acc, series) => {
        acc[series.name] = {
          color: getColorBySeriesName(series.name),
        };
        return acc;
      }, {}),
      //data: props.data.map((item) => item.name),
    },
    grid: {
      x: 50,
      y: 25,
      x2: 30,
      y2: 10,
    },
    xAxis: {
      type: "category",
      data: xAxisData,
      axisTick: {
        show: false,
      },
      axisLabel: {
        show: false,
      },
      axisLine: {
        show: true,
        lineStyle: {
          color: "#005B8B",
        }
      },
    },
    yAxis: {
      type: "value",
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      splitLine: {
        show: true,
        lineStyle: {
          color: "#005B8B",
          type: "dashed",
        },
      },
      axisLabel: {
        color: "#00CCFF",
        fontSize: 12,
        formatter: "{value}",
      },
      // 修改以下配置
      name: "kW",
      nameLocation: "end", // 放在 Y 轴最末端
      nameGap: 13, // 距离 Y 轴线
      nameRotate: 0, // 竖直显示
      nameTextStyle: {
        fontSize: 14,
        color: "#00CCFF"
      },
    },
    series: seriesData.map((series) => ({
      ...series,
      showSymbol: false,
      lineStyle: {
        width: 2,
        color: getColorBySeriesName(series.name),
      },
      areaStyle: {
        opacity: 0.3,
        color: getColorBySeriesName(series.name, true),
      },
    })),
  };

  // 合并父组件提供的自定义配置
  const finalOption = {
    ...baseOption,
    ...props.chartOptions,
  };

  // 应用配置
  chartInstance.setOption(finalOption);

  // 监听窗口大小变化
  const resizeHandler = () => {
    if (chartInstance) {
      chartInstance.resize();
    }
  };

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

const SERIES_COLORS = {
  光伏功率: "#00CCFFFF",
  充电功率: "#00CCFFFF",
  放电功率: "#E99D45FF",
  V2G: "#E99D45FF",
};
// 根据 name 返回对应颜色
const getColorBySeriesName = (name, isArea = false) => {
  const color = SERIES_COLORS[name] || "#00CCFF";
  return color;
};

// 判断是否有有效数据
const hasData = computed(() => {
  if (!props.data || !Array.isArray(props.data) || props.data.length === 0) {
    return false;
  }
  // 检查至少有一个 series 的 data 是非空数组
  return props.data.some(
    (series) => Array.isArray(series.data) && series.data.length > 0
  );
});
let prevHasData = false;

watch(
  [() => props.data, () => props.name],
  () => {
    nextTick(() => {
      initChart();
    });
  },
  { deep: true }
);

// 组件挂载时初始化图表
onMounted(() => {
  initChart();
});
</script>

<style scoped lang="scss">
.echarts-container {
  height: 150px;
  width: 400px;
  overflow: visible;
  /* 或 hidden，视需求 */
}

.echarts-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.no-data-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  min-height: 150px;
  color: #999;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
  text-align: center;
  box-sizing: border-box;
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
