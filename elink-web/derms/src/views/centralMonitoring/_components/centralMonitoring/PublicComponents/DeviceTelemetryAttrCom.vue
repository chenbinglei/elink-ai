<template>
  <TitleView :isTitleIcon="false" :title="titleName" is-content-height>
    <template #headerRight>
      <el-button :icon="Setting" size="small" @click="clickSettingButFun">设置</el-button>
    </template>
    <template #content>
      <div v-loading="listLoading" class="content-body">
        <el-row v-if="list && list.length" :gutter="12">
          <template v-for="(item,index) in list" :key="index">
            <el-col :lg="8" :md="12" :sm="24" :xl="6" v-if="item.showType === 1">
              <AttrFieldCardCom :attrFieldInfo="item"></AttrFieldCardCom>
            </el-col>
          </template>
        </el-row>
        <null-data v-else words="暂无功能点属性"></null-data>
        <FunctionAttrShowDialog v-if="functionAttrShowVisible" v-model:isVisible="functionAttrShowVisible" :activeDeviceId="activeDeviceId" @changeEvent="queryDeviceTelemetryList"/>
      </div>
    </template>
  </TitleView>
</template>

<script lang="ts">
import {Setting} from '@element-plus/icons-vue';
import {defineComponent, reactive, toRefs, watch} from "vue";
import AttrFieldCardCom from "./DeviceTelemetryAttrCom/AttrFieldCardCom.vue";
import {findDeviceTelemetryList} from "@/api/centralMonitoring/centralMonitoring";
import FunctionAttrShowDialog from "./DeviceTelemetryAttrCom/FunctionAttrShowDialog.vue";

export default defineComponent({
  name: "DeviceTelemetryAttrCom",
  components: {FunctionAttrShowDialog, AttrFieldCardCom},
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
      Setting,
      list: [],
      listLoading: false,
      functionAttrShowVisible: false,
    });

    // 查询设备遥测字段数据列表
    const queryDeviceTelemetryList = () => {
      that.listLoading = true;
      findDeviceTelemetryList({deviceId: props.activeDeviceId, timer: new Date()}).then(res => {
        that.list = res.data ? res.data : [];
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      });
    };

    const clickSettingButFun = () => {
      that.functionAttrShowVisible = true;
    };

    const watchActiveDeviceId = watch(() => props.activeDeviceId, (newActiveDeviceId) => {
      if (newActiveDeviceId) queryDeviceTelemetryList();
    }, {deep: true, immediate: true});

    return {...toRefs(that), clickSettingButFun, queryDeviceTelemetryList, watchActiveDeviceId};
  }
});
</script>

<style lang="scss" scoped></style>