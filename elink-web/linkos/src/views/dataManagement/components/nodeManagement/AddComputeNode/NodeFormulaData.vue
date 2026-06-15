<template>
  <div class="nodeFormulaData">
    <div class="alter_title flex jc-space-between">
      <span class="alter_title_left">公式编辑：</span>
<!--      <span class="alter_title_right">未校验</span>-->
    </div>
    <div class="content_tinymce">
      <TinymceEditor v-model="formula" :api-key="tinymceApiKay" :init="tinymceInit"></TinymceEditor>
    </div>
  </div>
</template>

<script lang="ts">
import TinymceEditor from '@tinymce/tinymce-vue';
import {reactive, toRefs, onMounted, defineComponent} from "vue";

export default defineComponent({
  name: "NodeFormulaData",
  components: {TinymceEditor},
  setup() {

    const that = reactive({
      formula: "",
      tinymceInit: {
        width: "100%",
        height: "100%",
        menubar: false, //自定义菜单
        toolbar: false, //自定义工具栏
        resize: false, // 禁止设置宽高
        statusbar: false, //隐藏状态栏
        language: 'zh_CN', //语言类型
        branding: false,
        plugins: "link lists image code table wordcount nonbreaking",
        init_instance_callback: (editor) => {
          // console.log(editor);
          console.log("tinymce初始化完成！");
          // that.tinymceEditor = editor;
          // editor.on('click',clickTinymceEditor);
        }
      },
      tinymceApiKay: "dvimxjet1wxtpw7mtlljwec6qgj8e426803ilp4c45quxhzx",
    })

    //在onMounted中初始化编辑器
    onMounted(() => {})

    return {...toRefs(that),}
  }
})
</script>

<style lang="scss" scoped>
.nodeFormulaData {
  height: 100%;
  display: flex;
  flex-direction: column;

  .content_tinymce {
    flex: 1;
    margin-top: 12px;
  }
}
</style>
