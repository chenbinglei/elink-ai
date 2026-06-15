<template>
  <div v-loading="listLoading" class="monitoring_content">
    <DeviceListCom :activeDeviceId="activeDeviceId" :deviceList="pcsDeviceList" @changeEvent="changeEvent">
      <template v-slot:status="{ data }">
        <PCSDeviceStatusCom :deviceInfo="data"></PCSDeviceStatusCom>
      </template>
      <template v-slot:content="{ data }">
        <PCSDeviceCardInfo :deviceInfo="data"></PCSDeviceCardInfo>
      </template>
    </DeviceListCom>
    <div class="flex-all monitoring_content_right">
      <template v-if="activeDeviceId">
        <PCSDeviceInfoCom :activeDeviceInfo="activeDeviceInfo" @changeEvent="changeEvent"></PCSDeviceInfoCom>
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

<script lang="ts">
import { defineComponent, reactive, toRefs, watch } from "vue";
import { findPcsMonitorList } from "@/api/centralMonitoring/centralMonitoring";
import PCSDeviceInfoCom from "./EnergyStoragePCSMonitoring/PCSDeviceInfoCom.vue";
import RemoteControlCom from "./EnergyStoragePCSMonitoring/RemoteControlCom.vue";
import { DeviceListCom, PCSDeviceCardInfo, PCSDeviceStatusCom } from "@/views/centralMonitoring/_components/centralMonitoring/PublicComponents";
import { RemoteSignalingAlarmCom, DeviceTelemetryAttrCom, SystemVarTimeChartCom, OperationIndexCom } from "@/views/centralMonitoring/_components/centralMonitoring/PublicComponents";

export default defineComponent({
  name: "EnergyStoragePCSMonitoring",
  components: {
    PCSDeviceStatusCom, PCSDeviceCardInfo, DeviceListCom, PCSDeviceInfoCom, RemoteControlCom, DeviceTelemetryAttrCom, RemoteSignalingAlarmCom,
    SystemVarTimeChartCom, OperationIndexCom
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
      pcsDeviceList: [],
      listLoading: false,
      activeDeviceId: "",
      activeDeviceInfo: {},

      list: [
        { name: "遥控", componentName: "RemoteControlCom", xl: 8, lg: 10, md: 24, sm: 24 },
        { name: "遥测", componentName: "DeviceTelemetryAttrCom", xl: 16, lg: 41, md: 24, sm: 24 },
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
              isNoAreaStyle: true, //不绘制
              fieldName: 'pcs_activepower',
            },
            {
              data: [],
              type: 'line',
              yAxisIndex: 1,
              name: '无功功率',
              showSymbol: false,
              isNoAreaStyle: true, //不绘制
              fieldName: 'pcs_reactivepower',
            }
          ]
        },
        { name: "运行指标", componentName: "OperationIndexCom", xl: 8, lg: 10, md: 24, sm: 24 },
        {
          name: "充放电量",
          timerType: 2,
          yAxisName: "kWh",
          isRequestType: 8,
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
              name: '充电电量',
              fieldName: 'chargeQtList'
            },
            {
              data: [],
              type: 'bar',
              barMaxWidth: 12,
              name: '放电电量',
              fieldName: 'dischargeQtList'
            }
          ]
        },
      ]
    });

    // 查询PCS设备监测列表数据
    const queryPcsMonitorList = () => {
      that.listLoading = true;
      findPcsMonitorList({ siteId: props.siteId, timer: new Date() }).then(res => {
        that.pcsDeviceList = res.data ? res.data : [];
        that.listLoading = false;
      }).catch(() => {
        that.pcsDeviceList = [];
        that.listLoading = false;
      });
    };

    const changeEvent = (data) => {
      if (data.operateType === "PCSDeviceInfoCom") queryPcsMonitorList();
      if (data.operateType === "DeviceListCom") {
        that.activeDeviceInfo = JSON.parse(JSON.stringify(data ?? {}));
        that.fileName = `${props.siteName}_${data.deviceName}_逆变器监控`;
      }
    };

    const watchSiteId = watch(() => props.siteId, (newSiteId) => {
      if (newSiteId) queryPcsMonitorList();
    }, { deep: true, immediate: true });

    return { ...toRefs(that), changeEvent, watchSiteId, queryPcsMonitorList };
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