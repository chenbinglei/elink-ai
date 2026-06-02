<template>
  <div ref="chartDom" style="width: 100%; height: 100%;"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, defineProps, watch } from 'vue';
import * as echarts from 'echarts';
import { formatter } from 'element-plus';

const props = defineProps({
  pieData: {
    type: Array,
    default: () => [
      { value: 23, name: '离线' },
      { value: 1, name: '提示' },
      { value: 33, name: '次要' },
      { value: 23, name: '紧急' },
      { value: 33, name: '重要' }
    ],
  }
});
const chartDom = ref(null);
let myChart = null;
const data = ref([])
const initChart = () => {
  const option = {
    tooltip: {
      trigger: 'item',
      className: "custom-tooltip-box",
      // formatter: '{b}: {c} ({d}%)'
      formatter: (params) => {
        const html = `<div class="custom-tooltip-style">
      ${params.name}：${params.value} (${params.percent}%)
      </div>`;
        return html;


      }
    },
    series: [
      {
        name: '状态',
        type: 'pie',
        radius: ['30%', '33%'], // 环形饼图
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 3,
          fontWeight: 'bold'
        },
        label: {
          fontSize: 12,
          color: '#eee',
          show: true,
          formatter: '{nameStyle|{b}}   {valueStyle|{c}}',
          rich: {
            nameStyle: {
              color: '#fff',
              fontSize: 14,
              fontFamily: 'Microsoft YaHei, Microsoft YaHei',
            },
            valueStyle: {
              color: 'inherit',
              fontSize: 24,
              fontWeight: '400',
              fontFamily: 'Agency FB, Agency FB',
            }
          },
        },
        labelLine: {  // 引导线配置
          length: 60,  // 第一段线长度
          smooth: 0,   // 平滑度，0-1
          maxSurfaceAngle: 80,
          lineStyle: {
            width: 1,
            color: '#006FB4',
            type: 'solid'  // 可选: 'solid', 'dashed', 'dotted'
          }
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14
          }
        },
        data: data.value
      }
    ]
  };
  myChart.setOption(option);
};

onMounted(() => {
  myChart = echarts.init(chartDom.value);
  initChart();
  window.addEventListener('resize', myChart.resize);
});
watch(() => props.pieData, (newValue) => {
  data.value = newValue;
  if (myChart) initChart();
}, { immediate: true, deep: true })
onBeforeUnmount(() => {
  if (myChart) {
    window.removeEventListener('resize', myChart.resize);
    myChart.dispose();
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