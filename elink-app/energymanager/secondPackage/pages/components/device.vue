<template>
  <view class="containers">
    <view class="deviceList">
      <view class="deviceList-item" v-for="item in deviceList" :key="item.type"
        :class="{ active: selectStatus === item.type }" @click="changeStatus(item.type)">
        <view class="deviceList-num">{{ item.value }}</view>
        <view>{{ item.name }}</view>
      </view>
    </view>
    <view class="deviceType-list" v-if="deviceTypeList?.length > 0">
      <view class="deviceItem-list" v-for="item in deviceTypeList" :key="item.siteId">
        <view class="item-tip-title">{{ item.siteName }}</view>
        <view class="item-content">
          <view class="item-boeder" v-for="i in item.children" :key="i.deviceId">
            <view class="item-boeder-top">
              <view class="item-left">
                <image :src="getValidImageUrl(i.logoPath)" mode="aspectFit" @error="handleImageError($event, i)"
                  @load="handleImageLoad" lazy-load />
                <view class="item-text" :class="'statusText_' + i.txStatus">
                  <view class="circle" :class="'status_' + i.txStatus">
                    <view class="circle-fill" :class="'circle-fill_' + i.txStatus"></view>
                  </view>
                  <view :class="'statusText_' + i.txStatus">{{
                    i.txStatus == 0 ? '未注册' : i.txStatus == 1 ? '在线' : i.txStatus == 2 || i.txStatus == 3 ? '故障'
                      : i.txStatus == 88 ? "离线" : "--" }}</view>

                </view>
              </view>
              <view class="item-right">
                <view class="item-text">{{ i.deviceName }}</view>
                <view class="item-content-text">
                  <view class="item-content-label">设备类型：</view>
                  <view class="item-content-text-type">{{ i.typeName ? i.typeName : '-/-' }}</view>

                </view>
                <view class="item-content-text">
                  <view class="item-content-label">额定功率：</view>
                  <view class="item-content-text-type" v-if="i.ratedPower">{{ i.ratedPower }}kW</view>

                  <view class="item-content-text-type" v-else>-/-</view>
                </view>
                <view class="item-content-text">
                  <view class="item-content-label">设备型号：</view>
                  <view class="item-content-text-type">{{ i.equipmentModel ? i.equipmentModel : '-/-' }}</view>

                </view>
                <view class="item-content-text">
                  <view class="item-content-label">序列号：</view>
                  <view class="item-content-text-type">{{ i.deviceNumber ? i.deviceNumber : '-/-' }}</view>

                </view>

              </view>
            </view>
            <view class="item-another">
              <view class="another" v-for="(x, y) in i.alarmList" :key="y">
                <view class="another-text">{{ x.alarmName }}</view>
                <view class="another-time">{{ x.alarmTime }}</view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
    <view class="deviceType-list" v-else>
      <empty></empty>
    </view>
  </view>
</template>

<script setup>

import { queryDeviceList } from '../../../api/monitor';
import { ref, onMounted, watch, defineExpose } from 'vue';
import empty from '../../components/empty.vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
const props = defineProps({
  siteId: {
    type: String,
    default: ''
  },

})

const deviceList = ref([
  {
    name: '全部',
    type: '',
    value: '82'

  },
  {
    name: '离线',
    type: '88',
    value: '9'
  },
  {
    name: '故障',
    type: '2',
    value: '1'
  },
  {
    name: '未注册',
    type: '0',
    value: '2'
  }, {
    name: '在线',
    type: '1',
    value: '70'
  },
])
const selectStatus = ref('')
const deviceTypeList = ref([])
const sites = ref([])

// 校验并返回有效的图片路径
const getValidImageUrl = (logoPath) => {
  // 1. 校验路径是否有效（非空、是合法URL/本地路径）
  if (!logoPath || logoPath.trim() === '') {
    return '/static/empty2.png'; // 返回本地兜底图

  }

  // 2. 校验网络图片是否是合法URL（避免无效地址）
  const isHttpUrl = /^https?:\/\/.+/.test(logoPath);
  if (isHttpUrl) {
    return logoPath; // 合法网络URL直接返回
  }

  // 3. 非合法URL（如相对路径、空路径等）返回兜底图
  return '/static/empty2.png';
};
// 图片加载失败时的兜底处理
const handleImageError = (e, item) => {
  // 打印错误信息（方便调试）
  console.warn('图片加载失败，已切换兜底图:', e.detail.path);
  // 直接修改图片src为兜底图（终极兜底）
  e.target.src = '/static/empty2.png';

  // 可选：将当前item的logoPath标记为无效，避免下次重复加载
  if (item) {
    item.logoPath = '';
  }
};

