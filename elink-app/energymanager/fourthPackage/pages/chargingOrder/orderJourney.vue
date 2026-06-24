<template>
  <view class="content_body">
    <view v-for="(item, index) in timeline" :key="item.timestamp"
      :class="{ 'last-item': index === timeline.length - 1 }">
      <view class="timeline_item">
        <view class="timeline_item_left">
          <uni-icons v-if="item.icon == 'CircleCheck'" type="checkbox" size="30" :color="item.color"></uni-icons>
          <uni-icons v-else type="close" size="30" :color="item.color"></uni-icons>
        </view>
        <view class="timeline_item_right">
          <view class="journeyNameTime">
            <view class="journeyName">{{ item.name }}</view>
            <view class="journeyTime">{{ $filters.moreData(returnDataInfo[item.timestamp]) }}</view>
          </view>
          <view v-if="item.content" class="content">{{ $filters.moreData(returnDataInfo[item.content]) }}</view>

        </view>
        <!-- <view class="timeline_item_left">
          {{ item.name }}</view>

        <view class="timeline_item_right">
          <view class="title">{{ $filters.moreData(returnDataInfo[item.timestamp]) }}</view>
          <view v-if="item.content" class="content">{{ $filters.moreData(returnDataInfo[item.content]) }}</view>
        </view> -->
      </view>
      <view class="bottom_line"></view>
    </view>
  </view>
</template>

<script setup>
import { ref, watch } from 'vue'
const props = defineProps({
  orderInfo: {
    type: Object,
    default: () => {
      return {}
    }
  },
  isDisCharging: {
    type: Boolean,
    default: false
  }
})

// 👇 你的所有原始数据 完全不动！
const returnDataInfo = ref({})
const activities = ref([{ timestamp: 'createTime', icon: "CircleCheckFilled", color: "#007AFF", name: "创建订单" }])
const activities1 = ref([
  { timestamp: 'createTime', icon: "CircleCheck", color: "#007AFF", name: "创建订单" },
  { timestamp: 'startTime', icon: "CircleCheck", color: "#007AFF", name: props.isDisCharging ? "开始放电" : "开始充电" },
  { timestamp: 'updateTime', icon: "Clock", color: "#007AFF", name: props.isDisCharging ? "正在放电" : "正在充电" },

])
// 充电完成
const activities2 = ref([
  { timestamp: 'createTime', icon: "CircleCheck", color: "#007AFF", name: "创建订单" },
  { timestamp: 'startTime', icon: "CircleCheck", color: "#007AFF", name: props.isDisCharging ? "开始放电" : "开始充电" },
  { timestamp: 'endTime', icon: "CircleCheck", color: "#007AFF", name: props.isDisCharging ? "结束放电" : "结束充电", content: "stopDetailReason" },
  { timestamp: 'updateTime', icon: "CircleCheckFilled", color: "#007AFF", name: "结算完成" },
])
// 启动失败
const activities3 = ref([
  { timestamp: 'createTime', icon: "CircleCheck", color: "#007AFF", name: "创建订单" },
  { timestamp: 'updateTime', icon: "CircleCloseFilled", color: "#FF1515", name: "启动失败", content: "stopDetailReason" },
])
// 异常中断
const activities4 = ref([
  { timestamp: 'createTime', icon: "CircleCheck", color: "#007AFF", name: "创建订单" },
  { timestamp: 'updateTime', icon: "CircleCloseFilled", color: "#FF1515", name: "异常中断-订单完成", content: "stopDetailReason" },
])
// 异常取消
const activities5 = ref([
  { timestamp: 'createTime', icon: "CircleCheck", color: "#007AFF", name: "创建订单" },
  { timestamp: 'updateTime', icon: "CircleCloseFilled", color: "#666666", name: "订单取消" },
])
// 预约
const activities6 = ref([
  { timestamp: 'createTime', icon: "CircleCheck", color: "#007AFF", name: "创建订单" },
  { timestamp: 'clockingTime', icon: "Clock", color: "#007AFF", name: "预约时间" },
])

// 统一渲染的时间线（页面上用这个）
const timeline = ref([])

// 监听逻辑（只修复逻辑，不碰数据）
watch(() => props.orderInfo, (newOrderInfo) => {
  returnDataInfo.value = newOrderInfo || {}
  const status = returnDataInfo.value.orderStatus
  // 映射你原本的变量：activities = 0/空，activities1=1，activities2=2 ... activities6=6
  const map = {
    0: activities,
    1: activities1,
    2: activities2,
    3: activities3,
    4: activities4,
    5: activities5,
    6: activities6,
  }

  // 赋值：完全使用你原来的数组，不做任何修改
  if (map[status] !== undefined) {
    timeline.value = map[status].value
  } else {
    timeline.value = activities.value // 默认
  }

  console.log('订单状态:', status)
  console.log('当前时间线数据:', timeline.value)
}, { deep: true, immediate: true })
</script>

<style lang="scss" scoped>
.content_body {
  box-sizing: border-box;
  padding: 24rpx 24rpx 16rpx 24rpx;
  margin: 0 auto;
}

.bottom_line {
  width: 1rpx;
  height: 90rpx;
  margin-top: 8rpx;
  background: rgba(0, 122, 255, 1);
  margin-left: 30rpx;
}

.timeline_item {
  display: flex;
  align-items: center;

  .timeline_item_left {
    display: flex;
    flex-direction: column;
    align-items: center;


  }

  .timeline_item_right {
    width: 80%;

    .journeyNameTime {
      display: flex;
      justify-content: space-between;
      align-items: center;


      .journeyName {
        align-items: center;
        color: #0a0a0a;
        font-weight: 500;
        font-size: 28rpx;
      }

      .journeyTime {
        color: #6a7282;
        font-weight: 400;
        font-size: 24rpx;
      }

    }


  }


  // .timeline_item_left {
  //   width: 40%;
  //   font-size: 14px;
  // }


}

.content {
  font-size: 24rpx;
  color: #4a5565;
  font-weight: 400;
  margin-top: 12rpx;
}

.last-item {
  margin-bottom: 0;

  .bottom_line {
    display: none;
  }
}
</style>
