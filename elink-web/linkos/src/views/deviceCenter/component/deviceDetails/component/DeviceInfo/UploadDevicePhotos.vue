<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="780" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formdialogRef" :model="formdialog" :rules="rules" label-width="110px">
          <el-form-item label="照片：" prop="imageFiles">
            <el-upload class="upload_class" ref="uploadRef" :auto-upload="false" :limit="fileLength" list-type="picture-card"
                       v-model:file-list="fileListArray" :class="{ showAddImageIcon: fileListArray.length >= fileLength }"
                       :on-exceed="exceedFile" :on-change="uploadChange" accept=".jpg, .jpeg, .png, .JPG">
              <template #default>
                <el-icon class="uploader-icon"><Plus /></el-icon>
              </template>
              <template #file="{ file }">
                <img :src="file.url" class="el-upload-list__item-thumbnail" alt=""/>
                <div class="el-upload-list__item-actions">
                  <span class="el-upload-list__item-delete" @click="handleRemove(file)">
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

<script>
import {useRoute} from "vue-router";
import {ElMessage} from "element-plus";
import {Plus, Delete} from '@element-plus/icons-vue';
import {saveDevice} from "@/api/deviceCenter/deviceList";
import {getCurrentInstance, reactive, ref, toRefs, watch} from "vue";

export default {
  name: "UploadDevicePhotos",
  components:{Plus,Delete},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
  },
  setup(props){

    const route = useRoute();
    const {emit} = getCurrentInstance();

    const validateImageFiles = (rule, value, callback) => {
      if (!that.fileListArray || !that.fileListArray.length) {
        callback(new Error("请上传设备图片"));
      } else {
        callback();
      }
    };

    const that = reactive({
      fileLength: 12,
      formdialog: {},
      fileListArray: [],
      listLoading: false,
      titleName:"上传设备图片",
      activeDeviceId: route.query.id, // 当前设备模型id
      dialog_visible: props.isVisible,

      rules:{
        imageFiles:[{required: true, trigger: "change", validator: validateImageFiles }],
      }
    })

    const formdialogRef = ref(null);
    const saveDialog = () => {
      formdialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formData = new FormData();
          formData.append("id", that.activeDeviceId);

          if(that.fileListArray && that.fileListArray.length){
            for (let i = 0; i < that.fileListArray.length; i++) {
              if (that.fileListArray[i].raw) formData.append("imageFiles", that.fileListArray[i].raw);
            }
          }
          saveDevice(formData).then(()=>{
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

    // 上传图片数量限制
    const exceedFile = (file, fileList) => {
      ElMessage({type: "warning", showClose: true, message: `只能上传${ that.fileLength }张图片`});
    };

    // 删除图片
    const handleRemove = (file) => {
      let findIndex = that.fileListArray.findIndex(item => item.url === file.url);
      that.fileListArray.splice(findIndex,1);
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    return { ...toRefs(that), watchVisible, watchDialogVisible, formdialogRef, saveDialog,uploadChange,exceedFile,handleRemove}
    }
}
</script>

<style scoped>

</style>
