<template>
  <view class="content">
    <view class="alarm-echart">
      <pieEcharts style="width: 30%; height: 100%;" :statusTypeList="statusTypeList" :totalSize="totalSize">
      </pieEcharts>

      <view class="alarm-echart-title">
        <view v-for="(item, index) in statusTypeList" :key="index" class="alarm-echart-title-item">
          <view class="alarm-echart-label">{{ $filters.numberUnit(item.value) }}</view>
          <view class="alarm-echart-title-value">
            <view class="circle" :style="{ backgroundColor: item.color }"></view>
            <view>{{ item.label }}</view>
          </view>
        </view>
      </view>
    </view>
    <view class="alarm-search">
      <view class="alarm-search-type" @click="showTypePicker">
        <view class="alarm-search-type-text"> {{ selectedType.name }}</view>
        <u-icon name="arrow-down-fill" color="#000000" size="12"></u-icon>
        <view class="alarm-search-type-list" v-if="showDropdown">
          <view class="alarm-search-type-list-item" v-for="(item, index) in typeList" :key="index" @click.stop="selectType(item)" :class="{'active': item.name == selectedType.name}">
            {{ item.name }}
          </view>
        </view>
      </view>

      <view class="alarm-search-input">
        <u--input placeholder="请输入" border="surround" style="background-color: #ffffff;border-radius: 100rpx;" v-model="searchValue" @change="change"
          prefixIcon="search" prefixIconStyle="font-size: 22px;color: #909399">
          <template #suffix>
            <image class="image" src="/static/image/alarm/selecttp.png" mode="" @click="goSelectSite()"></image>
          </template>
        </u--input>
      </view>
      <image class="alarm-search-more" src="/static/image/alarm/moreselect.png" @click="show = true"></image>
    </view>
    <scroll-view class="alarm-list-wrap" scroll-y @scrolltolower="handleScrollToLower" lower-threshold="100" scroll-with-animation :style="{ height: '68vh' }"
      @scroll="handleScroll">
      <view class="alarm-list" v-if="alarmList?.length > 0">
        <view class="alarm-item" v-for="(item, index) in alarmList" :key="`alarm_${index}_${item.year}_${item.dataDay}`">
          <view class="alarm-item-dataTime">
            <view class="alarm-item-dataTime-day">{{ item.dataDay }}</view>
            <view class="alarm-item-dataTime-time">{{ item.year }}</view>
            <view class="line" v-if="index != alarmList.length - 1">
              <u-line direction="col" color="#A9A9A9" dashed></u-line>
            </view>
          </view>
          <view class="alarm-item-content">
            <view class="alarm-item-content-list" v-for="(i, j) in item.list" :key="`item_${j}_${i.deviceNumber || i.id}`"
              :style="{ backgroundColor: i.eventLevel == 1 ? '#FEBB0B' : i.eventLevel == 2 ? '#EA721C' : i.eventLevel == 3 ? '#FA2E49' : i.eventLevel == 4 ? '#5890FD' : '#B8B8B8' }">
              <view class="alarm-bgc">
                <view class="pl-15 alarm-item-content-list-status">
                  <view class="status-tip" :style="{
                    backgroundColor: i.eventLevel == 1 ? '#FEBB0B' : i.eventLevel == 2 ? '#EA721C' : i.eventLevel == 3 ? '#FA2E49' : i.eventLevel == 4 ? '#5890FD' : '#B8B8B8',
                    border: (i.eventLevel === 1 ? '#FF0022' : i.eventLevel === 2 ? '#EA721C' : i.eventLevel === 3 ? '#5890FD' : i.eventLevel === 4 ? '#FEBB0B' : '#B8B8B8') + ' 1px solid'
                  }">
                    <view class="status-tip-text">{{ $filters.iseventLevel(i.eventLevel) }}</view>
                    <view class="time">
                      <image class="image" src='/static/image/alarm/Rectangle.png' mode="aspectFit"></image>
                      <view
                        :style="{ color: i.eventLevel == 1 ? '#FF0022' : i.eventLevel == 2 ? '#EA721C' : i.eventLevel == 3 ? '#5890FD' : i.eventLevel == 4 ? '#FEBB0B' : '#B8B8B8' }"
                        class="creatime">{{ i.createTime?.slice(11) }}</view>
                    </view>
                  </view>
                  <view class="list-stuts" :style="{ backgroundColor: i.eventStatus == 0 ? '#FF0022' : '#5890FD' }">
                    {{ i.eventStatus == 0 ? "转消缺" : '已修复' }}
                  </view>
                </view>
                <view class="pl-15 siteName">{{ i.eventName }}</view>
                <view class="pl-15 content-value"
                  :style="{ backgroundColor: i.eventLevel == 1 ? 'rgba(250, 46, 73, 0.07)' : i.eventLevel == 2 ? 'rgba(234, 114, 28, 0.07)' : i.eventLevel == 3 ? 'rgba(88, 144, 253, 0.07)' : i.eventLevel == 4 ? 'rgba(254, 187, 11, 0.07)' : 'rgba(184, 184, 184, 0.07)' }">
                  <view class="content-label">所属场站</view>{{ i.siteName }}
                </view>
                <view class="pl-15 content-value">
                  <view class="content-label">设备类型</view>{{ i.typeName }}
                </view>
                <view class="pl-15 content-value">
                  <view class="content-label">告警对象</view>{{ i.deviceName }}
                </view>
                <view class="pl-15 content-value">
                  <view class="content-label">SN号</view>{{ i.deviceNumber }}
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 加载中提示 -->
        <view class="load-more" v-if="isLoading">
          <u-loading-icon mode="flower" size="20"></u-loading-icon>
          <text style="font-size: 24rpx; margin-left: 10rpx;">加载中...</text>
        </view>

        <!-- 没有更多数据提示 -->
        <view class="no-more" v-if="!hasMore && alarmList.length > 0">
          <text style="font-size: 24rpx; color: #999;">没有更多数据了</text>
        </view>
      </view>

      <view v-else class="emptying">
        <empty></empty>
      </view>
    </scroll-view>

    <u-popup :show="show" :round="10" mode="bottom">
      <view style="height: 70vh;">
        <view class="select-tip">筛选</view>
        <scroll-view scroll-y style="height: 78%">
          <view class="select-list">
            <view>设备类型</view>
            <view class="select-option">
              <view class="select-item" v-for="(item, index) in deviceTypeList" :key="index" @click="toggleDeviceType(item)"
                :class="{ 'active': selectedDeviceTypes.includes(item.id) }">
                {{ item.typeName }}
              </view>
            </view>
          </view>

          <!-- 状态（单选） -->

          <view class="select-list">
            <view>事件状态</view>
            <view class="select-option">
              <view class="select-item" v-for="(item, index) in deviceStatusList" :key="index" @click="selectDeviceStatus(item)"
                :class="{ 'active': selectedDeviceStatus === item.value }">
                {{ item.label }}
              </view>
            </view>
          </view>

          <!-- 事件级别 -->
          <view class="select-list">
            <view>事件级别</view>
            <view class="select-option">
              <view class="select-item" v-for="(item, index) in siteTypeList" :key="index" @click="selectSiteType(item)"
                :class="{ 'active': selectedSiteType === item.value }">
                {{ item.label }}
              </view>
            </view>
          </view>

          <view class="select-list">
            <view>告警发生时间</view>
            <view class="select-option">
              <!-- <sm-range-time-range style="width: 100%;" @update:start="handleStart" @update:end="handleEnd"></sm-range-time-range> -->
              <timeRange style="width: 100%;" @update:startDate="handleStart" :timeSelectList="timeSelectList"  @update:endDate="handleEnd" :typeName="'alarm'" :resetList="resetList"></timeRange>
            </view>
          </view>

        </scroll-view>
        <view class="bottom-btn">
          <u-button text="重置" shape="circle" style="border:1rpx solid #999; background-color: #fff;" @click="reset()"></u-button>
          <u-button type="primary" text="确定" shape="circle" @click="submit()"></u-button>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script setup>
