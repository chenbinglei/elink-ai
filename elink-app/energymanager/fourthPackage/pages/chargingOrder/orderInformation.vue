<template>
  <view class="containers">
    <view class="card-item" v-for="(item, index) in list" :key="index">
      <view class="card-item-title">
        <text>{{ item.name }}</text>
      </view>
      <view class="card-item-content">
        <!-- 改用过滤后的子数组，消除 v-for + v-if 警告 -->
        <view 
          v-for="(child, idx) in getFilteredChildren(item.children)" 
          :key="idx" 
          class="card-item-content-item"
        >
          <view class="card-item-content-label">{{ child.name }}</view>
          <view class="card-item-content-value">
            {{
              (() => {
                const val = item.fieldName
                  ? orderInfo?.[item.fieldName]?.[child.fieldName]
                  : orderInfo?.[child.fieldName];
                return child.filterName
                  ? $filters[child.filterName]?.(val ?? '-/-')
                  : val ?? '-/-';
              })()
            }}<text v-if="child.unit"> {{ child.unit }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
const props = defineProps({
  orderInfo: {
    type: Object,
    default: () => {
      return {}
    }
  },
  isDisCharging: {
    type: Boolean,
    default: false
  }
})

// 子项过滤方法（核心修复）
const getFilteredChildren = (children) => {
  return children.filter(child => !child.isShow)
}

const list = ref([{
  name: "基本信息",
  children: [
    { name: "订单号", fieldName: "orderNum" },
    { name: "订单状态", fieldName: "orderStatus", filterName: props.isDisCharging ? "disChargingState" : "chargingState", className: "orderStatus" },
    { name: props.isDisCharging ? "放电电量（度）" : "充电电量（度）", fieldName: "totalQt", unit: "度", filterName: "moneyTwoNum" },
    { name: "订单金额（元）", fieldName: "totalCost", unit: "元", filterName: "moneyTwoNum" },
    { name: "电费（元）", fieldName: "totalElect", unit: "元", filterName: "moneyTwoNum" },
    { name: "服务费（元）", fieldName: "totalFee", unit: "元", filterName: "moneyTwoNum", isShow: props.isDisCharging },
    { name: props.isDisCharging ? "放电前Soc" : "充电前Soc", fieldName: "startSoc", unit: "%", filterName: "moneyTwoNum" },
    { name: props.isDisCharging ? "放电后Soc" : "充后Soc", fieldName: "endSoc", unit: "%", filterName: "moneyTwoNum" },
    { name: "创建订单时间", fieldName: "createTime" },
    { name: props.isDisCharging ? "放电开始时间" : "开始充时间", fieldName: "startTime" },
    { name: props.isDisCharging ? "放电结束时间" : "结束充时间", fieldName: "endTime" },
    { name: props.isDisCharging ? "放电时长" : "充电时长", fieldName: "chargeDuration" },
    { name: "停止码", fieldName: "stopReason" },
    { name: "结束原因", fieldName: "stopDetailReason" },
    { name: "是否有序充/放电", fieldName: "isOrderly", filterName: "whetherOrNot" },
  ]
}, {
  name: "设备信息",
  fieldName: "pileInfoData",
  children: [
    { name: "电站名称", fieldName: "siteName" },
    { name: "电站ID", fieldName: "siteId" },
    { name: "运营商名称", fieldName: "operatorName" },
    { name: "所在城市", fieldName: "city" },
    { name: "具体地址", fieldName: "address" },
    { name: "设备出厂编码", fieldName: "factoryCode" },
    { name: "电桩类型", fieldName: "pileType", filterName: "pileType" },
    { name: "桩编号", fieldName: "pileCode" },
    { name: "枪编号", fieldName: "gunCode" },
  ]
}, {
  name: "补单信息",
  fieldName: "repairOrderRecord",
  children: [
    { name: "挂单时间", fieldName: "createTime" },
    { name: "补单时间", fieldName: "updateTime" },
    { name: "补单状态", fieldName: "repairStatus", filterName: "repairStatus", className: "repairStatus" },
    { name: "异常时长", fieldName: "exceptionTime" },
    { name: "操作人", fieldName: "repairOperator" },
  ]
}, {
  name: "用户信息",
  children: [
    { name: "账号类型", fieldName: "accountType", filterName: "accountType" },
    { name: "手机号", fieldName: "accountData" },
    { name: "启动方式", fieldName: "starter", filterName: "pileRunMode" },
    { name: "平台", fieldName: "platformName" },
    { name: "车牌号", fieldName: "plateNumber" },
    { name: "VIN码", fieldName: "busVin" },
    { name: "电卡卡号", fieldName: "cardNumber" },
    { name: "企业账户", fieldName: "enterpriseAccount" },
    { name: "车队名称", fieldName: "fleetName" },
    { name: "开票状态", fieldName: "invoicingState", filterName: "invoicingState", className: "invoicingState" },
  ]
}])

const orderInfo = ref(null)
watch(() => props.orderInfo, (newValue) => {
  if (newValue) orderInfo.value = newValue
}, { immediate: true })

</script>

<style lang="scss" scoped>
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
      width: 34%;
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
</style>