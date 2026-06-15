<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName"
          disabledLoading width="620px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formdialog" :rules="rules" label-width="130px">

          <el-form-item label="设备分类：" prop="typeId">
            <el-tree-select v-model="formdialog.typeId" :data="handleMenuArray" :indent="0" :props="treeProps" :render-after-expand="false"
                            class="leftArrowClass" default-expand-all filterable @change="queryModelNameListByTypeId" />
          </el-form-item>
          <el-form-item label="选择模型：" prop="modelId">
            <el-select v-model="formdialog.modelId" :disabled="!formdialog.typeId" placeholder="请选择选择模型">
              <el-option v-for="item in modelArray" :key="item.id" :label="item.modelName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="所属站点：" prop="siteId">
            <el-select v-model="formdialog.siteId" placeholder="请选择所属站点" @change="changeSiteFun">
              <el-option v-for="item in siteArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="父级节点：">
            <el-tree-select v-model="formdialog.parentId" :data="parentNodeArray" :indent="0" :props="treeProps1" :render-after-expand="false"
                            clearable class="leftArrowClass" check-strictly default-expand-all filterable :disabled="!formdialog.siteId" />
          </el-form-item>

          <el-form-item label="上传设备表：" prop="dataFile">
            <el-upload ref="uploadRef" v-model:file-list="fileListArray" :auto-upload="false" :limit="fileLength" :on-change="uploadChange"
                       :on-exceed="exceedFile" accept=".xlsx" class="upload_class">
              <template #default>
                <el-button :icon="Upload" class="whiteFontButtons">上传文件</el-button>
              </template>
              <template #tip>
                <div class="el-upload__tip">
                  <span style="margin-right: 4px">格式为.xlsx 最大2MB，单次1000个设备</span>
                  <el-link type="primary" :underline="false" @click="downloadTemplate">模板下载</el-link>
                </div>
              </template>
            </el-upload>
          </el-form-item>

          <el-form-item label="上传错误信息：" v-if="isShowErrorInfo">
            <div class="content_item">
              <div class="alterText">
                <span>上传总失败</span>
                <span class="errNumBer">{{ $filters.numberNull(errDataList.length) }}</span>
                <span>条，</span>
                <el-link type="primary" :underline="false" @click="downloadTemplate(2)">下载</el-link>
              </div>

              <div class="errDescList scrollbarStyle">
                <template v-for="(item,index) in errDescList" :key="index">
                  <div class="errDesc">{{ item }}</div>
                </template>
              </div>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {setTreeData} from "@/utils";
import {Upload} from "@element-plus/icons-vue";
import {ElMessage, genFileId} from "element-plus";
import {pickerOptionsMinutesTimer} from "@/utils/dateTime";
import {getAssetTypeList} from "@/api/modelCenter/modelManagement";
import {findSiteInfoListByUserId} from "@/api/siteCenter/siteManagement";
import {exportDeviceCustomExcel} from "@/views/deviceCenter/component/generateExcel";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";
import {batchInsertDevice, findDeviceAssetList, getModelFieldUpdateListByModelId, getModelNameListByTypeId} from "@/api/deviceCenter/deviceList";

