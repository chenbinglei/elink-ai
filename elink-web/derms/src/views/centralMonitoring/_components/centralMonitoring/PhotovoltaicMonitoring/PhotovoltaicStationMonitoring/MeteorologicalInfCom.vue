<template>
  <TitleView :isTitleIcon="false" :title="titleName" isContentHeight>
    <template #headerRight>
      <div class="header-button">
        <ButtonsTabs :tabsIndex="tabsIndex" :tabsArray="tabsArray"></ButtonsTabs>
      </div>
    </template>
    <template #content>
      <div v-loading="listLoading" class="content_body">
        <WeatherActualityCom v-if="tabsIndex === 1" :returnDataInfo="returnDataInfo"/>
        <WeatherForecastCom v-if="tabsIndex === 2" :returnDataInfo="returnDataInfo"/>
      </div>
    </template>
  </TitleView>
</template>

<script lang="ts">
import { useEnergyManagementStore } from '@/stores/index';

import {reactive, defineComponent, toRefs, computed, watch} from "vue";
import {findWeatherForecast} from "@/api/centralMonitoring/centralMonitoring";
import WeatherForecastCom from "./MeteorologicalInfCom/WeatherForecastCom.vue";
import WeatherActualityCom from "./MeteorologicalInfCom/WeatherActualityCom.vue";

export default defineComponent({
  name: "MeteorologicalInfCom",
  components: {WeatherActualityCom, WeatherForecastCom},
  props: {
    siteId: {
      type: [Number, String],
      default: ""
    },
    titleName: {
      type: String,
      default: ""
    },
  },
  setup(props) {
    const energyManagementStore = useEnergyManagementStore();
    const activeSlSiteInfo = computed(() => {
      return energyManagementStore.activeSlSiteInfo;
    });

    const that = reactive({
      tabsIndex: 1,
      listLoading: false,
      returnDataInfo: {},
      tabsArray: [{name: "实况", id: 1}, {name: "预报", id: 2}],
    });

    // 根据经纬度获取天气预报数据
    const queryWeatherForecast = () => {
      that.listLoading = true;
      let {location} = activeSlSiteInfo.value;
      if(location) location = JSON.parse(location);

      if (!location?.longitude || !location?.latitude) {
        that.returnDataInfo = {};
        that.listLoading = false;
        return;
      }

      findWeatherForecast({longitude: location.longitude, latitude: location.latitude, siteId: props.siteId}).then(res => {
        let returnDataInfo = res.data ? res.data : {};
        that.returnDataInfo = {...returnDataInfo,...location};
        that.listLoading = false;
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return;
        that.returnDataInfo = {};
      });
    };

    const watchSiteId = watch(() => props.siteId, (newSiteId) => {
      if (newSiteId) queryWeatherForecast();
    }, {deep: true, immediate: true});

    return {...toRefs(that), activeSlSiteInfo, queryWeatherForecast, watchSiteId};
  }
});

</script>

<style lang="scss" scoped>
.header-button {
  display: flex;
  justify-content: flex-end;

  :deep(.buttonsTabs) {
    width: fit-content;
    margin-right: 8px;

    .tabs_li {
      max-height: 32px;
      margin-right: 4px;
      padding: 6px 10px;
    }
  }
}

.content_body {
  height: 100%;
}
</style>