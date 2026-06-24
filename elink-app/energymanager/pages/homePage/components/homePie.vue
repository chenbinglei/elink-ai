<template>
  <view class="home-pie">
    <view class="site-name">
      <view class="site-name-text">{{ siteNameBar }}趋势</view>
      <view class="Magnified">
        <image src="../../../static/image/home/fangda.png" mode="" @click="screenEchart()"></image>
      </view>
    </view>
    <view class="site-date">
      <view v-for="item in dateType" :key="item.type" class="date-item" @click="dateTypeClick(item.dateType)"
        :class="{ 'active': selectType === item.dateType }">
        {{ item.typeName }}
      </view>

    </view>
    <view class="date-detailsTime">
      <u-icon name="arrow-left" size="14" v-if="selectType !== '4'" @click="lastTime()"></u-icon>
      <UDate :type="selectType" @dateTypeDate="handleDateTypeDate" :chageType="chageType" @valueDefault="valueDefault" :clickTime="clickTime" />

      <u-icon name="arrow-right" size="14" v-if="selectType !== '4'" @click="nextTime()"></u-icon>
    </view>
    <view class="bar-echarts">
      <lineArea style="width: 100%; height: 100%;" :lendList="lendList" :selectType="selectType" :selectDeviceType="selectDeviceType"></lineArea>

    </view>
    <view class="site-value-container">
      <view class="site-value-item">
        <view class="circle" style="background-color: #19CE89;"></view>
        <view class="text">{{ selectDeviceType === "dianzhuang" ? '电桩充' : selectDeviceType === "chuneng" ? '储能充' : '光伏发'
        }}电量：{{ $filters.numberUnit(selectDeviceType === "dianzhuang" ? lendList.chargeQt : selectDeviceType ===
            "chuneng" ?
            lendList.chargeQt : lendList.generateQt) }} 度</view>
      </view>
      <view class="site-value-item">
        <view class="circle" style="background-color: #FAB758;"></view>
        <view class="text">{{ selectDeviceType === "dianzhuang" ? '电桩放' : selectDeviceType === "chuneng" ? '储能放' : '上网'
        }}电量：{{ $filters.numberUnit(selectDeviceType === "dianzhuang" ? lendList.dischargeQt : selectDeviceType ===
            "chuneng"
            ? lendList.dischargeQt : lendList.netQt) }} 度</view>

      </view>
      <view class="site-value-item" v-if="selectDeviceType !== 'chuneng'">
        <view class="circle" style="background-color: #FAB758;"></view>
        <view class="text">{{ selectDeviceType === "dianzhuang" ? '充电金额' : '消纳电量'
        }}：{{ $filters.numberUnit(selectDeviceType === "dianzhuang" ? lendList.chargeMoney : lendList.consumeQt) }}
          {{ selectDeviceType === "dianzhuang" ? '元' : '度' }}</view>
      </view>
      <view class="site-value-item" v-if="selectDeviceType !== 'chuneng'">
        <view class="circle" style="background-color: #B775E6;"></view>
        <view class="text">{{ selectDeviceType === "dianzhuang" ? '充电次数' : '等效发电时长'
        }}：{{ $filters.numberUnit(selectDeviceType === "dianzhuang" ? lendList.chargeCount : lendList.effectiveTime) }}
          {{ selectDeviceType === "dianzhuang" ? '次' : '小时' }}</view>

      </view>
    </view>
  </view>
</template>
<script setup>
import { computed, ref, watch, onMounted } from 'vue'
import { findAllEnergyPile, findAllEnergyStorage, findAllEnergyPv } from '../../../api/homePage'
// 实现柱状图横向滚动
// import lineArea from './test.vue';
import lineArea from './lineArea.vue';
import UDate from '../../../components/UDate.vue'
const props = defineProps({
  siteNameBar: {
    type: String,
    default: ''
  },
  siteIds: {
    type: String,
    default: ''
  },
  selectDeviceType: {
    type: String,
    default: ''
  },
  isRefreshing: {
    type: Boolean,
    default: false
  }

})
const dateType = ref([
  {
    type: 'date',
    typeName: '日',
    dateType: '1'
  },
  {
    type: 'year-month',
    typeName: '月',
    dateType: '2'
  },
  {
    type: 'year',
    typeName: '年',
    dateType: '3'
  },
  {
    type: 'sum',
    typeName: '总',
    dateType: '4'

  }
])
const selectType = ref('1')
const startTime = ref('')
const endTime = ref('')
const lendList = ref({})
const chageType = ref(false)
const defaultTime = ref('')
const clickTime = ref('')

