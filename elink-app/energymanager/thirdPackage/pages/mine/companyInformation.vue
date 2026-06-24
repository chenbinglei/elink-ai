<template>
  <view class="container">
    <view class="title">公司信息</view>
    <view class="company-info">
      <view class="company-item">
        <view class="company-info-item-left">
          公司名称
        </view>
        <view class="company-info-item-right">
          杭州晟曼科技有限公司
        </view>
      </view>
      <view class="line"></view>
      <view class="company-item">
        <view class="company-info-item-left">
          公司地址
        </view>
        <view class="company-info-item-right">
          浙江省杭州市西湖区三墩镇西园八路 七号西投云城犀谷 1 号楼 907室
        </view>
      </view>
      <view class="line"></view>
      <view class="company-item">
        <view class="company-info-item-left">
          主账号
        </view>
        <view class="company-info-item-right">
          hzsm
        </view>
      </view>
      <view class="line"></view>
      <view class="company-item">
        <view class="company-info-item-left">
          联系方式
        </view>
        <view class="company-info-item-right">
          13766887788
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
  padding: 0 4%;
}
.title {
  padding: 20rpx 0;
  font-family: PingFang SC, PingFang SC;
  font-weight: 400;
  font-size: 24rpx;
  color: #606163;
  line-height: 30rpx;
  padding: 28rpx;
}
.company-info {
  width: 100%;
  background-color: #fff;
  color: #606163;
  font-size: 24rpx;
  box-shadow: 0rpx 0rpx 2rpx 0rpx rgba(0, 0, 0, 0.05);
  border-radius: 30rpx 30rpx 30rpx 30rpx;
  .company-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 35rpx;
    height: 93rpx;
  }
  .company-info-item-left {
    font-family: PingFang SC, PingFang SC;
    font-weight: 400;
    font-size: 28rpx;
    color: #000000;
    line-height: 30rpx;
  }
  .company-info-item-right {
    width: 70%;
    text-align: right;
  }
}
.line {
  width: 90%;
  height: 0rpx;
  border-bottom: 1rpx solid #e8e6e6;
  margin: 0 auto;
}
</style>