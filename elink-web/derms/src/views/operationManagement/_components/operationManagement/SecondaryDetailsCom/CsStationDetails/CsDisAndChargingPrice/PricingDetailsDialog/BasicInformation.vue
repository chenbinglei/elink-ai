<template>
  <el-row :gutter="12" class="content_list">
    <template v-for="(child,i) in list" :key="i">
      <el-col :span="8" class="info_li">
        <div class="flex_li_left">{{ child.name }}：</div>
        <div class="flex_li_right">
          <span v-if="child.filterName">{{ $filters[child.filterName](returnDataInfo[child.fieldName]) }}</span>
          <span v-else>{{ $filters.moreData(returnDataInfo[child.fieldName]) }}</span>
        </div>
      </el-col>
    </template>
  </el-row>
</template>
<script>
import {defineComponent, reactive, toRefs} from "vue";

export default defineComponent({
  name: "BasicInformation",
  props: {
    returnDataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup() {

    const that = reactive({

      list: [
        {name: "生效时间", fieldName: "takeTime", filterName: "", unit: ""},
        {name: "生效设备", fieldName: "deviceType", filterName: "pileDeviceType", unit: ""},
        {name: "定价方式", fieldName: "fixedType", filterName: "fixedType", unit: ""},
      ]
    });

    return {...toRefs(that)};
  }
});
</script>
<style lang="scss" scoped>
.content_list {
  width: 100%;
  margin-bottom: 12px;

  .info_li {
    display: flex;
    align-items: center;
    margin-bottom: 24px;

    .flex_li_left {
      color: #d3ecfb;
      font-size: 14px;
    }

    .flex_li_right {
      color: #d3ecfb;
      font-size: 14px;
      display: flex;
      align-items: center;
    }
  }
}
</style>