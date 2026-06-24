<template>
  <view class="container">
    <view class="header">
      <view class="header-image">
      </view>
      <view class="header-text">点击更换头像</view>
    </view>
    <view class="header-message">
      <view class="header-message-item">
        <view class="header-message-item-left">
          <view class="header-message-item-icon">
            <image src="/static/image/profile/user.png" mode="aspectFill"></image>
          </view>
          <view class="header-message-item-text">
            姓名
          </view>
        </view>
        <view class="header-message-item-right">
          <view class="header-message-item-text">
            张三
          </view>
          <view class="header-message-item-icon">
            <image src="/static/image/profile/edit.png" mode="aspectFill"></image>
          </view>
        </view>
      </view>
      <view class="line"></view>
      <view class="header-message-item">
        <view class="header-message-item-left">
          <view class="header-message-item-icon">
            <image src="/static/image/profile/phone.png" mode="aspectFill"></image>
          </view>
          <view class="header-message-item-text">
            手机号
          </view>
        </view>
        <view class="header-message-item-right">
          <view class="header-message-item-text">
            189****6688
          </view>
          <view class="header-message-item-icon">
            <image src="/static/image/profile/close.png" mode="aspectFill"></image>
          </view>
        </view>
      </view>
      <view class="line"></view>
      <view class="header-message-item">
        <view class="header-message-item-left">
          <view class="header-message-item-icon">
            <image src="/static/image/profile/no.png" mode="aspectFill"></image>
          </view>
          <view class="header-message-item-text">
            账号
          </view>
        </view>
        <view class="header-message-item-right">
          <view class="header-message-item-text">
            D80A3Chs
          </view>
          <view class="header-message-item-icon">
            <image src="/static/image/profile/file.png" mode="aspectFill"></image>
          </view>
        </view>
      </view>
      <view class="line"></view>
      <view class="header-message-item">
        <view class="header-message-item-left">
          <view class="header-message-item-icon">
            <image src="/static/image/profile/file.png" mode="aspectFill"></image>
          </view>
          <view class="header-message-item-text">
            企业
          </view>
        </view>
        <view class="header-message-item-right">
          <view class="header-message-item-text">
            杭州晟曼科技有限公司
          </view>
        </view>
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
  padding: 8% 4%;
  .header {
    width: 100%;
    border-radius: 15rpx;
    margin: 0 auto;
    background-color: #fff;
    background-color: #fff;
    .header-image {
      width: 152rpx;
      height: 152rpx;
      border-radius: 50%;
      margin: 47rpx auto 26rpx;
      background: aliceblue;
    }
    .header-text {
      font-size: 32rpx;
      color: #5e687b;
      margin-bottom: 41rpx;
      text-align: center;
    }
  }
  .header-message {
    background-color: #fff;
    margin-top: 20rpx;
    border-radius: 15rpx;
    .header-message-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 27rpx;
      margin: 23rpx 0 26rpx 0;

      .header-message-item-left {
        display: flex;
        justify-content: space-between;
        align-items: center;
        .header-message-item-text {
          color: #2c3a52;
        }
      }
      .header-message-item-right {
        display: flex;

        justify-content: space-between;
        align-items: center;
        .header-message-item-text {
          color: #000000;
        }
      }
    }
    .line {
      width: 70%;
      height: 0rpx;
      border-bottom: 1rpx solid #e8e6e6;
      margin: 0 auto;
    }
    .header-message-item-text {
      font-size: 28rpx;
    }
    .header-message-item-icon {
      width: 64rpx;
      height: 64rpx;
      margin-right: 20rpx;
      image {
        width: 100%;
        height: 100%;
        padding: 7rpx;
      }
    }
  }
}
</style>