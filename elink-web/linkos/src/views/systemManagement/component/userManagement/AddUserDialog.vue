<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName" width="620" customClass="marginDialogClass"  @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="form_dialog" :rules="rules" label-width="130px">
          <el-form-item label="角色：" prop="userRole">
            <el-radio-group v-model="form_dialog.userRole">
              <el-radio v-for="(item,index) in userRoleArray" :key="index" :label="item.id">{{ item.name }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="所属组织：" prop="organId">
            <el-tree-select class="leftArrowClass" v-model="form_dialog.organId" :data="organIdArray" :indent="0" :props="organTreeProps"
                            check-strictly node-key="id" placeholder="请选择所属组织" default-expand-all :render-after-expand="false"/>
          </el-form-item>
          <el-form-item label="用户组：">
            <el-tree-select class="leftArrowClass" v-model="form_dialog.groupId" :data="groupIdsArray" :indent="0" multiple collapse-tags
                            :max-collapse-tags="1" :props="treeProps" node-key="id" placeholder="请选择用户组" default-expand-all
                            :render-after-expand="false" show-checkbox />
          </el-form-item>
          <el-form-item label="用户账号：" prop="userAccount">
            <el-input v-model="form_dialog.userAccount" maxlength="32" show-word-limit placeholder="请输入用户账号"/>
          </el-form-item>
          <el-form-item label="密码：" prop="password">
            <el-input v-model="form_dialog.password" placeholder="请输入密码" type="password" show-password/>
          </el-form-item>
          <el-form-item label="请重复密码：" prop="repeatPassword">
            <el-input v-model="form_dialog.repeatPassword" placeholder="请输入密码" type="password" show-password/>
          </el-form-item>
          <el-form-item label="姓名：" prop="fullName">
            <el-input v-model="form_dialog.fullName" maxlength="32" show-word-limit placeholder="请输入姓名"/>
          </el-form-item>
          <el-form-item label="电话：" prop="phone">
            <el-input v-model="form_dialog.phone" maxlength="11" show-word-limit placeholder="请输入电话"/>
          </el-form-item>

          <el-form-item label="上传头像：">
            <UploadPicturesCom v-model:fileArray="fileListArray"></UploadPicturesCom>
          </el-form-item>

          <el-form-item label="状态：" prop="userState">
            <el-switch v-model="form_dialog.userState" :active-value="1" :inactive-value="0"/>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {getDataListFun, setTreeData} from "@/utils";
import {ElMessage, genFileId} from "element-plus";
import {Delete, Plus} from "@element-plus/icons-vue";
import UploadPicturesCom from "@/components/component/UploadPicturesCom.vue";
import {CNENCharmap, isvalidPassword, mobile, someCharmap} from "@/utils/validate";
import {findOrganStructureListByTenantId} from "@/api/systemManagement/userGroupManagement";
import {findUserGroupListById, saveOrUpdateUserInfo} from "@/api/systemManagement/userManagement";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AddUserDialog",
  components:{Plus,Delete,UploadPicturesCom},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "新增用户"
    },
    formDialog: {
      type: Object,
      default: () => {
        return {}
      }
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateUserRole = (rule, value, callback) => {
      if(!value){
        callback(new Error("请选择角色"));
      } else {
        callback();
      }
    };

    const validateFullName = (rule, value, callback) => {
      if(!value || !CNENCharmap(value)){
        callback(new Error("请输入正确的姓名"));
      } else {
        callback();
      }
    };

    const validateUserAccount = (rule, value, callback) => {
      if(!value || !someCharmap(value)){
        callback(new Error("请输入正确的用户账号"));
      } else {
        callback();
      }
    };

    const validatePassword = (rule, value, callback) => {
      if(!value || !isvalidPassword(value)){
        callback(new Error("请输入正确的密码"));
      } else {
        callback();
      }
    };

    const validateRepeatPassword = (rule, value, callback) => {
      if(!value || !isvalidPassword(value)) {
        callback(new Error("请输入正确的密码"));
      } else if(that.form_dialog.password !== that.form_dialog.repeatPassword) {
        callback(new Error("两次密码不一致"));
      } else {
        callback();
      }
    };

    const validatePhone = (rule, value, callback) => {
      if(!value || !mobile(value)){
        callback(new Error("请输入正确的手机号"));
      } else {
        callback();
      }
    };

    const validateOrganId = (rule, value, callback) => {
      if(!value){
        callback(new Error("请选择所属组织"));
      } else {
        callback();
      }
    };

    const that = reactive({
      form_dialog: {},
      listLoading: false,
      dialog_visible: props.isVisible,
      treeProps:{value: 'id', label: 'label', children: 'children'},
      organTreeProps:{value: 'id', label: 'organName', children: 'children'},

      organIdArray: [], // 组织数组
      fileListArray: [],
      groupIdsArray: [], // 用户组数组
      userRoleArray: [{name: "管理员", id: 1}, {name: "普通用户", id: 2}],

      rules: {
        userRole: [{ required: true, trigger: "change", validator: validateUserRole }],
        userAccount: [{ required: true, trigger: "change", validator: validateUserAccount }],
        password: [{ required: true, trigger: "change", validator: validatePassword }],
        repeatPassword: [{ required: true, trigger: "change", validator: validateRepeatPassword }],
        phone: [{ required: true, trigger: "change", validator: validatePhone }],
        fullName: [{ required: true, trigger: "change", validator: validateFullName }],
        organId: [{ required: true, trigger: "change", validator: validateOrganId }],
      },
    })

    // 查询租户下用户组列表
    const getUserGroupList = () => {
      findUserGroupListById({}).then(res => {
        let resData = res.data ? res.data : [],isIdArr = [];
        resData.forEach(item => {
          if(item.id)isIdArr.push(item);
        });

        if(isIdArr && isIdArr.length){
          that.groupIdsArray = getDataListFun(isIdArr,true,false,"groupName")
        }
      })
    }

    // 根据租户id查询组织架构信息列表
    const queryOrganStructureListByTenantId = () => {
      findOrganStructureListByTenantId({ timer: new Date() }).then( res=> {
        let list = setTreeData(res.data ? res.data : []);
        that.organIdArray = JSON.parse(JSON.stringify(list));
      })
    }

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formData = new FormData();
          let form_dialog = JSON.parse(JSON.stringify(that.form_dialog));
          if(form_dialog.groupId && form_dialog.groupId.length)form_dialog.groupId.join(",")

          //字段参数
          for (let key in form_dialog) formData.append(key, form_dialog[key]);
          if(that.fileListArray && that.fileListArray.length){
            for (let i = 0; i < that.fileListArray.length; i++) {
              if (that.fileListArray[i].raw) formData.append("imageFile", that.fileListArray[i].raw);
            }
          }

          saveOrUpdateUserInfo(formData).then(()=>{
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

    const initParamConfigFun = ()=>{
      let formDialog = Object.assign({},props.formDialog,that.form_dialog);
      if(formDialog.userProfile)that.fileListArray = [{ url: formDialog.userProfile }];
      that.form_dialog = JSON.parse(JSON.stringify(formDialog));
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      getUserGroupList();
      initParamConfigFun();
      queryOrganStructureListByTenantId();
    })

    return {...toRefs(that),watchVisible,watchDialogVisible,formDialogRef,saveDialog,getUserGroupList, queryOrganStructureListByTenantId, initParamConfigFun}
  }
})
</script>

<style scoped>

</style>
