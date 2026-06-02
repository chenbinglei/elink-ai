<template>
  <div class="app-container-right">
    <div class="containers">
      <div class="content-left">
        <div class="content-top">
          <div class="station-name-change flex-ai-center">
            <div>{{ stationName ? stationName : "--" }}</div>
            <el-dropdown @command="changeSiteStation" class="custom-trigger">
              <div class="ml-5 pt-2" style="width: 50px; height: 50px">
                <img src="@/assets/image/station-details/change-station.png" width="50" alt="" />
              </div>
              <template #dropdown>
                <el-dropdown-menu class="custom-dropdown-menu">
                  <el-input style="padding:10px" v-model="searchText" placeholder="请输入电站名称" clearable
                    @keyup.enter="querySiteListByUserIdList"></el-input>
                  <el-dropdown-item v-for="(item, index) in stationList" :key="index + 'stationList'"
                    :command="index">{{
                      item.siteName }}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <div class="station-info-title">
            <div class="flex-ai-center">
              <div class="line"></div>
              收益测算模型
            </div>
            <div class="flex-ai-center" style="color: #038ACD; cursor: pointer;"
              @click="showSupplementaryInformation()">
              <el-icon size="16" color="#038ACD">
                <Plus />
              </el-icon>
              补充信息
            </div>
          </div>
          <div class="contenti-list">
            <div class="content-list-label">收益模型：</div>
            <div class="content-list-value">{{ chargeAnalysis.incomeModelId ? chargeAnalysis.incomeModelId : "工商业V2G" }}
            </div>
            <div class="content-list-unit"></div>
          </div>
          <div class="contenti-list">
            <div class="content-list-label">设备总成本</div>
            <div class="content-list-value">
              <!-- {{ chargeAnalysis.deviceCost ? chargeAnalysis.deviceCost : "0" }} -->
              {{ formatNumber(chargeAnalysis.deviceCost) }}
            </div>
            <div class="content-list-unit">元</div>
          </div>
          <div class="contenti-list">
            <div class="content-list-label">施工总成本</div>
            <div class="content-list-value">
              <!-- {{ chargeAnalysis.constructionCost ? chargeAnalysis.constructionCost : "0"
            }} -->
              {{ formatNumber(chargeAnalysis.constructionCost) }}
            </div>
            <div class="content-list-unit">元</div>
          </div>
          <div class="contenti-list">
            <div class="content-list-label">运营补贴</div>
            <div class="content-list-value">
              <!-- {{ chargeAnalysis.operationSubsidy ? chargeAnalysis.operationSubsidy : "0"
            }} -->
              {{ formatNumber(chargeAnalysis.operationSubsidy) }}
            </div>
            <div class="content-list-unit">元/度</div>
          </div>
          <div class="contenti-list">
            <div class="content-list-label">建站补贴</div>
            <div class="content-list-value">
              <!-- {{ chargeAnalysis.constructionSubsidy ? chargeAnalysis.constructionSubsidy : "0"
            }} -->
              {{ formatNumber(chargeAnalysis.constructionSubsidy) }}
            </div>
            <div class="content-list-unit">元</div>
          </div>
          <div class="contenti-list">
            <div class="content-list-label">场地租金</div>
            <div class="content-list-value">
              <!-- {{ chargeAnalysis.siteRent ? chargeAnalysis.siteRent : "0"
            }} -->
              {{ formatNumber(chargeAnalysis.siteRent) }}
            </div>
            <div class="content-list-unit">元/月</div>
          </div>
          <div class="contenti-list">
            <div class="content-list-label">运营成本</div>
            <div class="content-list-value">
              <!-- {{ chargeAnalysis.operationCost ? chargeAnalysis.operationCost : "0" }} -->
              {{ formatNumber(chargeAnalysis.operationCost) }}

            </div>
            <div class="content-list-unit">元/月</div>
          </div>
          <div class="contenti-list">
            <div class="content-list-label">运维成本</div>
            <div class="content-list-value">
              <!-- {{ chargeAnalysis.maintainCost ? chargeAnalysis.maintainCost : "0" }} -->
              {{ formatNumber(chargeAnalysis.maintainCost) }}
            </div>
            <div class="content-list-unit">元/月</div>
          </div>
        </div>
        <div class="content-top content-bottom">
          <div class="station-info-title">
            <div class="flex-ai-center">
              <div class="line"></div>
              投资收益概况
            </div>
          </div>
          <!-- <div class="contenti-list">
            <div class="content-list-label">收益模型：</div>
            <div class="content-list-value">{{ siteInvestIncome.incomeModelId ? siteInvestIncome.incomeModelId :
              "工商业V2G"
            }}
            </div>
            <div class="content-list-unit"></div>
          </div> -->
          <div class="contenti-list">
            <div class="content-list-label">累计收益</div>
            <div class="content-list-value">
              <!-- {{ siteInvestIncome.totalIncome ? siteInvestIncome.totalIncome : "0" }} -->
              {{ formatNumber(siteInvestIncome.totalIncome) }}
            </div>

            <div class="content-list-unit">元</div>
          </div>
          <div class="contenti-list">
            <div class="content-list-label">日均收益</div>
            <div class="content-list-value">
              <!-- {{ siteInvestIncome.dailyIncome ? siteInvestIncome.dailyIncome : "0" }} -->
              {{ formatNumber(siteInvestIncome.dailyIncome) }}

            </div>


            <div class="content-list-unit">元/天</div>
          </div>
          <div class="contenti-list">
            <div class="content-list-label">月均收益</div>
            <div class="content-list-value">
              <!-- {{ siteInvestIncome.monthlyIncome ? siteInvestIncome.monthlyIncome : "0" }} -->
              {{ formatNumber(siteInvestIncome.monthlyIncome) }}

            </div>

            <div class="content-list-unit">元/月</div>
          </div>
          <div class="contenti-list">
            <div class="content-list-label">年均收益</div>
            <div class="content-list-value">
              <!-- {{ siteInvestIncome.annualIncome ? siteInvestIncome.annualIncome : "0" }} -->
              {{ formatNumber(siteInvestIncome.annualIncome) }}
            </div>

            <div class="content-list-unit">元/年</div>
          </div>
          <div class="contenti-list">
            <div class="content-list-label">年化收益率</div>
            <div class="content-list-value">
              <!-- {{ siteInvestIncome.annualYield ? siteInvestIncome.annualYield : "0" }} -->
              {{ formatNumber(siteInvestIncome.annualYield) }}

            </div>
            <div class="content-list-unit">%</div>
          </div>
          <div class="contenti-list">
            <div class="content-list-label">投资回收周期</div>
            <div class="content-list-value">
              {{ formatNumber(siteInvestIncome.recoveryPeriod) }}
              <!-- {{ siteInvestIncome.recoveryPeriod ? siteInvestIncome.recoveryPeriod : "0" }} -->
            </div>


            <div class="content-list-unit">年</div>
          </div>
        </div>
      </div>
      <div class="content-right">
        <div class="content-date">
          <div class="content-date-label">
            <TabBackground v-model:tabs-index="dateType" :tabsArray="tabsArray" @click="queryCountOperationDataFun()">
            </TabBackground>

            <div class="date_picker_class" v-if="dateType === 4">
              <el-date-picker v-model="customDateTime" :disabled-date="pickerOptions.disabledDate"
                value-format="YYYY-MM-DD" format="YYYY-MM-DD" :clearable="false" end-placeholder="结束时间"
                range-separator="~" start-placeholder="开始时间" type="daterange" @change="queryCountOperationDataFun" />
            </div>
          </div>
          <div class="content-date-label" style="color:#007FFF;cursor: pointer;" @click="Recalculate()">
            <el-icon size="16" color="#1AFA29">
              <Refresh />
            </el-icon> <span style="margin-left: 10px;">{{ isProcessing ? '正在处理...' : '重算' }}</span>
          </div>

        </div>
        <div class="chart-Topology">
          <div class="chart-box">
            <div class="chart-box-chart">
              <div class="chart-box-top">
                <div class="chart-box-title">经营收益</div>
                <singleEcharts :IncomeDate="IncomeDate" :type="'line'" />
              </div>
              <div class="chart-box-top">
                <div class="chart-box-title">投资回收</div>
                <singleEcharts :IncomeDate="IncomeDate" :type="'linedouble'" />
              </div>

            </div>
          </div>
          <div class="topology">
            <div style="margin: auto;width: 50%;">
              <TabBackground v-model:tabs-index="PowerProfitType" :tabsArray="tabsPowerProfit"></TabBackground>
            </div>
            <div class="PowerProfitStatistics">
              <PowerStatistics :IncomeDate="IncomeDate" v-if="PowerProfitType === 1" />
              <ProfitStatistics :IncomeDate="IncomeDate" v-else />

            </div>

          </div>
        </div>
      </div>
    </div>
    <dialogModel v-if="showModel" v-model:isVisible="showModel" :stationSelect="stationSelect"
      :chargeAnalysis="chargeAnalysis" @updata="updata"> </dialogModel>


  </div>
