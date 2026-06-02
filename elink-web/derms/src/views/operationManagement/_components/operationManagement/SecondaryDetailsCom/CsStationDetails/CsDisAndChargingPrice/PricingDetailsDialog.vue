<template>
  <Dialog v-model:isVisible="dialog_visible" :title="titleName" width="50vw" closeOnClickModal :cancelVisible="false">
    <template v-slot:content>
      <div class="dialog-main">
        <template v-for="(item,index) in list" :key="index">
          <title-view :title="item.name">
            <template #content>
              <component :is="item.componentName" :returnDataInfo="returnDataInfo" :chargingType="chargingType"></component>
            </template>
          </title-view>
        </template>
      </div>
    </template>
  </Dialog>
</template>
<script>
import PriceStrategy from "./PricingDetailsDialog/PriceStrategy.vue";
import BasicInformation from "./PricingDetailsDialog/BasicInformation.vue";
import ApplicationScope from "./PricingDetailsDialog/ApplicationScope.vue";
import {findPriceDetailsById} from "@/api/operationManagement/CsStationDetails";
import {getCurrentInstance, onMounted, reactive, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "PricingDetailsDialog",
  components:{BasicInformation,PriceStrategy,ApplicationScope},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activePricingId: {
      type: [Number, String],
      default: ""
    },
    // 1： 充   2；放
    chargingType: {
      type: Number,
      default: 1
    },
  },
  setup(props){
    const {emit} = getCurrentInstance();

    const that = reactive({
      returnDataInfo: {},
      listLoading: false,
      titleName: "定价详情",
      dialog_visible: props.isVisible,
      list:[
        {name:"基础信息",componentName:"BasicInformation"},
        {name:"价格策略",componentName:"PriceStrategy"},
        {name:"应用范围",componentName:"ApplicationScope"},
      ]
    });

    const initParamConfigFun = () => {
      that.listLoading = true;
      findPriceDetailsById({pirceId:props.activePricingId}).then(res=>{
        let returnDataInfo = res.data ? res.data : {};
        that.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun};

  }
});
</script>
<style lang="scss" scoped>

</style>