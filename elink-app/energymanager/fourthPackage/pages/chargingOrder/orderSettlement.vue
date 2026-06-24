<template>
  <view class="containers">
    <view class="card-item" v-for="(item, index) in getFilteredChildren(list)" :key="index">
      <view class="card-item-title">
        <text>{{ item.name }}</text>
      </view>
      <view class="card-item-content">
        <view v-for="(child, index) in getFilteredChildren(item.children)" :key="index" class="card-item-content-item">
          <view class="card-item-content-label">{{ child.name }}</view>
          <view class="card-item-content-value">
            {{
              (() => {
                // 如果 child 配置了 root: true，就直接读根数据
                const val = child.root
                  ? orderInfo?.[child.fieldName]
                  : item.fieldName
                    ? orderInfo?.[item.fieldName]?.[child.fieldName]
                    : orderInfo?.[child.fieldName];

                return child.filterName
                  ? $filters[child.filterName]?.(val ?? '-/-')
                  : val ?? '-/-';
              })()
            }}
            <text v-if="child.unit"> {{ child.unit }}</text>
          </view>
        </view>
      </view>
      <view class="card-item-content" v-if="item.fieldName == 'refundRecordList'">
        <view v-for="x in orderInfo.refundRecordList" :key="x.id" class="card-item-content-item-refund">
          <view class="refundRecord-top">
            <view class="left">
              <uni-icons type="checkbox-filled" size="30" v-if="x.refundStatus === 1" color="#1FA900"></uni-icons>
              <uni-icons type="clear" size="30" v-else color="#F2632B"></uni-icons>
              <view class="refundStatus-time">{{ x.createTime }}</view>
            </view>
            <view class="refundStatus" :style="{ 'color': x.refundStatus === 1 ? '#1FA900' : '#F2632B' }">{{
              $filters.refundStatus(x.refundStatus) }}</view>
          </view>
          <view class="refundRecord-top">
            <view class="refundRecord-left">
              <text>操作人</text>
            </view>
            <view class="refundRecord-right">
              <text>{{ x.refundOperator }}</text>
            </view>
          </view>
          <view class="refundRecord-top bottom">
            <view class="refundRecord-left">
              <text>退款金额</text>
            </view>
            <view class="refundRecord-right">
              <text>￥{{ $filters.moneyTwoNum(x.refundAmount) }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({
  orderInfo: {
    type: Object,
    default: () => ({})
  },
  isDisCharging: {
    type: Boolean,
    default: false
  }
})
const getFilteredChildren = (children) => {
  return children.filter(child => !child.isShow)
}
const list = [
  {
    name: "结算信息",
    fieldName: "settlementRecord",
    children: [
      { name: props.isDisCharging ? "放电电量" : "充电电量", fieldName: "totalQt", unit: "kWh", root: true },
      { name: "原价总金额", fieldName: "totalCost", filterName: "moneyTwoNum", unit: "元", root: true, isShow: props.isDisCharging },
      { name: "预付金额", fieldName: "prepayMoney", filterName: "moneyTwoNum", root: true, isShow: props.isDisCharging },
      { name: "支付方式", fieldName: "payWay", filterName: props.isDisCharging ? "disPayWay" : "payWay" },
      { name: "实付电费", fieldName: "actualTotalElect", filterName: "moneyTwoNum", unit: "元", isShow: props.isDisCharging },
      { name: "放电收益（元）", fieldName: "actualTotalCost", filterName: "moneyTwoNum", isShow: !props.isDisCharging },
      { name: "实付服务费", fieldName: "actualTotalFee", filterName: "moneyTwoNum", unit: "元", isShow: props.isDisCharging },
      { name: "实付金额", fieldName: "actualTotalCost", filterName: "moneyTwoNum", unit: "元", isShow: props.isDisCharging },
      { name: "优惠金额", fieldName: "discountAmount", filterName: "moneyTwoNum", unit: "元", isShow: props.isDisCharging },
      { name: "退款金额", fieldName: "refundMoney", filterName: "moneyTwoNum", unit: "元", isShow: props.isDisCharging },
      { name: "结算状态", fieldName: "settlementState", filterName: "settlementState" },
    ]
  },
  {
    name: "退款记录",
    fieldName: "refundRecordList",
    isShow: props.isDisCharging,
    children: []
  }
]

const orderInfo = computed(() => props.orderInfo || {})
</script>

<style lang="scss" scoped>
/* 样式完全不变 */
.card-item {
  width: 100%;
  background: rgba(255, 255, 255, 1);
  border-radius: 38rpx;
  padding: 42rpx 30rpx;
  margin-top: 30rpx;

  .card-item-title {
    color: rgba(0, 0, 0, 1);
    font-weight: 500;
    font-size: 28rpx;

    text {
      position: relative;
      padding-bottom: 2rpx;
    }

    text::after {
      content: '';
      position: absolute;
      left: 50rpx;
      bottom: 0;
      width: 100%;
      height: 2rpx;
      background: linear-gradient(90deg, rgba(57, 138, 255, 1) 0%, rgba(57, 138, 255, 0.3) 60%, rgba(57, 138, 255, 0.01) 100%);
    }
  }

  .card-item-content-item {
    margin-top: 40rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: rgba(44, 53, 62, 1);
    font-size: 28rpx;
    font-weight: 400;

    .card-item-content-label {
      width: 40%;
      text-align: left;
    }

    .card-item-content-value {
      text-align: right;

      text {
        padding-left: 5rpx;
      }
    }
  }
}

.card-item-content-item-refund {
  margin-top: 40rpx;
  width: 100%;

  .refundRecord-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    font-size: 24rpx;

    .refundRecord-left {
      margin-left: 15%;
      font-weight: 400;
      color: rgba(74, 85, 101, 1);
    }

    .refundRecord-right {
      color: rgba(106, 114, 130, 1);
      font-weight: 400;
    }

    .left {
      display: flex;
      align-items: center;
      width: 40%;

      .refundStatus-time {
        padding-left: 30rpx;
        color: rgba(106, 114, 130, 1);
        font-weight: 400;

      }
    }

  }

  .bottom {
    margin-top: 30rpx;

    .refundRecord-right {
      color: rgba(0, 0, 0, 1);
      font-size: 28rpx;
    }
  }
}
</style>