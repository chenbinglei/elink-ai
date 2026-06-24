<template>
  <view class="time-containers">
    <view class="time-select">
      <view v-for="item in timeSelectList" :key="item.value" class="time-select-item"
        :class="{ active: item.value === selectedValue }" @click="selectTimeType(item.value)">
        {{ item.label }}
      </view>
    </view>

    <uni-popup ref="alertDialog" background-color="#fff" :close-on-click-overlay="false" class="custom-popup"
      borderRadius="20rpx">
      <view class="custom-date-wrap">
        <view class="date-title">自定义</view>

        <view class="date-item">
          <view class="date-label">开始日期</view>
          <view class="date-value" :class="{ null_bg_color: oldSelectFromData.startDate }" @click="clickSelectTime(1)">
            <text class="timer">{{ oldSelectFromData.startDate || '选择时间' }}</text>
          </view>
        </view>

        <view class="date-item">
          <view class="date-label">结束日期</view>
          <view class="date-value" :class="{ null_bg_color: oldSelectFromData.endDate }" @click="clickSelectTime(2)">
            <text class="timer">{{ oldSelectFromData.endDate || '选择时间' }}</text>
          </view>
        </view>

        <sm-time-selector ref="timeSelectorRef" @btnConfirm="btnConfirm" :end-date="yesterday"></sm-time-selector>

        <view class="date-btn-box">
          <view class="reset-btn" @click="resetDate">重置</view>
          <view class="confirm-btn" @click="confirmDate">确定</view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup>
import { ref, defineEmits, onMounted } from 'vue'
import { getCurrentMonthFirstDay, getDaysFromCurrentTime, getDaysBetweenDates } from '../../utils/dateTime.js'


const emit = defineEmits(['timeSelect'])

const timeSelectList = ref([
  { label: '近7天', value: '1' },
  { label: '近30天', value: '2' },
  { label: '近12个月', value: '3' },
  { label: '自定义', value: '4' }
])

const oldSelectFromData = ref({
  startDate: '',
  endDate: ''
})

const timeSelectorRef = ref(null)
const selectedValue = ref('1')
const alertDialog = ref(null)
const operateTimeTypeBtn = ref('')

// 🔥 固定：昨天（所有时间都不算今天）
const getYesterday = () => {
  const d = new Date()
  d.setDate(d.getDate() - 1)
  return d
}
const yesterday = ref(formatDate(getYesterday()))

