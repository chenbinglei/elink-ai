<template>
  <div class="station-details">
    <StationTopInfo @tabChangeEvent="tabChangeEventHandler"></StationTopInfo>
    <PowerStationOverview v-if="tabActive == 0" :siteId="siteId"></PowerStationOverview>
    <PhotovoltaicOverview v-if="tabActive == 1" :siteId="siteId"></PhotovoltaicOverview>
    <StoredEnergyOverview v-if="tabActive == 2" :siteId="siteId"></StoredEnergyOverview>
    <ChargingPileOverview v-if="tabActive == 3" :siteId="siteId"></ChargingPileOverview>
    <ReplaceBatteryOverview v-if="tabActive == 6" :siteId="siteId"></ReplaceBatteryOverview>
    <parkingOverview v-if="tabActive == 9" :siteId="siteId"></parkingOverview>
  </div>
</template>
<script>
import { reactive, defineComponent, toRefs, provide, computed } from "vue";
import {
  ChargingPileOverview,
  PhotovoltaicOverview,
  PowerStationOverview, ReplaceBatteryOverview,
  StationTopInfo,
  StoredEnergyOverview,
  parkingOverview
} from "@/views/stationDetails/components";
import { useRoute } from "vue-router";
export default defineComponent({
  name: "StationDetails",
  components: { ReplaceBatteryOverview, ChargingPileOverview, StoredEnergyOverview, PhotovoltaicOverview, PowerStationOverview, StationTopInfo, parkingOverview },
  setup () {
    const that = reactive({
      siteId: '',
      tabActive: 0,
    });
    const tabChangeEventHandler = (obj) => {
      console.log(obj);
      that.tabActive = obj.type;
      that.siteId = obj.siteId || that.siteId;
      provide('siteId', computed(() => that.siteId))
    }
    const route = useRoute();
    if (route.query.type) that.tabActive = route.query.type
    if (route.query.siteId) {
      that.siteId = route.query.siteId
      provide('siteId', computed(() => that.siteId))
    }
    return { ...toRefs(that), tabChangeEventHandler };
  }
});
</script>
<style scoped lang="scss">
.station-details {
  height: calc(100vh - 147px);
  background: url("@/assets/image/station-details/sta-detail-bg.png") no-repeat;
  background-size: 100% 100%;
}
</style>