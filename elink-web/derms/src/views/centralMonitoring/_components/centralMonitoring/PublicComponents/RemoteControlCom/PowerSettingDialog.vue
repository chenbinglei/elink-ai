<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" closeOnClickModal disabledLoading width="580" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="100px">
          <el-form-item label="类型：" prop="valueType">
            <el-radio-group v-model="formDialog.valueType">
              <el-radio v-for="(item,index) in valueTypeArray" :key="index" :value="item.id">{{ item.name }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="数值：" prop="bfbValue" v-if="formDialog.valueType === 1">
            <el-input-number v-model="formDialog.bfbValue" :min="0" :max="100" controls-position="right" controls />
          </el-form-item>
          <el-form-item label="数值：" prop="numValue" v-if="formDialog.valueType === 2">
            <el-input-number v-model="formDialog.numValue" controls-position="right" controls />
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, ref, onMounted} from "vue";

export default defineComponent({
  name: "PowerSettingDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "有功功率"
    },
    // 2 有功功率 3 无功功率
    operateType: {
      type: Number,
      default: 2
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateValueType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择类型"));
      } else {
        callback();
      }
    };

    const validateBfbValue = (rule, value, callback) => {
      if (!value && value !== 0) {
        callback(new Error("请输入数值"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      formDialog: { valueType: 1 },
      dialog_visible: props.isVisible,
      valueTypeArray: [{id: 1, name: "百分比"}, {id: 2, name: "目标值"}],
      rules: {
        valueType: [{required: true, trigger: "change", validator: validateValueType }],
        bfbValue: [{required: true, trigger: "change", validator: validateBfbValue }],
        numValue: [{required: true, trigger: "change", validator: validateBfbValue }],
      }
    });


    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
        }
      });
    };

    const initParamConfigFun = () => {
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, clickConfirmBut, formDialogRef, initParamConfigFun};

  }
});
</script>

<style lang="scss" scoped>

</style>
<script setup>
</script>