watch(() => props.selectDeviceType, (newVal) => {
  chageType.value = true
  selectType.value = '1'
  const today = new Date();
  const year = today.getFullYear(); // 年
  const month = String(today.getMonth() + 1).padStart(2, '0'); // 月（0-11，加1）
  const day = String(today.getDate()).padStart(2, '0'); // 日
  const todayStr = `${year}/${month}/${day}`;
  clickTime.value = todayStr

  startTime.value = `${year}-${month}-${day} 00:00:00`;
  endTime.value = `${year}-${month}-${day} 23:59:59`;
  getChartData()
})
onMounted(() => {
  // 初始化图表数据
})
watch(() => [props.isRefreshing, props.siteIds], (newVal) => {
  if (newVal) {
    selectType.value = '1'
    getChartData()
  }
})
const screenEchart = () => {
  uni.navigateTo({
    url: `/thirdPackage/pages/homePage/screenEcharts?lendList=${JSON.stringify(lendList.value)}&selectType=${selectType.value}&selectDeviceType=${props.selectDeviceType}`
  });
};
// 格式化日期函数
function formatDate (date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}


const handleDateTypeDate = (val) => {
  startTime.value = val.startTime
  endTime.value = val.endTime
  getChartData()
}

const valueDefault = (val) => {
  defaultTime.value = val
}
// 获取图表数据
const getChartData = () => {

  setTimeout(async () => {
    let arr = props.siteIds
    let obj = {
      siteIds: JSON.stringify(arr),
      startTime: selectType.value === '4' ? uni.getStorageSync('EARLIEST_CREATE_TIME') : startTime.value,
      endTime: selectType.value === '4' ? formatDate(new Date()) : endTime.value,
      dateType: selectType.value
    }
    let res = null;
    if (obj.startTime && obj.endTime) {
      switch (props.selectDeviceType) {
        case 'dianzhuang':
          res = await findAllEnergyPile(obj);
          // 使用 res.data 处理数据
          break;
        case 'chuneng':
          res = await findAllEnergyStorage(obj);
          break;
        case 'guangdong':
          res = await findAllEnergyPv(obj);
          break;
        default:
          break;
      }
      lendList.value = res.data
    }
  }, 100)
}




const lastTime = () => {
  if (selectType.value === '1') {
    const [year, month, day] = defaultTime.value.split('/');
    const date = new Date(year, month - 1, day);
    date.setDate(date.getDate() - 1);
    clickTime.value = formatTime(date);
    // 使用 date 对象生成 start 和 end 时间
    const newYear = date.getFullYear();
    const newMonth = String(date.getMonth() + 1).padStart(2, '0');
    const newDay = String(date.getDate()).padStart(2, '0');

    startTime.value = `${newYear}-${newMonth}-${newDay} 00:00:00`;
    endTime.value = `${newYear}-${newMonth}-${newDay} 23:59:59`;
  } else if (selectType.value === '2') {
    const [year, month] = defaultTime.value.split('/');
    const date = new Date(year, month - 1, 1);
    date.setMonth(date.getMonth() - 1);
    clickTime.value = formatYearMonth(date); // 使用新的格式

    const newYear = date.getFullYear();
    const newMonth = String(date.getMonth() + 1).padStart(2, '0');
    const lastDay = getLastDayOfMonth(newYear, newMonth);

    startTime.value = `${newYear}-${newMonth}-01 00:00:00`;
    endTime.value = `${newYear}-${newMonth}-${lastDay} 23:59:59`;
  } else {
    const year = parseInt(defaultTime.value);
    const newYear = year - 1;
    clickTime.value = newYear.toString();

    startTime.value = `${newYear}-01-01 00:00:00`;
    endTime.value = `${newYear}-12-31 23:59:59`;
  }

  getChartData();
};

