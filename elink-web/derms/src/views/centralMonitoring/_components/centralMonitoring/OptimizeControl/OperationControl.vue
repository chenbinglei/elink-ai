<template>
  <div class="operationControl">
    <div class="content_list">
      <title-view title="策略控制">
        <template #content>
          <el-form :model="formInline">
            <el-form-item label="策略类型：">
              <el-select v-model="formInline.strategyType" :teleported="false" placeholder="请选择">
                <el-option v-for="item in strategyTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="策略模板：">
              <el-select v-model="formInline.strategy" :teleported="false" placeholder="请选择">
                <el-option v-for="item in strategyArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-form>
        </template>
      </title-view>
    </div>
  </div>
</template>

<script lang="ts">
import {reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "OperationControl",
  props: {
    siteId: {
      type: [Number, String],
      default: ""
    },
  },
  setup() {

    const that = reactive({
      formInline: {},
      strategyArray: [],
      strategyTypeArray: [
        {id: 1, name: "削峰填谷"},
        {id: 2, name: "储协同优化"},
        {id: 3, name: "需量控制"},
        {id: 4, name: "储充协同优化"},
        {id: 5, name: "光储充协同优化"},
        {id: 6, name: "手动控制"},
      ],
    });

    return {...toRefs(that)};
  }
});


</script>

<style lang="scss" scoped>
.content_list {
  padding: 10px 12px;
  box-sizing: border-box;
  background-color: #071527;

  .el-form-item{
    max-width: 580px;
  }
}
</style>