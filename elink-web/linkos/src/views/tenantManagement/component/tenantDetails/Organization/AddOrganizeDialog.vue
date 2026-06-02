<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName" width="550px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">
          <el-form-item label="组织名称:" prop="organName">
            <el-input type="text" v-model="formDialog.organName" maxlength="32" show-word-limit placeholder="请输入组织名称"></el-input>
          </el-form-item>
          <el-form-item label="父级组织:" prop="parentId">
            <el-select v-model="formDialog.parentId" clearable placeholder="请选择父级组织">
              <template v-for="item in fatherArr" :key="item.id">
                <el-option v-if="item.id !== tenant_id" :label="item.organName" :value="item.id"></el-option>
              </template>
            </el-select>
          </el-form-item>
          <el-form-item label="排序:" prop="sortNumber">
            <el-input type="text" v-model="formDialog.sortNumber" placeholder="请输入排序"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {commonCharName} from "@/utils/validate";
import {saveOrUpdateOrganStructure} from "@/api/tenantManagement/tenantTabulation";
import {getCurrentInstance, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AddOrganize",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "新增组织"
    },
    fatherArr: {
      type: Array,
      default: []
    },
    tenantId: {
      type: [String, Number],
      default: ""
    },
    parentId: {
      type: [String, Number],
      default: ""
    },
  },
  setup(props) {

    const {emit} = getCurrentInstance();

    const validateOrganName = (rule, value, callback) => {
      if(!value || !commonCharName(value)){
        callback(new Error("请输入正确的组织名称"));
      } else {
        callback();
      }
    };

    const validateParentId = (rule, value, callback) => {
      if(!value){
        callback(new Error("请选择父级组织"));
      } else {
        callback();
      }
    };

    const validateSortNumber = (rule, value, callback) => {
      if(!value && value !== 0){
        callback(new Error("请输入排序"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      tenant_id: props.tenantId,
      dialog_visible: props.isVisible,
      formDialog: { parentId: props.parentId },

      rules: {
        organName: [{ required: true, trigger: "change", validator: validateOrganName }],
        parentId: [{ required: true, trigger: "change", validator: validateParentId }],
        sortNumber: [{ required: true, trigger: "change", validator: validateSortNumber }],
      },
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          saveOrUpdateOrganStructure({ ...that.formDialog,tenantId: that.tenant_id }).then(()=>{
            emit("changeEvent");
            that.listLoading = false;
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    return { ...toRefs(that),watchVisible,watchDialogVisible,formDialogRef,saveDialog }
  }
})
</script>

<style scoped lang="scss">

</style>
