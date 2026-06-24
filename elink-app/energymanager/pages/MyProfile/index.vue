<template>
  <view class="content">
    <image class="content-bgc" src="/static/image/bgc.png"></image>
    <view class="container">
      <view class="tabs">我的</view>
      <view class="header-list">
        <view class="image-userName" @click="geProfile()">
          <image class="image"></image>
          <view class="userNamr-company">
            <view>{{userInfo?.userAccount || '用户'}}</view>
            <view class="company">{{userInfo?.tenantName|| '杭州晟曼科技有限公司'}}</view>
          </view>
        </view>
        <view class="message" @click="goOthers('messageCenter')">
          <view class="single">
            <u-icon name="chat" color="#2979ff" size="28"></u-icon>
            <view class="message-text">消息中心</view>
          </view>
          <view class="single">
            <view class="message-num">0</view>
            <u-icon name="arrow-right" color="#2979ff" size="28"></u-icon>
          </view>

        </view>
        <view class="station-sum" @click="goOthers()">
          <view class="sum-item">
            <view class="single"> <u-icon name="chat" color="#2979ff" size="28"></u-icon>
              <view class="message-text">场站管理</view>
            </view>
            <u-icon name="arrow-right" color="#2979ff" size="28"></u-icon>
          </view>
          <view class="line"></view>
          <view class="sum-item" @click="goOthers()">
            <view class="single"> <u-icon name="chat" color="#2979ff" size="28"></u-icon>
              <view class="message-text">用户管理</view>
            </view>
            <u-icon name="arrow-right" color="#2979ff" size="28"></u-icon>
          </view>
          <view class="line"></view>
          <view class="sum-item" @click="goOthers('companyInformation')">
            <view class="single"><u-icon name="chat" color="#2979ff" size="28"></u-icon>
              <view class="message-text">企业信息</view>
            </view>
            <u-icon name="arrow-right" color="#2979ff" size="28"></u-icon>
          </view>
        </view>
        <view class="message" @click="goOthers()">
          <view class="single">
            <u-icon name="info-circle" color="#2979ff" size="28"></u-icon>
            <view class="message-text">关于</view>
          </view>
          <view class="single">
            <view class="message-nums"></view>
            <u-icon name="arrow-right" color="#2979ff" size="28"></u-icon>
          </view>

        </view>
        <view class="message" @click="goSetting()">
          <view class="single"> <u-icon name="setting" color="#2979ff" size="28"></u-icon>
            <view class="message-text">设置</view>
          </view>
          <u-icon name="arrow-right" color="#2979ff" size="28"></u-icon>

        </view>
      </view>
    </view>
  </view>
</template>


<script setup>
import { ref, onMounted } from 'vue'
const userInfo = ref({})
const goSetting = () => {
  uni.navigateTo({
    url: '/thirdPackage/pages/setting/loginOut'
  })
}
onMounted(() => {
  userInfo.value = uni.getStorageSync('USER_INFO')
  console.log(userInfo.value)
})
const goOthers = (val) => {
  if (val) {
    const url = '/thirdPackage/pages/mine/' + val
    uni.navigateTo({
      url: url
    })

  } else {

    uni.showToast({
      title: '暂未开放',
      icon: 'none'
    })
  }

}
const geProfile = () => {
  uni.navigateTo({
    url: '/thirdPackage/pages/mine/profile'
  })
}
</script>

<style lang="scss" scoped>
.content {
  position: relative;
  width: 100%;
  height: 100%;
}

.content-bgc {
  position: absolute;
  width: 100%;
  height: 100%;
}

.container {
  width: 90%;
  margin: 0 auto;
}

.tabs {
  padding-top: 16%;
  width: 100%;
  margin: 0 auto;
  display: flex;
  font-weight: 500;
  font-size: 36rpx;
  color: #000000;
}

.header-list {
  margin-top: 30rpx;
  .image-userName {
    display: flex;
    align-items: center;

    .image {
      width: 142rpx;
      height: 142rpx;
      border-radius: 50%;
      border: 1rpx solid #ffffff;
    }

    .userNamr-company {
      margin-left: 29rpx;
      font-weight: 600;
      font-size: 36rpx;
      color: #000000;
    }

    .company {
      font-weight: 400;
      font-size: 28rpx;
      color: #aeaeae;
    }
  }
}

.message {
  display: flex;
  align-items: center;
  background: #ffffff;
  box-shadow: 0rpx 0rpx 2rpx 0rpx rgba(0, 0, 0, 0.05);
  border-radius: 28rpx 28rpx 28rpx 28rpx;
  padding: 27rpx;
  color: #cfcfcf;
  margin-top: 30rpx;
  justify-content: space-between;
}
.line {
  margin: 23rpx 0;
  width: 90%;
  height: 1rpx;
  border: 1rpx solid rgba(203, 203, 203, 0.2);
}
.single {
  display: flex;
  align-items: center;
}

.message-text {
  font-weight: 500;
  font-size: 28rpx;
  color: #000000;
  margin-left: 28rpx;
}

.message-num {
  width: 33rpx;
  height: 33rpx;
  background: #f53149;
  border-radius: 50%;
  margin-left: 19rpx;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.message-nums {
  width: 10rpx;
  height: 10rpx;
  border-radius: 50%;
  margin-left: 19rpx;
}

.station-sum {
  background: #ffffff;
  color: #cfcfcf;

  box-shadow: 0rpx 0rpx 2rpx 0rpx rgba(0, 0, 0, 0.05);
  border-radius: 28rpx 28rpx 28rpx 28rpx;
  padding: 27rpx;
  margin-top: 30rpx;

  .sum-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}
</style>
