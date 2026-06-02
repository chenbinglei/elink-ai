<template>
  <div class="chargingGunStatusCardCom">
    <div class="content_title">
      <span class="pileCode">{{ $filters.moreData(pileDataInfo.pileCode) }}</span>
      <span>-</span>
      <span class="gunCode">{{ $filters.moreData(pileDataInfo.gunCode) }}</span>
    </div>
    <div class="content_bottom">
      <div class="content_bottom_left">
        <div :class="'pile_type_' + pileDataInfo.pileType" class="content_bottom_left_li">
          {{ $filters.pileType(pileDataInfo.pileType) }}
        </div>
        <div class="content_bottom_left_li">
          <span>{{ $filters.moreData(pileDataInfo.ratedPower) }}</span>
          <span>kW</span>
        </div>
      </div>
      <div class="content_bottom_right">
        <template v-if="pileDataInfo.gunWorkState === 1 || pileDataInfo.gunWorkState === 2">
          <ChargingGunProgressCircle :batterySOC="pileDataInfo.batterySOC" :gunWorkState="pileDataInfo.gunWorkState" />
        </template>
        <div v-else :class="'gun_status_' + pileDataInfo.gunWorkState" class="gun_status_class">
          <span v-if="pileDataInfo.gunWorkState === 3" class="iconfont icon-kongxianmoshi"></span>
          <span v-else-if="pileDataInfo.gunWorkState === 4" class="iconfont icon-zhanyong"></span>
          <span v-else-if="pileDataInfo.gunWorkState === 5" class="iconfont icon-guzhang"></span>
          <span v-else-if="pileDataInfo.gunWorkState === 6" class="iconfont icon-lixian"></span>
          <span v-else-if="pileDataInfo.gunWorkState === 8" class="iconfont icon-yuyue"></span>
          <span v-else class="iconfont icon-qita"></span>
          <span>{{ $filters.gunWorkState(pileDataInfo.gunWorkState) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {defineComponent, reactive, toRefs, watch} from "vue";
import ChargingGunProgressCircle from "./ChargingGunProgressCircle.vue";

export default defineComponent({
  name: "ChargingGunStatusCardCom",
  components: {ChargingGunProgressCircle},
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
      // activePileId: "2c9e90d98f28ed88018f521c59c50024",
      // chargingPileDetailVisible: false,
    });

    const watchDataInfo = watch(() => props.dataInfo, (newDataInfo) => {
      that.pileDataInfo = JSON.parse(JSON.stringify(newDataInfo));
    }, {deep: true});

    return {...toRefs(that), watchDataInfo};
  }
});
</script>

<style lang="scss" scoped>
.chargingGunStatusCardCom {
  width: 100%;
  cursor: pointer;
  padding: 12px 14px;
  border-radius: 6px;
  background: #ffffff0d;
  box-sizing: border-box;
  margin-bottom: 16px;

  .content_title {
    font-size: 18px;
    color: #ffffffcc;
    margin-bottom: 4px;
  }

  .content_bottom {
    display: flex;
    align-items: flex-end;
    justify-content: space-between;
    padding-right: 12px;
    box-sizing: border-box;

    .content_bottom_left {
      display: flex;
      align-items: center;

      .content_bottom_left_li {
        width: 55px;
        height: 30px;
        border-radius: 4px;
        text-align: center;
        line-height: 30px;
        margin-right: 12px;

        &:last-child {
          color: #00e0ff;
          margin-right: 0;
          background: #00e0ff4d;
        }
      }

      .pile_type_28 {
        color: #00ff75;
        background: #00ff754d;
      }

      .pile_type_29 {
        color: #00f0ff;
        background: #70eeff4d;
      }

      .pile_type_30 {
        color: #ffd600;
        background: #ffd6004d;
      }
    }

    .content_bottom_right {
      width: 80px;
      height: 80px;

      .gun_status_class {
        width: 100%;
        height: 100%;
        border-radius: 50%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;

        color: #ffffff66;
        background: #ffffff1A;

        .iconfont {
          font-size: 24px;
          margin-bottom: 4px;
        }
      }

      .gun_status_3{
        color: #00D1FFFF;
        background: #00D1FF1A;
      }

      .gun_status_8{
        color: #ad00ff;
        background: #ad00ff1a;
      }

      .gun_status_7 {
        color: rgba(255,255,255,.25);
        background: #00D1FF0B;
      }

      .gun_status_4 {
        color: #ffb800;
        background: #ffb8001a;
      }

      .gun_status_5 {
        color: #ff1818;
        background: #ff18181a;
      }
    }
  }
}
</style>