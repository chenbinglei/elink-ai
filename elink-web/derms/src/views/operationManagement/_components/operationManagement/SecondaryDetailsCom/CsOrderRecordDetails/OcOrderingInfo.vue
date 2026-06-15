<template>
  <div class="content_body">

    <div class="content_top_card">
      <div class="card_c_li">
        <div class="card_c_li_title">占桩时长）</div>
        <div class="card_c_li_num">{{ $filters.moneyTwoNum(returnDataInfo.totalCost) }}</div>
      </div>
      <div class="card_c_li">
        <div class="card_c_li_title">占桩费用（元）</div>
        <div class="card_c_li_num">{{ $filters.moneyTwoNum(returnDataInfo.totalElect) }}</div>
      </div>
      <div class="occupyState" :class="'occupyState' + returnDataInfo.occupyState">
        <span>{{ $filters.occupyState(returnDataInfo.occupyState) }}</span>
      </div>
    </div>

    <template v-for="(item,index) in list" :key="index">
      <TitleView :title="item.name">
        <template #content>
          <div class="content_list">
            <el-row :gutter="12" class="content_list">
              <template v-for="(child,i) in item.children" :key="i">
                <el-col :sm="12" :lg="8" class="info_li">
                  <div class="flex_li_left">{{ $filters.chargingDisText(child.name, orderType) }}：</div>
                  <div class="flex_li_right" :class="[child.className ? child.className : '', child.className ? child.className + returnDataInfo[child.fieldName] : '' ]">
                    <span v-if="child.filterName">{{ $filters[child.filterName](returnDataInfo[child.fieldName]) }}</span>
                    <span v-else>{{ $filters.moreData(returnDataInfo[child.fieldName]) }}</span>
                  </div>
                </el-col>
              </template>
            </el-row>
          </div>
        </template>
      </TitleView>
    </template>
  </div>
</template>

<script lang="ts">
import {reactive, toRefs, defineComponent, watch} from "vue";

export default defineComponent({
  name: "DcOrderingInfo",
  props:{
    orderInfo:{
      type: Object,
      default:()=>{
        return { };
      }
    }
  },
  setup(props) {
    const that = reactive({
      returnDataInfo: {},
      list: [{
        name: "订单信息",
        children: [
          {name: "订单号", fieldName: "occupyNum", filterName: "", unit: ""},
          {name: "所属订单记录", fieldName: "orderId", filterName: "", unit: ""},
          {name: "订单状态", fieldName: "occupyState", filterName: "occupyState", unit: "",className:"occupyState"},
          {name: "充/放电电量（度）", fieldName: "totalCurQt", filterName: "", unit: ""},
          {name: "订单金额（元）", fieldName: "orderMoney", filterName: "", unit: ""},
          {name: "实付金额（元）", fieldName: "paidMoney", filterName: "", unit: ""},
          {name: "支付状态", fieldName: "payState", filterName: "occupyPaidMoney", unit: "",className:"payState"},
          {name: "支付时间", fieldName: "payTime", filterName: "", unit: ""},
          {name: "开始时间", fieldName: "startTime", filterName: "", unit: ""},
          {name: "结束时间", fieldName: "endTime", filterName: "", unit: ""},
          {name: "占位时长", fieldName: "duration", filterName: "", unit: ""},
        ]
      }, {
        name: "电桩信息",
        children: [
          {name: "电站名称", fieldName: "siteName", filterName: "", unit: ""},
          {name: "电站ID", fieldName: "siteId", filterName: "", unit: ""},
          {name: "运营商名称", fieldName: "operatorName", filterName: "", unit: ""},
          {name: "所在城市", fieldName: "city", filterName: "", unit: ""},
          {name: "具体地址", fieldName: "address", filterName: "", unit: ""},
          {name: "电桩类型", fieldName: "pileType", filterName: "pileType", unit: ""},
          // {name: "设备出厂编码", fieldName: "factoryCode", filterName: "", unit: ""},
          {name: "桩编号", fieldName: "pileCode", filterName: "", unit: ""},
          {name: "枪编号", fieldName: "gunCode", filterName: "", unit: ""},
        ]
      }, {
        name: "用户信息",
        children: [
          {name: "车牌号", fieldName: "plateNumber", filterName: "", unit: ""},
          {name: "VIN码", fieldName: "busVin", filterName: "", unit: ""},
          {name: "企业账户", fieldName: "enterpriseAccount", filterName: "", unit: ""},
          {name: "车队名称", fieldName: "fleetName", filterName: "", unit: ""},
          // {name: "开票状态", fieldName: "invoicingState", filterName: "invoicingState", unit: ""},
        ]
      }]
    });

    const watchOrderInfo = watch(()=>props.orderInfo,(newOrderInfo)=>{
      let returnDataInfo = newOrderInfo ? newOrderInfo : {};
      let pileInfoData = returnDataInfo.pileInfoData;
      let userRecordDto = returnDataInfo.userRecordDto;
      that.returnDataInfo = { ...returnDataInfo,...pileInfoData,...userRecordDto };
    },{ deep: true,immediate:true });

    return { ...toRefs(that),watchOrderInfo };
  }
});
</script>

<style lang="scss" scoped>
.content_top_card {
  padding: 20px 24px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  border-radius: 8px;
  margin-bottom: 24px;
  border: 1px solid #EDA300;
  background: rgba(255,255,255,0.1);
  position: relative;

  .occupyState {
    position: absolute;
    right: 0;
    top: 0;
    padding: 8px 18px;
    border-radius: 0 8px 0 8px;
    background: #EDA300;
    font-size: 14px;
    color: #FFFFFF;
  }

  .occupyState1 {
    background: #41CB4A;
  }

  .occupyState2 {
    background: #00B3EB;
  }

  .occupyState9 {
    background: #FF1515;
  }

  .card_c_li {
    color: #EDA300;
    margin-right: 112px;
    text-align: center;

    .card_c_li_title {
      font-size: 14px;
      margin-bottom: 8px;
    }

    .card_c_li_num {
      font-size: 22px;
    }

    &:last-child {
      margin-right: 0;
    }
  }
}

.el-row{
  width: 100%;

  .info_li {
    display: flex;
    align-items: center;
    margin-bottom: 24px;

    .flex_li_left {
      color: #666666;
      font-size: 14px;
    }

    .flex_li_right {
      color: #121C3F;
      font-size: 14px;
      display: flex;
      align-items: center;
    }

    .occupyState1{
      color: #EDA300;
    }

    .occupyState2,.payState1{
      color: #41CB4A;
    }

    .occupyState3,.occupyState4,.payState2{
      color: #FF1515;
    }

    .occupyState6{
      color: #00B3EB;
    }
  }
}
</style>