const nextTime = () => {
  if (selectType.value === '1') {
    const [year, month, day] = defaultTime.value.split('/');
    const date = new Date(year, month - 1, day);
    date.setDate(date.getDate() + 1);
    clickTime.value = formatTime(date);

    // 使用新的 date 对象生成时间
    const newYear = date.getFullYear();
    const newMonth = String(date.getMonth() + 1).padStart(2, '0');
    const newDay = String(date.getDate()).padStart(2, '0');

    startTime.value = `${newYear}-${newMonth}-${newDay} 00:00:00`;
    endTime.value = `${newYear}-${newMonth}-${newDay} 23:59:59`;
  } else if (selectType.value === '2') {
    const [year, month] = defaultTime.value.split('/');
    const date = new Date(year, month - 1, 1);
    date.setMonth(date.getMonth() + 1);
    clickTime.value = formatYearMonth(date); // 使用新的格式

    const newYear = date.getFullYear();
    const newMonth = String(date.getMonth() + 1).padStart(2, '0');
    const lastDay = getLastDayOfMonth(newYear, newMonth);

    startTime.value = `${newYear}-${newMonth}-01 00:00:00`;
    endTime.value = `${newYear}-${newMonth}-${lastDay} 23:59:59`;
  } else {
    const year = parseInt(defaultTime.value);
    const newYear = year + 1;
    clickTime.value = newYear.toString();

    startTime.value = `${newYear}-01-01 00:00:00`;
    endTime.value = `${newYear}-12-31 23:59:59`;
  }

  getChartData();
};
const getLastDayOfMonth = (year, month) => {
  return new Date(year, month, 0).getDate()
}
const formatYearMonth = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  return `${year}/${month}`;
};
// 格式化日期函数
const formatTime = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}/${month}/${day}`;
};
const dateTypeClick = (type) => {
  selectType.value = type
}

</script>

<style lang="scss" scoped>
.home-pie {
  width: 100%;
  height: 100%;
  padding: 0 29rpx;

  .site-name {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 26rpx 0;

    .site-name-text {
      font-weight: 600;
      font-size: 28rpx;
      color: #000000;
    }

    .Magnified {
      padding: 10rpx;
      background: rgba(0, 0, 0, 0.1);
      border-radius: 50%;
      display: flex;
      align-content: center;
      justify-content: center;

      image {
        width: 30rpx;
        height: 30rpx;
      }
    }
  }

  .site-date {
    display: flex;
    width: 100%;
    align-items: center;
    justify-content: space-between;
    background: rgba(0, 0, 0, 0.1);
    border-radius: 143rpx;
    padding: 8rpx 2rpx;

    .date-item {
      font-weight: 400;
      font-size: 24rpx;
      color: rgba(255, 255, 255, 1);
      width: 25%;
      text-align: center;
    }

    .active {
      background: rgba(255, 255, 255, 1);
      color: rgba(56, 139, 255, 1);
      background: rgba(255, 255, 255, 1);
      border-radius: 343rpx;
    }
  }

  .date-detailsTime {
    display: flex;
    align-items: center;
    margin-top: 30rpx;
    justify-content: space-between;
  }

  .bar-echarts {
    width: 100%;
    height: 52%;
    margin-top: 26rpx;
    background-color: #fff;
    border-radius: 30rpx;
  }
}

.site-value-container {
  display: flex;
  align-items: center;
  /* gap: 50rpx; */
  flex-wrap: wrap;
  margin-top: 40rpx;
  background: rgba(56, 139, 255, 0.05);
  border-radius: 30rpx;

  padding: 13rpx 27rpx;

  .site-value-item {
    width: 50%;
    font-weight: 400;
    font-size: 20rpx;
    color: #333333;
    display: flex;
    align-items: center;
    gap: 15rpx;
    padding: 14rpx 0;

    .circle {
      width: 14rpx;
      height: 14rpx;
      border-radius: 50%;
    }
  }
}
</style>