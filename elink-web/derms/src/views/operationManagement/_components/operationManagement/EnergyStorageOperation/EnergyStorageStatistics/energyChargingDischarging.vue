<template>
  <div class="app-container-right">
    <!-- <div class="app_top_left">
      <el-form inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="24" :md="24" :xl="24">
            <el-form-item style="width: 80%;margin-right: 0; float: right;">
              <el-select v-model="siteId" placeholder="请选择站点" filterable clearable @change="handleSelectChange">
                <el-option :label="item.siteName" :value="item.id" v-for="item in siteList" :key="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div> -->
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="6" :xl="6">
            <el-form-item label="并网点：">
              <el-select v-model="formInline.MeterListId" clearable filterable max-collapse-tags="1" placeholder="全部">
                <el-option v-for="item in MeterList" :key="item.id" :value="item.id"
                  :label="item.deviceName"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="4" :xl="4">
            <el-form-item label="时间：">
              <TabBackground v-model:tabs-index="formInline.dateType" :tabsArray="timeTypeArray"></TabBackground>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6" v-if="formInline.dateType === 1">
            <el-form-item label="选择时间：">
              <el-date-picker v-model="formInline.dateTimeDate" :disabled-date="pickerOptions.disabledDateToday"
                :shortcuts="pickerOptions.shortcuts" :clearable="false" value-format="YYYY-MM-DD" format="YYYY-MM-DD"
                end-placeholder="结束时间" range-separator="-" start-placeholder="开始时间" type="daterange" />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :xl="6" v-if="formInline.dateType === 2">
            <el-form-item label="选择时间：">
              <el-date-picker v-model="formInline.dateTimeDate" :disabled-date="pickerOptions.disabledDate"
                end-placeholder="结束时间" format="YYYY-MM" range-separator="-" :clearable="false" start-placeholder="开始时间"
                type="monthrange" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :xl="6" v-if="formInline.dateType === 3">
            <el-form-item label="选择时间：">
              <el-date-picker v-model="formInline.dateTimeDate" :disabled-date="pickerOptions.disabledDate"
                end-placeholder="结束时间" format="YYYY" range-separator="-" :clearable="false" start-placeholder="开始时间"
                type="yearrange" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :md="6" :sm="12" :xl="6" :xs="24">
            <div style="display: flex;width: 100%; justify-content: center; align-items: center;">
              <el-button :icon="Search" type="primary" @click="getEnergyStorageStatistics">查询</el-button>
              <el-button :icon="Download" @click="importenergy">下载</el-button>
            </div>
          </el-col>
        </el-row>

      </el-form>
    </div>
    <div class="app_content_table" v-loading="loading">

      <div class="chargingDischargeChart">
        <div class="flex-ai-center">
          <div class="line"></div>
          充放电量
        </div>
        <div class="tree-chart">
          <div v-if="tableArray?.dateList?.length === 0" class="flex-jc-ai-center null-data" style="margin-top:6%;">
            <el-empty :image="emptyImg" description="暂无数据" />
          </div>
          <stackEcharts v-else style="width:100%; height:100%" :list="listArray"></stackEcharts>
        </div>

      </div>
      <div class="chargingDischargedetails">
        <div class="chargingCost">
          <div class="chargingCost-tip">
            <div class="chargingSumName">充电电量</div>
            <div class="chargingSumValue"><span style="color: #15FFD4;padding-right: 10px;">{{
              $filters.formatNumber(tableArray?.chargeQt || 0.00) }}</span>kWh</div>


          </div>
          <div class="chargingCost-line"></div>
          <div class="chargingCost-value">
            <div class="chargingCost-value-item">

              <div class="chargingCost-value-item-name" style="color:#FF6262;"><img src="./image/jian.png" alt="">尖
              </div>
              <div>{{ $filters.formatNumber(tableArray?.topChargeQt || 0.00) }} kWh</div>

            </div>
            <div class="chargingCost-value-item">

              <div class="chargingCost-value-item-name" style="color:#FFBA4C;"> <img src="./image/feng.png" alt="">峰
              </div>
              <div>{{ $filters.formatNumber(tableArray?.peakChargeQt || 0.00) }} kWh</div>

            </div>
            <div class="chargingCost-value-item">
              <div class="chargingCost-value-item-name" style="color:#00D4FF;"> <img src="./image/ping.png" alt="">平
              </div>
              <div>{{ $filters.formatNumber(tableArray?.plainChargeQt || 0.00) }} kWh</div>


            </div>
            <div class="chargingCost-value-item">
              <div class="chargingCost-value-item-name" style="color:#00FF73;"> <img src="./image/gu.png" alt="">谷</div>
              <div>{{ $filters.formatNumber(tableArray?.valleyChargeQt || 0.00) }} kWh</div>

            </div>
            <div class="chargingCost-value-item">

              <div class="chargingCost-value-item-name" style="color:#9590FF;"> <img src="./image/shen.png" alt="">深
              </div>
              <div>{{ $filters.formatNumber(tableArray?.deepChargeQt || 0.00) }} kWh</div>
            </div>
          </div>
        </div>
        <div class="chargingCost" style="margin-top: 20px;">

          <div class="chargingCost-tip">
            <div class="chargingSumName">放电电量</div>
            <div class="chargingSumValue"><span style="color: #15FFD4;padding-right: 10px;">{{
              $filters.formatNumber(tableArray?.dischargeQt || 0.00) }}</span>kWh</div>

          </div>
          <div class="chargingCost-line"></div>
          <div class="chargingCost-value">
            <div class="chargingCost-value-item">

              <div class="chargingCost-value-item-name" style="color:#FF6262;"><img src="./image/jian.png" alt="">尖
              </div>
              <div>{{ $filters.formatNumber(tableArray?.topDischargeQt || 0.00) }} kWh</div>

            </div>
            <div class="chargingCost-value-item">

              <div class="chargingCost-value-item-name" style="color:#FFBA4C;"> <img src="./image/feng.png" alt="">峰
              </div>
              <div>{{ $filters.formatNumber(tableArray?.peakDischargeQt || 0.00) }} kWh</div>



            </div>
            <div class="chargingCost-value-item">
              <div class="chargingCost-value-item-name" style="color:#00D4FF;"> <img src="./image/ping.png" alt="">平
              </div>
              <div>{{ $filters.formatNumber(tableArray?.plainDischargeQt || 0.00) }} kWh</div>


            </div>
            <div class="chargingCost-value-item">

              <div class="chargingCost-value-item-name" style="color:#00FF73;"> <img src="./image/gu.png" alt="">谷</div>
              <div>{{ $filters.formatNumber(tableArray?.valleyDischargeQt || 0.00) }} kWh</div>



            </div>
            <div class="chargingCost-value-item">

              <div class="chargingCost-value-item-name" style="color:#9590FF;"> <img src="./image/shen.png" alt="">深
              </div>
              <div>{{ $filters.formatNumber(tableArray?.deepDischargeQt || 0.00) }} kWh</div>
            </div>
          </div>
        </div>

      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from "vue";
