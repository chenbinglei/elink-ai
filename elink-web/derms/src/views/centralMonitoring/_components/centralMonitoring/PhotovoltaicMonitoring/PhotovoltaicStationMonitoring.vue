<template>
  <div class="monitoring_content">
    <div v-loading="listLoading" class="content_top content_bg_color">
      <div class="content_top_left">
        <BubblePercentageCom :returnDataInfo="returnDataInfo" centerImg="photovoltaic" dividendField="capacity" divisorField="realPower"/>
      </div>
      <DeviceDetailedInfoCom :deviceFieldList="deviceFieldList" :returnDataInfo="returnDataInfo" class="flex-all"/>
    </div>
    <el-row :gutter="6" class="content_bottom">
      <el-col :lg="10" :md="24" :sm="24" :xl="6">
        <div class="content_bg_color content_bottom_card">
          <MeteorologicalInfCom :siteId="siteId" titleName="气象信息"></MeteorologicalInfCom>
        </div>
      </el-col>
      <el-col :lg="14" :md="24" :sm="24" :xl="9">
        <div class="content_bg_color content_bottom_card">
          <SystemVarTimeChartCom :fileName="fileName" :seriesListArray="powerCurveSeriesList" :siteId="siteId" titleName="功率曲线"
                                 yAxisName="kW" :isRequestType="4"/>
        </div>
      </el-col>
      <el-col :lg="24" :md="24" :sm="24" :xl="9">
        <div class="content_bg_color content_bottom_card">
          <SystemVarTimeChartCom :fileName="fileName" :seriesListArray="pvPowerGenerationSeriesList" :siteId="siteId" :timerType="2"
                                 :legend="pvPowerGenerationLegend" titleName="光伏发电量" yAxisName="kWh" :isRequestType="5"/>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts">
import {reactive, defineComponent, toRefs, watch} from "vue";
import {findPvSiteMonitorData} from "@/api/centralMonitoring/centralMonitoring";
import MeteorologicalInfCom from "./PhotovoltaicStationMonitoring/MeteorologicalInfCom.vue";
import {BubblePercentageCom, DeviceDetailedInfoCom, SystemVarTimeChartCom} from "@/views/centralMonitoring/_components/centralMonitoring/PublicComponents";

export default defineComponent({
  name: "PhotovoltaicStationMonitoring",
  components: {MeteorologicalInfCom, DeviceDetailedInfoCom, BubblePercentageCom, SystemVarTimeChartCom},
  props: {
    siteId: {
      type: [Number, String],
      default: ""
    },
    siteName: {
      type: String,
      default: ""
    },
  },
  setup(props) {
    const that = reactive({
      listLoading: false,
      returnDataInfo: {},
      fileName: props.siteName + '_光伏电站监测',
      deviceFieldList: [
        {name: "装机容量", fieldName: "capacity", unit: "kW"},
        {name: "并网点", fieldName: "parallelNum", unit: "个"},
        {name: "逆变器数量", fieldName: "inverterNum", unit: "个"},
        {name: "阵列面积", fieldName: "arrayArea", unit: "m²"},
        {name: "阵列倾角", fieldName: "arrayInclination", unit: "°"},
        {name: "累计发电量", fieldName: "sumQt", unit: "kWh"},
        {name: "今日发电量", fieldName: "dayQt", unit: "kWh"},
        {name: "昨日发电量", fieldName: "lastDayQt", unit: "kWh"},
        {name: "昨日系统效率", fieldName: "lastDayEff", unit: "%"},
        {name: "昨日等效利用小时数", fieldName: "lastDayHours", unit: "h"},
      ],
      // 功率曲线
      powerCurveSeriesList: [
        {type: 'line', name: '实际功率', showSymbol: false, fieldName: 'realPowerList', data: []},
        {type: 'line', name: '理论功率', showSymbol: false, fieldName: 'theoryPowerList', data: []},
        {type: 'line', name: '短期预测功率', showSymbol: false, fieldName: 'shortForecastPowerList', data: []}
      ],
      // 光伏发电量
      pvPowerGenerationSeriesList: [
        {
          data: [],
          type: 'bar',
          barMaxWidth: 12,
          name: '实际发电量',
          fieldName: 'realQtList'
        },
        // {
        //   data: [],
        //   type: 'bar',
        //   barMaxWidth: 12,
        //   name: '理论发电量',
        //   fieldName_1: 'pv_theory_day_kwh',
        //   fieldName_2: 'pv_theory_month_kwh',
        //   fieldName_3: 'pv_theory_year_kwh',
        // },
        // {
        //   data: [],
        //   type: 'bar',
        //   barMaxWidth: 12,
        //   name: '短期预测发电量',
        //   fieldName_1: 'pv_shortterm_forecast_day_kwh',
        //   fieldName_2: 'pv_shortterm_forecast_month_kwh',
        //   fieldName_3: 'pv_shortterm_forecast_year_kwh',
        // }
      ],
      pvPowerGenerationLegend: {
        top: 0,
        left: 'center',
        icon: 'rect',
        itemGap: 12,
        itemWidth: 10,
        itemHeight: 10,
        textStyle: {color: '#9EA9B5'},
      }
    });

    // 查询光伏站点监测数据
    const queryPvSiteMonitorData = () => {
      that.listLoading = true;
      findPvSiteMonitorData({siteId: props.siteId, timer: new Date()}).then(res => {
        that.returnDataInfo = res.data ?? {};
        that.listLoading = false;
      }).catch(() => {
        that.returnDataInfo = {};
        that.listLoading = false;
      });
    };

    const watchSiteId = watch(() => props.siteId, (newSiteId) => {
      if (newSiteId) queryPvSiteMonitorData();
    }, {deep: true, immediate: true});

    return {...toRefs(that), queryPvSiteMonitorData, watchSiteId};
  }
});
</script>

<style lang="scss" scoped>
.monitoring_content {
  height: 100%;
  padding: 14px 12px;
  box-sizing: border-box;
  overflow-y: auto;

  .content_top {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    box-sizing: border-box;
    padding: 24px 16px 10px 16px;
  }

  .content_bottom_card {
    height: 460px;
    padding: 14px 12px;
    box-sizing: border-box;
    margin-bottom: 12px;
  }

  .content_bg_color {
    border-radius: 6px;
    background: #ffffff08;
  }
}
</style>