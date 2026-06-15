<template>
  <div class="content_card_body">
    <el-row :gutter="12" class="content_list">
      <template v-for="(item,index) in fieldList" :key="index">
        <el-col :lg="8" :md="12" :sm="24" :xs="24">
          <div class="card_li_class" :class="{active_class: item.id === active_sl_id}" @click="clickItemCardFun(item)">
            <div class="card_li_top">
              <div class="card_li_top_title textTwo">{{ item.name }}</div>
              <div class="card_li_top_alter" v-if="item.describe">
                <el-tooltip effect="dark" placement="top-start">
                  <template #content>
                    <div class="describe" v-html="item.describe"></div>
                  </template>
                  <span class="iconfont icon-tishi"></span>
                </el-tooltip>
              </div>
            </div>
            <div class="card_li_bottom">
              <div class="card_li_bottom_left flex-all textTwo">
                <span class="number">{{ $filters.numberValue(return_data_info[item.fieldName]) }}</span>
                <span class="unit">{{ $filters.numberUnit(return_data_info[item.fieldName],item.unit) }}</span>
              </div>
              <template v-if="dateType !== 3">
                <div class="card_li_bottom_right" :class="[return_data_info[item.ratioName] < 0 ? 'green_class' : 'red_class']">
                  <span v-if="return_data_info[item.ratioName] < 0" class="iconfont icon-xiajiantou"></span>
                  <span v-else class="iconfont icon-shangjiantou"></span>
                  <span class="number">{{ $filters.numberNull(return_data_info[item.ratioName]) }}%</span>
                </div>
              </template>
            </div>
          </div>
        </el-col>
      </template>
    </el-row>
    <div class="content_bottom_chart">

      <div class="content_bt_chart_top">
        <div class="bt_chart_top_left flex-all">
          <template v-for="(item,index) in seriesListArray" :key="index">
            <div class="compute_class" v-if="item.isComputeSum">
              <div class="compute_class_left">{{ item.name }}：</div>
              <div class="compute_class_right">
                <span class="number">{{ $filters.numberValue(computeSumDataInfo[item.fieldName]) }}</span>
                <span class="unit">{{ $filters.numberUnit(computeSumDataInfo[item.fieldName],unitChart) }}</span>
              </div>
            </div>
          </template>
        </div>
        <div class="bt_chart_top_right">
          <template v-if="active_sl_id === 3">
            <ButtonsTabs :tabs-array="tabsArray" v-model:tabs-index="tabsIndex" @changeEvent="queryFirstCardFun({isRequest: false})"></ButtonsTabs>
          </template>

          <el-dropdown :disabled="exportLoading" placement="bottom-start">
            <span class="iconfont icon-xiazai exportIcon"></span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="downloadChartDataFun(1)">导出图片</el-dropdown-item>
                <el-dropdown-item @click="downloadChartDataFun(2)">导出Excel</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <div class="flex-all bottom_chart">
        <RevenueStAndOperatingEfChart ref="revenueStAndOperatingEfChartRef" :unit="unitChart" :legendShow="legendShow" :boundaryGap="boundaryGap"
                                      :dateList="dateList" :seriesListArray="seriesListArray" />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import $filters from "@/common/filters";
import {getNowDateAll} from "@/utils/dateTime";
import {calcNumberFun, downloadFiles} from "@/utils";
import {exportCustomExcel} from "@/common/exportExcel";
import RevenueStAndOperatingEfChart from "./RevenueStAndOperatingEfChart.vue";
import {onMounted, reactive, toRefs, defineComponent, getCurrentInstance, nextTick, watch, ref} from "vue";

