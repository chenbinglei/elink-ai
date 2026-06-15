<template>
  <TitleView :title="titleName" isContentHeight>
    <template #content>
      <div class="content_body">
        <template v-for="(item,index) in list" :key="index">
          <div :class="item.className" class="card_class">
            <div class="card_class_top">
              <span :class="item.iconName" class="iconfont"></span>
            </div>
            <div class="content_bottom">
              <div class="content_bottom_num">
                <template v-if="item.isFilterKg">
                  <span class="number">{{$filters.numberValue($filters.kgNumConvert(return_data_info[item.fieldName]))}}</span>
                  <span class="unit">{{$filters.numberUnit($filters.kgNumConvert(return_data_info[item.fieldName]), $filters.kgUnitConvert(return_data_info[item.fieldName]))}}</span>
                </template>
                <template v-else>
                  <span class="number">{{ $filters.numberValue(return_data_info[item.fieldName]) }}</span>
                  <span class="unit">{{ $filters.numberUnit(return_data_info[item.fieldName], item.unit) }}</span>
                </template>
              </div>
              <div class="content_bottom_text">{{ item.name }}</div>
            </div>
          </div>
        </template>
      </div>
    </template>
  </TitleView>
</template>

<script lang="ts">
import {defineComponent, reactive, toRefs, watch} from "vue";

export default defineComponent({
  name: "SocialContributionCom",
  props: {
    titleName: {
      type: String,
      default: ""
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
      list: [
        {
          unit: "千克",
          isFilterKg: true,
          name: "节约标准煤",
          className: "coal_class",
          iconName: "icon-jieyueyuanmei",
          fieldName: "standardCoalReduction",
        },
        {
          unit: "千克",
          isFilterKg: true,
          name: "CO2减排量",
          className: "co2_class",
          fieldName: "co2Reduction",
          iconName: "icon-eryanghuatan",
        },
        {
          unit: "棵",
          name: "等效植树量",
          className: "tree_class",
          fieldName: "treeReduction",
          iconName: "icon-dengxiaozhishu",
        },
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
  height: 100%;
  padding: 0 16px;
  box-sizing: border-box;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;

  .card_class {
    width: 184px;
    height: 184px;
    padding: 30px 0;
    border-radius: 50%;
    margin-bottom: 12px;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    flex-direction: column;
    justify-content: space-between;

    .card_class_top {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;

      .iconfont {
        font-size: 28px;
        font-weight: bold;
      }
    }

    .content_bottom {
      //margin-top: 12px;

      .content_bottom_num {
        margin-bottom: 4px;
        display: flex;
        align-items: center;
        justify-content: center;

        .number {
          font-size: 18px;
          color: #ffffffff;
        }

        .unit {
          margin-left: 2px;
          font-size: 12px;
          color: #ffffff99;
        }
      }

      .content_bottom_text {
        font-size: 16px;
        color: #ffffff99;
      }
    }
  }

  .coal_class {
    background: #c56af326;
    border: 1px solid #c56af3cc;

    .card_class_top {
      color: #c56af3cc;
      border: 1px solid #c56af3cc;
    }
  }

  .co2_class {
    background: #268fff26;
    border: 1px solid #268fffcc;

    .card_class_top {
      color: #268fffcc;
      border: 1px solid #268fffcc;
    }
  }

  .tree_class {
    background: #47cc4726;
    border: 1px solid #47cc47cc;

    .card_class_top {
      color: #47cc47cc;
      border: 1px solid #47cc47cc;
    }
  }
}
</style>