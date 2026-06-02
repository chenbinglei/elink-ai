<template>
  <div class="app-container-right">
    <div class="header-form content_border">
      <div class="header-form-left">
        <TabBackground v-model:tabs-index="dateType" :tabsArray="tabsArray"></TabBackground>
        <div class="date_picker_class" v-if="dateType === 4">
          <el-date-picker v-model="customDateTime" :disabled-date="pickerOptions.disabledDate" value-format="YYYY-MM-DD" format="YYYY-MM-DD" :clearable="false"
                          end-placeholder="结束时间" range-separator="~" start-placeholder="开始时间" type="daterange" @change="queryCountOperationDataFun"/>
        </div>
      </div>
      <div class="header-form-right">
        <el-select v-model="siteIds" clearable filterable multiple collapse-tags max-collapse-tags="1" placeholder="全部" @change="queryCountOperationDataFun">
          <el-option v-for="item in siteIdArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
        </el-select>
      </div>
    </div>
    <div ref="tableContentRef" class="tableContent" v-loading="listLoading || chartLoading">
      <el-row :gutter="12" v-if="isThereAreSite">
        <template v-for="(item,index) in list" :key="index">
          <el-col :lg="12" :sm="24" :xs="24">
            <div class="content_border content_li_body">
              <TitleView :title="item.name" :isTitleIcon="false">
                <template #content>
                  <RevenueStAndOperatingEfCom ref="revenueStAndOperatingEfComRef" :dateType="dateType" :returnDataInfo="returnDataInfo" :fieldList="item.fieldList"
                                              :returnChartInfo="returnChartInfo" :componentIndex="index" @changeEvent="changeEvent"/>
                </template>
              </TitleView>
            </div>
          </el-col>
        </template>
      </el-row>
      <null-data v-else words="请联系管理员开放站点列表权限！"></null-data>
    </div>
  </div>
</template>

<script>
import {ElMessage} from "element-plus";
import TabBackground from "@/components/Tabs/TabBackground.vue";
import {reactive, toRefs, defineComponent, onMounted, watch} from "vue";
import RevenueStAndOperatingEfCom from "./RevenueStAndOperatingEfCom.vue";
import {findSiteInfoByUserId} from "@/api/operationManagement/CsStationManagement";
import {countOperationCurve, countOperationOverview} from "@/api/operationManagement/CsBusinessAnalysis";
import {getCurrentMonthFirstDay, getDaysBetweenDates, getDaysFromCurrentTime, pickerOptionsGthanAcTime} from "@/utils/dateTime";