// 日期格式化
function formatDate (date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

// 打开时间选择（限制最大日期：昨天）
const clickSelectTime = (operateTimeType) => {
  selectedValue.value = ''
  const timer = operateTimeType === 1
    ? oldSelectFromData.value.startDate
    : oldSelectFromData.value.endDate
  timeSelectorRef.value.openSelect(timer)
  operateTimeTypeBtn.value = operateTimeType
}

const selectTimeType = (value) => {
  selectedValue.value = value
  const end = getYesterday() // 昨天
  let start = new Date(end)

  if (value === '1') {
    start.setDate(end.getDate() - 6)
  } else if (value === '2') {
    start.setDate(end.getDate() - 29)
  } else if (value === '3') {
    // 近12个月：365天前 → 当月1号
    let startDate = new Date(end)
    startDate.setDate(end.getDate() - 365)
    start = new Date(startDate.getFullYear(), startDate.getMonth(), 1)
  } else if (value === '4') {
    alertDialog.value.open('bottom')
    const defStart = new Date(end)
    defStart.setDate(end.getDate() - 30)
    oldSelectFromData.value.startDate = formatDate(defStart)
    oldSelectFromData.value.endDate = formatDate(end)
    return
  }

  const startDateStr = formatDate(start)
  const endDateStr = formatDate(end)
  const diffDayNum = getDaysBetweenDates(startDateStr, endDateStr)
  const beforeEndDate = getDaysFromCurrentTime(-1, 0, startDateStr)

  // ✅ 修复：obj 先转成 Date 再调用方法！！！
  let obj = getDaysFromCurrentTime(-diffDayNum, 0, beforeEndDate)
  let beforeStartDate
  if (value === '3') {
    const dateObj = new Date(obj) // 转日期
    beforeStartDate = formatDate(new Date(dateObj.getFullYear(), dateObj.getMonth(), 1))
  } else {
    beforeStartDate = obj
  }

  emit('timeSelect', {
    startDate: startDateStr,
    endDate: endDateStr,
    beforeEndDate,
    beforeStartDate,
    dataType: value
  })
}
onMounted(() => {
  const end = getYesterday() // 昨天
  let start = new Date(end)
  start.setDate(end.getDate() - 6)
  const startDateStr = formatDate(start)
  const endDateStr = formatDate(end)
  const diffDayNum = getDaysBetweenDates(startDateStr, endDateStr)
  const beforeEndDate = getDaysFromCurrentTime(-1, 0, startDateStr)
  const beforeStartDate = getDaysFromCurrentTime(0 - diffDayNum, 0, beforeEndDate)
   emit('timeSelect', {
    startDate: startDateStr,
    endDate: endDateStr,
    beforeEndDate,
    beforeStartDate
  })
})

// 重置
const resetDate = () => {
  const end = getYesterday()
  const start = new Date(end)
  start.setDate(end.getDate() - 29)
  oldSelectFromData.value.startDate = formatDate(start)
  oldSelectFromData.value.endDate = formatDate(end)
}

// 时间选择回调
const btnConfirm = (data) => {
  if (!data) return
  if (operateTimeTypeBtn.value === 1) {
    oldSelectFromData.value.startDate = data
  } else if (operateTimeTypeBtn.value === 2) {
    oldSelectFromData.value.endDate = data
  }

  const start = new Date(oldSelectFromData.value.startDate)
  const end = new Date(oldSelectFromData.value.endDate)
  if (end < start) {
    uni.showToast({ title: '结束时间不能早于开始时间', icon: 'none' })
    oldSelectFromData.value.endDate = ''
  }
}

// 确定
const confirmDate = () => {
  const { startDate, endDate } = oldSelectFromData.value
  if (!startDate || !endDate) {
    uni.showToast({ title: '请选择起止日期', icon: 'none' })
    return
  }

  const diffDayNum = getDaysBetweenDates(startDate, endDate)
  const beforeEndDate = getDaysFromCurrentTime(-1, 0, startDate)
  const beforeStartDate = getDaysFromCurrentTime(0 - diffDayNum, 0, beforeEndDate)

  emit('timeSelect', {
    startDate,
    endDate,
    beforeEndDate,
    beforeStartDate
  })
  alertDialog.value.close()
}

</script>

<style lang="less" scoped>
.time-containers {
  width: 100%;

  .time-select {
    margin-top: 16rpx;
    display: flex;
    flex-wrap: wrap;
    gap: 34rpx;
    width: 100%;

    .time-select-item {
      padding: 12rpx 24rpx;
      border-radius: 30rpx;
      background: #f3f3f3;
      text-align: center;
      font-size: 26rpx;
    }

    .active {
      background-color: rgba(195, 220, 255, 1);
      color: rgba(24, 119, 255, 1);
    }
  }
}

.custom-date-wrap {
  padding: 40rpx;
  width: 100%;
  box-sizing: border-box;

  .date-title {
    font-size: 36rpx;
    font-weight: 500;
    margin-bottom: 40rpx;
  }

  .date-item {
    margin-bottom: 30rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .date-label {
      font-size: 30rpx;
      color: #333;
    }

    .date-value {
      width: 70%;
      background: #efefef;
      border-radius: 999rpx;
      padding: 18rpx 30rpx;
      font-size: 30rpx;
      color: #007aff;
      text-align: center;
    }
  }

  .date-btn-box {
    display: flex;
    justify-content: space-between;
    margin-top: 60rpx;

    .reset-btn {
      width: 42%;
      text-align: center;
      font-size: 32rpx;
      color: #007aff;
      line-height: 80rpx;
    }

    .confirm-btn {
      width: 52%;
      background: #007aff;
      color: #fff;
      border-radius: 999rpx;
      text-align: center;
      font-size: 32rpx;
      line-height: 80rpx;
    }
  }
}
</style>