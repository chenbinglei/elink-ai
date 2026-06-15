<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="640" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="120px">
          <el-form-item label="设备类型：" prop="typeId">
            <el-tree-select v-model="formDialog.typeId" :data="deviceAssetTypeList" :indent="0" :props="treeProps" :render-after-expand="false"
                            placeholder="请选择设备类型" class="leftArrowClass" default-expand-all filterable @change="changeTypeIdFun" />
          </el-form-item>
          <el-form-item label="设备型号：" prop="equipmentModels">
            <el-select v-model="formDialog.equipmentModels" filterable multiple :max-collapse-tags="1" placeholder="请选择设备类型">
              <el-option v-for="item in deviceModelArray" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="固件包：" prop="file_List">
            <UploadFileCustom :acceptType="acceptType" :classFileType="2" fieldName="file_List" v-model:fileArray="formDialog.file_List"
                              :fileSize="20" :isDisabled="!formDialog.typeId" @changEvent="parseFirmwareDataFun">
              <template #tip_content>
                <div class="alter_text" v-if="acceptType">请选择后缀为{{ acceptType }}类型的文件。</div>
                <div class="alter_text" v-else>请选择固件包文件进行上传。</div>
              </template>
            </UploadFileCustom>
          </el-form-item>
          <el-form-item label="固件名称：" prop="firmwareName">
            <el-input v-model="formDialog.firmwareName" placeholder="请输入固件名称"></el-input>
          </el-form-item>
          <el-form-item label="版本号：">
            <el-input v-model="formDialog.firmwareVersion" placeholder="请输入版本号" disabled></el-input>
          </el-form-item>
          <el-form-item label="固件类型：">
            <template v-if="formDialog.typeId >= 28 && formDialog.typeId <= 30">
              <el-input v-model="formDialog.firmwareTypeNmae" placeholder="请输入固件类型" disabled></el-input>
            </template>
            <template v-else>
              <el-input v-model="formDialog.firmwareType" placeholder="请输入固件类型"></el-input>
            </template>
          </el-form-item>
          <el-form-item label="描述：">
            <el-input type="textarea" v-model="formDialog.firmwareDesc" placeholder="请输入描述" :rows="3" maxlength="150" show-word-limit></el-input>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import $filters from "@/common/filters";
import {getUrlFileName, setTreeData} from "@/utils";
import {getAssetTypeList} from "@/api/modelCenter/modelManagement";
import UploadFileCustom from "@/components/component/UploadFileCustom.vue";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";
import {getEquipmentModelList, parseFirmwareData, uploadOrEditFirmware} from "@/api/deviceCenter/firmwareManagement";

export default defineComponent({
  name: "AddFirmwareDialog",
  components: {UploadFileCustom},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "上传固件"
    },
    activeEditDataInfo: {
      type: Object,
      default: ()=>{
        return {}
      }
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateTypeId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择设备分类"));
      } else {
        callback();
      }
    };

    const validateDeviceModel = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请选择设备型号"));
      } else {
        callback();
      }
    };

    const validateFileList = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请上传固件包文件"));
      } else {
        if(!that.formDialog.isAnalysisStatus){
          callback(new Error("固件包文件正在解析！"));
        } else {
          callback();
        }
      }
    };

    const validateFirmwareName = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入固件名称"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      acceptType: "", // 上传文件类型
      listLoading: false,
      deviceModelArray: [],
      deviceAssetTypeList: [],
      dialog_visible: props.isVisible,
      treeProps: {value: 'id', label: 'typeName', children: 'children'},
      rules: {
        typeId: [{required: true, trigger: "change", validator: validateTypeId }],
        file_List: [{required: true, trigger: "change", validator: validateFileList }],
        firmwareName: [{required: true, trigger: "change", validator: validateFirmwareName }],
        equipmentModels: [{required: true, trigger: "change", validator: validateDeviceModel }],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formData = new FormData();

          for (let key in that.formDialog){
            if(key.indexOf('file_List') !== -1){
              let key_arr = key.split("_");
              if(that.formDialog[key] && that.formDialog[key].length){
                for (let i = 0; i < that.formDialog[key].length; i++) {
                  if (that.formDialog[key][i].raw){
                    formData.append(key_arr[0], that.formDialog[key][i].raw);
                  }
                }
              }
            } else {
              formData.append(key, that.formDialog[key]);
            }
          }
          uploadOrEditFirmware(formData).then(()=>{
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

    const changeTypeIdFun = ()=>{
      let acceptType = "";
      delete that.formDialog.deviceModel;
      delete that.formDialog.equipmentModels;
      if(that.formDialog.typeId >= 28 && that.formDialog.typeId <= 30) acceptType = ".fw"; // 充电中包类型
      that.acceptType = acceptType;
      queryEquipmentModelList();
    }

    // 获取设备型号列表
    const queryEquipmentModelList = ()=>{
      getEquipmentModelList({typeId: that.formDialog.typeId,timer: new Date()}).then(res=>{
        that.deviceModelArray = res.data;
      }).catch(()=>{
        that.equipmentModelArray = [];
      })
    }

    // 解析固件包数据
    const parseFirmwareDataFun = ()=>{
      let formData = new FormData();
      that.formDialog.isAnalysisStatus = false;
      if(that.formDialog.file_List && that.formDialog.file_List.length){
        for (let i = 0; i < that.formDialog.file_List.length; i++) {
          if (that.formDialog.file_List[i].raw){
            formData.append("file", that.formDialog.file_List[i].raw);
          }
        }
      }
      parseFirmwareData(formData).then(res=>{
        let returnDataInfo = res.data ?? {};
        returnDataInfo.typeId = that.formDialog.typeId;
        returnDataInfo.firmwareTypeNmae = handleDeviceTypeName(returnDataInfo);
        for (let key in returnDataInfo) that.formDialog[key] = returnDataInfo[key];
        that.formDialog.isAnalysisStatus = true;
      })
    }

    const initParamConfigFun = () => {
      if(props.activeEditDataInfo.id){
        let activeEditDataInfo = JSON.parse(JSON.stringify(props.activeEditDataInfo));
        if(activeEditDataInfo.equipmentModels) activeEditDataInfo.equipmentModels = activeEditDataInfo.equipmentModels.split(',');
        activeEditDataInfo.firmwareTypeNmae = handleDeviceTypeName(activeEditDataInfo);
        if(activeEditDataInfo.firmwarePath){
          activeEditDataInfo.file_List = [{url: activeEditDataInfo.firmwarePath, name: getUrlFileName(activeEditDataInfo.firmwarePath)}]
        }
        activeEditDataInfo.isAnalysisStatus = true;
        that.formDialog = JSON.parse(JSON.stringify(activeEditDataInfo));
        // console.log(that.formDialog);
      }
    }

    const handleDeviceTypeName = (returnDataInfo)=>{
      let deviceTypeName = "";
      // 充电桩设备类型
      if(returnDataInfo.typeId >= 28 && returnDataInfo.typeId <= 30){
        deviceTypeName = $filters.pileFirmwareType(returnDataInfo.firmwareType);
      }
      return deviceTypeName
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      queryAssetTypeList();
      initParamConfigFun();
    });

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun, queryAssetTypeList, queryEquipmentModelList,
      changeTypeIdFun, parseFirmwareDataFun, handleDeviceTypeName}
  }
})

</script>


<style scoped lang="scss">

</style>