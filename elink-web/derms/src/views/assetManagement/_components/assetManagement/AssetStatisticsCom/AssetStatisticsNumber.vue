<template>
  <div class="content_body_top">
    <div class="content_body_top_l">
      <el-carousel ref="carouselRef" :autoplay="false" arrow="always" height="68px" indicator-position="none"
        @change="carouselChangeFun">
        <el-carousel-item v-for="(item, index) in scenarioTypeList" :key="index">
          <img :src="item.image" alt="" class="carousel_image" />
        </el-carousel-item>
      </el-carousel>
    </div>
    <div class="content_body_top_r">
      <div class="content_body_top_r_li">
        <div class="top_r_li_title">装机量</div>
        <div class="top_r_li_number">
          <template v-if="scenarioType === 2">
            <span class="capacity">{{ $filters.numberValue(returnDataInfo.stationRatedpower) }}</span>
            <span class="unit">{{ $filters.numberUnit(returnDataInfo.stationRatedpower, '') }}</span>
            <span>/</span>
          </template>
          <span class="capacity">{{ $filters.numberValue(returnDataInfo.capacity) }}</span>
          <span class="unit">{{ $filters.numberUnit(returnDataInfo.capacity, '') }}</span>
        </div>
        <div class="top_r_li_unit">
          <span>kW</span>
          <span>/</span>
          <span v-if="scenarioType === 1">kWp</span>
          <span v-if="scenarioType === 2">kWh</span>
          <span v-if="scenarioType === 3">kWh</span>
        </div>
      </div>
      <div class="content_body_top_r_li">
        <div class="top_r_li_title">
          <template v-if="scenarioType === 1">
            <span v-if="activeSiteId">逆变器</span>
            <span v-else>站点数</span>
          </template>
          <template v-if="scenarioType === 2">
            <span v-if="activeSiteId">PCS数量</span>
            <span v-else>站点数</span>
          </template>
          <template v-if="scenarioType === 3">桩/枪</template>
        </div>
        <div class="top_r_li_number">
          <template v-if="scenarioType === 1">
            <span v-if="activeSiteId">{{ $filters.numberNull(returnDataInfo.inverterNum) }}</span>
            <span v-else>{{ $filters.numberNull(returnDataInfo.siteNum) }}</span>
          </template>
          <template v-if="scenarioType === 2">
            <span v-if="activeSiteId">{{ $filters.numberNull(returnDataInfo.pcsDeviceNum) }}</span>
            <span v-else>{{ $filters.numberNull(returnDataInfo.siteNum) }}</span>
          </template>
          <template v-if="scenarioType === 3">
            <span>{{ $filters.numberNull(returnDataInfo.pileNum) }}</span>
            <span>/</span>
            <span>{{ $filters.numberNull(returnDataInfo.gunNum) }}</span>
          </template>
        </div>
        <div class="top_r_li_unit">
          <template v-if="scenarioType === 1">
            <span v-if="activeSiteId">台</span>
            <span v-else>座</span>
          </template>
          <template v-if="scenarioType === 2">
            <span v-if="activeSiteId">台</span>
            <span v-else>座</span>
          </template>
          <template v-if="scenarioType === 3">个</template>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { useStore } from 'vuex';
import { computed, defineComponent, onMounted, reactive, toRefs, watch, ref } from "vue";
//
import photovoltaicIcon from "@/assets/image/photovoltaic.png";
import energyStorageIcon from "@/assets/image/energy_storage.png";
import electricPileIcon from "@/assets/image/electric_pile.png";

export default defineComponent({
  name: "AssetStatisticsNumber",
  props: {
    dataPSCAssetCountInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {

    const store = useStore();
    const scenarioType = computed(() => {
      return store.state.assetManagement.scenarioType;
    });

    const activeSiteId = computed(() => {
      return store.state.assetManagement.activeSiteId;
    });

    const that = reactive({
      activeIndex: 0,
      returnDataInfo: {},
      scenarioTypeList: [
        { id: 1, name: "光伏", image: photovoltaicIcon },
        { id: 2, name: "储能", image: energyStorageIcon },
        { id: 3, name: "电桩", image: electricPileIcon },
      ]
    });

    const carouselRef = ref(null);
    const carouselChangeFun = (newIndex, oldIndex) => {
      // console.log(newIndex);
      // console.log(oldIndex);
      let findItem = that.scenarioTypeList[newIndex];
      store.dispatch("updateActiveSiteId", ""); // 清除当前选中的站点
      store.dispatch("updateScenarioType", findItem.id);
    };

    // 监听数据
    const watchDataPSCAssetCountInfo = watch(() => props.dataPSCAssetCountInfo, (newDataPSCAssetCountInfo) => {
      // console.log(newDataPSCAssetCountInfo);
      that.returnDataInfo = JSON.parse(JSON.stringify(newDataPSCAssetCountInfo));
    }, { deep: true });

    onMounted(() => {
      let activeIndex = that.scenarioTypeList.findIndex(item => item.id === scenarioType.value);
      carouselRef.value.setActiveItem(activeIndex);
    });

    return { ...toRefs(that), carouselChangeFun, scenarioType, watchDataPSCAssetCountInfo, carouselRef, activeSiteId };
  }
});
</script>
<style lang="scss" scoped>
.content_body_top {
  display: flex;
  align-items: center;

  .content_body_top_l {
    width: 120px;
    margin-right: 12px;

    :deep(.el-carousel) {
      --el-carousel-arrow-size: 28px;

      .el-carousel__arrow--left {
        left: 0;
      }

      .el-carousel__arrow--right {
        right: 0;
      }

      .el-carousel__item {
        display: flex;
        align-items: center;
        justify-content: center;


        .carousel_image {
          width: 68px;
          height: 68px;
        }
      }
    }
  }

  .content_body_top_r {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-around;

    .content_body_top_r_li {
      display: flex;
      flex-direction: column;
      align-items: center;

      .top_r_li_title {
        color: #b1e3ff;
        font-size: 14px;
      }

      .top_r_li_number {
        color: #ffffff;
        font-size: 16px;
        font-weight: 800;
        margin: 6px 0;
      }

      .top_r_li_unit {
        font-size: 14px;
        color: #a1ddfe80;
      }
    }
  }
}
</style>