<template>
  <!-- 计费详情 -->
  <view class="containers" v-if="orderInfoList?.length > 0">

    <view class="card-item" v-for="(item, index) in orderInfoList" :key="index">
      <view class="billingTop">
        <view class="billDuration">{{ item.chargeDuration }}</view>
        <view class="billDuration-right">
          <view class="billrechargeQt bt">
            <view>总电量</view>
            <view>{{ $filters.moenyTwoNum(item.rechargeQt) }} kWh</view>
          </view>

          <view class="billMoney bt">
            <view>总费用</view>
            <view class="color1">￥{{ $filters.moenyTwoNum(item.electMoney) }}</view>
          </view>
        </view>
      </view>
      <view class="billingBottom">
        <view class="billingBottom-left">
          <text :class="`type-${item.periodType}`" class="border-radius">{{ $filters.periodType(item.periodType)
          }}</text>
        </view>
        <view class="billingBottom-right">
          <view class="electricity-bill">
            <view class="billMoney bt">
              <view>电费</view>
              <view class="color1">￥{{ $filters.moenyTwoNum(item.electMoney) }}</view>
            </view>
            <view class="serviceMoney bt">
              <view>服务费</view>
              <view class="color1">￥{{ $filters.moenyTwoNum(item.serviceMoney) }}</view>
            </view>
          </view>
          <view class="electricity-price">
            <view class="electPrice bt">
              <view>总电费价格</view>
              <view>￥{{ $filters.moenyTwoNum(item.electPrice) }}</view>
            </view>
            <view class="servicePrice bt">
              <view>服务费价格</view>
              <view>￥{{ $filters.moenyTwoNum(item.servicePrice) }}</view>
            </view>
          </view>


        </view>
      </view>
    </view>
  </view>
   <empty v-else></empty>

</template>


<script setup>
import empty from '@/components/uni-custom/empty.vue'

import { ref, watch } from 'vue'
const props = defineProps({
  orderInfo: {
    type: Object,
    default: () => {
      return {}
    }
  }
})

const orderInfoList = ref(null)
watch(() => props.orderInfo, (newValue, oldValue) => {
  if (newValue) {
    orderInfoList.value = newValue.chargingDetailsList
    console.log(orderInfoList.value)
  }
}, {
  immediate: true
})

</script>

<style lang="scss" scoped>
.card-item {
  width: 100%;
  background: rgba(255, 255, 255, 1);
  border-radius: 38rpx;
  padding: 42rpx 30rpx;
  margin-top: 30rpx;

  .billingTop {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .billDuration {
      width: 20%;
      color: rgba(20, 4, 255, 1);
      font-size: 28rpx;
      font-weight: 400;
    }

    .billDuration-right {
      width: 79%;
      display: flex;
      justify-content: space-between;
      align-items: center;

      text {
        padding-left: 30rpx;
      }

      .billrechargeQt {
        width: 51%;
        white-space: nowrap;

      }

      .billMoney {
        width: 47%;
        white-space: nowrap;
      }

    }

  }

  .billingBottom {
    display: flex;
    justify-content: space-between;
    margin-top: 33rpx;

    .billingBottom-left {
      width: 20%;

      .border-radius {
        padding: 8rpx 19rpx;
        border-radius: 12rpx;
        font-weight: 400;
        font-size: 24rpx;
        color: rgba(255, 255, 255, 1);
      }

      .type-1 {
        background: rgba(248, 39, 39, 1);
      }

      .type-2 {
        background: rgba(250, 183, 88, 1);
      }

      .type-3 {
        background: #56adf7;
      }

      .type-4 {
        background: rgba(25, 206, 137, 1);
      }

      .type-5 {
        background: #3399FF;
      }

      .type-6 {
        background: #6dcf3680;
      }




    }

    .billingBottom-right {
      width: 79%;

      .electricity-bill {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .billMoney {
          width: 51%;
          white-space: nowrap;
        }

        .serviceMoney {
          width: 46%;
          white-space: nowrap;
        }

      }

      .electricity-price {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 40rpx;

        .electPrice {
          width: 51%;
          white-space: nowrap;
        }

        .servicePrice {
          width: 46%;
          white-space: nowrap;

        }

      }

    }
  }
}

.bt {
  display: flex;
  justify-content: space-between;
  align-items: center;
  white-space: nowrap;
}

.color1 {
  color: rgba(41, 130, 255, 1);
}
</style>
