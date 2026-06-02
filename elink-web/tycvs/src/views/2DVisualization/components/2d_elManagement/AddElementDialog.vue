<template>
  <Dialog v-model:isVisible="dialog_visible" append-to-body :manualEnterClose="false" :title="titleName" disabledloading :listLoading="listLoading"
          width="550" @confirm="clickEnterBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px" @submit.prevent>

          <el-form-item label="类型：" v-if="update_type === 1">
            <el-select v-model="formDialog.type" placeholder="请选择类型">
              <el-option v-for="item in fileTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item prop="name">
            <template #label>{{ formDialog.type === 1 ? '文件名称：' : '图元名称：' }}</template>
            <el-input v-model="formDialog.name" maxlength="18" placeholder="请输入" show-word-limit type="text"></el-input>
          </el-form-item>

          <template v-if="formDialog.type === 2 && update_type <= 2">
            <el-form-item label="图元类型：" prop="pelType">
              <el-select v-model="formDialog.pelType" clearable placeholder="请选择图元类型">
                <el-option v-for="item in pelTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
            <template v-if="formDialog.pelType">
              <el-form-item :label="formDialog.pelType === 2 ? '上传缩略图：' : '上传文件：'" prop="fileList">
                <UploadPicturesCom v-model:fileArray="formDialog.fileList" accept=".jpg, .png, .gif, .svg" :fileSize="10">
                  <template #tip_content>
                    <span class="tip_content">请选择.svg，.jpg，.png，.gif格式文件</span>
                  </template>
                </UploadPicturesCom>
              </el-form-item>
              <el-form-item>
                <template #label>{{ formDialog.pelType === 2 ? '自定义图元：' : '绑定数据：' }}</template>
                <el-button plain @click="clickCodeEditor">...</el-button>
                <div class="add_data" v-if="formDialog.dataData">
                  <span class="iconfont icon-duihao"></span>
                  <span>已添加</span>
                </div>
              </el-form-item>
            </template>
          </template>
        </el-form>

        <CodeEditorDialog v-if="codeEditorVisible" v-model:isVisible="codeEditorVisible" v-model:codeValue="formDialog.dataData" />
        <CustomPelDialog v-if="customPelVisible" v-model:isVisible="customPelVisible" v-model:codeValue="formDialog.dataData" @changEvent="changEvent" />
      </div>
    </template>
  </Dialog>
</template>

<script>
import {generateUUID} from '@/utils';
import {ElMessage} from 'element-plus';
import {hasWhiteSpace} from "@/utils/validate";
import CustomPelDialog from "./CustomPelDialog";
import CodeEditorDialog from "./CodeEditorDialog";
import UploadPicturesCom from "@/components/component/UploadPicturesCom.vue";
import {saveGraphPel,findPelById} from "@/api/2DVisualization/2d_elManagement";
import {reactive, toRefs, watch, getCurrentInstance, ref, defineComponent, onMounted} from 'vue';

