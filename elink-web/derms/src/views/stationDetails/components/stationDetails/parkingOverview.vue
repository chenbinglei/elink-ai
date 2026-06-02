<template>
  <div class="empty-container">
    <el-empty :image="emptyImg" description="暂无数据" />
  </div>
</template>

<script setup>
import emptyImg from "@/assets/image/empty.png";
</script>

<style lang="scss" scoped>
.empty-container {
  width: 100%;
  height: 86%;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>