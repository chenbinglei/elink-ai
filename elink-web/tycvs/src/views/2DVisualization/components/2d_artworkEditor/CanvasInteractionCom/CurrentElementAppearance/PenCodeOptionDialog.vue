<template>
  <div class="penCodeOptionClass">
    <el-drawer v-model="dialog_visible" direction="rtl" size="520">
      <template #header>
        <div class="header_class">echarts配置(echarts)</div>
      </template>

      <div class="drawer_content">
        <div class="content_top scrollbarStyle">
          <CodeEditor ref="codeEditorRef" v-model:value="codeEditorValue"></CodeEditor>
        </div>
        <div class="content_bottom">
          <el-button class="cancelBut" @click="clickCancelBut">取消</el-button>
          <el-button class="confirmBut" type="primary" @click="clickConfirmBut">确认</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import CodeEditor from "@/components/component/CodeEditor.vue";
import {reactive, toRefs, watch, getCurrentInstance, defineComponent, onMounted} from 'vue';

export default defineComponent({
  name: "PenCodeOptionDialog",
  components: {CodeEditor},
  props: {
    isVisible: {
      type: Boolean,
      default: false,
    },
    activeEditInfo: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: ["update:isVisible", "changeEvent"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      listLoading: false,
      codeEditorValue: "",
      dialog_visible: props.isVisible,
    })

    const clickCancelBut = ()=>{
      that.dialog_visible = false;
    }

    const clickConfirmBut = ()=>{
      try {
        const codeEditorValue = JSON.parse(that.codeEditorValue);
        // console.log(codeEditorValue);
        emit("changeEvent", { type:"penCodeOptionDialog",codeValue: codeEditorValue });
        that.dialog_visible = false;
      } catch (e) {
        ElMessage({ type: "error", message: "数据格式错误,请检查！", showClose: true });
      }
    }

    const watchVisible = watch(() => props.isVisible, (newVisible) => {
      that.dialog_visible = newVisible;
    })

    const watchDialogVisible = watch(() => that.dialog_visible, (newDialogVisible) => {
      emit("update:isVisible", newDialogVisible);
    })

    onMounted(() => {
      try {
        const codeEditorValue = JSON.parse(JSON.stringify(props.activeEditInfo));
        that.codeEditorValue = JSON.stringify(codeEditorValue, null, 2);
      } catch (e) {
        that.codeEditorValue = "{}";
      }
      // console.log(that.codeEditorValue);
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, clickCancelBut, clickConfirmBut}
  }
})
</script>

<style lang="scss" scoped>
.penCodeOptionClass {

  :deep(.el-drawer) {
    --el-drawer-padding-primary: 12px;

    .el-drawer__header {
      margin-bottom: 0;
    }

    .el-drawer__body{
      padding-left: 0;
      padding-right: 0;
    }
  }

  .drawer_content {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;

    .content_top{
      flex: 1;
      overflow-y: auto;

      :deep(.cm-editor) {
        height: 100%;
      }
    }

    .content_bottom {
      display: flex;
      justify-content: flex-end;
      margin-top: 12px;
      padding: 0 12px;
      box-sizing: border-box;
    }
  }
}
</style>