<template>
  <Dialog v-model:isVisible="dialog_visible" :title="titleName" width="620" @confirm="clickEnterBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-radio-group v-model="titleType" @change="changeTitleType">
          <el-radio v-for="(item,index) in titleTypeArray" :key="index" :value="item.id">{{ item.name }}</el-radio>
        </el-radio-group>
        <div class="dialog-main-bottom">
          <div v-if="titleType === 2" class="functionText">function tooltip(pen) {</div>
          <CodeEditor ref="codeEditorRef" v-model:value="codeEditorValue" :placeholder="placeholder"></CodeEditor>
          <template v-if="titleType === 2">
            <div class="functionText">)</div>
            <div class="tip_class">支持Markdown格式</div>
          </template>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script>
import CodeEditor from "@/components/component/CodeEditor.vue";
import {reactive, toRefs, watch, getCurrentInstance, defineComponent, onMounted} from 'vue';

export default defineComponent({
  name: "PenTooltipDialog",
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
  components: {CodeEditor},
  emits: ["update:isVisible", "changeEvent"],
  setup(props) {

    const {emit} = getCurrentInstance();

    const that = reactive({
      titleType: 1,
      placeholder: "",
      codeEditorValue: "",
      titleName: "鼠标提示",
      dialog_visible: props.isVisible,
      titleTypeArray: [{id: 1, name: "文字"}, {id: 2, name: "函数"}],

      textPlaceholder: "请输入提示语",
      funPlaceholder: String("例如：return `${pen.name}<br/>${pen.text}`;"),
    })

    const changeTitleType = () => {
      that.placeholder = that.titleType === 1 ? that.textPlaceholder : that.funPlaceholder;
    }

    const clickEnterBut = () => {
      let data = {title: "", titleFnJs: null};
      if (that.titleType === 1) data['title'] = that.codeEditorValue;
      if (that.titleType === 2) data['titleFnJs'] = that.codeEditorValue;
      emit("changeEvent", {type: "penTooltipDialog", ...data});
    }

    const watchVisible = watch(() => props.isVisible, (newVisible) => {
      that.dialog_visible = newVisible;
    })

    const watchDialogVisible = watch(() => that.dialog_visible, (newDialogVisible) => {
      emit("update:isVisible", newDialogVisible);
    })

    onMounted(() => {
      that.titleType = props.activeEditInfo.titleFnJs ? 2 : 1;
      that.codeEditorValue = props.activeEditInfo.title || props.activeEditInfo.titleFnJs;
      changeTitleType();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, clickEnterBut, changeTitleType}

  }
})
</script>


<style lang="scss" scoped>
.dialog-main-bottom {
  margin-top: 10px;

  .functionText {
    color: #8c8c8c;
    font-size: 12px;
    margin-bottom: 2px;
  }

  .tip_class {
    color: #bfbfbf;
    font-size: 12px;
  }

  :deep(.cm-editor){
    height: 280px;
  }
}
</style>