import timeRange from '@/components/time-range.vue'
import pieEcharts from './pieEcharts.vue'
// import smRangeTimeRange from '../../../components/uni-custom/sm-range-time-range.vue'
import { queryAlarmList, getAssetTypeList } from '../../../api/operations'
import { onLoad, onUnload, onShow, onPullDownRefresh, onHide, } from '@dcloudio/uni-app'
import empty from '../../../components/uni-custom/empty.vue'
import { ref, onMounted, watch, getCurrentInstance, onUnmounted } from 'vue'

const props = defineProps({
  isRefreshing: {
    type: Boolean,
    default: false
  }
})

// 基础搜索变量
const searchValue = ref('')
const selectedType = ref('')
const showDropdown = ref(false)
const resetList = ref(false)
const show = ref(false)
const typeList = ref([
  { name: '设备编号', value: '0' },
  { name: '设备名称', value: '1' },
])
const timeSelectList=ref([
  {
    label: '近7天',
    value: '2'
  },
  {
    label: '近1月',
    value: '3'
  },
  {
    label: '近3月',
    value: '4'
  },
])

// 分页相关变量
const page = ref(1)
const pageSize = ref(10)
const hasMore = ref(true)
const isLoading = ref(false)
const totalCount = ref(0)    // 新增：缓存总条数，避免重复读取

