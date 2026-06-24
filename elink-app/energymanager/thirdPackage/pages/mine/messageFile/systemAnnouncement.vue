<template>
  <view class="container">
    <view class="system-announcement-item" v-for="item in list" :key="item.title">
      <view class="system-time">{{item.time}}</view>
      <view class="system-content">
        <view class="system-title">{{item.title}}</view>
        <view class="system-content-text">{{item.content}}</view>
        <view class="line"></view>
        <view class="system-content-details" @click="gotoDetails(item)">
          <view class="header-message-item-text">查看详情</view>
          <view class="header-message-item-right">
            <u-icon name="arrow-right" size="16" color="#DCDCDC"></u-icon>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup name="setup">
import { ref } from 'vue'
const list = ref([
  {
    title: '系统维护公告',
    time: '2023-08-01',
    content: '尊敬的用户，您好！为了给您提供更优质的服务，能链管家将于 2025-12-27 00:00-03:00进行升级更新，期间可能会影响您的正常登录使用。给您造成的不便，敬请谅解。如有疑问，请联系服务，感谢您的配合！'
  },
  {
    title: '系统维护公告',
    time: '2023-08-01',
    content: '尊敬的用户，您好！为了给您提供更优质的服务，能链管家将于 2025-12-27 00:00-03:00进行升级更新，期间可能会影响您的正常登录使用。给您造成的不便，敬请谅解。如有疑问，请联系服务，感谢您的配合！'
  },
])
const gotoDetails = (item) => {
  uni.navigateTo({
    url: `/thirdPackage/pages/mine/messageFile/systemDetails`
  });
}
</script>

<style scoped lang="less">
.container {
  background-color: #f5f5f5;
  width: 100%;
  height: 100%;
  padding: 25rpx 4%;
}
.system-announcement-item {
  .system-time {
    color: #aeaeae;
    font-family: PingFang SC, PingFang SC;
    font-weight: 400;
    font-size: 24rpx;
    text-align: center;
  }
  .system-content {
    background: #fff;
    box-shadow: 0 0 2rpx rgba(0, 0, 0, 0.05);
    border-radius: 30rpx;
    padding: 27rpx;
    margin: 20rpx;
    .system-title {
      font-family: PingFang SC, PingFang SC;
      font-weight: 600;
      font-size: 32rpx;
      color: #2c353e;
      margin-bottom: 25rpx;
    }
    .system-content-text {
      font-family: PingFang SC, PingFang SC;
      font-weight: 400;
      font-size: 24rpx;
      color: #7d7d7d;
      line-height: 38rpx;

      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2; /* 限制显示行数为2 */
      overflow: hidden;
      // text-overflow: ellipsis; /* 超出部分显示省略号 */
      text-overflow: clip; /* 直接截断，不显示省略号 */
    }
    .system-content-details {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-family: PingFang SC, PingFang SC;
      font-weight: 400;
      font-size: 24rpx;
      color: #7d7d7d;
    }
  }

  .line {
    width: 100%;
    height: 0rpx;
    border-bottom: 1rpx solid #e8e6e6;
    margin: 0 auto;
    margin: 27rpx 0;
  }
}
</style>