</template>

<script setup>
import { Plus, Refresh } from "@element-plus/icons-vue";
import { ref, onMounted } from 'vue'
import { findSiteIncomeBySiteId, countSiteInvestIncome, countSiteOperateIncome } from "@/api/operationManagement/CsBusinessAnalysis";
import { findSiteListByUserId } from "@/api/centralMonitoring/centralMonitoring";
import TabBackground from "@/components/Tabs/TabBackground.vue";
import { getCurrentMonthFirstDay, getDaysFromCurrentTime, pickerOptionsGthanAcTime } from "@/utils/dateTime";
import ProfitStatistics from "@/views/operationManagement/_components/operationManagement/ChargingStationOperation/CsBusinessAnalysis/chargeAnalysis/ProfitStatistics.vue";
import PowerStatistics from "@/views/operationManagement/_components/operationManagement/ChargingStationOperation/CsBusinessAnalysis/chargeAnalysis/PowerStatistics.vue";
import dialogModel from "@/views/operationManagement/_components/operationManagement/ChargingStationOperation/CsBusinessAnalysis/chargeAnalysis/SupplementaryInformation.vue";
import singleEcharts from "@/views/operationManagement/_components/operationManagement/ChargingStationOperation/CsBusinessAnalysis/chargeAnalysis/singleEcharts.vue";

