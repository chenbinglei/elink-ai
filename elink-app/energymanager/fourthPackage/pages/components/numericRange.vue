<template>
  <view class="containers">
    <u--input placeholder="最小值" class="input" v-model="min_value" inputAlign="center" @blur="changeValueFun" />
    <view>~</view>
    <u--input placeholder="最大值" class="input" v-model="max_value" @blur="changeValueFun" inputAlign="center">
      <template #suffix>
        <view v-if="unit" class="unit">{{ unit }}</view>
      </template>
    </u--input>
  </view>
</template>

<script setup>
import { ref, watch } from 'vue'

const emit = defineEmits(['update:minValue', 'update:maxValue'])
const props = defineProps({
  unit: {
    type: String,
    default: ''
  },
  minValue: {
    type: [Number, String],
    default: ''
  },
  maxValue: {
    type: [Number, String],
    default: ''
  }
})

const min_value = ref(props.minValue || '')
const max_value = ref(props.maxValue || '')

// 同步 props 到响应式数据
watch(
  () => props.minValue,
  (newVal) => {
    min_value.value = newVal
  },
  { immediate: true }
)

watch(
  () => props.maxValue,
  (newVal) => {
    max_value.value = newVal
  },
  { immediate: true }
)

const changeValueFun = () => {
  let min = min_value.value
  let max = max_value.value

  // 如果是字符串，尝试转为数字
  if (typeof min === 'string') min = parseFloat(min)
  if (typeof max === 'string') max = parseFloat(max)

  // 确保 min 和 max 是有效数字
  if (!isNaN(min) && !isNaN(max)) {
    if (min > max) {
      [min, max] = [max, min]
    }

    min_value.value = min
    max_value.value = max

    emit('update:minValue', min)
    emit('update:maxValue', max)
  } else {
    console.warn('输入值无效，无法进行比较')
  }
}
</script>

<style lang="scss" scoped>
.containers {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #F3F3F5;
  border-radius: 100rpx;

  .input {
    width: 49%;
    text-align: center; // 内容居中
  }

  .unit {
    font-size: 12px;
    color: #999;
  }
}
</style>