<template>
  <div ref="chartDom" style="width: 100%; height: 100%;"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, defineProps, watch } from 'vue';
import * as echarts from 'echarts';
const props = defineProps({
  echartsData: {
    type: Array,
    default: () => [
      { value: [10, 20], name: '点A' },
      { value: [30, 40], name: '点B' },
      { value: [50, 60], name: '点C' }
    ]
  }
});
const chartDom = ref(null); // 绑定 DOM 元素
let myChart = null; // 存储图表实例
const data = ref(null);
// 散点图配置
const initChart = () => {
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: function (params) {
        // params 是当前数据项的信息
        return `
          <div style="font-weight:bold">${params.name}</div>
          <div>累计时长(h):${params.value[1]}</div>
          <div>告警频次:${params.value[0]}</div>
        `;
      }, // 显示坐标值
    },
    xAxis: {
      type: 'value', // 数值轴
      name: '告警频次',
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
          color: '#00CCFF'
        }
      },
    },
    yAxis: {
      type: 'value',
      scale: 'value', 
      name: '累计时长(h)',
      axisTick: {
        show: false
      },
      splitLine: {
        lineStyle: {
          color: "#005B8B",
          type: 'dotted'    //设置网格线类型 dotted：虚线   solid:实线
        },
      },
      nameTextStyle: {
        fontSize: 12,
        color: "#00CCFF"
      },
      axisLabel: {
        fontSize: 12,
        color: "#00CCFF"
      },
      axisLine: {
        show: false,
        lineStyle: {
          color: '#00CCFF'
        }
      },
    },
    lineStyle: {
      width: 3,
    },
    grid: {
      left: "10%",
      right: "15%",
      top: "20%",
      bottom: "10%",
    },
    dataZoom: [
      {
        type: 'inside', // 内部缩放（支持滚轮）
        start: 0,
        end: 100,
        
      }
    ],
    series: [
      {
        name: '数据点',
        type: 'scatter',
        symbolSize: 10, // 点的大小
        data: data.value,
        label: {
          show: true,
          position: 'top',
          formatter: function (params) {
            return `${params.name}`
          },
          color: '#fff',
          fontSize: 12
        },
        itemStyle: {
          color: '#FFBB00', // 点的颜色
        }
      },
    ],
  };
  myChart.setOption(option);
};
watch(() => props.echartsData, (val) => {
  data.value = val
  if (myChart) initChart();
}, { immediate: true, deep: true })
// 初始化图表
onMounted(() => {
  myChart = echarts.init(chartDom.value);
  initChart();
  window.addEventListener('resize', myChart.resize); // 响应式调整
});

// 销毁图表
onBeforeUnmount(() => {
  if (myChart) {
    window.removeEventListener('resize', myChart.resize);
    myChart.dispose();
  }
});
</script>
<style>
  .el-tooltip__popper {
    border: none !important;
    box-shadow: none !important;
  }
</style>