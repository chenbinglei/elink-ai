<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <el-form :model="formInline" inline>
        <el-row :gutter="16">
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="选择站点：">
              <el-select v-model="formInline.siteIds" clearable collapse-tags filterable max-collapse-tags="1" multiple placeholder="全部">
                <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="时间维度：">
              <TabBackground v-model:tabs-index="formInline.dateType" :tabsArray="timeTypeArray"></TabBackground>
            </el-form-item>
          </el-col>
          <el-col v-if="formInline.dateType === 1" :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="选择时间：">
              <el-date-picker v-model="formInline.monthTimeDate" :clearable="false" :disabled-date="pickerOptions.disabledDate" format="YYYY-MM"
                              placeholder="请选择时间" type="month" value-format="YYYY-MM"/>
            </el-form-item>
          </el-col>
          <el-col v-if="formInline.dateType === 2" :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="选择时间：">
              <el-date-picker v-model="formInline.yearTimeDate" :clearable="false" :disabled-date="pickerOptions.disabledDate" format="YYYY"
                              placeholder="请选择时间" type="year" value-format="YYYY"/>
            </el-form-item>
          </el-col>
          <el-col v-if="formInline.dateType === 3" :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item label="选择时间：">
              <el-date-picker v-model="formInline.yearRangeTimeDate" :disabled-date="pickerOptions.disabledDate" end-placeholder="结束时间" format="YYYY"
                              range-separator="~" :clearable="false" start-placeholder="开始时间" type="yearrange" value-format="YYYY"/>
            </el-form-item>
          </el-col>
          <el-col :md="8" :sm="12" :xl="6" :xs="24">
            <el-form-item>
              <el-button :icon="Search" type="primary" @click="queryCountPvOperationAnalysis('refresh')">查询</el-button>
              <el-button :icon="RefreshRight" @click="clickResetForm">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div ref="tableContentRef" class="tableContent" >
      <div v-loading="listLoading">
        <StatisticsTotalDataInfo :returnDataInfo="returnDataInfo"></StatisticsTotalDataInfo>
        <el-row :gutter="16">
          <template v-for="(item,index) in list" :key="index">
            <el-col :md="12" :sm="24" :xs="24">
              <div class="content_border card_list">
                <component :is="item.componentName" :titleName="item.name" :boundaryGap="item.boundaryGap" :unit="item.unit"
                           :seriesListArray="item.seriesListArray" :returnDataInfo="returnDataInfo"></component>
              </div>
            </el-col>
          </template>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
import {ElMessage} from "element-plus";
import {colorHexTurnRgba} from "@/utils";
import {RefreshRight, Search} from "@element-plus/icons-vue";
import TabBackground from "@/components/Tabs/TabBackground.vue";
import {onMounted, reactive, toRefs, defineComponent} from "vue";
import LossElectricityCom from "./PvOperationsAnalysis/PublicCardChartCom.vue";
import SystemEfficiencyCom from "./PvOperationsAnalysis/PublicCardChartCom.vue";
import {findSiteInfoByUserId} from "@/api/operationManagement/CsStationManagement";
import ElectricityStatisticsCom from "./PvOperationsAnalysis/PublicCardChartCom.vue";
import SocialContributionCom from "./PvOperationsAnalysis/SocialContributionCom.vue";
import EquivalentPowerDurationCom from "./PvOperationsAnalysis/PublicCardChartCom.vue";
import PowerStationRankingCom from "./PvOperationsAnalysis/PowerStationRankingCom.vue";
import {countPvOperationAnalysis} from "@/api/operationManagement/PvOperationsAnalysis";
import StatisticsTotalDataInfo from "./PvOperationsAnalysis/StatisticsTotalDataInfo.vue";
import {getCurrentMonthFirstDay, getCurrentMonthLastDay, getCurrentYearFirstDay, getCurrentYearLastDay, getDaysFromCurrentTime, getTheDayBeforeDate, isMonth, isYear,
  pickerOptionsGthanAcTime} from "@/utils/dateTime";

