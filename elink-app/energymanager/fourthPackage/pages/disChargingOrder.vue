<template>
  <view class="containers">
    <view class="card-title">
      <view class="title-card">
        <view class="title-card-item" v-for="(item, index) in changeList" :key="index">
          <view class="title-card-item-title">{{ item.value }}</view>
          <view class="title-card-item-content">{{ item.label }}</view>
          <view class="title-card-item-content">({{ item.unit }})</view>
        </view>
      </view>
      <view style="padding: 0 15rpx;">
        <select-list :isHowSelect="isHowSelect" :isIconImage="isIconImage" :placeholder="placeholder"
          :typeList="typeList" @change="change" @selectTypeValue="selectTypeValue" @goSelectSite="goSelectSite"
          :isMore="false"></select-list>
      </view>
      <view class="filter-card">
        <selectIf :type="'disChargingOrder'" @handleTimeRange="handleTimeRange" @handleOrderStatus="handleOrderStatus"
          @handleAbnormalType="handleAbnormalType" @handleFilter="handleFilter"></selectIf>
      </view>

    </view>
    <scroll-view class="alarm-list-wrap" scroll-y @scrolltolower="handleScrollToLower" lower-threshold="100"
      scroll-with-animation :style="{ height: '68vh' }" @scroll="handleScroll">
      <view class="content-list">
        <view class="content-list-item" v-for="(item, index) in siteListCard" :key="index">
          <view class="site-title">
            <view>{{ item.orderNum }}
              <text class="iconfont-site iconfont icon-wendang-moren"></text>
            </view>
            <view :class="['site-status', 'bgc-' + item.orderStatus]">
              {{ $filters.disChargingState(item.orderStatus) }}
            </view>
          </view>
          <view class="site-title site-title-top">
            <view class="site-title-label">所属站点:</view>
            <view class="site-title-value">{{ item.siteName }}</view>
          </view>
          <view class="site-title ">
            <view class="site-title-label">设备编号:</view>
            <view class="site-title-value"> <text class="iconfont-site iconfont icon-wendang-moren"></text> {{
              item.pileCode }}</view>
          </view>
          <view class="line"></view>
          <view class="charge-power-money">
            <view class="charge-power-money-item">
              <view class="charge-power-num">{{ $filters.numberUnit(item.totalQt) }}</view>
              <view class="charge-power-money-item-label">放电电量（kW ）</view>
            </view>
            <view class="charge-power-money-item">
              <view class="charge-power-num">￥{{ $filters.numberUnit(item.totalCost) }}</view>
              <view class="charge-power-money-item-label">订单金额（万元）</view>
            </view>
            <view class="charge-power-money-item">
              <view class="charge-power-num">￥{{ $filters.numberUnit(item.actualTotalCost) }}</view>
              <view class="charge-power-money-item-label">实付金额（万元）</view>
            </view>
          </view>
          <view class="line"></view>
          <view class="site-title site-title-item">
            <view class="site-title-label">开始时间:</view>
            <view class="site-title-value">{{ item.startTime || '-/-' }}</view>
          </view>
          <view class="site-title site-title-item">
            <view class="site-title-label">结束时间:</view>
            <view class="site-title-value">{{ item.endTime || '-/-' }}</view>
          </view>
          <view class="site-title site-title-item">
            <view class="site-title-label">充电时长:</view>
            <view class="site-title-value">{{ item.duration || '-/-' }}</view>
          </view>
          <view class="site-title site-title-item">
            <view class="site-title-label">结束原因:</view>
            <view class="site-title-value">{{ item.stopDetailReason || '-/-' }}</view>
          </view>
          <view class="card-button">
            <view class="button-item btn">
              <view class="button-text" @click="goOrderDetail(item)">详情</view>
            </view>
            <view class="button-item btn2" v-if="item.orderStatus == 1" @click="stopCharge(item)">
              <view class="button-text">结束充电</view>
            </view>
            <!-- <view class="button-item btn1" v-else @click="applyRefund(item)">
            <view class="button-text">补单/退款</view>
          </view> -->
            <view class="button-item btn1" @click="applyRefund(item)"
              :class="{ disabled: item.orderStatus == 1 || item.orderStatus == 6 }">
              <view class="button-text">补单/退款</view>
            </view>

          </view>
        </view>
        <!-- 加载中提示 -->
        <view class="load-more" v-if="isLoading">
          <u-loading-icon mode="flower" size="20"></u-loading-icon>
          <text style="font-size: 24rpx; margin-left: 10rpx;">加载中...</text>
        </view>
        <!-- 没有更多数据提示 -->
        <view class="no-more" v-if="!hasMore && siteListCard.length > 0">
          <text style="font-size: 24rpx; color: #999;">没有更多数据了</text>
        </view>
      </view>
    </scroll-view>
    <uni-popup ref="alertDialog" background-color="#fff" :close-on-click-overlay="false" class="custom-popup"
      borderRadius="20rpx">
      <view style="border-top-left-radius: 30rpx;border-top-right-radius: 30rpx;" class="applyRefund-card">
        <view class="card-title">订单退款</view>
        <view class="apply-money">
          <view class="apply-money-label">订单编号:</view>
          <view class="apply-money-value">{{ refoundItem ? refoundItem.orderNum : '' }}</view>
        </view>
        <view class="apply-money">
          <view class="apply-money-label">实付金额:</view>
          <view class="apply-money-value" style="color: #007aff;">￥{{ refoundItem ?
            $filters.numberUnit(refoundItem.actualTotalCost) : '' }}
          </view>
        </view>
        <u--form labelPosition="left" :model="applyRefundModel" :rules="rules" ref="uForm">
          <u-form-item label="退款金额" prop=" refundMoney" border-bottom label-position="top" label-width="100%"
            :borderBottom="false">
            <view
              style="display: flex; align-items: center;justify-content: space-between;width: 100%;margin-top: 20rpx;">
              ￥
              <u--input v-model="applyRefundModel.refundMoney" border="surround" placeholder="请输入退款金额"
                style="background-color:#F3F3F5;border-radius: 100rpx;" type="number">

              </u--input>
            </view>
          </u-form-item>
          <u-form-item label="退款原因（选填）" prop="refundReason" border-bottom label-position="top" label-width="100%"
            :borderBottom="false">
            <view
              style="display: flex; align-items: center;justify-content: space-between;width: 100%;margin-top: 20rpx;">
              <u--textarea v-model="applyRefundModel.refundReason" border="surround" placeholder="请输入退款原因"
                style="background-color:#F3F3F5;border-radius:20rpx;">
              </u--textarea>
            </view>
          </u-form-item>
        </u--form>
        <view style="display: flex;align-items: center; justify-content: space-between;margin-top: 20rpx;">
          <u-button @click="cancel" style="border-radius: 48rpx; width: 40%; color: rgba(0, 122, 255, 1);">取消</u-button>
          <u-button @click="submitRefund"
            style="background-color: rgba(0, 122, 255, 1);color: #fff;border-radius: 48rpx; width: 40%; ">确认退款</u-button>
        </view>


      </view>
    </uni-popup>
  </view>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import selectList from './components/select-list.vue';
