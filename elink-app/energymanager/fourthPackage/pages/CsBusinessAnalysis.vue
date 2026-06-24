<template>
  <view class="containers">

    <view class="title-containers">
      <analysisTime @timeSelect="handleTimeSelect"></analysisTime>
      <image class="image" src="/static/image/alarm/selecttp.png" mode="" @click="goSelectSite()"></image>
    </view>
    <view class="card-list">
      <view class="card-item" v-for="(item, groupIndex) in list" :key="item.id">
        <view class="card-item-title">
          <view class="title-card-item-title">{{ item.name }}</view>
        </view>
        <view class="card-item-content">
          <view class="card-grid">
            <view class="grid-item" v-for="(item1, index) in item.fieldList" :key="index" @click="clickType(item1.id,item.name)"
              :class="{ active: types.includes(String(item1.id)) }">
              <view class="grid-title">{{ item1.name }}</view>
              <view class="grid-value">
                <view class="grid-value-text">{{ $filters.numberValue(cardListValue[item1.fieldName]) }}
                  {{ $filters.numberUnits(cardListValue[item1.fieldName], item1.unit) }}</view>
                <view class="grid-value-unit" :class="cardListValue[item1.ratioName] > 0 ? 'red' : cardListValue[item1.ratioName] < 0 ? 'green' : ''">
                  <view class="iconfont icon-shangjiantou" v-if="cardListValue[item1.ratioName] > 0 "></view>
                  <view class="iconfont icon-xiajiantou" v-else-if="cardListValue[item1.ratioName] < 0"></view>
                  {{ cardListValue[item1.ratioName]?cardListValue[item1.ratioName]+'%':'' }}
                </view>
              </view>
            </view>
          </view>
        </view>

        <view class="card-item-chart" v-if="types[groupIndex]==='3'">
          <view class="card-item-chart-btn">
            <view class="changeBtn" v-for="i in changeBtnList" :key="i.value" :class="i.value==btnSelect?'selectBtn':''" @click="btnSelectClick(i)">
              {{i.label}}
            </view>
          </view>

          <!-- 折线 -->
            <qiun-data-charts v-if="btnSelect === '1' && item.chartData3" type="line" :opts="chartOpts" :chartData="item.chartData3"
              style="width: 100%; height: 400rpx;" :canvas2d="true" :ontouch="true" :canvasId="'chart-area-3-'+groupIndex"
              :inScrollView="true"></qiun-data-charts>

            <!-- 柱状 -->
            <qiun-data-charts v-else-if="btnSelect === '2' && item.chartData3" type="column" :opts="barOpts" :chartData="item.chartData3"
              style="width: 100%; height: 400rpx;" :canvas2d="true" :ontouch="true" :canvasId="'chart-area-4-'+groupIndex"
              :inScrollView="true"></qiun-data-charts>
        </view>

        <view class="card-item-chart" v-else>
          <qiun-data-charts type="line" :opts="chartOpts" :chartData="item.chartData" style="width: 100%; height: 400rpx;" :canvas2d="true" :ontouch="true"
            :canvasId="'chart-area-' + groupIndex" :inScrollView="true"></qiun-data-charts>
        </view>
      </view>
    </view>

  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import analysisTime from './components/analysisTime.vue'
const PAGE_ID = 'CsBusinessAnalysis';
const SELECT_SITE_EVENT = `selectSiteEvent_${PAGE_ID}`;
import { countOperationCurve, countOperationOverview } from '../api/chargingOrder.js';

