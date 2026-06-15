<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="680" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">
          <el-form-item label="鉴权类型：" prop="authorityType">
            <el-select v-model="formDialog.authorityType" placeholder="请选择鉴权类型">
              <el-option v-for="item in authorityTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-if="formDialog.authorityType === 1" label="用户手机号码：" prop="authorityAccount">
            <el-input v-model="formDialog.authorityAccount" maxlength="11" placeholder="请输入" type="text" show-word-limit></el-input>
          </el-form-item>
          <el-form-item v-else label="车辆vin码：" prop="authorityAccount">
            <el-input v-model="formDialog.authorityAccount" placeholder="请输入" type="text" maxlength="17" show-word-limit></el-input>
          </el-form-item>
          <el-form-item label="备注：">
            <el-input v-model="formDialog.notes" placeholder="请输入" maxlength="200" :rows="3" type="textarea" show-word-limit></el-input>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {isCheckVIN, mobile} from "@/utils/validate";
import {saveWhiteRosterInfo} from "@/api/operationManagement/CsStationDetails";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AddWhiteListDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加白名单"
    },
    activeEditInfo: {
      type: Object,
      default: ()=>{
        return{};
      }
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateAuthorityType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择鉴权类型"));
      } else {
        callback();
      }
    };

    const validateAuthorityAccount = (rule, value, callback) => {
      if(that.formDialog.authorityType === 1){
        if (!mobile(value)) {
          callback(new Error("请输入正确的手机号"));
        } else {
          callback();
        }
      } else {
        if (!isCheckVIN(value)) {
          callback(new Error("请输入正确的VIN码"));
        } else {
          callback();
        }
      }
    };

    const that = reactive({
      listLoading: false,
      dialog_visible: props.isVisible,
      formDialog: { authorityType: 1 },
      authorityTypeArray: [{id: 1, name: "用户"}, {id: 2, name: "车辆"}],
      rules: {
        authorityType: [{required: true, trigger: "change", validator: validateAuthorityType}],
        authorityAccount: [{required: true, trigger: "change", validator: validateAuthorityAccount}],
      }
    });

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          // 新增或编辑白名单信息
          saveWhiteRosterInfo({ ...that.formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", message: "操作成功", showClose: true });
          }).catch(()=>{
            that.listLoading = false;
          });
        }
      });
    };

    const initParamConfigFun = () => {
      that.formDialog = Object.assign({},props.activeEditInfo,that.formDialog);
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut, formDialogRef};
  }
});
</script>

<style lang="scss" scoped>

</style>