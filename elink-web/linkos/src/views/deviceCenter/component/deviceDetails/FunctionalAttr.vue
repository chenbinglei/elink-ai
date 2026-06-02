<template>
  <div class="content_body">
    <div class="content_body_top">
      <el-button :icon="RefreshRight" @click="queryDeviceFunctionListById">刷新</el-button>
      <el-button v-if="returnDataList && returnDataList.length" :icon="Setting" @click="clickFunctionButFun">管理</el-button>
    </div>
    <div class="content_body_bottom scrollbarStyle" v-loading="listLoading">
      <template v-if="list && list.length">
        <el-row :gutter="16">
          <template v-for="(item,index) in list" :key="index">
            <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
              <functional-attr-card :cardInfo="item"></functional-attr-card>
            </el-col>
          </template>
        </el-row>
      </template>
      <template v-else><null-data></null-data></template>
    </div>

    <FunctionAttrFieldDialog v-if="functionAttrFieldVisible" v-model:isVisible="functionAttrFieldVisible" :activeDeviceId="activeDeviceId" @changeEvent="queryDeviceFunctionListById" />
  </div>
</template>

<script>
import {RefreshRight,Setting} from "@element-plus/icons-vue";
import {onMounted, reactive, toRefs, defineComponent} from "vue";
import {FunctionalAttrCard,FunctionAttrFieldDialog} from "./component";
import {findDeviceFunctionListById} from "@/api/deviceCenter/deviceList";

export default defineComponent({
  name: "FunctionalAttr",
  components: {FunctionalAttrCard,FunctionAttrFieldDialog},
  props: {
    activeDeviceId: {
      type: [Number, String],
      default: ""
    }
  },
  setup(props) {

    const that = reactive({
      Setting,
      list: [],
      RefreshRight,
      returnDataList: [],
      listLoading: false, // 表格加载
      functionAttrFieldVisible: false,
    })

    const queryDeviceFunctionListById = () => {
      that.listLoading = true;
      findDeviceFunctionListById({deviceId: props.activeDeviceId}).then(res => {
        that.returnDataList = res.data ?? [];
        that.list = that.returnDataList.filter(item=> item.showType === 1);
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      });
    }

    const clickFunctionButFun = ()=>{
      that.functionAttrFieldVisible = true;
    }

    onMounted(() => {
      queryDeviceFunctionListById();
    })

    return {...toRefs(that), queryDeviceFunctionListById, clickFunctionButFun}
  }
})
</script>

<style lang="scss" scoped>
.content_body {
  height: 100%;
  padding: 16px 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;

  .content_body_top{
    display: flex;
    justify-content: flex-end;
    margin-bottom: 12px;
  }

  .content_body_bottom{
    flex: 1;
    flex-basis: auto;
    overflow-y: auto;
  }
}
</style>
