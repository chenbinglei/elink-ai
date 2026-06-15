<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="680" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">
          <el-form-item label="关联方：" prop="tenantId">
            <el-select v-model="formDialog.tenantId" filterable placeholder="请选择关联方">
              <el-option v-for="(item,index) in tenantIdArray" :key="index" :label="item.tenantName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="关联方类型：" prop="affiliateTypes">
            <el-select v-model="formDialog.affiliateTypes" multiple collapse-tags placeholder="请选择关联方类型">
              <el-option v-for="(item,index) in affiliateTypesArray" :key="index" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
<!--          <el-form-item label="资产权限：" prop="authorityType">-->
<!--            <el-select v-model="formDialog.authorityType" placeholder="请选择关联方类型">-->
<!--              <el-option v-for="(item,index) in authorityTypeArray" :key="index" :label="item.name" :value="item.id"></el-option>-->
<!--            </el-select>-->
<!--          </el-form-item>-->
          <el-form-item label="联系人：" prop="contactsName">
            <el-input v-model="formDialog.contactsName" maxlength="32" placeholder="请输入联系人名称" show-word-limit/>
          </el-form-item>
          <el-form-item label="联系电话：" prop="contactsPhone">
            <el-input v-model="formDialog.contactsPhone" maxlength="11" placeholder="请输入联系电话" show-word-limit/>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";
import {findTenantInfoByPage} from "@/api/tenantManagement/tenantTabulation";
import {mobile} from "@/utils/validate";
import {saveOrUpdateAffiliatesInfo} from "@/api/siteCenter/stationDetails";

export default defineComponent({
  name: "AddRelatedPartiesDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加关联方"
    },
    activeEditInfo:{
      type: Object,
      default:()=>{
        return { }
      }
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateTenantId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择所属租户"));
      } else {
        callback();
      }
    };

    const validateAffiliateTypes = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择关联方类型"));
      } else {
        callback();
      }
    };

    const validateContactsName = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入联系人名称"));
      } else {
        callback();
      }
    };

    const validateContactsPhone = (rule, value, callback) => {
      if (!mobile(value)) {
        callback(new Error("请输入正确的联系电话"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      dialog_visible: props.isVisible,
      formDialog: { authorityType: 0 },

      tenantIdArray: [], // 所属租户
      authorityTypeArray: [{id: 0, name: "无权限"}, {id: 1, name: "只读"}, {id: 2, name: "读写"}],
      affiliateTypesArray: [{id: 1, name: "用能单位"}, {id: 4, name: "建设单位"}, {id: 5, name: "供电单位"}], // {id: 2, name: "产权单位"}, {id: 3, name: "运营单位"},
      rules: {
        tenantId: [{required: true, trigger: "change", validator: validateTenantId }],
        affiliateTypes: [{required: true, trigger: "change", validator: validateAffiliateTypes }],
        // contactsName: [{required: true, trigger: "change", validator: validateContactsName }],
        // contactsPhone: [{required: true, trigger: "change", validator: validateContactsPhone }],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          formDialog.affiliateTypes = formDialog.affiliateTypes.join(',');
          saveOrUpdateAffiliatesInfo({ ...formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    // 分页查询租户信息
    const queryTenantInfoByPage = () => {
      findTenantInfoByPage({page: 1, size: 0}).then(res => {
        that.tenantIdArray = res.data ? res.data : [];
      })
    }

    const initParamConfigFun = () => {
      queryTenantInfoByPage();
      that.formDialog = Object.assign({},props.activeEditInfo,that.formDialog);
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

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun, queryTenantInfoByPage}
  }
})
</script>

<style lang="scss" scoped>

</style>