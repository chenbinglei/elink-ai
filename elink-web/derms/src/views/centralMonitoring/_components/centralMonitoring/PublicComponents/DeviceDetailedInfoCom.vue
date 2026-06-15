<template>
  <el-row class="content_body">
    <template v-for="(item,index) in deviceFieldList" :key="index">
      <el-col :lg="6" :md="8" :sm="12" :xl="4" :xs="24">
        <div class="content_body_li">
          <div class="content_body_li_title">{{ item.name }}</div>
          <div class="content_body_li_number">
            <span class="number">{{ $filters.numberValue(return_data_info[item.fieldName]) }}</span>
            <template v-if="item.unit">
              <span class="unit">{{ $filters.numberUnit(return_data_info[item.fieldName],item.unit) }}</span>
            </template>
            <template v-else>
              <span v-if="return_data_info[item.fieldName] >= 10000" class="unit">万</span>
            </template>

            <template v-if="item.nextFieldName">
              <span class="splitSymbol">{{ item.splitSymbol }}</span>
              <span class="number">{{ $filters.numberValue(return_data_info[item.nextFieldName]) }}</span>
              <template v-if="item.nextUnit">
                <span class="unit">{{ $filters.numberUnit(return_data_info[item.nextFieldName],item.nextUnit) }}</span>
              </template>
              <template v-else>
                <span v-if="return_data_info[item.nextFieldName] >= 10000" class="unit">万</span>
              </template>
            </template>

          </div>
        </div>
      </el-col>
      <el-col v-if="index !== 4" :lg="0" :md="0" :sm="0" :xl="1" :xs="0"></el-col>
    </template>
  </el-row>
</template>

<script lang="ts">
import {reactive, defineComponent, toRefs, watch} from "vue";

export default defineComponent({
  name: "DeviceDetailedInfoCom",
  props: {
    deviceFieldList:{
      type: Array,
      default: ()=> []
    },
    returnDataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {
    const that = reactive({
      return_data_info: {},
    });

    const watchReturnDataInfo = watch(() => props.returnDataInfo, (newReturnDataInfo) => {
      that.return_data_info = JSON.parse(JSON.stringify(newReturnDataInfo ?? {}));
    }, {deep: true});

    return {...toRefs(that), watchReturnDataInfo};
  }
});
</script>

<style lang="scss" scoped>
.content_body_li {
  margin-bottom: 14px;
  text-align: center;

  .content_body_li_title {
    font-size: 14px;
    color: #ffffffcc;
    margin-bottom: 8px;
  }

  .content_body_li_number {
    color: #08f7fd;
    font-size: 12px;
    font-weight: 700;
  }
}
</style>