import { getMeterListBySiteId, countStorageQt } from "@/api/operationManagement/EnergyStorageStatistics";
import { exportCustomExcel } from "@/common/exportExcel";
import { findSiteListByUserId } from "@/api/centralMonitoring/centralMonitoring";
import {
  getNowDate, pickerOptionsGthanAcTime, getYear, getMonth, getCurrentYearLastDay,
  getCurrentMonthLastDay, isMonth, isYear, isToday, getCurrentMonthFirstDay, getCurrentYearFirstDay, getDaysFromCurrentTime
} from "@/utils/dateTime";

import TabBackground from "@/components/Tabs/TabBackground.vue";
import { RefreshRight, Search, Download } from "@element-plus/icons-vue";
import * as echarts from "echarts";
import stackEcharts from './stackEcharts.vue';

const siteId = ref("");
const siteList = ref([]);
const siteName = ref("");
const formInline = ref({
  dateType: 1,
  dateTimeDate: [getDaysFromCurrentTime(-29), getDaysFromCurrentTime(0)],
});

import emptyImg from "@/assets/image/empty.png";
const exportTableHeader = ref([
  { width: 30, key: "dateList", name: "时间日期" },
  { width: 20, key: "topDischargeQtList", name: "放电（尖）" },
  { width: 15, key: "peakDischargeQtList", name: "放电（峰）" },
  { width: 20, key: "plainDischargeQtList", name: "放电（平）", },
  { width: 25, key: "valleyDischargeQtList", name: "放电（谷）" },
  { width: 20, key: "deepDischargeQtList", name: "放电（深谷）" },
  { width: 20, key: "topChargeQtList", name: "充电（尖）" },
  { width: 20, key: "peakChargeQtList", name: "充电（峰）" },
  { width: 20, key: "plainChargeQtList", name: "充电（平）" },
  { width: 20, key: "valleyChargeQtList", name: "充电（谷）" },
  { width: 20, key: "deepChargeQtList", name: "充电（深谷）" },
])
const props = defineProps({
  siteArray: {
    type: Array,
    default: () => {
      return {};
    },
  },
  siteId: {
    type: String,
    default: () => {
      return "";
    },
  },

})
const loading = ref(false)

watch(() => props.siteArray, (newVal, oldVal) => {

  if (newVal) {
    siteId.value = newVal.id;
    siteName.value = newVal.siteName;
    getEnergyStorageStatistics();
    getMeterList();
  }
}, { deep: true }
);

