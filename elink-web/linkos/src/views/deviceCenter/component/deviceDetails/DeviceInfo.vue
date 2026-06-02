<template>
  <div class="content_body">
    <template v-for="(item,index) in tabsCardArray" :key="index">
      <div class="content_body_list" v-loading="listLoading">
        <TitleView :title="item.name">
          <template v-if="item.componentName === 'DeviceInformation'" #headerRight>
            <span class="iconfont icon-bianji pointer" @click="editDeviceInfoVisible = true"></span>
          </template>
          <template #content>
            <component :is="item.componentName" :deviceInfo="activeDeviceInfo" :activeDeviceId="activeDeviceId" @changeEvent="queryDeviceBasicInfoById"></component>
          </template>
        </TitleView>
      </div>
    </template>

    <add-device-dialog v-if="editDeviceInfoVisible" v-model:isVisible="editDeviceInfoVisible" :titleName="titleName" :activeDeviceId="activeDeviceId"
                       :editingType="editingType" @changeEvent="queryDeviceBasicInfoById" />
  </div>
</template>

<script>
import AddDeviceDialog from "../deviceList/AddDeviceDialog";
import {onMounted, reactive, toRefs, defineComponent} from "vue";
import {findDeviceBasicInfoById} from "@/api/deviceCenter/deviceList";
import {DeviceInformation,ModelInformation,AssetInformation,EquipmentPhotos} from "./component";

export default defineComponent({
  name: "DeviceInfo",
  components: { DeviceInformation,ModelInformation,AssetInformation,EquipmentPhotos,AddDeviceDialog },
  props: {
    activeDeviceId: {
      type: [Number, String],
      default: ""
    }
  },
  setup(props) {

    const that = reactive({
      listLoading: false, // 表格加载
      activeDeviceInfo: {},

      editingType: 1,
      titleName: "编辑设备信息",
      editDeviceInfoVisible: false,

      tabsCardArray: [
        {componentName: "DeviceInformation", name: "设备信息"},
        {componentName: "ModelInformation", name: "模型信息"},
        {componentName: "AssetInformation", name: "资产信息"},
        {componentName: "EquipmentPhotos", name: "设备照片"}
      ],
    })

    // 根据设备id查询设备基本信息数据
    const queryDeviceBasicInfoById = ()=>{
      that.listLoading = true;
      findDeviceBasicInfoById({ deviceId: props.activeDeviceId }).then(res=>{
        that.activeDeviceInfo = res.data;
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    onMounted(()=>{
      queryDeviceBasicInfoById();
    })

    return {...toRefs(that),queryDeviceBasicInfoById}
  }
})
</script>

<style scoped lang="scss">
.content_body_list{
  padding: 12px 16px;
  box-sizing: border-box;
}
</style>
