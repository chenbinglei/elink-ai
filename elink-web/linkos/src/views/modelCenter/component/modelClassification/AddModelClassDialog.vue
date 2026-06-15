<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName" width="620px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formdialogRef" :model="formdialog" :rules="rules" label-width="130px">
          <el-form-item label="场景名称：" prop="sortName">
            <el-input v-model="formdialog.sortName" maxlength="32" placeholder="请输入场景名称" show-word-limit type="text"></el-input>
          </el-form-item>
          <el-form-item label="Logo：" prop="logoFile">
            <el-upload class="upload_class" ref="uploadRef" :auto-upload="false" :limit="fileLength" list-type="picture-card"
                       v-model:file-list="fileListArray" :class="{ showAddImageIcon: fileListArray.length >= fileLength }"
                       :on-exceed="exceedFile" :on-change="uploadChange" accept=".jpg, .jpeg, .png, .JPG">
              <template #default>
                <el-icon class="uploader-icon"><Plus /></el-icon>
              </template>
              <template #file="{ file }">
                <img :src="file.url" class="el-upload-list__item-thumbnail" alt=""/>
                <div class="el-upload-list__item-actions">
                  <span class="el-upload-list__item-delete" @click="handleRemove">
                    <el-icon><Delete /></el-icon>
                  </span>
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {someCharmap} from "@/utils/validate";
import {Plus, Delete} from '@element-plus/icons-vue';
import {saveSort} from "@/api/modelCenter/modelClassification";
import { ElMessage, ElMessageBox, genFileId } from "element-plus";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch} from "vue";

export default {
  name: "AddModelClassDialog",
  components:{Plus,Delete},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "创建场景"
    },
    formDialog:{
      type: Object,
      default:()=>{
        return { }
      }
    }
  },
  setup(props) {

    const {emit} = getCurrentInstance();

    const validateSortName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的场景名称"));
      } else {
        callback();
      }
    };

    const validateLogoFile = (rule, value, callback) => {
      if (!that.fileListArray || !that.fileListArray.length) {
        callback(new Error("请上传Logo图标"));
      } else {
        callback();
      }
    };

    const that = reactive({
      fileLength: 1,
      formdialog: {},
      fileListArray: [],
      listLoading: false,
      dialog_visible: props.isVisible,
      rules:{
        sortName:[{required: true, trigger: "change", validator: validateSortName }],
        logoFile:[{required: true, trigger: "change", validator: validateLogoFile }],
      }
    })

    const formdialogRef = ref(null);
    const saveDialog = () => {
      formdialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formData = new FormData();

          //字段参数
          for (let key in that.formdialog) formData.append(key, that.formdialog[key]);
          if(that.fileListArray && that.fileListArray.length){
            for (let i = 0; i < that.fileListArray.length; i++) {
              if (that.fileListArray[i].raw) formData.append("logoFile", that.fileListArray[i].raw);
            }
          }
          saveSort(formData).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const uploadChange = (file,fileList)=>{
      const isLt1M = file.size / 1024 / 1024 < 2;
      const isIMAGE = file.type === "image/jpeg" || "image/jpg" || "image/png";

      if (!isIMAGE) {
        ElMessage({ type: "error", showClose: true, message: "上传图片格式只能为jpg、png、jpeg!" });
        fileList.splice(fileList.length - 1, 1);
        return
      }

      if (!isLt1M) {
        ElMessage({ type: "error", showClose: true, message: "上传文件大小不能超过 2MB!" });
        fileList.splice(fileList.length - 1, 1);
        return
      }

      that.fileListArray = fileList;
    }

    // 上传图片数量限制(覆盖上一个文件)
    const uploadRef = ref(null);
    const exceedFile = (files) => {
      uploadRef.value.clearFiles();
      const file = files[0];
      file.uid = genFileId();
      uploadRef.value.handleStart(file);
    };

    // 删除图片
    const handleRemove = () => {
      ElMessageBox.confirm(`确定删除该图片？`, "删除提示", {
        dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
        customClass: "elMessageBoxWarning", showClose:false,type: 'warning',
      }).then(() => {
        ElMessage({ type: "success", showClose: true, message: "删除成功！" });
        that.fileListArray.splice(that.fileListArray.length - 1, 1);
      }).catch(() => {
        console.log("取消删除！");
      });
    };

    // 初始化参数配置
    const initParamConfigFun = ()=>{
      // console.log(props.formDialog);
      let formDialog = JSON.parse(JSON.stringify(props.formDialog));
      if(formDialog.sortLogo){
        that.fileListArray = [{ url: formDialog.sortLogo }];
        delete formDialog.sortLogo
      }
      that.formdialog = JSON.parse(JSON.stringify(formDialog));
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

    return {...toRefs(that),watchVisible,watchDialogVisible,formdialogRef,saveDialog,uploadChange,exceedFile,uploadRef,handleRemove, initParamConfigFun}
  }
}
</script>

<style scoped lang="scss">
</style>
