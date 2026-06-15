<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="980" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="120px">
          <LxyCollapse title="基本信息">
            <template #content>
              <el-row :gutter="12">
                <el-col :span="12">
                  <el-form-item label="任务名称：" prop="taskName">
                    <el-input v-model="formDialog.taskName" placeholder="请输入任务名称"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="任务描述：" >
                    <el-input type="textarea" v-model="formDialog.taskDesc" placeholder="请输入任务描述" :rows="2" maxlength="120" show-word-limit></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
            </template>
          </LxyCollapse>
          <LxyCollapse title="选择设备">
            <template #headerRight>
              <div class="alter_text">设备列表不包含：离线和升级中的设备。</div>
            </template>
            <template #content>
              <el-row :gutter="12">
                <el-col :span="12">
                  <el-form-item label="设备类型：" prop="typeId">
                    <el-tree-select v-model="formDialog.typeId" :data="deviceAssetTypeList" :indent="0" :props="treeProps" :render-after-expand="false"
                                    placeholder="请选择设备类型" class="leftArrowClass" default-expand-all filterable @change="changeTypeIdFun" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="设备型号：" prop="equipmentModel">
                    <el-select v-model="formDialog.equipmentModel" filterable placeholder="请选择设备型号">
                      <el-option v-for="item in equipmentModelArray" :key="item" :label="item" :value="item"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <div style="height: 16px"></div>
                  <el-form-item label="固件类型：" prop="firmwareType">
                    <el-select v-model="formDialog.firmwareType" filterable placeholder="请选择固件类型" @change="changeFirmwareTypeFun">
                      <template v-for="item in firmwareTypeArray" :key="item.firmwareType">
                        <el-option :label="item.firmwareTypeName" :value="item.firmwareType"></el-option>
                      </template>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <AllowDeviceUpgradeListCom ref="allowDeviceUpgradeListComRef" :typeId="formDialog.typeId" :equipmentModel="formDialog.equipmentModel"
                                         :firmwareType="formDialog.firmwareType" v-model:deviceCheckList="formDialog.deviceCheckList" />
            </template>
          </LxyCollapse>
          <LxyCollapse title="目标版本">
            <template #content>
              <el-row :gutter="12">
                <el-col :span="12">
                  <el-form-item label="目标版本：" prop="firmwareId">
                    <el-select v-model="formDialog.firmwareId" filterable placeholder="请选择目标版本" @change="changeFirmwareIdFun">
                      <el-option v-for="(item,index) in firmwareIdArray" :key="index" :label="item.firmwareVersion" :value="item.id"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-popover placement="right" :width="280">
                    <template #reference>
                      <div class="flex ai-center" style="width: fit-content;height: 100%">
                        <span class="iconfont icon-tishi"></span>
                      </div>
                    </template>
                    <template #default>
                      <div class="content_body">
                        <div class="content_body_list">
                          <div class="content_list_left">固件名称：</div>
                          <div class="content_list_right">{{ $filters.moreData(firmware_info_obj.firmwareName) }}</div>
                        </div>
                        <div class="content_body_list">
                          <div class="content_list_left">固件类型：</div>
                          <div class="content_list_right">
                            <template v-if="firmware_info_obj.typeId >= 28 && firmware_info_obj.typeId <= 30">{{ $filters.pileFirmwareType(firmware_info_obj.firmwareType) }}</template>
                            <template v-else>{{ $filters.moreData(firmware_info_obj.firmwareType) }}</template>
                          </div>
                        </div>
                        <div class="content_body_list">
                          <div class="content_list_left">文件大小：</div>
                          <div class="content_list_right">{{ $filters.formatBytes(firmware_info_obj.firmwareSize) }}</div>
                        </div>
                        <div class="content_body_list">
                          <div class="content_list_left">发布时间：</div>
                          <div class="content_list_right">{{ $filters.moreData(firmware_info_obj.createTime) }}</div>
                        </div>
                      </div>
                    </template>
                  </el-popover>
                </el-col>
              </el-row>
            </template>
          </LxyCollapse>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {setTreeData} from "@/utils";