const MeterList = ref([]);
const listArray = ref({
  xAxisData: [],
  yAxisData: []
});
const pickerOptions = pickerOptionsGthanAcTime(1);
const timeTypeArray = ref([
  {
    id: 1,
    name: "日",
  },
  {
    id: 2,
    name: "月",
  },
  {
    id: 3,
    name: "年",
  },
]);

const tableArray = ref(null)
watch(() => formInline.value.dateType, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    if (newVal === 1) {
      formInline.value.dateTimeDate = [getDaysFromCurrentTime(-29), getDaysFromCurrentTime(0)];
    }
    else if (newVal === 2) {
      formInline.value.dateTimeDate = [getCurrentMonthFirstDay(getDaysFromCurrentTime(-335)), getDaysFromCurrentTime(0)];
    }
    else if (newVal === 3) {
      formInline.value.dateTimeDate = [getCurrentYearFirstDay(getDaysFromCurrentTime(-722)), getDaysFromCurrentTime(0)];

    }

  }
})
// 将对象转换为对象数组
const convertToTableArray = () => {
  const result = [];
  const dates = tableArray.value.dateList;

  for (let i = 0; i < dates.length; i++) {
    result.push({
      dateList: dates[i],
      topDischargeQtList: tableArray.value.topDischargeQtList[i],
      peakDischargeQtList: tableArray.value.peakDischargeQtList[i],
      plainDischargeQtList: tableArray.value.plainDischargeQtList[i],
      valleyDischargeQtList: tableArray.value.valleyDischargeQtList[i],
      deepDischargeQtList: tableArray.value.deepDischargeQtList[i],
      topChargeQtList: tableArray.value.topChargeQtList[i],
      peakChargeQtList: tableArray.value.peakChargeQtList[i],
      plainChargeQtList: tableArray.value.plainChargeQtList[i],
      valleyChargeQtList: tableArray.value.valleyChargeQtList[i],
      deepChargeQtList: tableArray.value.deepChargeQtList[i],
    });
  }

  return result;
};
// 下载
const importenergy = () => {
  let recordDataList = convertToTableArray()
  exportCustomExcel(
    exportTableHeader.value,
    recordDataList,
    `${siteName.value} - 电量${formInline.value.dateTimeDate[0]}至${formInline.value.dateTimeDate[1]}`
  );
}
// const getMeterList = async () => {

//   const res = await getMeterListBySiteId({ siteId: siteId.value });
//   MeterList.value = res.data
//   getEnergyStorageStatistics(); 
// }
let isFetching = false;
let retryCount = 0;

const getMeterList = async () => {
  if (isFetching) return;
  isFetching = true;
  loading.value = true;

  try {
    const res = await getMeterListBySiteId({ siteId: siteId.value });
    MeterList.value = res.data;
    getEnergyStorageStatistics();
  } catch (err) {
    console.error('获取电表列表失败:', err);
    if (retryCount < 2) {
      retryCount++;
      setTimeout(() => {
        getMeterList(); // 递归重试
      }, 1000);
    }
  } finally {
    isFetching = false;
    loading.value = false;
  }
};// 获取当前数据
const getEnergyStorageStatistics = async () => {
  loading.value = true

  if (formInline.value.MeterListId) {
    formInline.value.queryType = '2';
    formInline.value.dataId = formInline.value.MeterListId;
  } else {
    formInline.value.queryType = '1';
    formInline.value.dataId = siteId.value;
  }
  if (formInline.value.dateType === 1) {
    formInline.value.startTime = formInline.value.dateTimeDate[0];
    formInline.value.endTime = formInline.value.dateTimeDate[1];
  }

  if (formInline.value.dateType === 2) {
    formInline.value.startTime = getCurrentMonthFirstDay(formInline.value.dateTimeDate[0]);
    formInline.value.endTime = getCurrentMonthLastDay(formInline.value.dateTimeDate[1]);
    console.log(formInline.value, formInline.value.startTime, formInline.value.endTime);
  }

  if (formInline.value.dateType === 3) {
    formInline.value.startTime = getCurrentYearFirstDay(formInline.value.dateTimeDate[0]);
    formInline.value.endTime = getCurrentYearLastDay(formInline.value.dateTimeDate[1]);
  }
  const res = await countStorageQt({
    queryType: formInline.value.queryType,
    dataId: formInline.value.dataId,
    startDate: formInline.value.startTime,
    endDate: formInline.value.endTime,
    dateType: formInline.value.dateType,
  });
  loading.value = false

  tableArray.value = res.data
  listArray.value = {
    xAxisData: res.data.dateList,
    yAxisData: [
      {
        name: '充电（尖）',
        data: res.data.topChargeQtList,
        stack: 'charge',
        color: '#3FD73B'

      }, {
        name: '充电（峰）',
        data: res.data.peakChargeQtList,

        stack: 'charge',
        color: '#71DF66'
      }, {
        name: '充电（平）',
        data: res.data.plainChargeQtList,

        stack: 'charge',
        color: '#99E78F'
      }, {
        name: '充电（谷）',
        data: res.data.valleyChargeQtList,

        stack: 'charge',
        color: '#BEEFB7'
      }, {
        name: '充电（深）',
        data: res.data.deepChargeQtList,
        stack: 'charge',
        color: '#DFF7DC'
      }, {
        name: '放电（尖）',
        data: res.data.topDischargeQtList,
        stack: 'discharge',
        color: '#3496FF'
      }, {
        name: '放电（峰）',
        data: res.data.peakDischargeQtList,
        stack: 'discharge',
        color: '#63ABFF'

      }, {
        name: '放电（平）',
        data: res.data.plainDischargeQtList,
        stack: 'discharge',
        color: '#8CC0FF'

      }, {
        name: '放电（谷）',
        data: res.data.valleyDischargeQtList,
        stack: 'discharge',
        color: '#B2D5FF'

      }, {
        name: '放电（深）',
        data: res.data.deepDischargeQtList,
        stack: 'discharge',
        color: '#D9EBFF'

      }
    ]
  }
};



