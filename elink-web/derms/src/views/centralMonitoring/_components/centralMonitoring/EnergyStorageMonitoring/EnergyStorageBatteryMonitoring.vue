<template>
  <div v-loading="listLoading" class="monitoring_content">
    <DeviceListCom :activeDeviceId="activeDeviceId" :deviceList="batteryDeviceList" @changeEvent="changeEvent">
      <template v-slot:status="{data}">
        <BatteryDeviceStatusCom :deviceInfo="data"></BatteryDeviceStatusCom>
      </template>
      <template v-slot:content="{data}">
        <BatteryDeviceCardInfo :deviceInfo="data"></BatteryDeviceCardInfo>
      </template>
    </DeviceListCom>
    <div class="flex-all monitoring_content_right">
      <template v-if="activeDeviceId">
        <BatteryDeviceInfoCom :activeDeviceInfo="activeDeviceInfo" @changeEvent="changeEvent"></BatteryDeviceInfoCom>
        <el-row :gutter="10" class="bottom_card">
            <el-col v-for="(item,index) in list" :key="index" :xl="item.xl" :lg="item.lg" :sm="item.sm">
              <div class="content_body">
                <component :is="item.componentName" :activeDeviceId="activeDeviceId" :siteId="activeDeviceId" :titleName="item.name" :timerType="item.timerType"
                           :fileName="fileName" :isRequestType="item.isRequestType" :seriesListArray="item.seriesListArray" :legend="item.legend"
                           :yAxis="item.yAxis" :yAxisName="item.yAxisName"></component>
              </div>
            </el-col>
        </el-row>
      </template>
      <null-data v-else words="请先选择设备"></null-data>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent, reactive, toRefs, watch} from "vue";
import {findBatteryMonitorList} from "@/api/centralMonitoring/centralMonitoring";
import SingleCellStatusCom from "./EnergyStorageBatteryMonitoring/SingleCellStatusCom.vue";
import BatteryDeviceInfoCom from "./EnergyStorageBatteryMonitoring/BatteryDeviceInfoCom.vue";
import {DeviceListCom, BatteryDeviceCardInfo, BatteryDeviceStatusCom, RemoteSignalingAlarmCom, DeviceTelemetryAttrCom, SystemVarTimeChartCom, OperationIndexCom} from "@/views/centralMonitoring/_components/centralMonitoring/PublicComponents";

export default defineComponent({
  name: "EnergyStorageBatteryMonitoring",
  components: {BatteryDeviceStatusCom, BatteryDeviceCardInfo, DeviceListCom, BatteryDeviceInfoCom, DeviceTelemetryAttrCom, RemoteSignalingAlarmCom,
    SystemVarTimeChartCom, OperationIndexCom, SingleCellStatusCom},
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
  setup(props){

    const that = reactive({
      fileName: "",
      batteryDeviceList: [],
      listLoading: false,
      activeDeviceId: "",
      activeDeviceInfo: {},

      list: [
        {name: "遥信告警", componentName:"RemoteSignalingAlarmCom",xl: 8,lg: 10,md: 24,sm: 24},
        {name: "遥测", componentName:"DeviceTelemetryAttrCom",xl: 16,lg: 14,md: 24,sm: 24},
        {name: "运行指标", componentName:"OperationIndexCom",xl: 8,lg: 10,md: 24,sm: 24},
        {
          name: "SOC曲线",
          isRequestType: 2,
          xl: 16,lg: 14,md: 24,sm: 24,
          componentName:"SystemVarTimeChartCom",
          yAxis: [
            {
              name: "%",
              type: 'value',
              splitNumber: 3,
              splitLine: {show: false},
              axisLabel: {color: "#9EA9B5"},
            },
            {
              name: "A",
              type: 'value',
              splitNumber: 3,
              splitLine: {show: false},
              axisLabel: {color: "#9EA9B5"},
            },
          ],
          seriesListArray:[
            {
              data: [],
              name: 'SOC',
              type: 'line',
              yAxisIndex: 0,
              color: "#79C3FF",
              fieldName: 'soc',
              showSymbol: false
            },
            {
              data: [],
              type: 'line',
              yAxisIndex: 1,
              name: '总电流',
              color: "#00FF6A",
              showSymbol: false,
              fieldName: 'batterycurrent',
            }
          ]
        },
        {
          name: "电压曲线",
          isRequestType: 2,
          yAxisName: "V",
          xl: 8,lg: 10,md: 24,sm: 24,
          componentName: "SystemVarTimeChartCom",
          seriesListArray:[
            {
              data: [],
              type: 'line',
              color: "#82FF79",
              name: '电池总电压',
              showSymbol: false,
              fieldName: 'batterytotalvoltage',
            }
          ]
        },
        {
          name: "温度曲线",
          timerType: 1,
          yAxisName: "℃",
          isRequestType: 2,
          xl: 16,lg: 14,md: 24,sm: 24,
          componentName:"SystemVarTimeChartCom",
          seriesListArray: [
            {
              data: [],
              type: 'line',
              color: "#FF7979",
              name: '最高单体温度',
              showSymbol: false,
              fieldName: 'maxcelltemperature',
            },
            {
              data: [],
              type: 'line',
              color: "#D979FF",
              name: '最低单体温度',
              showSymbol: false,
              fieldName: 'mincelltemperature',
            }
          ]
        },
        {
          name: "单体电芯状态",
          xl: 24,lg: 24,md: 24,sm: 24,
          componentName:"SingleCellStatusCom",
        },
      ]
    });

    // 查询PCS设备监测列表数据
    const queryBatteryMonitorList = () => {
      that.listLoading = true;
      findBatteryMonitorList({siteId: props.siteId, timer: new Date()}).then(res => {
        that.batteryDeviceList = res.data ? res.data : [];
        that.listLoading = false;
      }).catch(() => {
        that.batteryDeviceList = [];
        that.listLoading = false;
      });
    };

    const changeEvent = (data) => {
      if (data.operateType === "PCSDeviceInfoCom") queryBatteryMonitorList();
      if (data.operateType === "DeviceListCom"){
        that.activeDeviceInfo = JSON.parse(JSON.stringify(data ?? {}));
        that.fileName = `${ props.siteName }_${ data.deviceName }_电池监控`;
      }
    };

    const watchSiteId = watch(() => props.siteId, (newSiteId) => {
      if (newSiteId) queryBatteryMonitorList();
    }, {deep: true, immediate: true});

    return {...toRefs(that), changeEvent, watchSiteId, queryBatteryMonitorList};
  }
});
</script>

<style scoped lang="scss">
.monitoring_content {
  height: 100%;
  display: flex;
  padding: 12px 8px;
  box-sizing: border-box;

  .monitoring_content_right {
    width: 2px;
    height: 100%;
    overflow-y: auto;
    padding-left: 12px;
    box-sizing: border-box;

    .bottom_card{
      margin-top: 14px;

      .content_body{
        height: 280px;
        border-radius: 6px;
        background: #ffffff08;
        box-sizing: border-box;
        margin-bottom: 10px;
        padding: 12px 10px;
      }

      .el-col:last-child{
        .content_body{
          height: fit-content;
        }
      }
    }
  }
}
</style>