const stationList = ref([])
const stationName = ref('')
const stationSelect = ref()
const chargeAnalysis = ref({})
const siteInvestIncome = ref({})
const dateType = ref(1)
const pickerOptions = ref(pickerOptionsGthanAcTime())
const customDateTime = ref([getDaysFromCurrentTime(-31), getDaysFromCurrentTime(-1)])
const tabsArray = ref([
  { id: 1, name: "近7天" },
  { id: 2, name: "近30天" },
  { id: 3, name: "近12个月" },
  { id: 4, name: "自定义" }
]);
const isProcessing = ref(false)
const searchText = ref('')

const IncomeDate = ref({})
const totalIncomeDate = ref({})// 累计收益
const PowerProfitType = ref(1)
const tabsPowerProfit = ref([
  { id: 1, name: "电量统计" },
  { id: 2, name: "投资回收" }
])
const showModel = ref(false)
const Recalculate = () => {
  isProcessing.value = true;
  getCountSiteInvestIncome(stationSelect.value.id);
  // getCountSiteOperateIncome(stationSelect.value.id);
  queryCountOperationDataFun()

}
const showSupplementaryInformation = () => {
  showModel.value = true;
}
const querySiteListByUserIdList = () => {
  findSiteListByUserId({ siteName: searchText.value, })
    .then((res) => {
      stationList.value = res.data ? res.data : [];
    })
    .catch((e) => {
      console.log(e);
    });
}


// 查询场站列表
const querySiteListByUserId = () => {
  findSiteListByUserId({ })
    .then((res) => {
      stationList.value = res.data ? res.data : [];
      stationSelect.value = stationList.value.find((item, index) => index === 0);
      stationName.value = stationSelect.value ? stationSelect.value.siteName : '';
      getFindSiteIncomeBySiteId(stationSelect.value.id);
      getCountSiteInvestIncome(stationSelect.value.id);
      // getCountSiteOperateIncome(stationSelect.value.id);
      queryCountOperationDataFun()


    })
    .catch((e) => {
      console.log(e);
    });
};
// 根据日期统计站点经营收益概况
const getCountSiteOperateIncome = (siteId) => {
  console.log('getCountSiteOperateIncome', dateType.value);
  let obj = {
    siteId,
    startDate: customDateTime.value[0],
    endDate: customDateTime.value[1],
    dateType: dateType.value === 3 ? 2 : 1
  }
  countSiteOperateIncome(obj)
    .then((res) => {
      IncomeDate.value = res.data;
    })
    .catch((e) => {
      console.log(e);
    });
}
// 根据日期获取经营收益
const queryCountOperationDataFun = () => {
  if (dateType.value === 1) {
    customDateTime.value = [getDaysFromCurrentTime(-6), getDaysFromCurrentTime(0)]
  }

  if (dateType.value === 2) {
    customDateTime.value = [getDaysFromCurrentTime(-29), getDaysFromCurrentTime(0)]
  }

  if (dateType.value === 3) {
    customDateTime.value = [getCurrentMonthFirstDay(getDaysFromCurrentTime(-335)), getDaysFromCurrentTime(0)]
  }
  getCountSiteOperateIncome(stationSelect.value.id);
  isProcessing.value = false;
}
// 切换场站
const changeSiteStation = (index) => {
  searchText.value = ''
  let item = stationList.value[index];
  stationSelect.value = item;
  stationName.value = item.siteName;
  getFindSiteIncomeBySiteId(stationSelect.value.id);
  getCountSiteInvestIncome(stationSelect.value.id);
  // getCountSiteOperateIncome(stationSelect.value.id);
  queryCountOperationDataFun()
  querySiteListByUserIdList()
}
// 根据站点id查询站点收益测算数据
const getFindSiteIncomeBySiteId = (siteId) => {
  findSiteIncomeBySiteId({ siteId })
    .then((res) => {
      chargeAnalysis.value = res.data;
    })
    .catch((e) => {
      console.log(e);
    });
}
const formatNumber = (value) => {
  const num = Number(value) || 0;
  return num.toLocaleString(); // 自动添加逗号
}
// 统计站点投资收益概况
const getCountSiteInvestIncome = (siteId) => {
  countSiteInvestIncome({ siteId })
    .then((res) => {
      siteInvestIncome.value = res.data;
      isProcessing.value = false;

    })
    .catch((e) => {
      console.log(e);
    });
}
const updata = () => {
  Recalculate()
  getFindSiteIncomeBySiteId(stationSelect.value.id);

}



