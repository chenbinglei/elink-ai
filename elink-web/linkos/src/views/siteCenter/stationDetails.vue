<template>
  <div class="app-container">
    <template v-if="tabsArray && tabsArray.length">
      <Tabs v-model:tabsIndex="componentName" :tabsArray="tabsArray" label="label"></Tabs>
      <div class="app-container-right">
        <component :is="componentName" :contentMaxHeight="contentMaxHeight" :siteRecordsId="siteRecordsId"></component>
      </div>
    </template>
    <div v-else class="null-data app-container-right">
      <null-data words="请联系管理员开放权限"></null-data>
    </div>
  </div>
</template>

<script lang="ts">
import {useRoute} from "vue-router";
import {getLeftTreeDataFun} from "@/utils";
import {defineComponent, onMounted, reactive, toRefs} from "vue";
import {ProjectAgreement, EnergyInformation, StationInformation, RelatedPartiesManage, StationSetting, TopologicalNodeCom} from "@/views/siteCenter/component";

export default defineComponent({
  name: "stationDetails",
  props: {
    contentMaxHeight: {
      type: Number,
      default: 520
    }
  },
  components: {ProjectAgreement, EnergyInformation, StationInformation, RelatedPartiesManage, StationSetting, TopologicalNodeCom},
  setup() {
    const route = useRoute();
    const that = reactive({
      tabsArray: [],
      componentName: "",
      siteRecordsId: route.query.id,
    })

    // 查询当前页面的子级权限
    const queryTabsArrayFun = () => {
      that.tabsArray = getLeftTreeDataFun(route.path, 0, 0);
      // console.log(that.tabsArray);
    }

    onMounted(() => {
      queryTabsArrayFun();
    })

    return {...toRefs(that), queryTabsArrayFun,}
  }
})
</script>

<style lang="scss" scoped>
.app-container {
  flex-direction: column;

  :deep(.tabs_list) {
    padding: 0 !important;

    .tabs_li {
      min-height: 32px;
    }
  }
}
</style>