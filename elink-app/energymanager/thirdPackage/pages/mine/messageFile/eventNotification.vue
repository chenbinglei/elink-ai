<template>
  <div class="container">
    <empty v-if="list.length === 0" style="width: 100%; height: 300rpx;margin-top: 93rpx;"></empty>
  </div>
</template>

<script setup name="setup">
import empty from '@/components/uni-custom/empty.vue'
import { ref } from 'vue'
const list = ref([
])
</script>

<style scoped lang="less">
.container {
  background-color: #f5f5f5;
  width: 100%;
  height: 100%;
  padding: 25rpx 4%;
}
</style>