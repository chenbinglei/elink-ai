<template>
  <div class="historyDataTable">
    <el-table :data="list" :max-height="tableMaxHeight" border stripe>
      <el-table-column align="center" label="序号" type="index" width="80" fixed></el-table-column>
      <el-table-column align="center" label="时间" width="180" fixed>
        <template #default="{ row }">{{ $filters.moreData(row)}}</template>
      </el-table-column>
      <template v-for="(item,index) in valueListNum" :key="index">
        <el-table-column align="center" :label="'第' + (index + 1) + '个值'" width="120">
          <template #default="{ row,$index }">{{ $filters.moreData(returnDataInfo['seriesList' + index][$index]) }}</template>
        </el-table-column>
      </template>
    </el-table>
  </div>
</template>

<script lang="ts">
import {reactive, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "HistoryDataTable",
  props:{
    returnDataInfo:{
      type:Object,
      default:()=>{
        return { }
      }
    }
  },
  setup(props){
    const that = reactive({
      list: [],
      valueListNum: 1,
      tableMaxHeight: 380,
    })

    const watchReturnDataInfo = watch(()=>props.returnDataInfo,(newReturnDataInfo)=>{
      that.list = newReturnDataInfo.dateList;
      that.valueListNum = newReturnDataInfo.valueListNum || 1;
    },{ deep: true,immediate: true })

    return {...toRefs(that), watchReturnDataInfo }
  }
})
</script>

<style lang="scss" scoped>
.historyDataTable {
  width: 100%;
  height: 100%;
}
</style>
