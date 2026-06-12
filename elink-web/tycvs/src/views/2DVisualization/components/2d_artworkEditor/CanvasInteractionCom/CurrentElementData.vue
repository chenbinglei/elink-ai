<template>
  <el-collapse v-model="activeNames" class="currentElementData">

    <el-collapse-item class="content_list" name="1" title="参数">
      <PelBindRequestParamsCom v-model:requestParams="activePelDate.requestParams"/>
    </el-collapse-item>

    <el-collapse-item class="content_list" name="2" title="数据">
      <!--      图元绑定数据操作-->
      <template v-if="activePelDate.realTimes && activePelDate.realTimes.length">
        <div class="content_list_li fd-column">
          <BindRealTimeTableCom :realTimes="activePelDate.realTimes" @changeEvent="changeEvent"></BindRealTimeTableCom>
          <AddDataSourceButtonList :buttonType="2" @changeEvent="changeEvent"></AddDataSourceButtonList>
        </div>
      </template>
      <template v-else>
        <div class="null_data">
          <null-data words="还没有动态数据">
            <template #content>
              <AddDataSourceButtonList @changeEvent="changeEvent"></AddDataSourceButtonList>
            </template>
          </null-data>
        </div>
      </template>
    </el-collapse-item>
  </el-collapse>
</template>

<script>
import { useMeta2dStore } from '@/stores/index';

import {deepClone} from "@meta2d/core";
import {reactive, toRefs, defineComponent, computed, watch} from "vue";
import {AddDataSourceButtonList, BindRealTimeTableCom, PelBindRequestParamsCom} from "./CurrentElementData/index";

export default defineComponent({
  name: "CurrentElementData",
  components: {AddDataSourceButtonList, BindRealTimeTableCom, PelBindRequestParamsCom},
  setup() {

    const meta2dStore = useMeta2dStore();
    const canvasMeta2d = computed(() => {
      return meta2dStore.canvasMeta2d;
    });

    const current_active_pel_list = computed(() => {
      return meta2dStore.current_active_pel_list;
    });

    const delete_field_list = computed(() => {
      return meta2dStore.setValueDeleteFieldList;
    });

    const that = reactive({
      activePelDate: {},
      activeNames: ["1", "2", "3", "4"],
      mse_list:[{id: true,name: "MSE(支持H265格式,需最新chrome)"}, {id: false,name: "Webrtc(低延迟,仅支持H264格式)"}]
    })

    const findPenIdConfigFun = () => {
      // console.log(current_active_pel_list);
      let pen_id = current_active_pel_list.value[0];
      let activePelDate = canvasMeta2d.value.findOne(pen_id);
      let clonePelDate = deepClone(activePelDate ? activePelDate : {});
      that.activePelDate = JSON.parse(JSON.stringify(clonePelDate));
      // console.log(that.activePelDate);
    };

    const changeEvent = (data) => {
      let activePelDate = JSON.parse(JSON.stringify(that.activePelDate));

      // 动态数据绑定
      if (data.operateData === 'realTimes') {
        let realTimeData = JSON.parse(JSON.stringify(data));

        delete realTimeData.operateData
        if (!activePelDate.realTimes) activePelDate.realTimes = [];
        // 如果有当前数据，直接获取展示
        if (activePelDate[realTimeData.key]) realTimeData.value = activePelDate[realTimeData.key];
        activePelDate.realTimes.push(realTimeData);
      }

      // 更新绑定的整个数据列表
      if (data.operateData === 'updateRealTime') activePelDate.realTimes = data.realTimes;

      that.activePelDate = JSON.parse(JSON.stringify(activePelDate)); // 保存只执行一次数据更新
    }

    const watchActiveObjectDate = watch(() => that.activePelDate, (newActiveObjectDate, oldActiveObjectDate) => {
      if (JSON.stringify(oldActiveObjectDate) !== "{}") {
        let activePelDate = JSON.parse(JSON.stringify(newActiveObjectDate));

        // 删除对应的视图数据
        for(let i = 0;i < delete_field_list.value.length;i++) delete activePelDate[delete_field_list.value[i]];
        canvasMeta2d.value.setValue(activePelDate);
      }
    }, {deep: true})

    const watchCurrentActivePelList = watch(() => current_active_pel_list, () => {
      findPenIdConfigFun();
    }, {deep: true,immediate: true});

    return {...toRefs(that), canvasMeta2d, current_active_pel_list, watchCurrentActivePelList, watchActiveObjectDate, changeEvent, delete_field_list}
  }
})
</script>

<style lang="scss" scoped>
.currentElementData {
  border: none;
  padding: 12px 0;
  box-sizing: border-box;

  :deep(.content_list) {
    padding: 12px 0;
    box-sizing: border-box;
    border-bottom: 1px solid var(--el-collapse-border-color);

    .el-collapse-item__header {
      font-size: 13px;
      font-weight: 700;
      color: var(--color-title);
      padding-left: 16px;
      box-sizing: border-box;
    }

    .content_list_li {
      display: flex;
      padding: 0 12px 0 16px;
      box-sizing: border-box;
      margin-bottom: 8px;

      .content_list_li_left {
        width: 70px;
        font-size: 14px;
        color: var(--color);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        line-height: 30px;
      }

      .content_list_li_right {
        flex: 1;
        display: flex;
        align-items: center;
        padding-left: 8px;
        box-sizing: border-box;
      }

      &:last-child {
        margin-bottom: 0;
      }
    }

    &:last-child {
      border-bottom: none;

      .el-collapse-item__wrap {
        border-bottom: none;
      }
    }
  }

  .el-collapse-item {
    padding: 0;
    border-bottom: none;

    .el-collapse-item__content {
      padding-bottom: 12px;
    }
  }
}

.null_data {
  padding-top: 24px;

  :deep(.nullData) {

    .noCartImages {
      max-width: 120px;
    }

    .textFont {
      font-size: 14px;
      margin-bottom: 12px;
    }
  }
}
</style>