<template>
  <div ref="chartContainer" style="width: 100%; height: 100%;"></div>
</template>
<script setup>
import * as echarts from 'echarts';
import { ref, onMounted, onBeforeUnmount, defineProps,watch } from 'vue';
const props = defineProps({
  echartsData: {
    type: Array,
    default: () => [['0', '1-3h', '3-12h', '12-24h', '24-72h', '>72h'],[50, 120, 200, 150, 80, 30]]
  }
});
const chartContainer = ref(null);
const data = ref(null);
let chartInstance = null;
// 初始化图表
const initChart = () => {
  if (!chartContainer.value) return;
  chartInstance = echarts.init(chartContainer.value);
  // 图表配置
  const option = {
    tooltip: {
      trigger: 'axis',
      className: "custom-tooltip-box",
      axisPointer: {
        type: 'shadow'
      },
      // formatter: '{b}<br/>数量: {c}'
      formatter: (params) => {
        let html = '';
        params.forEach((item) => {
          html += `<div class="custom-tooltip-style">${item.axisValueLabel}</br>数量: ${item.value}</div>`;

        });
        return html;
      }

    },
    grid: {
      left: '12%',
      right: '10%',
      bottom: '20%',
      top: '20%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.value[0],
      splitLine: {
        show: false, // 显示 y 轴分割线（网格线）
      },
      axisLabel: {
        fontSize: 12,
        color: "#00CCFF"
      },
      axisLine: {
        show: true,
        lineStyle: {
          color: '#005B8B'
        }
      },
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value',
      nameTextStyle: {
        padding: [0, 0, 0, 40]
      },
      axisLine: {
        false: false,
        lineStyle: {
          color: '#00CCFF'
        }
      },
      splitLine: {
        lineStyle: {
          type: 'dotted',
          color: '#005B8B'
        }
      }
    },
    legend: {
      show: true,
      itemHeight: 1,
      top: "10%",
      right: "0%",
      color: "#fff",
      data: [{
        name: '持续时间',
        textStyle: {
          color: "#fff",
        }
      }]
    },
    grid: {
      left: "10%",
      right: "3%",
      top: "20%",
      bottom: "15%",
    },
    series: [{
      name: '持续时间',
      type: 'bar',
      barWidth: '25%',
      data: data.value[1], // 示例数据，请替换为实际数据
      itemStyle: {
        color:  new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: "#66FFCC" },
          { offset: 1, color: "#FFFFFF" },
        ])
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.3)'
        }
      }
    }]
  };

  chartInstance.setOption(option);
};
watch(() => props.echartsData, (val) => { 
  data.value = val
  if(chartInstance) initChart();
},{immediate: true,deep: true})
// 响应式调整图表大小
const resizeChart = () => {
  if (chartInstance) {
    chartInstance.resize();
  }
};
onMounted(() => {
  initChart();
  window.addEventListener('resize', resizeChart);
});
onBeforeUnmount(() => {
      window.removeEventListener('resize', resizeChart);
      if (chartInstance) {
        chartInstance.dispose();
      }
    });

</script>
<style scoped lang="scss">
/* 可以添加自定义样式 */
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
    color: #ffffff;
  }
}
</style>