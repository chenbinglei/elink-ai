<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="680px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="form_dialog" :rules="rules" label-width="130px">
          <el-form-item label="模型名称：" prop="modelName">
            <el-input v-model="form_dialog.modelName" maxlength="32" placeholder="请输入模型名称" show-word-limit type="text"></el-input>
          </el-form-item>
          <template v-if="!ifAlter">
            <el-form-item label="设备类型：" prop="typeId">
              <el-tree-select v-model="form_dialog.typeId" :data="handleMenuArray" :indent="0" :props="treeProps" :render-after-expand="false"
                              class="leftArrowClass" default-expand-all filterable placeholder="请选择设备类型" @change="typeChangeFun" />
            </el-form-item>
<!--            <el-form-item label="枪数量：" prop="gunNum" v-if="form_dialog.isChargingGun">-->
<!--              <el-input-number v-model="form_dialog.gunNum" :min="1" :max="100" controls-position="right"/>-->
<!--            </el-form-item>-->
          </template>
          <el-form-item label="模型描述：">
            <el-input v-model="form_dialog.modelDesc" :rows="3" maxlength="200" placeholder="请输入模型描述" show-word-limit type="textarea"/>
          </el-form-item>
          <el-form-item label="logo：">
            <UploadPicturesCom ref="fileLogoListRef" v-model:fileArray="logoFileList">
              <template #tip_content>
                <div class="alter_text">此图用于模型的LOGO图片展示，上传1张，不超过2M</div>
              </template>
            </UploadPicturesCom>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {setTreeData} from "@/utils";
import {ElMessage} from "element-plus";
import {someCharmap} from "@/utils/validate";
import {saveModel,getAssetTypeList} from "@/api/modelCenter/modelManagement";
import UploadPicturesCom from "@/components/component/UploadPicturesCom.vue";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "CreateModelDialog",
  components: {UploadPicturesCom},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    ifAlter: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "创建模型"
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

    const validateModelName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的模型名称"));
      } else {
        callback();
      }
    };

    const validateDeviceTypeId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择设备类型"));
      } else {
        callback();
      }
    };

    // const validateGunNum = (rule, value, callback) => {
    //   if (!value) {
    //     callback(new Error("请输入枪数量"));
    //   } else {
    //     callback();
    //   }
    // };

    const that = reactive({
      form_dialog: {},
      listLoading: false,
      dialog_visible: props.isVisible,

      logoFileList: [],
      modelTypeArray: [],
      handleMenuArray: [],
      allHandleMenuArray: [],
      treeProps:{value: 'id', label: 'typeName', children: 'children'},

      rules: {
        modelName: [{required: true, trigger: "change", validator: validateModelName}],
        typeId: [{required: true, trigger: "change", validator: validateDeviceTypeId}],
        // gunNum: [{required: true, trigger: "change", validator: validateGunNum}],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          // 新增编辑模型数据
          that.listLoading = true;
          let formData = new FormData();
          let form_dialog = JSON.parse(JSON.stringify(that.form_dialog));
          for (let i = 0; i < that.logoFileList.length; i++) {
            if (that.logoFileList[i].raw){
              formData.append("logoFile", that.logoFileList[i].raw);
            }
          }
          for (let key in form_dialog) formData.append(key, form_dialog[key]);
          saveModel(formData).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    // 获取资产分类列表
    const queryAssetTypeList = ()=>{
      getAssetTypeList({ timer: new Date(),pageName: "CreateModelDialog" }).then(res=>{
        let assetTypeList = res.data ? res.data : [];
        that.handleMenuArray = setTreeData(assetTypeList);
        that.allHandleMenuArray = JSON.parse(JSON.stringify(assetTypeList));
      })
    }

    const typeChangeFun = ()=>{
      // let findItem = that.allHandleMenuArray.find(item => item.id === that.form_dialog.typeId);
      that.form_dialog.isChargingGun = that.form_dialog.typeId >= 28 && that.form_dialog.typeId <= 30;
    }

    const initParamConfigFun = () => {
      // console.log(props.formDialog);
      let formDialog = JSON.parse(JSON.stringify(props.formDialog));
      if(formDialog.logoPath)that.logoFileList = [{ url: formDialog.logoPath }];
      delete formDialog.logoPath
      that.form_dialog = JSON.parse(JSON.stringify(formDialog));
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      queryAssetTypeList();
      initParamConfigFun();
    })

    return { ...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun, queryAssetTypeList, typeChangeFun }
  }
})
</script>

<style lang="scss" scoped>
.text-center {
  text-align: center;
}
</style>