const selectedSite = ref([])
const list = ref([
  {
    name: "营收统计",
    exportLoading: false,
    fieldList: [
      {
        id: 1,
        unit: "元",
        name: "充电订单金额",
        fieldName: "chargeOrderMoney",
        ratioName: "chargeOrderMoneyRatio",
        describe: "筛选日期内创建的充电订单中，累计“订单金额之和”，未扣除优惠减免等费用。",
        seriesListArray: [
          { type: 'line', name: '订单总金额', showSymbol: false, fieldName: 'sumCost', data: [] },
          { type: 'line', name: '充电电费', showSymbol: false, isComputeSum: true, fieldName: 'chargeFee', data: [] },
          { type: 'line', name: '充电服务费', showSymbol: false, isComputeSum: true, fieldName: 'chargeServiceFee', data: [] },
        ]
      },
      {
        id: 2,
        unit: "元",
        name: "充电实付金额",
        fieldName: "chargePayMoney",
        ratioName: "chargePayMoneyRatio",
        describe: "筛选日期内创建的充电订单中，累计“实付金额之和”。",
        seriesListArray: [
          { type: 'line', name: '实付总金额', showSymbol: false, fieldName: 'actualTotalCost', data: [] },
          { type: 'line', name: '实付电费', showSymbol: false, isComputeSum: true, fieldName: 'actualTotalElect', data: [] },
          { type: 'line', name: '实付服务费', showSymbol: false, isComputeSum: true, fieldName: 'actualTotalFee', data: [] },
        ]
      },
      {
        id: 3,
        unit: "度",
        name: "充电电量",
        isChildrenTab: true,
        seriesListArray: [],
        fieldName: "chargeOrderQt",
        ratioName: "chargeOrderQtRatio",
        describe: "充电量：筛选日期内创建的充电订单中，累计“充电度数总和”。<br />尖峰平谷充电量：尖峰平谷时段的充电量",
        seriesListArray1: [
          { type: 'line', name: '充电电量', showSymbol: false, fieldName: 'chargeQt', data: [] },
          { type: 'line', name: '直流充电量', showSymbol: false, isComputeSum: true, fieldName: 'dcChargeQt', data: [] },
          { type: 'line', name: '交流充电量', showSymbol: false, isComputeSum: true, fieldName: 'acChargeQt', data: [] },
        ],
        seriesListArray2: [
          { type: 'bar', name: '尖', stack: 'Ad', barWidth: 16, emphasis: { focus: "series" }, color: "#FB6868", isComputeSum: true, fieldName: 'sharpQt', data: [] },
          { type: 'bar', name: '峰', stack: 'Ad', barWidth: 16, emphasis: { focus: "series" }, color: "#FD9449", isComputeSum: true, fieldName: 'peakQt', data: [] },
          { type: 'bar', name: '平', stack: 'Ad', barWidth: 16, emphasis: { focus: "series" }, color: "#56ADF7", isComputeSum: true, fieldName: 'flatQt', data: [] },
          { type: 'bar', name: '谷', stack: 'Ad', barWidth: 16, emphasis: { focus: "series" }, color: "#6DCF36", isComputeSum: true, fieldName: 'valleyQt', data: [] },
          { type: 'bar', name: '深谷', stack: 'Ad', barWidth: 16, emphasis: { focus: "series" }, color: "#36CFC2", isComputeSum: true, fieldName: 'deepvalleyQt', data: [] },
        ],
      },
      {
        id: 4,
        unit: "笔",
        name: "充电订单数量",
        fieldName: "chargeOrderNum",
        ratioName: "chargeOrderNumRatio",
        describe: "筛选日期内创建的充电订单累计数量，排除异常订单和启动失败的订单。<br />异常订单量：筛选日期内创建的充电订单累计异常订单数量，含启动失败订单。",
        seriesListArray: [
          { type: 'line', name: '订单数量', color: '#56ADF7', showSymbol: false, fieldName: 'orderNum', data: [] },
          { type: 'line', name: '异常订单数量', color: '#FB6868', showSymbol: false, fieldName: 'abOrderNum', data: [] }
        ]
      },
      {
        id: 5,
        unit: "元",
        legendShow: true,
        name: "V2G放电金额",
        fieldName: "dischargeOrderMoney",
        ratioName: "dischargeOrderMoneyRatio",
        describe: "筛选日期内创建的放电订单中，累计“订单金额之和”。",
        seriesListArray: [
          { type: 'line', name: 'V2G订单金额', color: '#3CC3DF', showSymbol: false, fieldName: 'dischargeSumCost', data: [] }
        ]
      },
      {
        id: 6,
        unit: "度",
        legendShow: true,
        name: "V2G放电电量",
        fieldName: "dischargeOrderQt",
        ratioName: "dischargeOrderQtRatio",
        describe: "放电量：筛选日期内创建的放电订单中，累计放电度数总和。<br />尖峰平谷放电量：尖峰平谷时段的放电量。",
        seriesListArray: [
          { type: 'line', name: 'V2G放电电量', color: '#FFBE70', showSymbol: false, fieldName: 'dischargeQt', data: [] }
        ]
      },
    ]
  },
  {
    name: "经营效率",
    exportLoading: false,
    fieldList: [
      {
        id: 7,
        unit: "度",
        name: "枪均电量",
        fieldName: "avgChargeQt",
        ratioName: "avgChargeQtRatio",
        describe: "筛选日期内创建的订单中，累计充电度数 / 筛选日期内平均枪数。",
        seriesListArray: [
          { type: 'line', name: '枪均充电量', showSymbol: false, fieldName: 'gunAvChargeQt', data: [] },
          { type: 'line', name: '直流枪均充电量', showSymbol: false, fieldName: 'dcGunAvChargeQt', data: [] },
          { type: 'line', name: '交流枪均充电量', showSymbol: false, fieldName: 'acGunAvChargeQt', data: [] },
        ]
      },
      {
        id: 8,
        unit: "%",
        name: "时间利用率",
        legendShow: true,
        fieldName: "timeRatio",
        ratioName: "timeRatioRatio",
        describe: "筛选日期内创建的订单中，累计(充电时长+放电时长)/(总枪数*24h(筛选日期内每天的数据求和))。",
        seriesListArray: [
          { type: 'line', name: '时间利用率', color: '#3CC3DF', showSymbol: false, fieldName: 'timeRatio', data: [] }
        ]
      },
      {
        id: 9,
        unit: "时",
        name: "充电时长",
        fieldName: "chargeDuration",
        ratioName: "chargeDurationRatio",
        describe: "筛选日期内创建的充电订单，累计充电时长。",
        seriesListArray: [
          { type: 'line', name: '充电时长', showSymbol: false, fieldName: 'chargeDuration', data: [] },
          { type: 'line', name: '直流充电时长', showSymbol: false, fieldName: 'dcChargeDuration', data: [] },
          { type: 'line', name: '交流充电时长', showSymbol: false, fieldName: 'acChargeDuration', data: [] },
        ]
      },
      {
        id: 10,
        unit: "元",
        legendShow: true,
        name: "度均服务费",
        fieldName: "avgChargeFee",
        ratioName: "avgChargeFeeRatio",
        describe: "筛选日期内创建的订单中，累计充电服务费 / 总充电度数。",
        seriesListArray: [
          { type: 'line', name: '度均服务费', color: '#3CC3DF', showSymbol: false, fieldName: 'avgChargeFee', data: [] }
        ]
      },
      {
        id: 11,
        unit: "%",
        legendShow: true,
        name: "功率利用率",
        fieldName: "powerRatio",
        ratioName: "powerRatioRatio",
        describe: "筛选日期内创建的订单中，累计(充电度数+放电度数)/(全部充电桩额定功率之和*24h(筛选日期内每天的数据求和))。",
        seriesListArray: [
          { type: 'line', name: '功率利用率', color: '#3CC3DF', showSymbol: false, fieldName: 'powerRatio', data: [] }
        ]
      },
      {
        id: 12,
        unit: "%",
        legendShow: true,
        name: "一次充电成功率",
        fieldName: "chargeSuccessRatio",
        ratioName: "chargeSuccessRatioRatio",
        describe: "筛选日期内创建的订单中，启动成功的订单数量(即排除启动失败、无效订单、挂起)/总订单数量(不含进行中)*100%。",
        seriesListArray: [
          { type: 'line', name: '一次充电成功率', color: '#3CC3DF', showSymbol: false, fieldName: 'chargeSuccessRatio', data: [] }
        ]
      },
    ]
  }
])

