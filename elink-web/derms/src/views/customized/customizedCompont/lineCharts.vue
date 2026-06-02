<template>
  <div ref="chart" style="width:100%; height: 100%;"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, onUnmounted, watch, defineProps, nextTick } from 'vue';
import * as echarts from 'echarts';

const chart = ref(null);
let chartInstance = null;

const props = defineProps({
  websoctList: {
    type: Object,
    default: () => ({})
  },
  chooseTimeType: {
    type: [String, Number],
    default: 1
  }
});

const pvList = ref([]);
const storageList = ref([]);
const loadList = ref([]);
const batteryList = ref([]);
const timeList = ref([]);

// ==================== 监听 WebSocket 数据 ====================
watch(() => props.websoctList, (newVal) => {
  if (!newVal || !newVal.powerCurveAnalysis) return;

  const data = newVal.powerCurveAnalysis;
  // 自动加载【当前选中的时间类型】
  updateDataByType(Number(props.chooseTimeType), data);
}, { immediate: true });

// ==================== 监听时间切换 ====================
watch(() => props.chooseTimeType, (newVal) => {
  const type = Number(newVal);
  if (!props.websoctList?.powerCurveAnalysis) return;

  updateDataByType(type, props.websoctList.powerCurveAnalysis);
}, { immediate: true });

// ==================== 核心：根据时间类型更新图表数据 ====================
function updateDataByType (type, sumList) {
  if (!sumList) return;

  if (type === 1) {
    pvList.value = sumList.pvTodayPowerList || [];
    storageList.value = sumList.seTodayPowerList || [];
    loadList.value = sumList.loadTodayPowerList || [];
    batteryList.value = sumList.busTodayPowerList || [];
    timeList.value = sumList.todayTimeList || [];
  } else if (type === 2) {
    pvList.value = sumList.pvYestdayPowerList || [];
    storageList.value = sumList.seYestdayPowerList || [];
    loadList.value = sumList.loadYestdayPowerList || [];
    batteryList.value = sumList.busYestdayPowerList || [];
    timeList.value = sumList.yestdayTimeList || [];
  }

  // 数据更新后 → 刷新图表
  nextTick(() => updateChart());
}

// ==================== 初始化图表 ====================
function initChart () {
  if (chartInstance) chartInstance.dispose();
  chartInstance = echarts.init(chart.value);
  updateChart();
}

// ==================== 更新图表（统一入口） ====================
function updateChart () {
  if (!chartInstance) return;

  const option = {
    xAxis: {
      type: 'category',
      axisLabel: { color: '#fff' },
      axisLine: { lineStyle: { color: '#005599' } },
      axisTick: { show: false },
      data: timeList.value
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
      className: "custom-tooltip-box",
      formatter: (params) => {
        const xAxisValue = params[0]?.name || ""; // 获取 x 轴值

        let html = `<strong style="color:#fff;font-weight:bold;">${xAxisValue}</strong><br/>`;

        params.forEach((p) => {

          const color = p.color;
          html += `
       <div><span style="
            display:inline-block;
            margin:2px 8px 2px 0;
            border-radius:50%;
            width:6px;
            height:6px;
            background:${color};
          "></span>
          <span style="color:#fff;font-size:14px;">${p.seriesName}： ${p.value??'--'}</span></div>
        `;
        });
        let htmlText = `<div class='custom-tooltip-style'><div class="custom-tooltip-title">${html}</div>`;
        return htmlText;

      },
    },
    legend: { textStyle: { color: '#fff', fontSize: 14 } },
    grid: {
      left: '2%', right: '2%', top: '15%', bottom: '15%', containLabel: true
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#fff' },
      axisLine: { lineStyle: { color: '#005599' } },
      splitLine: { lineStyle: { color: '#005599' } }
    },
    series: [
      {
        data: pvList.value, type: 'line', name: '光伏功率', smooth: true, showSymbol: false,
        itemStyle: { color: 'rgba(255, 187, 0, 1)' }
      },
      {
        data: loadList.value, type: 'line', name: '负载功率', smooth: true, showSymbol: false,
        itemStyle: { color: 'rgba(255, 82, 72, 1)' }
      },
      {
        data: storageList.value, type: 'line', name: '储能功率', smooth: true, showSymbol: false,
        itemStyle: { color: 'rgba(0, 232, 131, 1)' }
      },
      {
        data: batteryList.value, type: 'line', name: '直流母线功率', smooth: true, showSymbol: false,
        itemStyle: { color: 'rgba(6, 162, 235, 1)' }
      }
    ]
  };

  chartInstance.setOption(option, true);
}

// ==================== 响应式 ====================
let resizeTimer = null;
const handleResize = () => {
  clearTimeout(resizeTimer);
  resizeTimer = setTimeout(() => {
    chartInstance?.resize();
  }, 100);
};

onMounted(() => {
  initChart();
  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
});

onUnmounted(() => {
  chartInstance?.dispose();
  chartInstance = null;
});
</script>
<style lang="scss" scoped>
::v-deep .custom-tooltip-box {
  padding: 0 !important;
  border: none !important;
  background-color: transparent !important;
  color: #ffffff;
  

  .custom-tooltip-style {

    background: rgba(4, 57, 90, 0.9);
    box-shadow: inset 0px 0px 8px 1px #00ccff;
    border-radius: 3px 3px 3px 3px;
    padding: 10px 15px;
    

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