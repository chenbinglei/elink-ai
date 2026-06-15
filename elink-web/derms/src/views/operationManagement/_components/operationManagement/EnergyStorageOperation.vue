<template>
  <div class="content_body">
    <null-data words="未开放"></null-data>
  </div>
</template>

<script lang="ts">
import {defineComponent, onMounted, reactive, toRefs} from "vue";

export default defineComponent({
  name: "EnergyStorageOperation",
  setup() {

    const that = reactive({});

    return {...toRefs(that)};
  }
});
</script>

<style lang="scss" scoped>
.content_body{
  height: 100%;
}
</style>