onMounted(() => {
  if (props.siteArray?.id) {
    console.log('qiehua', props.siteArray);
    siteId.value = props.siteArray.id;
    siteName.value = props.siteArray.siteName;
    getMeterList();
  }
});

</script>

<style lang="scss" scoped>
.app-container-right {
  position: relative;

  .app_top_left {
    width: 20%;
    position: absolute;
    top: -45px;
    right: 0
  }

  .app_content_table {
    border: 1px solid rgba(16, 110, 196, 0.6);
    padding: 21px 34px;
    box-sizing: border-box;
    width: 100%;
    height: calc(100% - 120px);
    display: flex;
    align-items: center;
    justify-content: center;

    .chargingDischargeChart {
      width: 75%;
      height: 100%;
      font-size: 15px;
      font-family: Agency FB, Agency FB;
      color: #ffffff;

      .line {
        width: 2px;
        height: 15px;
        background-color: #4ADBEB;
        margin-right: 10px;
      }

      .tree-chart {
        width: 100%;
        height: 100%;
      }

    }

    .chargingDischargedetails {
      flex: 1;
      height: 100%;
      box-sizing: border-box;

      .chargingCost {
        width: 100%;
        box-sizing: border-box;
        height: 48%;
        color: #fff;
        background: rgba(0, 95, 155, 0.15);

        .chargingCost-tip {
          padding: 15px 29px;
          font-size: 16px;
          font-family: Microsoft YaHei, Microsoft YaHei;
          color: #ffffff;
          font-weight: 600;
          display: flex;
          align-items: center;
          justify-content: space-between;

          .chargingSumName {

            font-size: 18px;
          }
        }

        .chargingCost-line {
          height: 1px;
          width: 100%;
          background-color: rgba(255, 255, 255, 0.15);

        }

        .chargingCost-value {
          padding: 15px 29px;
          font-size: 16px;
          font-family: Microsoft YaHei, Microsoft YaHei;
          color: #ffffff;
          font-weight: 600;
          height: calc(100% - 80px);


          /* 确保父容器有高度 */
          .chargingCost-value-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            height: 20%;

            .chargingCost-value-item-name {
              display: flex;
              align-items: center;
              font-weight: 700;
              font-family: Agency FB, Agency FB;

              img {
                margin-right: 15px;
              }

            }
          }
        }
      }
    }
  }

}

:deep(.custom-tooltip-box) {
  padding: 0 !important;
  border: none !important;
  background-color: transparent !important;

  // 给子盒子自定义样式
  .custom-tooltip-style {

    background: rgba(0, 47, 78, 0.9);
    box-shadow: inset 0px 0px 8px 1px #00ccff;
    border-radius: 3px 3px 3px 3px;
    padding: 5px 15px;

    .custom-tooltip-title {
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      font-size: 14px;
      color: #ffffff;
      text-align: left;
      font-style: normal;
    }

    .custom-tooltip-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }


    .custom-radio {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      display: inline-block;
      margin-right: 5px;
      box-sizing: border-box;

    }



    .custom-tooltip-value {
      font-family: Microsoft YaHei, Microsoft YaHei;
      font-weight: 400;
      margin-left: 20px;
      font-size: 14px;
      color: #ffffff;
      text-align: left;
      font-style: normal;
    }
  }
}
</style>
