<template>
  <view class="container">
    <view class="header-message">
      <view class="header-message-item" @click="gomessage('systemAnnouncement')">
        <view class="header-message-item-left">
          系统公告
        </view>
        <view class="header-message-item-right">
          <view class="header-message-item-text">
            1
          </view>
          <view class="header-message-item-icon">
            <u-icon name="arrow-right" size="16" color="#DCDCDC"></u-icon>
          </view>
        </view>
      </view>
      <view class="line"></view>
      <view class="header-message-item" @click="gomessage('todoTask')">
        <view class="header-message-item-left">
          代办任务
        </view>
        <view class="header-message-item-right">
          <u-icon name="arrow-right" size="16" color="#DCDCDC"></u-icon>
        </view>
      </view>
      <view class="line"></view>
      <view class="header-message-item" @click="gomessage('eventNotification')">
        <view class="header-message-item-left">
          事件通知
        </view>
        <view class="header-message-item-right">
          <u-icon name="arrow-right" size="16" color="#DCDCDC"></u-icon>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup name="setup">
import { ref } from 'vue'
const gomessage = (path) => {
  const url = '/thirdPackage/pages/mine/messageFile/' + path
  uni.navigateTo({
    url: url
  })
}
</script>

<style scoped lang="less">
.container {
  background-color: #f5f5f5;
  width: 100%;
  height: 100%;
  padding: 8% 4%;
  .header-message {
    width: 100%;
    border-radius: 15rpx;
    margin: 0 auto;
    background-color: #fff;
    padding: 0 28rpx;
    border-radius: 30rpx;
    .header-message-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 35rpx 0;
    }
    .header-message-item-left {
      font-size: 28rpx;
      color: #2c353e;
      font-weight: 600;
    }
    .header-message-item-right {
      display: flex;
      align-items: center;
      .header-message-item-text {
        width: 33rpx;
        height: 33rpx;
        background: #f53149;
        font-size: 20rpx;
        margin-right: 15rpx;
        text-align: center;
        line-height: 33rpx;
        border-radius: 50%;
        color: #fff;
        font-weight: 400;
      }
    }
  }
}
.line {
  width: 100%;
  height: 0rpx;
  border-bottom: 1rpx solid #e8e6e6;
  margin: 0 auto;
}
</style>