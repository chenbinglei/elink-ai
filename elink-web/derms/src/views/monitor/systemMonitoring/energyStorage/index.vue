<template>
    <PageTemplate :view-map="ViewMap" tab-type="2" />
</template>

<script setup>
// 子视图引入
import Overview from "./_overview.vue";
import BatteryCluster from './_batteryCluster.vue';
import AirConditioner from './_airConditioner.vue';
import PCSView from "./_pcs.vue";
// import MeterPileView from "./_meterpile.vue";
import MeterPileView from "@/views/monitor/systemMonitoring/electricPiles/_meterpile.vue";
import EmptyView from "@/views/monitor/_slice/base/empty.vue";
// 模板组件引入
import PageTemplate from "../pageTemplate.vue";

// 设备类型与视图的映射
const ViewMap = {
    '1': Overview,
    '23': PCSView,
    '25': BatteryCluster,
    '60': AirConditioner,
    "38": MeterPileView,
    '39': MeterPileView,
    '78': PCSView,
    'default': EmptyView

};

</script>