<template>
  <div class="assetStatisticsCard">
    <div class="content_list_li">
      <div class="content_list_li_left">
        <img class="content_list_li_img" src="@/assets/image/shishichuli.png" alt="" />
      </div>
      <div class="content_list_li_right">
        <div class="content_list_li_title">
          <template v-if="scenarioType === 1">实时出力</template>
          <template v-if="scenarioType === 2">充电功率</template>
          <template v-if="scenarioType === 3">充电功率</template>
        </div>
        <div class="content_list_li_bottom">
          <div class="content_list_li_number">
            <template v-if="scenarioType === 1">{{ $filters.numberValue(returnDataInfo.realOutput) }}</template>
            <template v-if="scenarioType === 2">{{ $filters.numberValue(returnDataInfo.chargePower) }}</template>
            <template v-if="scenarioType === 3">{{ $filters.numberValue(returnDataInfo.chargePower) }}</template>
          </div>
          <div class="content_list_li_unit">
            <template v-if="scenarioType === 1">{{ $filters.numberUnit(returnDataInfo.realOutput,'kW') }}</template>
            <template v-if="scenarioType === 2">{{ $filters.numberUnit(returnDataInfo.chargePower,'kW') }}</template>
            <template v-if="scenarioType === 3">{{ $filters.numberUnit(returnDataInfo.chargePower,'kW') }}</template>
          </div>
        </div>
      </div>
    </div>
    <div class="content_list_li">
      <div class="content_list_li_left">
        <img class="content_list_li_img" src="@/assets/image/yunxingxiaolv.png" alt="" />
      </div>
      <div class="content_list_li_right">
        <div class="content_list_li_title">
          <template v-if="scenarioType === 1">系统效率</template>
          <template v-if="scenarioType === 2">放电功率</template>
          <template v-if="scenarioType === 3">V2G功率</template>
        </div>
        <div class="content_list_li_bottom">
          <div class="content_list_li_number">
            <template v-if="scenarioType === 1">{{ $filters.numberNull(returnDataInfo.runEfficiency) }}</template>
            <template v-if="scenarioType === 2">{{ $filters.numberValue(returnDataInfo.dischargePower) }}</template>
            <template v-if="scenarioType === 3">{{ $filters.numberValue(returnDataInfo.v2gPower) }}</template>
          </div>
          <div class="content_list_li_unit">
            <template v-if="scenarioType === 1">%</template>
            <template v-if="scenarioType === 2">{{ $filters.numberUnit(returnDataInfo.dischargePower,'kW') }}</template>
            <template v-if="scenarioType === 3">{{ $filters.numberUnit(returnDataInfo.v2gPower,'kW') }}</template>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { useAssetManagementStore } from '@/stores/index';

import {reactive, defineComponent, toRefs, onMounted, computed, watch} from "vue";

export default defineComponent({
  name: "AssetStatisticsCard",
  props: {
    dataPSCAssetCountInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {

    const assetManagementStore = useAssetManagementStore();
    const scenarioType = computed(() => {
      return assetManagementStore.scenarioType;
    });

    const that = reactive({
      returnDataInfo: {},
    });

    onMounted(() => {

    });

    // 监听数据
    const watchDataPSCAssetCountInfo = watch(()=>props.dataPSCAssetCountInfo,(newDataPSCAssetCountInfo)=>{
      // console.log(newDataPSCAssetCountInfo);
      that.returnDataInfo = JSON.parse(JSON.stringify(newDataPSCAssetCountInfo));
    },{ deep: true });

    return {...toRefs(that),scenarioType,watchDataPSCAssetCountInfo};
  }
});
</script>
<style lang="scss" scoped>
.assetStatisticsCard{
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;

  .content_list_li{
    flex: 1;
    display: flex;
    align-items: center;
    margin-right: 8px;
    padding: 8px 12px 8px 12px;
    box-sizing: border-box;
    background: linear-gradient(180deg, #3fa3ff99 0%, #00235d30 100%);

    .content_list_li_img{
      width: 36px;
      height: 36px;
    }

    .content_list_li_right{
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;

      .content_list_li_title{
        color: #b1e3ff;
        font-size: 14px;
        margin-bottom: 12px;
        text-align: center;
      }

      .content_list_li_bottom{
        display: flex;
        align-items: center;

        .content_list_li_number{
          color: #ffffff;
          font-size: 16px;
          font-weight: 800;
        }

        .content_list_li_unit{
          color: #a1ddfe80;
          font-size: 14px;
          margin-left: 4px;
        }
      }
    }

    &:last-child{
      margin-right: 0;
    }
  }
}
</style>