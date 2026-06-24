<template>
  <view class="containers">
    <view class="content-image" :class="[
      scenarioTypes.length > 0 ? 'class-a' : '',
      scenarioTypes.length > 2 ? 'class-c' : ''
    ]">
      <view class="weather">
        <view style="display: flex;align-items: center;" class="site-image_nameWeight">
          <image :src="weatherIcons[overviewList?.iconDay] || weatherIcons[100]" mode="aspectFit"
            style="width: 40rpx; height: 40rpx;margin-right: 12rpx;" />
          {{ overviewList?.tempMin ?? '--' }}℃~{{ overviewList?.tempMax ?? '--' }}℃
        </view>

        <view class="site-image_nameWeight" style="display: flex;margin-top: 18rpx; align-items: center;">
          <image src="../../assets/kva.png" class="site-image_init" style="margin-right: 12rpx;"></image>
          <view>{{ $filters.numberUnit(overviewList.tranSafeCap) }}kVA</view>


        </view>
      </view>
      <view class="site_image">
        <image src="../../assets/dw.png" class="site-image_dw"></image>
        <view class="site-image_value">{{ $filters.numberUnit(overviewList.gridCap) }}kW</view>

        <view class="site-image_name">电网</view>
      </view>
      <view class="safe">
        <view class="site-image_name" style="text-align: right;">安全运行</view>
        <view class="site-image_value" style="text-align: right;margin-top: 18rpx;">{{ overviewList?.runDays ?? '--' }}
          <text class="site-image_name">天</text>
        </view>
      </view>

      <view v-for="(item, index) in filteredScenario" :key="index" :class="['site-image', getPositionClass(index)]">
        <view style="color:#00CD6F;    display: flex;
    align-items: center;
    text-align: center;
    justify-content: center;" v-if="item.label == '2'">
          <image src="../../assets/green_lightning.png" style="width: 40rpx;height: 40rpx;"></image>
          {{overviewList.soc}}%
        </view>
        <image :src="item.imageName" mode="aspectFit" style="width: 100%;height: 100%;" />
        <view style="display: flex; justify-content: center;">
          <view class="site-image_value" v-if="item.label == '2'">{{ $filters.numberUnit(item.value1) }}{{ item.unit1
          }}/</view>
          <view class="site-image_value">{{ $filters.numberUnit(item.value) }}{{ item.unit }}</view>
        </view>
        <view class="site-image_name">{{ item.name }}</view>
      </view>
      <!-- 中心位置的充电标志 -->
      <view class="site-image_center">
        <image src="../../assets/black_lightning.png" class="site-image_lightning"></image>

      </view>
      <!-- 动态生成能量流动线条 -->
      <view class="line line-dw">
        <view class="flow"></view>
      </view>

      <view v-for="(item, index) in filteredScenario" :key="index" class="line-line">
        <view class="line line-h" :class="getLineClass(index, 'h')">
          <view class="flow"></view>
        </view>
        <view class="line line-w" :class="getLineClass(index, 'w')">
          <view class="flow"></view>
        </view>
      </view>

    </view>
    <view class="content-switch">
      <view class="switch-item">
        <view class="switch-item-name">指令下发</view>
        <view class="switch-item-content">
          <image v-if="CommandDelivery" src="../../assets/open.png" class="site-image_init"></image>
          <image v-else src="../../assets/close.png" class="site-image_init"></image>
        </view>
        <switch :checked="CommandDelivery" @change="switch1Change" />
      </view>
      <view class="switch-item">
        <view class="switch-item-name">策略控制</view>
        <view class="switch-item-content">
          <image v-if="PolicyControl" src="../../assets/open.png" class="site-image_init"></image>
          <image v-else src="../../assets/close.png" class="site-image_init"></image>
        </view>
        <switch :checked="PolicyControl" @change="switch2Change" />
      </view>
    </view>
    <view class="profit-card">
      <view class="profit-card-kpi">
        <image src="../../assets/kpi.png" class="site-image_kpi"></image>

        <view class="profit-card-kpi-value">KPI</view>
      </view>
      <view v-for="(item, index) in filelterProfit" :key="index" class="profit-item">
        <view class="profit-item-image">
          <image :src="item.imageName" mode="aspectFit" style="width: 100%;height: 100%;" />
        </view>
        <view class="profit-item-content">
          <view class="profit-item-content1">
            <view class="profit-item-name">
              <view class="profit-item-name-text">{{ item.profitName1 }}</view>
              <view class="profit-item-name-text">{{ item.profitName2 }}</view>
            </view>
            <view class="profit-item-name">
              <view class="profit-item-value-text">
                <view class="circle"
                  :style="item.label == '1' ? 'background-color:#66EDAF' : item.label == '2' ? 'background-color:#ffb200' : item.label == '3' ? 'background-color:#3288EB' : 'background-color:#7D59CC'">
                </view>
                <view class="profit-item-value">{{ $filters.numberUnit(item.profitValue1) }}</view>
                <view class="profit-item-unit">{{ item.profitUnit1 }}</view>
              </view>
              <view class="profit-item-value-text">
                <view class="circle"
                  :style="item.label == '1' ? 'background-color:#66EDAF' : item.label == '2' ? 'background-color:#ffb200' : item.label == '3' ? 'background-color:#3288EB' : 'background-color:#7D59CC'">
                </view>
                <view class="profit-item-value">{{ $filters.numberUnit(item.profitValue2) }}</view>
                <view class="profit-item-unit">{{ item.profitUnit2 }}</view>
              </view>
            </view>

          </view>
          <view class="profit-item-content1 profit-item-content2 " v-if="item.label == '3' || item.label == '4'">
            <view class="profit-item-name">
              <view class="profit-item-name-text" style="display: flex;align-items: center;">{{ item.profitName3 }}
                <uni-tooltip content="示例文字" placement="top">
                  <uni-icons v-if="item.label == '3'" type="info" size="20" color="#808080"></uni-icons>
                </uni-tooltip>
              </view>
              <view class="profit-item-name-text">{{ item.profitName4 }}</view>
            </view>
            <view class="profit-item-name">
              <view class="profit-item-value-text">
                <view class="circle"
                  :style="item.label == '1' ? 'background-color:#66EDAF' : item.label == '2' ? 'background-color:#ffb200' : item.label == '3' ? 'background-color:#3288EB' : 'background-color:#7D59CC'">
                </view>
                <view class="profit-item-value">{{ $filters.numberUnit(item.profitValue3) }}
                </view>
                <view class="profit-item-unit">{{ item.profitUnit3 }}</view>
              </view>
              <view class="profit-item-value-text">
                <view class="circle"
                  :style="item.label == '1' ? 'background-color:#66EDAF' : item.label == '2' ? 'background-color:#ffb200' : item.label == '3' ? 'background-color:#3288EB' : 'background-color:#7D59CC'">
                </view>
                <view class="profit-item-value">{{ $filters.numberUnit(item.profitValue4) }}</view>
                <view class="profit-item-unit">{{ item.profitUnit4 }}</view>
              </view>
            </view>
          </view>
        </view>

      </view>
    </view>
  </view>
