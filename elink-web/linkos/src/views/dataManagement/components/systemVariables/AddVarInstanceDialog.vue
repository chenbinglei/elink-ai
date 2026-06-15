<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName" width="48vw"
          :manualCancelClose="false" :cancelVisible="cancelVisible" :cancelText="cancelText" :confirmText="confirmText" @cancel="clickCancelFun" @confirm="clickConfirmFun">
    <template v-slot:content>
      <div class="content_body">
        <div class="content_top">
          <CustomSteps :stepsArray="stepsArray" :active="activeIndex"></CustomSteps>
        </div>
        <div class="content_bottom">
          <template v-if="activeIndex === 1">
            <SelectSiteAndDevice ref="selectSiteAndDeviceRef" :formDialog="formDialog" :activeVarInfo="activeVarInfo" @changEvent="changEvent"></SelectSiteAndDevice>
          </template>
          <template v-if="activeIndex === 2">
            <!--            关联节点数据-->
            <AssociateNodeDataSources v-if="activeVarInfo.dataSource === 1" :formDialog="formDialog" @changEvent="changEvent"></AssociateNodeDataSources>
            <!--            关联功能点数据-->
            <AssociateFunctionDataSources v-if="activeVarInfo.dataSource === 2" :formDialog="formDialog" @changEvent="changEvent"></AssociateFunctionDataSources>
          </template>
          <div class="successClass" v-if="activeIndex === 3">
            <span class="iconfont icon-chenggongtongzhi"></span>
            <div class="text">操作成功</div>
          </div>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import SelectSiteAndDevice from "./SelectSiteAndDevice";
import CustomSteps from "@/components/component/CustomSteps";
import AssociateNodeDataSources from "./AssociateNodeDataSources";
import {addVariableNode} from "@/api/dataManagement/systemVariables";
import AssociateFunctionDataSources from "./AssociateFunctionDataSources.vue";
import {getCurrentInstance, reactive, toRefs, watch, ref, onMounted, defineComponent} from "vue";

export default defineComponent({
  name: "AddVarInstanceDialog",
  components:{ CustomSteps,SelectSiteAndDevice,AssociateNodeDataSources,AssociateFunctionDataSources },
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加实例"
    },
    activeVarInfo:{
      type: Object,
      default:()=>{
        return { }
      }
    },
  },
  setup(props){

    const { emit } = getCurrentInstance();
    const formDialogRef = ref(null);
    const selectSiteAndDeviceRef = ref(null);

    const that = reactive({
      formDialog: {},
      oldFormDialog: {},
      listLoading: false,
      dialog_visible: props.isVisible,

      activeIndex: 1,
      cancelText: "取消",
      confirmText: "下一步",
      cancelVisible: true, // 取消按钮是否显示
      stepsArray:[{ id:1,title:"选择站点/设备",iconTitle: 1 },{ id:2,title:"关联数据源",iconTitle:2 },{ id:3,title:"完成",iconTitle:3 }],
    });

    const clickConfirmFun = ()=>{
      if(that.activeIndex === 1){
        selectSiteAndDeviceRef.value.formDialogRef.validate((valid) => {
          if (valid) {
            that.formDialog = { ...that.oldFormDialog, ...selectSiteAndDeviceRef.value.formDialog };
            that.confirmText = "下一步";
            that.cancelText = "上一步";
            that.activeIndex+=1;
          }
        });
        return
      }

      if(that.activeIndex === 2){
        let formDialog = JSON.parse(JSON.stringify(that.formDialog));

        if(formDialog.dataSource === 1 && !formDialog.nodeId){
          ElMessage({ type: "error", message: "请选择关联节点数据", showClose: true });
          return
        }

        if(formDialog.dataSource === 2 && !formDialog.functionId){
          ElMessage({ type: "error", message: "请选择关联功能点数据", showClose: true });
          return
        }

        // 添加实例
        that.listLoading = true;
        addVariableNode({ ...formDialog }).then(()=>{
          that.activeIndex+=1;
          that.listLoading = false;
          that.confirmText = "完成";
          that.cancelVisible = false;
        }).catch(()=>{
          that.listLoading = false;
        })

        return;
      }

      // 完成
      if(that.activeIndex === 3){
        that.dialog_visible = false;
        emit("changEvent", { type: "listArray" });
      }
    }

    const clickCancelFun = ()=>{

      if(that.activeIndex <= 1){
        that.dialog_visible = false;
        return
      }

      if(that.activeIndex === 2){
        that.activeIndex-=1;
        that.cancelText = "取消";
        that.confirmText = "下一步";
        return
      }

      if(that.activeIndex === 3){
        that.dialog_visible = false;
        emit("changEvent", { type: "listArray" });
      }
    }

    const changEvent = (data)=>{

      if(data.type === "selNodeId"){
        that.formDialog.nodeId = data.nodeId;
        that.formDialog.storageId = data.storageId;
      }

      if(data.type === "selFunctionId"){
        that.formDialog.functionId = data.functionId;
      }
    }

    const initParamConfigFun = ()=>{
      that.formDialog = Object.assign({},props.activeVarInfo,that.formDialog);
      that.oldFormDialog = JSON.parse(JSON.stringify(that.formDialog));
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      initParamConfigFun();
    })

    return { ...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, selectSiteAndDeviceRef, changEvent, clickCancelFun, clickConfirmFun,
      initParamConfigFun }
  }
})
</script>

<style scoped lang="scss">
.content_body{

  .content_top{
    padding: 0 12px;
    box-sizing: border-box;
  }

  .content_bottom{
    height: 50vh;
    margin-top: 12px;

    .successClass{
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;

      .iconfont{
        font-size: 64px;
        color: #00D15B;
      }

      .text{
        color: #242424;
        font-size: 16px;
        margin-top: 12px;
      }
    }
  }
}
</style>
