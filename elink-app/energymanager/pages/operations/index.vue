<template>
  <view class="content">
    <!-- 背景图片 -->
    <image class="content-bgc" src="/static/image/bgc.png"></image>
    <view class="container">
      <!-- 标签切换 -->
      <view class="tabs">
        <view v-for="(tab, index) in tabs" :key="index" :class="['tab-item', { active: currentIndex === index }]" @click="changeTab(index)">
          {{ tab.name }}
        </view>
      </view>

      <view class="messageContainers" v-if="currentIndex === 0">
        <alarmPage :isRefreshing="isRefreshing" />
      </view>
      <view class="messageContainers" v-if="currentIndex === 1">
        <!-- <u-tabs :list="list" @click="clickActive" class="center-tabs"></u-tabs> -->
        <view class="tabs-list">
          <view class="tabs-item" :class="{ 'active': item.name === taskActive }" v-for="item in list" :key="item.name" @click="currentTabs(item)">
            {{ item.name }}</view>
        </view>
        <!-- 巡检任务 -->
        <view class="InspectionTask" v-if="taskActive === '巡检任务'">
          <u--input placeholder="请输入任务名称" prefixIcon="search" prefixIconStyle="font-size: 22px;color: #909399"
            style="background-color: #ffffff;border-radius: 100rpx;" v-model="taskName"></u--input>
          <view class="task-list" v-if="TaskList?.length > 0">
            <view class="task-item" v-for="item in TaskList" :key="item.id" @click="goDetails(item)">
              <view class="task-item-title">
                <view class="task-item-title-content">{{ item.taskName }}</view>
                <view class="task-item-title-stuts">{{ $filters.inspectHandStatus(item.taskStatus) }}</view>

              </view>
              <view class="task-item-content other">
                <view class="task-item-content-title">任务描述</view>
                <view class="task-item-content-desc">{{ $filters.moreData(item.taskDesc) }}</view>
              </view>
              <view class="inspection-line"></view>
              <view class="task-item-content">
                <view class="task-item-content-title">任务时间</view>
                <view class="task-item-content-desc">{{ $filters.moreData(item.updateTime) }}</view>
              </view>

            </view>
          </view>
          <view class="tasking" v-else>
            <empty></empty>
          </view>

        </view>
        <!-- 消缺任务 -->
        <view class="tasking" v-if="taskActive === '消缺任务'">

          <empty></empty>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, watch } from 'vue'
