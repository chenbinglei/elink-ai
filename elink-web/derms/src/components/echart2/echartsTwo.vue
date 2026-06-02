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
import {ref, onMounted, onUnmounted, defineProps, watch, onBeforeUnmount, reactive} from "vue";
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
    required: true,
    default: () => {
      return {
        name: "",
      };
    }
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
  width: {
    type: [String, Number],
    default: "400px",
  },
  height: {
    type: [String, Number],
    default: "180px",
  },
});
const initChart = (data) => {
  if (!chartRef.value) return;
  viewData.data= data;
  // 销毁旧实例避免内存泄漏
  if (chartInstance) {
    chartInstance.dispose();
  }
  // 创建新实例
  chartInstance = echarts.init(chartRef.value, props.theme);
  // 应用配置
  if(chartInstance)chartInstance.setOption(data);
}
// 监听props变化，更新图表
watch(
    [() => props.data, () => props.name],
    () => {
      if(!chartInstance){
        initChart(props.data);
      }else{
        viewData.data = props.data;
        chartInstance.setOption(viewData.data);
      }
    },
    {deep: true}
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