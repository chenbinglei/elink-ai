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
  type: {
    type: String,
    default: 'line',
    validator: (value) => ['line', 'bar'].includes(value)
  }
});

const chart = ref(null);
let myChart = null;

// 初始化图表
const initChart = () => {
  if (!chart.value) return;

  // 使用 SVG 渲染器以支持 tooltip.className
  myChart = echarts.init(chart.value, 'svg');

  const option = getOption();
  myChart.setOption(option);

  window.addEventListener('resize', onResize);
};

// 获取图表配置
const getOption = () => {
  const { xAxisData, seriesData, seriesData2, seriesData3, seriesData4 } = props.list;
  let series = [];
  if (props.type === 'bar-line') {
    series = [
      {
        name: "充电成本",
        data: seriesData,
        type: 'bar',
        barWidth: '15%',
        itemStyle: {
          normal: {
            color: '#0CE19E'
          }
        }
      },
      {
        name: '放电收入',
        data: seriesData2,
        type: 'bar',
        barWidth: '15%',
        itemStyle: {
          normal: {
            color: '#E1C50C'
          }
        }
      },
      {
        name: "收益",
        data: seriesData3,
        type: 'line',
        itemStyle: {
          normal: {
            color: '#1FD7FF'
          }
        },
        lineStyle: {
          width: 1,
          type: 'dashed' // 虚线
        }
      }
    ];
  } else if (props.type === 'line') {
    series = [
      {
        name: "累计收益",
        data: seriesData4,
        type: 'line',
        itemStyle: {
          normal: {
            color: '#1FD7FF'
          }
        },
        lineStyle: {
          width: 1,
        }
      },
    ]
  }else if (props.type === 'bar') { 
    series = [
      {
        name: "度电收益",
        data: seriesData,
        type: 'bar',
        barWidth: '15%',
        itemStyle: {
          normal: {
            color: '#0CE19E'
          }
        }
      },
    ]
  }
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
      data: xAxisData,

    },
    yAxis: {
      type: 'value',
      name: '元',
      splitLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.3)',
          width: 1,
          type: 'dashed'
        }
      }
    },
    legend: {
      right: 'center',
      itemWidth: 10,
      itemHeight: 8,
      textStyle: {
        fontSize: 12,
        color: 'rgba(255, 255, 255, 0.7)',
      }
    },

    series: series
  };
};

// 窗口大小变化时调整图表
const onResize = () => {
  if (myChart) {
    myChart.resize();
  }
};

// 组件挂载后初始化图表
onMounted(() => {
  initChart();
});

// 组件卸载前销毁图表
onBeforeUnmount(() => {
  if (myChart) {
    window.removeEventListener('resize', onResize);
    myChart.dispose();
  }
});

// 监听 props 变化，重新渲染图表
watch(
  () => [props.list, props.type],
  () => {
    if (myChart) {
      myChart.setOption(getOption());
    }
  }
);
</script>

<style scoped>
.echarts-container {
  width: 100%;
  height: calc(100% - 30px);
 
}
</style>