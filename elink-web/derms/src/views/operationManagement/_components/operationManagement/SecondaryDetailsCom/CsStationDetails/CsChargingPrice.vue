<template>
  <div class="content_body bg_color_class">
    <div class="content_body_top">
      <buttons-tabs v-model:tabsIndex="componentName" :tabsArray="tabsArray" customClass="connectClass">
        <template #content>
          <el-button v-if="authority === 2" type="primary" class="whiteFontButtons" @click="clickSetPriceBut">
            <span class="iconfont icon-jine"></span>
            <span>设置价格</span>
          </el-button>
        </template>
      </buttons-tabs>
    </div>
    <div class="content_list scrollbarStyle">
      <component ref="componentRef" :authority="authority" :is="componentName" :chargingType="chargingType" :siteId="siteId"></component>
    </div>

    <SetPriceDialog v-if="setPriceVisible" v-model:isVisible="setPriceVisible" :chargingType="chargingType" :siteId="siteId" @changeEvent="changeEvent" />
  </div>
</template>
<script lang="ts">
import {reactive, toRefs, ref, defineComponent} from "vue";
import SetPriceDialog from "./CsDisAndChargingPrice/SetPriceDialog.vue";
import PricingRecords from "./CsDisAndChargingPrice/PricingRecords.vue";
import ChargingCurrentPrice from "./CsDisAndChargingPrice/ChargingCurrentPrice.vue";

export default defineComponent({
  name: "CsChargingPrice",
  components: {PricingRecords, ChargingCurrentPrice, SetPriceDialog},
  props: {
    siteId: {
      type: [Number, String],
      default: ""
    },
    //权限 1-只读 2-读写
    authority:{
      type: Number,
      default: 1
    },
  },
  setup() {

    const that = reactive({
      chargingType: 1, // 1： 充   2；放
      setPriceVisible: false,
      componentName: "ChargingCurrentPrice",
      tabsArray: [{id: "ChargingCurrentPrice", name: "当前价格"}, {id: "PricingRecords", name: "定价记录"}],
    });

    // 设置价格
    const clickSetPriceBut = ()=>{
      that.setPriceVisible = true;
    };

    const componentRef = ref(null);
    const changeEvent = ()=>{
      if(that.componentName === "PricingRecords"){
        componentRef.value.queryFixPriceRecordList();
      }
      if(that.componentName === "ChargingCurrentPrice"){
        componentRef.value.queryFixPriceRecordList();
      }
    };

    return { ...toRefs(that),clickSetPriceBut, changeEvent, componentRef };
  }
});
</script>
<style lang="scss" scoped>
.content_body {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding-top: 12px;
  box-sizing: border-box;

  .content_list {
    flex: 1;
    overflow-y: auto;
    padding-top: 16px;
    box-sizing: border-box;
  }

  .whiteFontButtons {
    .iconfont {
      margin-right: 4px;
    }
  }
}
</style>