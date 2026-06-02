<template>
  <div class="table_content">
    <el-table :data="list" :max-height="tableMaxHeight">
      <template #empty><null-data words="暂无数据"></null-data></template>
      <el-table-column label="时间">
        <template #default="{ row }">
          <span>{{ $filters.moreData(row.startTime)}}</span>
          <span>~</span>
          <span>{{ $filters.moreData(row.endTime)}}</span>
        </template>
      </el-table-column>
      <el-table-column label="时段">
        <template #default="{ row }">
          <div class="periodType" :class="'periodType' + row.periodType">
            <span>{{ $filters.periodType(row.periodType) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="电费（元）">
        <template #default="{ row }">{{ $filters.moneyTwoNum(row.electMoney)}}</template>
      </el-table-column>
      <el-table-column label="服务费（元）">
        <template #default="{ row }">{{ $filters.moneyTwoNum(row.serviceMoney)}}</template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import {reactive, toRefs, watch,defineComponent} from "vue";

export default defineComponent({
  name: "ChargingDcBilling",
  props:{
    chargingInfo:{
      type: Object,
      default: ()=>{
        return {};
      }
    }
  },
  setup(props) {
    const that = reactive({
      list: [],
      tableMaxHeight: 260,
    });

    const watchChargingInfo = watch(()=>props.chargingInfo,(newChargingInfo)=>{
      // console.log(newChargingInfo);
      that.list = newChargingInfo.dcPriceConfigList;
    },{ deep:true });

    return { ...toRefs(that),watchChargingInfo };
  }
});
</script>
<style lang="scss" scoped>
.periodType{
  width: fit-content;
  color: #FFFFFF;
  font-size: 14px;
  padding: 7px 16px;
  border-radius: 8px;
  box-sizing: border-box;
  background: rgba(109, 207, 54, .5);
}

.periodType1{
  background: #FB6868;
}
.periodType2{
  background: #FD9449;
}
.periodType3{
  background: #56ADF7;
}
.periodType4{
  background: #6DCF36;
}
</style>