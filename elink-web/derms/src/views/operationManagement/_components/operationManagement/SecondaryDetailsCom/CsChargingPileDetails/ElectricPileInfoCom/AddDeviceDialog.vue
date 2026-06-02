<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="720" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="150px">
          <el-row :gutter="10">
            <el-col :span="24">
              <el-form-item label="设备名称：">
                <el-input v-model="formDialog.deviceName" placeholder="请输入设备名称" disabled/>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="设备序列号：">
                <el-input v-model="formDialog.deviceNumber" placeholder="请输入设备序列号" disabled/>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div class="maxHeightClass scrollbarStyle">
          <ModelDynamicFieldCom ref="modelDynamicFieldComRef" :sourceType="3"></ModelDynamicFieldCom>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {updatePileReaById} from "@/api/operationManagement/CsPileGunRunningStatus";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent, nextTick} from "vue";
import ModelDynamicFieldCom from "@/views/operationManagement/_components/operationManagement/SecondaryDetailsCom/ModelDynamicFieldCom.vue";

export default defineComponent({
  name:"AddDeviceDialog",
  components:{ModelDynamicFieldCom},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeEditInfo:{
      type: Object,
      default:()=>{
        return { };
      }
    }
  },
  setup(props){
    const {emit} = getCurrentInstance();

    const that = reactive({
      formDialog: {},
      listLoading: false,
      titleName: "编辑扩展属性",
      dialog_visible: props.isVisible,
      rules: {}
    });

    const formDialogRef = ref(null);
    const modelDynamicFieldComRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          modelDynamicFieldComRef.value.formSubmitFun().then(res=>{
            let formDialog = JSON.parse(JSON.stringify(that.formDialog));
            let readwriteObject = res.readwriteObject ? res.readwriteObject : {};
            updatePileReaById({ ...formDialog,readwriteObject: readwriteObject }).then(()=>{
              emit("changeEvent");
              that.dialog_visible = false;
              ElMessage({ type: "success", showClose: true, message: "操作成功！" });
            }).catch(()=>{
              that.listLoading = false;
            });
          }).catch(()=>{
            that.listLoading = false;
          });
        }
      });
    };

    const initParamConfigFun = ()=>{
      that.formDialog.id = props.activeEditInfo.id;
      that.formDialog.deviceName = props.activeEditInfo.deviceName;
      that.formDialog.deviceNumber = props.activeEditInfo.deviceNumber;

      nextTick(()=>{
        if(props.activeEditInfo.readwriteObject){
          modelDynamicFieldComRef.value.formDialog = props.activeEditInfo.readwriteObject;
        }
        if(props.activeEditInfo.deviceReaList && props.activeEditInfo.deviceReaList.length){
          modelDynamicFieldComRef.value.queryModelFieldUpdateListByModelId(props.activeEditInfo.deviceReaList);
        }
      });
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

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun, modelDynamicFieldComRef};

  }
});
</script>

<style scoped lang="scss">
.maxHeightClass{
  max-height: 580px;
  overflow-y: auto;
}

.el-form-item:last-child {
  margin-bottom: 18px !important;
}
</style>