<template>
  <div class="app-container">
    <HandleMenus :handleMenuArray="handleMenuArray" :isShowHeader="false" @handleMenuEvent="handleMenuEvent"/>
    <graphic-ass-table-list :contentMaxHeight="contentMaxHeight" :activeSelectId="activeSelectId" :activeTypeId="activeTypeId"></graphic-ass-table-list>
  </div>
</template>

<script>
import {setTreeData} from "@/utils";
import {reactive, toRefs, defineComponent, onMounted} from "vue";
import {GraphicAssTableList} from "@/views/visualization/component";
import {getSiteAssetsTreeList} from "@/api/visualization/graphicAssociation";

export default defineComponent({
  name: "graphicAssociation",
  components: {GraphicAssTableList},
  props: {
    contentMaxHeight: {
      type: Number,
      default: 520
    }
  },
  setup() {

    const that = reactive({
      activeTypeId: "",
      activeSelectId: "",
      handleMenuArray: [],
      allHandleMenuArray: [],
    })

    // 获取站点设备树形结构
    const querySiteAssetsTreeList = ()=>{
      getSiteAssetsTreeList({ timer: new Date() }).then(res=>{

        let handleMenuArray = res.data ? res.data : [];

        handleMenuArray.forEach(element => {
          if (element.type === 1) {
            element.disabled = true;
            element.iconName = "icon-zhandian";
          }

          if (element.type !== 1) {
            let typeDetailText = "--";
            if(element.type === 2 || element.type === 3 ){
              if(element.typeDetail === 1) typeDetailText = "直连";
              if(element.typeDetail === 2) typeDetailText = "网关";
              if(element.typeDetail === 3) typeDetailText = "子设备";
            }
            if(element.type === 4) typeDetailText = "子系统";
            element.name = `<span class="textTwo">${ element.name }</span><span class='typeDetailClass'>${ typeDetailText }</span>`;
          }
        });

        that.handleMenuArray = setTreeData(handleMenuArray);
        that.allHandleMenuArray = handleMenuArray;
      })
    }

    const handleMenuEvent = (menuButDate) => {
      // console.log(menuButDate);
      if (menuButDate.menuType === "clickTreeNode") {
        that.activeSelectId = menuButDate.id;
        that.activeTypeId = menuButDate.typeId;
      }
    }

    onMounted(()=>{
      querySiteAssetsTreeList();
    })

    return {...toRefs(that), handleMenuEvent, querySiteAssetsTreeList }
  }
})
</script>

<style lang="scss" scoped></style>