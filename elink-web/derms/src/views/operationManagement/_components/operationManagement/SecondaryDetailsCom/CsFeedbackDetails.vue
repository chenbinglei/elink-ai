<template>
  <div class="app-container-right">
    <div class="content_body_top content_border">
      <TitleView title="处理进度">
        <template #headerRight>
          <el-button>处理</el-button>
        </template>
        <template #content>
          <div class="content_timeline"></div>
        </template>
      </TitleView>
    </div>
    <div class="tableContent">
      <el-row :gutter="12" style="height: 100%">
        <template v-for="(item,index) in list" :key="index">
          <el-col :span="12">
            <div class="content_border" style="height: 100%;">
              <TitleView :title="item.name" is-content-height>
                <template #content>
                  <component :is="item.componentName"></component>
                </template>
              </TitleView>
            </div>
          </el-col>
        </template>
      </el-row>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent, onMounted, reactive, toRefs} from "vue";

export default defineComponent({
  name: "CsFeedbackDetails",
  setup() {

    const that = reactive({
      list: [
        {name: "反馈信息", componentName: "FeedbackInfo"},
        {name: "处理结果", componentName: "FeedbackInfo"},
      ]
    });

    return {...toRefs(that)};
  }
});
</script>

<style lang="scss" scoped>
.content_border {
  padding: 12px;
}

.content_body_top {
  margin-bottom: 12px;
  box-sizing: border-box;
}
</style>