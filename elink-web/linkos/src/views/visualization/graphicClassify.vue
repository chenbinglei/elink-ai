<template>
  <div class="app-container">
    <HandleMenus :handleMenuArray="handleMenuArray" isCollapseBut title="资产分类" @handleMenuEvent="handleMenuEvent"/>
    <graphic-class-table-list :contentMaxHeight="contentMaxHeight" :activeTypeId="activeTypeId"></graphic-class-table-list>
  </div>
</template>

<script>
import {setTreeData} from "@/utils";
import {onMounted, reactive, toRefs, defineComponent} from "vue";
import {getAssetTypeList} from "@/api/modelCenter/modelManagement";
import {GraphicClassTableList} from "@/views/visualization/component";

export default defineComponent({
  name: "graphicClassify",
  components:{GraphicClassTableList},
  props: {
    contentMaxHeight: {
      type: Number,
      default: 520
    }
  },
  setup(){

    const that = reactive({
      activeTypeId: "",
      handleMenuArray: [],
      allHandleMenuArray: []
    })

    const handleMenuEvent = (menuButDate) => {
      // console.log(menuButDate);
      if (menuButDate.menuType === "clickTreeNode") {
        if (menuButDate.type === 2) that.activeTypeId = menuButDate.id;
      }
    }

    // 获取设备类型列表
    const queryAssetTypeList = () => {
      getAssetTypeList({ timer: new Date(), pageName: "graphicClassify" }).then(res => {
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

    return {...toRefs(that), queryAssetTypeList, handleMenuEvent}
  }
})

</script>

<style scoped lang="scss">

</style>