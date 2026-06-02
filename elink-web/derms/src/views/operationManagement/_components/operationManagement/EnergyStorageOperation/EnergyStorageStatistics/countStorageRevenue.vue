<template>
  <div class="app-container-right">
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
                end-placeholder="结束时间" range-separator="~" start-placeholder="开始时间" type="daterange" />
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
              <el-button :icon="Search" type="primary" @click="getEnergyStorageStatistics()">查询</el-button>
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
          度电收益
        </div>
        <div class="tree-chart">
          <div v-if="tableArray?.dateList?.length === 0" class="flex-jc-ai-center null-data" style="margin-top:6%;">
            <el-empty :image="emptyImg" description="暂无数据" />
          </div>
          <barChart v-else style="width:100%; height:100%" :list="list" :type="'bar'"></barChart>
        </div>
      </div>
      <div class="chargingDischargedetails">
        <div class="chargingCost">
          <div class="chargingCost-value-item">
            <div class="chargingCost-value-item-name">充电量</div>
            <div><span style="color: #15FFD4;padding-right: 10px;">{{ $filters.formatNumber(tableArray?.chargeQt, 2) || 0.00 }}</span> kWh</div></div>
          <div class="chargingCost-value-item">
            <div class="chargingCost-value-item-name">充电成本</div>
            <div><span style="color: #15FFD4;padding-right: 10px;">{{ $filters.formatNumber(tableArray?.chargeMoney, 2) || 0.00 }}</span> 元</div>
          </div>
        </div>
        <div class="chargingCost" style="margin-top: 20px;">
          <div class="chargingCost-value-item">
            <div class="chargingCost-value-item-name">放电量</div>
            <div><span style="color: #15FFD4;padding-right: 10px;">{{ $filters.formatNumber(tableArray?.dischargeQt, 2) || 0.00 }}</span> kWh</div></div>
          <div class="chargingCost-value-item">
            <div class="chargingCost-value-item-name">放电收入</div>
            <div><span style="color: #15FFD4;padding-right: 10px;">{{ $filters.formatNumber(tableArray?.dischargeMoney, 2) || 0.00 }}</span> 元</div>
          </div>
        </div>
         <div class="chargingCost" style="margin-top: 20px;">
          <div class="chargingCost-value-item">
            <div class="chargingCost-value-item-name">合计收益</div>
            <div><span style="color: #15FFD4;padding-right: 10px;">{{ $filters.formatNumber(tableArray?.totalIncome, 2) || 0.00 }}</span> 元</div></div>
          <div class="chargingCost-value-item">
            <div class="chargingCost-value-item-name">合计度电收益</div>
            <div><span style="color: #15FFD4;padding-right: 10px;">{{ $filters.formatNumber(tableArray?.totalKwhIncome, 2) || 0.00 }}</span> 元 / kWh</div>
          </div>
        </div>

      </div>

    </div>
  </div>
</template>

<script setup>
import emptyImg from "@/assets/image/empty.png";
import { ref, watch, onMounted, onUnmounted } from "vue";
import { getMeterListBySiteId, countStorageRevenue } from "@/api/operationManagement/EnergyStorageStatistics";
import {
  getNowDate, pickerOptionsGthanAcTime, getYear, getMonth, getCurrentYearLastDay,
  getCurrentMonthLastDay, isMonth, isYear, isToday, getCurrentMonthFirstDay, getCurrentYearFirstDay, getDaysFromCurrentTime
} from "@/utils/dateTime";import TabBackground from "@/components/Tabs/TabBackground.vue";
import { RefreshRight, Search, Download } from "@element-plus/icons-vue";
import * as echarts from "echarts";
import BarChart from './BarChart.vue';
const siteId = ref("");
const siteName = ref("");
const siteList = ref([]);
const formInline = ref({
  dateType: 1,
  dateTimeDate: [getDaysFromCurrentTime(-29), getDaysFromCurrentTime(0)],
});
import { exportCustomExcel } from "@/common/exportExcel";
const MeterList = ref([]);
const pickerOptions = pickerOptionsGthanAcTime(1);
const loading=ref(false)
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
const exportTableHeader = ref([
  { width: 80, key: "dateList", name: "时间日期" },
  { width: 80, key: "kwhIncomeList", name: "度电收益" },
  // { width: 80, key: "dischargeMoneyList", name: "放电收益" },
  // { width: 80, key: "incomeList", name: "逐日收益", },
  // { width: 80, key: "totalIncomeList", name: "累计收益" },
])
const props = defineProps({
  siteArray: {
    type: Object,
    default: () => ({})
  },
  siteId: {
    type: String,
    default: () => {
      return "";
    },
  },
})
const tableArray = ref(null)
const list = ref({})
// 将对象转换为对象数组
const convertToTableArray = () => {
  const result = [];
  const dates = tableArray.value.dateList;

  for (let i = 0; i < dates.length; i++) {
    result.push({
      dateList: dates[i],
      kwhIncomeList: tableArray.value.kwhIncomeList[i],
      // dischargeMoneyList: tableArray.value.dischargeMoneyList[i],
      // incomeList: tableArray.value.incomeList[i],
      // totalIncomeList: tableArray.value.totalIncomeList[i],
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
    `${siteName.value} - 度电收益${formInline.value.dateTimeDate[0]}至${formInline.value.dateTimeDate[1]}`
  );
}
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


watch(() => props.siteArray, (newVal, oldVal) => {
  if (newVal) {
    siteId.value = newVal.id;
    siteName.value = newVal.siteName;
    getMeterList();
  }
}, { deep: true }
);
// 获取当前数据
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
    console.log(formInline.value,formInline.value.startTime, formInline.value.endTime);
  }

  if (formInline.value.dateType === 3) {
    formInline.value.startTime = getCurrentYearFirstDay(formInline.value.dateTimeDate[0]);
    formInline.value.endTime = getCurrentYearLastDay(formInline.value.dateTimeDate[1]);
  }
  const res = await countStorageRevenue({
   queryType: formInline.value.queryType,
    dataId: formInline.value.dataId,
    startDate: formInline.value.startTime,
    endDate: formInline.value.endTime,
    dateType: formInline.value.dateType,
  });
    loading.value = false
  tableArray.value = res.data
  list.value = {
    xAxisData: res.data.dateList,
    seriesData: res.data.kwhIncomeList,
  }

};

const getMeterList = async () => {
  const res = await getMeterListBySiteId({ siteId: siteId.value });
  MeterList.value = res.data,
    getEnergyStorageStatistics();
};

onMounted(() => {

  if (props.siteArray?.id) {
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
        color: #fff;
        // height: 30%;
        background: rgba(0, 95, 155, 0.15);
        .chargingCost-value-item{
          display: flex;
          padding: 5% 30px;
          justify-content: space-between;
        }
        .chargingCost-value {
          padding: 15px 29px;
          font-size: 16px;
          font-family: Microsoft YaHei, Microsoft YaHei;
          color: #ffffff;
          // font-weight: 600;
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
