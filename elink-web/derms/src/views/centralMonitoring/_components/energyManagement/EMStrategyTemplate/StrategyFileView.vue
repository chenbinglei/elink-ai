<template>
  <Dialog v-model:isVisible="dialog_visible" closeOnClickModal :footerVisible="false" :title="titleName" width="65vw">
    <template v-slot:content>
      <div class="content_body" v-loading="listLoading">
        <FileShowView v-if="fileContent" :fileType="fileType" :fileContent="fileContent"></FileShowView>
        <null-data v-else words="无配置文件"></null-data>
      </div>
    </template>
  </Dialog>

</template>

<script>
import FileShowView from "@/components/uploadFileCom/FileShowView.vue";
import {parseTemplateContent} from "@/api/centralMonitoring/energyManagement";
import { reactive, toRefs, watch, getCurrentInstance, onMounted, defineComponent } from "vue";

export default defineComponent({
  name: "StrategyFileView",
  components:{FileShowView},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName:{
      type: String,
      default: "策略说明"
    },
    // 1: 策略说明  2:配置文件
    templateType:{
      type: Number,
      default: 1
    },
    templateId:{
      type: [String,Number],
      default: ""
    },
  },
  setup(props) {
    const { emit } = getCurrentInstance();

    const that = reactive({
      fileType: "md",
      fileContent: "",
      listLoading: false,
      dialog_visible: props.isVisible,
    });

    const queryParseTemplateContent = ()=>{
      that.listLoading = true;
      parseTemplateContent({ id: props.templateId,type: props.templateType }).then(res=>{
        that.listLoading = false;
        that.fileContent = res.data ? res.data : "";
        that.fileType = props.templateType === 1 ? 'md' : 'json';
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      queryParseTemplateContent();
    });

    return { ...toRefs(that),watchVisible,watchDialogVisible,queryParseTemplateContent };
  }
});
</script>

<style scoped lang="scss">
.content_body{
  width: 100%;
  height: 60vh;
}
</style>
