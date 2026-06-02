<template>
  <div class="dcFinancialSettlement">
    <TitleView title="结算订单">
      <template #content>
        <div class="content_list">
          <el-row :gutter="12" class="content_list">
            <template v-for="(child,i) in list" :key="i">
              <el-col v-if="!child.orderType || child.orderType === orderType " :lg="8" :sm="12" class="info_li">
                <div class="flex_li_left">{{ $filters.chargingDisText(child.name, orderType) }}：</div>
                <div :class="[child.className ? child.className : '', child.className ? child.className + returnDataInfo[child.fieldName] : '' ]" class="flex_li_right">
                  <span v-if="child.filterName">{{$filters.chargingDisText($filters[child.filterName](returnDataInfo[child.fieldName]), orderType)}}</span>
                  <span v-else>{{$filters.chargingDisText($filters.moreData(returnDataInfo[child.fieldName]), orderType)}}</span>
                </div>
              </el-col>
            </template>
          </el-row>
        </div>
      </template>
    </TitleView>
    <TitleView v-if="orderType === 1" title="退款记录">
      <template #content>
        <div class="content_list">
          <el-table :data="table_list" :max-height="tableMaxHeight">
            <el-table-column label="退款时间">
              <template #default="{ row }">{{ $filters.moreData(row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作人">
              <template #default="{ row }">{{ $filters.moreData(row.refundOperator) }}</template>
            </el-table-column>
            <el-table-column label="退款金额（元）">
              <template #default="{ row }">{{ $filters.moneyTwoNum(row.refundAmount) }}</template>
            </el-table-column>
            <el-table-column label="退款状态">
              <template #default="{ row }">
                <span class="refundStatus" :class="'refundStatus' + row.refundStatus">{{ $filters.refundStatus(row.refundStatus) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </template>
    </TitleView>
  </div>
</template>

<script>
import {reactive, toRefs, defineComponent, watch} from "vue";

export default defineComponent({
  name: "DcFinancialSettlement",
  props: {
    orderInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
    // 1: 充 2： 放
    orderType: {
      type: Number,
      default: 1
    },
  },
  setup(props) {

    const that = reactive({
      returnDataInfo: {},
      list: [
        {name: "充/放电电量（度）", fieldName: "totalQt", filterName: ""},
        {name: "实付电费（元）", fieldName: "actualTotalElect", filterName: "moneyTwoNum", orderType: 1},
        {name: "实付服务费（元）", fieldName: "actualTotalFee", filterName: "moneyTwoNum", orderType: 1},
        {name: "实付金额（元）", fieldName: "actualTotalCost", filterName: "moneyTwoNum", orderType: 1},
        {name: "原价总金额（元）", fieldName: "totalCost", filterName: "moneyTwoNum", orderType: 1},
        {name: "预付金额（元）", fieldName: "prepayMoney", filterName: "moneyTwoNum", orderType: 1},
        {name: "优惠金额（元）", fieldName: "discountAmount", filterName: "moneyTwoNum", orderType: 1},
        {name: "退款金额（元）", fieldName: "refundMoney", filterName: "moneyTwoNum", orderType: 1},
        {name: "放电收益（元）", fieldName: "actualTotalCost", filterName: "moneyTwoNum", orderType: 2},
        {name: "支付方式", fieldName: "payWay", filterName: "payWay", orderType: 1},
        {name: "支付方式", fieldName: "payWay", filterName: "disPayWay", orderType: 2},
        {name: "结算状态", fieldName: "settlementState", filterName: "settlementState",className: "settlementState"},
      ],
      table_list: [],
      tableMaxHeight: 380,
    });

    const watchOrderInfo = watch(() => props.orderInfo, (newOrderInfo) => {
      let returnDataInfo = newOrderInfo ? newOrderInfo : {};
      let settlementRecord = returnDataInfo.settlementRecord ?? {};
      let totalFeeReduction = settlementRecord.totalFeeReduction ?? 0; //服务费减免
      let totalElectReduction = settlementRecord.totalElectReduction ?? 0; //电费减免

      that.table_list = returnDataInfo.refundRecordList ?? [];
      that.returnDataInfo = {
        ...settlementRecord,
        totalQt: returnDataInfo.totalQt,
        totalCost: returnDataInfo.totalCost,
        prepayMoney: returnDataInfo.prepayMoney,
        discountAmount: totalFeeReduction + totalElectReduction
      };
    }, {deep: true, immediate: true});

    return {...toRefs(that), watchOrderInfo};

  }
});
</script>

<style lang="scss" scoped>
.info_li {
  display: flex;
  align-items: center;
  margin-bottom: 24px;

  .flex_li_left {
    color: #d3ecfb;
    font-size: 14px;
  }

  .flex_li_right {
    color: #d3ecfb;
    font-size: 14px;
    display: flex;
    align-items: center;
  }

  .settlementState0{
    color: #EDA300;
  }
  .settlementState1{
    color: #00B3EB;
  }
  .settlementState2,.refundStatus2{
    color: #FF1515;
  }
  .settlementState3,.refundStatus1{
    color: #41CB4A;
  }
}
</style>