<template>
  <div class="content">
    <!-- 场站数据 -->
    <div v-show="!searchStaition">
      <div class="tips">{{ deviceTypeArray?.totalName }}</div>
      <div class="tipsNum">
        <div>
          <span class="tipsNum_span">{{ deviceTypeArray?.TatolNum1 }}</span>
          <span v-if="selectedDevice === 'pileData' || selectedDevice === 'changeData'"></span>
          <span v-else-if="selectedDevice == 'pvData'">kWp</span>
          <span v-else>kW</span>
        </div>
        <div v-if="selectedDevice === 'pileData' || selectedDevice === 'storageData'">
          /
          <span class="tipsNum_span"> {{ deviceTypeArray?.TatolNum1 }}</span>
          <span v-if="selectedDevice == 'pileData'">次</span>
          <span v-else>kWh</span>
        </div>
      </div>
      <div class="tips">{{ deviceTypeArray?.chargeValue }}</div>
      <div class="tipsNum">
        <div>
          <span class="tipsNum_span">{{ deviceTypeArray?.chargeNum1 }}</span>
          <span v-if="selectedDevice === 'pileData'">台</span>
          <span v-if="selectedDevice === 'storageData' || selectedDevice === 'pvData'">座</span>
          <span v-else>kWh</span>
        </div>

      </div>
      <div v-if="selectedDevice == 'pileData'">
        <div class="tips">场站数量</div>
        <div class="tipsNum">
          <div>
            <span class="tipsNum_span">{{ deviceTypeArray?.chargeNum2 }}</span>
            <span>座</span>
          </div>

        </div>
      </div>
      <div class="tips">{{ deviceTypeArray?.chargeNum }}</div>
      <div class="tipsNum">
        <div>
          <span class="tipsNum_span">{{ deviceTypeArray?.chargeNum3 }}</span>
          <span v-if="selectedDevice == 'changeData'">座</span>
          <span v-else>台</span>
        </div>
      </div>
      <div class="tips">{{ deviceTypeArray?.totalSumValue }}</div>
      <div class="tipsNum">
        <div>
          <span class="tipsNum_span">{{ deviceTypeArray?.totalSumNum1 }}</span>
          <span>kWh</span>
        </div>
      </div>
      <div class="tips">{{ deviceTypeArray?.totalSumValueV2G }}</div>
      <div class="tipsNum" v-if="selectedDevice === 'pileData' || selectedDevice === 'storageData'">
        <span class="tipsNum_span">{{ deviceTypeArray?.totalSumNumDis1 }}</span>
        <span v-if="selectedDevice == 'pileData'">kWh</span>
        <span v-else>度kWh</span>
      </div>
      <div class="date">*数据截止至 2025-08-18 13:54:35</div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  selectedDevice: {
    type: String,
    default: 'pileData',
  },
  originalData: {
    type: Object,
    default: () => ({})
  },
});
const deviceList = ref([
  // 电桩
  {
    id: 'pileData',
    totalName: "累计充放电次数",
    TatolNum1: 'totalChargeNum',
    TatolNum2: 'totalDisChargeNum',
    chargeValue: '充电场站',
    chargeNum1: 'chargePileNum',
    chargeNum2: 'siteNum',
    chargeNum: 'V2G场站',
    chargeNum3: 'v2gPileNum',
    // chargeNum4: 'v2gPileNum',
    totalSumValue: '累计充电量',
    totalSumNum1: 'totalChargeQt',
    totalSumValueV2G: '累计V2G电量',
    totalSumNumDis1: 'totalDisChargeQt',
  },
  // 换电
  {
    id: 'changeData',
    totalName: "累计换电次数",
    TatolNum1: 'totalChangeNum',
    chargeValue: '换电总装机量',
    chargeNum1: 'changeCapacity',
    chargeNum: '换电场站',
    chargeNum3: 'changeSiteNum',
    totalSumValue: '累计换电量',
    totalSumNum1: ' totalChangeQt',
  },
  // 光伏
  {
    id: 'pvData',
    totalName: "光伏总装机量",
    TatolNum1: 'pvCapacity',
    chargeValue: '光伏场站',
    chargeNum1: 'siteNum',
    chargeNum: '逆变器数量',
    chargeNum3: 'inverterNum',
    totalSumValue: '累计发电量',
    totalSumNum1: 'totalQt',
  },
  // 储能
  {
    id: 'storageData',
    totalName: "储能总装机量",
    TatolNum1: ' pcsRatedPower',
    TatolNum2: 'batteryRatedCapacity',
    chargeValue: '储能场站',
    chargeNum1: 'siteNum',
    chargeNum: '储能柜数量',
    chargeNum3: 'storageNum',
    totalSumValue: '累计充电量',
    totalSumNum1: 'totalChargeQt',
    totalSumValueV2G: '累计放电量',
    totalSumNumDis1: 'totalDisChargeQt',
  },
]);
const convertToConfig = (val, id) => {
  const typeList = deviceList.value.find(item => item.id === id);
  if (!typeList) return {};

  const result = {};
  for (const key in typeList) {
    const configKey = typeList[key];

    if (typeof configKey === 'string' && isFieldName(configKey)) {
      result[key] = val.hasOwnProperty(configKey) ? val[configKey] : '--';
    } else {
      result[key] = configKey;
    }
  }

  return result;
};
function isFieldName (str) {
  return /^[a-z][a-zA-Z0-9]*$/.test(str); // 匹配驼峰命名的字段名
}
</script>

<style lang="scss" scoped>
.content {
  width: 100%;
  height: 100%;
}
</style>