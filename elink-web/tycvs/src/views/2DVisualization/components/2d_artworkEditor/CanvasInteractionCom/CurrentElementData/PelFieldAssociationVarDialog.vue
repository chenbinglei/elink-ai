<template>
  <Dialog v-model:isVisible="dialog_visible" :manualEnterClose="false" :title="titleName" append-to-body width="580" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main" v-loading="listLoading">
        <el-form ref="formDialogRef" :disabled="formDialog.keywords" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="数据名：" prop="label">
            <el-input v-model="formDialog.label" placeholder="请输入数据名" type="text"></el-input>
          </el-form-item>
          <el-form-item label="属性名：" prop="key">
            <el-input v-model="formDialog.key" placeholder="请输入属性名" type="text"></el-input>
          </el-form-item>
          <el-form-item label="类型：">
            <el-select v-model="formDialog.type" clearable placeholder="请选择类型">
              <el-option v-for="item in dataTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: 'PelFieldAssociationVarDialog',
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeEditInfo: {
      type: Object,
      default: () => {
        return {}
      }
    },
  },
  emits:["update:isVisible","changeEvent"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateLabel = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入数据名"));
      } else {
        callback();
      }
    };

    const validateKey = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入属性名"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      listLoading: false,
      titleName: "编辑动态数据",
      dialog_visible: props.isVisible,
      dataTypeArray: [
        {id: "string", name: "字符串"},
        {id: "integer", name: "整数"},
        {id: "float", name: "浮点数"},
        {id: "bool", name: "布尔"},
        {id: "object", name: "对象"},
        {id: "array", name: "数组"},
      ],
      rules: {
        label: [{required: true, trigger: "change", validator: validateLabel}],
        key: [{required: true, trigger: "change", validator: validateKey}],
      }
    })

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          ElMessage({type: "success", message: "操作成功", showClose: true});
          emit("changeEvent", that.formDialog);
          that.dialog_visible = false;
        }
      })
    }

    const initParamConfigFun = () => {
      that.formDialog = Object.assign({}, props.activeEditInfo, that.formDialog);
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
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