export default defineComponent({
  name: "RevenueStAndOperatingEfCom",
  components:{RevenueStAndOperatingEfChart},
  emits: ["changeEvent","update:exportLoading"],
  props: {
    dateType:{
      type: Number,
      default: 1
    },
    // 组件下标
    componentIndex:{
      type: Number,
      default: 0
    },
    fieldList: {
      type: Array,
      default: () => []
    },
    returnDataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
    returnChartInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      tabsIndex: 1,
      active_sl_id: 1,
      listLoading: false,
      exportLoading: false,
      return_data_info: {},
      return_chart_info: {},
      computeSumDataInfo: {}, // 计算曲线总数据
      tabsArray: [{id: 1, name: "趋势"}, {id: 2, name: "分时"}],

      dateList: [],
      unitChart: "",
      legendShow: true, // 图表legend 是否显示   true： 不显示 false 显示
      boundaryGap: false,
      seriesListArray: [],
    });

    const queryFirstCardFun = (config = {isRequest: true})=>{
      let findItem = props.fieldList.find(item => item.id === that.active_sl_id);
      if(!findItem) findItem = props.fieldList[0];
      clickItemCardFun(findItem,config);
    };

    const clickItemCardFun = (data,config = {isRequest: true})=>{
      let boundaryGap = false;
      that.unitChart = data.unit;
      that.active_sl_id = data.id;
      that.legendShow = !data.legendShow;
      let seriesListArray = JSON.parse(JSON.stringify(data.seriesListArray ?? []));
      if(data.isChildrenTab){
        boundaryGap = that.tabsIndex === 2;
        seriesListArray = JSON.parse(JSON.stringify(data['seriesListArray' + that.tabsIndex] ?? []));
      }

      that.boundaryGap = boundaryGap;
      that.seriesListArray = JSON.parse(JSON.stringify(seriesListArray));
      if(!config.isRequest) setChartOptionFun();

      if(config.isRequest){
        nextTick(()=>{
          emit("changeEvent",{
            active_sl_id: that.active_sl_id,
            componentIndex: props.componentIndex,
            operateType: "RevenueStAndOperatingEfCom",
          });
        });
      }
    };

    const setChartOptionFun = ()=>{
      let chartDataList = [];
      that.dateList = that.return_chart_info?.dateList;
      if(that.return_chart_info.curveDataMap){
        if(that.return_chart_info.curveDataMap[that.active_sl_id]){
          chartDataList = that.return_chart_info?.curveDataMap[that.active_sl_id];
        }
      }

      if(that.seriesListArray && that.seriesListArray.length){
        for(let i = 0;i < that.seriesListArray.length; i++){
          let seriesDataArray = [],computeSumNum = "";
          if(chartDataList && chartDataList.length){
            let findItem = chartDataList.find(item => item.dataName === that.seriesListArray[i].fieldName);
            if(findItem) seriesDataArray = findItem.dataValueList;
          }

          if(seriesDataArray && seriesDataArray.length){
            computeSumNum = 0;
            for(let j = 0;j < seriesDataArray.length;j++){
              if(that.seriesListArray[i].isComputeSum) computeSumNum = calcNumberFun(computeSumNum,seriesDataArray[j] ?? 0,"+");
              seriesDataArray[j] = $filters.moneyTwoNum(seriesDataArray[j],3,{ toNA: '--', isZeroFill: false });
            }
          }

          that.computeSumDataInfo[that.seriesListArray[i].fieldName] = computeSumNum; // 先重置数据
          that.seriesListArray[i].data = JSON.parse(JSON.stringify(seriesDataArray));
        }
      }
    };

    const revenueStAndOperatingEfChartRef = ref(null);
    const downloadChartDataFun = (operateType)=>{
      that.exportLoading = true;
      let findItem = props.fieldList.find(item=> item.id === that.active_sl_id);

      if (operateType === 1) {
        const fileUrl = revenueStAndOperatingEfChartRef.value.chartComponentRef.getDataURL({pixelRatio: 2, backgroundColor: '#FFFFFF'});
        ElMessage({type: 'success', showClose: true, message: '下载成功！'});
        downloadFiles(fileUrl, `${ findItem.name }_趋势数据_ ${ getNowDateAll() }`);
        that.exportLoading = false;
      }

      if (operateType === 2) {
        let tableHeader = [],exportExcelListData = [];
        if(that.seriesListArray && that.seriesListArray.length){
          for(let j = 0;j < that.seriesListArray.length; j++){
            if(that.seriesListArray[j].fieldName){
              tableHeader.push({
                width: 25,
                name: that.seriesListArray[j].name,
                key: that.seriesListArray[j].fieldName,
              });
            }
          }
        }

        if(that.dateList && that.dateList.length){
          for(let i = 0;i < that.dateList.length; i++){
            exportExcelListData[i] = {dateTime: that.dateList[i]};
            for(let j = 0;j < tableHeader.length; j++){
              exportExcelListData[i][tableHeader[j].key] = that.seriesListArray[j].data[i];
            }
          }
        }

        let newTableHeader = [{width: 25, key: "dateTime", name: "时间"}, ...tableHeader];
        exportCustomExcel(newTableHeader, exportExcelListData, `${ findItem.name }_趋势数据_ ${ getNowDateAll() }`);
        that.exportLoading = false;
      }
    };

    const watchReturnDataInfo = watch(()=>props.returnDataInfo,(newReturnDataInfo)=>{
      that.return_data_info = JSON.parse(JSON.stringify(newReturnDataInfo ?? {}));
    },{ deep: true });

    const watchReturnChartInfo = watch(()=>props.returnChartInfo,(newReturnChartInfo)=>{
      that.return_chart_info = JSON.parse(JSON.stringify(newReturnChartInfo ?? {}));
      setChartOptionFun();
    },{ deep: true });

    onMounted(()=>{
      queryFirstCardFun();
    });

    return {...toRefs(that), clickItemCardFun, queryFirstCardFun, watchReturnDataInfo, watchReturnChartInfo, setChartOptionFun, downloadChartDataFun,
      revenueStAndOperatingEfChartRef};
  }
});
</script>