</template>

<script setup name="setup">
import { ref, watch, computed, onMounted } from 'vue'
import weatherIcons from './icon'
import GFImg from '../../assets/gf.png'
import CNImg from '../../assets/cn.png'
import FZImg from '../../assets/fz.png'
import DZImg from '../../assets/cdz.png'
import powerrofitImg from '../../assets/powerImg.png'
import GFProfitImg from '../../assets/GFImg.png'
import CNProfitImg from '../../assets/CNImg.png'
import DZProfitImg from '../../assets/DZImg.png'
import { findSiteOverview } from '../../api/index'
const props = defineProps({
  siteId: {
    type: String,
    default: ''
  },
  scenarioTypes: {
    type: Array,
    default: () => []
  }
})
// 加在顶部
const iconPrefix = '../../assets/image/'
const siteId = ref('')
const scenarioTypes = ref([])
const CommandDelivery = ref(false)
const PolicyControl = ref(false)
const scenarioTypeName = ref([])
// 电网，光伏，储能，电桩的收益信息
const scenarioTypeNameProfit = ref([])
const overviewList = ref({})

// 过滤出当前场景
const filteredScenario = computed(() => {
  return scenarioTypeName.value.filter(item =>
    scenarioTypes.value.includes(item.label.toString()) // 关键：转字符串匹配
  );
});
const filelterProfit = computed(() => {
  return scenarioTypeNameProfit.value.filter(item =>
    scenarioTypes.value.includes(item.label.toString()) // 关键：转字符串匹配
  );
});
const getFindSiteOverview = () => {
  if (!siteId.value) return;
  findSiteOverview({ siteId: siteId.value }).then(res => {
    console.log(res, '站点概览', scenarioTypes.value);
    scenarioTypeName.value = [
      {
        label: '1',
        name: '光伏',
        imageName: GFImg,
        unit: 'kW',
        value: res.data.pvCap
      },
      {
        label: '2',
        name: '储能',
        imageName: CNImg,
        unit1: 'kW',
        value1: res.data.pcsPower,
        unit: 'kWh',
        value: res.data.batteryCap

      },
      {
        label: '4',
        name: '负载',
        imageName: FZImg,
        unit: 'kW',
        value: res.data.loadCap
      },
      {
        label: '3',
        name: '电桩',
        imageName: DZImg,
        unit: 'kW',
        value: res.data.pileCap
      }
    ]
    overviewList.value = res.data
    scenarioTypeNameProfit.value = [
      {
        label: '1',
        imageName: powerrofitImg,
        name: '电网',
        profitName1: '今日下网电量',
        profitValue1: res.data.dayLowerQt,
        profitName2: '今日上网电量',
        profitValue2: res.data.dayNetQt,
        profitUnit1: 'kWh',
        profitUnit2: 'kWh',
      },
      {
        label: '1',
        name: '光伏',
        imageName: GFProfitImg,
        profitName1: '光伏今日发电量',
        profitValue1: res.data.dayPvQt,
        profitName2: '光伏今日收益',
        profitValue2: res.data.dayPvIncome,
        profitUnit1: 'kWh',
        profitUnit2: '元',

      },
      {
        label: '2',
        name: '储能',
        imageName: CNProfitImg,
        profitName1: '储能今日充电量',
        profitValue1: res.data.daySeChargeQt,
        profitUnit1: 'kWh',
        profitName2: '储能今日放电量',
        profitValue2: res.data.daySeDischargeQt,
        profitUnit2: 'kWh',
        profitName3: '储能今日收益',
        profitValue3: res.data.daySeIncome,
        profitUnit3: '元',
        profitName4: '储能累计循环次数',
        profitValue4: res.data.daySeCycleNum,
        profitUnit4: '次'
      },
      {
        label: '3',
        name: '电桩',
        imageName: DZProfitImg,
        profitName1: '电桩今日充电量',
        profitValue1: res.data.dayPileChargeQt,
        profitUnit1: 'kWh',
        profitName2: '电桩今日V2G电量',
        profitValue2: res.data.dayPileDischargeQt,
        profitUnit2: 'kWh',
        profitName3: '充电订单金额',
        profitValue3: res.data.dayPileChargeMoney,
        profitUnit3: '元',
        profitName4: 'V2G订单金额',
        profitValue4: res.data.dayPileDischargeMoney,
        profitUnit4: '元'
      }
    ]
  });
};
watch(() => [props.siteId, props.scenarioTypes], (newVal) => {
  siteId.value = newVal[0];

  // 核心修复：保证一定是数组，并且解开 Proxy
  let val = newVal[1] || [];
  if (typeof val === 'string') {
    scenarioTypes.value = val.split(',');
  } else {
    scenarioTypes.value = [...val]; // 数组解构，彻底脱离 Proxy
  }

  console.log("最终数组：", scenarioTypes.value); // 现在一定是 [1,2]
  getFindSiteOverview();
}, { immediate: true });