import { onLoad, onShow, onPullDownRefresh, onReachBottom, onHide } from '@dcloudio/uni-app'
import selectIf from './components/selectIf.vue';
import { findOrderRecordListByPage, updateOrderStatus, orderRefund } from '../api/chargingOrder'

const startAlsoEndDate = ref('')
const startAlsoStartDate = ref('')

const isHowSelect = ref(true)
const isIconImage = ref(true)
const placeholder = ref('请输入')
const keyword = ref('')
const keywordType = ref('')
const sites = ref([])
const orderStatus = ref('')
const abnormalType = ref('')
const populObj = ref({})
const isLoading = ref(false)
const pageId = ref('dischargingOrder')
const applyRefundModel = ref({
  refundMoney: '',
  refundReason: ''
})
const rules = ref({
  refundMoney: [
    {
      required: true,
      message: '请输入退款金额',
      trigger: 'blur'
    }
  ]
})
const uForm = ref(null)

const typeList = ref([{
  name: '订单号',
  value: "1",
},
{
  name: '手机号',
  value: "2"
},
{
  name: 'VIN',
  value: "3"
}, {
  name: '电卡ID',
  value: "3"
}, {
  name: '桩编码',
  value: "4"
}
])
const changeList = ref([
  {
    label: "总订单笔数",
    value: '5',
    unit: "笔"
  }, {
    label: "总充电量",
    value: '5',
    unit: "万KWh"
  }, {
    label: "订单金额",
    value: '5',
    unit: "万元"
  }, {
    label: "实付金额",
    value: '5',
    unit: "万元"
  }, {
    label: "实付电费",
    value: '5',
    unit: "万元"
  }, {
    label: "实付服务费",
    value: '5',
    unit: "万元"
  }
])
const alertDialog = ref(null)
const siteListCard = ref([])
const refoundItem = ref(null)
// 分页核心（关键）
const page = ref(1)
const pageSize = ref(10)
const loading = ref(false)   // 防止重复请求
const hasMore = ref(true)    // 是否还有更多数据

