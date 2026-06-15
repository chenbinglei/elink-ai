<template>
  <div class="app-container">
    <HandleMenus ref="handleMenusRef" :handleMenuArray="handleMenuArray" :isShowHeader="false" @handleMenuEvent="handleMenuEvent"></HandleMenus>
    <device-list-table ref="deviceListTableRef" :contentMaxHeight="contentMaxHeight" :activeSiteId="activeSiteId" :handleMenuIds="handleMenuIds"></device-list-table>
  </div>
</template>

<script lang="ts">
import {DeviceListTable} from "@/views/deviceCenter/component";
import {onActivated, reactive, toRefs, defineComponent} from "vue";
import {getSiteDeviceTreeList} from "@/api/deviceCenter/deviceList";

export default defineComponent({
  name: "deviceList",
  components:{DeviceListTable},
  props:{
    contentMaxHeight:{
      type:Number,
      default: 520
    }
  },
  setup() {

    const that = reactive({
      handleMenuIds: [], // 全部站点Ids
      activeSiteId: null,
      handleMenuArray: [],
    })

    // 获取站点设备树形结构
    const querySiteDeviceTreeList = ()=>{
      getSiteDeviceTreeList({ type: 1,timer: new Date() }).then(res=>{
        let handleMenuIds = [];
        let handleMenuArray = res.data ? res.data : [];
        for(let i = 0;i < handleMenuArray.length;i++)handleMenuIds.push(handleMenuArray[i].id);

        handleMenuArray.unshift({id: "quanBuId",name: "全部" });
        that.handleMenuIds = JSON.parse(JSON.stringify(handleMenuIds));
        that.handleMenuArray = JSON.parse(JSON.stringify(handleMenuArray));
      })
    }

    const handleMenuEvent = (menuButDate) => {
      // console.log(menuButDate);
      if (menuButDate.menuType === "clickTreeNode") {
        that.activeSiteId = menuButDate.id !== "quanBuId" ? menuButDate.id  : "";
      }
    }

    onActivated(() => {
      querySiteDeviceTreeList();
    })

    return {...toRefs(that), handleMenuEvent,querySiteDeviceTreeList}
  }
})
</script>

<style scoped lang="scss">
</style>
