<template>
  <div class="miniProgramUserVehiclesCom">
     <!-- <el-empty :image="emptyImg" description="暂无数据" /> -->
       <null-data words="暂无数据"></null-data>
  </div>
</template>

<script lang="ts">
import {defineComponent, reactive, toRefs} from "vue";
import emptyImg from "@/assets/image/empty.png";
export default defineComponent({
  name: 'MiniProgramUserVehiclesCom',
  setup() {
    const that = reactive({});

    return {...toRefs(that)};
  }
});
</script>

<style lang="scss" scoped>
.miniProgramUserVehiclesCom {
  width: 100%;
  height: 86%;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>