<template>
  <div class="monitoring_content">
    <div class="content_top content_bg_color">
      <div class="content_top_left">
        <BubblePercentageCom :returnDataInfo="returnDataInfo" centerImg="station_icon" dividendField="capacity" divisorField="realPower"/>
      </div>
      <DeviceDetailedInfoCom :deviceFieldList="deviceFieldList" :returnDataInfo="returnDataInfo" class="flex-all"/>
    </div>
    <el-row :gutter="6" class="content_bottom">
      <el-col :lg="12" :md="24" :sm="24">
        <div class="content_bg_color content_bottom_card">
          <SystemVarTimeChartCom :fileName="fileName" :seriesListArray="powerCurveSeriesList" :siteId="siteId" titleName="功率曲线" yAxisName="kW" :isRequestType="9" />
        </div>
      </el-col>
      <el-col :lg="12" :md="24" :sm="24">
        <div class="content_bg_color content_bottom_card">
          <SystemVarTimeChartCom :fileName="fileName" :isRequestType="3" :legend="electricityStatisticsLegend" :seriesListArray="electricityStatisticsSeriesList"
                                 :siteId="siteId" :timerType="2" titleName="电量统计" yAxisName="kWh"/>
        </div>
      </el-col>
      <el-col :span="24">
        <div class="content_bg_color content_bottom_card" style="height: fit-content;padding-bottom: 0">
          <DeviceMonitorCom :siteId="siteId"></DeviceMonitorCom>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import DeviceMonitorCom from "./DeviceMonitorCom.vue";
import {reactive, defineComponent, toRefs, watch} from "vue";
import {findChargeSiteMonitorData} from "@/api/centralMonitoring/centralMonitoring";
import {BubblePercentageCom, DeviceDetailedInfoCom, SystemVarTimeChartCom} from "@/views/centralMonitoring/_components/centralMonitoring/PublicComponents";

export default defineComponent({
  name: "ChargingStationMonitoring",
  components: {SystemVarTimeChartCom, DeviceDetailedInfoCom, BubblePercentageCom, DeviceMonitorCom},
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
      fileName: props.siteName + '_充电站监测',
      deviceFieldList: [
        {name: "装机容量", fieldName: "capacity", unit: "kW"},
        {name: "电桩数量", fieldName: "pileNum", unit: "台"},
        {name: "直流桩/枪", fieldName: "acPileNum", unit: "台", splitSymbol: '/', nextFieldName: "acGunNum", nextUnit: "个"},
        {name: "交流桩/枪", fieldName: "dcPileNum", unit: "台", splitSymbol: '/', nextFieldName: "dcGunNum", nextUnit: "个"},
        {name: "今日充电量", fieldName: "dayChargeQt", unit: "kWh"},
        {name: "今日V2G电量", fieldName: "dayV2gQt", unit: "kWh"},
        {name: "昨日充电量", fieldName: "lastDayChargeQt", unit: "kWh"},
        {name: "昨日V2G电量", fieldName: "lastDayV2gQt", unit: "kWh"},
        {name: "累计充电量", fieldName: "sumChargeQt", unit: "kWh"},
        {name: "累计V2G电量", fieldName: "sumV2gQt", unit: "kWh"},
      ],
      powerCurveSeriesList: [
        {type: 'line', name: '充电功率', data: [], showSymbol: false, fieldName: 'chargePowerList'},
        {type: 'line', name: '放电功率', data: [], showSymbol: false, fieldName: 'dischargePowerList'}
      ],
      electricityStatisticsSeriesList: [
        {type: 'bar', name: '充电电量', barMaxWidth: 12, data: [], fieldName: 'chargeQtList'},
        {type: 'bar', name: '放电电量', barMaxWidth: 12, data: [], fieldName: 'dischargeQtList'}
      ],
      electricityStatisticsLegend: {
        top: 0,
        left: 'center',
        icon: 'rect',
        itemGap: 12,
        itemWidth: 10,
        itemHeight: 10,
        textStyle: {color: '#9EA9B5'},
      },
    });

    // 查询光伏站点监测数据
    const queryChargeSiteMonitorData = () => {
      that.listLoading = true;
      findChargeSiteMonitorData({siteId: props.siteId, timer: new Date()}).then(res => {
        that.returnDataInfo = res.data ?? {};
        that.listLoading = false;
      }).catch(() => {
        that.returnDataInfo = {};
        that.listLoading = false;
      });
    };

    const watchSiteId = watch(() => props.siteId, (newSiteId) => {
      if (newSiteId) queryChargeSiteMonitorData();
    }, {deep: true, immediate: true});


    return {...toRefs(that), queryChargeSiteMonitorData, watchSiteId};
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
    height: 380px;
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