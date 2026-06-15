<template>
  <div class="cacheDataTable">
    <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
      <el-table-column align="center" label="序号" type="index" width="60"></el-table-column>
      <el-table-column align="center" label="缓存时间">
        <template #default="{ row }">{{ $filters.moreData(row.cacheTime) }}</template>
      </el-table-column>
      <el-table-column align="center" label="下次存储至数据库时间">
        <template #default="{ row }">{{ $filters.moreData(row.nextStorageTime) }}</template>
      </el-table-column>
      <el-table-column align="center" label="数据">
        <template #default="{ row }">{{ $filters.moreData(row.resultValue) }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {onMounted, reactive, toRefs} from "vue";

export default {
  name: "CacheDataTable",
  props: {
    // 当前编辑的节点id
    computeNodeId: {
      type: [String, Number],
      default: ""
    },
  },
  setup(props) {

    const that = reactive({
      list: [],
      listLoading: false,
      tableMaxHeight: 320,
    })

    const listArray = (operateType) => {
      // that.listLoading = true;
      // findLocalCacheDataByIds({ids: props.computeNodeId}).then(res => {
      //   that.listLoading = false;
      //   that.list = res.data[props.computeNodeId] ? [res.data[props.computeNodeId]] : [];
      //   if (operateType === "refresh"){
      //     ElMessage({type: "success", message: "刷新成功", showClose: true});
      //   }
      // }).catch(() => {
      //   that.listLoading = false;
      // })
    }

    onMounted(() => {
      listArray();
    })

    return {...toRefs(that), listArray}
  }
}
</script>

<style scoped>

</style>