<style lang="scss" scoped>
.content_card_body{
  width: 100%;

  .card_li_class{
    cursor: pointer;
    padding: 14px 12px;
    border-radius: 8px;
    box-sizing: border-box;
    background: #ffffff1a;
    margin-bottom: 12px;
    border: 1px solid transparent;

    .card_li_top{
      display: flex;
      align-items: center;
      font-size: 16px;
      color: #ffffffcc;

      .card_li_top_title{
        flex: 1;
        flex-basis: auto;
        -webkit-line-clamp: 1;
      }

      .card_li_top_alter{}
    }

    .card_li_bottom{
      margin-top: 14px;
      display: flex;
      align-items: center;

      .card_li_bottom_left{
        color: #92d8ff;
        .number{
          font-size: 24px;
          font-weight: 500;
        }

        .unit{
          font-size: 16px;
          margin-left: 2px;
        }
      }

      .card_li_bottom_right{
        display: flex;
        align-items: center;
        font-size: 12px;

        .iconfont{
          font-size: 10px;
          margin-right: 2px;
        }
      }

      .green_class{
        color: #41CB4A;
      }

      .red_class{
        color: #FF2B2B;
      }
    }
  }

  .active_class{
    background: #22a7ff33;
    border: 1px solid #22a7ff;
  }

  .content_bottom_chart{
    width: 100%;
    height: 380px;
    padding: 2px 12px 12px 0;
    box-sizing: border-box;

    display: flex;
    flex-direction: column;

    .content_bt_chart_top{
      margin-bottom: 4px;
      display: flex;
      align-items: center;
      justify-content: space-between;

      .bt_chart_top_left{
        display: flex;
        align-items: center;

        .compute_class{
          flex: 1;
          display: flex;
          align-items: center;

          .compute_class_left{
            font-size: 12px;
            color: #ffffffcc;
          }

          .compute_class_right{
            color: #92d8ff;
            font-size: 12px;
          }
        }
      }

      .bt_chart_top_right{
        display: flex;
        align-items: center;

        :deep(.buttonsTabs){
          width: fit-content;

          .tabs_li {
            padding: 4px 16px;
          }
        }

        .exportIcon{
          cursor: pointer;
          font-size: 24px;
          color: #ffffffcc;
          margin-left: 12px;
        }
      }
    }

    .bottom_chart{
      width: 100%;
      height: 2px;
    }
  }
}
</style>