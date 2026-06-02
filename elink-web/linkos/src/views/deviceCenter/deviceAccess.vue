<template>
  <div class="app-container">
    <HandleMenus :handleMenuArray="handleMenuArray" :isShowHeader="false" @handleMenuEvent="handleMenuEvent" />
    <div class="app-container-right">
      <template v-if="activeType !== 1">
        <div class="content_list">
          <device-details-card ref="deviceDetailsCardRef" :activeDeviceId="activeDeviceId" @changeEvent="changeEvent"></device-details-card>
        </div>
        <div class="content_list tableContent scrollbarStyle">
          <DirectConnectionComponent v-if="typeDetail === 1" :activeDeviceId="activeDeviceId" @changeEvent="changeEvent"></DirectConnectionComponent>
          <GatewayComponent v-if="typeDetail === 2" :deviceInfo="deviceInfo" :activeDeviceId="activeDeviceId" @changeEvent="changeEvent" />
          <ChildDeviceComponent v-if="typeDetail === 3" :activeDeviceId="activeDeviceId"></ChildDeviceComponent>
        </div>
      </template>
      <template v-else><null-data words="站点层次不可操作"></null-data></template>
    </div>
  </div>
</template>

<script>
import {setTreeData} from "@/utils";
import {onActivated, reactive, toRefs, ref} from "vue";
import {getSiteDeviceTreeList} from "@/api/deviceCenter/deviceList";
import {DeviceDetailsCard,DirectConnectionComponent,GatewayComponent,ChildDeviceComponent} from "@/views/deviceCenter/component";

export default {
  name: "deviceAccess",
  components:{DeviceDetailsCard,DirectConnectionComponent,GatewayComponent,ChildDeviceComponent},
  setup() {

    const deviceDetailsCardRef = ref(null);

    const that = reactive({
      activeType: 1,
      typeDetail: 1,
      deviceInfo: {},
      activeDeviceId: "",
      handleMenuArray: [],
      allHandleMenuArray: [],
    })

    // 获取站点设备树形结构
    const querySiteDeviceTreeList = ()=>{
      getSiteDeviceTreeList({ type: 0,timer: new Date() }).then(res=>{

        let handleMenuArray = res.data ? res.data : [];

        handleMenuArray.forEach(element => {
          if (element.type === 1) {
            element.disabled = true;
            element.iconName = "icon-zhandian";
          }

          if (element.type !== 1) {
            let typeDetailText = "--";
            if(element.typeDetail === 1) typeDetailText = "直连";
            if(element.typeDetail === 2) typeDetailText = "网关";
            if(element.typeDetail === 3) typeDetailText = "子设备";
            element.name = `<span class="textTwo flex-all">${ element.name }</span><span class='typeDetailClass'>${ typeDetailText }</span>`;
          }
        });

        that.handleMenuArray = setTreeData(handleMenuArray);
        that.allHandleMenuArray = handleMenuArray;
      })
    }

    const handleMenuEvent = (menuButDate) => {
      // console.log(menuButDate);
      if (menuButDate.menuType === "clickTreeNode") {
        that.activeType = menuButDate.type;
        that.activeDeviceId = menuButDate.id;
        that.typeDetail = menuButDate.typeDetail;
      }
    }

    const changeEvent = (data)=>{

      if(data.operateType === "deviceDetailsCard"){
        that.deviceInfo = data;
        // console.log(that.deviceInfo)
      }

      if(data.operateType === "registerDevice"){
        deviceDetailsCardRef.value.queryDeviceBasicInfoById();
      }
    }

    onActivated(()=>{
      querySiteDeviceTreeList();
    })

    return {...toRefs(that), handleMenuEvent,querySiteDeviceTreeList, changeEvent, deviceDetailsCardRef}
  }
}
</script>

<style scoped lang="scss">
.app-container-right{
  padding-left: 12px;
  box-sizing: border-box;

  .content_list{
    padding: 16px 16px 12px 16px;
    border-radius: 4px;
    box-sizing: border-box;
    border: 1px solid #DBDBDD;
    margin-bottom: 16px;

    &:last-child{
      margin-bottom: 0;
    }
  }
}
</style>
