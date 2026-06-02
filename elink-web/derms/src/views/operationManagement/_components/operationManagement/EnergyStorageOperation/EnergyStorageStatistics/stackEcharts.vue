<template>
  <div ref="chart" class="echarts-container"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import * as echarts from 'echarts';

const props = defineProps({
  list: {
    type: Object,
    required: true,
    default: () => ({})
  },
});

const chart = ref(null);
let myChart = null;

// 初始化图表
const initChart = () => {
  if (!chart.value) return;
  myChart = echarts.init(chart.value);

  const option = getOption();
  myChart.setOption(option);

  window.addEventListener('resize', onResize);
};

// 获取图表配置
const getOption = () => {
  const { xAxisData, yAxisData } = props.list || { xAxisData: [], yAxisData: [] };
  return {
    grid: {
      left: '3%',
      right: '3%',
      bottom: '3%',
      containLabel: true
    },
    tooltip: {
      trigger: 'axis',
      className: "custom-tooltip-box",
      formatter: function (params) {
        let result = params[0].name + '<br/>';
        params.forEach(item => {
          result += item.marker + item.seriesName + ': ' + item.value.toFixed(2) + '<br/>';


        });
        let htmlText = `<div class='custom-tooltip-style'><div class="custom-tooltip-title">${result}</div>`;
        return htmlText;
      }
    },
    xAxis: {
      type: 'category',
      data: xAxisData
    },
    yAxis: {
      type: 'value',
      name: '单位: kWh',
      splitLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.3)',
          width: 1,
          type: 'dashed'
        }
      }
    },
    legend: {
      width: '80%',
      right: 'center',
      itemWidth: 8,
      itemHeight: 8,
      textStyle: {
        color: 'rgba(255, 255, 255, 0.7)',
        fontSize: 12
      }
    },
    series: yAxisData.map(item => ({
      name: item.name,
      type: 'bar',
      barWidth: '20%',
      data: item.data,
      stack: item.stack,
      itemStyle: {
        normal: {
          color: item.color
        }
      }
    }))
  };
};

// 窗口大小变化时调整图表
const onResize = () => {
  if (myChart) {
    myChart.resize();
  }
};


// 组件卸载前销毁图表
onBeforeUnmount(() => {
  if (myChart) {
    window.removeEventListener('resize', onResize);
    myChart.dispose();
  }
});
// 监听 props.list 变化并更新图表
watch(
  () => props.list,
  (newVal) => {            
    if (newVal && newVal.xAxisData && newVal.yAxisData) {
      initChart();
    }
  },
  { deep: true } // 关键：深度监听对象内部变化
);
onMounted(() => {
  initChart();
});
</script>

<style scoped>
.echarts-container {
  width: 100%;
  height: 400px; /* 固定高度 */
}
</style>