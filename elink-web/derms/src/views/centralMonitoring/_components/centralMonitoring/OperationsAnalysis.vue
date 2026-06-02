<template>
  <div class="operationsAnalysis">
    {{ siteId }},{{ code }}
  </div>
</template>

<script>
import { defineComponent } from "vue";

export default defineComponent({
  name: "OperationsAnalysis",
  props: {
    siteId: {
      type: [Number, String],
      default: ""
    },
    code: {
      type: [Number, String],
      default: ""
    },
  },
  setup() {

  }
});
</script>

<style lang="scss" scoped>
.operationsAnalysis {
  color: #FFFFFF;
}
</style>