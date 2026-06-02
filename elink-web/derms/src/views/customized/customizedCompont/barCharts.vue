<template>
  <div>
    <div ref="chart" style="width:100%; height: 100%;"></div>
    <div v-if="showEmpty" class="no-list">暂无数据</div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, onUnmounted, watch, defineProps, nextTick, computed } from 'vue';

import * as echarts from 'echarts';

const props = defineProps({
  lineList: {
    type: Array,  // 这里必须是 Array！你之前写的 Object
    default: () => []
  },
  dataList: {
    type: Array,
    default: () => []
  },
});

// 是否显示空状态
const showEmpty = computed(() => {
  const list = props.lineList || [];
  return list.length === 0 || (list.every(item => !item.value?.length));
});

const chart = ref(null);
let chartInstance = null;

// 初始化图表
const initChart = () => {
  if (!chart.value) return;
  if (showEmpty.value) return;

  // 销毁旧实例
  if (chartInstance) chartInstance.dispose();

  chartInstance = echarts.init(chart.value);

  const option = {
    xAxis: {
      type: 'category',
      axisLabel: { color: '#fff' },
      axisLine: { lineStyle: { color: '#005599' } },
      axisTick: { show: false },
      data: props.dataList || []
    },
    legend: {
      top: "5%",
      icon: 'rect',
      itemWidth: 12,
      itemHeight: 12,
      textStyle: { color: '#fff' }
    },
    grid: {
      left: '12%',
      right: '6%',
      top: "20%",
      bottom: "10%",
      containLabel: true
    },
    tooltip: {
      trigger: 'axis',
      className: "custom-tooltip-box",
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(4, 57, 90, 0.9)',
      borderColor: '#00ccff',
      textStyle: { color: '#fff' },
      formatter: (params) => {
        if (!params || params.length === 0) return '';
        const xVal = params[0]?.name || '';
        let html = `<strong>${xVal}</strong><br/>`;
        params.forEach(p => {
          html += `<div style="margin:3px 0;">
            <span style="display:inline-block;width:6px;height:6px;border-radius:50%;background:${p.color};margin-right:6px;"></span>
            ${p.seriesName}：${p.value ?? '--'}
          </div>`;
        });
        return html;
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#fff' },
      axisLine: { lineStyle: { color: '#005599' } },
      splitLine: { lineStyle: { color: '#005599' } }
    },
    series: (props.lineList || []).map(item => ({
      data: item.value || [],
      type: 'bar',
      name: item.name,
      itemStyle: {
        color: item.opacityColor || item.color,
        borderColor: item.color,
        borderWidth: 1,
        borderRadius: 2
      },
      backgroundStyle: { color: 'rgba(16, 45, 78, 1)' }
    }))
  };

  chartInstance.setOption(option);
};

// 监听数据更新
watch(
  () => [props.lineList, props.dataList],
  () => nextTick(initChart),
  { deep: true, immediate: true }
);

// 窗口自适应
let resizeTimer = null;
const handleResize = () => {
  clearTimeout(resizeTimer);
  resizeTimer = setTimeout(() => chartInstance?.resize(), 100);
};

onMounted(() => {
  nextTick(initChart);
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
.no-list {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #54a1df;
}
</style>