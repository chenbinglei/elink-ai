<template>
  <div class="number-stations">
    <div class="number-stations-title flex_c_c">场站数量 <span class="special-text">{{ data?.siteCount }}</span></div>
    <div class="number-stations-list">
      <div class="number-stations-item flex_b_c" :class="'number-stations-item' + (index + 1)" v-for="(item, index) in list" :key="'numberstations' + index">
        <div class="info flex_s_c">
          <img :src="item.img" alt="" width="19" height="26">
          <div class="text ml-2">{{ item.text }}</div>
        </div>
        <div class="number special-text" :style="{ color: item.color }" v-if="data.siteCountMap">{{ data.siteCountMap[item.value] || 0 }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

// 图片路径函数
function getImageUrl (name) {
  return new URL(`/src/assets/image/panoramic-monitor/number-stations-icon-${name}.png`, import.meta.url).href;
}

const list = [
  { text: "光伏电站", value: 1, img: getImageUrl("map1"), color: '#ffa700' },
  { text: "储能电站", value: 2, img: getImageUrl("map2"), color: '#00FFB1' },
  { text: "充电站", value: 3, img: getImageUrl("map3"), color: '#00CCFF' },
  { text: "换电站", value: 4, img: getImageUrl("map4"), color: '#F56C6C' },
  { text: "一体化电站", value: 0, img: getImageUrl("map5"), color: '#B24BFF' },
];

const props = defineProps({
  data: {
    type: Object,
    required: true,
    default: () => ({})
  }
});
watch(() => props.data, (newVal, oldVal) => {
  console.log(newVal, 'newVal')
})
</script>

<style scoped lang="scss">
@font-face {
  font-family: AGENCYB;
  src: url("~@/assets/fonts/AGENCYB.TTF") format("truetype");
}
.flex_s_c {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
.flex_b_c {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.flex_c_c {
  display: flex;
  align-items: center;
  justify-content: center;
}
@import "./style.scss";
</style>