const chartOpts = {
  padding: [10, 15, 20, 0],
  dataLabel: false,
  dataPointShape: false,
  legend: {
    show: true, position: 'top', float: 'right',
    margin: 5, padding: 5, itemGap: 5, fontSize: 12, fontColor: '#333'
  },
  extra: {
    area: { type: "curve", opacity: 0.6, addLine: true, width: 2, gradient: true }
  },
  xAxis: {
    disableGrid: true, fontColor: '#666666', fontSize: 10,
    labelCount: 5,
    rotate: false
  },
  yAxis: {
    show: true, disableGrid: false, gridType: "dash", splitNumber: 4, gridColor: "#CCCCCC",
    padding: 10, showTitle: true, data: [{ position: "left", title: "" }]
  }
}
const barOpts = {
  padding: [10, 15, 20, 0],
  dataLabel: false,
  dataPointShape: false,
  legend: {
    show: true, position: 'top', float: 'right',
    margin: 5, padding: 5, itemGap: 5, fontSize: 12, fontColor: '#333'
  },
  extra: {
    column: {
      type: "stack",
      barBorderCircle: true
    },

  },
  xAxis: {
    disableGrid: true, fontColor: '#666666', fontSize: 10,
    labelCount: 5,
    rotate: false
  },
  yAxis: {
    show: true, disableGrid: false, gridType: "dash", splitNumber: 4, gridColor: "#CCCCCC",
    padding: 10, showTitle: true, data: [{ position: "left", title: "" }]
  }
}

