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
import {reactive, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "ChargingAcBilling",
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
      that.list = newChargingInfo.acPriceConfigList;
    },{ deep:true });

    return { ...toRefs(that),watchChargingInfo };
  }
});
</script>
<style lang="scss" scoped>

</style>