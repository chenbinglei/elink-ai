<template>
  <div class="app-container">
    <Tabs v-model:tabsIndex="componentName" :tabsArray="tabsArray"></Tabs>
    <div class="app-container-right scrollbarStyle">
      <component :is="componentName" :key="componentName" :activeDeviceId="activeDeviceId"></component>
    </div>
  </div>
</template>

<script>
import {useRoute} from "vue-router"
import {reactive, toRefs, onMounted} from "vue";
import {DeviceInfo, FunctionalAttr, DeviceEvent, ExtendedAttr, GatewayChildDevice, DeviceTopology, ChargingGunManagement} from "@/views/deviceCenter/component";

export default {
  name: "deviceDetails",
  components: {DeviceInfo, FunctionalAttr, DeviceEvent, ExtendedAttr, GatewayChildDevice, DeviceTopology, ChargingGunManagement},
  setup() {

    const route = useRoute();
    const that = reactive({
      tabsArray: [],
      typeId: route.query.typeId, // 当前设备的资产类型
      componentName: "DeviceInfo",
      activeDeviceId: route.query.id, // 当前设备模型id
      oldTabsArray: [
        {id: "DeviceInfo", name: "设备信息"},
        {id: "FunctionalAttr", name: "功能属性"},
        {id: "DeviceEvent", name: "事件"},
        {id: "ExtendedAttr", name: "扩展属性"},
        {id: "GatewayChildDevice", name: "网关子设备"},
        {id: "ChargingGunManagement", name: "充电枪管理"},
        {id: "DeviceTopology", name: "设备拓扑"}
      ]
    })

    const initParamConfigFun = () => {
      let newTypeId = that.typeId >= 0 ? Number(that.typeId) : -1;
      let tabsArray = JSON.parse(JSON.stringify(that.oldTabsArray));

      // 网关设备
      if (newTypeId !== 31 && newTypeId !== 32) {
        let findIndex = tabsArray.findIndex(item => item.id === "GatewayChildDevice");
        tabsArray.splice(findIndex, 1);
      }

      // 电桩设备
      if (newTypeId < 28 || newTypeId > 30) {
        let findIndex = tabsArray.findIndex(item => item.id === "ChargingGunManagement");
        tabsArray.splice(findIndex, 1);
      }

      that.tabsArray = JSON.parse(JSON.stringify(tabsArray));
    }

    onMounted(() => {
      initParamConfigFun();
    })

    return {...toRefs(that), initParamConfigFun}
  },
}
</script>

<style lang="scss" scoped>
.app-container {
  flex-direction: column;
  padding-top: 21px;
}
</style>
