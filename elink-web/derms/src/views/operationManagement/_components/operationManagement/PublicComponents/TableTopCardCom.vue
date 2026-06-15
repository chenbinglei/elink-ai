<template>
  <div class="table_top_card">
    <div class="card_top">{{ $filters.chargingDisText($filters.moreData(cardInfo.name),chargingType) }}</div>
    <div class="card_bottom">
      <template v-if="isValueNumber(fieldValue)">
        <span class="number">{{ $filters.numberValue(fieldValue) }}</span>
        <span class="unit">{{ $filters.numberUnit(fieldValue) }}</span>
      </template>
      <span v-else class="number">{{ $filters.moreData(fieldValue) }}</span>
    </div>
  </div>
</template>

<script lang="ts">
import {isNumber} from "@/utils";
import {defineComponent, reactive, toRefs} from "vue";

export default defineComponent({
  name: "TableTopCardCom",
  props: {
    cardInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
    fieldValue: {
      type: [Number, String],
      default: ""
    },
    // 1： 充   2；放
    chargingType: {
      type: Number,
      default: 1
    }
  },
  setup() {
    const that = reactive({});

    const isValueNumber = (value) => {
      return isNumber(value);
    };

    return {...toRefs(that), isValueNumber};
  }
});
</script>

<style lang="scss" scoped>
.table_top_card {
  padding: 14px 12px;
  border-radius: 8px;
  background: #ffffff1a;
  box-sizing: border-box;
  margin-bottom: 12px;

  .card_top{
    font-size: 14px;
    color: #ffffffcc;
    white-space: nowrap;
    margin-bottom: 12px;
  }

  .card_bottom{
    color: #92d8ff;
    font-size: 21px;
    font-weight: bold;
  }
}
</style>