// 数据列表
const alarmList = ref([])
const deviceTypeList = ref([])
const deviceStatusList = ref([
  { label: '全部', value: '' },
  { label: '未修复', value: '0' },
  { label: '已修复', value: '1' }
])
const siteTypeList = ref([
  { label: '全部', value: '' },
  { label: '紧急', value: '3' },
  { label: '重要', value: '2' },
  { label: '次要', value: '1' },
  { label: '提示', value: '4' },
  { label: '离线', value: '5' }
])
// 筛选条件
const typeValue = ref("")
const selectedSite = ref([])
const selectedDeviceTypes = ref([]);
const selectedDeviceStatus = ref('');
const selectedSiteType = ref('');
const startTime = ref('');
const endTime = ref('');
const PAGE_ID = 'alarm'; // 直接固定，不用ref
const SELECT_SITE_EVENT = `selectSiteEvent_${PAGE_ID}`;
// 饼图数据
const statusTypeList = ref([]);
const totalSize = ref(0)
onLoad(() => {
  uni.$on(SELECT_SITE_EVENT, (value) => {
    selectedSite.value = value;
    page.value = 1;
    hasMore.value = true;
    totalCount.value = 0;
    getqueryAlarmList(selectedSite.value, true);
  });
});
// 多选：设备类型
const toggleDeviceType = (item) => {
  const index = selectedDeviceTypes.value.indexOf(item.id);
  if (index === -1) {
    selectedDeviceTypes.value.push(item.id);
  } else {
    selectedDeviceTypes.value.splice(index, 1);
  }
};

// 单选：状态
const selectDeviceStatus = (item) => {
  selectedDeviceStatus.value = item.value;
};

// 单选：站点类型
const selectSiteType = (item) => {
  selectedSiteType.value = item.value;
};

const showTypePicker = () => {
  showDropdown.value = !showDropdown.value
}

const selectType = (item) => {
  showDropdown.value = false
  selectedType.value = item
}

const handleStart = (val) => {
  startTime.value = val
};

const handleEnd = (val) => {
  endTime.value = val
};

const goSelectSite = () => {
  console.log('下一层级',JSON.stringify(selectedSite.value))
  uni.navigateTo({
    url: `/thirdPackage/pages/components/selectSite?selectedSite=${encodeURIComponent(JSON.stringify(selectedSite.value))}&source=${PAGE_ID}` // 改用常量PAGE_ID
  });
}

