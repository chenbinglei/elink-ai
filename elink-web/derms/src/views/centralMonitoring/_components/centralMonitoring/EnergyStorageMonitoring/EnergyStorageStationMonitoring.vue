<template>
  <div class="monitoring_content">
    <div class="content_top content_bg_color">
      <div class="content_top_left">
        <BubblePercentageCom :returnDataInfo="returnDataInfo" centerImg="chuNeng_icon" dividendField="batteryTotalCapacity" divisorField="realPower"/>
      </div>
      <DeviceDetailedInfoCom :deviceFieldList="deviceFieldList" :returnDataInfo="returnDataInfo" class="flex-all"/>
    </div>
    <el-row :gutter="6" class="content_bottom">
      <el-col :lg="12" :md="24" :sm="24">
        <div class="content_bg_color content_bottom_card">
          <SystemVarTimeChartCom :fileName="fileName" :seriesListArray="powerCurveSeriesList" :siteId="siteId" yAxisName="kW"
                                 titleName="功率曲线" :isRequestType="6"/>
        </div>
      </el-col>
      <el-col :lg="12" :md="24" :sm="24">
        <div class="content_bg_color content_bottom_card">
          <SystemVarTimeChartCom :fileName="fileName" :legend="electricityStatisticsLegend" :siteId="siteId" :timerType="2" titleName="充放电量" yAxisName="kWh"
                                 :seriesListArray="electricityStatisticsSeriesList" :isRequestType="7"/>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {reactive, defineComponent, toRefs, watch} from "vue";
import {findStorageMonitorData} from "@/api/centralMonitoring/centralMonitoring";
import {BubblePercentageCom, DeviceDetailedInfoCom, SystemVarTimeChartCom} from "@/views/centralMonitoring/_components/centralMonitoring/PublicComponents";

export default defineComponent({
  name: "EnergyStorageStationMonitoring",
  components: {SystemVarTimeChartCom, DeviceDetailedInfoCom, BubblePercentageCom},
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
      fileName: props.siteName + '_储能站监测',
      deviceFieldList: [
        {name: "装机容量", fieldName: "pcsTotalPower", unit: 'kW', splitSymbol: "/", nextFieldName: "batteryTotalCapacity", nextUnit: "kWh"},
        {name: "PCS数量", fieldName: "pcsNum", unit: '个'},
        {name: "电池簇数量", fieldName: "batteryNum", unit: '个'},
        {name: "电池包数量", fieldName: "batteryPackNum", unit: '个'},
        {name: "电芯数量", fieldName: "batteryCellNum", unit: '个'},
        {name: "充放电倍率", fieldName: "chargeMagnification", unit: 'C'},
        {name: "昨日系统效率", fieldName: "lastDayEff", unit: '%'},
        {name: "累计充电量", fieldName: "sumChargeQt", unit: 'kWh'},
        {name: "累计放电量", fieldName: "sumDischargeQt", unit: 'kWh'},
        {name: "累计充放电循环次数", fieldName: "sumChargeCycleNum", unit: '次'},
      ],
      powerCurveSeriesList: [{type: 'line', name: '有功功率', data: [], showSymbol: false, fieldName: 'activePowerList',color: "#FF928A"}],
      electricityStatisticsSeriesList: [
        {
          data: [],
          type: 'bar',
          name: '充电电量',
          barMaxWidth: 12,
          fieldName: 'chargeQtList'
        },
        {
          data: [],
          type: 'bar',
          name: '放电电量',
          barMaxWidth: 12,
          fieldName: 'dischargeQtList'
        }
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

    // 查询储能站点监测数据
    const queryStorageMonitorData = () => {
      that.listLoading = true;
      findStorageMonitorData({siteId: props.siteId, timer: new Date()}).then(res => {
        that.returnDataInfo = res.data ?? {};
        that.listLoading = false;
      }).catch(() => {
        that.returnDataInfo = {};
        that.listLoading = false;
      });
    };

    const watchSiteId = watch(() => props.siteId, (newSiteId) => {
      if (newSiteId) queryStorageMonitorData();
    }, {deep: true, immediate: true});

    return {...toRefs(that), queryStorageMonitorData, watchSiteId};
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