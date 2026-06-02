<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName" width="580" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="属性名称：">
            <el-input v-model="formDialog.reaName" type="text" disabled></el-input>
          </el-form-item>
          <el-form-item label="默认值：" prop="defaultValue">
            <template v-if="formDialog.reaType !== 6">
              <el-input v-model="formDialog.defaultValue" maxlength="100" placeholder="请输入默认值" show-word-limit type="text"></el-input>
            </template>
            <template v-else>
              <el-date-picker v-model="formDialog.defaultValue" type="datetime" placeholder="请选择默认时间" format="YYYY-MM-DD hh:mm:ss" value-format="YYYY-MM-DD hh:mm:ss"/>
            </template>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {updateModelReaValue} from "@/api/modelCenter/modelManagement";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "EditModelDefaultValueDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "编辑默认值"
    },
    activeEditInfo:{
      type: Object,
      default:()=>{
        return { }
      }
    }
  },
  setup(props){

    const {emit} = getCurrentInstance();

    const validateDefaultValue = (rule, value, callback) => {
      if (!value && value !== 0) {
        callback(new Error("请输入默认值"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      listLoading: false,
      dialog_visible: props.isVisible,
      rules:{
        defaultValue:[{required: true, trigger: "change", validator: validateDefaultValue }],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          updateModelReaValue({ ...formDialog }).then(()=>{
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
      that.formDialog = Object.assign({},props.activeEditInfo,that.formDialog);
      // console.log(that.formDialog);
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      initParamConfigFun();
    })

    return {...toRefs(that),watchVisible,watchDialogVisible,formDialogRef,saveDialog,initParamConfigFun,}
  }
})
</script>

<style scoped lang="scss">

</style>