const change = (val) => {
  keyword.value = val
  // 调用事件
}
const selectTypeValue = (val) => {
  keywordType.value = val.value,
    placeholder.value = '请输入' + val.name

}
const goOrderDetail = (item) => {
  uni.navigateTo({
    url: `/fourthPackage/pages/disChargingOrderDetail?orderNum=${item.orderNum}`
  });
}

const goSelectSite = () => {
  uni.navigateTo({
    url: `/thirdPackage/pages/components/selectSite?selectedSite=${encodeURIComponent(JSON.stringify(sites.value))}&source=${pageId.value}`
  });
}
// 停止充电--出现弹窗
const stopCharge = () => {
  // 
  uni.showModal({
    content: '确认停止充电？',
    confirmText: '确认停止',
    confirmColor: 'rgba(219, 86, 59, 1)', // 确认按钮颜色
    cancelColor: 'rgba(41, 130, 255, 1)',   // 取消按钮颜色
    success: function (res) {
      if (res.confirm) {
        console.log('用户点击确定');
        // 执行停止充电逻辑
      } else if (res.cancel) {
        console.log('用户点击取消');
      }
    }
  })
}
const getfindOrderRecordListByPage = async (isReset = false) => {
  const userInfo = uni.getStorageSync('tenantId');
  if (!userInfo) {
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    });
    uni.reLaunch({ url: '/thirdPackage/login/login' });
    return;
  }
  if (loading.value || !hasMore.value) return
  loading.value = true

  try {
    // const userInfo = uni.getStorageSync('USER_INFO')
    // ✅ 强制保证是数组，小程序不会转字符串
    const orderTypesArray = [1, 2]

    const res = await findOrderRecordListByPage({
      page: page.value,
      size: pageSize.value,
       userTenantId: userInfo,
      orderTypes: orderTypesArray, // 一定是数组
      keyword: keyword.value,
      keywordType: keywordType.value,
      siteId: sites.value.join(','),
      startAlsoStartDate: startAlsoStartDate.value,
      startAlsoEndDate: startAlsoEndDate.value,
      orderStatus: orderStatus.value,
      abnormalType: abnormalType.value,
      ...populObj.value

    })

    const list = res.data?.orderRecordDataPage?.items || []

    const total = res.data?.orderRecordDataPage?.totalSize || 0
    changeList.value = [{ label: "总订单笔数", value: total, unit: "笔" },

    { label: "总充电量", value: res.data?.sumQt || '0', unit: "万KWh" },

    { label: "订单金额", value: res.data?.sumCost || '0', unit: "万元" },

    { label: "实付金额", value: res.data?.actualTotalCost || '0', unit: "万元" },
    { label: "实付电费", value: res.data?.actualTotalElect || '0', unit: "万元" },
    { label: "实付服务费", value: res.data?.actualTotalFee || '0', unit: "万元" }]

    if (isReset || page.value === 1) {
      siteListCard.value = list
    } else {
      siteListCard.value = [...siteListCard.value, ...list]
    }

    hasMore.value = siteListCard.value.length < total

  } catch (err) {
    console.error('请求失败', err)
  } finally {
    loading.value = false
    uni.stopPullDownRefresh()
  }
}
// 申请退款
const applyRefund = (item) => {
  // 完全沿用你 Vue 里的提示语
  let confirmTitle = item.prepayMoney <= 0
    ? `该订单为免支付订单，确定结算该订单吗？`
    : `该订单已挂起，确定结算该订单吗？`

  uni.showModal({
    content: confirmTitle,
    confirmText: '确定',
    cancelText: '取消',
    confirmColor: '#DB563B',
    cancelColor: '#2982FF',
    success: async (res) => {
      if (!res.confirm) return

      try {
        uni.showLoading({ title: '正在操作...', mask: true })

        // ==============================================
        // ✅ 关键：和 Vue 一样调用同一个接口
        // 不新增任何接口！
        // ==============================================
        const resData = await updateOrderStatus({
          orderId: item.id,
        })

        uni.hideLoading()

        if (resData.success) {
          uni.showToast({
            title: '操作成功！',
            icon: 'success'
          })
          // 刷新列表
          refreshList()
        } else {
          uni.showToast({
            title: resData.msg || '操作失败',
            icon: 'none'
          })
        }
      } catch (err) {
        uni.hideLoading()
        uni.showToast({
          title: err.message,
          icon: 'none'
        })
      }
    }
  })
}
const handleScrollToLower = async () => {
  // 防止重复加载（核心：同时判断加载状态和是否有更多数据）
  if (isLoading.value || !hasMore.value) {
    return;
  }

  // 防抖处理：避免快速滚动多次触发
  isLoading.value = true;
  try {
    page.value += 1;
    await getfindOrderRecordListByPage(false);
  } catch (error) {
    // 加载失败回退页码
    page.value -= 1;
  } finally {
    isLoading.value = false;
  }

}
const handleOrderStatus = (val) => {
  orderStatus.value = val || ''

  refreshList()
}
const handleFilter = (val) => {

  populObj.value = val
  refreshList()

}
const handleAbnormalType = (val) => {
  abnormalType.value = val || ''

  refreshList()


}
const handleTimeRange = (val) => {
  startAlsoStartDate.value = val[0]
  startAlsoEndDate.value = val[1]
  refreshList()
}
// 取消申请退款
const cancel = () => {
  alertDialog.value.close()
  applyRefundModel.value.refundMoney = ''
  applyRefundModel.value.refundReason = ''

}
// 刷新重置
const refreshList = () => {
  page.value = 1
  hasMore.value = true
  getfindOrderRecordListByPage(false)
}