// 修复：全部统一字符串类型，避免判断异常
const btnSelect = ref("1")
const changeBtnList = ref([
  { label: "趋势", value: "1" },
  { label: "分时", value: "2" }
])
const changeListValue = ref([]) // 修复：必须是数组，不是对象
const types = ref(['1', '7'])
const cardListValue = ref({})
const timeList = ref([])
const dateListArray = ref([])

onLoad(() => {
  uni.$on(SELECT_SITE_EVENT, (value) => {
    selectedSite.value = value;
  });
});

// ====================== 核心修复：按钮切换 ======================
const btnSelectClick = (item) => {
  btnSelect.value = item.value;
  const group1 = list.value[0];
  const item3 = group1.fieldList[2];
  const len = dateListArray.value?.length || 0;

  // 趋势图
  if (item.value === "1") {
    group1.chartData3 = {
      categories: dateListArray.value || [],
      series: item3.seriesListArray1.map(s => {
        const dataItem = changeListValue.value.find(y => y.dataName === s.fieldName);
        // 强制给长度正确的数字数组，没有就补0
        const data = Array(len).fill(0);



        if (dataItem?.dataValueList) {
          dataItem.dataValueList.forEach((val, i) => {
            if (i < len) data[i] = Number(val) || 0;
          });
        }
        return { ...s, data };
      })
    };
  }

  // 柱状图（深谷也强制补0，彻底解决空数组！）
  if (item.value === "2") {
    group1.chartData3 = {
      categories: dateListArray.value || [],
      series: item3.seriesListArray2.map(s => {
        const dataItem = changeListValue.value.find(y => y.dataName === s.fieldName);
        // 强制给长度正确的数字数组，没有就补0
        const data = Array(len).fill(0);
        if (dataItem?.dataValueList) {
          dataItem.dataValueList.forEach((val, i) => {
            if (i < len) data[i] = Number(val) || 0;
          });
        }
        return { ...s, data };
      })
    };
  }
};

const goSelectSite = () => {
  uni.navigateTo({
    url: `/thirdPackage/pages/components/selectSite?selectedSite=${encodeURIComponent(JSON.stringify(selectedSite.value))}&source=${PAGE_ID}`
  });
}

const clickType = (id, name) => {
  if (name == '营收统计') {
    types.value[0] = id.toString();
  } else {
    types.value[1] = id.toString();
  }
  getCountOperationCurve();
}

