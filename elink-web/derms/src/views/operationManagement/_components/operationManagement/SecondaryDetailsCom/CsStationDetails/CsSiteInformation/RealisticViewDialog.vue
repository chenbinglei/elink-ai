<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="648" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <UploadPicturesCom ref="uploadPicturesComRef" :uploadNum="10" v-model:fileArray="fileArray">
          <template #tip_content>
            <div class="alter_text">最多不超过10张，支持PNG/JPG文件，大小不超过2M</div>
          </template>
        </UploadPicturesCom>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {updateSiteImageById} from "@/api/operationManagement/CsStationDetails";
import UploadPicturesCom from "@/components/uploadFileCom/UploadPicturesCom.vue";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "RealisticViewDialog",
  components:{UploadPicturesCom},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    returnDataInfo: {
      type: Object,
      default: ()=>{
        return { };
      }
    }
  },
  setup(props){

    const uploadPicturesComRef = ref(null);
    const {emit} = getCurrentInstance();

    const that = reactive({
      fileArray: [],
      formDialog: {},
      listLoading: false,
      titleName: "编辑实景图",
      oldFileArray: props.imagePath,
      dialog_visible: props.isVisible,
    });

    const clickConfirmBut = () => {
      that.listLoading = true;
      let formData = new FormData();
      let deleteImagePath = uploadPicturesComRef.value.deleteImageArray;

      if(that.fileArray && that.fileArray.length){
        for (let i = 0; i < that.fileArray.length; i++) {
          if (that.fileArray[i].raw){
            formData.append("imageFiles", that.fileArray[i].raw);
          }
        }
      }

      formData.append("id", that.formDialog.id); //站点id
      formData.append("deleteImagePaths", deleteImagePath.join(','));

      updateSiteImageById(formData).then(()=>{
        emit("changeEvent");
        that.dialog_visible = false;
        ElMessage({ type: "success", showClose: true, message: "编辑成功！" });
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    const initParamConfigFun = () => {
      let returnDataInfo = JSON.parse(JSON.stringify(props.returnDataInfo));
      if(returnDataInfo.imagePath && returnDataInfo.imagePath.length){
        let fileArray = [];
        for(let i = 0;i < returnDataInfo.imagePath.length;i++){
          fileArray.push({ url: returnDataInfo.imagePath[i] });
        }
        that.fileArray = JSON.parse(JSON.stringify(fileArray));
        // console.log(that.fileArray);
      }
      delete returnDataInfo.imagePath;
      that.formDialog = JSON.parse(JSON.stringify(returnDataInfo));
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut, uploadPicturesComRef};
  }
});
</script>
<style lang="scss" scoped>

</style>