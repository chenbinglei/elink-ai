<template>
  <div class="fileShowView">
    <!--    展示json对象 或者 md 文件-->
    <JsonViewer v-if="file_type === 'json'" v-model:value="file_content" :copyable="copyable" :expand-depth="expandDepth" sort></JsonViewer>
    <v-md-preview v-if="file_type === 'md'" :text="file_content"></v-md-preview>
  </div>
</template>
<script>
import JsonViewer from 'vue-json-viewer';
import { reactive, toRefs, watch, defineComponent } from "vue";

import VMdPreview from '@kangc/v-md-editor/lib/preview';
import '@kangc/v-md-editor/lib/style/preview.css';
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js';
import '@kangc/v-md-editor/lib/theme/style/github.css';
import hljs from 'highlight.js'; // highlightjs
VMdPreview.use(githubTheme, { Hljs: hljs });

export default defineComponent({
  name: "FileShowView",
  components: { JsonViewer,VMdPreview },
  props: {
    fileType: {
      type: String,
      default: "json"
    },
    fileContent: {
      type: String,
      default: ""
    }
  },
  setup(props) {
    const that = reactive({
      expandDepth: 999,
      file_content: "",
      file_type: props.fileType,
      copyable: { copyText: "复制" },
    });

    const watchFile = watch([() => props.fileType, () => props.fileContent], ([newFileType, newFileContent]) => {
      that.file_type = newFileType;
      if(that.file_type === 'md')that.file_content = newFileContent;
      if(that.file_type === 'json') that.file_content = newFileContent ? JSON.parse(newFileContent) : newFileContent;
    }, { deep: true,immediate: true });

    return { ...toRefs(that), watchFile };
  }
});
</script>

<style lang="scss" scoped>
.fileShowView {
  width: 100%;
  height: 100%;
  overflow-y: auto;

  :deep(.jv-container){
    padding: 0;
    color: #FFFFFF;
    background: none;

    .jv-code{
      padding: 0 !important;
      max-height: initial !important
    }

    .jv-key{
      color: #C7E3F3CC;
    }

    .jv-object,.jv-array{
      color: #BD7800FF !important;
    }

    .jv-button{
      color: #00F7FFFF;
    }
  }
}
</style>