// 重置筛选
const reset = () => {
  selectedDeviceTypes.value = [];
  selectedDeviceStatus.value = '';
  selectedSiteType.value = '';
  show.value = false;
  resetList.value = true
  page.value = 1;
  hasMore.value = true;
  totalCount.value = 0;
  const today = new Date();
  const start = new Date(today);
  start.setDate(today.getDate() - 6);
  startTime.value = start.toISOString().split('T')[0]
  endTime.value = today.toISOString().split('T')[0]
  getqueryAlarmList(selectedSite.value, true);
};

// 提交筛选
const submit = () => {
  show.value = false;
  page.value = 1;
  hasMore.value = true;
  totalCount.value = 0;
  getqueryAlarmList(selectedSite.value, true);
};

// 监听刷新
watch(() => props.isRefreshing, (newVal) => {
  if (newVal) {
    const ids = uni.getStorageSync('SITE_LIST')
    page.value = 1;
    hasMore.value = true;
    totalCount.value = 0;
    getqueryAlarmList(ids, true);
  }
})

// 滚动到底部处理函数（增加防抖和状态控制）
const handleScrollToLower = async () => {

  // 防止重复加载（核心：同时判断加载状态和是否有更多数据）
  if (isLoading.value || !hasMore.value) {
    console.log('跳过加载：加载中或无更多数据');
    return;
  }

  // 防抖处理：避免快速滚动多次触发
  isLoading.value = true;
  try {
    page.value += 1;
    await getqueryAlarmList(selectedSite.value, false);
  } catch (error) {
    // 加载失败回退页码
    page.value -= 1;
    console.error('加载更多失败:', error);
  } finally {
    isLoading.value = false;
  }
};

// 可选：监听滚动事件，便于调试
const handleScroll = (e) => {
};

// 获取告警列表（优化分页判断）
const getqueryAlarmList = async (val, isReset = false) => {
  isLoading.value = true;

  try {
    let siteIds = val.filter(id => id);

    let obj = {
      typeIds: JSON.stringify(selectedDeviceTypes.value),
      startDate: startTime.value,
      endDate: endTime.value,
      eventLevel: selectedSiteType.value ? selectedSiteType.value : '',
      eventStatus: selectedDeviceStatus.value ? selectedDeviceStatus.value : '',
      deviceNumber: selectedType.value?.value === '0' ? selectedType.value?.value : "0",
      deviceName: typeValue.value,

      siteIds: JSON.stringify(siteIds),
      page: page.value,
      size: pageSize.value
    };

    const res = await queryAlarmList(obj);

    // 缓存总条数（兼容total和totalSize字段）
    totalCount.value = res.data.total || res.data.totalSize || 0;
    const newData = convertRawDataToAlarmList(res.data.alarmMap || {});

    // 处理数据合并
    if (isReset || page.value === 1) {
      alarmList.value = newData;
    } else {
      mergeAlarmData(alarmList.value, newData);
    }

    // 更新饼图数据
    statusTypeList.value = [
      { label: '紧急', value: res.data.emergency || 0, color: '#FA2E49' },
      { label: '重要', value: res.data.important || 0, color: '#EA721C' },
      { label: '次要', value: res.data.secondary || 0, color: '#FEBB0B' },
      { label: '提示', value: res.data.tip || 0, color: '#5890FD' },
      { label: '离线', value: res.data.offLine || 0, color: '#999999' },
      { label: "未知", value: res.data.unknown || 0, color: "#666666" }
    ];
    totalSize.value = totalCount.value;

    // 优化：更准确的分页判断
    const currentTotal = alarmList.value.reduce((sum, item) => sum + item.list.length, 0);
    // 双重判断：当前总数 < 总条数 且 当前页码*每页条数 < 总条数
    hasMore.value = currentTotal < totalCount.value && (page.value * pageSize.value) < totalCount.value;

  } catch (error) {
    console.error('获取告警列表失败:', error);
    if (!isReset && page.value > 1) {
      page.value -= 1;
    }
  } finally {
    isLoading.value = false;
  }
}