export default defineComponent({
  name: "PvOperationsAnalysis",
  components: {TabBackground, StatisticsTotalDataInfo, LossElectricityCom, SystemEfficiencyCom, SocialContributionCom, PowerStationRankingCom,
    ElectricityStatisticsCom, EquivalentPowerDurationCom},
  setup() {
    const that = reactive({
      Search,
      RefreshRight,
      siteIdArray: [],
      oldFormInline: {},
      returnDataInfo: {},
      listLoading: false,

      formInline: {
        dateType: 1,  // 时间类型 1-逐日 2-逐月 3-生命周期
        yearTimeDate: getDaysFromCurrentTime(0, 2),
        monthTimeDate: getDaysFromCurrentTime(0, 1),
        yearRangeTimeDate: [getDaysFromCurrentTime(-722, 2), getDaysFromCurrentTime(0, 2)],
      },

      siteAllIds: [], // 全部站点ids
      pickerOptions: pickerOptionsGthanAcTime(),
      timeTypeArray: [{id: 1, name: "月"}, {id: 2, name: "年"}, {id: 3, name: "生命周期"}],
      list: [
        {
          unit: "kWh",
          name: "发电量统计",
          boundaryGap: true,
          componentName: "ElectricityStatisticsCom",
          seriesListArray:[
            {
              data: [],
              type: 'bar',
              name: '发电量',
              barMaxWidth: 12,
              color: "#75F7FDFF",
              fieldName: 'generationList',
            },
          ]
        },
        {
          unit: "%",
          name: "系统效率PR",
          componentName: "SystemEfficiencyCom",
          seriesListArray:[
            {
              data: [],
              type: 'line',
              name: '系统效率',
              // showSymbol: false,
              color: "#75F7FDFF",
              fieldName: 'prList',
              areaStyle:{
                color: {
                  type: "linear", x: 0, y: 0, x2: 0, y2: 1,
                  colorStops: [
                    {offset: 0, color: colorHexTurnRgba("#75F7FDFF",0.5)},
                    {offset: 1, color: colorHexTurnRgba("#75F7FDFF",0.1)}
                  ],
                }
              }
            },
          ]
        },
        {
          unit: "kWh",
          name: "损失电量",
          boundaryGap: true,
          componentName: "LossElectricityCom",
          seriesListArray:[
            {
              data: [],
              type: 'bar',
              name: '损失电量',
              barMaxWidth: 12,
              color: "#747FFCFF",
              fieldName: 'lossGenerationList',
            },
          ]
        },
        {
          unit: "h",
          name: "等效发电时长",
          componentName: "EquivalentPowerDurationCom",
          seriesListArray:[
            {
              data: [],
              type: 'line',
              name: '等效发电时长',
              // showSymbol: false,
              color: "#FAC859FF",
              fieldName: 'generationTimeList',
              areaStyle:{
                color: {
                  type: "linear", x: 0, y: 0, x2: 0, y2: 1,
                  colorStops: [
                    {offset: 0, color: colorHexTurnRgba("#FAC859FF",0.5)},
                    {offset: 1, color: colorHexTurnRgba("#FAC859FF",0.1)}
                  ],
                }
              }
            },
          ]
        },
        {name: "电站排名（等效发电时长）", componentName: "PowerStationRankingCom"},
        {name: "社会贡献", componentName: "SocialContributionCom"},
      ]
    });

    // 统计光伏运营运行分析数据
    const queryCountPvOperationAnalysis = () => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));

      if(formInline.dateType === 1){
        formInline['startDate'] = getCurrentMonthFirstDay(formInline.monthTimeDate);
        formInline['endDate'] = getCurrentMonthLastDay(formInline.monthTimeDate);
        // 如果为当年当月的话，获取当前时间 的 前一天
        if(isMonth(formInline.monthTimeDate)) formInline['endDate'] = getTheDayBeforeDate();
        delete formInline.monthTimeDate;
      }

      if(formInline.dateType === 2){
        formInline['startDate'] = getCurrentYearFirstDay(formInline.yearTimeDate);
        formInline['endDate'] = getCurrentYearLastDay(formInline.yearTimeDate);
        if(isYear(formInline.yearTimeDate)) formInline['endDate'] = getTheDayBeforeDate();
        delete formInline.yearTimeDate;
      }

      if(formInline.dateType === 3){
        formInline['startDate'] = getCurrentYearFirstDay(formInline.yearRangeTimeDate[0]);
        formInline['endDate'] = getCurrentYearLastDay(formInline.yearRangeTimeDate[1]);
        if(isYear(formInline.yearRangeTimeDate[1])) formInline['endDate'] = getTheDayBeforeDate();
        delete formInline.yearRangeTimeDate;
      }

      let siteIds = JSON.parse(JSON.stringify(formInline.siteIds ?? []));
      if(!siteIds || !siteIds.length){
        if(!that.siteAllIds || !that.siteAllIds.length){
          that.returnDataInfo = {};
          that.listLoading = false;
          ElMessage({type: "error", showClose: true, message: "请先选择查询的站点！"});
          return;
        }
        siteIds = JSON.parse(JSON.stringify(that.siteAllIds));
      }

      delete formInline.siteIds; // 删除筛选表单里的siteIds
      // console.log(formInline);
      countPvOperationAnalysis({ ...formInline, siteIds }).then(res=>{
        that.returnDataInfo = res.data ? res.data : {};
        that.listLoading = false;
      }).catch(()=>{
        that.returnDataInfo = {};
        that.listLoading = false;
      });
    };

    const clickResetForm = (operateType = "resetPage") => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      if (!that.siteAllIds.length) querySiteBasicInfoByTenantId();
      if (that.siteAllIds.length) queryCountPvOperationAnalysis(operateType);
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({scenarioTypes: 1, timer: new Date()}).then(res => {
        let siteAllIds = [];
        let list = res.data ? res.data : [];
        for (let i = 0; i < list.length; i++) siteAllIds.push(list[i].id); // 站点id
        that.siteIdArray = JSON.parse(JSON.stringify(list));
        that.siteAllIds = JSON.parse(JSON.stringify(siteAllIds));
        if (that.siteAllIds.length) queryCountPvOperationAnalysis(); // 有站点id 就获取报表数据
      });
    };

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      querySiteBasicInfoByTenantId();
    });

    return {...toRefs(that), queryCountPvOperationAnalysis, clickResetForm, querySiteBasicInfoByTenantId};
  }
});
</script>

<style lang="scss" scoped>
:deep(.tabBackground) {
  .content_left {
    width: 100%;

    .but_list_li {
      flex: 1;
      height: 32px;
      padding: 8px 0;
    }
  }
}

.card_list {
  height: 380px;
  margin-bottom: 16px;
  padding: 18px 16px;
  box-sizing: border-box;
}
</style>