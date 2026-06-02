<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" append-to-body disabledLoading width="520" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px" @submit.native.prevent>
          <el-form-item prop="name">
            <template #label>{{ formDialog.type === 1 ? '文件名称：' : '图模名称：' }}</template>
            <el-input v-model="formDialog.name" maxlength="16" placeholder="请输入" show-word-limit type="text"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {someCharMap} from "@/utils/validate";
import {saveGraph} from "@/api/2DVisualization/2d_drawManagement";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AddFileAndFolderDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "新建文件夹"
    },
    activeEditInfo: {
      type: Object,
      default: ()=>{
        return {}
      }
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateName = (rule, value, callback) => {
      if (!value || !someCharMap(value)) {
        callback(new Error("请输入正确的文件名称"));
      } else {
        callback();
      }
    }

    const that = reactive({
      listLoading: false,
      dialog_visible: props.isVisible,
      // 编辑类型 1-新增 2-移动 3-重命名 4-锁定状态 5-保存 6-发布
      formDialog: { type: 1,updateType: 1 },
      rules: {
        name: [{required: true, trigger: "change", validator: validateName}],
      }
    })

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          // console.log(formDialog);

          // 新建或编辑图模数据
          saveGraph({ ...formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", message: "操作成功", showClose: true });
          }).catch(()=>{
            that.listLoading = false;
          })

        }
      })
    }

    const initParamConfigFun = () => {
      that.formDialog = Object.assign({}, that.formDialog, props.activeEditInfo);
      // console.log(that.formDialog);
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

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut, formDialogRef}
  }
})
</script>

<style lang="scss" scoped>

</style>