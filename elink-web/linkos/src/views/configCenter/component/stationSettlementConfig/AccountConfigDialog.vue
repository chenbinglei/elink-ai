<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="520" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="账户类型：" prop="accountType">
            <el-select v-model="formDialog.accountType" placeholder="请选择账户类型">
              <el-option v-for="item in accountTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="选择企业：" prop="companyId">
            <el-select v-model="formDialog.companyId" placeholder="请选择企业">
              <el-option v-for="item in companyArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="选择商户：" prop="merchantId">
            <el-select v-model="formDialog.merchantId" placeholder="请选择商户">
              <el-option v-for="item in merchantArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AccountConfigDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeSiteId: {
      type: [Number, String],
      default: ""
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateAccountType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择账户类型"));
      } else {
        callback();
      }
    };

    const validateCompanyId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择企业"));
      } else {
        callback();
      }
    };

    const validateMerchantId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择商户"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      titleName: "结算账户配置",
      dialog_visible: props.isVisible,
      formDialog: {accountType: 1},
      companyArray: [],
      merchantArray: [],
      accountTypeArray: [{id: 1, name: "微信收款账户"}, {id: 2, name: "微信付款账户"}],
      rules: {
        accountType: [{required: true, trigger: "change", validator: validateAccountType}],
        companyId: [{required: true, trigger: "change", validator: validateCompanyId}],
        merchantId: [{required: true, trigger: "change", validator: validateMerchantId}],
      }
    })

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          // that.listLoading = true;

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

    onMounted(() => {
      initParamConfigFun();
    })

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut, formDialogRef}
  }
})
</script>

<style lang="scss" scoped>

</style>