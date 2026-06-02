<template>
  <Dialog v-model:isVisible="dialog_visible" :footerVisible="false" closeOnClickModal :title="titleName" width="50vw">
    <template v-slot:content>
      <div class="content_body">
        <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
          <el-table-column align="center" label="序号" type="index" width="60"></el-table-column>
          <el-table-column align="center" label="缓存时间">
            <template #default="{ row }">{{ $filters.moreData(row.cacheTime) }}</template>
          </el-table-column>
<!--          <el-table-column align="center" label="下次存储至数据库时间">-->
<!--            <template #default="{ row }">{{ $filters.moreData(row.nextStorageTime) }}</template>-->
<!--          </el-table-column>-->
          <el-table-column align="center" label="数据">
            <template #default="{ row }">{{ $filters.moreData(row.resultValue) }}</template>
          </el-table-column>
        </el-table>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {getCurrentInstance, reactive, toRefs, watch, onMounted} from "vue";
import {findLocalCacheDataByIds} from "@/api/dataManagement/nodeManagement";

export default {
  name: "LookComputeNodeDate",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "标题"
    },
    // 当前编辑的节点id
    computeNodeId: {
      type: [String, Number],
      default: ""
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      list: [],
      listLoading: false,
      tableMaxHeight: 520,
      dialog_visible: props.isVisible,
    })

    // 根据多个节点id查询本地缓存数据
    const queryLocalCacheDataByIds = ()=>{
      that.listLoading = true;
      findLocalCacheDataByIds({ ids: props.computeNodeId }).then(res=>{
        let list = [];
        let returnDateInfo = res.data ? res.data : [];
        for(let key in returnDateInfo){
          list.push({id: key, ...returnDateInfo[key]});
        }
        that.list = JSON.parse(JSON.stringify(list));
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      queryLocalCacheDataByIds();
    })

    return { ...toRefs(that), queryLocalCacheDataByIds, watchVisible, watchDialogVisible }
  }
}
</script>

<style scoped lang="scss">
.content_body{

  .content_body_top{
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .content_body_bottom{
    margin-top: 12px;
  }
}
</style>
