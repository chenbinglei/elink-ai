<template>
  <Dialog v-model:isVisible="dialog_visible" closeOnClickModal :footerVisible="false" :title="titleName" width="45vw">
    <template v-slot:content>
      <div class="content_body">
        <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
          <el-table-column align="center" type="index" label="序号" width="80"></el-table-column>
          <el-table-column align="center" label="实例ID" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.deviceId) }}</template>
          </el-table-column>
          <el-table-column align="center" label="实例名称" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.deviceName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="数据来源" show-overflow-tooltip>
            <template #default="{ row }">{{ $filters.moreData(row.dataSourceName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="操作">
            <template #default="{ row,$index }">
              <el-link :underline="false" type="danger" @click="clickOperate(row,$index)">删除</el-link>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage, ElMessageBox} from "element-plus";
import {deleteVariableNodeById} from "@/api/dataManagement/systemVariables";
import {getCurrentInstance, onMounted, reactive, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "LookVarAssociatedInstance",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    variableExampleInfoList: {
      type: Array,
      default: ()=>[]
    },
  },
  setup(props) {
    const { emit } = getCurrentInstance();

    const that = reactive({
      list: [],
      listLoading: false,
      tableMaxHeight: 320,
      titleName: "关联实例",
      dialog_visible: props.isVisible,
    });

    const clickOperate = (row,index)=>{
      ElMessageBox.confirm(`确定删除（<spam class="highlightText">${ row.dataSourceName }</span）吗？`, "提示", {
        dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消',
        customClass: "elMessageBoxWarning", showClose: false, type: 'warning',
        beforeClose: (action, instance, done)=>{
          if (action === 'confirm') {
            instance.confirmButtonLoading = true;
            instance.confirmButtonText = '正在删除...';
            // 根据关联实例id删除关联实例数据
            deleteVariableNodeById({ id: row.id }).then(()=>{
              done();
              instance.confirmButtonLoading = false;
            }).catch(() => {
              instance.confirmButtonText = '确定';
              instance.confirmButtonLoading = false;
            });
          } else {
            done();
          }
        }
      }).then(() => {
        that.list.splice(index,1);
        emit("changEvent", { type: "listArray" });
        ElMessage({type: "success", message: "删除成功", showClose: true});
      }).catch(() => {
        console.log("取消操作！");
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      if(props.variableExampleInfoList && props.variableExampleInfoList.length){
        that.list = JSON.parse(JSON.stringify(props.variableExampleInfoList))
      }
    })

    return { ...toRefs(that), watchVisible, watchDialogVisible,clickOperate }
  }
})
</script>

<style scoped>

</style>
