<template>
  <view>
    <!-- 日期显示区域 -->
    <view class="date-detailsTime-text" @click="openDatePicker" v-if="type !== '4'">
      <text>{{ displayValue }}</text>
      <u-icon name="arrow-down-fill" size="8"></u-icon>
    </view>

    <!-- 日期时间选择器 -->
    <u-datetime-picker v-if="showDatePicker" :show="showPicker" v-model="pickerValue" :mode="dateMode"
      @confirm="handleDateConfirm" @cancel="handleCancel" :class="{ 'custom-datepicker-year': dateMode === 'year' }"
      :max-date="maxDate"></u-datetime-picker>

    <!-- 年份选择器 -->
    <u-picker v-else-if="showYearPicker" :show="showPicker" :columns="yearColumns" @confirm="handleYearConfirm"
      @cancel="handleCancel" v-model="pickerValue"></u-picker>
  </view>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue'
const emit = defineEmits(['dateTypeDate', 'valueDefault'])

const props = defineProps({
  type: {
    type: String,
    default: '1', // '1': 日期, '2': 年月, '3': 年份, '4': 只显示
    validator: (value) => ['1', '2', '3', '4'].includes(value)
  },
  chageType: {
    type: Boolean,
    default: false
  },
  clickTime: {
    type: String,
    default: ''
  }
})

// 响应式状态
const showPicker = ref(false)
const displayValue = ref('')
const pickerValue = ref([]); // 改为数组类型
const dateMode = ref('date')

// 年份选择器数据
const yearColumns = ref([])

// 计算属性
const showDatePicker = computed(() => props.type !== '3' && props.type !== '4')
const showYearPicker = computed(() => props.type === '3')
// 最大可选时间（当前时间）
const maxDate = computed(() => {
  let now = new Date();
  return now.toISOString().slice(0, 10);
});
// 初始化年份列数据
const initYearColumns = () => {
  const startYear = 1996
  const endYear = new Date().getFullYear() // 当前年份
  const years = []

  for (let year = startYear; year <= endYear; year++) {
    years.push(year.toString())
  }

  // u-picker需要二维数组格式
  yearColumns.value = [years]

  // 设置默认选中今年
  const currentYear = new Date().getFullYear().toString()
  const yearIndex = years.indexOf(currentYear)

  if (yearIndex !== -1) {
    // 对于u-picker，通过设置默认值来选中
    // 注意：u-picker可能需要其他方式设置默认选中项
    pickerValue.value = [currentYear]
  }
}

// 格式化日期为字符串
const formatDate = (date) => {
  if (!(date instanceof Date) || isNaN(date)) {
    return ''
  }

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')

  return `${year}/${month}/${day}`
}

// 格式化日期为年月字符串
const formatYearMonth = (date) => {
  if (!(date instanceof Date) || isNaN(date)) {
    return ''
  }

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')

  return `${year}/${month}`
}

// 获取当月的最后一天
const getLastDayOfMonth = (year, month) => {
  return new Date(year, month, 0).getDate()
}

// 根据日期类型获取时间范围
const getTimeRange = (date, mode) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')

  let startTime = ''
  let endTime = ''

  switch (mode) {
    case 'date':
      startTime = `${year}-${month}-${day} 00:00:00`
      endTime = `${year}-${month}-${day} 23:59:59`
      break

    case 'year-month':
      const lastDay = getLastDayOfMonth(year, parseInt(month))
      startTime = `${year}-${month}-01 00:00:00`
      endTime = `${year}-${month}-${lastDay} 23:59:59`
      break

    case 'year':
      startTime = `${year}-01-01 00:00:00`
      endTime = `${year}-12-31 23:59:59`
      break

    default:
      return { startTime: '', endTime: '' }
  }

  return { startTime, endTime }
}