// 合并告警数据
const mergeAlarmData = (oldData, newData) => {
  const dateMap = {};
  oldData.forEach(item => {
    const key = `${item.year}-${item.dataDay}`;
    dateMap[key] = item;
  });

  newData.forEach(newItem => {
    const key = `${newItem.year}-${newItem.dataDay}`;
    if (dateMap[key]) {
      dateMap[key].list.push(...newItem.list);
    } else {
      oldData.push(newItem);
      dateMap[key] = newItem;
    }
  });

  // 保持按日期倒序
  oldData.sort((a, b) => {
    const dateA = `${a.year.replace('/', '-')}-${a.dataDay}`;
    const dateB = `${b.year.replace('/', '-')}-${b.dataDay}`;
    return new Date(dateB) - new Date(dateA);
  });
}

// 数据转换函数
function convertRawDataToAlarmList (rawData) {
  const result = [];
  if (!rawData || typeof rawData !== 'object') return result;

  for (const date in rawData) {
    const [year, month, day] = date.split('-');
    const formattedYear = `${year}/${month}`;
    const formattedDay = day;

    const items = rawData[date].map(item => ({ ...item }));

    result.push({
      year: formattedYear,
      dataDay: formattedDay,
      list: items
    });
  }

  result.sort((a, b) => {
    const dateA = `${a.year.replace('/', '-')}-${a.dataDay}`;
    const dateB = `${b.year.replace('/', '-')}-${b.dataDay}`;
    return new Date(dateB) - new Date(dateA);
  });

  return result;
}

// 搜索值变化
const change = (data) => {
  typeValue.value = data;
  page.value = 1;
  hasMore.value = true;
  totalCount.value = 0;
  getqueryAlarmList(selectedSite.value, true);
};

// 获取设备类型列表
const querygetAssetTypeList = () => {
  getAssetTypeList().then(res => {
    const targetIds = ["20", "23", "25", "28", "29", "30", "38", "39", "31", "32"];
    deviceTypeList.value = res.data.filter(item => targetIds.includes(item.id))
  })
}

onMounted(() => {
  selectedType.value = typeList.value[0];
  if (!startTime.value) {
    const today = new Date();
    const start = new Date(today);
    start.setDate(today.getDate() - 6);
    startTime.value = start.toISOString().split('T')[0]
    endTime.value = today.toISOString().split('T')[0]
  }
  const ids = uni.getStorageSync('SITE_LIST')
  selectedSite.value = ids;
  getqueryAlarmList(ids, true);
  querygetAssetTypeList();
})
</script>

<style scoped lang="scss">
.content {
  height: 100%;
  width: 100%;
}

