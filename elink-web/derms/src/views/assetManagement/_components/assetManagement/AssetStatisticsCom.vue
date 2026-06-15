<template>
  <div class="assetStatisticsCom">
    <title-icon-view isContentHeight title="资产统计">
      <template #icon>
        <span class="iconfont icon-zichantongji"></span>
      </template>
      <template #content>
        <div class="content_body" v-loading="listLoading">
          <AssetStatisticsNumber :dataPSCAssetCountInfo="dataPSCAssetCountInfo"></AssetStatisticsNumber>
          <AssetStatisticsCard :dataPSCAssetCountInfo="dataPSCAssetCountInfo"></AssetStatisticsCard>
          <AssetStatisticsTimeNum :dataPSCAssetCountInfo="dataPSCAssetCountInfo"></AssetStatisticsTimeNum>
        </div>
      </template>
    </title-icon-view>
  </div>
</template>

<script lang="ts">
import {reactive, defineComponent, toRefs, onMounted} from "vue";
import {AssetStatisticsNumber, AssetStatisticsCard, AssetStatisticsTimeNum} from "./AssetStatisticsCom/index";

export default defineComponent({
  name: "AssetStatisticsCom",
  components: {AssetStatisticsNumber, AssetStatisticsCard, AssetStatisticsTimeNum},
  props: {
    dataPSCAssetCountInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
    listLoading:{
      type: Boolean,
      default: false
    }
  },
  setup() {

    const that = reactive({});

    onMounted(() => {});

    return {...toRefs(that)};
  }
});
</script>

<style lang="scss" scoped>
.assetStatisticsCom {
  width: 420px;
  height: 100%;
  margin-right: 4px;

  .content_body {
    height: 100%;
    border-radius: 6px;
    box-sizing: border-box;
    padding: 16px 14px 14px 14px;
    //background: rgba(255, 255, 255, 0.03);
    background: #ffffff08;

    display: flex;
    flex-direction: column;
  }
}
</style>