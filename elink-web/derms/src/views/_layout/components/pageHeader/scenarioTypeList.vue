<template>
  <div class="flex items-center justify-start w-full h-full">
    <span
      class="siteScenarioText"
      v-for="item in ScenarioTypeList"
      :key="item.id"
      >{{ item.name }}</span
    >
  </div>
</template>

<script setup>
import { SiteScenarioTypeList } from "@/common/enum.js";
import { computed } from "vue";
// 读取外部传入字符串数组参数scenarioTypes
const props = defineProps({
  scenarioTypes: {
    type: String,
    default: "",
  },
});
const ScenarioTypeList = computed(() => {
  // 将字符串转换为数组
  return props.scenarioTypes
    .split(",")
    .map((item) => {
      let findedItem = SiteScenarioTypeList.find((type) => type.id === item);
      if (findedItem) {
        findedItem = {
          ...findedItem,
          img: `/img/scenarioType/${findedItem.id}.png`,
        };
      }
      return findedItem;
    })
    .filter((item) => item !== undefined);
});
</script>

<style scoped>
.siteScenarioText {
  margin-right: 5px;
  font-size: 12px;
  padding: 3px 10px;
  background: rgba(0, 84, 128, 0.9);
  box-shadow: inset 0px 0px 6px 1px #3baaf5;
  border-radius: 3px;
  border: 1px solid rgba(3, 165, 255, 0.25);
}
</style>