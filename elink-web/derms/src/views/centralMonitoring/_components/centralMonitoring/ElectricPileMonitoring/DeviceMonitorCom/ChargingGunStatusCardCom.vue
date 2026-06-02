<template>
  <div class="chargingGunStatusCardCom">
    <div class="card_content_top">
      <div class="content_title textTwo">
        <span class="pileCode">{{ $filters.moreData(pileDataInfo.pileCode) }}</span>
        <span>-</span>
        <span class="gunCode">{{ $filters.moreData(pileDataInfo.gunCode) }}</span>
      </div>
      <div class="gun_status" :class="'gun_status_' + pileDataInfo.gunWorkState">
        <span class="gunWorkState">{{ $filters.gunWorkState(pileDataInfo.gunWorkState) }}</span>
      </div>
    </div>
    <div class="card_content_bottom">
      <div class="battery_class">
        <BatteryCartoon :batteryNum="pileDataInfo.batterySOC" :gunWorkState="pileDataInfo.gunWorkState" direction="horizontal"></BatteryCartoon>
      </div>
    </div>
  </div>
</template>

<script>
import BatteryCartoon from "./BatteryCartoon.vue";
import {defineComponent, reactive, toRefs, watch} from "vue";

export default defineComponent({
  name: "ChargingGunStatusCardCom",
  components:{BatteryCartoon},
  props: {
    dataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {

    const that = reactive({
      pileDataInfo: props.dataInfo,
    });

    const watchDataInfo = watch(() => props.dataInfo, (newDataInfo) => {
      that.pileDataInfo = JSON.parse(JSON.stringify(newDataInfo));
    }, {deep: true});

    return {...toRefs(that), watchDataInfo};
  }
});
</script>

<style lang="scss" scoped>
.chargingGunStatusCardCom{
  padding: 10px 10px;
  border-radius: 6px;
  background: #003559;
  margin-bottom: 12px;
  box-sizing: border-box;
  background: linear-gradient(205.99deg, #09545580 14.49%, #1594bb80 89.5%);

  .card_content_top{
    display: flex;
    align-items: center;
    margin-bottom: 10px;

    .content_title{
      flex: 1;
      color: #f4f4f4;
      font-size: 14px;
      -webkit-line-clamp: 1;
    }

    .gun_status{
      font-size: 14px;
      color: #ffffff66;
      border-right: 4px;
      padding: 2px 6px;
      background: #c6c6c647;
      box-sizing: border-box;
    }

    .gun_status_1 {
      color: #00f0ff;
      background: #35fbfa47;
    }

    .gun_status_2 {
      color: rgba(255, 248, 134, 1);
      background: rgba(255, 248, 134, .5);
    }

    .gun_status_3{
      color: #00d1ff;
      background: rgba(0, 209, 255, .102);
    }

    .gun_status_4 {
      color: #ffb800;
      background: #ffb8001a;
    }

    .gun_status_5 {
      color: #ff1818;
      background: #ff18181a;
    }

    .gun_status_8{
      color: #ad00ff;
      background: #ad00ff1a;
    }

    .gun_status_7 {
      color: rgba(255,255,255,.25);
      background: #00D1FF0B;
    }
  }

  .card_content_bottom{
    box-sizing: border-box;

    .battery_class{
      width: 138px;
      height: 58px;
    }
  }
}
</style>