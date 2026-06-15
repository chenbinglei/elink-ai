<template>
  <TitleView :isTitleIcon="false" :title="titleName" is-content-height>
    <template #content>
      <el-row :gutter="10" class="content_list">
        <el-col v-for="(item, index) in list" :key="index" :xl="12" :sm="24">
          <div class="card_li_class flex-ai-center" @click="clickItemCardFun(item.id)">
            <div class="card_li_left">
              <div class="left_line"></div>
              <div class="left_title">{{ item.name }}</div>
            </div>
            <div class="card_li_right flex-jc-ai-center">
              <span class="iconfont icon-shezhi"></span>
            </div>
          </div>
        </el-col>
      </el-row>

      <AlarmResetDialog v-if="alarmResetVisible" :isVisible="alarmResetVisible" />
      <ControlModelDialog v-if="controlModelVisible" :isVisible="controlModelVisible" />
      <PowerSettingDialog v-if="powerSettingVisible" :isVisible="powerSettingVisible" :operateType="operateType"
        :titleName="dialogTitleName" />
    </template>
  </TitleView>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs } from "vue";
import { ControlModelDialog, PowerSettingDialog, AlarmResetDialog } from "@/views/centralMonitoring/_components/centralMonitoring/PublicComponents";

export default defineComponent({
  name: "RemoteControlCom",
  components: { ControlModelDialog, PowerSettingDialog, AlarmResetDialog },
  props: {
    titleName: {
      type: String,
      default: ""
    }
  },
  setup() {
    const that = reactive({
      operateType: 1,
      dialogTitleName: "",
      alarmResetVisible: false,
      controlModelVisible: false,
      powerSettingVisible: false,
      list: [{ id: 1, name: "控制模式" }, { id: 2, name: "有功功率" }, { id: 3, name: "无功功率" }, { id: 4, name: "告警复位" }]
    });

    const clickItemCardFun = (operateType) => {
      that.operateType = operateType;
      if (operateType === 4) that.alarmResetVisible = true;
      if (operateType === 1) that.controlModelVisible = true;
      that.dialogTitleName = operateType === 2 ? "有功功率控制" : "无功功率控制";
      if (operateType === 2 || operateType === 3) that.powerSettingVisible = true;
    };

    return { ...toRefs(that), clickItemCardFun };
  }
});
</script>

<style lang="scss" scoped>
.card_li_class {
  padding: 14px 12px;
  border-radius: 6px;
  background: #a7bcce26;
  box-sizing: border-box;
  margin-bottom: 12px;
  justify-content: space-between;

  .card_li_left {
    color: #ffffff;
    font-size: 12px;
    display: flex;
    align-items: center;

    .left_line {
      width: 2px;
      height: 14px;
      margin-right: 4px;
      border-radius: 2px;
      box-shadow: 0 4px 4px #00000059;
      background: linear-gradient(180deg, #48c5ff 0%, #057cb3 106.06%);
    }
  }

  .card_li_right {
    width: 16px;
    height: 16px;
    cursor: pointer;
    border-radius: 50%;
    background: #00a3ff4d;
    box-sizing: border-box;

    .iconfont {
      color: #ffffff;
      font-size: 12px;
    }
  }
}
</style>