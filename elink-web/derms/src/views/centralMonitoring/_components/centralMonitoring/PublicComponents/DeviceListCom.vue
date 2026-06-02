<template>
  <div class="deviceList">
    <div class="content_top">
      <div class="content_top_left">设备列表</div>
      <div class="content_top_right">共{{ list?.length ?? 0 }}台</div>
    </div>
    <div class="content_bottom">
      <template v-if="list && list.length">

        <div v-for="(item, index) in list" :key="index" :class="{ active_select_class: item.id === activeDeviceId }"
          class="content_bottom_li" @click="clickDeviceListCardFun(item)">
          <div class="card_left_line"></div>
          <div class="card_right_info">
            <div class="card_right_info_top">
              <div class="textTwo deviceNumber">{{ $filters.moreData(item.deviceName) }}</div>
              <slot :data="item" name="status"></slot>
            </div>
            <slot :data="item" name="content"></slot>
          </div>
        </div>
      </template>
      <null-data v-else words="暂无设备列表"></null-data>
    </div>
  </div>
</template>

<script>
import { reactive, defineComponent, toRefs, getCurrentInstance, watch } from "vue";

export default defineComponent({
  name: "DeviceListCom",
  props: {
    activeDeviceId: {
      type: [Number, String],
      default: ""
    },
    deviceList: {
      type: Array,
      default: () => []
    }
  },
  emits: ["update:activeDeviceId", "changeEvent"],
  setup(props) {
    const { emit } = getCurrentInstance();

    const that = reactive({
      list: [],
    });

    const clickDeviceListCardFun = (data = null) => {
      let findItem = that.list.find(item => item.id === props.activeDeviceId); // 查询当前策略是否还存在
      if (!data) data = findItem ? findItem : that.list[0]; // 获取第一条数据
      emit("changeEvent", { operateType: "DeviceListCom", ...data });
      emit("update:activeDeviceId", data?.id);
    };

    const watchDeviceList = watch(() => props.deviceList, (newDeviceList) => {
      that.list = JSON.parse(JSON.stringify(newDeviceList ?? []));
      clickDeviceListCardFun();
    }, { deep: true, immediate: true });

    return { ...toRefs(that), clickDeviceListCardFun, watchDeviceList };
  }
});
</script>

<style lang="scss" scoped>
.deviceList {
  width: 340px;
  height: 100%;
  padding: 12px 10px;
  box-sizing: border-box;
  border-radius: 6px;
  background: #ffffff08;
  display: flex;
  flex-direction: column;

  .content_top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;

    .content_top_left {
      color: #ffffff;
      font-size: 16px;
      font-weight: 700;
    }

    .content_top_right {
      font-size: 12px;
      color: #ffffff80;
    }
  }

  .content_bottom {
    flex: 1;
    height: 2px;
    flex-basis: auto;
    overflow-y: auto;

    .content_bottom_li {
      width: 100%;
      border-radius: 6px;
      background: #a7bcce26;
      box-sizing: border-box;
      border: 1px solid #106ec426;
      margin-bottom: 10px;
      padding: 16px 12px;
      position: relative;
      display: flex;
      align-items: center;

      .card_left_line {
        width: 4px;
        height: 33px;
        position: absolute;
        left: 0;
        border-radius: 0 12px 12px 0;
        box-shadow: 0 4px 4px #00000059;
        background: linear-gradient(180deg, #48c5ff 0%, #057cb3 106.06%);
      }

      .card_right_info {
        width: 100%;

        .card_right_info_top {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 24px;

          .deviceNumber {
            color: #ffffff;
            font-size: 16px;
            -webkit-line-clamp: 1;
          }
        }
      }

      &:last-child {
        margin-bottom: 0;
      }
    }

    .active_select_class {
      background: #0071a199;
      border: 1px solid #106ec426;
    }
  }
}
</style>