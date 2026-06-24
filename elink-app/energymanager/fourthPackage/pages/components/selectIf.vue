<template>
  <view class="containers">
    <view class="select-list">
      <view class="select-item" v-for="(item, index) in selectList" :key="index" @click="filterSelect(item)">
        <view class="select-item-text" v-if="item.name !== '4'" :style="{
          color: filterType.name === item.name && filterCard ? '#007AFF' : '#2C2C2C'
        }">
          {{ item.value }}
          <view class="iconfont icon-xiajiantou" v-if="item.name === filterType.name && filterCard"> </view>
          <view class="iconfont icon-shangjiantou" v-else></view>
        </view>
        <view class="select-item-other" v-else :style="{
          color: filterType.name === item.name && filterCard ? '#007AFF' : '#2C2C2C'
        }">
          {{ item.value }}
          <view class="iconfont icon-shaixuan">
          </view>
        </view>
      </view>
    </view>
    <view class="filter-card-list" v-if="filterCard">
      <view class="filter-content" v-if="filterType.name == 1">
        <timeRange style="width: 100%;" @update:startDate="handleStart" :timeSelectList="timeSelectList"
          @update:endDate="handleEnd" :resetList="resetList" :btnIsshow="true" @close-card="filterCard = false;">
        </timeRange>


      </view>
      <view class="filter-content" v-if="filterType.name == 2" style=" margin-top: 16rpx;">
        <view v-for="(item, index) in type === 'disChargingOrder' ? dischangeType : changeType" :key="index"
          class="filter-content-item" :class="orderStatus == item.id ? 'select-item' : ''"
          @click="selectType('changeType', item)">
          {{ item.name }}
        </view>
      </view>
      <view class="filter-content" v-if="filterType.name == 3" style=" margin-top: 16rpx;">
        <view v-for="(item, index) in errType" :key="index" class="filter-content-item"
          :class="abnormalType == item.id ? 'select-item' : ''" @click="selectType('errType', item)">
          {{ item.name }}
        </view>
      </view>

    </view>
    <uni-popup ref="filterPopup" background-color="#fff" :close-on-click-overlay="false">
      <view style="height: 70vh;border-top-left-radius: 30rpx;border-top-right-radius: 30rpx;" class="filter-popup">
        <view class="popup-title"> <text>更多筛选</text> <uni-icons class="iconfont" type="closeempty" size="20"
            @click="closeFilterPopup"></uni-icons>
        </view>
        <view class="filter-popup-content">
          <!-- 电桩类型 -->
          <view class="filter-popup-Type">
            <view class="filter-popup-Type-label">电桩类型</view>
            <view class="filter-content">
              <view class="filter-content-item" v-for="(item, index) in typeListArray" :key="index"
                :class="pileType == item.id ? 'select-item' : ''" @click="selectType('pileType', item)">
                {{ item.name }}
              </view>
            </view>
          </view>
          <!-- 启动方式 -->
          <view class="filter-popup-Type">
            <view class="filter-popup-Type-label">启动方式</view>
            <view class="filter-content">
              <view class="filter-content-item" v-for="(item, index) in runModeArray" :key="index"
                :class="runMode == item.id ? 'select-item' : ''" @click="selectType('runMode', item)">
                {{ item.name }}
              </view>
            </view>
          </view>
          <!-- 补单状态 -->
          <view class="filter-popup-Type">
            <view class="filter-popup-Type-label">补单状态</view>
            <view class="filter-content">
              <view class="filter-content-item" v-for="(item, index) in repairStatusArray" :key="index"
                :class="repairStatus == item.id ? 'select-item' : ''" @click="selectType('repairStatus', item)">
                {{ item.name }}
              </view>
            </view>
          </view>
          <!-- 电量区间 -->
          <view class="filter-popup-Types" style="margin-top: 10rpx;">
            <view class="filter-popup-Type-label">电量区间</view>
            <view class="filter-popup-Type-value">
              <numericRange v-model:minValue="minQt" v-model:maxValue="maxQt" unit="度"></numericRange>
            </view>
          </view>
          <view class="filter-popup-Types">
            <view class="filter-popup-Type-label">时长区间</view>
            <view class="filter-popup-Type-value">
              <numericRange v-model:minValue="minDuration" v-model:maxValue="maxDuration" unit="时"></numericRange>

            </view>
          </view>

          <!-- 开始时间 -->
          <view class="filter-popup-Types">
            <view class="filter-popup-Type-label">结束时间</view>
            <view class="filter-popup-Type-value datetime">
              <timeRange ref="timeRangeRef" style="width: 100%;" @update:startDate="endChangeTimeStart"
                @update:endDate="endChangeTimeEnd">
              </timeRange>
            </view>
          </view>
        </view>
        <view class="filter-popup-btn">
          <view class="filter-popup-btn-cancel" @click="resetBtn">重置</view>
          <view class="filter-popup-btn-confirm" @click="submitBtn">确定</view>
        </view>

      </view>
    </uni-popup>
  </view>
