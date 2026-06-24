<template>
  <view class="containers">
    <view class="orderStatus_card">
      <view :class="['orderStatus', 'bgc-' + orderDetail.orderStatus]">
        <text class="iconfont icon-chenggong1" v-if="orderDetail.orderStatus == 2"></text>
        <text class="iconfont icon-shibai2" v-if="orderDetail.orderStatus == 3"></text>
        <text class="iconfont icon-rum-status-api" v-if="orderDetail.orderStatus == 1"></text>
        {{ $filters.disChargingState(orderDetail.orderStatus) }}

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
      <orderInformation v-if="orderTab == 1" :orderInfo="orderDetail" :isDisCharging="true"></orderInformation>
      <orderJourney v-if="orderTab == 2" :orderInfo="orderDetail" :isDisCharging="true"></orderJourney>
      <orderSettlement v-if="orderTab == 3" :orderInfo="orderDetail" :isDisCharging="true"></orderSettlement>
      <processAnalysis v-if="orderTab == 4" :orderInfo="orderDetail" :isDisCharging="true"></processAnalysis>
      <billingDetails v-if="orderTab == 5" :orderInfo="orderDetail" :isDisCharging="true"></billingDetails>




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

const orderTab = ref(1)


const orderDetail = ref({

  "id": "31026202506060012604160658202002",
  "siteId": "2c99698b9733ea2d0197540002b101a1",
  "isOrderly": 2,
  "orderNum": "31026202506060012604160658202002",
  "prepayMoney": 0,
  "orderStatus": 2,
  "type": 1,
  "clockingTime": null,
  "pileCode": "3102620250606001",
  "gunCode": 1,
  "starter": 5,
  "platformName": null,
  "startTime": "2026-04-16 06:58:20",
  "endTime": "2026-04-16 11:10:45",
  "totalQt": 181.76,
  "totalCost": 72.704,
  "totalElect": 0,
  "totalFee": 0,
  "startSoc": 97,
  "endSoc": 37,
  "stopReason": "4104",
  "stopDetailReason": "TCU正常停止，达到桩设定SOC值",
  "accountType": 3,
  "accountData": "0571-86625251",
  "cardNumber": null,
  "createTime": "2026-04-16 11:10:45",
  "updateTime": "2026-04-16 11:10:45",
  "chargeDuration": "04:12:25",
  "busVin": "LC04S74P5NB000282",
  "startDirMeter": 64862.96,
  "endDirMeter": 65044.72,
  "pileInfoData": {
    "siteName": "南部公司陡沟V2V场站",
    "siteId": "2c99698b9733ea2d0197540002b101a1",
    "operatorName": "济南公交集团",
    "province": "山东省",
    "city": "济南市",
    "county": "历下区",
    "address": "山东省济南市历下区舜华路街道瑞晶精品酒店(济南奥体中心店)天业龙奥天街",
    "pileType": 30,
    "factoryCode": null
  },
  "userRecordDto": null,
  "repairOrderRecord": null,
  "settlementRecord": {
    "payWay": 2,
  },
  "chargingDetailsList": null,
  "refundRecordList": null
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