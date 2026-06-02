<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName"
    disabledLoading width="680" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :disabled="formDialog.previewCom" :model="formDialog" :rules="rules"
          label-width="auto">
          <el-form-item label="上传文件：" prop="file_List">
            <UploadFileCustom v-model:fileArray="formDialog.file_List" :fileSize="20" acceptType=".xlsx"
              :classFileType="2">
              <template #tip_content>
                <div class="content_tip flex-ai-center">
                  <div>上传.xlsx格式，文件大小不超过20M，</div>
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
import { ElLoading, ElMessage } from "element-plus";
import UploadFileCustom from "@/components/uploadFileCom/UploadFileCustom.vue";
import { importInspectionItem } from "@/api/assetManagement/inspection";
import { onMounted, reactive, toRefs, defineComponent, ref, getCurrentInstance, watch } from "vue";
export default defineComponent({
  name: "ImportComponentLibraryDialog",
  components: { UploadFileCustom },
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeSiteId: {
      type: String,
      default: ""
    }
  },
  setup (props) {
    const { emit } = getCurrentInstance();

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
      titleName: "导入巡检项配置",
      dialog_visible: props.isVisible,
      siteId: props.activeSiteId,
      rules: {
        file_List: [{ required: true, trigger: "change", validator: validateFile_List }],
      }
    });

    const formDialogRef = ref(null);

    const saveDialog = async () => {
      formDialogRef.value.validate(async (valid) => {
        if (valid) {
          that.listLoading = true;
          let formData = new FormData();
          const file = that.formDialog.file_List[0].raw;
          formData.append("itemFile", file);

          formData.append("siteId", that.siteId);
          importInspectionItem(formData).then(() => {
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(() => {
            that.listLoading = false;
          });
        }
      });
    };

    const initParamConfigFun = () => { };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return { ...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun };
  }
});
</script>
<style lang="scss" scoped></style>