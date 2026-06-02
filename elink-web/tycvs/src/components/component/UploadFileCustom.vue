<template>
  <div class="uploadFileCustom">
    <el-upload ref="uploadRef" v-model:file-list="fileListArray" :auto-upload="false" :limit="fileLength" :disabled="isDisabled" :on-change="uploadChange"
               class="upload_class" :on-exceed="exceedFile" :accept="acceptType" :on-remove="handleRemove">
      <template #tip>
        <div class="el-upload__tip">
          <slot name="tip_content"></slot>
        </div>
      </template>
      <template #default><el-icon class="uploader-icon"><Plus/></el-icon></template>
    </el-upload>
  </div>
</template>

<script>
import {Plus} from '@element-plus/icons-vue';
import {ElMessage, genFileId} from "element-plus";
import {defineComponent, getCurrentInstance, reactive, ref, toRefs, watch} from "vue";

export default defineComponent({
  name: "UploadFileCustom",
  components: {Plus},
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
  },
  emits: ["update:fileArray"],
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
      const isLt1M = file.size / 1024 / 1024 < props.fileSize;

      if (!isLt1M) {
        ElMessage({type: "error", showClose: true, message: `上传文件大小不能超过 ${ props.fileSize }MB!`});
        fileList.splice(fileList.length - 1, 1);
        return
      }

      that.fileListArray = fileList;
    }

    // 删除
    const handleRemove = (file) => {
      if(!file.raw)that.deleteImageArray.push(file.url);  // 记录删除的图片路径
      emit("changEvent", { type: "removeFile",fieldName: props.fieldName });
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
.uploadFileCustom {
  width: 100%;

  :deep(.el-upload){
    width: 90px;
    height: 90px;
    border-radius: 4px;
    background-color: #EAEEF1;
    --el-border-color-darker: none;

    .uploader-icon{
      font-size: 16px;
    }
  }
}
</style>