import {ElMessage} from "element-plus";
import $filters from "@/common/filters";
import LxyCollapse from "@/components/Tabs/LxyCollapse.vue";
import {getAssetTypeList} from "@/api/modelCenter/modelManagement";
import AllowDeviceUpgradeListCom from "./AllowDeviceUpgradeListCom.vue";
import {getEquipmentModelList} from "@/api/deviceCenter/firmwareManagement";
import {createDeviceTask, getFirmwareListByTypeId} from "@/api/deviceCenter/deviceUpgrade";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "CreateUpgradeTaskDialog",
  components:{LxyCollapse,AllowDeviceUpgradeListCom},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    }
  },
  setup(props){
    const {emit} = getCurrentInstance();

    const validateTaskName = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入任务名称"));
      } else {
        callback();
      }
    };
    const validateTargetVersion = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择目标版本"));
      } else {
        callback();
      }
    };

    const validateTypeId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择设备分类"));
      } else {
        callback();
      }
    };

    const validateEquipmentModel = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择设备型号"));
      } else {
        callback();
      }
    };

    const validateFirmwareType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择固件类型"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      listLoading: false,
      firmwareTypeArray: [],
      titleName: "创建升级任务",
      firmwareIdArray: [],
      firmwareListArray: [],  // 当前设备类型的所有固件包列表
      deviceAssetTypeList: [],
      equipmentModelArray: [],

      firmware_info_obj: {}, // 固件信息详情
      dialog_visible: props.isVisible,
      treeProps: {value: 'id', label: 'typeName', children: 'children'},
      rules:{
        typeId: [{required: true, trigger: "change", validator: validateTypeId }],
        taskName: [{required: true, trigger: "change", validator: validateTaskName }],
        firmwareId: [{required: true, trigger: "change", validator: validateTargetVersion }],
        firmwareType: [{required: true, trigger: "change", validator: validateFirmwareType }],
        equipmentModel: [{required: true, trigger: "change", validator: validateEquipmentModel }],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          // console.log(formDialog);

          if(!formDialog.deviceCheckList || !formDialog.deviceCheckList.length){
            ElMessage({ type: "error", showClose: true, message: "请选择需要升级的设备列表！" });
            return
          }

          let deviceIds = [];
          that.listLoading = true;
          for(let i = 0;i < formDialog.deviceCheckList.length;i++){
            deviceIds.push(formDialog.deviceCheckList[i].id);
          }
          delete formDialog.deviceCheckList;
          console.log(formDialog,deviceIds);

          createDeviceTask({ ...formDialog, deviceIds: deviceIds }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })

        }
      })
    }

    // 获取资产分类列表
    const queryAssetTypeList = ()=>{
      getAssetTypeList({pageName:"AddFirmwareDialog",timer: new Date()}).then(res=>{
        let returnDataList = setTreeData(res.data ?? []);
        let findDataItem= returnDataList.find(item => item.id === "3");
        that.deviceAssetTypeList = findDataItem?.children;
      })
    }

    // 设备类型 发生改变执行
    const changeTypeIdFun = ()=>{
      that.firmwareIdArray = [];
      that.firmware_info_obj = {};
      delete that.formDialog.firmwareId;
      delete that.formDialog.firmwareType;
      delete that.formDialog.equipmentModel;
      delete that.formDialog.deviceCheckList;
      queryEquipmentModelList();  // 获取设备型号列表
      queryFirmwareListByTypeId(); // 根据设备类型id查询固件包数据
    }

    // 固件类型  发生改变执行
    const changeFirmwareTypeFun = ()=>{
      let firmwareIdArray = [];
      for(let i = 0;i < that.firmwareListArray.length;i++){
        if(that.firmwareListArray[i].firmwareType === that.formDialog.firmwareType){
          firmwareIdArray.push(that.firmwareListArray[i]);
        }
      }

      // 目标版本 列表数据
      that.firmware_info_obj = {};
      delete that.formDialog.firmwareId;
      that.firmwareIdArray = JSON.parse(JSON.stringify(firmwareIdArray));
    }

    // 目标版本  发生改变执行
    const changeFirmwareIdFun = ()=>{
      let findItem = that.firmwareIdArray.find(item => item.id === that.formDialog.firmwareId);
      if(findItem) that.firmware_info_obj = JSON.parse(JSON.stringify(findItem));
    }

    // 获取设备型号列表
    const queryEquipmentModelList = ()=>{
      getEquipmentModelList({typeId: that.formDialog.typeId,timer: new Date()}).then(res=>{
        that.equipmentModelArray = res.data;
      }).catch(()=>{
        that.equipmentModelArray = [];
      })
    }

    // 根据设备类型id查询固件包数据
    const queryFirmwareListByTypeId = ()=>{
      getFirmwareListByTypeId({typeId: that.formDialog.typeId,timer: new Date()}).then(res=>{
        let firmwareTypeArray = [];
        let returnDataList = res.data ?? [];
        if(returnDataList && returnDataList.length){
          for(let i = 0;i < returnDataList.length;i++){
            // 固件类型处理
            let findTypeItem = firmwareTypeArray.find(item => item.firmwareType === returnDataList[i].firmwareType);
            if(!findTypeItem) {
              let firmwareTypeName = returnDataList[i].firmwareType;
              if(that.formDialog.typeId >= 28 && that.formDialog.typeId <= 30) firmwareTypeName = $filters.pileFirmwareType(returnDataList[i].firmwareType);
              firmwareTypeArray.push( {firmwareType: returnDataList[i].firmwareType,firmwareTypeName: firmwareTypeName});
            }
          }
        }

        that.firmwareListArray = JSON.parse(JSON.stringify(returnDataList));
        that.firmwareTypeArray = JSON.parse(JSON.stringify(firmwareTypeArray));
      }).catch(()=>{
        that.firmwareListArray = [];
        that.firmwareTypeArray = [];
      })
    }

    const initParamConfigFun = ()=>{
      queryAssetTypeList();  // 获取资产分类列表
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun, changeTypeIdFun, queryAssetTypeList,
      queryEquipmentModelList, queryFirmwareListByTypeId, changeFirmwareIdFun, changeFirmwareTypeFun}
  }
})
</script>

<style scoped lang="scss">
.alter_text{
  width: 100%;
  font-size: 12px;
  color: #6162664d;
  margin-left: 12px;
}

.content_body_list{
  display: flex;
  align-items: center;
}
</style>