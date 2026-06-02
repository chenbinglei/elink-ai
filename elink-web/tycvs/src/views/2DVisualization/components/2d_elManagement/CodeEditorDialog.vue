<template>
  <Dialog v-model:isVisible="dialog_visible" :manualEnterClose="false" title="绑定数据" width="680px" @confirm="clickEnterBut">
    <template v-slot:content>
      <div class="dialog-main">
        <CodeEditor ref="codeEditorRef" v-model:value="codeEditorValue"></CodeEditor>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import CodeEditor from "@/components/component/CodeEditor.vue";
import {reactive, toRefs, watch, getCurrentInstance, defineComponent, onMounted} from 'vue';

export default defineComponent({
  name: "CodeEditorDialog",
  components:{ CodeEditor },
  props: {
    isVisible: {
      type: Boolean,
      default: false,
    },
    codeValue: {
      type: String,
      default: '',
    },
  },
  emits: ["update:codeValue","update:isVisible"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      codeEditorValue: "",
      dialog_visible: props.isVisible,
    })

    const clickEnterBut = () => {
      ElMessage({type: "success", showClose: true, message: "操作成功！"});
      emit("update:codeValue", that.codeEditorValue);
      that.dialog_visible = false;
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    })

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    })

    onMounted(()=>{
      if(props.codeValue){
        that.codeEditorValue = JSON.stringify(JSON.parse(props.codeValue),undefined,2);
      }
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, clickEnterBut}
  }
})
</script>

<style scoped lang="scss">

</style>
