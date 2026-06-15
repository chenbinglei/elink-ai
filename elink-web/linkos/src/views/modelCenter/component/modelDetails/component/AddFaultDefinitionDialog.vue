<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="620px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formdialog" :rules="rules" label-width="130px">
          <el-form-item label="事件名称：" prop="eventName">
            <el-input v-model="formdialog.eventName" maxlength="32" placeholder="请输入事件名称" show-word-limit type="text"></el-input>
          </el-form-item>
          <el-form-item label="故障编码：" prop="faultCode">
            <el-input v-model="formdialog.faultCode" placeholder="请输入故障编码" type="text" :disabled="formDialog.id"></el-input>
          </el-form-item>
          <el-form-item label="事件级别：" prop="eventLevel">
            <el-select v-model="formdialog.eventLevel" placeholder="请选择事件级别">
              <el-option v-for="item in eventLevelArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch,defineComponent} from "vue";
import {letterNumLine, someCharmap} from "@/utils/validate";
import {savePileFault} from "@/api/modelCenter/modelManagement";
import {ElMessage} from "element-plus";

export default defineComponent({
  name: "AddFaultDefinitionDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "创建模型"
    },
    formDialog: {
      type: Object,
      default: ()=>{
        return { }
      }
    }
  },
  setup(props){

    const {emit} = getCurrentInstance();

    const validateEventName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的事件名称"));
      } else {
        callback();
      }
    };

    const validateFaultCode = (rule, value, callback) => {
      if (!value || !letterNumLine(value)) {
        callback(new Error("请输入正确的故障编码"));
      } else {
        callback();
      }
    };

    const validateEventLevel = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择事件级别"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      formdialog: props.formDialog,
      dialog_visible: props.isVisible,
      eventLevelArray:[{id:1,name:"次要告警"},{id:2,name:"重要告警"},{id:3,name:"紧急告警"},{id:4,name:"提示告警"},{id:5,name:"离线告警"}],

      rules: {
        eventName: [{required: true, trigger: "change", validator: validateEventName}],
        faultCode: [{required: true, trigger: "change", validator: validateFaultCode}],
        eventLevel: [{required: true, trigger: "change", validator: validateEventLevel}],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formdialog = JSON.parse(JSON.stringify(that.formdialog));
          savePileFault({ ...formdialog }).then(()=>{
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

    return { ...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog }

  }
})
</script>

<style scoped lang="scss">

</style>