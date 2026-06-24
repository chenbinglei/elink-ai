<template>
  <view class="time-containers">
    <view class="time-select">
      <view v-for="item in timeSelectList" :key="item.value" class="time-select-item" :class="{ active: item.value === selectedValue }"
        @click="selectTimeType(item.value)">
        {{ item.label }}
      </view>
    </view>
    <view class="startAndEndDate">
      <view class="dateTime" :class="{ null_bg_color: oldSelectFromData.startDate }" @click="clickSelectTime(1)">
        <text class="null_time" v-if="!oldSelectFromData.startDate">请选择日期</text>
        <text class="timer">{{ oldSelectFromData.startDate }}</text>
      </view>
      <view class="text">~</view>
      <view class="dateTime" :class="{ null_bg_color: oldSelectFromData.endDate }" @click="clickSelectTime(2)">
        <text class="null_time" v-if="!oldSelectFromData.endDate">请选择日期</text>
        <text class="timer">{{ oldSelectFromData.endDate }}</text>
      </view>
    </view>
    <sm-time-selector ref="timeSelectorRef" @btnConfirm="btnConfirm"></sm-time-selector>
  </view>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
const emit = defineEmits(['update:startDate', 'update:endDate'])
const timeSelectorRef = ref(null)
const props = defineProps({
  resetList: {
    type: Boolean,
    default: false
  },
  typeName: {
    type: String,
    default: ''
  },
  timeSelectList: {
    type: Array,
    default: () => []
  }
})
const timeSelectList = ref([])
const typeName = ref('')
const selectedValue = ref('')
const oldSelectFromData = ref({
  startDate: '',
  endDate: ''
})
const operateTimeTypeBtn = ref('')
const selectTimeType = (item) => {
  selectedValue.value = item
  const now = new Date()
  let start = new Date()
  let end = new Date()

  if (selectedValue.value === '0') {
    // 今日
    start.setHours(0, 0, 0, 0)
    end.setHours(23, 59, 59, 999)
  } else if (selectedValue.value === '1') {
    // 昨日
    start.setDate(now.getDate() - 1)
    start.setHours(0, 0, 0, 0)

    end.setDate(now.getDate() - 1)
    end.setHours(23, 59, 59, 999)
  } else if (selectedValue.value === '2') {
    // 近7天
    start.setDate(now.getDate() - 6)
  } else if (selectedValue.value === '3') {
    // 近1月
    start.setDate(now.getDate() - 30)
  } else if (selectedValue.value === '4') {
    // 近3月
    start.setDate(now.getDate() - 90)
  } else if (selectedValue.value === '5') {
    // 近6月
    start.setMonth(now.getMonth() - 180)
  }
  oldSelectFromData.value.startDate = formatDate(start)
  oldSelectFromData.value.endDate = formatDate(end)
  emit('update:startDate', oldSelectFromData.value.startDate)
  emit('update:endDate', oldSelectFromData.value.endDate)
}

const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}
const clickSelectTime = (operateTimeType) => {
  selectedValue.value = ''
  let timer = operateTimeType === 1 ? oldSelectFromData.value.startDate : oldSelectFromData.value.endDate;
  timeSelectorRef.value.openSelect(timer);
  operateTimeTypeBtn.value = operateTimeType
}
const btnConfirm = (data) => {
  if (operateTimeTypeBtn.value === 1) {
    oldSelectFromData.value.startDate = data
    emit('update:startDate', data)
  } else if (operateTimeTypeBtn.value === 2) {
    oldSelectFromData.value.endDate = data
    emit('update:endDate', data)
  }
  const startDate = new Date(oldSelectFromData.value.startDate)
  const endDate = new Date(oldSelectFromData.value.endDate)
  // 开始时间不能晚于结束时间
  if (endDate < startDate) {
    uni.showToast({
      title: '结束时间不能晚于开始时间',
      icon: 'none'
    })
    oldSelectFromData.value.endDate = ''
    return
  }
  if (typeName.value === 'alarm') {
    // 开始时间和结束时间时间跨度不得超过一年
    const diffInMs = endDate - startDate
    const diffInYears = diffInMs / (1000 * 60 * 60 * 24 * 365)


    if (diffInYears > 1) {
      uni.showToast({
        title: '时间跨度不能超过一年',
        icon: 'none'
      })
      const oneYearLater = new Date(startDate)
      oneYearLater.setFullYear(startDate.getFullYear() + 1)

      oldSelectFromData.value.endDate = formatDate(oneYearLater)
      return
    }
  }

}
watch(() => props.resetList, (newVal) => {
  if (newVal) {
    selectedValue.value = timeSelectList.value[0].value
  }
})
onMounted(() => {
  timeSelectList.value = props.timeSelectList
  typeName.value = props.typeName
  // 默认选中第一个
  selectedValue.value = timeSelectList.value[0].value
  const now = new Date()
  let start = new Date()

  if (selectedValue.value === '2') {
    start.setDate(now.getDate() - 6)
  } else if (selectedValue.value === '3') {
    start.setDate(now.getDate() - 30)
  } else if (selectedValue.value === '4') {
    start.setDate(now.getDate() - 90)
  }
  oldSelectFromData.value.startDate = formatDate(start)
  oldSelectFromData.value.endDate = formatDate(now)
})
</script>

<style scoped lang="less">
.time-containers {
  width: 100%;
  .time-select {
    margin-top: 32rpx;
    display: flex;
    flex-wrap: wrap; // 允许换行
    justify-content: space-between;
    .time-select-item {
      padding: 1% 8%;
      border-radius: 30rpx;
      background: #f3f3f3;
      flex: 0 0 32%; // 每个 item 占 32% 宽度（留出间距）
      margin-bottom: 2%; // 用于上下间距
      text-align: center;
    }
    .active {
      background-color: rgba(195, 220, 255, 1);
      color: rgba(24, 119, 255, 1);
    }
  }
}
.startAndEndDate {
  display: flex;
  align-items: center;
  margin: 20rpx 0 24rpx 0;

  .dateTime {
    flex: 1;
    height: 72rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8rpx;
    border: 2rpx solid #107be9;

    .null_time {
      font-size: 28rpx;
      color: rgba(0, 0, 0, 0.1);
    }
  }

  .text {
    margin: 0 50rpx;
  }

  .null_bg_color {
    border: none;
    background: rgba(31, 116, 226, 0.1);

    .timer {
      font-size: 28rpx;
      color: rgba(0, 0, 0, 0.6);
    }
  }
}
</style>