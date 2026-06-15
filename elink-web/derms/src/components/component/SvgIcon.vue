<template>
  <svg :class="['svg-icon', $attrs.class]" aria-hidden="true">
    <use :xlink:href="'#icon-' + iconName" :fill="color" />
  </svg>
</template>

<script lang="ts">
import { toRefs, reactive, defineComponent } from "vue";

export default defineComponent({
  name: "SvgIcon",
  props: {
    iconName: {
      type: String,
      default: "",
      require: true,
    },
    className: {
      type: String,
      default: ""
    },
    color: {
      type: String,
      default: ""
    }
  }
});
</script>
<style scoped>
.svg-icon {
  width: 1em;
  height: 1em;
  vertical-align: -0.15em;
  fill: currentColor;
  overflow: hidden;
}
</style>
