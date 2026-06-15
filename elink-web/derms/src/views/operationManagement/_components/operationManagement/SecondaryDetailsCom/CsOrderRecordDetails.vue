<template>
  <div class="app-container-right" v-loading="listLoading">
    <Tabs :tabsArray="tabsArray" v-model:tabsIndex="componentName" isRightSlot>
      <template #rightButtonSlot>
        <el-button class="blackFontButtons" @click="clickRefreshBut">刷新</el-button>
      </template>
    </Tabs>
    <div class="tableContent scrollbarStyle">
      <component :is="componentName" ref="componentRef" :orderInfo="orderInfo" :orderType="orderType"></component>
    </div>
  </div>
</template>
<script lang="ts">
import {onMounted, reactive, toRefs, ref, defineComponent} from "vue";
import {findOccupyPileRecordInfoById, findOrderRecordInfoById} from "@/api/operationManagement/CsDisAndChargingRecord";
import {OcOrderingInfo, OcOrderTrajectory, OcBillingRules,DcOrderingInfo,DcOrderTrajectory,DcBillingDetails,DcOrderAnalysis,DcFinancialSettlement} from "./CsOrderRecordDetails/index";

export default defineComponent({
  name: "CsOrderRecordDetails",
  components: {OcOrderingInfo, OcOrderTrajectory, OcBillingRules,DcOrderingInfo,DcOrderTrajectory,DcBillingDetails,DcOrderAnalysis,DcFinancialSettlement},
  props:{
    routeInfo:{
      type: Object,
      default:()=>{
        return {};
      }
    }
  },
  setup(props) {

    const that = reactive({
      orderInfo: {},
      tabsArray: [],
      componentName: "",
      listLoading: false,
      orderId: props.routeInfo.id,
      orderType: props.routeInfo.type,
      ocTabsArray: [
        {id: "OcOrderingInfo", name: "订单信息"},
        {id: "OcOrderTrajectory", name: "订单轨迹"},
        {id: "OcBillingRules", name: "计费规则"}
      ],
      dcTabsArray:[
        {id: "DcOrderingInfo", name: "订单信息"},
        {id: "DcOrderTrajectory", name: "订单轨迹"},
        {id: "DcBillingDetails", name: "计费详情"},
        {id: "DcFinancialSettlement", name: "财务结算"},
        {id: "DcOrderAnalysis", name: "过程分析"}
      ]
    });

    const initParamConfigFun = () => {
      let tabsArray = JSON.parse(JSON.stringify(that.dcTabsArray));
      if(that.orderType === 3)tabsArray = JSON.parse(JSON.stringify(that.ocTabsArray));
      that.tabsArray = JSON.parse(JSON.stringify(tabsArray));
      that.orderType === 3 ? queryOccupyPileRecordInfoById() : queryOrderRecordInfoById();
    };

    const componentRef = ref(null);
    const clickRefreshBut = ()=>{
      that.orderType === 3 ? queryOccupyPileRecordInfoById() : queryOrderRecordInfoById();
      // 过程分析 数据刷新
      if(that.componentName === "DcOrderAnalysis"){
        componentRef.value.queryProcessAnalysisByOrderId();
      }
    };

    // 根据订单id查询基本信息
    const queryOrderRecordInfoById = ()=>{
      that.listLoading = true;
      findOrderRecordInfoById({ orderId: that.orderId }).then(res=>{
        that.orderInfo = res.data;
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    // 根据占桩订单id查询基本信息
    const queryOccupyPileRecordInfoById = ()=>{
      that.listLoading = true;
      findOccupyPileRecordInfoById({ occupyId: that.orderId }).then(res=>{
        that.orderInfo = res.data;
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    const setTableHeaderTitleFun = ()=>{
      let title = "充电记录详情";
      if(that.orderType === 2)title = "放电记录详情";
      if(that.orderType === 3)title = "占用订单详情";
      return title;
    };

    onMounted(()=>{
      initParamConfigFun();
    });

    return {...toRefs(that),initParamConfigFun,queryOrderRecordInfoById,queryOccupyPileRecordInfoById,setTableHeaderTitleFun,clickRefreshBut,componentRef};
  }
});
</script>

<style lang="scss" scoped>
.tableContent {
  padding: 16px 0 12px 0;
  box-sizing: border-box;
}
</style>