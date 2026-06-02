<template>
  <div class="uploadPicturesCom">
    <el-upload ref="uploadRef" v-model:file-list="fileListArray" :auto-upload="false" :limit="fileLength" :disabled="isDisabled" :on-change="uploadChange"
               :on-exceed="exceedFile" :accept="accept" class="upload_class" list-type="picture-card" :class="{ showAddImageIcon: fileListArray.length >= fileLength }">

      <template #tip>
        <div class="el-upload__tip"><slot name="tip_content"></slot></div>
      </template>

      <template #default><el-icon class="uploader-icon"><Plus/></el-icon></template>

      <template #file="{ file }">

        <!--        <img :src="file.url" alt="" class="el-upload-list__item-thumbnail"/>-->

        <div class="el-upload-list__item-thumbnail">
          <Viewer ref="viewerRef" :imageArray="[file.url]" :activeImage="file.url"></Viewer>
        </div>

        <div class="el-upload-list__item-actions">

          <template v-if="isPreview">
            <span class="el-upload-list__item-preview" @click="handlePreview">
              <el-icon><Search /></el-icon>
            </span>
          </template>

          <span class="el-upload-list__item-delete" @click="handleRemove(file)">
            <el-icon><Delete/></el-icon>
          </span>

        </div>
      </template>
    </el-upload>
  </div>
</template>

<script>
import Viewer from "@/components/component/Viewer.vue";
import {Delete, Plus, Search} from '@element-plus/icons-vue';
import {ElMessage, ElMessageBox, genFileId} from "element-plus";
import {defineComponent, getCurrentInstance, nextTick, reactive, ref, toRefs, watch} from "vue";

export default defineComponent({
  name: "UploadPicturesCom",
  components: {Viewer, Search, Plus, Delete},
  props: {
    // 上传张数
    uploadNum: {
      type: Number,
      default: 1
    },
    // 文件列表
    fileArray: {
      type: Array,
      default: () => []
    },
    // 上传文件限制大小
    fileSize: {
      type: Number,
      default: 2
    },
    // 是否可预览
    isPreview: {
      type: Boolean,
      default: false
    },
    // 是否禁用
    isDisabled: {
      type: Boolean,
      default: false
    },
    // 绑定的字段名称
    fieldName: {
      type: String,
      default: "fieldName"
    },
    accept: {
      type: String,
      default: ".jpg, .jpeg, .png, .JPG"
    },
  },
  emits: ["update:fileArray","changEvent"],
  setup(props) {

    const {emit} = getCurrentInstance();
    const that = reactive({
      deleteImageArray: [], // 删除的图片
      fileLength: props.uploadNum,
      fileListArray: props.fileArray,
    })

    // 上传图片数量限制(覆盖上一个文件)
    const uploadRef = ref(null);
    const exceedFile = (files) => {
      uploadRef.value.clearFiles();
      const file = files[0];
      file.uid = genFileId();
      uploadRef.value.handleStart(file);
    };

    const uploadChange = (file, fileList) => {
      const fileType = file.name.split(".")[1];
      const isIMAGE = props.accept.indexOf(fileType) !== -1;
      const isLt1M = file.size / 1024 / 1024 < props.fileSize;

      if (!isIMAGE) {
        ElMessage({ type: "warning", showClose: true, message: "请上传正确格式的文件！" });
        fileList.splice(fileList.length - 1, 1);
        return
      }

      if (!isLt1M) {
        ElMessage({type: "error", showClose: true, message: `上传文件大小不能超过 ${ props.fileSize }MB!`});
        fileList.splice(fileList.length - 1, 1);
        return
      }

      that.fileListArray = fileList;

      nextTick(()=>{
        emit("changEvent", { type: "uploadChange",fieldName: props.fieldName });
      })
    }

    // 删除图片
    const handleRemove = (file) => {
      ElMessageBox.confirm(`确定删除吗？`, "删除提示", {
        dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消',
        customClass: "elMessageBoxWarning", showClose: false, type: 'warning',
      }).then(() => {
        if(!file.raw)that.deleteImageArray.push(file.url);  // 记录删除的图片路径
        let findIndex = that.fileListArray.findIndex(item => item.url === file.url);
        that.fileListArray.splice(findIndex,1);
        emit("changEvent", { type: "removeFile",fieldName: props.fieldName });
      }).catch(() => {
        console.log("取消删除！");
      });
    };

    const viewerRef = ref([]);
    const handlePreview = ()=>{
      viewerRef.value.clickLookImage();
    }

    const watchFileArray = watch(() => props.fileArray, (newFileArray) => {
      that.fileListArray = newFileArray;
    }, {deep: true})

    const watchFileListArray = watch(() => that.fileListArray, (newFileListArray) => {
      emit("update:fileArray", newFileListArray);
    }, {deep: true})

    return {...toRefs(that), exceedFile, uploadRef, uploadChange, handleRemove, watchFileListArray, watchFileArray, handlePreview, viewerRef}
  }
})
</script>

<style lang="scss" scoped>
.uploadPicturesCom {
  :deep(.el-upload--picture-card){
    --el-border-color-darker: none;
    --el-fill-color-lighter: #EAEEF1;
    --el-upload-picture-card-size: 90px;
    --el-upload-list-picture-card-size: 90px;

    .uploader-icon{
      font-size: 16px;
    }
  }

  :deep(.el-upload-list__item){
    --color: #FFFFFF;
    --el-upload-list-picture-card-size: 90px;
  }

  .showAddImageIcon {
    :deep(.el-upload--picture-card){
      transition: all 0.28s;
      display: none !important;
      border: 1px dashed var(--el-border-color-darker);
    }
  }
}
</style>