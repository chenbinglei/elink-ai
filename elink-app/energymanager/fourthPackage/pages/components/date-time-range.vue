<template>
  <view class="time-containers">
    <view class="time-select">
      <view v-for="item in timeSelectList" :key="item.value" class="time-select-item"
        :class="{ active: item.value === selectedValue }" @click="selectTimeType(item.value)">
        {{ item.label }}
      </view>
    </view>
    <view class="startAndEndDate">
      <view class="dateTime" :class="{ null_bg_color: oldSelectFromData.startDate }" @click="clickSelectTime(1)">
        <text v-if="!oldSelectFromData.startDate" style="color: rgba(0, 0, 0, 0.5);">选择时间</text>
        <text class="timer">{{ oldSelectFromData.startDate }}</text>
      </view>
      <view class="text">~</view>
      <view class="dateTime" :class="{ null_bg_color: oldSelectFromData.endDate }" @click="clickSelectTime(2)">
        <text v-if="!oldSelectFromData.endDate" style="color: rgba(0, 0, 0, 0.5);">选择时间</text>
        <text class="timer">{{ oldSelectFromData.endDate }}</text>
      </view>
    </view>
    <sm-time-selector ref="timeSelectorRef" @btnConfirm="btnConfirm"></sm-time-selector>
    <view class="filter-popup-btn" v-if="btnIsshow">
      <view class="filter-popup-btn-cancel" @click="resetBtn">重置</view>
      <view class="filter-popup-btn-confirm" @click="submitBtn">确定</view>
    </view>
  </view>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
const emit = defineEmits(['update:startDate', 'update:endDate', 'close-card'])
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
  },
  btnIsshow: {
    type: Boolean,
    default: false
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
const btnIsshow = ref(false)
watch(() => props.btnIsshow, (newVal) => {
  btnIsshow.value = newVal
}, { immediate: true })

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
  } else if (selectedValue.value === '6') {
    // 自定义
    oldSelectFromData.value.startDate = ''
    oldSelectFromData.value.endDate = ''
    return
  }
  oldSelectFromData.value.startDate = formatDate(start)
  oldSelectFromData.value.endDate = formatDate(end)
  if (!btnIsshow.value) {
    emit('update:startDate', oldSelectFromData.value.startDate)
    emit('update:endDate', oldSelectFromData.value.endDate)
  }






}

const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}
// 重置
const resetBtn = () => {
  if (btnIsshow.value) {
    let start = new Date()
    let end = new Date()
    selectedValue.value = timeSelectList.value[0].value
    start.setHours(0, 0, 0, 0)
    end.setHours(23, 59, 59, 999)
    oldSelectFromData.value.startDate = formatDate(start)
    oldSelectFromData.value.endDate = formatDate(end)
    emit('update:startDate', oldSelectFromData.value.startDate)
    emit('update:endDate', oldSelectFromData.value.endDate)
    emit('close-card')
  } else {
    oldSelectFromData.value.startDate = ''
    oldSelectFromData.value.endDate = ''
    emit('update:startDate', oldSelectFromData.value.startDate)
    emit('update:endDate', oldSelectFromData.value.endDate)
  }


}
// 确定
const submitBtn = () => {
  emit('update:startDate', oldSelectFromData.value.startDate)
  emit('update:endDate', oldSelectFromData.value.endDate)
  emit('close-card')
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
}

watch(() => props.resetList, (newVal) => {
  if (newVal) {
    selectedValue.value = timeSelectList.value[0].value
  }
})
onMounted(() => {
  if (props.timeSelectList.length > 0) {
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
  }


})
defineExpose({
  resetBtn: resetBtn
})
</script>

<style scoped lang="less">
.time-containers {
  width: 100%;

  .time-select {
    margin-top: 16rpx;
    display: flex;
    flex-wrap: wrap; // 允许换行
    gap: 34rpx;
    width: 100%;

    // justify-content: space-between;
    .time-select-item {
      padding: 1% 8%;
      border-radius: 30rpx;
      background: #f3f3f3;
      flex: 0 0 29%; // 每个 item 占 32% 宽度（留出间距）
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
  background-color: #F3F3F3;
  border-radius: 50rpx;

  .dateTime {
    flex: 1;
    height: 72rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8rpx;


    .null_time {
      font-size: 28rpx;
      color: rgba(0, 0, 0, 0.1);
    }
  }

  .text {
    //  margin: 0 10rpx;
  }

  .null_bg_color {
    border: none;
    // background: rgba(31, 116, 226, 0.1);

    .timer {
      font-size: 28rpx;
      color: rgba(0, 0, 0, 0.6);
    }
  }
}

.filter-popup-btn {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding-top: 20rpx;

}

.filter-popup-btn-cancel {
  padding: 20rpx 80rpx;
  color: rgba(24, 119, 255, 1);
  background: #f3f3f3;
  border: 1rpx solid rgba(243, 243, 243, 1);
  border-radius: 48.5rpx;
}

.filter-popup-btn-confirm {
  padding: 20rpx 80rpx;
  background: rgba(24, 119, 255, 1);
  border: 1rpx solid rgba(243, 243, 243, 1);
  color: rgba(255, 255, 255, 1);
  border-radius: 48.5rpx;

}
</style>