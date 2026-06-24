<template>
  <view class="container">
    <view class="tabs">
      <view v-for="(tab, index) in filteredTabs" :key="index" :class="['tab-item', { active: currentIndex === tab.type }]"
        @click="changeTab(tab)">
        {{ tab.name }}
        <u-icon v-if="tab.type == '1' && tab.children.length > 1" :disabled="currentIndex !== '1'"
          @click.stop="changeMetering()" name="arrow-down-fill" :color="currentIndex == '1' ? '#388bff' : '#000000'"
          size="12"></u-icon>
      </view>
    </view>
    <view class="contents">
      <meteringPointOverview v-if="currentIndex == '1' && props.siteId" :siteId="props.siteId"
        :currentMetering="currentMetering"></meteringPointOverview>

      <pvOverview v-if="currentIndex == '2'" :siteId="props.siteId"></pvOverview>
      <energyStorageOverview v-if="currentIndex == '3'" :siteId="props.siteId"></energyStorageOverview>
      <chargingPileOverview v-if="currentIndex == '4'" :siteId="props.siteId"></chargingPileOverview>

    </view>

    <!-- 关口表选择弹窗 -->
    <uni-popup ref="meteringPopup" type="center" background-color="#fff" border-radius="20rpx">
      <view class="popup-wrapper">
        <view class="popup-title">请选择关口表</view>
        <view class="metering-list">
          <radio-group @change="selectMetering">
            <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index) in tabs[0]?.children" :key="item.id"
              style="display: flex;align-items: center; margin: 30rpx 0;">
              <view>
                <!-- 默认选中第一项 -->
                <radio :value="item.id" :checked="index === currentIndexMeter" />
              </view>
              <view style="margin-left: 60rpx;">{{ item?.deviceName }}</view>
            </label>
          </radio-group>
        </view>
        <view class="popup-close" @click="closePopup">取消</view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import meteringPointOverview from './statisticsComponent/meteringPointOverview.vue'
import pvOverview from './statisticsComponent/pvOverview.vue'
import energyStorageOverview from './statisticsComponent/energyStorageOverview.vue'
import chargingPileOverview from './statisticsComponent/chargingPileOverview.vue'
import { findSiteGwDeviceList } from '../../api/index'
const props = defineProps({
  siteId: {
    type: String,
    default: ''
  },
  scenarioTypes: {
    type: Array,
    default: () => []
  },

})
const tabs = ref([
  {
    name: '关口总览',
    type: '1',
    label: '0',
    children: [

    ]
  },
  { name: '光伏概览', type: '2', label: '1' },
  { name: '储能概览', type: '3', label: '2' },
  { name: '电桩概览', type: '4', label: '3' }
])
// 过滤后的 tabs（保留 "关口总览"，其他根据 scenarioTypes 显示）
// const filteredTabs = computed(() => {
//   return tabs.value.filter(tab => {
//     console.log(tab, 'tab.label');
//     // 强制保留 "关口总览"
//     if (tab.label === '0') return true;

//     // 其他 tab 根据 scenarioTypes 显示
//     return props.scenarioTypes.includes(tab.label);
//   });
// });
const filteredTabs = computed(() => {
  return tabs.value.filter(tab => {
    // 如果是 "关口总览"（label为0）
    if (tab.label === '0') {
      // 只有当 children 有数据时才显示
      return tabs.value[0].children.length > 0;
    }

    // 其他 tab 根据 scenarioTypes 显示
    return props.scenarioTypes.includes(tab.label);
  });
});
// 单选框默认选中索引（0 = 第一项）
const currentIndexMeter = ref(0)
const currentIndex = ref(null)
// 弹窗实例
const meteringPopup = ref(null)
// 当前选中的关口表
const currentMetering = ref({})
watch(() => props.siteId, (newVal) => {
  console.log(newVal, '站点ID****');
  if (newVal) {
    findSiteGwDeviceList({
      siteId: newVal
    }).then(res => {
      console.log(res, '站点关口表');
      if (res.message) {
        tabs.value[0].children = res.data
        if (tabs.value[0].children && tabs.value[0].children.length > 0) {
          currentMetering.value = tabs.value[0].children[0]
        }
      }
       // 确保 tabs 已更新后才设置默认选中
      if (filteredTabs.value.length > 0) {
        changeTab(filteredTabs.value[0]);
      }
    })
  }

}, { immediate: true })
// 切换顶部标签
const changeTab = (item) => {
  currentIndex.value = item.type
}

// 打开关口表弹窗
const changeMetering = () => {
  console.log('打开关口表选择弹窗')
  meteringPopup.value.open()
}

// 选择关口表（radio-group 正确写法）
const selectMetering = (e) => {
  // 获取选中的id
  const selectId = e.detail.value
  // 找到对应的数据
  const selectItem = tabs.value[0].children.find(item => item.id === selectId)

  currentMetering.value = selectItem
  // 更新选中索引
  currentIndexMeter.value = tabs.value[0].children.findIndex(item => item.id === selectId)

  console.log('选中了：', selectItem)
  // 选中后关闭弹窗
  meteringPopup.value.close()
}

// 关闭弹窗
const closePopup = () => {
  meteringPopup.value.close()
}

onMounted(() => {
  
  // currentMetering.value = tabs.value[0].children[0].name
})
</script>

<style lang="scss" scoped>
.container {
  width: 100%;
  height: 100%;


  .tabs {
    width: 100%;
    margin: 0 auto;
    padding: 0 30px;
    display: flex;
    gap: 56rpx;
    // justify-content: space-around;

    .tab-item {
      color: #666666;
      font-size: 28rpx;
      font-weight: 500;
      display: flex;
      align-items: center;
    }

    .active {
      color: #388bff;
      font-size: 28rpx;
      font-weight: 500;
      border-bottom: 4rpx solid #388bff;
    }
  }
}

// 弹窗样式
.popup-wrapper {
  width: 500rpx;
  padding: 30rpx;
  border-radius: 16rpx;

  .popup-title {
    text-align: center;
    font-size: 32rpx;
    font-weight: bold;
    margin-bottom: 30rpx;
  }

  .metering-list {
    margin-bottom: 40rpx;
  }

  .popup-close {
    text-align: center;
    color: #388bff;
    font-size: 28rpx;
    padding: 20rpx;
  }
}

.contents {
  width: 100%;
  padding: 25rpx 30rpx;
  height: 97%;
}
</style>