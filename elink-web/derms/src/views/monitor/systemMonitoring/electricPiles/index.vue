<template>
  <PageTemplate :view-map="ViewMap" tab-type="3" />
</template>

<script setup>
// 子视图引入
import Overview from "./_overview.vue";
import PileView from "./_pile.vue";
import MeterPileView from "./_meterpile.vue";
import SuperView from "./_superView.vue";
import EmptyView from "@/views/monitor/_slice/base/empty.vue";
// 模板组件引入
import PageTemplate from "../pageTemplate.vue";

// 设备类型与视图的映射
const ViewMap = {
  1: Overview,
  28: PileView,
  29: PileView,
  30: PileView,
  38: MeterPileView,
  39: MeterPileView,
  79: SuperView,


  default: EmptyView,
};
</script>