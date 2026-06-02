<template>
  <div class="chargingCurrentPrice">
    <template v-for="(item,index) in list" :key="index">
<!--      v-if="chargingType !== 2 || chargingType === 2 && item.componentName !== 'ChargingAcBilling'-->
      <title-view :title="item.name">
        <template #content>
          <div class="content_table" v-loading="listLoading">
            <component :is="item.componentName" :chargingInfo="chargingInfo"></component>
          </div>
        </template>
      </title-view>
    </template>
  </div>
</template>

<script>
import ChargingDcBilling from "./ChargingDcBilling.vue";
import ChargingAcBilling from "./ChargingAcBilling.vue";
import {defineComponent, onMounted, reactive, toRefs, watch} from "vue";
import {findFixPriceRecordList} from "@/api/operationManagement/CsStationDetails";

export default defineComponent({
  name: "ChargingCurrentPrice",
  components:{ChargingDcBilling,ChargingAcBilling},
  props: {
    // 1： 充   2；放
    chargingType: {
      type: Number,
      default: 1
    },
    siteId: {
      type: [Number, String],
      default: ""
    }
  },
  setup(props) {

    const that = reactive({
      chargingInfo: {},
      listLoading: false,
      list: [{name:"直流计费",componentName:"ChargingDcBilling"}, {name:"交流计费",componentName:"ChargingAcBilling"}]
    });

    // 根据站点id查询定价记录
    const queryFixPriceRecordList = ()=>{
      that.listLoading = true;
      findFixPriceRecordList({ priceType: props.chargingType,siteId: props.siteId, type: "ChargingCurrentPrice" }).then(res=>{
        that.chargingInfo = res.data ? res.data : {};
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    const watchSite = watch(()=>props.siteId,(newSite)=>{
      queryFixPriceRecordList();
    },{ deep:true });

    onMounted(()=>{
      queryFixPriceRecordList();
    });

    return {...toRefs(that),queryFixPriceRecordList,watchSite};
  }
});
</script>

<style lang="scss" scoped>
.content_table{
  margin-bottom: 24px;
}
</style>