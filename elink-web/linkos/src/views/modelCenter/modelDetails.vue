<template>
  <div class="app-container" v-resize="setTableMaxHeight">
    <div class="header-form" ref="headerFormRef">
      <model-details-card :activeModelId="activeModelId" @changEvent="changEvent"></model-details-card>
    </div>
    <div class="app-container-right">
      <Tabs v-model:tabsIndex="componentName" :tabsArray="tabsArray"></Tabs>
      <div class="tableContent">
        <component :is="componentName" :key="componentName" :componentMaxHeight="componentMaxHeight" :activeModelName="activeModelName" :activeModelId="activeModelId"></component>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {useRoute} from "vue-router";
import {reactive, ref, toRefs, defineComponent} from "vue";
import {DeviceList, FunctionDefine, EventComponent, ExtendedAttr, TopologyNode, ModelDetailsCard, FaultDefinition} from "@/views/modelCenter/component";

export default defineComponent({
  name: "modelDetails",
  components: {DeviceList, FunctionDefine, EventComponent, ExtendedAttr, TopologyNode, ModelDetailsCard, FaultDefinition},
  props:{
    contentMaxHeight:{
      type:Number,
      default: 520
    }
  },
  setup(props) {

    const route = useRoute();

    const that = reactive({
      activeModelName: "",
      componentMaxHeight: 380,
      componentName: "DeviceList",
      activeModelId: route.query.id, // 当前父级模型id
      tabsArray: [
        {id: "DeviceList", name: "设备列表"},
        {id: "FunctionDefine", name: "功能定义"},
        {id: "EventComponent", name: "事件"},
        {id: "ExtendedAttr", name: "扩展属性"},
        {id: "TopologyNode", name: "拓扑节点"},
      ],
      chargingGunList:[{id: "FaultDefinition", name: "故障定义"}]
    })

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.componentMaxHeight = props.contentMaxHeight - headerFormHeight - 28;
    }

    const changEvent = (data)=>{
      // console.log(data)
      that.activeModelName = data?.modelName;
      if(data.typeId >= 28 && data.typeId <= 30){
        let tabsArray = JSON.parse(JSON.stringify(that.tabsArray));
        let findItem = tabsArray.find(item => item.id === "FaultDefinition");
        if(!findItem){
          let findIndex = tabsArray.findIndex(item => item.id === "TopologyNode");
          for(let i = 0; i < that.chargingGunList.length;i++){
            tabsArray.splice(findIndex, 0, that.chargingGunList[i]);
          }
          that.tabsArray = JSON.parse(JSON.stringify(tabsArray));
        }
      }
    }

    return { ...toRefs(that), headerFormRef, setTableMaxHeight, changEvent}
  }
})
</script>

<style lang="scss" scoped>
.app-container {
  flex-direction: column;
}
</style>
