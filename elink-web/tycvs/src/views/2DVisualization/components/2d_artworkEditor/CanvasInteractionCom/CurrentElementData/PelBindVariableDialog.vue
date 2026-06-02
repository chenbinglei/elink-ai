<template>
  <Dialog v-model:isVisible="dialog_visible" :manualEnterClose="false" :title="titleName" width="640"
    @confirm="clickConfirmBut">
    <template v-slot:content>
      <div v-loading="listLoading" class="dialog-main">
        <Tabs ref="tabsRef" v-model:tabsIndex="formDialog.variableType" :tabsArray="tabsArray"
          @changeEvent="changeEvent" />
        <div class="content_body">
          <template v-if="formDialog.variableType !== 3">
            <HandleMenus ref="handleMenusRef" :defaultIsChecked="defaultIsChecked" :handleMenuArray="handleMenuArray"
              :isShowHeader="false" :expandOnClickNode="false" :id="formDialog.siteOrDeviceId" defaultExpandAll
              :treeProps="treeProps" @handleMenuEvent="handleMenuEvent" />
          </template>
          <div class="content_body_right">
            <PelBindVariableListCom ref="pelBindVariableListComRef" v-model:variableId="formDialog.variableId"
              v-model:selectTypeDataValue="formDialog.selectTypeDataValue"
              v-model:dataType="formDialog.dataType"

              v-model:variableName="formDialog.variableName" :varType="formDialog.varType"
              :siteOrDeviceId="formDialog.siteOrDeviceId" :variableType="formDialog.variableType" />
          </div>
        </div>
      </div>
    </template>
    <template #bottomContent>
      <el-button type="warning" @click="clickResetForm">重置</el-button>
    </template>
  </Dialog>
</template>

<script>
import { useStore } from "vuex";
import { setTreeData } from "@/utils";
import { ElMessage } from "element-plus";
import PelBindVariableListCom from "./PelBindVariableListCom.vue";
import { findSiteDeviceListBySiteId } from "@/api/2DVisualization/2d_artworkEditor";
import { getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent, computed, nextTick } from "vue";

export default defineComponent({
  name: "PelBindVariableDialog",
  components: { PelBindVariableListCom },
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeEditInfo: {
      type: Object,
      default: () => {
        return {}
      }
    },
  },
  emits: ["update:isVisible", "changeEvent"],
  setup (props) {
    const store = useStore();
    const { emit } = getCurrentInstance();

    // 当前图纸数据
    const canvasMeta2dData = computed(() => {
      return store.state.meta2d.canvasMeta2dData;
    });

    const that = reactive({
      isFirst: true,
      formDialog: {},
      oldFormDialog: {},
      listLoading: false,
      titleName: "动态数据设置",
      dialog_visible: props.isVisible,

      variableArray: [],
      handleMenuArray: [],
      defaultIsChecked: true, // 左侧树形数据是否默认获取第一项
      treeProps: { value: 'id', label: 'name', children: 'children' },
      tabsArray: [{ id: 1, name: "计算节点" }, { id: 2, name: "设备功能点" }, { id: 3, name: "自定义变量" }],
      deviceTypeList: [
        { deviceName: "电桩系统", id: "dianZhuangXiTong", fieldName: "pileDeviceList" },
        { deviceName: "储能系统", id: "chuNengXiTong", fieldName: "storageDeviceList" },
        { deviceName: "配电系统", id: "peiDianXiTong", fieldName: "powerDeviceList" },
        { deviceName: "光伏系统", id: "guangFuXiTong", fieldName: "pvDeviceList" },
      ]
    })

    // 确定绑定数据
    const clickConfirmBut = () => {
      let formDialog = JSON.parse(JSON.stringify(that.formDialog));
      console.log("formDialog", formDialog);
      let obj = {
        ...formDialog,
        variableId: formDialog.selectTypeDataValue ? (formDialog.variableId.split('@')[0]) + '@' + formDialog.selectTypeDataValue : formDialog.variableId?.split('@')[0],
      }
      delete obj.selectTypeDataValue;
      emit("changeEvent", obj);
      ElMessage({ type: "success", message: "操作成功", showClose: true });
      that.dialog_visible = false;
    }

    //
    const handleMenuEvent = (data) => {
      // console.log(data);
      if (data.menuType === "clickTreeNode") {
        that.formDialog.siteOrDeviceId = data.id;
        that.formDialog.varType = data.type > 1 ? 2 : 1;
      }
    }

    const changeEvent = (data) => {
      // console.log(data);
      // 变量选择类型发生改变执行
      if (data.operateType === "updateTabs" && !that.isFirst) {
        that.defaultIsChecked = false;
        delete that.formDialog.variableId
        if (data.tabsIndex === 3) delete that.formDialog.siteOrDeviceId
      }

      that.isFirst = false; // 初始化加载完成
    }

    // 根据图模id查询关联变量数据
    const tabsRef = ref(null);
    const pelBindVariableListComRef = ref(null);
    const initParamConfigFun = () => {
      let activeEditInfo = JSON.parse(JSON.stringify(props.activeEditInfo));
      if (!activeEditInfo.variableType && activeEditInfo.variableName) {
        activeEditInfo.variableType = 3;
        activeEditInfo.variableId = activeEditInfo.variableName;
      }

      that.defaultIsChecked = !!activeEditInfo.siteOrDeviceId;
      that.formDialog = JSON.parse(JSON.stringify(activeEditInfo));
      console.log(that.formDialog,'999');
      nextTick(() => {
        that.oldFormDialog = JSON.parse(JSON.stringify(activeEditInfo));
        if (activeEditInfo.siteOrDeviceId) pelBindVariableListComRef.value.queryVarListByDeviceIdFun();
      })
    }


    const clickResetForm = () => {
      that.isFirst = true;
      initParamConfigFun();
      tabsRef.value.selectIndex(that.oldFormDialog.variableType);
    }

    // 查询系统设备列表
    const findSystemDeviceList = () => {
      if (!canvasMeta2dData.value.siteId) return
      findSiteDeviceListBySiteId({ siteId: canvasMeta2dData.value.siteId, timer: new Date() }).then(res => {
        let handleMenuArray = JSON.parse(JSON.stringify(setTreeData(res.data ? res.data : [])));
        let findHandleMenuArray = handleMenuArray.find(item => item.id === canvasMeta2dData.value.siteId);
        if (findHandleMenuArray) that.handleMenuArray = JSON.parse(JSON.stringify([findHandleMenuArray]));
      }).catch((error) => {
        if (error && error.code === 88886) return
        that.handleMenuArray = [{ deviceName: canvasMeta2dData.value.siteName, id: canvasMeta2dData.value.siteId, type: 1 }];
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
      findSystemDeviceList();
    })

    return {
      ...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut, canvasMeta2dData, handleMenuEvent, tabsRef,
      findSystemDeviceList, changeEvent, clickResetForm, pelBindVariableListComRef
    }
  }
})
</script>

<style lang="scss" scoped>
.content_body {
  width: 100%;
  padding-top: 16px;
  box-sizing: border-box;
  display: flex;

  :deep(.handleMenu) {
    max-height: 420px;
    margin-right: 12px;
  }

  .content_body_right {
    flex: 1;
    max-height: 420px;
    overflow-y: auto;
  }
}
</style>