// 自动获取图标位置 class
const getPositionClass = (index) => {
  const classList = ['one', 'two', 'three', 'four']
  return classList[index]
}
const getLineClass = (index, type) => {
  return `line-${type}${index + 1}`
}
// 指令下发切换
const switch1Change = (e) => {
  CommandDelivery.value = e.detail.value
  console.log(e.detail.value)
}
// 策略控制切换
const switch2Change = (e) => {
  PolicyControl.value = e.detail.value
  console.log(e.detail.value)
}

</script>

<style scoped lang="less">
.containers {
  height: 100%;
}

.content-image {
  width: 100%;
  position: relative;
  display: flex;
  justify-content: center;
  padding-bottom: 23rpx;
  min-height: 570rpx;
  /* 底部留一点空隙即可 */

  .weather {
    position: absolute;
    top: 0;
    left: 47rpx;
    width: 200rpx;

    image {
      width: 44rpx;
      height: 44rpx;
    }
  }

  .safe {
    position: absolute;
    top: 0;
    right: 47rpx;
    width: 200rpx;
  }

  .site-image_dw {
    width: 163rpx;
    height: 174rpx;

  }

  .site-image_center {
    width: 95rpx;
    height: 95rpx;
    border-radius: 50%;
    background-color: #fff;
    border: 3rpx solid #66edaf;
    display: flex;
    align-items: center;
    position: absolute;
    top: 427rpx;
    justify-content: center;
    box-shadow: 0 0 16rpx 8rpx rgba(102, 237, 175, 0.25);

    .site-image_lightning {
      width: 44rpx;
      height: 44rpx;
    }
  }

  .site-image {
    height: 158rpx;
  }

  .one {
    position: absolute;
    top: 227rpx;
    left: 47rpx;
    width: 200rpx;
  }

  .two {
    position: absolute;
    top: 227rpx;
    right: 47rpx;
    width: 200rpx;
  }

  .three {
    position: absolute;
    top: 538rpx;
    left: 47rpx;
    width: 200rpx;
  }

  .four {
    position: absolute;
    top: 538rpx;
    right: 47rpx;
    width: 200rpx;
  }

  .line {
    position: absolute;
    background-color: #66edaf;
    z-index: -1;
    border-radius: 2rpx;
    overflow: hidden;
  }

  .flow {
    position: absolute;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, #fff, transparent);
    animation: flow 1.6s infinite linear;
  }

  .line-w .flow {
    animation: flow-v 1.6s infinite linear;
  }

  /* 中心 ↔ 电网 */
  .line-dw {
    width: 4rpx;
    height: 178rpx;
    left: 50%;
    top: 245rpx;
    transform: translate(-50%);
  }

  @keyframes flow {
    0% {
      transform: translateX(-100%);
    }

    100% {
      transform: translateX(100%);
    }
  }

  @keyframes flow-v {
    0% {
      transform: translateY(-100%);
    }

    100% {
      transform: translateY(100%);
    }
  }

  /* 左上 */
  .line-h1 {
    width: 131rpx;
    height: 4rpx;
    left: 48%;
    top: 332rpx;
    transform: translate(-100%);
  }

  .line-w1 {
    height: 93rpx;
    width: 4rpx;
    left: 48%;
    top: 332rpx;
    transform: translate(-100%);
  }

  /* 右上 */
  .line-h2 {
    width: 131rpx;
    height: 4rpx;
    left: 34%;
    top: 332rpx;
    transform: translate(100%);
  }

  .line-w2 {
    height: 93rpx;
    width: 4rpx;
    left: 52%;
    top: 332rpx;
    transform: translate(-100%);
  }

  /* 左下 */
  .line-h3 {
    width: 131rpx;
    height: 4rpx;
    left: 48%;
    top: 634rpx;
    transform: translate(-100%);
  }

  .line-w3 {
    height: 104rpx;
    width: 4rpx;
    left: 48%;
    top: 535rpx;
    transform: translate(-100%);
  }

  /* 右下 */
  .line-h4 {
    width: 131rpx;
    height: 4rpx;
    left: 33%;
    top: 634rpx;
    transform: translate(100%);
  }

  .line-w4 {
    height: 104rpx;
    width: 4rpx;
    left: 52%;
    top: 535rpx;
    transform: translate(-100%);
  }

  .site_image {
    width: 163rpx;
  }

  .site-image_value {
    text-align: center;
    color: rgba(50, 136, 235, 1);
    font-family: "DIN Alternate";
    font-weight: 700;
    font-size: 24rpx;
  }

  .site-image_name {
    text-align: center;
    color: rgba(0, 0, 0, 1);
    font-family: "PingFang SC";
    font-weight: 400;
    font-size: 24rpx;
  }

  .site-image_nameWeight {
    text-align: center;
    color: rgba(0, 0, 0, 1);
    font-family: "PingFang SC";
    font-weight: 700;
    font-size: 24rpx;
  }
}