// 更新显示值和触发事件
const updateDisplayAndEmit = (date, mode) => {
  let formattedValue = ''

  switch (mode) {
    case 'date':
      formattedValue = formatDate(date)
      break
    case 'year-month':
      formattedValue = formatYearMonth(date)
      break
    case 'year':
      formattedValue = date.getFullYear().toString()
      break
  }

  if (formattedValue) {
    displayValue.value = formattedValue
    const timeRange = getTimeRange(date, mode)

    emit('dateTypeDate', timeRange)
    emit('valueDefault', formattedValue)
  }
}

// 打开日期选择器
const openDatePicker = () => {
  const now = new Date()

  // 设置选择器的默认值
  if (showDatePicker.value) {
    pickerValue.value = now.getTime() // u-datetime-picker通常需要时间戳
  }

  showPicker.value = true
}
watch(() => props.clickTime, (newValue) => {
  if (newValue) {
    displayValue.value = props.clickTime
    emit('valueDefault', displayValue.value)
  }
})


// 处理日期选择器确认
const handleDateConfirm = (value) => {
  showPicker.value = false

  try {
    let dateValue

    // 处理不同格式的返回值
    if (value && typeof value.value !== 'undefined') {
      // u-datetime-picker返回的对象格式
      dateValue = value.value
    } else if (typeof value === 'number' || typeof value === 'string') {
      // 直接的时间戳或日期字符串
      dateValue = value
    } else {
      return
    }

    const date = new Date(dateValue)

    if (!(date instanceof Date) || isNaN(date)) {
      return
    }

    updateDisplayAndEmit(date, dateMode.value)
  } catch (error) {
  }
}

// 处理年份选择器确认
// const handleYearConfirm = (value) => {
//   showPicker.value = false

//   try {
//     // u-picker返回的格式通常是 {value: [], index: []}
//     if (value && Array.isArray(value.value) && value.value.length > 0) {
//       const yearStr = value.value[0]
//       const year = parseInt(yearStr)

//       if (!isNaN(year) && year >= 1000 && year <= 9999) {
//         const date = new Date(year, 0, 1) // 设置为该年的1月1日
//         updateDisplayAndEmit(date, 'year')
//       } else {
//       }
//     } else {
//     }
//   } catch (error) {
//   }
// }
// 处理年份选择器确认
const handleYearConfirm = (value) => {
  showPicker.value = false

  try {
    // u-picker返回的格式通常是 {value: [], index: []}
    if (value && Array.isArray(value.value) && value.value.length > 0) {
      const yearStr = value.value[0]
      const year = parseInt(yearStr)

      if (!isNaN(year) && year >= 1000 && year <= 9999) {
        const date = new Date(year, 0, 1) // 设置为该年的1月1日
        updateDisplayAndEmit(date, 'year')
      } else {
        // 无效年份处理
      }
    } else {
      // 未选择有效年份
    }
  } catch (error) {
  }
}

// 处理取消
const handleCancel = () => {
  showPicker.value = false
}

// 初始化查询时间
const queryTime = () => {
  const now = new Date()
  updateDisplayAndEmit(now, dateMode.value)
}

// 监听类型变化
watch(() => props.type, (newType) => {
  // 根据类型设置日期模式
  switch (newType) {
    case '1':
      dateMode.value = 'date'
      break
    case '2':
      dateMode.value = 'year-month'
      break
    case '3':
      dateMode.value = 'year'
      break
  }

  // 更新显示
  queryTime()
})

// 监听chageType变化
watch(() => props.chageType, (newValue) => {
  if (newValue) {
    dateMode.value = 'date'
    queryTime()
  }
})

// 组件挂载
onMounted(() => {
  // 初始化年份列
  initYearColumns()

  // 设置初始日期模式
  switch (props.type) {
    case '1':
      dateMode.value = 'date'
      break
    case '2':
      dateMode.value = 'year-month'
      break
    case '3':
      dateMode.value = 'year'
      break
  }

  // 初始化时间
  queryTime()
})
</script>

<style lang="scss" scoped>
.date-detailsTime-text {
  display: flex;
  align-items: center;
  gap: 24rpx;
  font-weight: 600;
  font-size: 24rpx;
  color: #000000;
  cursor: pointer;
}

.custom-datepicker-year {
  /* 年份选择器的自定义样式 */
}
</style>