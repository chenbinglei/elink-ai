<template>
  <Dialog v-model:isVisible="dialog_visible" :confirmVisible="false" :title="titleName" width="620">
    <template v-slot:content>
      <div v-loading="listLoading" class="dialog-main">
        <FileShowView :fileType="fileType" :fileContent="parse_message_content"></FileShowView>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import FileShowView from "@/components/uploadFileCom/FileShowView.vue";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, onMounted} from "vue";

export default defineComponent({
  name: "MessageParsingDialog",
  components: {FileShowView},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeMsgDataInfo: {
      type: Object,
      default: ()=>{
        return {};
      }
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      fileType: "json",
      listLoading: false,
      titleName: "报文解析",
      parse_message_content: "{}",
      copyable: { copyText: '复制' },
      dialog_visible: props.isVisible,
    });

    const initParamConfigFun = () => {
      that.parse_message_content = props.activeMsgDataInfo?.content ?? "{}";
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

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun};
  }
});
</script>

<style lang="scss" scoped>
.dialog-main{
  max-height: 520px;
  overflow-y: auto;
}
</style>