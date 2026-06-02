<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName"
          width="52vw" @confirm="saveDialog">
    <template v-slot:content>
      <div class="content_body">
        <div class="content_body_left content_body_list flex-all">
          <div class="content_top">
            <div class="content_top_left">
              <el-select v-model="formInline.reaType" clearable placeholder="请选择" @change="listArray">
                <el-option v-for="item in reaTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </div>
            <div class="content_top_right">
              <el-input v-model="formInline.keyword" placeholder="请输入扩展属性名称" @input="listArray"></el-input>
            </div>
          </div>
          <div class="content_bottom scrollbarStyle">
            <template v-if="unCheckList && unCheckList.length">
              <template v-for="(item,index) in unCheckList" :key="index">
                <extended-attr-card :operateType="1" :cardInfo="item" @changeEvent="changeEvent"></extended-attr-card>
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
                <extended-attr-card :operateType="2" :cardInfo="item" @changeEvent="changeEvent"></extended-attr-card>
              </template>
            </template>
            <template v-else><null-data></null-data></template>
          </div>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {Search} from "@element-plus/icons-vue";
import ExtendedAttrCard from "./ExtendedAttrCard";
import {getCurrentInstance, onMounted, reactive, toRefs, watch} from "vue";
import {findModelBindReaByModelId, modelBindReaData} from "@/api/modelCenter/modelManagement";

export default {
  name: "AddExtendedAttr",
  components:{ExtendedAttrCard},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加标准功能"
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
      dialog_visible: props.isVisible,

      checkList: [],
      unCheckList: [],
      allExtendedAttrArray: [],
      reaTypeArray: [{id: 1, name: "数值"},{id: 2, name: "文字"},{id: 3, name: "选项"},{id: 4, name: "位置"},{id: 5, name: "开关"},{id: 6, name: "时间"}]
    })

    const queryModelBindFunctionByModelId = ()=>{
      findModelBindReaByModelId({ modelId: props.activeModelId }).then(res=>{
        that.checkList = res.data.checkList ? res.data.checkList : [];
        that.unCheckList = res.data.uncheckList ? res.data.uncheckList : [];
        that.allExtendedAttrArray = [...that.checkList,...that.unCheckList];
      })
    }

    const listArray = ()=>{
      // 先提取已选择的功能点id
      let checkListIds = [],unCheckList = [];
      for(let i = 0;i < that.checkList.length;i++)checkListIds.push(that.checkList[i].id);
      for(let i = 0;i < that.allExtendedAttrArray.length;i++){
        let findIndex = checkListIds.findIndex(item => item === that.allExtendedAttrArray[i].id);
        if(findIndex === -1)unCheckList.push(that.allExtendedAttrArray[i]);
      }

      // 对剩下未选择的进行过滤
      for(let i = 0;i < unCheckList.length;i++){
        if((that.formInline.keyword && unCheckList[i].reaName.indexOf(that.formInline.keyword) === -1) ||
            (that.formInline.reaType && unCheckList[i].reaType !== that.formInline.reaType)){
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
      let reaIds = [];
      that.listLoading = true;

      if(that.checkList && that.checkList.length){
        for(let i = 0;i < that.checkList.length;i++){
          reaIds.push(that.checkList[i].id);
        }
      }

      modelBindReaData({ modelId: props.activeModelId,type: 1,reaIds: reaIds }).then(res=>{
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
}
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