export default defineComponent({
  name: "CsOperationOverview",
  components: {TabBackground,RevenueStAndOperatingEfCom},
  setup() {
    const that = reactive({
      types: [], // 曲线数据查询类型
      siteIds: [],
      dateType: 1,
      siteAllIds: [],
      siteIdArray: [],
      listLoading: false,
      returnDataInfo: {},
      chartLoading: false,
      returnChartInfo: {},
      isThereAreSite: false,
      pickerOptions: pickerOptionsGthanAcTime(1),
      customDateTime:[getDaysFromCurrentTime(-31),getDaysFromCurrentTime(-1)],
      tabsArray: [{id: 1, name: "近7天"}, {id: 2, name: "近30天"}, {id: 3, name: "近12个月"}, {id: 4, name: "自定义"}],
      list: [
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
                {type: 'line', name: '订单总金额', showSymbol: false, fieldName: 'sumCost', data: []},
                {type: 'line', name: '充电电费', showSymbol: false, isComputeSum: true, fieldName: 'chargeFee', data: []},
                {type: 'line', name: '充电服务费', showSymbol: false, isComputeSum: true, fieldName: 'chargeServiceFee', data: []},
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
                {type: 'line', name: '实付总金额', showSymbol: false, fieldName: 'actualTotalCost', data: []},
                {type: 'line', name: '实付电费', showSymbol: false, isComputeSum: true, fieldName: 'actualTotalElect', data: []},
                {type: 'line', name: '实付服务费', showSymbol: false, isComputeSum: true, fieldName: 'actualTotalFee', data: []},
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
                {type: 'line', name: '充电电量', showSymbol: false, fieldName: 'chargeQt', data: []},
                {type: 'line', name: '直流充电量', showSymbol: false, isComputeSum: true, fieldName: 'dcChargeQt', data: []},
                {type: 'line', name: '交流充电量', showSymbol: false, isComputeSum: true, fieldName: 'acChargeQt', data: []},
              ],
              seriesListArray2: [
                {type: 'bar', name: '尖', stack: 'Ad', barWidth: 16, emphasis: {focus: "series"}, color: "#FB6868", isComputeSum: true, fieldName: 'sharpQt', data: []},
                {type: 'bar', name: '峰', stack: 'Ad', barWidth: 16, emphasis: {focus: "series"}, color: "#FD9449", isComputeSum: true, fieldName: 'peakQt', data: []},
                {type: 'bar', name: '平', stack: 'Ad', barWidth: 16, emphasis: {focus: "series"}, color: "#56ADF7", isComputeSum: true, fieldName: 'flatQt', data: []},
                {type: 'bar', name: '谷', stack: 'Ad', barWidth: 16, emphasis: {focus: "series"}, color: "#6DCF36", isComputeSum: true, fieldName: 'valleyQt', data: []},
                {type: 'bar', name: '深谷', stack: 'Ad', barWidth: 16, emphasis: {focus: "series"}, color: "#36CFC2", isComputeSum: true, fieldName: 'deepvalleyQt', data: []},
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
                {type: 'line', name: '订单数量', color: '#56ADF7', showSymbol: false, fieldName: 'orderNum', data: []},
                {type: 'line', name: '异常订单数量', color: '#FB6868', showSymbol: false, fieldName: 'abOrderNum', data: []}
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
                {type: 'line', name: 'V2G订单金额', color: '#3CC3DF', showSymbol: false, fieldName: 'dischargeSumCost', data: []}
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
                {type: 'line', name: 'V2G放电电量', color: '#FFBE70', showSymbol: false, fieldName: 'dischargeQt', data: []}
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
                {type: 'line', name: '枪均充电量', showSymbol: false, fieldName: 'gunAvChargeQt', data: []},
                {type: 'line', name: '直流枪均充电量', showSymbol: false, fieldName: 'dcGunAvChargeQt', data: []},
                {type: 'line', name: '交流枪均充电量', showSymbol: false, fieldName: 'acGunAvChargeQt', data: []},
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
                {type: 'line', name: '时间利用率', color: '#3CC3DF', showSymbol: false, fieldName: 'timeRatio', data: []}
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
                {type: 'line', name: '充电时长', showSymbol: false, fieldName: 'chargeDuration', data: []},
                {type: 'line', name: '直流充电时长', showSymbol: false, fieldName: 'dcChargeDuration', data: []},
                {type: 'line', name: '交流充电时长', showSymbol: false, fieldName: 'acChargeDuration', data: []},
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
                {type: 'line', name: '度均服务费', color: '#3CC3DF', showSymbol: false, fieldName: 'avgChargeFee', data: []}
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
                {type: 'line', name: '功率利用率', color: '#3CC3DF', showSymbol: false, fieldName: 'powerRatio', data: []}
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
                {type: 'line', name: '一次充电成功率', color: '#3CC3DF', showSymbol: false, fieldName: 'chargeSuccessRatio', data: []}
              ]
            },
          ]
        }
      ]
    });

    const queryCountOperationDataFun = ()=>{
      if(!that.isThereAreSite){
        querySiteBasicInfoByTenantId();
        return;
      }
      queryCountOperationOverview();
      queryCountOperationCurve();
    };

    // 统计运营总览数据
    const queryCountOperationOverview = ()=>{
      let data = {};
      that.listLoading = true;

      if(that.dateType === 1){
        data['endDate'] = getDaysFromCurrentTime(-1);
        data['startDate'] = getDaysFromCurrentTime(-7);
      }

      if(that.dateType === 2){
        data['endDate'] = getDaysFromCurrentTime(-1);
        data['startDate'] = getDaysFromCurrentTime(-31);
      }

      if(that.dateType === 3){
        data['endDate'] = getDaysFromCurrentTime(-1);
        data['startDate'] = getCurrentMonthFirstDay(getDaysFromCurrentTime(-365));
      }

      if(that.dateType === 4){
        data['endDate'] = that.customDateTime[1];
        data['startDate'] = that.customDateTime[0];
      }

      let diffDayNum = getDaysBetweenDates(data.startDate,data.endDate);
      data.beforeEndDate = getDaysFromCurrentTime(-1,0,data.startDate);
      data.beforeStartDate = getDaysFromCurrentTime((0 - diffDayNum),0,data.beforeEndDate);
      if(that.dateType === 3) data.beforeStartDate = getCurrentMonthFirstDay(data.beforeStartDate);

      // console.log(data);
      // console.log(that.siteAllIds);
      let siteIds = JSON.parse(JSON.stringify(that.siteIds));
      if(!siteIds || !siteIds.length){
        if(!that.siteAllIds || !that.siteAllIds.length){
          that.returnDataInfo = {};
          that.listLoading = false;
          ElMessage({type: "error", showClose: true, message: "请先选择查询的站点！"});
          return;
        }
        siteIds = JSON.parse(JSON.stringify(that.siteAllIds));
      }

      countOperationOverview({ ...data, siteIds }).then(res=>{
        that.returnDataInfo = res.data ? res.data : {};
        that.listLoading = false;
      }).catch(()=>{
        that.returnDataInfo = {};
        that.listLoading = false;
      });
    };

    const changeEvent = (data)=>{
      if(data.operateType === "RevenueStAndOperatingEfCom"){
        that.types[data.componentIndex] = data.active_sl_id;
        if(that.types.length === that.list.length) queryCountOperationCurve();
      }
    };

    // 统计运营总览曲线数据
    const queryCountOperationCurve = ()=>{
      let data = {};
      that.chartLoading = true;
      if(that.dateType === 1){
        data['endDate'] = getDaysFromCurrentTime(-1);
        data['startDate'] = getDaysFromCurrentTime(-7);
      }

      if(that.dateType === 2){
        data['endDate'] = getDaysFromCurrentTime(-1);
        data['startDate'] = getDaysFromCurrentTime(-31);
      }

      if(that.dateType === 3){
        data['endDate'] = getDaysFromCurrentTime(-1);
        data['startDate'] = getCurrentMonthFirstDay(getDaysFromCurrentTime(-365));
      }

      if(that.dateType === 4){
        data['endDate'] = that.customDateTime[1];
        data['startDate'] = that.customDateTime[0];
      }

      let siteIds = JSON.parse(JSON.stringify(that.siteIds));
      if(!siteIds || !siteIds.length){
        if(!that.siteAllIds || !that.siteAllIds.length){
          that.returnChartInfo = {};
          that.chartLoading = false;
          ElMessage({type: "error", showClose: true, message: "请先选择查询的站点！"});
          return;
        }
        siteIds = JSON.parse(JSON.stringify(that.siteAllIds));
      }
      let dateType = that.dateType === 3 ? 2 : 1; // 日期类型 1-天 2-月
      countOperationCurve({ ...data, siteIds, types: that.types, dateType }).then(res=>{
        that.returnChartInfo = res.data ? res.data : {};
        that.chartLoading = false;
      }).catch(()=>{
        that.returnChartInfo = {};
        that.chartLoading = false;
      });
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({scenarioTypes: "3",timer: new Date()}).then(res => {
        let siteAllIds = [];
        let list = res.data ? res.data : [];
        list.forEach(item=>siteAllIds.push(item.id));
        that.siteIdArray = JSON.parse(JSON.stringify(list));
        that.siteAllIds = JSON.parse(JSON.stringify(siteAllIds));
        that.isThereAreSite = !!siteAllIds.length;
        if(that.isThereAreSite) queryCountOperationOverview();
      });
    };

    const watchDateType = watch(()=> that.dateType,()=>{
      queryCountOperationDataFun();
    },{ deep: true });

    onMounted(()=>{
      querySiteBasicInfoByTenantId();
    });

    return {...toRefs(that), querySiteBasicInfoByTenantId, queryCountOperationOverview, changeEvent, queryCountOperationCurve, queryCountOperationDataFun, watchDateType};
  }
});
</script>

<style lang="scss" scoped>
.header-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;

  .header-form-left{
    display: flex;
    align-items: center;
    margin-bottom: 12px;

    .date_picker_class{
      margin-left: 12px;

      :deep(.el-date-editor) {
        --el-date-editor-daterange-width: 280px;
      }
    }
  }

  .header-form-right{
    width: 280px;
    margin-bottom: 12px;
  }
}

.content_li_body{
  margin-bottom: 10px;
  box-sizing: border-box;
  padding: 16px 4px 4px 16px;
}
</style>