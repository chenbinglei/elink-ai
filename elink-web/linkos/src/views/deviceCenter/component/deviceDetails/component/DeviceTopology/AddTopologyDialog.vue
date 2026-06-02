<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName" width="52vw" @confirm="saveDialog">
    <template v-slot:content>
      <div class="content_body" v-loading="listLoading">
        <div class="content_body_left content_body_list flex-all">
          <div class="content_top">
            <div class="content_top_right">
              <el-input v-model="formInline.keyword" placeholder="请输入设备名称" @input="listArray"></el-input>
            </div>
          </div>
          <div class="content_bottom scrollbarStyle">
            <template v-if="unCheckList && unCheckList.length">
              <template v-for="(item,index) in unCheckList" :key="index">
                <device-topology-card :operateType="1" :cardInfo="item" :activeNodeId="activeNodeId" @changeEvent="changeEvent"></device-topology-card>
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
                <device-topology-card :operateType="2" :cardInfo="item" :activeNodeId="activeNodeId" @changeEvent="changeEvent"></device-topology-card>
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
import {useRoute} from "vue-router"
import {ElMessage} from "element-plus";
import {Search} from "@element-plus/icons-vue";
import DeviceTopologyCard from "./DeviceTopologyCard";
import {getCurrentInstance, onMounted, reactive, toRefs, watch} from "vue";
import {batchBindDeviceTopology, findDeviceNodeUpdateList} from "@/api/deviceCenter/deviceList";

export default {
  name: "AddTopologyDialog",
  components:{DeviceTopologyCard},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeNodeId: {
      type: [String,Number],
      default: ""
    },
  },
  setup(props) {

    const route = useRoute();
    const {emit} = getCurrentInstance();

    const that = reactive({
      Search,
      formInline: {},
      listLoading: false,
      titleName: "添加设备",
      dialog_visible: props.isVisible,
      activeDeviceId: route.query.id, // 当前设备模型id

      checkList: [],
      unCheckList: [],
      allFunctionArray: [],
    })

    const saveDialog = () => {
      let functionIds = [];
      that.listLoading = true;

      if(that.checkList && that.checkList.length){
        for(let i = 0;i < that.checkList.length;i++){
          functionIds.push(that.checkList[i].deviceId + ',' +that.checkList[i].nodeId);
        }
      }

      batchBindDeviceTopology({ deviceId: that.activeDeviceId,nodeId: props.activeNodeId,bindData: functionIds }).then(()=>{
        emit("changeEvent");
        that.dialog_visible = false;
        ElMessage({ type: "success", showClose: true, message: "操作成功！" });
      }).catch((e)=>{
        that.listLoading = false;
      })
    }

    // 查询设备拓扑图编辑列表
    const queryDeviceNodeUpdateList = ()=>{
      that.listLoading = true;
      findDeviceNodeUpdateList({ deviceId: that.activeDeviceId,nodeId: props.activeNodeId }).then(res=>{
        that.checkList = res.data.checkList ? res.data.checkList : [];
        that.unCheckList = res.data.uncheckList ? res.data.uncheckList : [];
        that.allFunctionArray = [...that.checkList,...that.unCheckList];
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    const changeEvent = (data)=>{
      if(data.type === "deviceTopologyCard"){
        if(data.operateType === 1){
          let findIndex = that.unCheckList.findIndex(item=> item.nodeId === data.nodeId);
          if(findIndex > -1){
            that.checkList.push(that.unCheckList[findIndex]);
            that.unCheckList.splice(findIndex,1);
          }
        }

        if(data.operateType === 2){
          let findIndex = that.checkList.findIndex(item=> item.nodeId === data.nodeId);
          if(findIndex > -1){
            that.unCheckList.push(that.checkList[findIndex]);
            that.checkList.splice(findIndex,1);
          }
        }

        listArray();
      }
    }

    const listArray = ()=>{

    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      queryDeviceNodeUpdateList();
    })

    return { ...toRefs(that),watchVisible,watchDialogVisible, saveDialog, queryDeviceNodeUpdateList, changeEvent, listArray}
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

