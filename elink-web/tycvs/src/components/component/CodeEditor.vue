<template>
  <codemirror v-model="code" :autofocus="autofocus" :extensions="extensions" :indent-with-tab="indentWithTab" :keyMap="keyMap" :placeholder="placeholder"
              :style="{ height: '400px' }" :tabSize="tabSize" @focus="codemirrorFocus" @blur="codemirrorBlur" @change="codemirrorChange"/>
  <!--  @ready="log('ready', $event)" @change="log('change', $event)" @focus="log('focus', $event)" @blur="log('blur', $event)"-->
</template>

<script lang="ts">
import {Codemirror} from "vue-codemirror";
import {oneDark} from "@codemirror/theme-one-dark";
import {javascript} from "@codemirror/lang-javascript";
import {reactive, toRefs, defineComponent, getCurrentInstance, computed, onUnmounted} from "vue";
import { useMeta2dStore } from '@/stores/index';


export default defineComponent({
  name: "CodeEditor",
  components: {Codemirror},
  props: {
    value: {
      type: String,
      default: ''
    },
    placeholder: {
      type: String,
      default: 'Code gose here...'
    },
  },
  emits: ["update:value"],
  setup(props) {
    const meta2dStore = useMeta2dStore();
    const {emit} = getCurrentInstance();

    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    const that = reactive({
      locked: 0,
      code: props.value,
      mode: 'application/json',

      tabSize: 2,  // tab的空格个数
      autofocus: true,
      keyMap: 'sublime',  // sublime编辑器效果
      indentWithTab: true,
      extensions: [javascript(), oneDark],
    });

    const codemirrorFocus = ()=>{
      try {
        const {locked} = canvasMeta2d.value.store.data;
        that.locked = locked;
      } catch (e) {}
      canvasMeta2d.value?.lock(2);
    }

    const codemirrorChange = () => {
      // console.log(that.code)
      // emit('update:value', that.code);
    }

    // 失去焦点时,使用已编辑的代码
    const codemirrorBlur = () => {
      emit('update:value', that.code);
      canvasMeta2d.value?.lock(that.locked);
    }

    onUnmounted(()=>{
      codemirrorBlur();
    })

    return {...toRefs(that), codemirrorChange, codemirrorBlur, codemirrorFocus, canvasMeta2d};
  },
});
</script>
