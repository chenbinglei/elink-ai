<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName"
    disabledLoading width="45vw" @confirm="saveDialog">
    <template v-slot:content>
      <div class="content_body">
        <el-form ref="formDialogRef" :model="formdialog" :rules="rules" label-width="130px">
          <el-form-item label="选择设备/功能点:">
            <div class="transfer_class">
              <div class="content_body_left content_body_list flex-all scrollbarStyle">
                <HandleTree ref="handleMenusLeftRef" showCheckbox defaultExpandAll :isShowHeader="false"
                  :isBottomBut="false" :handleMenuArray="handleMenuArray" @handleMenuEvent="handleMenuEvent">
                </HandleTree>
              </div>
              <div class="content_body_right content_body_list flex-all scrollbarStyle">
                <div class="content_top">已选择功能（{{ checkMenuNum }}个）</div>
                <div class="content_bottom">
                  <HandleTree ref="HandleMenusLeftRef" defaultExpandAll :isSearchInput="false" :isShowHeader="false"
                    :handleMenuArray="checkMenuArray" displayClosed @handleMenuEvent="handleMenuEvent"></HandleTree>
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="上传点表:" prop="pointTableFile">
            <el-upload ref="uploadRef" v-model:file-list="fileListArray" :auto-upload="false" :limit="fileLength"
              :on-change="uploadChange" :on-exceed="exceedFile" accept=".xlsx" class="upload_class">
              <template #default>
                <el-button :icon="Upload" class="whiteFontButtons">上传文件</el-button>
              </template>
              <template #tip>
                <div class="el-upload__tip">
                  <span style="margin-right: 4px">格式为.xlsx 最大2MB</span>
                  <el-link type="primary" :underline="false" @click="downloadTemplate">点表模板下载</el-link>
                </div>
              </template>
            </el-upload>
          </el-form-item>

          <el-form-item label="上传错误信息：" v-if="isShowErrorInfo">
            <div class="content_item">
              <div class="alterText">
                <span>上传总失败</span>
                <span class="errNumBer">{{ $filters.numberNull(errDescList.length) }}</span>
                <span>条</span>
              </div>
              <div class="errDescList scrollbarStyle">
                <template v-for="(item, index) in errDescList" :key="index">
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
import { Upload } from "@element-plus/icons-vue";
import { ElMessage, genFileId } from "element-plus";
import { deleteTreeArray, setTreeData, treeToArray } from "@/utils";
import { exportPointTableExcel } from "@/views/deviceCenter/component/generateExcel";
import { getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent } from "vue";
import { findSubDeviceFunctionListByDeviceId, importPointTableData } from "@/api/deviceCenter/deviceAccess";

