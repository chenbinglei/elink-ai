<template>
  <view class="date-range">

    <view class="site-date">
      <view v-for="item in dateType" :key="item.type" class="date-item" @click="dateTypeClick(item.dateType)"
        :class="{ 'active': selectType === item.dateType }">
        {{ item.typeName }}
      </view>
    </view>
    <view class="date-detailsTime">
      <u-icon name="arrow-left" size="14" v-if="selectType !== '4'" @click="lastTime()"></u-icon>
      <UDate :type="selectType" @dateTypeDate="handleDateTypeDate" :chageType="chageType" @valueDefault="valueDefault"
        :clickTime="clickTime" />
      <u-icon name="arrow-right" size="14" v-if="selectType !== '4'" @click="nextTime()"></u-icon>
    </view>
  </view>
</template>

<script setup>
import UDate from '../components/UDate.vue'
import { computed, ref, watch, onMounted } from 'vue'
const emit = defineEmits(['handleDateTypeDate', 'selectType'])

const dateType = ref([
  {
    type: 'date',
    typeName: '日',
    dateType: '1'
  },
  {
    type: 'year-month',
    typeName: '月',
    dateType: '2'
  },
  {
    type: 'year',
    typeName: '年',
    dateType: '3'
  },
  {
    type: 'sum',
    typeName: '总',
    dateType: '4'

  }
])
const defaultTime = ref('')
const selectType = ref('1')
const chageType = ref(false)
const startTime = ref('')
const endTime = ref('')
const clickTime = ref('')
const dateTypeClick = (type) => {
  selectType.value = type
  emit('selectType', type)

}
// 格式化日期函数
function formatDate (date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

const handleDateTypeDate = (val) => {
  if (selectType.value === '4') {
    startTime.value = uni.getStorageSync('detailsInformationTime')
    endTime.value = formatDate(new Date()) 

  } else {
    startTime.value = val.startTime
    endTime.value = val.endTime
  }
  let obj={
    startTime:startTime.value,
    endTime:endTime.value
  }

  emit('handleDateTypeDate', obj)
  console.log(val, 'handleDateTypeDate*************')
}
const valueDefault = (val) => {
  defaultTime.value = val
  console.log(val, 'valueDefault')
}
const getLastDayOfMonth = (year, month) => {
  return new Date(year, month, 0).getDate()
}
const formatYearMonth = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  return `${year}/${month}`;
};
// 格式化日期函数
const formatTime = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}/${month}/${day}`;
};

const lastTime = () => {
  if (selectType.value === '1') {
    const [year, month, day] = defaultTime.value.split('/');
    const date = new Date(year, month - 1, day);
    date.setDate(date.getDate() - 1);
    clickTime.value = formatTime(date);
    // 使用 date 对象生成 start 和 end 时间
    const newYear = date.getFullYear();
    const newMonth = String(date.getMonth() + 1).padStart(2, '0');
    const newDay = String(date.getDate()).padStart(2, '0');

    startTime.value = `${newYear}-${newMonth}-${newDay} 00:00:00`;
    endTime.value = `${newYear}-${newMonth}-${newDay} 23:59:59`;
  } else if (selectType.value === '2') {
    const [year, month] = defaultTime.value.split('/');
    const date = new Date(year, month - 1, 1);
    date.setMonth(date.getMonth() - 1);
    clickTime.value = formatYearMonth(date); // 使用新的格式

    const newYear = date.getFullYear();
    const newMonth = String(date.getMonth() + 1).padStart(2, '0');
    const lastDay = getLastDayOfMonth(newYear, newMonth);

    startTime.value = `${newYear}-${newMonth}-01 00:00:00`;
    endTime.value = `${newYear}-${newMonth}-${lastDay} 23:59:59`;
  } else {
    const year = parseInt(defaultTime.value);
    const newYear = year - 1;
    clickTime.value = newYear.toString();

    startTime.value = `${newYear}-01-01 00:00:00`;
    endTime.value = `${newYear}-12-31 23:59:59`;
  }
  const val = { startTime: startTime.value, endTime: endTime.value };
  emit('handleDateTypeDate', val)
};
const nextTime = () => {
  if (selectType.value === '1') {
    const [year, month, day] = defaultTime.value.split('/');
    const date = new Date(year, month - 1, day);
    date.setDate(date.getDate() + 1);
    clickTime.value = formatTime(date);

    // 使用新的 date 对象生成时间
    const newYear = date.getFullYear();
    const newMonth = String(date.getMonth() + 1).padStart(2, '0');
    const newDay = String(date.getDate()).padStart(2, '0');

    startTime.value = `${newYear}-${newMonth}-${newDay} 00:00:00`;
    endTime.value = `${newYear}-${newMonth}-${newDay} 23:59:59`;
  } else if (selectType.value === '2') {
    const [year, month] = defaultTime.value.split('/');
    const date = new Date(year, month - 1, 1);
    date.setMonth(date.getMonth() + 1);
    clickTime.value = formatYearMonth(date); // 使用新的格式

    const newYear = date.getFullYear();
    const newMonth = String(date.getMonth() + 1).padStart(2, '0');
    const lastDay = getLastDayOfMonth(newYear, newMonth);

    startTime.value = `${newYear}-${newMonth}-01 00:00:00`;
    endTime.value = `${newYear}-${newMonth}-${lastDay} 23:59:59`;
  } else {
    const year = parseInt(defaultTime.value);
    const newYear = year + 1;
    clickTime.value = newYear.toString();

    startTime.value = `${newYear}-01-01 00:00:00`;
    endTime.value = `${newYear}-12-31 23:59:59`;
  }
  const val = { startTime: startTime.value, endTime: endTime.value };
  emit('handleDateTypeDate', val)

};
onMounted(() => {
  emit('selectType', selectType.value)
})


</script>

<style lang="scss" scoped>
.date-range {
  width: 100%;

  .site-date {
    display: flex;
    width: 100%;
    align-items: center;
    justify-content: space-between;
    background: rgba(160, 183, 197, 0.2);
    border-radius: 143rpx;
    padding: 8rpx 2rpx;

    .date-item {
      font-weight: 400;
      font-size: 24rpx;
      color: rgba(174, 174, 174, 1);
      width: 25%;
      text-align: center;
    }

    .active {
      background: rgba(255, 255, 255, 1);
      color: rgba(0, 0, 0, 1);
      background: rgba(255, 255, 255, 1);
      border-radius: 343rpx;
    }
  }

  .date-detailsTime {
    display: flex;
    align-items: center;
    margin-top: 30rpx;
    justify-content: space-between;
  }
}
</style>