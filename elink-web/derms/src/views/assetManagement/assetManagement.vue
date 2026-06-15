<template>
  <el-row :gutter="10" class="app-container">
    <el-col :span="19">
      <div class="app-container-right">
        <div class="content_left_top">
          <AssetStatisticsCom
            :dataPSCAssetCountInfo="dataPSCAssetCountInfo"
            :listLoading="listLoading"
          />
          <AssetStatisticsMap ref="assetStatisticsMapRef"></AssetStatisticsMap>
        </div>
        <div class="content_left_bottom">
          <PvPowerCurveCom v-if="scenarioType === 1"></PvPowerCurveCom>
          <ESPowerCurveCom v-if="scenarioType === 2"></ESPowerCurveCom>
          <EpPowerCurveCom v-if="scenarioType === 3"></EpPowerCurveCom>
        </div>
      </div>
    </el-col>
    <el-col :span="5">
      <div class="app-container-left">
        <PvPowerGenerationAnalysisCom
          v-if="scenarioType === 1"
        ></PvPowerGenerationAnalysisCom>
        <ESDisAndChargeAnalysisCom
          v-if="scenarioType === 2"
        ></ESDisAndChargeAnalysisCom>
        <EpDisAndChargeAnalysisCom
          v-if="scenarioType === 3"
        ></EpDisAndChargeAnalysisCom>
      </div>
    </el-col>
  </el-row>
</template>

<script lang="ts">
import { useAssetManagementStore } from '@/stores/index';

import { findPSCAssetCountData } from "@/api/assetManagement/assetManagement";
import {
  reactive,
  defineComponent,
  toRefs,
  computed,
  watch,
  defineAsyncComponent,
} from "vue";
const AssetStatisticsMap = defineAsyncComponent(() =>
  import(
    "@/views/assetManagement/_components/assetManagement/AssetStatisticsMap.vue"
  )
);
import {
  ESPowerCurveCom,
  PvPowerCurveCom,
  AssetStatisticsCom,
  ESDisAndChargeAnalysisCom,
  PvPowerGenerationAnalysisCom,
  EpPowerCurveCom,
  EpDisAndChargeAnalysisCom,
} from "@/views/assetManagement/_components";

export default defineComponent({
  name: "assetManagement",
  components: {
    ESPowerCurveCom,
    PvPowerCurveCom,
    AssetStatisticsCom,
    ESDisAndChargeAnalysisCom,
    PvPowerGenerationAnalysisCom,
    AssetStatisticsMap,
    EpPowerCurveCom,
    EpDisAndChargeAnalysisCom,
  },
  setup() {
    const assetManagementStore = useAssetManagementStore();
    const scenarioType = computed(() => {
      return assetManagementStore.scenarioType;
    });

    const siteAllIds = computed(() => {
      return assetManagementStore.siteAllIds;
    });

    const updateTimeNum = computed(() => {
      return assetManagementStore.updateTimeNum;
    });

    const that = reactive({
      listLoading: false,
      dataPSCAssetCountInfo: {}, // 资产统计和功率曲线数据
    });

    // 查询资产统计和功率曲线数据
    const queryPSCAssetCountData = () => {
      that.listLoading = true;

      if (!siteAllIds.value || !siteAllIds.value.length) {
        that.dataPSCAssetCountInfo = {};
        that.listLoading = false;
        return;
      }

      findPSCAssetCountData(
        { timer: new Date(), siteIds: siteAllIds.value },
        scenarioType.value
      )
        .then((res) => {
          that.dataPSCAssetCountInfo = res.data ? res.data : {};
          that.listLoading = false;
        })
        .catch(() => {
          that.dataPSCAssetCountInfo = {};
          that.listLoading = false;
        });
    };

    // 监听 updateTimeNum 重新获取资产数据
    const watchUpdateTimeNum = watch(
      () => updateTimeNum,
      (newUpdateTimeNum) => {
        queryPSCAssetCountData(); // 查询资产统计和功率曲线数据
      },
      { deep: true }
    );

    return {
      ...toRefs(that),
      scenarioType,
      siteAllIds,
      queryPSCAssetCountData,
      watchUpdateTimeNum,
      updateTimeNum,
    };
  },
});
</script>

<style lang="scss" scoped>
.app-container-right {
  .content_left_top {
    flex: 1;
    height: 2px;
    display: flex;
    align-items: center;
  }

  .content_left_bottom {
    width: 100%;
    height: 35%;
    max-height: 380px;
    padding-top: 10px;
    box-sizing: border-box;
  }
}

.app-container-left {
  width: 100%;
  height: 100%;
}
</style>