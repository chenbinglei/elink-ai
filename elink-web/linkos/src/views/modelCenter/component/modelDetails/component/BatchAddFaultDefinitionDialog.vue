<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="620px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formdialog" :rules="rules" label-width="130px">

          <el-form-item label="上传故障表：" prop="dataFile">
            <el-upload ref="uploadRef" v-model:file-list="fileListArray" :auto-upload="false" :limit="fileLength" :on-change="uploadChange"
                       :on-exceed="exceedFile" accept=".xlsx" class="upload_class">
              <template #default>
                <el-button :icon="Upload" class="whiteFontButtons">上传文件</el-button>
              </template>
              <template #tip>
                <div class="el-upload__tip">
                  <span style="margin-right: 4px">格式为.xlsx 最大2MB，单次5000条</span>
                  <el-link :underline="false" type="primary" @click="downloadTemplate">模板下载</el-link>
                </div>
              </template>
            </el-upload>
          </el-form-item>

          <el-form-item v-if="isShowErrorInfo" label="上传错误信息：">
            <div class="content_item">
              <div class="alterText">
                <span>上传总失败</span>
                <span class="errNumBer">{{ $filters.numberNull(errDataList.length) }}</span>
                <span>条，</span>
                <el-link :underline="false" type="primary" @click="downloadTemplate(2)">下载</el-link>
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

<script>
import {Upload} from "@element-plus/icons-vue";
import {exportExcelTable} from "./generateExcel";
import {ElMessage, genFileId} from "element-plus";
import {importPileFaultList} from "@/api/modelCenter/modelManagement";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch} from "vue";

export default {
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
    formDialog: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

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
      formdialog: props.formDialog,
      dialog_visible: props.isVisible,

      errDataList: [],
      errDescList: [],
      isShowErrorInfo: false,

      rules: {
        dataFile: [{required: true, trigger: "change", validator: validateDataFile}],
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
          if (that.fileListArray && that.fileListArray.length) {
            for (let i = 0; i < that.fileListArray.length; i++) {
              if (that.fileListArray[i].raw) formData.append("file", that.fileListArray[i].raw);
            }
          }

          importPileFaultList(formData).then(res => {
            emit("changeEvent");
            if(res.data.errorNum===5000){
              ElMessage({type: "error", showClose: true, message: res.data.errDescList.join(',') });
            }
            ElMessage({type: "success", showClose: true, message: "操作成功！"});
            if (!res.data.errDataList || !res.data.errDataList.length) that.dialog_visible = false;

            that.errDataList = res.data.errDataList;
            that.errDescList = res.data.errDescList;
            if(res.data.errDataList && res.data.errDataList.length){
              that.isShowErrorInfo = true;
              that.listLoading = false;
            }
          }).catch(() => {
            that.listLoading = false;
          })
        }
      })
    }

    // 下载模板
    const downloadTemplate = (operateType = 1) => {

      let json = [], fileName = `故障码模板`;

      let tableHeader = [
        {
          width: 20,
          reaType: 2,
          key: "faultCode",
          header: "faultCode",
          fieldNameCn: "故障编码"
        },
        {
          width: 20,
          reaType: 2,
          key: "eventName",
          header: "eventName",
          fieldNameCn: "事件名称"
        },
        {
          width: 20,
          reaType: 3,
          key: "eventLevel",
          header: "eventLevel",
          fieldNameCn: "事件级别",
          options: [
            {id: 1, name: "次要告警"}, {id: 2, name: "重要告警"}, {id: 3, name: "紧急告警"}, {id: 4, name: "提示告警"}, {id: 5, name: "离线告警"}
          ]
        },
      ]

      if (operateType === 2) {
        fileName = `批量添加故障码-错误信息`;
        json = JSON.parse(JSON.stringify(that.errDataList));
      }

      exportExcelTable(tableHeader, json, fileName, "Sheet1", 5000);
    }

    const uploadChange = (file, fileList) => {
      const isLt1M = file.size / 1024 / 1024 < 2;

      const fileType = file.name.split(".")[1];
      const isIMAGE = fileType === "xlsx";

      if (!isIMAGE) {
        ElMessage({type: "error", showClose: true, message: "上传文件格式只能为xlsx!"});
        fileList.splice(fileList.length - 1, 1);
        return
      }

      if (!isLt1M) {
        ElMessage({type: "error", showClose: true, message: "上传文件大小不能超过 2MB!"});
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

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, uploadChange, uploadRef, exceedFile, downloadTemplate}
  }
}
</script>

<style lang="scss" scoped>
.content_item {
  width: 100%;

  .errNumBer {
    color: #FF0000;
  }

  .errDescList {
    max-height: 180px;
    overflow-y: auto;

    .errDesc {
      font-size: 12px;
      margin-bottom: 4px;
    }
  }
}
</style>
