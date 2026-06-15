<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName" width="620px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="form_dialog" :rules="rules" label-width="130px">
          <el-form-item v-if="titleName === '添加至组织'" label="选择用户：" prop="userIds">
            <el-tree-select class="leftArrowClass" v-model="form_dialog.userIds" :data="userIds_array" :indent="0" multiple filterable
                            collapse-tags :max-collapse-tags="1" :props="treeProps" node-key="id" placeholder="请选择用户"
                            default-expand-all :render-after-expand="false" show-checkbox highlight-current/>
          </el-form-item>
          <el-form-item label="选择组织：" prop="organId">
            <el-select v-model="form_dialog.organId" clearable placeholder="请选择所属组织">
              <el-option v-for="item in organIdArray" :key="item.id" :label="item.organName" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {Delete, Plus} from "@element-plus/icons-vue";
import {updateUserOrganStructure} from "@/api/systemManagement/userManagement";
import {findOrganStructureListByTenantId} from "@/api/systemManagement/userGroupManagement";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AddToOrganDialog",
  components:{Plus,Delete},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: ""
    },
    formDialog: {
      type: Object,
      default: () => {
        return {}
      }
    },
    // 用户
    userIdsArray: {
      type: Array,
      default: []
    },
  },
  setup(props) {
    const validateUserIds = (rule, value, callback) => {
      if(!value){
        callback(new Error("请选择用户"));
      } else {
        callback();
      }
    };
    const validateOrganId = (rule, value, callback) => {
      if(!value){
        callback(new Error("请选择组织"));
      } else {
        callback();
      }
    };
    const {emit} = getCurrentInstance();
    const that = reactive({
      organIdArray: [], // 组织数组
      listLoading: false,
      dialog_visible: props.isVisible,
      userIds_array: props.userIdsArray,
      form_dialog: {...props.formDialog},
      treeProps:{value: 'id', label: 'label', children: 'children'},
      rules: {
        userIds: [{ required: true, trigger: "change", validator: validateUserIds }],
        organId: [{ required: true, trigger: "change", validator: validateOrganId }]
      }
    })

    // 根据租户id查询组织架构信息列表
    const queryOrganStructureListByTenantId = () => {
      findOrganStructureListByTenantId({timer: new Date() }).then( res=> {
         that.organIdArray = res.data ? res.data : [];
      });
    }

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          for(let key in that.form_dialog)that.form_dialog[key] = that.form_dialog[key].toString();
          that.listLoading = true;
          updateUserOrganStructure({...that.form_dialog}).then(()=>{
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

    onMounted(() => {
      queryOrganStructureListByTenantId();
    })

    return { ...toRefs(that),watchVisible,watchDialogVisible,formDialogRef,saveDialog, queryOrganStructureListByTenantId}
  }
})
</script>

<style scoped>

</style>
