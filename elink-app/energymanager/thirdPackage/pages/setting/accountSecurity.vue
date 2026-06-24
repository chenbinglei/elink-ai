<template>
  <view class="container">
    <view class="card-item">
      <view class="sum-item">
        <view class="message-text">修改密码</view>
        <u-icon name="arrow-right" size="16" color="#DCDCDC"></u-icon>
      </view>
      <view class="sum-item">
        <view class="message-text">账号注销</view>
        <u-icon name="arrow-right" size="16" color="#DCDCDC"></u-icon>
      </view>
    </view>

  </view>
</template>

<script setup name="setup">
import { ref } from 'vue'

</script>

<style scoped lang="less">
.container {
  background-color: #f5f5f5;
  width: 100%;
  height: 100%;
  padding: 0 4%;
}
.card-item {
  width: 100%;
  height: 80%;
  margin: 0 auto;

  .sum-item {
    background-color: #fff;
    display: flex;
    justify-content: space-between;
    padding: 30rpx 35rpx;
    width: 96%;
    margin: 30rpx auto;
    border-radius: 30rpx;
  }
}
</style>