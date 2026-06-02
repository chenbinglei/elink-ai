<template>
    <PageTemplate :view-map="ViewMap" />
</template>

<script setup>
// 子视图引入
import Overview from "./_overview.vue";
import WeatherStationView from "./_weatherStation.vue";
import ConverterView from "./_converter.vue";
import EmptyView from "@/views/monitor/_slice/base/empty.vue";
import MeterPileView from "@/views/monitor/systemMonitoring/electricPiles/_meterpile.vue";

// 模板组件引入
import PageTemplate from "../pageTemplate.vue";

// 设备类型与视图的映射
const ViewMap = {
    '1': Overview,
    '20': ConverterView,
    '65': WeatherStationView,
    "38": MeterPileView,
    '39': MeterPileView,
    '77': ConverterView,

    'default': EmptyView
};

</script>