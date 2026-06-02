<template>
  <div class="content_body" ref="tableContentRef" v-resize="setTableMaxHeight">
    <el-table :data="list" :max-height="tableMaxHeight">
      <el-table-column label="序号" type="index" width="80" fixed="left"></el-table-column>
      <el-table-column label="收费时段" fixed="left">
        <template #default="{ row }">{{ $filters.moreData(row.tariffPeriod) }}</template>
      </el-table-column>
      <el-table-column label="免占桩时长（分钟）">
        <template #default="{ row }">{{ $filters.moreData(row.avoidDuration) }}</template>
      </el-table-column>
      <el-table-column label="收费标准">
        <template #default="{ row }">{{ $filters.moreData(row.tariffStandard) }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import {reactive, toRefs, watch, ref} from "vue";

export default {
  name: "OcBillingRules",
  props: {
    orderInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
  },
  setup(props) {

    const that = reactive({
      list: [],
      listLoading: false,
      tableMaxHeight: 300,
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
};
</script>
<style lang="scss" scoped>
.content_body{
  height: 100%;
}
</style>