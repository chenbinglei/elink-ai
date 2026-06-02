<template>
  <div class="equipmentPhotos">


    <div class="add_photo_class pointer" v-for="(item,index) in list" :key="index">
      <div class="photo_hover_class flex-jc-ai-center">
        <div class="hover_li" @click="clickOperateBut(1,index)">
          <el-icon size="24"><View /></el-icon>
        </div>
        <div class="hover_li" @click="clickOperateBut(2,index)">
          <el-icon size="24" color="#EC2020"><Delete /></el-icon>
        </div>
      </div>
      <Viewer ref="viewerRef" :imageArray="[item]" :activeImage="item"></Viewer>
    </div>

    <div class="add_photo_class pointer" @click="uploadDevicePhotosVisible = true">
      <div class="add_photo_top"><el-icon><Plus /></el-icon></div>
      <div class="add_photo_bottom">上传图片</div>
    </div>

    <UploadDevicePhotos v-if="uploadDevicePhotosVisible" v-model:isVisible="uploadDevicePhotosVisible" @changeEvent="changeEvent"></UploadDevicePhotos>

  </div>
</template>

<script>
import Viewer from "@/components/component/Viewer";
import {ElMessage, ElMessageBox} from "element-plus";
import UploadDevicePhotos from "./UploadDevicePhotos";
import {saveDevice} from "@/api/deviceCenter/deviceList";
import { Plus, Delete, View } from '@element-plus/icons-vue';
import {getCurrentInstance, reactive, toRefs, watch, ref, defineComponent} from "vue";

export default defineComponent({
  name: "EquipmentPhotos",
  components:{ Plus, Delete, View, UploadDevicePhotos, Viewer },
  props: {
    deviceInfo: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup(props) {

    const viewerRef = ref([]);
    const {emit} = getCurrentInstance();

    const that = reactive({
      list: [],
      uploadDevicePhotosVisible: false
    })

    const changeEvent = ()=>{
      emit("changeEvent");
    }

    const clickOperateBut = (operate,index)=>{
      if(operate === 1){
        viewerRef.value[index].clickLookImage();
      }

      if(operate === 2){
        ElMessageBox.confirm(`确定删除该图片？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "elMessageBoxWarning", showClose:false,type: 'warning',
        }).then(() => {
          saveDevice({ id:props.deviceInfo.id,deleteImagePath: that.list[index] }).then(()=>{
            emit("changeEvent");
            ElMessage({ type: "success", showClose: true, message: "删除成功！" });
          })
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    }

    const watchDeviceInfo = watch(() => props.deviceInfo, (newDeviceInfo) => {
      if (newDeviceInfo.imagePaths) that.list = newDeviceInfo.imagePaths.split(',');
    }, { deep: true,immediate:true })

    return {...toRefs(that), watchDeviceInfo, changeEvent, clickOperateBut, viewerRef }
  }
})
</script>

<style scoped lang="scss">

.equipmentPhotos{
  display: flex;
  flex-wrap: wrap;

  .add_photo_class{
    width: 180px;
    height: 160px;
    border-radius: 4px;
    box-sizing: border-box;
    border: 1px solid #DBDBDD;
    margin-right: 12px;
    margin-bottom: 12px;

    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    position: relative;

    .photo_hover_class{
      width: 100%;
      height: 100%;
      position: absolute;
      left: 0;
      top: 0;
      z-index: 100;
      border-radius: 4px;
      background-color: rgba(0,0,0,.6);
      display: none;

      .hover_li{
        color: #FFFFFF;
        font-size: 18px;
        margin-right: 8px;
        &:last-child{
          margin-right: 0;
        }
      }
    }

    .add_photo_top{
      width: 28px;
      height: 28px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 4px;
      box-sizing: border-box;
      border: 1px solid #DBDBDD;
    }

    .add_photo_bottom{
      margin-top: 12px;
      font-size: 12px;
      color: #242424;
    }

    &:last-child{
      margin-right: 0;
    }

    &:hover{
      .photo_hover_class{
        display: flex;
      }
    }
  }
}
</style>
