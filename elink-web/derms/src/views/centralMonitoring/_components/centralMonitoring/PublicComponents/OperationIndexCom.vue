<template>
  <TitleView :isTitleIcon="false" :title="titleName" is-content-height>
    <template #content>
      <div class="content-body" v-loading="listLoading">
        <el-row :gutter="12" v-if="list && list.length">
          <template v-for="(item,index) in list" :key="index">
            <el-col :xl="8" :lg="12" :md="12" :sm="24">
              <AttrFieldCardCom :attrFieldInfo="item"></AttrFieldCardCom>
            </el-col>
          </template>
        </el-row>
        <null-data v-else words="暂无数据"></null-data>
      </div>
    </template>
  </TitleView>
</template>

<script>
import {defineComponent, reactive, toRefs, watch} from "vue";
import AttrFieldCardCom from "./DeviceTelemetryAttrCom/AttrFieldCardCom.vue";
import {findSystemVarDataListById} from "@/api/centralMonitoring/centralMonitoring";

export default defineComponent({
  name: "OperationIndexCom",
  components: {AttrFieldCardCom},
  props: {
    activeDeviceId: {
      type: String,
      default: ""
    },
    titleName: {
      type: String,
      default: ""
    }
  },
  setup(props) {
    const that = reactive({
      list: [],
      listLoading: false,
    });

    // 查询设备遥测字段数据列表
    const querySystemVarDataListById = () => {
      that.listLoading = true;
      findSystemVarDataListById({queryId: props.activeDeviceId, timer: new Date()}).then(res => {
        that.list = res.data ? res.data : [];
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      });
    };

    const watchActiveDeviceId = watch(() => props.activeDeviceId, (newActiveDeviceId) => {
      if (newActiveDeviceId) querySystemVarDataListById();
    }, {deep: true, immediate: true});

    return {...toRefs(that), querySystemVarDataListById, watchActiveDeviceId};
  }
});
</script>

<style lang="scss" scoped>
.card_list_class {
  width: 100%;
  padding: 2px 8px;
  margin-bottom: 16px;
  box-sizing: border-box;

  .card_li_top {
    display: flex;
    align-items: center;

    .number {
      color: #08f7fd;
      font-size: 14px;
      font-weight: 700;
    }

    .unit {
      font-size: 12px;
      color: #ffffffcc;
      margin-left: 4px;
    }
  }

  .card_li_line {
    height: 2px;
    display: flex;
    align-items: center;
    margin-top: 2px;
    margin-bottom: 4px;

    .line_left {
      width: 28%;
      height: 100%;
      max-width: 24px;
      background: #00AEFF;
    }

    .line_right {
      height: 100%;
      background: #0D4D7A;
    }
  }

  .card_li_bottom {
    font-size: 12px;
    color: #ffffffcc;
  }
}
</style>