const getqueryDeviceList = async () => {
  let type = selectStatus.value === '2' ? ['2', '3'] : [selectStatus.value];
  let siteIds = [sites.value]
  let obj = {
    siteIds: JSON.stringify(siteIds),
    txStatus: type == '' ? type : JSON.stringify(type)
  };
  const res = await queryDeviceList(obj)
  if (res.message) {
    const data = res.data
    // 更新 deviceList.value
    if (!selectStatus.value) {
      deviceList.value = deviceList.value.map(item => {
        switch (item.type) {
          case '': // 全部
            item.value = String(data.total)
            break
          case '88': // 离线
            item.value = String(data.offline)
            break
          case '2': // 故障
            item.value = String(data.fault)
            break
          case '0': // 未注册
            item.value = String(data.unregister)
            break
          case '1': // 在线
            item.value = String(data.online)
            break
        }
        return item
      })
    }

    deviceTypeList.value = Object.entries(res.data.siteDeviceMap).map(([siteName, devices]) => ({
      siteName,
      children: devices.map(device => ({
        ...device,
        // 如果需要，可以添加更多字段如 id、type 等
      }))
    }));
  }
}

const changeStatus = (val) => {
  selectStatus.value = val
  console.log(selectStatus.value, 'selectStatus.value')
  getqueryDeviceList()
}

onMounted(() => {

})
watch(() => props.siteId, (newVal, oldVal) => {
  if (newVal) {
    sites.value = newVal
    getqueryDeviceList()
  }
  console.log('newVal', newVal)
}, { immediate: true })
// 暴露方法给父组件
defineExpose({
  // refreshData,
  getqueryDeviceList
})

</script>

<style lang="scss" scoped>
.containers {
  width: 100%;
  height: 100%;
  padding: 0 17rpx;
}

.deviceList {
  display: flex;
  gap: 44rpx;
  align-content: center;
  padding: 0 23rpx;

  .deviceList-item {
    width: 18%;
    padding: 12rpx 0;
    background-color: rgba(255, 255, 255, 0.4);
    border-radius: 12rpx 12rpx 12rpx 12rpx;
    text-align: center;
    font-weight: 400;
    font-size: 24rpx;
    color: #000000;

    .deviceList-num {
      font-weight: bold;
      font-size: 32rpx;
      margin-top: 9rpx;
    }
  }

  .active {
    background: #388bff;
    color: #ffffff;
  }
}

.deviceType-list {
  margin-top: 23rpx;
  height: 90%;
  overflow: auto;
  font-weight: 400;
  font-size: 32rpx;
  color: #333333;

  .item-tip-title {
    margin-top: 23rpx;
    font-weight: 400;
    font-size: 32rpx;
    color: #333333;
  }

  .item-content {
    margin-top: 23rpx;
  }

  .item-boeder {
    margin-top: 23rpx;
    padding: 23rpx;
    gap: 23rpx;
    background: linear-gradient(360deg, #ffffff 0%, #e6f3ff 100%);
    border-radius: 30rpx 30rpx 30rpx 30rpx;
    border: 1rpx solid #ffffff;

    .item-boeder-top {
      display: flex;
    }

    .item-left {
      width: 35%;
      text-align: center;

      image {
        width: 86%;
        height: 249rpx;
        margin: 0 auto;
      }

      .item-text {
        display: flex;
        align-items: center;
        font-weight: 400;
        font-size: 28rpx;
        text-align: center;
        gap: 10rpx;
        justify-content: center;
        margin-top: 23rpx;
        justify-content: center;

        .circle {
          padding: 6rpx;
          border-radius: 50%;
          background-color: #fff;

          .circle-fill {
            width: 17rpx;
            height: 17rpx;
            border-radius: 50%;
          }
        }

        .statusText_1 {
          color: #46c700;
        }

        .statusText_2 {
          color: #ff0022;
        }

        .statusText_3 {
          color: #aeaeae;
        }

        .statusText_4 {
          color: #6a34fc;
        }

        // 在线
        .status_1 {
          border: 1rpx solid #46c700;
        }

        .circle-fill_1 {
          background-color: #46c700;
        }

        // 故障
        .status_2 {
          border: 1rpx solid #ff0022;
        }

        .circle-fill_2 {
          background-color: #ff0022;
        }

        .status_3 {
          border: 1rpx solid #ff0022;
        }

        .circle-fill_3 {
          background-color: #ff0022;
        }

        // 离线
        .status_88 {
          border: 1rpx solid #aeaeae;
        }

        .circle-fill_88 {
          background-color: #aeaeae;
        }

        // 未注册
        .status_0 {
          border: 1rpx solid #6a34fc;
        }

        .circle-fill_0 {
          background-color: #6a34fc;
        }
      }
    }

    .item-right {
      width: 65%;

      .item-text {
        font-weight: 600;
        font-size: 28rpx;
        color: #333333;
        padding-bottom: 23rpx;
        border-bottom: 1rpx solid rgba(0, 0, 0, 0.15);
      }

      .item-content-text {
        padding-top: 23rpx;
        font-weight: 400;
        font-size: 24rpx;
        color: #666666;
        display: flex;

        .item-content-label {
          width: 32%;
        }

        .item-content-text-type {
          width: 60%;
          white-space: nowrap;
          font-weight: 400;
          font-size: 24rpx;
          color: #333333;
        }
      }
    }
  }

  .item-another {
    width: 100%;
    margin-top: 23rpx;

    .another {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 15rpx 30rpx;
      background: #f9f9f9;
      border-radius: 8rpx;

      .another-text {
        font-size: 24rpx;
        color: #999999;
        line-height: 30rpx;
      }

      .another-time {
        font-weight: 400;
        font-size: 24rpx;
        color: #333333;
      }
    }
  }
}
</style>