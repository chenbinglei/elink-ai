<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName" width="680" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form :model="authorityForm_dialog" :rules="rules" ref="authorityFormDialogRef" label-width="130px">
          <el-form-item label="权限类型:" prop="permissionType" v-if="!authority_compile">
            <div class="displayFlex">
              <div class="flex" v-for="(item,index) in authorityTypeArray" :key="index" @click="dialogSelectCompany(item.value)">
                <div class="text">{{ item.label }}:</div>
                <div class="radius" :class="{ active: authorityForm_dialog.permissionType === item.value }">
                  <div class="children"></div>
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="权限名称:" prop="permissionName">
            <el-input type="text" v-model="authorityForm_dialog.permissionName" maxlength="50" show-word-limit placeholder="请输入权限名称"></el-input>
          </el-form-item>
          <el-form-item label="权限编码:" prop="permissionCode">
            <el-input type="text" v-model="authorityForm_dialog.permissionCode" maxlength="16" show-word-limit placeholder="请输入权限编码" :disabled="authority_compile" />
          </el-form-item>
          <el-form-item label="URL:" prop="url">
            <el-input type="text" v-model="authorityForm_dialog.url" maxlength="100" show-word-limit placeholder="请输入权限URL"></el-input>
          </el-form-item>
          <el-form-item label="父级权限:">

<!--            <el-select v-model="authorityForm_dialog.parentId" filterable clearable placeholder="请选择父级权限">-->
<!--              <template v-for="item in tiled_list" :key="item.id">-->
<!--                <template v-if="!item.permissionType || item.permissionType === 1">-->
<!--                  <el-option :label="item.permissionName" :value="item.id" :disabled="item.id===authorityForm_dialog.parentId"></el-option>-->
<!--                </template>-->
<!--              </template>-->
<!--            </el-select>-->

            <el-tree-select v-model="authorityForm_dialog.parentId" :data="tiled_list" filterable clearable check-strictly :render-after-expand="false"
                            class="leftArrowClass" node-key="id" :props="treeSelectProps" />


          </el-form-item>
          <el-form-item label="权限状态:" v-if="authorityForm_dialog.permissionType === 1">
            <el-select v-model="authorityForm_dialog.isHidden" placeholder="请选择权限状态">
              <el-option v-for="item in hiddenArray" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="是否有界面:" v-if="authorityForm_dialog.permissionType === 1">
            <el-select v-model="authorityForm_dialog.isLayout" clearable placeholder="请选择是否有界面">
              <el-option v-for="item in layoutArray" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="目录顺序:">
            <el-input type="text" v-model="authorityForm_dialog.directoryDesc" placeholder="请输入目录顺序"></el-input>
          </el-form-item>
          <el-form-item label="权限图标:" v-if="authorityForm_dialog.permissionType === 1">
            <el-input type="text" v-model="authorityForm_dialog.iconPath" maxlength="48" show-word-limit placeholder="请输入权限图标"></el-input>
          </el-form-item>
          <el-form-item label="页面说明:">
            <el-input type="textarea" v-model="authorityForm_dialog.explanation" :rows="2" maxlength="20" show-word-limit placeholder="请输入页面说明" />
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {databaseName, someCharmap} from "@/utils/validate";
import {saveOrUpdatePermission} from "@/api/configCenter/authorityManagement";
import {getCurrentInstance, reactive, ref, toRefs, watch, defineComponent, onMounted} from "vue";
import {setTreeData} from "@/utils";

export default defineComponent({
  name: "AuthorityCompileDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: ""
    },
    authorityCompile:  {
      type: Boolean,
      default: false
    },
    moduleIdArray: {
      type: Array,
      default: []
    },
    authorityFormDialog: {
      type: Object,
      default: () => {
        return {}
      }
    },
    tiledList: {
      type: Array,
      default: []
    }
  },
  setup(props) {
    const validatePermissionName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的权限名称"));
      } else {
        callback();
      }
    };
    const validatePermissionType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择权限类型"));
      } else {
        callback();
      }
    };
    const validatePermissionCode = (rule, value, callback) => {
      if (!databaseName(value)) {
        callback(new Error("请输入正确的权限编码"));
      } else {
        callback();
      }
    };
    const validateUrl = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入权限URL"));
      } else {
        callback();
      }
    };
    const {emit} = getCurrentInstance();

    const authorityFormDialogRef = ref(null);
    const that = reactive({
      tiled_list: [],
      listLoading: false,
      dialog_visible: props.isVisible,
      authority_compile: props.authorityCompile,
      authorityForm_dialog: {...props.authorityFormDialog},
      layoutArray: [{label: "是", value: 1}, {label: "否", value: 2}],
      hiddenArray: [{label: "显示", value: 0}, {label: "隐藏", value: 1}],
      authorityTypeArray: [{label: "页面", value: 1}, {label: "控件", value: 2}],
      treeSelectProps: {value: "id", label: "permissionName", children: "children"},
      rules: {
        url: [{required: true, trigger: "change", validator: validateUrl}],
        permissionName: [{required: true, trigger: "change", validator: validatePermissionName}],
        permissionType: [{required: true, trigger: "change", validator: validatePermissionType}],
        permissionCode: [{required: true, trigger: "change", validator: validatePermissionCode}],
      },
    })

    // 保存权限
    const saveDialog = () => {
      authorityFormDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let fromDialog = JSON.parse(JSON.stringify(that.authorityForm_dialog));
          saveOrUpdatePermission(fromDialog).then(() => {
            ElMessage({type: "success", showClose: true, message: "保存成功！"});
            emit("changeEvent");
            that.dialog_visible = false;
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      });
    }

    const dialogSelectCompany = (id) => {
      that.authorityForm_dialog.permissionType = id;
    }

    const filterTreeNodeFun = ()=>{
      let newTiledList = [];
      if(props.tiledList && props.tiledList.length){
        for(let i = 0;i < props.tiledList.length; i++){
          if(!props.tiledList[i].permissionType || props.tiledList[i].permissionType === 1){
            newTiledList.push(props.tiledList[i]);
          }
        }
      }
      that.tiled_list = setTreeData(newTiledList);
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      filterTreeNodeFun();
    })

    return { ...toRefs(that), watchVisible, watchDialogVisible, saveDialog, authorityFormDialogRef, dialogSelectCompany, filterTreeNodeFun }
  }
})
</script>

<style scoped lang="scss">
.displayFlex {
  width: 100%;
  display: flex;
  justify-content: space-between;
  padding: 0 24px;
  box-sizing: border-box;

  .flex {
    display: flex;
    cursor: pointer;

    .radius {
      width: 28px;
      height: 28px;
      border: 2px solid #E8F2FF;
      border-radius: 50%;
      position: relative;
      margin-left: 8px;

      .children {
        width: 20px;
        height: 20px;
        background: #E8F2FF;
        border-radius: 50%;
        position: absolute;
        left: 4px;
        top: 4px;
      }
    }

    .active {
      border-color: #1F74E2;

      .children {
        background: #1F74E2;
      }
    }
  }
}
</style>
