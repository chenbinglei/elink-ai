<template>
  <view class="containers">
    <view class="orderStatus_card">
      <view :class="['orderStatus', 'bgc-' + orderDetail.orderStatus]">
        <text class="iconfont icon-chenggong1" v-if="orderDetail.orderStatus == 2"></text>
        <text class="iconfont icon-shibai2" v-if="orderDetail.orderStatus == 3"></text>
        <text class="iconfont icon-rum-status-api" v-if="orderDetail.orderStatus == 1"></text>
        {{ $filters.chargingState(orderDetail.orderStatus) }}

      </view>
      <view class="orderStatus_card-title">订单编号：</view>
      <view class="orderStatus_card-value">{{ orderDetail.orderNum }}<text
          class="iconfont-site iconfont icon-wendang-moren"></text></view>

    </view>
    <view class="orderDetail-tabs">
      <view v-for="item in orderDetailTab" @click="switchTab(item.value)" :key="item.value" class="tabs-item"
        :class="{ 'active': item.value == orderTab }">{{ item.label }}</view>
    </view>
    <view class="orderDetail-tabs-content">
      <orderInformation v-if="orderTab == 1" :orderInfo="orderDetail"></orderInformation>
      <orderJourney v-if="orderTab == 2" :orderInfo="orderDetail"></orderJourney>
      <orderSettlement v-if="orderTab == 3" :orderInfo="orderDetail"></orderSettlement>
      <processAnalysis v-if="orderTab == 4" :orderInfo="orderDetail"></processAnalysis>
      <billingDetails v-if="orderTab == 5" :orderInfo="orderDetail"></billingDetails>




    </view>
  </view>
</template>


<script setup>
import { ref } from 'vue'
import { orderDetailTab } from './components/typeListArray.js'
import orderInformation from './chargingOrder/orderInformation.vue'
import orderJourney from './chargingOrder/orderJourney.vue'
import orderSettlement from './chargingOrder/orderSettlement.vue'
import processAnalysis from './chargingOrder/processAnalysis.vue'
import billingDetails from './chargingOrder/billingDetails.vue'
import { findOrderRecordInfoById } from '../api/chargingOrder.js'

import { onLoad, onShow, onPullDownRefresh, onReachBottom, onHide } from '@dcloudio/uni-app'

const orderTab = ref(1)
onLoad((options) => {
  console.log(options, '传递过来的值');
  findOrderRecordInfoById({ orderId: options.orderId }).then(res => {
    orderDetail.value = res.data
    console.log(res.data, '订单详情')

  })

})


const orderDetail = ref({

})
const switchTab = (type) => {
  orderTab.value = type
}

</script>

<style lang="scss" scoped>
.containers {
  padding: 20rpx 25rpx;
  background: rgba(245, 245, 245, 1);

  .orderStatus_card {
    width: 100%;
    background: linear-gradient(0deg, #f6fcfc 0%, #f4fcfe 100%);
    border-radius: 26rpx;

    .orderStatus {
      text-align: center;
      padding: 25rpx 0;
      font-weight: 600;
      font-size: 28rpx;
    }

    .orderStatus_card-title {
      font-size: 28rpx;
      color: #4a5565;
      text-align: center;
    }

    .orderStatus_card-value {
      font-size: 28rpx;
      color: #4a5565;
      text-align: center;
      padding-bottom: 26rpx;

      .iconfont-site {
        padding-left: 10rpx;
      }
    }
  }

  .orderDetail-tabs {
    padding-top: 20rpx;
    padding-bottom: 10rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .tabs-item {
      width: 20%;
      text-align: center;
      padding-bottom: 20rpx;
      border-bottom: 2rpx solid rgba(0, 0, 0, 0.15);
    }

    .active {
      color: #007AFF;
      border-bottom: 2rpx solid #007AFF;
    }
  }
}

.orderDetail-tabs-content {
  height: 75.5vh;
  overflow: auto;
}

.bgc-0 {
  color: #6A7282;
}

.bgc-1 {
  color: #007aff;
}

.bgc-2 {
  color: #00C950;

}

.bgc-3 {
  color: #FF4D4F;
}

.bgc-4 {
  color: #f0cd99;
}

.bgc-5 {
  color: #FF9900;
}

.bgc-6 {
  color: #dae7d3;

}
</style>