export default defineComponent({
  name: "ImportPointTable",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeDeviceId: {
      type: [Number, String],
      default: ""
    },
    activeDeviceName: {
      type: String,
      default: ""
    },
    activeChannelId: {
      type: [Number, String],
      default: ""
    },
    activeChannelName: {
      type: String,
      default: ""
    },
  },
  setup (props) {
    const { emit } = getCurrentInstance();
    const handleMenusLeftRef = ref(null);

    const validatePointTableFile = (rule, value, callback) => {
      if (!that.fileListArray || !that.fileListArray.length) {
        callback(new Error("请上传文件"));
      } else {
        callback();
      }
    };

    const that = reactive({
      Upload,
      fileLength: 1,
      formdialog: {},
      fileListArray: [],
      listLoading: false,
      titleName: "导入点表",
      dialog_visible: props.isVisible,

      errDescList: [],
      isShowErrorInfo: false,

      checkMenuNum: 0,
      checkMenuArray: [],
      handleMenuArray: [],
      allHandleMenuArray: [],
      rules: {
        pointTableFile: [{ required: true, trigger: "change", validator: validatePointTableFile }],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          that.isShowErrorInfo = false;

          let formData = new FormData();
          if (that.fileListArray && that.fileListArray.length) {
            for (let i = 0; i < that.fileListArray.length; i++) {
              if (that.fileListArray[i].raw) formData.append("pointTableFile", that.fileListArray[i].raw);
            }
          }
          formData.append("channelId", props.activeChannelId); // 当前通道id
          importPointTableData(formData).then(res => {
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
            if (!res.data.errDescList || !res.data.errDescList.length) that.dialog_visible = false;
            if (res.data.errDescList && res.data.errDescList.length) that.isShowErrorInfo = true;
            that.errDescList = res.data.errDescList;
            emit("changeEvent");
            that.listLoading = false;
          }).catch(() => {
            that.listLoading = false;
          })
        }
      })
    }

    function removeNodeByData (nodes, data) {
      return nodes.filter(node => {
        // 如果当前节点 id 和 parentId 匹配 data 中的值，则删除
        const isMatch = node.id === data.id;

        if (isMatch) {
          return false; // 删除该节点
        }

        // 递归处理 children
        if (node.children && node.children.length > 0) {
          node.children = removeNodeByData(node.children, data);
        }
        return true;
      });
    }
    // 选中触发
    const handleMenuEvent = (data) => {
      if (data.type === "clickTreeNode") {
        let checkMenuArray = [], deviceArray = [];
        
        const selectedNoChildren=data.selectedNodes.filter(item=>!item.children)
        console.log(data.allSelectIdArray,data,selectedNoChildren,'data.allSelectIdArray')
        that.checkMenuNum =selectedNoChildren.length;
        for (let i = 0; i < data.allSelectIdArray.length; i++) {
          let findItem = that.allHandleMenuArray.find(item => item.id === data.allSelectIdArray[i]);
          if (findItem) {
            checkMenuArray.push(findItem);
            let findItam = deviceArray.find(item => item.id === findItem.deviceId);
            if (!findItam) deviceArray.push({ id: findItem.deviceId, name: findItem.deviceName, isDevice: true, parentId: 0 });
          }
        }
        that.checkMenuArray = setTreeData([...deviceArray, ...checkMenuArray]);
      }

      if (data.menuType === "clickClosedIcon") {
        //  const selectIdArray = removeNodeByData(that.checkMenuArray, data.activeClickId);
        // console.log("handleMenuEvent",selectIdArray);
        let checkMenuNum = 0, checkMenuIdsArray = [];
        const checkMenuArray = removeNodeByData(that.checkMenuArray, data.activeClickId);
        // let checkMenuArray = deleteTreeArray(that.checkMenuArray, data.activeClickId);
        let levelTreeArray = treeToArray(checkMenuArray);
        console.log("handleMenuEvent999", levelTreeArray);
        for (let i = 0; i < levelTreeArray.length; i++) {
          if (!levelTreeArray[i].isDevice) {
            checkMenuNum += 1;
            checkMenuIdsArray.push(levelTreeArray[i].id);
          }
        }
        console.log("handleMenuEvent888", checkMenuIdsArray);
        // that.checkMenuNum =  checkMenuNum;
        // that.checkMenuArray =  checkMenuArray;
        handleMenusLeftRef.value.setCheckedKeysFun(checkMenuIdsArray);
      }
    }

    // 下载模板
    const downloadTemplate = () => {
      let tableHeader = [
        { width: 18, header: "设备名称", key: "deviceName" },
        { width: 35, header: "设备名ID", key: "deviceId" },
        { width: 18, header: "功能点名称", key: "functionName" },
        { width: 35, header: "功能点ID", key: "functionId" },
        { width: 18, header: "点号", key: "dataId" },
        { width: 18, header: "系数", key: "coefficient" },
        { width: 18, header: "偏移量", key: "offset" },
      ]; // 表头

      let allCheckMenuArray = []; //选择的数据
      let checkMenuArray = treeToArray(that.checkMenuArray);
      // console.log(checkMenuArray)

      for (let i = 0; i < checkMenuArray.length; i++) {
        if (!checkMenuArray[i].isDevice) {
          allCheckMenuArray.push({ ...checkMenuArray[i], dataId: null, coefficient: 1, offset: 0 });
        }
      }

      if (!allCheckMenuArray || !allCheckMenuArray.length) {
        ElMessage({ type: "error", showClose: true, message: "请选择需要导出的设备功能点!" });
        return
      }

      // console.log(allCheckMenuArray)
      exportPointTableExcel(tableHeader, allCheckMenuArray, `${props.activeDeviceName}-${props.activeChannelName}-批量导入点表模板`);
    }

    const initParamConfigFun = () => {
      // 根据设备id查询网关子设备功能点列表
      that.listLoading = true;
      findSubDeviceFunctionListByDeviceId({ deviceId: props.activeDeviceId, type: 1, pageName: "ImportPointTable" }).then(res => {

        let deviceArray = [];
        let functionArray = res.data ? res.data : [];

        for (let i = 0; i < functionArray.length; i++) {
          let findItem = deviceArray.find(item => item.id === functionArray[i].deviceId);
          if (!findItem) deviceArray.push({ id: functionArray[i].deviceId, name: functionArray[i].deviceName, isDevice: true, parentId: 0 });

          functionArray[i].id = functionArray[i].functionId + functionArray[i].deviceId;
          functionArray[i].name = functionArray[i].functionName;
          functionArray[i].parentId = functionArray[i].deviceId;
        }

        that.handleMenuArray = setTreeData([...deviceArray, ...functionArray]);
        console.log(that.handleMenuArray, 'handleMenuArray')
        that.allHandleMenuArray = functionArray;
        that.listLoading = false;
      }).catch((e) => {
        console.log(e);
        that.listLoading = false;
      })
    }

    const uploadChange = (file, fileList) => {
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

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    })

    return {
      ...toRefs(that), watchVisible, watchDialogVisible, saveDialog, initParamConfigFun, formDialogRef, uploadChange, uploadRef, exceedFile,
      handleMenuEvent, handleMenusLeftRef, downloadTemplate
    }
  }
})
</script>

<style lang="scss" scoped>
.content_body {

  .transfer_class {
    width: 100%;
    height: 45vh;
    display: flex;
    align-items: center;

    .content_body_list {
      height: 100%;
      border-radius: 4px;
      border: 1px solid #DBDBDD;
      display: flex;
      flex-direction: column;
      overflow-y: auto;

      :deep(.handleMenu) {
        width: 100%;
        border-right: none;
      }

      .content_top {
        height: 48px;
        padding: 0 16px;
        display: flex;
        align-items: center;
        border-bottom: 1px solid #E3E3E3;
        box-sizing: border-box;
      }

      .content_bottom {
        flex: 1;
        box-sizing: border-box;
      }
    }

    .content_body_left {
      margin-right: 12px;

      .content_top_left {
        width: 140px;
        margin-right: 12px;
      }
    }
  }

  .content_item {
    width: 100%;

    .errNumBer {
      color: #FF0000;
    }

    .errDescList {
      max-height: 120px;
      overflow-y: auto;

      .errDesc {
        font-size: 12px;
        margin-bottom: 4px;
      }
    }
  }
}
</style>
