<template>
  <div style="width: 100%;height: 100%;">
    <view class="image"></view>
    <view class="tip-font">暂无数据</view>
  </div>
</template>

<script setup>

</script>

<style lang="scss" scoped>
.image {
  background-image: url("../assets/empty2.png");
  background-size: contain;
  /* 等比例缩放，保持完整显示 */
  background-repeat: no-repeat;
  background-position: center center;
  width: 100%;
  height:92%;
}

.tip-font {
  font-size: 16px;
  font-weight: 400;
  color: #999999;
  text-align: center;
  line-height: 24px;
}
</style>