// 确认退款
const submitRefund = () => {
  uForm.value.validate().then(() => {
    // 这里写退款接口
    orderRefund({
      ...applyRefundModel.value,
      orderId: refoundItem.value.id,
      tradeOrderType: '1',
    }).then(resData => {
      if (resData.success) {
        uni.showToast({
          title: '操作成功！',
          icon: 'success'
        })
      }
    })
    alertDialog.value.close()
  })
}
onLoad(() => {
  uni.$on(`selectSiteEvent_${pageId.value}`, (value) => {
    sites.value = value
    page.value = 1;
    hasMore.value = true;
    getfindOrderRecordListByPage(true)
  });

})
onShow(() => {
  placeholder.value = '请输入' + typeList.value[0].name
  // 监听事件
  // uni.$on(`selectSiteEvent_${pageId.value}`, (value) => {
  //   sites.value = value;
  // });
})
onMounted(() => {
  getfindOrderRecordListByPage(true)
})
// 下拉刷新
onPullDownRefresh(() => {
  refreshList()
})
// 上拉加载更多（滚动到底部自动触发）
onReachBottom(() => {
  if (hasMore.value && !loading.value) {
    page.value++
    getfindOrderRecordListByPage()
  }
})

</script>

<style lang="scss" scoped>
.containers {
  width: 100%;
  height: 100%;
  overflow: hidden;
  background-color: #F5F5F5;

  .card-title {
    background: #fff;
    width: 100%;

    .title-card {
      display: flex;
      overflow: auto;
      overflow-x: auto;
      white-space: nowrap;
      margin-top: 14rpx;
      margin-bottom: 20rpx;
    }

    .title-card-item {
      flex: 0 0 auto;
      width: 34%;
      height: 178rpx;
      box-sizing: border-box;
      border-right: 1rpx solid #F3F4F6;

      .title-card-item-title {
        text-align: center;
        color: #007aff;
        font-weight: 700;
        font-size: 48rpx;
        margin: 24rpx 0;
      }

      .title-card-item-content {
        text-align: center;
        color: #6a7282;
        font-size: 24rpx;
      }
    }

    .filter-card {
      padding: 35rpx 0;
    }
  }

  .content-list {
    margin: 20rpx;
    // height: 67%;
    overflow: auto;

    .content-list-item {
      background-color: #fff;
      border-radius: 30rpx;
      box-shadow: 0rpx 0rpx 2rpx rgba(0, 0, 0, 0.05);
      margin-bottom: 20rpx;
      padding: 20rpx 20rpx;

      .site-title-top {
        margin-top: 80rpx;
        margin-bottom: 30rpx;
      }

      .site-title-item {
        margin-bottom: 25rpx;
      }


      .site-title {
        display: flex;
        align-items: center;
        justify-content: space-between;
        color: rgba(0, 0, 0, 0.7);
        font-size: 26rpx;

        .site-title-label {
          color: rgba(106, 114, 130, 1);
          font-size: 28rpx;
        }

        .site-title-value {
          color: rgba(10, 10, 10, 1);
          font-size: 28rpx;
          font-weight: 500;

        }



      }

      .line {
        width: 100%;
        height: 1rpx;
        border-bottom: 1rpx solid rgba(0, 0, 0, 0.1);
        margin: 35rpx 0;
      }

      .charge-power-money {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .charge-power-num {
          text-align: center;
          color: rgba(0, 122, 255, 1);
          font-family: Inter;
          font-weight: 600;
          font-size: 36rpx;
          line-height: 28rpx;
        }

        .charge-power-money-item-label {
          text-align: center;
          font-weight: 400;
          font-size: 24rpx;
          color: rgba(106, 114, 130, 1);
          margin-top: 40rpx;
        }
      }

      .site-status {
        padding: 10rpx 12rpx;
        color: #fff;
        font-size: 24rpx;
        border-radius: 16rpx;
      }

      .bgc-0 {
        background-color: #6A7282;
      }

      .bgc-1 {
        background-color: #007aff;
      }

      .bgc-2 {
        background-color: #00C950;
      }

      .bgc-3 {
        background-color: #FF4D4F;
      }

      .bgc-4 {
        background-color: #f0cd99;
      }

      .bgc-5 {
        background-color: #FF9900;
      }

      .bgc-6 {
        background-color: #dae7d3;

      }

      .card-button {
        display: flex;
        justify-content: flex-end;
        align-items: center;

        .button-item {
          padding: 10rpx 40rpx;
          border-radius: 8rpx;
        }

        .btn {
          border: 0.9rpx solid rgba(0, 122, 255, 1);
          color: rgba(0, 122, 255, 1);
        }

        .btn1 {
          border: 0.9rpx solid rgba(0, 0, 0, 0.1);
          color: rgba(10, 10, 10, 1);
          margin-left: 52rpx;
        }

        .btn2 {
          border: 0.9rpx solid rgba(251, 44, 54, 1);
          color: rgba(251, 44, 54, 1);
          margin-left: 52rpx;
        }

      }


    }
  }


}

.applyRefund-card {
  padding: 20rpx 40rpx;

  .card-title {
    text-align: center;
    font-size: 32rpx;
    font-weight: 500;
  }

  .apply-money {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 20rpx 0;

    .apply-money-label {
      font-size: 30rpx;
      color: #6a7282;
    }

    .apply-money-value {
      width: 70%;
      font-size: 30rpx;
      color: #0a0a0a;
      white-space: normal;
      word-break: break-all;
    }

  }
}

.button-item.disabled {
  opacity: 0.4;
  pointer-events: none;

}
</style>