</template>

<script setup>
import { ref, watch } from 'vue'
import timeRange from './date-time-range.vue'
import numericRange from './numericRange.vue'
import {
  selectList,
  timeSelectList,
  changeType,
  dischangeType,

  errType,
  typeListArray,
  runModeArray,
  repairStatusArray
} from './typeListArray.js'
const filterType = ref({})
const filterPopup = ref(null)
const filterCard = ref(false)
const startTime = ref('');
const endTime = ref('');
const resetList = ref(false)
const orderStatus = ref(null)// 订单状态
const abnormalType = ref(null)// 异常类型
const minDuration = ref('')// 电量区间最小值
const maxDuration = ref('')// 电量区间最大值
const endAlsoStartDate = ref('')// 结束时间开始
const endAlsoEndDate = ref('')// 结束时间结束
const pileType = ref(null)// 电桩类型
const runMode = ref(null)// 启动方式
const repairStatus = ref(null)// 补单状态
const minQt = ref('')// 电量区间最小值
const maxQt = ref('')// 电量区间最大值
const type = ref([])
const timeRangeRef = ref(null)
const props = defineProps({
  type: {
    type: String,
    default: ''
  }
})
const emit = defineEmits(['handleTimeRange', 'handleOrderStatus', 'handleAbnormalType', 'handleFilter'])


watch(() => props.type, (newValue, oldValue) => {
  if (newValue) {
    type.value = newValue
  }
}, { immediate: true })

const filterSelect = (item) => {
  if (item.name == filterType.value.name) {
    filterCard.value = !filterCard.value
  } else {
    if (item.name == '4') {
      filterPopup.value.open('bottom')
      filterCard.value = false
    } else {
      filterCard.value = true
      filterType.value = item
    }
  }
}
const closeFilterPopup = () => {
  filterPopup.value.close()
}
// 重置
const resetBtn = () => {
  pileType.value = null
  runMode.value = null
  repairStatus.value = null
  minQt.value = null
  maxQt.value = null
  minDuration.value = null
  maxDuration.value = null
  endAlsoStartDate.value = null
  endAlsoEndDate.value = null
  timeRangeRef.value.resetBtn()
  filterPopup.value.close()

}
const submitBtn = () => {
  let obj = {
    pileType: pileType.value||'',
    runMode: runMode.value||'',
    repairStatus: repairStatus.value||'',
    minQt: minQt.value||'',
    maxQt: maxQt.value||'',
    minDuration: minDuration.value||'',
    maxDuration: maxDuration.value||'',
    endAlsoStartDate: endAlsoStartDate.value||'',
    endAlsoEndDate: endAlsoEndDate.value||'',
  }
  emit('handleFilter', obj)
  filterPopup.value.close()


}

