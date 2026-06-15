<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName"
          width="620px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="form_dialog" :rules="rules" label-width="130px">
          <el-form-item label="用户组名称：" prop="groupName">
            <el-input v-model="form_dialog.groupName" maxlength="32" show-word-limit placeholder="请输入用户组名称"/>
          </el-form-item>
          <el-form-item label="描述：">
            <el-input v-model="form_dialog.refer" :rows="3" type="textarea" placeholder="请输入描述" maxlength="200" show-word-limit/>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {commonCharName} from "@/utils/validate";
import {Delete, Plus} from "@element-plus/icons-vue";
import {getCurrentInstance, reactive, ref, toRefs, watch} from "vue";
import {saveOrUpdateUserGroup} from "@/api/systemManagement/userGroupManagement";

export default {
  name: "AddUserGroup",
  components:{Plus,Delete},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "新增用户组"
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
    const validateGroupName = (rule, value, callback) => {
      if(!value || !commonCharName(value)){
        callback(new Error("请输入正确的用户组名称"));
      } else {
        callback();
      }
    };
    const that = reactive({
      form_dialog: {...props.formDialog},
      dialog_visible: props.isVisible,
      listLoading: false,

      rules: {
        groupName: [{ required: true, trigger: "change", validator: validateGroupName }]
      },
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          saveOrUpdateUserGroup({ ...that.form_dialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            that.listLoading = false;
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
}
</script>

<style scoped>

</style>
