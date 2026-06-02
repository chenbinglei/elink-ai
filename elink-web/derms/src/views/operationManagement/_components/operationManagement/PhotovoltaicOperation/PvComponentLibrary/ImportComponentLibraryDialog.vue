<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="680" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :disabled="formDialog.previewCom" :model="formDialog" :rules="rules" label-width="auto">
          <el-form-item label="上传文件：" prop="file_List">
            <UploadFileCustom v-model:fileArray="formDialog.file_List" :fileSize="20" acceptType=".xlsx" :classFileType="2">
              <template #tip_content>
                <div class="content_tip flex-ai-center">
                  <div>上传.xlsx格式，文件大小不超过20M，</div>
                  <el-link type="primary" @click="clickDownFileFun">点击下载模板。</el-link>
                </div>
              </template>
            </UploadFileCustom>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {downloadFiles} from "@/utils";
import {ElLoading, ElMessage} from "element-plus";
import UploadFileCustom from "@/components/uploadFileCom/UploadFileCustom.vue";
import {importModuleLibraryList} from "@/api/operationManagement/PvComponentLibrary";
import {onMounted, reactive, toRefs, defineComponent, ref, getCurrentInstance, watch} from "vue";

export default defineComponent({
  name: "ImportComponentLibraryDialog",
  components: {UploadFileCustom},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateFile_List = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请上传文件"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      listLoading: false,
      titleName: "上传组件模板",
      dialog_visible: props.isVisible,
      rules:{
        file_List: [{ required: true, trigger: "change", validator: validateFile_List }],
      }
    });

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formData = new FormData();
          for (let key in that.formDialog){
            if(key.indexOf('file_List') !== -1){
              let key_arr = key.split("_");
              if(that.formDialog[key] && that.formDialog[key].length){
                for (let i = 0; i < that.formDialog[key].length; i++) {
                  if (that.formDialog[key][i].raw) formData.append(key_arr[0], that.formDialog[key][i].raw);
                }
              }
            } else {
              formData.append(key, that.formDialog[key]);
            }
          }
          importModuleLibraryList(formData).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          });
        }
      });
    };

    const clickDownFileFun = ()=>{
      that.loading = ElLoading.service({ lock: true, text: '正在下载中...', background: 'rgba(0, 0, 0, 0.7)' });
      setTimeout(()=> {
        downloadFiles("/exlsx/componentTemplate.xlsx","组件导入模板.xlsx");
        ElMessage({ type:"success",showClose: true,message:"下载成功！" });
        that.loading.close();
      },500);
    };

    const initParamConfigFun = () => {};

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun, clickDownFileFun};
  }
});
</script>
<style lang="scss" scoped>

</style>