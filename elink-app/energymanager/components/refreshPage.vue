<template>
  <view class="refresh-container">
    <scroll-view 
      scroll-y 
      :style="{ height: height + 'px' }" 
      @scroll="onScroll"
    >
      <slot name="list"></slot>
    </scroll-view>
    <view v-if="isRefreshing" class="loading">加载中...</view>
  </view>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  height: { type: [String, Number], default: '100%' }
});

const emit = defineEmits(['refresh']);

const isRefreshing = ref(false);
const scrollTop = ref(0);

const onScroll = (e) => {
  scrollTop.value = e.detail.scrollTop;
  if (scrollTop.value <= 50 && !isRefreshing.value) {
    isRefreshing.value = true;
    emit('refresh');
  }
};
</script>