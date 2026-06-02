<template>
  <div class="app-container">
    <HandleMenus ref="handleMenusRef" :handleMenuArray="handleMenuArray" isCollapseBut title="资产分类" @handleMenuEvent="handleMenuEvent"/>
    <model-table-list :contentMaxHeight="contentMaxHeight" :activeTypeId="activeTypeId"></model-table-list>
  </div>
</template>

<script>
import {setTreeData} from "@/utils";
import { ModelTableList } from "@/views/modelCenter/component";
import {onMounted, reactive, toRefs, defineComponent} from "vue";
import {getAssetTypeList} from "@/api/modelCenter/modelManagement";

export default defineComponent({
  name: "modelManagement",
  components:{ModelTableList},
  props:{
    contentMaxHeight:{
      type:Number,
      default: 520
    }
  },
  setup() {

    const that = reactive({
      activeTypeId: "",
      handleMenuArray: [],
      allHandleMenuArray: [],
    })

    const handleMenuEvent = (menuButDate) => {
      // console.log(menuButDate);
      if (menuButDate.menuType === "clickTreeNode") {
        if (menuButDate.type === 2) that.activeTypeId = menuButDate.id;
      }
    }

    // 获取设备类型列表
    const queryAssetTypeList = () => {
      getAssetTypeList({timer: new Date(), pageName: "modelManagement"}).then(res => {
        let assetTypeList = res.data ? res.data : [];
        for (let i = 0; i < assetTypeList.length; i++) {
          if(assetTypeList[i].type === 1){
            assetTypeList[i].disabled = true;
            assetTypeList[i].iconName = "icon-wenjianjia";
          }
          assetTypeList[i].name = assetTypeList[i].typeName;
        }
        that.handleMenuArray = setTreeData(assetTypeList);
        that.allHandleMenuArray = JSON.parse(JSON.stringify(assetTypeList));
        // console.log(that.handleMenuArray);
      })
    }

    onMounted(() => {
      queryAssetTypeList();
    })

    return {...toRefs(that), queryAssetTypeList, handleMenuEvent }
  }
})
</script>

<style scoped lang="scss"></style>
