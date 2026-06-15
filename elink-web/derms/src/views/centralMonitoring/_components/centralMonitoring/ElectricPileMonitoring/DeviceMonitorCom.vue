<template>
  <TitleView :isTitleIcon="false" title="设备监控">
    <template #headerRight>
      <el-button :icon="RefreshRight" @click="querySitePileMonitor">刷新</el-button>
    </template>
    <template #content>
      <div v-loading="listLoading" class="device_content">
        <el-row v-if="pileGunList && pileGunList.length" :gutter="12">
          <el-col v-for="(childItem, childIndex) in pileGunList" :key="childIndex" :xl="4" :lg="6" :md="8" :sm="12"
            :xs="24">
            <ChargingGunStatusCardCom :dataInfo="childItem"></ChargingGunStatusCardCom>
          </el-col>
        </el-row>
        <template v-else>
          <null-data words="暂无设备列表！！！"></null-data>
        </template>
      </div>
    </template>
  </TitleView>
</template>

<script lang="ts">
import { RefreshRight } from '@element-plus/icons-vue';
import { defineComponent, reactive, toRefs, watch } from "vue";
import { countSitePileMonitor } from "@/api/operationManagement/CsPileGunRunningStatus";
import ChargingGunStatusCardCom from "./DeviceMonitorCom/ChargingGunStatusCardCom.vue";

export default defineComponent({
  name: "DeviceMonitorCom",
  components: { ChargingGunStatusCardCom },
  props: {
    siteId: {
      type: [Number, String],
      default: ""
    }
  },
  setup(props) {

    const that = reactive({
      pileGunList: [],
      RefreshRight,
      listLoading: false,
    });

    // 统计站点电桩监视数据
    const querySitePileMonitor = () => {
      that.listLoading = true;
      countSitePileMonitor({ siteIds: [props.siteId], scenarioTypes: "3" }).then(res => {
        let pileGunList = [];
        let returnDataInfo = JSON.parse(JSON.stringify(res.data ?? {}));
        if (returnDataInfo.sitePileList && returnDataInfo.sitePileList.length) {
          let findItem = returnDataInfo.sitePileList.find(item => item.siteId === props.siteId);
          if (findItem) pileGunList = findItem?.pileGunList ?? [];
        }
        that.pileGunList = JSON.parse(JSON.stringify(pileGunList));
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      });
    };

    const watchSiteId = watch(() => props.siteId, (newSiteId) => {
      if (newSiteId) querySitePileMonitor();
    }, { deep: true, immediate: true });

    return { ...toRefs(that), querySitePileMonitor, watchSiteId };
  }
});
</script>

<style lang="scss" scoped></style>