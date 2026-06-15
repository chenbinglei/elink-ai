<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName"
    disabledLoading width="680" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="140px">
          <el-form-item label="通道名称：" prop="channelName">
            <el-input v-model="formDialog.channelName" maxlength="32" placeholder="请输入通道名称" show-word-limit
              type="text"></el-input>
          </el-form-item>
          <el-form-item label="协议类型：" prop="protocolCode">
            <el-select v-model="formDialog.protocolCode" :disabled="!!dataForwardId" placeholder="请选择协议类型"
              @change="protocolCodeChange">
              <el-option v-for="item in protocolCodeArray" :key="item.code" :label="item.name"
                :value="item.code"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="ip地址：" required>
            <el-col :span="5">
              <el-form-item prop="ip_protocol">
                <!-- <el-select v-model="formDialog.ip_protocol" placeholder="请选择" :disabled="!formDialog.protocolCode || !!dataForwardId"> -->
                <el-select v-model="formDialog.ip_protocol" placeholder="请选择" :disabled="!formDialog.protocolCode ||!!dataForwardId && formDialog.protocolType !== 2">
                  <template v-for="item in protocolArray" :key="item.id">
                    <template
                      v-if="(formDialog.protocolType === 1 && item.id === 3) || (formDialog.protocolType === 2 && item.id <= 2)">
                      <el-option :label="item.name" :value="item.name"></el-option>
                    </template>
                  </template>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="19">
              <el-form-item prop="ip_address">
                <!-- <el-input v-model="formDialog.ip_address" :disabled="!!dataForwardId" placeholder="请输入IP域名"
                  type="text"></el-input> -->
                <el-input v-model="formDialog.ip_address" :disabled="!!dataForwardId && formDialog.protocolType !== 2"
                  placeholder="请输入IP域名" type="text" />
              </el-form-item>
            </el-col>
          </el-form-item>

          <div v-loading="dynamicFieldLoading" class="dynamicFieldList">
            <template v-for="(item, index) in dynamicFieldList" :key="index">
              <el-form-item v-if="(item.fieldCode !== 'password' && dataForwardId) || !dataForwardId"
                :label="item.fieldName + '：'" :prop="'dynamicFields.' + item.fieldCode"
                :rules="{ required: item.required, message: '请输入' + item.fieldName, trigger: 'change' }">
                <el-input v-model="formDialog.dynamicFields[item.fieldCode]"
                  :disabled="!!dataForwardId && formDialog.protocolType === 1"
                  :placeholder="'请输入' + item.fieldName"></el-input>
              </el-form-item>
            </template>
          </div>
          <el-form-item v-if="!dataForwardId" label="状态：">
            <el-switch v-model="formDialog.status" :active-value="1" :inactive-value="2" active-text="启用"
              inactive-text="关闭" />
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import { ElMessage } from "element-plus";
import { serviceAddressVerification, someCharmap } from "@/utils/validate";
import { getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent } from "vue";
import { findDataForwardById, findProtocolFieldByCode, getProtocolList, saveDataForward } from "@/api/configCenter/dataForwarding";

export default defineComponent({
  name: "AddPassagewayDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "新建通道"
    },
    dataForwardId: {
      type: [Number, String],
      default: ""
    },
  },
  setup (props) {
    const { emit } = getCurrentInstance();

    const validateChannelName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的通道名称"));
      } else {
        callback();
      }
    };

    const validateProtocolCode = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择协议类型"));
      } else {
        callback();
      }
    };

    const validateProtocol = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择"));
      } else {
        callback();
      }
    };

    const validateIpAddress = (rule, value, callback) => {
      if (!serviceAddressVerification(value)) {
        callback(new Error("请输入正确的ip地址"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      dynamicFieldList: [],
      protocolCodeArray: [],
      dynamicFieldStatus: false,
      dynamicFieldLoading: false,
      dialog_visible: props.isVisible,
      formDialog: { dynamicFields: {} },
      protocolArray: [{ id: 1, name: "http://" }, { id: 2, name: "https://" }, { id: 3, name: "tcp://" }],
      rules: {
        ip_protocol: [{ required: true, trigger: "change", validator: validateProtocol }],
        ip_address: [{ required: true, trigger: "change", validator: validateIpAddress }],
        channelName: [{ required: true, trigger: "change", validator: validateChannelName }],
        protocolCode: [{ required: true, trigger: "change", validator: validateProtocolCode }],
      }
    })

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {

          if (!that.dynamicFieldStatus) {
            ElMessage({ type: "warning", showClose: true, message: "当前协议动态字段不完整！" });
            return
          }

          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          formDialog.address = formDialog.ip_protocol + formDialog.ip_address;

          saveDataForward({ ...formDialog }).then(() => {
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", message: "操作成功", showClose: true });
          }).catch(() => {
            that.listLoading = false;
          })
        }
      })
    }

    const initParamConfigFun = () => {
      // that.formDialog = Object.assign({},props.activeEditInfo,that.formDialog);
      if (props.dataForwardId) {
        that.listLoading = true;
        findDataForwardById({ id: props.dataForwardId }).then(res => {
          let formDialog = res.data ? res.data : {};
          if (formDialog.dynamicFields) formDialog.dynamicFields = JSON.parse(formDialog.dynamicFields);
          if (!formDialog.dynamicFields) formDialog.dynamicFields = {};
          if (formDialog.address) {
            let findItem = that.protocolArray.find(item => formDialog.address.indexOf(item.name) !== -1);
            let ip_address_arr = formDialog.address.split("//");
            formDialog.ip_address = ip_address_arr[1];
            formDialog.ip_protocol = findItem?.name;
          }
          that.formDialog = JSON.parse(JSON.stringify(formDialog));
          that.listLoading = false;
          protocolCodeChange(); // 根据协议标识查询协议字段列表
        }).catch(() => {
          that.listLoading = false;
        })
      }
    }

    // 根据协议标识查询协议字段列表
    const protocolCodeChange = () => {
      that.dynamicFieldStatus = false;
      that.dynamicFieldLoading = true;
      try {
        if (that.protocolCodeArray && that.protocolCodeArray.length) {
          let findItem = that.protocolCodeArray.find(item => item.code === that.formDialog.protocolCode);
          that.formDialog.protocolType = findItem?.type; // 1-Mqtt  2-Http
        }
      } catch (e) {
      }
      findProtocolFieldByCode({ code: that.formDialog.protocolCode }).then(res => {
        console.log(res,'试剂库');
        that.dynamicFieldList = res.data ? res.data : [];
        that.dynamicFieldLoading = false;
        that.dynamicFieldStatus = true;
      }).catch(() => {
        // console.log(err);
        that.dynamicFieldList = [];
        that.dynamicFieldStatus = false;
        that.dynamicFieldLoading = false;
      })
    }

    // 获取接入协议列表
    const queryProtocolList = () => {
      getProtocolList({ timer: new Date() }).then(res => {
        that.protocolCodeArray = res.data ? res.data : [];
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
      queryProtocolList();
    })

    return { ...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut, formDialogRef, protocolCodeChange, queryProtocolList }
  }
})
</script>

<style lang="scss" scoped>
.dynamicFieldList {
  .el-form-item {
    margin-bottom: 18px !important;
  }
}
</style>