export default defineComponent({
  name: "AddElementDialog",
  components: {UploadPicturesCom,CodeEditorDialog,CustomPelDialog},
  props: {
    isVisible: {
      type: Boolean,
      default: false,
    },
    titleName: {
      type: [String, Number],
      default: '',
    },
    // 编辑类型 1-新增 2-正常编辑 3-重命名 4-移动
    updateType: {
      type: Number,
      default: 1,
    },
    // 当前图元id
    activePelId: {
      type: [String, Number],
      default: '',
    },
    //   1: 文件  2；图元
    operateType: {
      type: Number,
      default: 1,
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateName = (rule, value, callback) => {
      if (!value || hasWhiteSpace(value)) {
        callback(new Error("请输入正确的名称"));
      } else {
        callback();
      }
    };

    const validatePelType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择图元类型"));
      } else {
        callback();
      }
    };

    const validateFileList = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请上传相关文件"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      customPelVisible: false,
      codeEditorVisible: false,
      update_type: props.updateType, // 编辑类型 1-新增 2-正常编辑 3-重命名 4-移动
      dialog_visible: props.isVisible,
      formDialog: { type: props.operateType },
      fileTypeArray: [{id: 1, name: "文件夹"},{id: 2, name: "图元"}],
      pelTypeArray: [{id: 1, name: "文件格式"},{id: 2, name: "自定义图元"}],
      imageData: {"name":"image", "width": 100, "height": 100,"imageRatio": true},
      rules: {
        name: [{required: true, trigger: "change", validator: validateName }],
        pelType: [{required: true, trigger: "change", validator: validatePelType }],
        fileList: [{required: true, trigger: "change", validator: validateFileList }],
      },
    })

    const formDialogRef = ref(null);
    const clickEnterBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formData = new FormData();
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          // console.log(formDialog);

          if(formDialog.type === 2){
            let fileType = "";
            // console.log(that.formDialog.fileList);
            for (let i = 0; i < that.formDialog.fileList.length; i++) {
              if (that.formDialog.fileList[i].raw){
                fileType = that.formDialog.fileList[i].raw.type;
                formData.append("file", that.formDialog.fileList[i].raw);
              } else {
                if (that.formDialog.fileList[i].url){
                  if(that.formDialog.fileList[i].url.indexOf('.svg') === -1){
                    fileType = "image/png";
                  }

                  if(that.formDialog.fileList[i].url.indexOf('.gif') !== -1){
                    fileType = "image/gif";
                  }
                }
              }
            }

            if(formDialog.pelType === 1){
              // 处理文件类型，图片没有绑定数据问题
              if(fileType && fileType.indexOf('image') !== -1){
                let dataData = {};
                that.imageData.name = fileType === "image/gif" ? "gif" : "image";
                if(formDialog.dataData) dataData = JSON.parse(formDialog.dataData);
                let newImageData = Object.assign({},that.imageData,dataData);
                formDialog.dataData = JSON.stringify(newImageData);
              }
            }

            // 绑定数据生成文件 dataData
            if(formDialog.dataData){
              let dataDataBlob = new Blob([formDialog.dataData], {type: 'application/json'});
              let dataFile = new File([dataDataBlob], `${generateUUID()}.json`, {type: 'application/json'});
              formData.append("dataFile", dataFile);
            }

            delete formDialog.fileList // 删除该字段
            delete formDialog.dataData; // 删除该字段
          }


          formData.append("updateType", props.updateType);
          for(let key in formDialog) if(formDialog[key]) formData.append(key, formDialog[key]);

          saveGraphPel(formData).then(()=>{
            that.listLoading = false;
            that.dialog_visible = false;
            emit("changeEvent",{ type:"listArray" });
            ElMessage({type: "success", showClose: true, message: "保存成功！"});
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const changEvent = ()=>{}

    // 根据id查询图元详情数据
    const initParamConfigFun = ()=>{
      that.listLoading = true;
      findPelById({ id: props.activePelId }).then(res=>{
        let formDialog = res.data ? res.data : {};
        if(formDialog.filePath) formDialog.fileList = [{name: formDialog.fileName, url: formDialog.filePath}];
        that.formDialog = JSON.parse(JSON.stringify(formDialog));
        // console.log(that.formDialog);
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    // 打开设置js弹框 或者定义图元弹框
    const clickCodeEditor = () => {
      that.formDialog.pelType === 2 ? that.customPelVisible = true : that.codeEditorVisible = true;
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    })

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    })

    onMounted(()=>{
      if(props.activePelId) initParamConfigFun();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, clickEnterBut,clickCodeEditor, initParamConfigFun, changEvent}
  }
})
</script>

<style scoped lang="scss">
.add_data{
  color: #1F74E2;
  margin-left: 4px;

  .iconfont{
    margin-right: 2px;
  }
}
</style>