.alarm-echart {
  height: 14%;
  width: 100%;
  margin-top: 15rpx;
  display: flex;
  z-index: 1;

  .alarm-echart-container {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .alarm-echart-title {
    display: flex;
    flex-wrap: wrap;
    margin-top: 18rpx;
    width: 70%;
  }

  .alarm-echart-title-item {
    width: 30%;
    box-sizing: border-box;
    font-size: 24rpx;
    color: #000000;
  }

  .alarm-echart-title-value {
    display: flex;
    color: #626262;
    font-size: 22rpx;
    align-items: center;
    margin-top: 10rpx;
  }

  .circle {
    width: 13rpx;
    height: 13rpx;
    border-radius: 50%;
    margin-right: 10rpx;
    margin-top: 8rpx;
  }
}

.alarm-search {
  height: 10%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .alarm-search-type {
    display: flex;
    align-items: center;
    position: relative;

    .alarm-search-type-text {
      margin-right: 14rpx;
      color: #000000;
      font-weight: 500;
      white-space: nowrap;
    }

    .alarm-search-type-list {
      position: absolute;
      background: rgba(255, 255, 255, 1);
      box-shadow: 0rpx 0rpx 17rpx 0rpx rgba(0, 0, 0, 0.25);
      border-radius: 30rpx;
      padding: 20rpx 30rpx;
      top: 74rpx;
      left: 5rpx;
      z-index: 1;
    }

    .alarm-search-type-list-item {
      white-space: nowrap;
      padding: 20rpx 30rpx;
    }
  }

  .image {
    width: 38rpx;
    height: 38rpx;
  }

  .alarm-search-more {
    width: 48rpx;
    height: 48rpx;
  }

  .alarm-search-input {
    width: 70%;
  }
}

// 修改：列表容器样式
.alarm-list-wrap {
  width: 100%;
  height: 76%;
  box-sizing: border-box;
}

.alarm-list {
  width: 100%;

  // 新增：加载更多样式
  .load-more {
    padding: 20rpx;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .no-more {
    padding: 20rpx;
    text-align: center;
    color: #999;
  }

  .alarm-item {
    display: flex;
    justify-content: space-between;
  }

  .alarm-item-dataTime {
    text-align: center;

    .alarm-item-dataTime-day {
      font-weight: 600;
      font-size: 32rpx;
      color: #000000;
    }

    .alarm-item-dataTime-time {
      font-weight: 400;
      font-size: 28rpx;
      color: #aeaeae;
      margin-top: 16rpx;
    }

    .line {
      margin: 16px auto;
      height: calc(100% - 148rpx);
      width: 5rpx;
    }
  }

  .alarm-item-content {
    width: 80%;
  }

  .alarm-bgc {
    background-color: #fff;
    margin-left: 12rpx;
    border: 2px solid #fff;
    box-sizing: border-box;

    .list-stuts {
      clip-path: polygon(0% 0%, 100% 0%, 100% 100%, 9% 100%);
      width: 40%;
      font-weight: 400;
      align-items: center;
      display: flex;
      justify-content: center;
      font-size: 28rpx;
      color: #ffffff;
    }
  }

  .alarm-item-content-list {
    border-radius: 12rpx;
    margin-top: 20rpx;

    .status-tip {
      display: flex;
      width: 46%;
      align-items: center;
      font-size: 24rpx;
      margin-top: 20rpx;
      color: #fff;
      clip-path: polygon(0% 0%, 91% 0%, 98% 100%, 0% 100%);
      justify-content: space-between;
    }

    .time {
      position: relative;
      display: flex;
      align-items: center;
      text-align: center;

      .creatime {
        position: absolute;
        width: 100%;
      }

      .image {
        height: 40rpx;
        width: 159rpx;
      }
    }
  }

  .alarm-item-content-list-status {
    display: flex;
    justify-content: space-between;
  }

  .status-tip-text {
    margin: 0 auto;
  }

  .siteName {
    font-family: PingFang SC, PingFang SC;
    font-weight: 600;
    font-size: 32rpx;
    color: #2c353e;
    padding: 15rpx;
  }

  .content-label {
    font-weight: 400;
    font-size: 24rpx;
    color: #969696;
    width: 26%;
  }

  .content-value {
    font-weight: 400;
    font-size: 24rpx;
    color: #000000;
    padding: 16rpx 18rpx;
    display: flex;
  }
}

.emptying {
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pl-15 {
  padding-left: 15rpx;
}

.select-tip {
  padding: 42rpx 42rpx 12rpx 42rpx;
  font-weight: 500;
  font-size: 32rpx;
  color: #000000;
}

.select-list {
  padding: 42rpx 42rpx 0 42rpx;
  font-weight: 400;
  font-size: 24rpx;
  color: #000000;

  .select-option {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
  }

  .select-item {
    width: 30%;
    box-sizing: border-box;
    text-align: center;
    padding: 20rpx 0;
    font-size: 28rpx;
    background: #f3f3f3;
    border-radius: 35rpx;
    margin-top: 40rpx;
  }

  .select-item.active {
    background-color: rgba(195, 220, 255, 1);
    color: rgba(24, 119, 255, 1);
  }
}

.bottom-btn {
  margin-top: 2%;
  display: flex;
  justify-content: space-around;
  gap: 26px;
  width: 100%;
  padding: 0 42rpx;
}
.select-popup {
  height: 80vh;
}
.active {
  color: rgba(24, 119, 255, 1);
}
</style>