const getCountOperationCurve = async () => {
  const sites = selectedSite.value.length !== 0 ? selectedSite.value : uni.getStorageSync('SITE_LIST');
  const siteIds = Array.isArray(sites) ? sites.filter(id => id !== '593') : [];
  const realTypes = types.value.map(item => parseInt(item));

  const dateType = timeList.value.dataType === '3' ? 2 : 1;

  let obj = {
    startDate: timeList.value.startDate,
    endDate: timeList.value.endDate,
    siteIds: JSON.stringify(siteIds),
    types: JSON.stringify(realTypes),
    serId: uni.getStorageSync('USER_ID'),
    dateType: dateType,
  }

  const { data } = await countOperationCurve(obj);
  if (!data || !data.curveDataMap) return;

  const dateList = data.dateList || [];
  dateListArray.value = dateList;

  // 其他指标图表
  const y1 = data.curveDataMap['1'] || [];
  const group1 = list.value[0];
  const series1 = group1.fieldList.find(f => f.id == types.value[0])?.seriesListArray || [];
  group1.chartData = {
    // xAxis: { data: dateList },
    categories: dateList || [],
    series: series1.map(s => ({
      ...s,
      data: y1.find(y => y.dataName === s.fieldName)?.dataValueList || []
    }))
  };

  const y7 = data.curveDataMap['7'] || [];
  const group2 = list.value[1];
  const series2 = group2.fieldList.find(f => f.id == types.value[1])?.seriesListArray || [];
  group2.chartData = {
    categories: dateList || [],
    series: series2.map(s => ({
      ...s,
      data: y7.find(y => y.dataName === s.fieldName)?.dataValueList || []
    }))
  };

  // ====================== 类型3 初始化 ======================
  if (types.value[0] === '3') {
    const y3 = data.curveDataMap['3'] || [];
    const item3 = group1.fieldList[2];

    // 默认显示趋势图
    group1.chartData3 = {
      categories: dateList || [],
      series: item3.seriesListArray1.map(s => ({
        ...s,
        data: y3.find(y => y.dataName === s.fieldName)?.dataValueList || []
      }))
    };

    // 缓存数据
    changeListValue.value = y3;
  }
}

const handleTimeSelect = (data) => {
  timeList.value = data;
  getCountOperationOverview();
  getCountOperationCurve();
}

const getCountOperationOverview = async () => {
  const sites = selectedSite.value.length !== 0 ? selectedSite.value : uni.getStorageSync('SITE_LIST');
  const siteIds = Array.isArray(sites) ? sites.filter(id => id !== '593') : [];

  let obj = {
    startDate: timeList.value.startDate,
    endDate: timeList.value.endDate,
    beforeEndDate: timeList.value.beforeEndDate,
    beforeStartDate: timeList.value.beforeStartDate,
    userId: uni.getStorageSync('USER_ID'),
    siteIds: JSON.stringify(siteIds),
  }

  cardListValue.value = await countOperationOverview(obj).then(res => res.data);
}
</script>

<style lang="scss" scoped>
.containers {
  background-color: #f5f5f5;
  height: 100vh;

  .title-containers {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 96%;
    margin: 10rpx auto;
    background: #fff;
    .image {
      width: 40upx;
      height: 40upx;
    }
  }
}

.card-list {
  .card-item {
    background-color: #fff;
    box-sizing: border-box;
    width: 96%;
    padding: 20rpx;
    margin: 10rpx auto;
  }
  .card-item-title {
    font-size: 30rpx;
    font-weight: 500;
    color: #333;
    margin-bottom: 20rpx;
  }
  .card-item-content {
    border: 1px solid #eee;
    .card-grid {
      display: flex;
      flex-wrap: wrap;
      gap: 20rpx;
    }
    .grid-item {
      width: calc(33.333% - 14rpx);
      background: #f7f9fc;
      border-radius: 16rpx;
      padding: 12rpx;
      text-align: center;
    }
    .active {
      border: 1rpx solid #81c0eb;
    }
    .grid-title {
      font-size: 24rpx;
      color: #666;
      margin-bottom: 12rpx;
    }
    .grid-value {
      font-size: 24rpx;
      color: #666;
      display: flex;
      justify-content: space-around;
      .grid-value-unit {
        display: flex;
        align-items: center;
      }
      .red {
        color: #ff2b2b;
      }
      .green {
        color: #41cb4a;
      }
    }
  }
  .card-item-chart {
    width: 96%;
    height: 500rpx;
    .card-item-chart-btn {
      display: flex;
      margin-top: 10rpx;
      gap: 10rpx;
      .changeBtn {
        padding: 10rpx 20rpx;
        border: 1rpx solid #dbd9d9;
        border-radius: 6rpx;
      }
      .selectBtn {
        border: 1rpx solid #81c0eb;
      }
    }
  }
}
</style>