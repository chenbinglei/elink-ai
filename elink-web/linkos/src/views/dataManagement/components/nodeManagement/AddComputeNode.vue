<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName" width="82vw" customClass="marginDialogClass" @confirm="saveDialog">
    <template v-slot:content>
      <div class="content_body">
        <div class="content_body_top flex">
          <div class="content_body_top_left">
            <NodeBasicInfo ref="nodeBasicInfoRef"></NodeBasicInfo>
          </div>
          <div class="content_body_top_right flex-all">
            <NodeVariableInfo ref="nodeVariableInfoRef" :exampleType="exampleType" :recordId="recordId"></NodeVariableInfo>
          </div>
        </div>
        <div class="content_body_bottom flex">
          <div class="content_body_bottom_left flex-all">
            <NodeFormulaData ref="nodeFormulaDataRef"></NodeFormulaData>
          </div>
          <div class="content_body_bottom_right">
            <NodeFunctionData ref="nodeFunctionDataRef"></NodeFunctionData>
          </div>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {getNowDateAll} from "@/utils/dateTime";
import NodeBasicInfo from "./AddComputeNode/NodeBasicInfo";
import NodeFormulaData from "./AddComputeNode/NodeFormulaData";
import NodeFunctionData from "./AddComputeNode/NodeFunctionData";
import NodeVariableInfo from "./AddComputeNode/NodeVariableInfo";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";
import {findComputeNodeInfoById, saveOrUpdateComputeNodeInfo} from "@/api/dataManagement/nodeManagement";

export default defineComponent({
  name: "AddComputeNode",
  components:{NodeBasicInfo,NodeVariableInfo,NodeFormulaData,NodeFunctionData},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "说明"
    },
    recordId: {
      type: [String,Number],
      default: ""
    },
    //实例类型 1-设备类型 2-站点类型
    exampleType: {
      type: [String,Number],
      default: ""
    },
    // 节点id
    computeNodeId: {
      type: [String,Number],
      default: ""
    },
    // 站点id
    activeSiteId: {
      type: [String,Number],
      default: ""
    },
  },
  setup(props) {

    const { emit } = getCurrentInstance();

    const that = reactive({
      listLoading: false,
      htmlReg: /<[^>]+>/gim,
      dialog_visible: props.isVisible,
    });

    const nodeBasicInfoRef = ref(null);
    const nodeFormulaDataRef = ref(null);
    const nodeVariableInfoRef = ref(null);

    const saveDialog = () => {
      nodeBasicInfoRef.value.formDialogRef.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(nodeBasicInfoRef.value.formdialog)); // 基础参数
          formDialog.countPeriod = formDialog.countPeriod + formDialog.countPeriodType; // 统计周期
          formDialog.computePeriod = formDialog.computePeriod + formDialog.computePeriodType; // 计算周期
          formDialog.startTime = getNowDateAll(formDialog.startTime,{ isSs: false }); // 起始时间加秒
          // console.log(formDialog);
          let nodeParamInfos = nodeVariableInfoRef.value.returnVarTableListFun(); // 绑定数据
          if(!nodeParamInfos || !nodeParamInfos.length) nodeParamInfos = [];
          // if(!nodeParamInfos || !nodeParamInfos.length){
          //   that.listLoading = false;
          //   ElMessage({ type: "error", showClose: true, message: "请先添加变量表格数据！" });
          //   return
          // }

          let formulaFront = nodeFormulaDataRef.value.formula; // 计算公式-前端用
          // console.log(formulaFront);

          const dom = document.createElement('div');
          dom.innerHTML = formulaFront;

          const varDomArray = dom.querySelectorAll(".variable_class");
          for(let i = 0;i < varDomArray.length;i++){
            let innerHTML = varDomArray[i].innerHTML;
            let findItem = nodeParamInfos.find(item => item.paramName === innerHTML);
            if(findItem) varDomArray[i].innerHTML = findItem.sourceCode;
          }

          formDialog.siteId = props.activeSiteId;
          formDialog.formulaFront = formulaFront;
          formDialog.exampleType = props.exampleType;
          formDialog.formulaAfter = dom.innerHTML.replace(that.htmlReg, ""); // 把所有的标签都替换成空字符 组成公式
          saveOrUpdateComputeNodeInfo({ deviceId: props.recordId, ...formDialog,nodeParamInfos: nodeParamInfos }).then(()=>{
            emit("changEvent");
            that.listLoading = false;
            that.dialog_visible = false;
            ElMessage({type: "success", showClose: true, message: props.computeNodeId ? "修改成功！" : "新增成功！"});
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    // 根据id查询计算节点详情
    const queryComputeNodeInfoById = ()=>{
      that.listLoading = true;
      findComputeNodeInfoById({ id: props.computeNodeId }).then(res=>{
        let formdialog = JSON.parse(JSON.stringify(res.data ? res.data : {}));
        if(formdialog.nodeParamInfoList && formdialog.nodeParamInfoList.length){
          for(let i = 0;i < formdialog.nodeParamInfoList.length;i++){
            if(formdialog.nodeParamInfoList[i].sourceId){
              formdialog.nodeParamInfoList[i].sourceId = JSON.parse(formdialog.nodeParamInfoList[i].sourceId);
            }
          }
        }
        // console.log(formdialog.nodeParamInfoList);
        nodeVariableInfoRef.value.varTableList = formdialog.nodeParamInfoList;
        nodeFormulaDataRef.value.formula = formdialog.formulaFront;
        nodeBasicInfoRef.value.dataFeedbackFun(formdialog);
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      if(props.computeNodeId)queryComputeNodeInfoById();
    })

    return {...toRefs(that), saveDialog, watchVisible, watchDialogVisible, nodeBasicInfoRef,nodeVariableInfoRef,nodeFormulaDataRef,queryComputeNodeInfoById}

  }
})
</script>

<style scoped lang="scss">
.content_body{
  height: 70vh;
  display: flex;
  flex-direction: column;

  .content_body_top{
    margin-bottom: 12px;

    .content_body_top_right{
      margin-left: 12px;
    }
  }

  .content_body_bottom{
    flex: 1;
    height: 2px;

    .content_body_bottom_right{
      width: 38%;
      margin-left: 12px;
    }
  }
}
</style>
