<template>
  <view class="container">
    <view class="system-title">
      {{item.title}}
    </view>
    <view class="system-content-text">{{item.content}}</view>
  </view>
</template>

<script setup name="setup">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app';
const item = ref({
  title: '系统维护公告',
  time: '2023-08-01',
  content: '尊敬的用户，您好！为了给您提供更优质的服务，能链管家将于 2025-12-27 00:00-03:00进行升级更新，期间可能会影响您的正常登录使用。给您造成的不便，敬请谅解。如有疑问，请联系服务，感谢您的配合！'
});
</script>

<style scoped lang="less">
.container {
  background-color: #f5f5f5;
  width: 100%;
  height: 100%;
  padding: 25rpx 4%;
}
.system-title {
  margin-top: 30rpx;
  font-size: 32rpx;
  font-weight: 600;
  color: #2c353e;
  line-height: 30rpx;
}
.system-content-text {
  margin-top: 53rpx;
  font-family: PingFang SC, PingFang SC;
  font-weight: 600;
  font-size: 24rpx;
  color: #000000;
  line-height: 45rpx;
   text-indent: 2em;
}
</style>