.class-a {
  height: 47%;
}

.class-c {
  height: 63%;
}

.content-switch {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20rpx;

  .switch-item {
    border-radius: 24rpx;
    display: flex;
    background-color: #fff;
    border: 2rpx solid #ffffff;
    padding: 27rpx 12rpx;
    width: 49%;
    align-items: center;
    box-shadow: 0rpx 0rpx 4rpx #00000026;

    .switch-item-name {
      font-weight: 500;
      font-size: 28rpx;
      color: #000000;
      padding-left: 10rpx;
    }

    .switch-item-content {
      padding: 0 23rpx;
    }

    .site-image_init {
      width: 48rpx;
      height: 48rpx;

    }

  }

}

.profit-card {
  background-color: #fff;
  width: 98%;
  padding: 30rpx;
  margin: 26rpx auto 0;
  border-radius: 48rpx 48rpx 0rpx 0rpx;

  .profit-card-kpi {
    display: flex;
    align-items: center;
    padding: 0 0 9rpx 0;

    .site-image_kpi {
      width: 44rpx;
      height: 44rpx;
    }

    .profit-card-kpi-value {
      color: #000000;
      font-family: "PingFang SC";
      font-weight: 600;
      font-size: 36rpx;
      padding-left: 16rpx;
    }

  }

  .profit-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-radius: 12rpx;
    box-shadow: 0rpx 0rpx 8rpx rgba(0, 0, 0, 0.25);
    background: #ffffff;
    margin-top: 20rpx;

    .profit-item-image {
      width: 85rpx;
      height: 85rpx;
      margin: 26rpx 30rpx
    }
  }

  .profit-item-content {
    width: 80%;
    color: rgba(0, 0, 0, 0.5);
    font-size: 28rpx;

    .profit-item-content1 {
      width: 100%;

      .profit-item-name {
        display: flex;
        align-items: center;
        padding: 0 40rpx;
        justify-content: space-around;
        padding: 14rpx 0;

        .profit-item-value-text {
          width: 48%;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .profit-item-value {
          color: rgba(0, 0, 0, 1);
          font-weight: 600;
          font-size: 36rpx;
          width: 60%;
          text-align: center;
        }

        .profit-item-unit {
          color: rgba(0, 0, 0, 0.5);
          font-weight: 400;
          font-size: 24rpx;
        }

        .circle {
          width: 15rpx;
          height: 15rpx;
          border-radius: 50%;
        }
      }
    }

    .profit-item-content2 {
      margin-top: 20rpx;
    }
  }

}
</style>