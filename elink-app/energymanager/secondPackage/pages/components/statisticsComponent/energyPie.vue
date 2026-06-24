<template>
  <view class="pie-containers">
    <view class="pie-echarts" style="width: 100%;height: 100%;position: relative;">
      <qiun-data-charts type="ring" :opts="pieOpts" :chartData="chartData" />
      <view class="pie-echarts-title" v-if="props.title">{{ props.title }}</view>
    </view>
  </view>
</template>

<script setup>
import { defineProps, ref, watch, computed } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: ''
  }
  ,
  piechartData: {
    type: Array,
    default: () => []
  }

})
const chartData = ref({});
const firstItemPercentage = computed(() => {
  if (props.piechartData.length === 0) return '0%'
  const value = Number(props.piechartData[0].value)
  return `${value}%`
})
// 监听 props.piechartData 变化
watch(() => props.piechartData, (newVal) => {
if(newVal.length > 0){
  chartData.value = {
    series: [
      {
        data: newVal.map(item => ({
          name: item.name,
          value: Number(item.value),
          color: item.color
        }))
      }
    ]
  }
}
}, { immediate: true }) // 立即执行一次，用于初始化
const pieOpts = ref({
  rotate: false,
  rotateLock: false,
  padding: [0, 0, 0, 0],
  dataLabel: false,
  enableScroll: false,
  legend: {
    show: false
  },
   title: {
    name: firstItemPercentage,
    fontSize: 16,
    color: "rgba(0, 0, 0, 1)"
  },
  subtitle: {
    name: '',
    fontSize: 16,
  },
  extra: {
    ring: {
      ringWidth: 10,
      activeOpacity: 0.5,
      activeRadius: 10,
      offsetAngle: 0,
      labelWidth: 0,
      border: false,
      borderWidth: 3,
      borderColor: "#FFFFFF",
      radius: ['80%', '100%']
    }
  }
});
</script>

<style lang="scss" scoped>
.pie-containers {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  text-align: center;
  height: 100%;

  .pie-echarts-title {
    position: absolute;
    top: 82%;
    width: 100%;
    color: rgba(0, 0, 0, 0.5);
    font-size: 28rpx;
  }
}
</style>