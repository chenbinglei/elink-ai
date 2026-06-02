<template>
  <div ref="chartRef" style="width: 100%; height: 100%;"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount,defineProps,watch } from 'vue';
import * as echarts from 'echarts';
const props = defineProps({
  echartsData: {
    type: Array,
    default: () => [[],[]]
  }
});
const chartRef = ref(null);
let chartInstance = null;
const initChart = () => {
  if (!chartRef.value) return;
  chartInstance = echarts.init(chartRef.value);
  const option = {
    tooltip: {
       className: "custom-tooltip-box",
      trigger: 'axis',
      // formatter: '{b}<br/>{a}: {c}'
      formatter: (params) => { 
        console.log(params)
        let html = '<div class="custom-tooltip-style">';
        html += `<div class="custom-tooltip-title">${params[0].name}</div>`;
        html += '<div class="custom-tooltip-content">';
        params.forEach(item => {
          html += `<div class="custom-tooltip-item">
            <span class="custom-radio" style="background-color: ${item.color};"></span>
            <span>${item.seriesName}: ${item.value}</span>
          </div>`;
        });
        html += '</div></div>';
        return html;

      }
    },
    xAxis: {
      type: 'category',
      data:props.echartsData[0],
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
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        fontSize: 12,
        color: "#00CCFF"
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
        name: '告警数量',
        textStyle: {
          color: "#fff",
        }
      }]
    },
    series: [
      {
        name: '告警数量',
        type: 'line',
        data: props.echartsData[1],
        symbol: 'circle',
        symbolSize: 4,
        itemStyle: {
          color: '#00CCFF'
        },
        lineStyle: {
          width: 1
        }
      }
    ],
    grid: {
      left: '12%',
      right: '4%',
      top: "20%",
      bottom: "10%",
    }
  };
  
  chartInstance.setOption(option);
};
watch(() => props.echartsData, (val) => { 
  console.log(val)
  if(chartInstance && val) initChart();
},{immediate: true,deep: true})
onMounted(() => {
  initChart();
  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  if (chartInstance) {
    window.removeEventListener('resize', handleResize);
    chartInstance.dispose();
  }
});

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize();
  }
};
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

    .custom-tooltip-title {
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      font-size: 14px;
      color: #ffffff;
      text-align: left;
      font-style: normal;
    }
    .custom-tooltip-item {
        color: #ffffff;
      display: flex;
      align-items: center;
      justify-content: flex-start;
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