<template>
  <div class="webView">
    <iframe :src="iframe_src" class="iframe_class flex-jc-ai-center" frameborder="0" @load="iframeLoaded">
      抱歉，您的浏览器不支持内嵌框架！！
    </iframe>
    <div class="mask_class" v-if="loading"></div>
  </div>
</template>

<script>
import {reactive, defineComponent, toRefs, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "WebView",
  props: {
    src: {
      type: String,
      default: ""
    },
    loading: {
      type: Boolean,
      default: false
    },
  },
  emits: ["update:loading"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      iframe_src: props.src,
    });

    const iframeLoaded = () => {
      emit("update:loading", false);
    }

    const watchSrc = watch(() => props.src, (newSrc) => {
      that.iframe_src = newSrc;
    }, {deep: true})

    return {...toRefs(that), watchSrc, iframeLoaded};
  }
})
</script>

<style lang="scss" scoped>
.webView {
  width: 100%;
  height: 100%;
  position: relative;

  .iframe_class{
    width: 100%;
    height: 100%;
    color: #989898;
    font-size: 14px;
    box-sizing: border-box;
  }

  .mask_class{
    width: 100%;
    height: 100%;
    background-color: #081A30;
    position: absolute;
    left: 0;
    top: 0;
  }
}
</style>