import empty from '../../components/uni-custom/empty.vue'
import { onLoad, onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { findAppInspectHandTaskList } from '@/api/operations.js'
import alarmPage from './alarm/index.vue'

// 响应式数据
const currentIndex = ref(0)
const taskActive = ref('巡检任务')
const tabs = ref([
  { name: '告警' },
  { name: '任务' }
])
const list = ref([
  {
    name: '巡检任务',
  },
  {
    name: '消缺任务',
  }
])
const TaskList = ref([])
const originalTaskList = ref([])

const taskName = ref('')
const isRefreshing = ref(false)
// 切换主标签方法
const changeTab = (index) => {
  currentIndex.value = index
}
const currentTabs = (val) => {
  taskActive.value = val.name
}
onPullDownRefresh(async () => {
  try {
    // 设置刷新状态
    isRefreshing.value = true
    await getFindAppTaskList()
  } catch (error) {
    uni.showToast({
      title: '刷新失败',
      icon: 'error',
      duration: 1500
    })
  } finally {
    // 停止下拉刷新动画
    uni.stopPullDownRefresh()
    isRefreshing.value = false
  }
})

// 切换子标签方法
const clickActive = (item) => {
  taskActive.value = item.index
}
const goDetails = (item) => {
  uni.setStorageSync('listItem', item);

  uni.navigateTo({
    url: `/firstPackage/pages/operations/operationDetails/operationDetails?data=${encodeURIComponent(JSON.stringify(item))}`
  });
}
const getFindAppTaskList = () => {
  const userId = uni.getStorageSync('USER_ID')
  findAppInspectHandTaskList({ userId: userId }).then(res => {
    originalTaskList.value = res.data
    TaskList.value = res.data
  })

}
watch(() => taskName.value, (newValue) => {
  TaskList.value = originalTaskList.value.filter(item => item.taskName.includes(newValue))
})

// 生命周期钩子
onLoad(() => {
  // 这里可以放置页面加载时的逻辑
  getFindAppTaskList()
})
onShow(() => {
console.log(uni.getStorageSync('operationsCurrentIndex'))
  if (uni.getStorageSync('operationsCurrentIndex')) {
    currentIndex.value = uni.getStorageSync('operationsCurrentIndex')

    uni.removeStorageSync('operationsCurrentIndex')
  } else {
    currentIndex.value = 0
  }
})
</script>

<style scoped lang="scss">
.content {
  position: relative;
  width: 100%;
  height: 100%;
}

.content-bgc {
  position: absolute;
  width: 100%;
  height: 100%;
}

.container {
  width: 90%;
  margin: 0 auto;
  // background-color: aqua;
}

.tabs {
  padding-top: 16%;
  width: 100%;
  margin: 0 auto;
  display: flex;

  .tab-item {
    margin-right: 30rpx;
    color: #666666;
  }

  .active {
    color: #000000;
    font-size: 38rpx;
    font-weight: bold;
    transform: translateY(-12rpx);
  }
}

.messageContainers {
  height: 89%;
  overflow: hidden;

  .center-tabs {
    width: 55%;
    margin: 0 auto;
  }

  .InspectionTask {
    margin-top: 18rpx;
    height: 92%;

    .task-tip {
      font-family: PingFang SC, PingFang SC;
      font-weight: 400;
      font-size: 24rpx;
      color: #aeaeae;
      line-height: 30rpx;
      text-align: center;
      padding: 30rpx 0rpx;
    }
  }

  .task-list {
    height: 92%;
    width: 100%;
    overflow: auto;
  }

  .task-item {
    margin-top: 30rpx;
    width: 100%;
    background-color: #ffffff;
    box-shadow: 0rpx 0rpx 2rpx 0rpx rgba(0, 0, 0, 0.05);
    border-radius: 30rpx 30rpx 30rpx 30rpx;
    padding: 30rpx;
    box-sizing: border-box;

    .task-item-title {
      display: flex;
      justify-content: space-between;

      .task-item-title-content {
        height: 30rpx;
        font-weight: 600;
        font-size: 32rpx;
        width: 80%;

        color: #2c353e;
        line-height: 30rpx;
        text-align: left;

        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .task-item-title-stuts {
        font-weight: 400;
        font-size: 18rpx;
        color: #2982ff;
        background: rgba(41, 130, 255, 0.2);
        border-radius: 8rpx 8rpx 8rpx 8rpx;
        padding: 2rpx 13rpx;
      }
    }

    .other {
      margin-top: 38rpx !important;
    }

    .inspection-line {
      width: 100%;
      border-top: 0.5rpx solid rgba(203, 203, 203, 0.6);
      margin: 16rpx 0rpx;
    }

    .task-item-content {
      display: flex;
      justify-content: space-between;

      .task-item-content-title {
        font-weight: 400;
        font-size: 22rpx;
        color: #969696;
        line-height: 30rpx;
      }

      .task-item-content-desc {
        width: 82%;
        font-weight: 600;
        font-size: 22rpx;
        color: #000000;
        line-height: 30rpx;
        text-align: left;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}

.tasking {
  width: 100%;
  height: 80%;
}
.tabs-list {
  display: flex;
  margin: 35rpx auto;
  font-weight: 500;
  font-size: 28rpx;
  color: #666666;
  width: 56%;
  justify-content: space-between;

  .active {
    color: #388bff;
    border-bottom: 4rpx solid #388bff;
  }
}
</style>