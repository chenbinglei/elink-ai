<template>
  <div class="nullData">
    <slot><img alt="" class="noCartImages" src="@/assets/image/moduleNull.png"/></slot>
    <div class="textFont" v-if="isWords">{{ words }}</div>
    <div class="textFont"><slot name="content"></slot></div>
  </div>
</template>

<script lang="ts">
import {toRefs, reactive} from "vue";

export default {
  name: "NullData",
  props: {
    words: {
      type: String,
      default: "暂无数据"
    },
    isWords:{
      type: Boolean,
      default: true
    }
  },
  setup(props) {
    const that = reactive({
      words: props.words,
    });
    return {...toRefs(that)};
  }
};
</script>

<style lang="scss" scoped>
.nullData {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  padding: 20px 0;
  box-sizing: border-box;

  .noCartImages {
    max-width: 130px;
    margin-bottom: 10px;
  }

  .textFont {
    font-size: 14px;
    color: #989898;
    line-height: normal;
  }
}
</style>
