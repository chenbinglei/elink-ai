<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName" width="52vw" @confirm="saveDialog">
    <template v-slot:content>
      <div class="content_body" v-loading="listLoading">
        <div class="content_body_left content_body_list flex-all">
          <div class="content_top">
            <div class="content_top_left">
              <el-select v-model="formInline.functionType" clearable placeholder="请选择" @change="listArray">
                <el-option v-for="item in functionTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </div>
            <div class="content_top_right">
              <el-input v-model="formInline.keyword" placeholder="请输入功能名称" @input="listArray"></el-input>
            </div>
          </div>
          <div class="content_bottom scrollbarStyle">
            <template v-if="unCheckList && unCheckList.length">
              <template v-for="(item,index) in unCheckList" :key="index">
                <standard-features-card :operateType="1" :cardInfo="item" @changeEvent="changeEvent"></standard-features-card>
              </template>
            </template>
            <template v-else><null-data></null-data></template>
          </div>
        </div>
        <div class="content_body_right content_body_list flex-all">
          <div class="content_top">已选择功能（{{ checkList && checkList.length }}个）</div>
          <div class="content_bottom scrollbarStyle">
            <template v-if="checkList && checkList.length">
              <template v-for="(item,index) in checkList" :key="index">
                <standard-features-card :operateType="2" :cardInfo="item" @changeEvent="changeEvent"></standard-features-card>
              </template>
            </template>
            <template v-else><null-data></null-data></template>
          </div>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {Search} from "@element-plus/icons-vue";
import StandardFeaturesCard from "./StandardFeaturesCard";
import {getCurrentInstance, onMounted, reactive, toRefs, watch, defineComponent} from "vue";
import {findModelBindFunctionByModelId, modelBindFunctionData} from "@/api/modelCenter/modelManagement";

export default defineComponent({
  name: "AddStandardFeatures",
  components:{StandardFeaturesCard},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeModelId: {
      type: [String, Number],
      default: ''
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      Search,
      formInline: {},
      listLoading: false,
      titleName: "添加标准功能",
      dialog_visible: props.isVisible,

      checkList: [],
      unCheckList: [],
      allFunctionArray: [],
      functionTypeArray: [{id: 1, name: "遥测"}, {id: 2, name: "遥信"}, {id: 3, name: "遥脉"}, {id: 4, name: "遥控"}, {id: 5, name: "遥调"}]
    })

    const queryModelBindFunctionByModelId = ()=>{
      that.listLoading = true;
      findModelBindFunctionByModelId({ modelId: props.activeModelId }).then(res=>{
        that.checkList = res.data.checkList ? res.data.checkList : [];
        that.unCheckList = res.data.uncheckList ? res.data.uncheckList : [];
        that.allFunctionArray = [...that.checkList,...that.unCheckList];
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    const listArray = ()=>{
      // 先提取已选择的功能点id
      let checkListIds = [],unCheckList = [];
      for(let i = 0;i < that.checkList.length;i++)checkListIds.push(that.checkList[i].id);
      for(let i = 0;i < that.allFunctionArray.length;i++){
        let findIndex = checkListIds.findIndex(item => item === that.allFunctionArray[i].id);
        if(findIndex === -1)unCheckList.push(that.allFunctionArray[i]);
      }

      // 对剩下未选择的进行过滤
      for(let i = 0;i < unCheckList.length;i++){
        if((that.formInline.keyword && unCheckList[i].functionName.indexOf(that.formInline.keyword) === -1) ||
            (that.formInline.functionType && unCheckList[i].functionType !== that.formInline.functionType)){
          unCheckList.splice(i,1);
          i--
        }
      }
      that.unCheckList = JSON.parse(JSON.stringify(unCheckList));
    }

    const changeEvent = (data)=>{
      if(data.type === "standardFeaturesCard"){
        if(data.operateType === 1){
          let findIndex = that.unCheckList.findIndex(item=> item.id === data.id);
          if(findIndex > -1){
            that.checkList.push(that.unCheckList[findIndex]);
            that.unCheckList.splice(findIndex,1);
          }
        }

        if(data.operateType === 2){
          let findIndex = that.checkList.findIndex(item=> item.id === data.id);
          if(findIndex > -1){
            that.unCheckList.push(that.checkList[findIndex]);
            that.checkList.splice(findIndex,1);
          }
        }

        listArray();
      }
    }

    const saveDialog = ()=>{
      let functionMap = {};
      that.listLoading = true;

      if(that.checkList && that.checkList.length){
        for(let i = 0;i < that.checkList.length;i++) functionMap[that.checkList[i].id] = i;
      }

      modelBindFunctionData({ modelId: props.activeModelId,type: 1,functionMap: functionMap }).then(res=>{
        emit("changeEvent");
        that.dialog_visible = false;
        ElMessage({ type: "success", showClose: true, message: "操作成功！" });
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
      queryModelBindFunctionByModelId();
    })

    return { ...toRefs(that),watchVisible,watchDialogVisible,queryModelBindFunctionByModelId,changeEvent,saveDialog,listArray }
  }
})
</script>

<style scoped lang="scss">
.content_body{
  height: 50vh;
  display: flex;
  align-items: center;

  .content_body_list{
    height: 100%;
    border-radius: 4px;
    border: 1px solid #DBDBDD;
    display: flex;
    flex-direction: column;

    .content_top{
      height: 48px;
      padding: 0 16px;
      display: flex;
      align-items: center;
      border-bottom: 1px solid #E3E3E3;
      box-sizing: border-box;
    }

    .content_bottom{
      flex: 1;
      overflow-y: auto;
      padding: 12px 16px;
      box-sizing: border-box;
    }
  }

  .content_body_left{
    margin-right: 12px;
    .content_top_left{
      width: 140px;
      margin-right: 12px;
    }
  }
}
</style>
