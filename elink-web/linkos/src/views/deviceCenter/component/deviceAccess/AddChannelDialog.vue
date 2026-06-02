<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="620px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">
          <el-form-item label="通道名称:" prop="channelName">
            <el-input type="text" v-model="formDialog.channelName" placeholder="请输入通信名称"></el-input>
          </el-form-item>
          <el-form-item label="协议类型:" prop="protocolType">
            <el-select v-model="formDialog.protocolType" placeholder="请选择协议类型" :disabled="editStatus">
              <el-option v-for="item in protocolTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="接入协议:" prop="accessProtocol">
            <el-select v-model="formDialog.accessProtocol" placeholder="请选择接入协议" :disabled="editStatus">
              <el-option v-for="item in accessProtocolArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="IP地址:" prop="ip">
            <el-input type="text" v-model="formDialog.ip" placeholder="请输入IP地址"></el-input>
          </el-form-item>
          <el-form-item label="端口号:" prop="port">
            <el-input type="text" v-model="formDialog.port" placeholder="请输入端口号" maxlength="6" show-word-limit></el-input>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {saveChannel} from "@/api/deviceCenter/deviceAccess";
import {notCharmap,isValidIP,integer1tox} from "@/utils/validate";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch} from "vue";

export default {
  name: "AddChannelDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: [Number, String],
      default: "新增通道"
    },
    activeDeviceId: {
      type: [Number, String],
      default: ""
    },
    editStatus: {
      type: Boolean,
      default: false
    },
    // 编辑的通道id
    channelInfo: {
      type: Object,
      default: ()=>{
        return { }
      }
    }
  },
  setup(props) {

    const {emit} = getCurrentInstance();

    const validateChannelName = (rule, value, callback) => {
      if (!value || !notCharmap(value)) {
        callback(new Error("请输入正确的通信名称"));
      } else {
        callback();
      }
    };

    const validateIpAddress = (rule, value, callback) => {
      if (!isValidIP(value)) {
        callback(new Error("请输入正确的IP地址"));
      } else {
        callback();
      }
    };

    const validateProtocolType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择协议类型"));
      } else {
        callback();
      }
    };

    const validateAccessProtocol = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择接入协议"));
      } else {
        callback();
      }
    };

    const validatePort = (rule, value, callback) => {
      if (!integer1tox(value, 6)) {
        callback(new Error("请输入正确的端口号"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      formDialog: props.channelInfo,
      dialog_visible: props.isVisible,
      accessProtocolArray: [{name: "SMIEG", id: "SMIEG"}],
      protocolTypeArray: [{name: "MQTT", id: "mqtt"}, {name: "HTTP", id: "http"},{name: "TCP", id: "tcp"}],
      rules: {
        channelName: [{required: true, trigger: "change", validator: validateChannelName}],
        protocolType: [{required: true, trigger: "change", validator: validateProtocolType}],
        accessProtocol: [{required: true, trigger: "change", validator: validateAccessProtocol}],
        ip: [{required: true, trigger: "change", validator: validateIpAddress}],
        port: [{required: true, trigger: "change", validator: validatePort}],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          // 新增或编辑通道数据
          saveChannel({ ...that.formDialog,deviceId:props.activeDeviceId }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const initParamConfigFun = () => {

    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {})

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun}

  }
}
</script>

<style scoped>

</style>
