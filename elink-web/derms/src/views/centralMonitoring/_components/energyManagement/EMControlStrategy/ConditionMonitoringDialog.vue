<template>
  <Dialog v-model:isVisible="dialog_visible" :footerVisible="false" :title="titleName" closeOnClickModal disabledLoading width="65vw">
    <template v-slot:content>
      <div v-loading="listLoading" class="dialog-main">
        <div class="dialog-main-left">
          <HandleMenus :handleMenuArray="handleMenuArray" :isShowHeader="false" :treeProps="treeProps" custom-tree-class="leftArrowClass" @handleMenuEvent="handleMenuEvent"/>
        </div>
        <div class="dialog-main-right">
          <template v-if="activeDeviceId">
            <DeviceRealTimeStatus :activeDeviceId="activeDeviceId" :activeDeviceType="activeDeviceType"></DeviceRealTimeStatus>
            <DeviceOperatingCurve :activeDeviceId="activeDeviceId" :activeDeviceType="activeDeviceType" :activeDeviceTypeId="activeDeviceTypeId"></DeviceOperatingCurve>
          </template>
          <null-data v-else words="请先选择设备"></null-data>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {setTreeData} from "@/utils";
import HandleMenus from "@/components/handleMenu/HandleMenus.vue";
import {querySystemDeviceList} from "@/api/centralMonitoring/energyManagement";
import DeviceRealTimeStatus from "./ConditionMonitoringDialog/DeviceRealTimeStatus.vue";
import DeviceOperatingCurve from "./ConditionMonitoringDialog/DeviceOperatingCurve.vue";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, onMounted} from "vue";

export default defineComponent({
  name: "ConditionMonitoringDialog",
  components: {HandleMenus,DeviceRealTimeStatus,DeviceOperatingCurve},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeSiteId: {
      type: [String, Number],
      default: ""
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      listLoading: false,
      titleName: "状态监控",
      handleMenuArray: [],
      dialog_visible: props.isVisible,
      treeProps: {value: 'id', label: 'deviceName', children: 'children'},
      deviceTypeList: [
        {deviceName: "储能系统", id: "chuNengXiTong", fieldName: "storageDeviceList"},
        {deviceName: "光伏系统", id: "guangFuXiTong", fieldName: "pvDeviceList"},
        {deviceName: "电桩系统", id: "dianZhuangXiTong", fieldName: "pileDeviceList"},
        {deviceName: "配电系统", id: "peiDianXiTong", fieldName: "powerDeviceList"},
      ],

      activeDeviceId: "", // 当前设备id
      activeDeviceType: "", // 当前资产分类
      activeDeviceTypeId: "", // 当前资产分类id
    });

    const handleMenuEvent = (data)=>{
      // console.log(data);
      if(data.menuType === "clickTreeNode" && data.isDevice){
        that.activeDeviceType = data.parentId;
        that.activeDeviceTypeId = data.typeId;
        that.activeDeviceId = data.id;
      }
    };

    const initParamConfigFun = () => {};

    // 查询系统设备列表
    const findSystemDeviceList = () => {
      that.listLoading = true;
      querySystemDeviceList({siteId: props.activeSiteId}).then(res => {
        let handleMenuArray = [];
        let returnDataInfo = res.data ? res.data : {};
        for (let i = 0; i < that.deviceTypeList.length; i++) {
          if (returnDataInfo[that.deviceTypeList[i].fieldName] && returnDataInfo[that.deviceTypeList[i].fieldName].length) {
            handleMenuArray.push(that.deviceTypeList[i]);
            returnDataInfo[that.deviceTypeList[i].fieldName].forEach(item => {
              handleMenuArray.push({...item,isDevice: true,parentId: that.deviceTypeList[i].id});
            });
          }
        }
        that.handleMenuArray = JSON.parse(JSON.stringify(setTreeData(handleMenuArray)));
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      });
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      findSystemDeviceList();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, findSystemDeviceList, handleMenuEvent};
  }
});
</script>

<style lang="scss" scoped>
.dialog-main {
  display: flex;
  max-height: 70vh;

  .dialog-main-left {
    width: 25%;
    max-width: 280px;
    height: 100%;
    overflow-y: auto;

    :deep(.handleMenu){
      height: 100%;
      border-right: none;
    }
  }

  .dialog-main-right {
    flex: 1;
    width: 2px;
    flex-basis: auto;
    overflow-y: auto;
    box-sizing: border-box;
    border-left: 1px solid rgba(255, 255, 255, 0.1);
  }
}
</style>