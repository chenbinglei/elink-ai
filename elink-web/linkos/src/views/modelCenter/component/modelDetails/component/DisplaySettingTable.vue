<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName"
          width="620px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formdialogRef" :model="formdialog" label-width="130px">
          <el-form-item label="选择显示功能点：">
            <el-select v-model="formdialog.functionIds" multiple collapse-tags collapse-tags-tooltip placeholder="请选择">
              <el-option v-for="item in modelFunctionList" :key="item.id" :label="item.functionName" :value="item.id"/>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch} from "vue";
import {bindModelFunctionData, findModelFunctionListByModelId} from "@/api/modelCenter/modelManagement";

export default {
  name: "DisplaySettingTable",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeModelId: {
      type: [String, Number],
      default: ''
    },
    fieldDataList: {
      type: Array,
      default: []
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();
    const that = reactive({
      formdialog: {},
      listLoading: false,
      titleName: "显示设置",
      modelFunctionList: [],
      dialog_visible: props.isVisible,
    })

    // 根据模型id查询模型关联标准功能数据
    const queryModelDeviceListByModelId = ()=>{
      findModelFunctionListByModelId({ modelId: props.activeModelId,page: 1,size: 999999 }).then(res=>{
        that.modelFunctionList = res.data.items;
      })
    }

    const formdialogRef = ref(null);
    const saveDialog = () => {
      formdialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          bindModelFunctionData({ ...that.formdialog,modelId: props.activeModelId }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const initParamConfigFun = ()=>{
      if(!that.formdialog.functionIds)that.formdialog.functionIds = [];
      if(props.fieldDataList && props.fieldDataList.length){
        for(let i = 0;i < props.fieldDataList.length;i++){
          if(props.fieldDataList[i].id)that.formdialog.functionIds.push(props.fieldDataList[i].id);
        }
      }
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      initParamConfigFun();
      queryModelDeviceListByModelId();
    })

    return { ...toRefs(that), watchVisible, watchDialogVisible, formdialogRef, saveDialog, initParamConfigFun, queryModelDeviceListByModelId }
  }
}
</script>

<style scoped>

</style>