onMounted(() => {
  querySiteListByUserId();
});

</script>

<style lang="scss" scoped>
.containers {
  display: flex;
  justify-content: space-between;
  width: 100%;
  height: 100%;
  gap: 15px;
  color: #ffffff;
}

.station-info-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 15px;
  font-family: Agency FB, Agency FB;
  margin-top: 15px;

  .line {
    width: 2px;
    height: 15px;
    background-color: #4ADBEB;
    margin-right: 10px;
  }
}



.content-left {
  width: 20%;
  height: 100%;
  display: flex;
  flex-direction: column;

  .content-top {
    border-radius: 6px;
    box-sizing: border-box;
    // height: 55%;
    border: 1px solid #106ec499;
    padding: 5px 15px 15px;
    flex: 0 0 auto;
    /* 不伸缩，按内容高度 */
  }

  .content-bottom {
    margin-top: 3%;
    padding: 15px;
    flex: 1;
    /* 填充剩余空间 */
  }

  .station-name-change {

    // font-family: zihun35hao-jindianyahei, zihun35hao-jindianyahei;
    // font-family: Agency FB, Agency FB;
    //  font-family: Inter, serif;
    font-family: Microsoft YaHei, Microsoft YaHei;
    font-weight: 300;
    font-size: 18px;
  }

  .contenti-list {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 20px;
    color: #9FA5AE;
    width: 76%;
    font-size: 14px;


    .content-list-label {
      width: 40%;
    }

    .content-list-value {
      color: #00AEFF;
      width: 40%;
      text-align: center;
      font-family: zihun35hao-jindianyahei, zihun35hao-jindianyahei;
      font-weight: 600;
    }

    .content-list-unit {
      width: 20%;
      text-align: right;
    }


  }
}

.content-right {
  flex: 1;
  height: 100%;
  display: flex;
  gap: 20px;
  flex-direction: column;


  .content-date {
    display: flex;
    border-radius: 6px;
    box-sizing: border-box;
    border: 1px solid #106ec499;
    padding: 15px;
    justify-content: space-between;

    .content-date-label {
      display: flex;
      align-items: center;

      .date_picker_class {
        margin-left: 15px;
      }
    }
  }

  .chart-Topology {
    height: calc(100% - 100px);

    flex: 1;

    display: flex;
    gap: 20px;

    .chart-box {
      width: 65%;
      border-radius: 6px;
      box-sizing: border-box;
      border: 1px solid #106ec499;
      padding: 15px;

      .chart-box-chart {
        height: 100%;
        width: 100%;

        .chart-box-top {
          height: 50%;
          width: 100%;
        }


      }
    }

    .topology {
      flex: 1;
      border-radius: 6px;
      box-sizing: border-box;
      border: 1px solid #106ec499;
      padding: 15px;

      .PowerProfitStatistics {
        height: calc(100% - 30px);
        width: 100%;

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

<style>
.custom-trigger {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
}

.custom-dropdown-menu {
  max-height: 300px;
  /* 设置你想要的高度 */
  overflow-y: auto;
  /* 启用垂直滚动 */
}

.custom-trigger:focus {
  outline: none;
}

.el-dropdown,
.el-dropdown * {
  outline: none;
}

/* 强制移除 el-dropdown 悬停和聚焦时的边框 */
.el-dropdown__inner:hover,
.el-dropdown__inner:focus {
  outline: none !important;
  border: none !important;
  box-shadow: none !important;
}
</style>