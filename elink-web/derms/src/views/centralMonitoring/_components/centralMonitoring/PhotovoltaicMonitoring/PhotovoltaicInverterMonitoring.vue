<template>
  <div v-loading="listLoading" class="monitoring_content">
    <DeviceListCom :activeDeviceId="activeDeviceId" :deviceList="pvInverterList" @changeEvent="changeEvent">
      <template v-slot:status="{ data }">
        <InverterDeviceStatusCom :deviceInfo="data"></InverterDeviceStatusCom>
      </template>
      <template v-slot:content="{ data }">
        <InverterDeviceCardInfo :deviceInfo="data"></InverterDeviceCardInfo>
      </template>
    </DeviceListCom>
    <div class="flex-all monitoring_content_right">
      <template v-if="activeDeviceId">
        <InverterDeviceInfo :activeDeviceInfo="activeDeviceInfo" @changeEvent="changeEvent" />
        <el-row :gutter="10" class="bottom_card">
          <el-col v-for="(item, index) in list" :key="index" :xl="item.xl" :lg="item.lg" :sm="item.sm">
            <div class="content_body">
              <component :is="item.componentName" :activeDeviceId="activeDeviceId" :siteId="activeDeviceId"
                :titleName="item.name" :timerType="item.timerType" :fileName="fileName"
                :isRequestType="item.isRequestType" :seriesListArray="item.seriesListArray" :legend="item.legend"
                :yAxis="item.yAxis" :yAxisName="item.yAxisName" :isHandleVarName="item.isHandleVarName"></component>
            </div>
          </el-col>
        </el-row>
      </template>
      <null-data v-else words="请先选择设备"></null-data>
    </div>
  </div>
</template>

<script>
import { reactive, defineComponent, toRefs, watch } from "vue";
import { findPvInverterList } from "@/api/centralMonitoring/centralMonitoring";
import RemoteControlCom from "./PhotovoltaicInverterMonitoring/RemoteControlCom.vue";
import InverterDeviceInfo from "./PhotovoltaicInverterMonitoring/InverterDeviceInfo.vue";
import {
  DeviceListCom, InverterDeviceCardInfo, InverterDeviceStatusCom, RemoteSignalingAlarmCom, DeviceTelemetryAttrCom, OperationIndexCom,
  SystemVarTimeChartCom
} from "@/views/centralMonitoring/_components/centralMonitoring/PublicComponents";

export default defineComponent({
  name: "PhotovoltaicInverterMonitoring",
  components: {
    RemoteControlCom, OperationIndexCom, InverterDeviceInfo, DeviceListCom, InverterDeviceCardInfo, InverterDeviceStatusCom, RemoteSignalingAlarmCom,
    DeviceTelemetryAttrCom, SystemVarTimeChartCom
  },
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
      fileName: "",
      pvInverterList: [],
      listLoading: false,
      activeDeviceId: "",
      activeDeviceInfo: {},

      list: [
        { name: "遥控", componentName: "RemoteControlCom", xl: 8, lg: 10, md: 24, sm: 24 },
        { name: "遥测", componentName: "DeviceTelemetryAttrCom", xl: 16, lg: 14, md: 24, sm: 24 },
        { name: "遥信告警", componentName: "RemoteSignalingAlarmCom", xl: 8, lg: 10, md: 24, sm: 24 },
        {
          name: "功率曲线",
          isRequestType: 2,
          xl: 16, lg: 14, md: 24, sm: 24,
          componentName: "SystemVarTimeChartCom",
          yAxis: [
            {
              name: "kW",
              type: 'value',
              splitNumber: 3,
              splitLine: { show: false },
              axisLabel: { color: "#9EA9B5" },
            },
            {
              name: "kVar",
              type: 'value',
              splitNumber: 3,
              splitLine: { show: false },
              axisLabel: { color: "#9EA9B5" },
            },
          ],
          seriesListArray: [
            {
              data: [],
              type: 'line',
              yAxisIndex: 0,
              name: '有功功率',
              showSymbol: false,
              fieldName: 'active_power',
            },
            {
              data: [],
              type: 'line',
              yAxisIndex: 1,
              name: '无功功率',
              showSymbol: false,
              fieldName: 'reactive_power_value',
            }
          ]
        },
        { name: "运行指标", componentName: "OperationIndexCom", xl: 8, lg: 10, md: 24, sm: 24 },
        {
          name: "发电量",
          timerType: 2,
          yAxisName: "kW",
          isRequestType: 2,
          isHandleVarName: true,
          xl: 16, lg: 14, md: 24, sm: 24,
          componentName: "SystemVarTimeChartCom",
          legend: {
            top: 0,
            left: 'center',
            icon: 'rect',
            itemGap: 12,
            itemWidth: 10,
            itemHeight: 10,
            textStyle: { color: '#9EA9B5' },
          },
          seriesListArray: [
            {
              data: [],
              type: 'bar',
              barMaxWidth: 12,
              name: '实际发电量',
              fieldName_1: 'daily_power_generation',
              fieldName_2: 'monthly_power_generation',
              fieldName_3: 'power_generation_this_year',
            }
          ]
        },
      ]
    });

    // 查询光伏逆变器列表
    const queryPvInverterList = () => {
      that.listLoading = true;
      findPvInverterList({ siteId: props.siteId, timer: new Date() }).then(res => {
        that.pvInverterList = res.data ? res.data : [];
        that.listLoading = false;
      }).catch(() => {
        that.pvInverterList = [];
        that.listLoading = false;
      });
    };

    const changeEvent = (data) => {
      if (data.operateType === "InverterDeviceInfo") queryPvInverterList();
      if (data.operateType === "DeviceListCom") {
        that.activeDeviceInfo = JSON.parse(JSON.stringify(data ?? {}));
        that.fileName = `${props.siteName}_${data.deviceName}_逆变器监控`;
      }
    };

    const watchSiteId = watch(() => props.siteId, (newSiteId) => {
      if (newSiteId) queryPvInverterList();
    }, { deep: true, immediate: true });

    return { ...toRefs(that), changeEvent, watchSiteId, queryPvInverterList };
  }
});
</script>

<style lang="scss" scoped>
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

    .bottom_card {
      margin-top: 14px;

      .content_body {
        height: 280px;
        border-radius: 6px;
        background: #ffffff08;
        box-sizing: border-box;
        margin-bottom: 10px;
        padding: 12px 10px;
      }
    }
  }
}
</style>