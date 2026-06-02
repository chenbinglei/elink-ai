<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listloading" :title="titleName" width="720px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form :model="form_dialog" :rules="rules" ref="formDialogRef" :disabled="formDialogStatus" label-width="90px">
          <el-form-item label="产品名称:" prop="productName">
            <el-input type="text" v-model="form_dialog.productName" maxlength="20" show-word-limit placeholder="请输入产品名称"></el-input>
          </el-form-item>
          <el-form-item label="客户端id:" prop="clientId">
            <el-input type="text" v-model="form_dialog.clientId" maxlength="20" show-word-limit placeholder="请输入客户端id"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import Draggable from "vuedraggable";
import {ElMessage} from "element-plus";
import {commonCharName} from "@/utils/validate";
import {saveOrUpdateProduct} from "@/api/configCenter/authorityManagement";
import {getCurrentInstance, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AlterFormDialog",
  components: {Draggable},
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
    }
  },
  setup(props) {
    const validateProductName = (rule, value, callback) => {
      if (!commonCharName(value)) {
        callback(new Error("请输入正确的产品名称"));
      } else {
        callback();
      }
    };
    const validateClientId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入正确的客户端id"));
      } else {
        callback();
      }
    };
    const {emit} = getCurrentInstance();

    const formDialogRef = ref(null);
    const that = reactive({
      dialog_visible: props.isVisible,
      form_dialog: props.formDialog, //模块列表,
      // 新增产品，模块
      formDialogStatus: false,
      rules: {
        clientId: [{required: true, trigger: "change", validator: validateClientId}],
        productName: [{required: true, trigger: "change", validator: validateProductName}]
      },
    })

    // 保存产品模块
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          saveOrUpdateProduct(that.form_dialog).then(() => {
            emit("listArray");
            that.dialog_visible = false;
            ElMessage({type: "success", showClose: true, message: "保存成功！"});
          })
        }
      });
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    return { ...toRefs(that), watchVisible, watchDialogVisible, saveDialog, formDialogRef }
  }
})
</script>

<style scoped lang="scss">
.maxHeight {
  width: 100%;
  max-height: 260px;
  padding-right: 8px;
  overflow: auto;
  cursor: move;

  .text-center{
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .iconRedColor {
    color: #FF0000;
  }
}

.iconfont {
  display: block;

  &::before {
    margin-right: 5px;
  }
}
</style>