export default defineComponent({
  name: "BatchAddDeviceDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "创建模型"
    },
    activeSiteId: {
      type: [String, Number],
      default: ""
    }
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

    const validateSiteId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择所属站点"));
      } else {
        callback();
      }
    };

    const validateModelId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择模型"));
      } else {
        callback();
      }
    };

    const validateDataFile = (rule, value, callback) => {
      if (!that.fileListArray || !that.fileListArray.length) {
        callback(new Error("请上传文件"));
      } else {
        callback();
      }
    };

    const that = reactive({
      Upload,
      fileLength: 1,
      fileListArray: [],
      listLoading: false,
      dialog_visible: props.isVisible,
      formdialog: { siteId: props.activeSiteId },
      pickerOptions: pickerOptionsMinutesTimer(),
      treeProps1: {value: 'id', label: 'name', children: 'children'},
      treeProps: {value: 'id', label: 'typeName', children: 'children'},

      errDataList:[],
      errDescList:[],
      isShowErrorInfo: false,

      siteArray: [],
      modelArray: [],
      handleMenuArray: [],
      modelFieldArray: [],
      parentNodeArray: [],

      rules: {
        siteId: [{required: true, trigger: "change", validator: validateSiteId }],
        typeId: [{required: true, trigger: "change", validator: validateTypeId }],
        modelId: [{required: true, trigger: "change", validator: validateModelId }],
        dataFile:[{required: true, trigger: "change", validator: validateDataFile }],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          that.isShowErrorInfo = false;
          let formData = new FormData();

          //字段参数
          for (let key in that.formdialog) formData.append(key, that.formdialog[key]);
          if(that.fileListArray && that.fileListArray.length){
            for (let i = 0; i < that.fileListArray.length; i++) {
              if (that.fileListArray[i].raw) formData.append("dataFile", that.fileListArray[i].raw);
            }
          }

          batchInsertDevice(formData).then(res=>{
            emit("changeEvent");
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
            if(!res.data.errDataList || !res.data.errDataList.length) that.dialog_visible = false;

            that.errDataList = res.data.errDataList;
            that.errDescList = res.data.errDescList;
            if(res.data.errDataList && res.data.errDataList.length){
              that.isShowErrorInfo = true;
              that.listLoading = false;
            }
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    // 获取资产分类列表
    const queryAssetTypeList = ()=>{
      getAssetTypeList({ ids: [3],timer: new Date(),pageName: "AddDeviceDialog" }).then(res=>{
        let assetTypeList = res.data ? res.data : [];
        that.handleMenuArray = setTreeData(assetTypeList);
      })
    }

    // 根据设备类型id获取模型名称列表
    const queryModelNameListByTypeId = ()=>{
      that.isShowErrorInfo = false;
      getModelNameListByTypeId({ typeId: that.formdialog.typeId }).then(res=>{
        that.modelArray = res.data;
        that.formdialog.modelId = "";
      }).catch(()=>{
        that.modelArray = [];
        that.formdialog.modelId = "";
      })
    }

    // 下载模板
    const downloadTemplate = (operateType = 1)=>{

      if(!that.formdialog.modelId){
        ElMessage({ type: "error", showClose: true, message: "请先选择关联模型!" });
        return
      }

      let modelFindItem = that.modelArray.find(item=> item.id === that.formdialog.modelId);

      // 根据模型id获取模型编辑字段列表
      getModelFieldUpdateListByModelId({ modelId: that.formdialog.modelId }).then(res=>{
        let tableHeader = [];
        let modelFieldArray = res.data ? res.data : [];
        for(let i = 0;i < modelFieldArray.length;i++){

          let colsObj = {
            width: 20,
            key: modelFieldArray[i].fieldName,
            reaType: modelFieldArray[i].reaType,
            header: modelFieldArray[i].fieldName,
            fieldNameCn: modelFieldArray[i].reaName
          };
          modelFieldArray[i].extraValue = modelFieldArray[i].extraValue ? JSON.parse(modelFieldArray[i].extraValue) : {};

          // 数值
          if(modelFieldArray[i].reaType === 1){
            colsObj.options = modelFieldArray[i].extraValue;
            if(colsObj.options.minValue === undefined)colsObj.options.minValue = -99999999999;
            if(colsObj.options.maxValue === undefined)colsObj.options.maxValue = 99999999999;
          }

          // 选项
          if(modelFieldArray[i].reaType === 3){
            colsObj.options = modelFieldArray[i].extraValue.enumArray;
          }

          // 开关
          if(modelFieldArray[i].reaType === 5){
            colsObj.options = [
              { name: modelFieldArray[i].extraValue.trueValue, id: true },
              { name: modelFieldArray[i].extraValue.falseValue, id: false },
            ]
          }

          // 时间
          if(modelFieldArray[i].reaType === 6){
            colsObj.options = modelFieldArray[i].extraValue.timeFormat;
          }

          tableHeader.push(colsObj);
        }

        let json = [], fileName = `${ modelFindItem.modelName }-批量添加设备模板`;
        if(operateType === 2){
          fileName = `${ modelFindItem.modelName }-错误信息`;
          json = JSON.parse(JSON.stringify(that.errDataList));
        }
        exportDeviceCustomExcel(tableHeader, json, fileName);
      })
    }

    const uploadChange = (file,fileList)=>{
      const isLt1M = file.size / 1024 / 1024 < 2;

      const fileType = file.name.split(".")[1];
      const isIMAGE = fileType === "xlsx";

      if (!isIMAGE) {
        ElMessage({ type: "error", showClose: true, message: "上传文件格式只能为xlsx!" });
        fileList.splice(fileList.length - 1, 1);
        return
      }

      if (!isLt1M) {
        ElMessage({ type: "error", showClose: true, message: "上传文件大小不能超过 2MB!" });
        fileList.splice(fileList.length - 1, 1);
        return
      }

      that.fileListArray = fileList;
    }

    // 上传图片数量限制(覆盖上一个文件)
    const uploadRef = ref(null);
    const exceedFile = (files) => {
      uploadRef.value.clearFiles();
      const file = files[0];
      file.uid = genFileId();
      uploadRef.value.handleStart(file);
    };

    // 根据用户id查询站点列表信息
    const querySiteInfoListByUserId = ()=>{
      findSiteInfoListByUserId({ timer: new Date() }).then(res=>{
        that.siteArray = res.data ? res.data : [];
      })
    }

    const changeSiteFun = ()=>{
      that.formdialog.parentId = "";
      queryDeviceAssetList();
    }

    // 根据站点id查询设备资产父节点数据
    const queryDeviceAssetList = ()=>{
      if(!that.formdialog.siteId) return

      findDeviceAssetList({ siteId: that.formdialog.siteId }).then(res=>{
        let parentNodeArray = res.data ? res.data : [];
        parentNodeArray.forEach(item=>{
          if (item.parentId === that.formdialog.siteId) item.parentId = "";
        })
        that.parentNodeArray = setTreeData(parentNodeArray);
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      queryAssetTypeList();
      querySiteInfoListByUserId();
      if(that.formdialog.siteId) queryDeviceAssetList();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, querySiteInfoListByUserId,queryAssetTypeList, queryModelNameListByTypeId,
      uploadChange, uploadRef, exceedFile, downloadTemplate, queryDeviceAssetList, changeSiteFun}
  }
})
</script>

<style scoped lang="scss">
.content_item{
  width: 100%;

  .errNumBer{
    color: #FF0000;
  }

  .errDescList{
    max-height: 180px;
    overflow-y: auto;

    .errDesc{
      font-size: 12px;
      margin-bottom: 4px;
    }
  }
}
</style>
