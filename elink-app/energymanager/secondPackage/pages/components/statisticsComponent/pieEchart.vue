<template>
  <view class="pie-containers">
    <view class="pie-echarts" style="width: 30%;height: 100%;position: relative;">
      <qiun-data-charts type="ring" :opts="pieOpts" :chartData="chartData" />
      <view class="pie-echarts-title" v-if="props.title">{{ props.title }}</view>
    </view>
    <view class="period_list">
      <view v-for="item in props.piechartData" :key="item.name" class="period_list_item">
        <view class="period_list_item_name">{{ item.name }}</view>
        <view class="period_list_item_list">
          <view class="period_list_item_circle" :style="{ backgroundColor: item.color }"></view>
          <view class="period_list_item_value">{{  $filters.numberUnit(item.value) }}</view>
          <view class="period_list_item_text">kWh</view>
        </view>
      </view>

    </view>
  </view>
</template>

<script setup>
import { defineProps, ref, watch } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  piechartData: {
    type: Array,
    default: () => []
  },
  sum: {
    type: [Number, String],
    default: 0
  }
})

// 1. 先定义配置项
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
    name: '',
    fontSize:16,
    color: "rgba(0, 0, 0, 1)"
  },
  subtitle: {
    name: 'kWh',
    fontSize: 14,
    color: "rgba(0, 0, 0, 0.5)"
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
})

// 2. 饼图数据
const chartData = ref({})

// 3. 监听 piechartData（安全）
watch(() => props.piechartData, (newVal) => {
  if (!newVal || newVal.length === 0) return
  
  chartData.value = {
    series: [{
      data: newVal.map(item => ({
        name: item.name || '',
        value: Number(item.value) || 0,
        color: item.color || '#ccc'
      }))
    }]
  }
}, { immediate: true })

// 4. 监听 sum（修复关键：必须等 pieOpts 存在）
watch(() => props.sum, (newSum) => {
  // 安全判断
  if (pieOpts.value && pieOpts.value.title) {
     pieOpts.value.title.name = String(newSum || 0)>= 10000 ? (newSum / 10000).toFixed(2) + '万' : newSum.toString();
  
  }
}, { immediate: true })
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
    top: 80%;
    width: 100%;
    color: rgba(0, 0, 0, 0.5);
    font-size: 28rpx;
  }

  .period_list {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    width: 68%;

    .period_list_item {
      width: 50%;
      box-sizing: border-box;
      font-size: 24rpx;
      text-align: left;
      margin-top: 16rpx;
      color: #000000;

      .period_list_item_name {
        font-size: 28rpx;
        font-weight: 400;
        color: rgba(0, 0, 0, 0.5);
      }

      .period_list_item_list {
        display: flex;
        align-items: center;
        margin-top: 11rpx;
      }

      .period_list_item_circle {
        width: 12rpx;
        height: 12rpx;
        border-radius: 50%;
      }

      .period_list_item_value {
        color: rgba(0, 0, 0, 1);
        font-weight: 700;
        font-size: 28rpx;
        margin-left: 21rpx;
      }

      .period_list_item_text {
        font-size: 24rpx;
        color: rgba(0, 0, 0, 0.5);
        font-weight: 400;
        margin-left: 16rpx;
      }
    }
  }
}
</style>