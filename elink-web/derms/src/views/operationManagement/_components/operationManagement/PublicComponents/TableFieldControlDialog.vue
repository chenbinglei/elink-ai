<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="680" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-checkbox-group v-model="tableShowFieldList">
          <template v-for="item in tableAllFieldList" :key="item.key">
            <el-checkbox :label="item.name" :value="item.key"></el-checkbox>
          </template>
        </el-checkbox-group>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, onMounted} from "vue";

export default defineComponent({
  name: "TableFieldControlDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    tableAllFieldList: {
      type: Array,
      default: () => []
    },
    // 本地存储名称
    tableFieldStorageName: {
      type: String,
      default: ""
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      listLoading: false,
      titleName: "列表显示项",
      tableShowFieldList: [],
      dialog_visible: props.isVisible,
    });

    const clickConfirmBut = ()=>{
      localStorage.setItem(props.tableFieldStorageName,JSON.stringify(that.tableShowFieldList));
      ElMessage({ type: "success", showClose: true, message: "编辑成功！" });
      that.dialog_visible = false;
      emit("changeEvent");
    };

    const initParamConfigFun = () => {
      let tableShowFieldList = [];
      let storageFieldList = localStorage.getItem(props.tableFieldStorageName);
      if(!storageFieldList){
        for(let i = 0;i < props.tableAllFieldList.length;i++){
          tableShowFieldList.push(props.tableAllFieldList[i].key);
        }
      }
      that.tableShowFieldList = storageFieldList ? JSON.parse(storageFieldList) : tableShowFieldList;
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut};
  }
});
</script>

<style lang="scss" scoped>
.dialog-main {
  max-height: 520px;
  overflow-y: auto;

  .el-checkbox{
    min-width: 120px;
  }
}
</style>