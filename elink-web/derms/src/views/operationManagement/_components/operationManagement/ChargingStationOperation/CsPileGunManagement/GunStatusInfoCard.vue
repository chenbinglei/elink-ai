<template>
  <div class="gunStatusInfoCard">
    <template v-if="list && list.length">
      <template v-for="(item,index) in list" :key="index">
        <div class="gun_li">
          <div :class="'gun_round' + item.gunWorkState" class="gun_round"></div>
          <div class="gunName">{{ item.gunName }}</div>
          <div class="gunWorkState">{{ $filters.gunWorkState(item.gunWorkState) }}</div>
        </div>
      </template>
    </template>
    <template v-else>--</template>
  </div>
</template>
<script lang="ts">
import {reactive, toRefs, defineComponent, watch} from "vue";

export default defineComponent({
  name: "GunStatusInfoCard",
  props: {
    gunStateInfoList: {
      type: Array,
      default: () => []
    }
  },
  setup(props) {
    const that = reactive({
      list: props.gunStateInfoList
    });

    const watchGunStateInfoList = watch(() => props.gunStateInfoList, (newGunStateInfoList) => {
      that.list = newGunStateInfoList;
    }, {deep: true});

    return {...toRefs(that), watchGunStateInfoList};
  }
});
</script>
<style lang="scss" scoped>
.gunStatusInfoCard {
  width: 100%;
  display: flex;
  flex-wrap: wrap;

  .gun_li {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    margin-right: 32px;

    .gun_round {
      width: 8px;
      height: 8px;
      background: #E66AE3;
      border-radius: 50%;
    }

    .gun_round1 {
      background: #41CB4A;
    }

    .gun_round2 {
      background: #FF9C02;
    }

    .gun_round3 {
      background: #007FEB;
    }

    .gun_round4 {
      background: #EDA300;
    }

    .gun_round5 {
      background: #FF1515;
    }

    .gun_round6 {
      background: rgba(255,255,255,0.6);
    }

    .gun_round8 {
      background: #00B3EB;
    }

    .gunName {
      min-width: 32px;
      margin-left: 6px;
      margin-right: 10px;

      font-size: 14px;
      color: rgba(255,255,255,0.6);
    }

    .gunWorkState {
      font-size: 14px;
      color: rgba(255,255,255,0.8);
    }
  }
}
</style>