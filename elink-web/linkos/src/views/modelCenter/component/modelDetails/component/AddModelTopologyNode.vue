<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading
          width="580px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main" v-loading="listLoading">
        <el-form ref="formdialogRef" :model="formdialog" :rules="rules" label-width="130px">
          <el-form-item label="节点名称：" prop="nodeName">
            <el-input v-model="formdialog.nodeName" maxlength="32" placeholder="请输入节点名称" show-word-limit type="text"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {someCharmap} from "@/utils/validate";
import {saveModelTopology} from "@/api/modelCenter/modelManagement";
import {getCurrentInstance, reactive, ref, toRefs, watch} from "vue";

export default {
  name: "AddModelTopologyNode",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "创建场景"
    },
    formDialog: {
      type: Object,
      default: ()=>{
        return { }
      }
    }
  },
  setup(props) {

    const {emit} = getCurrentInstance();

    const validateNodeName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的节点名称"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      formdialog: props.formDialog,
      dialog_visible: props.isVisible,

      rules: {
        nodeName: [{required: true, trigger: "change", validator: validateNodeName}],
      }
    })

    const formdialogRef = ref(null);
    const saveDialog = () => {
      formdialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          saveModelTopology({ ...that.formdialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    return { ...toRefs(that), watchVisible, watchDialogVisible, formdialogRef, saveDialog }
  }
}
</script>

<style scoped>

</style>
