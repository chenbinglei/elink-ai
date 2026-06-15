<template>
  <div class="content_body content_border">
    <el-row :gutter="12">
      <template v-for="(item,index) in list" :key="index">
        <el-col :lg="6" :md="8" :sm="12" :xl="4">
          <div class="content_list">
            <div class="content_list_left flex-ai-center">
              <span :class="item.iconName" class="iconfont"></span>
            </div>
            <div class="content_list_right">
              <div class="list_right_top">{{ item.name }}</div>
              <div class="list_right_bottom">
                <div class="number">{{ $filters.numberValue(return_data_info[item.fieldName]) }}</div>
                <div class="unit">{{ $filters.numberUnit(return_data_info[item.fieldName], item.unit) }}</div>
              </div>
            </div>
          </div>
        </el-col>
      </template>
    </el-row>
  </div>
</template>

<script lang="ts">
import {watch, reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "StatisticsTotalDataInfo",
  props: {
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
      list: [
        {name: '光伏装机容量', fieldName: 'capacity', unit: "kWp", iconName: "icon-guang_fu"},
        {name: '逆变器数量', fieldName: 'inverterNum', unit: "台", iconName: "icon-nibianqi"},
        {name: '发电量', fieldName: 'generation', unit: "kWh", iconName: "icon-fa_dian_liang"},
        {name: '上网电量', fieldName: 'netGeneration', unit: "kWh", iconName: "icon-shangwangdianliang"},
        {name: '自用电量', fieldName: 'selfGeneration', unit: "kWh", iconName: "icon-ziyongdianliang"},
        {name: '损失电量', fieldName: 'lossGeneration', unit: "kWh", iconName: "icon-nibianqi"},
      ]
    });

    const watchReturnDataInfo = watch(() => props.returnDataInfo, (newReturnDataInfo) => {
      that.return_data_info = JSON.parse(JSON.stringify(newReturnDataInfo ?? {}));
    }, {deep: true});

    return {...toRefs(that), watchReturnDataInfo};
  }
});
</script>

<style lang="scss" scoped>
.content_body {
  width: 100%;
  margin-bottom: 16px;
  box-sizing: border-box;
  padding: 32px 14px 20px 16px;

  .content_list {
    margin-bottom: 12px;
    display: flex;
    align-items: center;

    .content_list_left {
      width: 59px;
      height: 59px;
      justify-content: center;
      background-size: 100% 100%;
      background-repeat: no-repeat;
      background-image: url("@/assets/image/bg_icon.png");

      .iconfont {
        color: #51ECF1FF;
        font-size: 24px;
      }
    }

    .content_list_right {
      font-size: 12px;
      color: #ffffffcc;
      margin-left: 12px;

      .list_right_top {
        margin-bottom: 4px;
      }

      .list_right_bottom {
        display: flex;
        align-items: center;

        .number {
          color: #51ecf1ff;
          font-size: 18px;
          margin-right: 2px;
        }
      }
    }
  }
}
</style>