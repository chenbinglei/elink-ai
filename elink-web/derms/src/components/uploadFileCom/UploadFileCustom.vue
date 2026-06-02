<template>
  <div class="uploadFileCustom" :class="[class_file_type === 1 ? 'uploadImageClass' : 'uploadFileClass']">
    <el-upload ref="uploadRef" v-model:file-list="fileListArray" :auto-upload="false" :limit="fileLength"
      :disabled="isDisabled" :on-change="uploadChange" :drag="class_file_type === 2" class="upload_class"
      :on-exceed="exceedFile" :accept="acceptType" :on-remove="handleRemove">
      <template #tip>
        <div class="el-upload__tip">
          <slot name="tip_content"></slot>
        </div>
      </template>
      <template #default>
        <template v-if="class_file_type === 1">
          <el-icon class="uploader-icon">
            <Plus />
          </el-icon>
        </template>
        <template v-if="class_file_type === 2">
          <el-icon class="el-icon--upload" size="28"><upload-filled /></el-icon>
          <div class="el-upload__text">将文件拖放到此处或<em>点击上传</em></div>
        </template>
        <template v-if="class_file_type === 3">
          <el-button type="primary"><el-icon>
              <Upload />
            </el-icon>点击上传</el-button>
        </template>
      </template>
    </el-upload>
  </div>
</template>

<script>
import { ElMessage, genFileId } from "element-plus";
import { Plus, UploadFilled } from '@element-plus/icons-vue';
import { defineComponent, getCurrentInstance, reactive, ref, toRefs, watch } from "vue";

export default defineComponent({
  name: "UploadFileCustom",
  components: { Plus, UploadFilled },
  props: {
    // 上传张数
    uploadNum: {
      type: Number,
      default: 1
    },
    fileArray: {
      type: Array,
      default: () => []
    },
    // 上传文件限制大小
    fileSize: {
      type: Number,
      default: 2
    },
    // 是否禁用
    isDisabled: {
      type: Boolean,
      default: false
    },
    fieldName: {
      type: String,
      default: "fieldName"
    },
    acceptType: {
      type: String,
      default: ""
    },
    // 1: 上传图片样式   2： 上传文件样式
    classFileType: {
      type: Number,
      default: 1
    }
  },
  emits: ["update:fileArray"],
  setup (props) {

    const { emit } = getCurrentInstance();
    const that = reactive({
      deleteImageArray: [], // 删除的图片
      fileLength: props.uploadNum,
      fileListArray: props.fileArray,
      class_file_type: props.classFileType,
    });

    // 上传图片数量限制(覆盖上一个文件)
    const uploadRef = ref(null);
    const exceedFile = (files) => {
      uploadRef.value.clearFiles();
      const file = files[0];
      file.uid = genFileId();
      uploadRef.value.handleStart(file);
    };

    const uploadChange = (file, fileList) => {
      const isLt1M = file.size / 1024 / 1024 < props.fileSize;

      if (!isLt1M) {
        ElMessage({ type: "error", showClose: true, message: `上传文件大小不能超过 ${props.fileSize}MB!` });
        fileList.splice(fileList.length - 1, 1);
        return;
      }
      that.fileListArray = fileList;
    };

    // 删除
    const handleRemove = (file) => {
      if (!file.raw) that.deleteImageArray.push(file.url);  // 记录删除的图片路径
      emit("changEvent", { type: "removeFile", fieldName: props.fieldName });
    };

    const viewerRef = ref([]);
    const handlePreview = () => {
      viewerRef.value.clickLookImage();
    };

    const watchFileArray = watch(() => props.fileArray, (newFileArray) => {
      that.fileListArray = newFileArray;
    }, { deep: true });

    const watchFileListArray = watch(() => that.fileListArray, (newFileListArray) => {
      emit("update:fileArray", newFileListArray);
    }, { deep: true });

    return { ...toRefs(that), exceedFile, uploadRef, uploadChange, handleRemove, watchFileListArray, watchFileArray, handlePreview, viewerRef };
  }
});
</script>

<style lang="scss" scoped>
.uploadFileCustom {
  width: 100%;
}

.uploadImageClass {

  :deep(.el-upload) {
    width: 90px;
    height: 90px;
    border-radius: 4px;
    background-color: #ffffff26;
    --el-border-color-darker: none;

    .uploader-icon {
      font-size: 16px;
    }
  }
}

.uploadFileClass {
  :deep(.el-upload) {
    --el-fill-color-blank: #ffffff26;
    --el-upload-dragger-padding-horizontal: 2px;

    .el-upload-dragger .el-icon--upload {
      margin-bottom: 0;
    }
  }
}
</style>