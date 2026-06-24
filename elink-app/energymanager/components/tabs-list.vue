<template>
  <view class="tabs-list">
    <view v-for="item in dateType" :key="item.type" class="date-item" @click="dateTypeClick(item)"
      :class="{ 'active': selectType.functionLogos === item.functionLogos }">
      {{ item.functionLogos }}
    </view>
  </view>
</template>

<script setup>
import {  onMounted } from 'vue'
const props = defineProps({
  dateType: {
    type: Array,
    default: () => []
  },
  selectType: {
    type: String,
    default: ''
  }
})
const emit = defineEmits(['dateTypeClick'])
const dateTypeClick = (type) => {
  emit('dateTypeClick', type)
}
// 默认选中第一个 tab
onMounted(() => {
  if (props.dateType.length > 0) {
    emit('dateTypeClick', props.dateType[0])
  }
})
</script>

<style lang="scss" scoped>
.tabs-list {
  display: inline-flex;
  align-items: center;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 143rpx;
  padding: 4rpx 2rpx;
}

.date-item {
  font-weight: 400;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 1);
  padding: 8rpx 30rpx;
  text-align: center;
}

.active {
  background: rgba(255, 255, 255, 1);
  color: rgba(56, 139, 255, 1);
  background: rgba(255, 255, 255, 1);
  border-radius: 343rpx;
}
</style>