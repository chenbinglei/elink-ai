<template>
  <view class="container">
    <view class="card-item">
      <view class="sum-item">
        <view class="message-text">消息设置</view>
       <u-icon name="arrow-right" size="16" color="#DCDCDC"></u-icon>
      </view>
      <view class="sum-item" @click="goAccountSecurity()">
        <view class="message-text" >账号安全</view>
        <u-icon name="arrow-right" size="16" color="#DCDCDC"></u-icon>
      </view>
    </view>
    <view class="loginout">
      <u-button class="btn-login" @click="goLoginOut()">退出登录</u-button>
    </view>
  </view>
</template>

<script setup name="setup">
import { ref } from 'vue'
const goLoginOut = () => {
  uni.removeStorageSync('USER_ID');
  uni.removeStorageSync('SITE_LIST');
  uni.removeStorageSync('COMPANY');
  uni.removeStorageSync('USER_INFO');
  uni.removeStorageSync('EARLIEST_CREATE_TIME');


  uni.showToast({
    icon: 'none',
    title: '退出登录成功'
  });
  uni.reLaunch({ url: '/thirdPackage/login/login' });
}
const goAccountSecurity = () => {
  uni.navigateTo({ url: '/thirdPackage/pages/setting/accountSecurity' });
}
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

.loginout {
  width: 50%;
  margin: 0 auto;
  .btn-login {
    border-radius: 30rpx;
  }
}
</style>