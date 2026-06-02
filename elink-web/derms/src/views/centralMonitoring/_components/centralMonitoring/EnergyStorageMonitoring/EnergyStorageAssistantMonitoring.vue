<template>
  <div v-loading="listLoading" class="monitoring_content">
    <DeviceListCom :activeDeviceId="activeDeviceId" :deviceList="auxiliaryMonitorList"
      @changeEvent="changeEvent">
      <template v-slot:status="{ data }">
        <AuxiliaryDeviceStatusCom :deviceInfo="data"></AuxiliaryDeviceStatusCom>
      </template>
      <template v-slot:content="{ data }">
        <AuxiliaryDeviceCardInfo :deviceInfo="data"></AuxiliaryDeviceCardInfo>
      </template>
    </DeviceListCom>
    <div class="flex-all monitoring_content_right">
      <template v-if="activeDeviceId">
        <AuxiliaryDeviceInfoCom :activeDeviceInfo="activeDeviceInfo" @changeEvent="changeEvent">
        </AuxiliaryDeviceInfoCom>
        <el-row :gutter="10" class="bottom_card">
          <el-col v-for="(item, index) in list" :key="index" :xl="item.xl" :lg="item.lg" :sm="item.sm">
            <div class="content_body">
              <component :is="item.componentName" :activeDeviceId="activeDeviceId" :siteId="activeDeviceId"
                :titleName="item.name" :timerType="item.timerType" :fileName="fileName"
                :isRequestType="item.isRequestType" :seriesListArray="item.seriesListArray" :legend="item.legend"
                :yAxisName="item.yAxisName"></component>
            </div>
          </el-col>
        </el-row>
      </template>
      <null-data v-else words="请先选择设备"></null-data>
    </div>
  </div>
</template>

<script>
import { defineComponent, reactive, toRefs, watch } from "vue";
import { findAuxiliaryMonitorList } from "@/api/centralMonitoring/centralMonitoring";
import RemoteControlCom from "./EnergyStorageAssistantMonitoring/RemoteControlCom.vue";
import AuxiliaryDeviceInfoCom from "./EnergyStorageAssistantMonitoring/AuxiliaryDeviceInfoCom.vue";
import { DeviceListCom, AuxiliaryDeviceCardInfo, AuxiliaryDeviceStatusCom, RemoteSignalingAlarmCom, DeviceTelemetryAttrCom, SystemVarTimeChartCom, OperationIndexCom } from "@/views/centralMonitoring/_components/centralMonitoring/PublicComponents";

export default defineComponent({
  name: "EnergyStorageAssistantMonitoring",
  components: {
    AuxiliaryDeviceStatusCom, AuxiliaryDeviceCardInfo, DeviceListCom, AuxiliaryDeviceInfoCom, DeviceTelemetryAttrCom, RemoteSignalingAlarmCom,
    SystemVarTimeChartCom, OperationIndexCom, RemoteControlCom
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
      auxiliaryMonitorList: [],
      listLoading: false,
      activeDeviceId: "",
      activeDeviceInfo: {},

      list: [
        { name: "遥控", componentName: "RemoteControlCom", xl: 8, lg: 10, md: 24, sm: 24 },
        { name: "遥测", componentName: "DeviceTelemetryAttrCom", xl: 16, lg: 14, md: 24, sm: 24 },
        { name: "遥信告警", componentName: "RemoteSignalingAlarmCom", xl: 8, lg: 10, md: 24, sm: 24 },
        {
          name: "温度曲线",
          isRequestType: 2,
          xl: 16, lg: 14, md: 24, sm: 24,
          componentName: "SystemVarTimeChartCom",
          yAxisName: "℃",
          seriesListArray: [
            {
              data: [],
              type: 'line',
              name: '柜内温度',
              color: "#79C3FF",
              showSymbol: false,
              fieldName: 'cabinettemperature',
            },
            {
              data: [],
              type: 'line',
              name: '环境温度',
              color: "#00FF6A",
              showSymbol: false,
              fieldName: 'externaltemperature',
            }
          ]
        },
        { name: "运行指标", componentName: "OperationIndexCom", xl: 8, lg: 10, md: 24, sm: 24 },
        {
          name: "湿度曲线",
          isRequestType: 2,
          xl: 16, lg: 14, md: 24, sm: 24,
          componentName: "SystemVarTimeChartCom",
          yAxisName: "%RH",
          seriesListArray: [
            {
              data: [],
              type: 'line',
              name: '柜内湿度',
              color: "#79C3FF",
              showSymbol: false,
              fieldName: 'humidity',
            }
          ]
        },
      ]
    });

    // 查询辅助设备监测列表数据
    const queryAuxiliaryMonitorList = () => {
      that.listLoading = true;
      findAuxiliaryMonitorList({ siteId: props.siteId, timer: new Date() }).then(res => {
        that.auxiliaryMonitorList = res.data ? res.data : [];
        that.listLoading = false;
      }).catch(() => {
        that.auxiliaryMonitorList = [];
        that.listLoading = false;
      });
    };

    const changeEvent = (data) => {
      if (data.operateType === "PCSDeviceInfoCom") queryAuxiliaryMonitorList();
      if (data.operateType === "DeviceListCom") {
        that.activeDeviceInfo = JSON.parse(JSON.stringify(data ?? {}));
        that.fileName = `${props.siteName}_${data.deviceName}_辅助监控`;
      }
    };

    const watchSiteId = watch(() => props.siteId, (newSiteId) => {
      if (newSiteId) queryAuxiliaryMonitorList();
    }, { deep: true, immediate: true });

    return { ...toRefs(that), changeEvent, watchSiteId, queryAuxiliaryMonitorList };
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