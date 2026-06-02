<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="640" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="120px">
          <el-form-item label="父级节点：">
            <el-input v-model="formDialog.parentName" disabled />
          </el-form-item>
          <el-form-item label="节点类型：">
            <TabBackground :tabsArray="nodeTypeArray" v-model:tabs-index="formDialog.nodeType"></TabBackground>
          </el-form-item>
          <el-form-item label="名称：" prop="nodeName">
            <el-input v-model="formDialog.nodeName" maxlength="32" placeholder="请输入名称" show-word-limit/>
          </el-form-item>

          <template v-if="formDialog.nodeType === 0">
            <el-form-item label="类型：" prop="deviceType" >
              <el-select v-model="formDialog.deviceType" placeholder="请选择类型" @change="changeDeviceTypeFun">
<!--                <el-option v-for="item in typeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>-->
                <template v-for="item in typeArray" :key="item.id">
                  <template v-if="!activeParentNodeInfo.activeDeviceType || item.id === 4 || (activeParentNodeInfo.activeDeviceType && activeParentNodeInfo.activeDeviceType === item.id)">
                    <el-option :label="item.name" :value="item.id"></el-option>
                  </template>
                </template>
              </el-select>
            </el-form-item>
            <el-form-item label="计量设备：" prop="deviceIds">
              <el-select v-model="formDialog.deviceIds" placeholder="请选择关联设备" filterable :disabled="!formDialog.deviceType">
                <el-option v-for="item in deviceIdsArray" :key="item.id" :label="item.deviceName" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </template>
          <template v-if="formDialog.nodeType === 1">
            <el-form-item label="设备类型：" prop="deviceType">
              <el-select v-model="formDialog.deviceType" placeholder="请选择设备类型" @change="changeDeviceTypeFun">
<!--                <el-option v-for="item in deviceTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>-->
                <template v-for="item in deviceTypeArray" :key="item.id">
                  <template v-if="!activeParentNodeInfo.activeDeviceType || item.id === 4 || (activeParentNodeInfo.activeDeviceType && activeParentNodeInfo.activeDeviceType === item.id)">
                    <el-option :label="item.name" :value="item.id"></el-option>
                  </template>
                </template>
              </el-select>
            </el-form-item>
            <el-form-item label="关联设备：" prop="deviceIds">
              <el-select v-model="formDialog.deviceIds" placeholder="请选择关联设备" filterable multiple :max-collapse-tags="1" :disabled="!formDialog.deviceType">
                <el-option v-for="item in deviceIdsArray" :key="item.id" :label="item.deviceName" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </template>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {commonCharName} from "@/utils/validate";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";
import {findDeviceListBySiteId, saveOrUpdateTopoNodeInfo} from "@/api/siteCenter/stationDetails";

export default defineComponent({
  name: "AddTopologyNodeDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加节点"
    },
    siteRecordsId: {
      type: [String, Number],
      default: ""
    },
    // 当前选择的父级节点信息
    activeParentNodeInfo: {
      type: Object,
      default: ()=>{
        return {}
      }
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateNodeName = (rule, value, callback) => {
      if (!value || !commonCharName(value)) {
        callback(new Error("请输入正确的名称"));
      } else {
        callback();
      }
    };

    const validateDeviceType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择类型"));
      } else {
        callback();
      }
    };

    const validateDeviceIds = (rule, value, callback) => {
      if (!value || (that.formDialog.nodeType === 1 && !value.length)) {
        callback(new Error("请选择关联设备"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      deviceIdsArray: [],
      dialog_visible: props.isVisible,
      formDialog: { nodeType: 0 },
      nodeTypeArray: [{id: 0,name: '计量节点'},{id: 1,name: '设备节点'}],
      deviceTypeArray: [{id: 1,name: '光伏'},{id: 2,name: '储能'},{id: 3,name: '电桩'},{id: 4,name: '其他'}],
      typeArray: [{id: 1,name: '光伏并网点'},{id: 2,name: '储能并网点'},{id: 3,name: '电桩并网点'},{id: 4,name: '其他'}],

      rules: {
        nodeName: [{required: true, trigger: "change", validator: validateNodeName }],
        deviceType: [{required: true, trigger: "change", validator: validateDeviceType }],
        deviceIds: [{required: true, trigger: "change", validator: validateDeviceIds }],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          if(formDialog.nodeType === 0) formDialog.deviceIds = [formDialog.deviceIds];
          formDialog.siteId = props.siteRecordsId;
          saveOrUpdateTopoNodeInfo(formDialog).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const changeDeviceTypeFun = ()=>{
      that.deviceIdsArray = [];
      delete that.formDialog.deviceIds;
      queryDeviceListBySiteId();
    }

    // 根据站点id查询设备列表
    const queryDeviceListBySiteId = ()=>{
      findDeviceListBySiteId({siteId:props.siteRecordsId,deviceType: that.formDialog.deviceType}).then(res=>{
        let deviceIdsArray = [];
        let returnDataList = res.data ?? [];
        for(let i = 0;i < returnDataList.length;i++){
          if(that.formDialog.nodeType === 0){
            if(returnDataList[i].typeId === "38"){
              deviceIdsArray.push(returnDataList[i]);
            }
          }

          if(that.formDialog.nodeType === 1){
            if(returnDataList[i].typeId !== "38"){
              deviceIdsArray.push(returnDataList[i]);
            }
          }
        }
        that.deviceIdsArray = JSON.parse(JSON.stringify(deviceIdsArray));
      })
    }

    const initParamConfigFun = ()=>{
      if(props.activeParentNodeInfo.id){
        that.formDialog = JSON.parse(JSON.stringify(props.activeParentNodeInfo));
        queryDeviceListBySiteId(); // 如果为编辑需要调用接口
        return
      }

      that.formDialog = Object.assign({},that.formDialog,props.activeParentNodeInfo);
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, changeDeviceTypeFun, initParamConfigFun}
  }
})
</script>

<style lang="scss" scoped>

</style>