<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName" width="620px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="form_dialog" :rules="rules" label-width="130px">
          <el-form-item v-if="titleName === '添加至用户组'" label="选择用户：" prop="userIds">
            <el-tree-select class="leftArrowClass" v-model="form_dialog.userIds" :data="userIds_array" :indent="0" multiple
                            collapse-tags :max-collapse-tags="1" :props="treeProps" node-key="id" placeholder="请选择用户"
                            default-expand-all :render-after-expand="false" show-checkbox highlight-current/>
          </el-form-item>
          <el-form-item label="选择用户组：" prop="groupIds">
            <el-tree-select class="leftArrowClass" v-model="form_dialog.groupIds" :data="groupIdsArray" :indent="0" multiple
                            collapse-tags :max-collapse-tags="1" :props="treeProps" node-key="id" placeholder="请选择用户组"
                            default-expand-all :render-after-expand="false" show-checkbox highlight-current/>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {getDataListFun} from "@/utils";
import {Delete, Plus} from "@element-plus/icons-vue";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch} from "vue";
import {findUserGroupListById, updateUserGroup} from "@/api/systemManagement/userManagement";

export default {
  name: "AddToUserGroup",
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
      if(!value || !value.length){
        callback(new Error("请选择用户"));
      } else {
        callback();
      }
    };
    const validateGroupIds = (rule, value, callback) => {
      if(!value || !value.length){
        callback(new Error("请选择用户组"));
      } else {
        callback();
      }
    };
    const {emit} = getCurrentInstance();
    const that = reactive({
      form_dialog: {...props.formDialog},
      dialog_visible: props.isVisible,
      userIds_array: props.userIdsArray,
      groupIdsArray: [], // 用户组数组
      listLoading: false,
      rules: {
        userIds: [{ required: true, trigger: "change", validator: validateUserIds }],
        groupIds: [{ required: true, trigger: "change", validator: validateGroupIds }]
      },
      treeProps:{
        value: 'id',
        label: 'label',
        children: 'children'
      },
    })

    // 查询租户下用户组列表
    const getUserGroupList = () => {
      findUserGroupListById({ date: new Date()}).then(res => {
        let resData = res.data ? res.data : [],isIdArr = [];
        resData.forEach(item => {
          if(item.id)isIdArr.push(item);
        });
        that.groupIdsArray = isIdArr && isIdArr.length ? getDataListFun(isIdArr,true,false,"groupName") : [];
      })
    }

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          for(let key in that.form_dialog)that.form_dialog[key] = that.form_dialog[key].toString();
          if(that.form_dialog.groupIds)that.form_dialog.groupIds = that.form_dialog.groupIds + ",";
          that.listLoading = true;
          updateUserGroup({...that.form_dialog}).then(()=>{
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
      getUserGroupList();
    })

    return { ...toRefs(that),watchVisible,watchDialogVisible,formDialogRef,saveDialog, getUserGroupList
    }
  }
}
</script>

<style scoped>

</style>
