<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" width="520" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="资产分类:" prop="typeId">
            <el-tree-select class="leftArrowClass" v-model="formDialog.typeId" :indent="0" :data="handleMenuArray" :props="treeProps"
                            filterable default-expand-all :render-after-expand="false" />
          </el-form-item>
          <el-form-item label="分类名称:" prop="name">
            <el-input type="text" v-model="formDialog.name" maxlength="32" show-word-limit placeholder="请输入分类名称"></el-input>
          </el-form-item>
          <el-form-item label="分类标识:" prop="code">
            <el-input type="text" v-model="formDialog.code" placeholder="请输入分类标识"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {setTreeData} from "@/utils";
import {ElMessage} from "element-plus";
import {saveGraphType} from "@/api/visualization/graphicClassify";
import {getAssetTypeList} from "@/api/modelCenter/modelManagement";
import {hasWhiteSpace, notCharmap, validateStartLowerCaseAndCompose} from "@/utils/validate";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AddGraphicClassDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加分类"
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

    const validateTypeId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择资产分类"));
      } else {
        callback();
      }
    };

    const validateClassName = (rule, value, callback) => {
      if (!value || !notCharmap(value)) {
        callback(new Error("请输入正确的图形名称"));
      } else {
        callback();
      }
    };

    const validateClassLogo = (rule, value, callback) => {
      if (!value || hasWhiteSpace(value)) {
        callback(new Error("请输入正确的分类标识"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      listLoading: false,
      handleMenuArray: [],
      dialog_visible: props.isVisible,
      treeProps:{value: 'id', label: 'typeName', children: 'children'},

      rules:{
        typeId: [{required: true, trigger: "change", validator: validateTypeId }],
        name: [{required: true, trigger: "change", validator: validateClassName }],
        code: [{required: true, trigger: "change", validator: validateClassLogo }],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          saveGraphType({ ...formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    // 获取设备类型列表
    const queryAssetTypeList = ()=>{
      getAssetTypeList({ timer: new Date(),pageName: "AddGraphicClassDialog" }).then(res=>{
        let assetTypeList = res.data ? res.data : [];
        that.handleMenuArray = setTreeData(assetTypeList);
        that.oldHandleMenuArray = JSON.parse(JSON.stringify(assetTypeList));
      })
    }

    const initParamConfigFun = () => {
      that.formDialog = Object.assign({},props.activeEditInfo,that.formDialog);
      queryAssetTypeList();
    }

    const watchVisible = watch(() => props.isVisible, (newVisible) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch(() => that.dialog_visible, (newDialogVisible) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun, queryAssetTypeList}
  }
})
</script>

<style lang="scss" scoped></style>