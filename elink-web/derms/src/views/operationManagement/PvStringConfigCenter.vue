<template>
  <!-- 串珠配置 -->
  <div class="app-container-right content_border">
    <div class="content_left">
      <HandleMenus :handleMenuArray="handleMenuArray" :treeProps="treeProps" title="组串配置"
        @handleMenuEvent="handleMenuEvent"></HandleMenus>
    </div>
    <div class="content_right">
      <PvStringConfigTable :activeSiteId="activeSiteId"></PvStringConfigTable>
    </div>
  </div>
</template>

<script>
import { onMounted, reactive, toRefs, defineComponent } from "vue";
import HandleMenus from "@/components/handleMenu/HandleMenus.vue";
import PvStringConfigTable from './_components/operationManagement/PhotovoltaicOperation/PvStringConfigCenter/PvStringConfigTable.vue';
import { findSiteInfoByUserId } from "@/api/operationManagement/CsStationManagement";

export default defineComponent({
  name: 'PvStringConfigCenter',
  components: { HandleMenus, PvStringConfigTable },
  setup () {

    const that = reactive({
      activeSiteId: '',
      handleMenuArray: [],
      treeProps: { value: 'id', label: 'siteName', children: 'children' },
    });

    const handleMenuEvent = (data) => {
      // console.log(data)
      if (data.menuType === "clickTreeNode") that.activeSiteId = data.id;
    };

    // 查询站点下拉列表
    const querySiteBasicInfoByTenantId = () => {
      findSiteInfoByUserId({ scenarioTypes: 1, timer: new Date() }).then(res => {
        let list = res.data ? res.data : [];
        that.handleMenuArray = JSON.parse(JSON.stringify(list));
      });
    };

    onMounted(() => {
      querySiteBasicInfoByTenantId();
    });

    return { ...toRefs(that), querySiteBasicInfoByTenantId, handleMenuEvent };
  }
});
</script>

<style lang="scss" scoped>
.app-container-right {
  padding: 0 10px;
  box-sizing: border-box;
  flex-direction: initial;
  display: flex;
  width: 100%;
  height: 100%;
  .content_left {
    width: 300px;

    :deep(.handleMenu) {
      height: 100%;
      padding-top: 4px;
      box-sizing: border-box;

      .bottomContent {
        padding-top: 0;
      }
    }
  }

  .content_right {
    flex: 1;
    height: 100%;
  }
}
</style>