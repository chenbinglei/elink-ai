<template>
  <div class="content_body" ref="tableContentRef" v-resize="setTableMaxHeight">
    <el-table :data="list" :max-height="tableMaxHeight">
      <template #empty><null-data></null-data></template>
      <el-table-column label="序号" type="index" width="80" fixed="left"></el-table-column>
      <el-table-column label="时段类型" fixed="left">
        <template #default="{ row }">
          <div class="periodType" :class="'periodType' + row.periodType">
            <span>{{ $filters.periodType(row.periodType) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="时段电价（元）">
        <template #default="{ row }">{{ $filters.moreData(row.electPrice) }}</template>
      </el-table-column>
      <el-table-column label="时段服务费价（元）" v-if="orderType === 1">
        <template #default="{ row }">{{ $filters.moreData(row.servicePrice) }}</template>
      </el-table-column>
      <el-table-column :label="orderType === 2 ? '放电时长' : '充电时长'">
        <template #default="{ row }">{{ $filters.moreData(row.chargeDuration) }}</template>
      </el-table-column>
      <el-table-column :label="orderType === 2 ? '放电量（度）' : '充电量（度）'">
        <template #default="{ row }">{{ $filters.moreData(row.rechargeQt) }}</template>
      </el-table-column>
      <el-table-column label="电费（元）">
        <template #default="{ row }">{{ $filters.moneyTwoNum(row.electMoney) }}</template>
      </el-table-column>
      <el-table-column label="服务费（元）" v-if="orderType === 1">
        <template #default="{ row }">{{ $filters.moneyTwoNum(row.serviceMoney) }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import {reactive, toRefs, watch, ref, defineComponent} from "vue";

export default defineComponent({
  name: "DcBillingDetails",
  props: {
    orderInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
    // 1: 充 2： 放
    orderType: {
      type: [Number, String],
      default: 1
    },
  },
  setup(props) {

    const that = reactive({
      list: [],
      listLoading: false,
      tableMaxHeight: 300,
      activities:[{timestamp: 'chargeStartTime', color: '#41CB4A'}, {timestamp: 'chargeEndTime', type: 'primary', hollow: true}]
    });

    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const setTableMaxHeight = ()=>{
      that.tableMaxHeight = tableContentRef.value.offsetHeight - 32;
    };

    const watchOrderInfo = watch(()=>props.orderInfo,(newOrderInfo)=>{
      let returnDataInfo = newOrderInfo ? newOrderInfo : {};
      that.list = returnDataInfo.chargingDetailsList;
    },{ deep: true,immediate:true });

    return {...toRefs(that),tableContentRef,setTableMaxHeight,watchOrderInfo};
  }
});
</script>
<style lang="scss" scoped>
.content_body{
  height: 100%;

  .periodType {
    width: fit-content;
    color: #FFFFFF;
    font-size: 14px;
    padding: 4px 16px;
    border-radius: 4px;
    box-sizing: border-box;
    background: rgba(109, 207, 54, .5);
  }

  .periodType1 {
    background: #FB6868;
  }

  .periodType2 {
    background: #FD9449;
  }

  .periodType3 {
    background: #56ADF7;
  }

  .periodType4 {
    background: #6DCF36;
  }
}
</style>