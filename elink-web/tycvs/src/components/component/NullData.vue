<template>
  <div class="nullData">
    <img alt="" class="noCartImages" src="@/assets/image/moduleNull.png"/>
    <div class="textFont" v-if="isWords">{{ words }}</div>
    <div class="content_bottom"><slot name="content"></slot></div>
  </div>
</template>

<script lang="ts">
import {toRefs, reactive, defineComponent} from "vue";

export default defineComponent({
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
});
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
    max-width: 185px;
    margin-bottom: 4px;
  }

  .textFont {
    color: #121C3F;
    font-size: 18px;
    line-height: normal;
  }
}
</style>
