<template>
  <div class="alarmMonitoring">
    {{ siteId }},{{ code }}
  </div>
</template>

<script>
import {reactive, defineComponent, toRefs, onMounted} from "vue";

export default defineComponent({
  name: "AlarmMonitoring",
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
.alarmMonitoring {
  color: #FFFFFF;
}
</style>