const endChangeTimeStart = (val) => {
  endAlsoStartDate.value = val
}
const endChangeTimeEnd = (val) => {
  endAlsoEndDate.value = val
}
const selectType = (val, item) => {

  if (val == 'changeType') {
    orderStatus.value = item.id
    emit('handleOrderStatus', item.id)
  } else if (val == 'errType') {
    abnormalType.value = item.id
    emit('handleAbnormalType', item.id)
  } else if (val == 'pileType') {
    pileType.value = item.id
  } else if (val == 'runMode') {
    runMode.value = item.id
  } else if (val == 'repairStatus') {
    repairStatus.value = item.id
  }
}
const handleStart = (val) => {
  startTime.value = val
};

const handleEnd = (val) => {
  endTime.value = val
  emit('handleTimeRange', [startTime.value, val])
};

</script>

<style lang="scss" scoped>
.containers {
  .select-list {
    display: flex;
    align-items: center;
    justify-content: space-around;

    .select-item {
      color: #000000;

      .select-item-text {
        padding: 10rpx 20rpx;
        display: flex;
        align-items: center;
        background-color: #F1F1F1;
        border-radius: 34rpx;
        gap: 8rpx;
      }

      .select-item-other {
        display: flex;
        align-items: center;
        gap: 8rpx;
      }
    }
  }
}

.filter-card-list {
  background-color: #fff;
  width: 100%;
  z-index: 100;
  padding: 20rpx 40rpx;

  .filter-content {
    display: flex;
    align-items: center;
    gap: 34rpx;
    flex-wrap: wrap; // 允许换行

  }

  .select-item {
    background-color: #c3dcff;
    color: #1877ff;
  }

  .filter-content-item {
    padding: 1%;
    border-radius: 30rpx;
    background: #f3f3f3;
    flex: 0 0 29%; // 每个 item 占 32% 宽度（留出间距）
    text-align: center;
    width: 100%;

  }
}

.filter-popup {
  padding: 20rpx 40rpx;

  .popup-title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20rpx 40rpx 0 40rpx;

    font-size: 32rpx;
    font-weight: 500;
    position: relative;

    text {
      width: 100%;
      text-align: center;
    }

    .iconfont {
      position: absolute;
      right: 40rpx;
    }
  }

  .filter-popup-content {
    height: 86%;
    overflow: auto;
  }

  .filter-popup-Types {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .filter-popup-Type-value {
      width: 75%;
    }

    .datetime {

      border-radius: 20rpx;
      overflow: hidden;
    }

  }

  .filter-popup-Type-label {
    font-size: 32rpx;
    font-weight: 500;
    padding: 33rpx 0;
  }

  .filter-popup-Type-content {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    align-items: center;
  }

  .filter-content {
    display: flex;
    align-items: center;
    gap: 34rpx;
    flex-wrap: wrap; // 允许换行
  }

  .select-item {
    background-color: #c3dcff;
    color: #1877ff;
  }

  .filter-content-item {
    padding: 1%;
    border-radius: 30rpx;
    background: #f3f3f3;
    flex: 0 0 29%; // 每个 item 占 32% 宽度（留出间距）
    text-align: center;
    width: 100%;

  }

  .filter-popup-btn {
    display: flex;
    justify-content: space-around;
    align-items: center;
    padding: 20rpx 0;

  }

  .filter-popup-btn-cancel {
    padding: 20rpx 80rpx;
    color: rgba(24, 119, 255, 1);
    background: #f3f3f3;
    border: 1rpx solid rgba(243, 243, 243, 1);
    border-radius: 48.5rpx;
  }

  .filter-popup-btn-confirm {
    padding: 20rpx 80rpx;
    background: rgba(24, 119, 255, 1);
    border: 1rpx solid rgba(243, 243, 243, 1);
    color: rgba(255, 255, 255, 1);
    border-radius: 48.5rpx;

  }

}
</style>