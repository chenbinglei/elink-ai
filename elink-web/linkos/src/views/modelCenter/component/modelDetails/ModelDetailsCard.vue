<template>
  <div class="modelDetailsCard">
    <div class="content_list_left">
      <el-image :src="modelInfo.logoPath ? modelInfo.logoPath : ''">
        <template #error>
          <div class="image-slot">
            <el-icon><Picture/></el-icon>
          </div>
        </template>
      </el-image>
    </div>
    <div class="content_list_right">

      <div class="right_top">
        <div class="right_top_left">
          <div class="modelName">{{ $filters.moreData(modelInfo.modelName) }}</div>
          <div :class="'modelStatus' + modelInfo.modelStatus" class="modelStatus">
            {{ $filters.modelStatus(modelInfo.modelStatus) }}
          </div>
        </div>
        <div class="right_top_right pointer" @click="clickItemButton">
          <span class="iconfont icon-bianji"></span>
        </div>
      </div>

      <div class="right_bottom">
        <el-row :gutter="12">
          <template v-for="(item,index) in list" :key="index">
            <el-col :lg="8" :sm="12" :xs="24">
              <div class="right_bottom_list">
                <div class="bottom_list_left">{{ item.name}}：</div>
                <div class="bottom_list_right" :class="item.className">
                  <span>{{ $filters.moreData(modelInfo[item.fieldName]) }}</span>
                  <template v-if="item.nextFieldName">
                    <span>，</span>
                    <span>{{ $filters.moreData(modelInfo[item.nextFieldName]) }}</span>
                  </template>
                </div>
              </div>
            </el-col>
          </template>
        </el-row>
      </div>

    </div>


    <CreateModelDialog v-if="createModelVisible" v-model:isVisible="createModelVisible" :formDialog="modelInfo" :titleName="titleName" ifAlter
                       @changeEvent="queryModelDetailById"></CreateModelDialog>

  </div>
</template>

<script lang="ts">
import {Box, Picture} from '@element-plus/icons-vue';
import {getCurrentInstance, onMounted, reactive, toRefs} from "vue"
import CreateModelDialog from "../modelManagement/CreateModelDialog";
import {findModelDetailById} from "@/api/modelCenter/modelManagement";

export default {
  name: "ModelDetailsCard",
  components: {Box, Picture, CreateModelDialog},
  props: {
    activeModelId: {
      type: [String, Number],
      default: ''
    }
  },
  emits: ["update:activeModelName"],
  setup(props) {

    const {emit} = getCurrentInstance();

    const that = reactive({
      modelInfo: {},
      titleName: "编辑模型",
      createModelVisible: false,

      list: [
        {name: "模型ID", fieldName: "id", className: ""},
        {name: "设备类型", fieldName: "typeName", className: "text_color"},
        {name: "关联设备数量", fieldName: "deviceNum", className: "text_color"},
        {name: "更新信息", fieldName: "updateTime", className: "", nextFieldName: "updateName"},
        {name: "创建信息", fieldName: "createTime", className: "", nextFieldName: "createName"},
        {name: "模型描述", fieldName: "modelDesc", className: ""},
      ]
    })

    // 根据模型id查询模型详情数据
    const queryModelDetailById = () => {
      findModelDetailById({id: props.activeModelId}).then(res => {
        that.modelInfo = res.data ? res.data : {};
        emit("changEvent",that.modelInfo);
      })
    }

    const clickItemButton = () => {
      that.createModelVisible = true;
    }

    onMounted(() => {
      queryModelDetailById();
    })

    return {...toRefs(that), queryModelDetailById, clickItemButton}
  }
}
</script>

<style lang="scss" scoped>
.modelDetailsCard {
  width: 100%;
  padding: 12px 16px;
  box-sizing: border-box;
  border-radius: 4px;
  display: flex;

  .content_list_left {
    width: 88px;
    height: 88px;

    .el-image {
      width: 100%;
      height: 100%;
      border-radius: 4px;
      border: 1px solid #DBDBDD;
      box-sizing: border-box;

      .image-slot {
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: #DBDBDD;
        font-size: 32px;
        color: #F5F5F5;
      }
    }
  }

  .content_list_right {
    flex: 1;
    padding: 4px 0 4px 10px;
    box-sizing: border-box;

    .right_top {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .right_top_left {
        display: flex;
        align-items: center;

        .modelName{
          font-weight: bold;
          font-size: 18px;
        }

        .modelStatus {
          padding: 2px 8px;
          margin-left: 10px;
          box-sizing: border-box;
          border-radius: 12px;
          color: #B9B9B9;
          font-size: 14px;
          background: #F5F5F5;
          border: 1px solid #B9B9B9;
        }

        .modelStatus1 {
          color: #049735;
          background: #E5FFEE;
          border: 1px solid #049735;
        }
      }

      .right_top_right {
        .iconfont {
          font-size: 18px;
        }
      }
    }

    .right_bottom{
      margin-top: 16px;
    }

    .right_bottom_list {
      display: flex;
      align-items: center;
      margin-bottom: 8px;

      .bottom_list_left{
        color: #242424;
        font-size: 14px;
      }

      .bottom_list_right{
        display: flex;
        align-items: center;
        color: #BBBBBB;
        font-size: 14px;

        .text_color {
          color: #1F74E2;
        }
      }
    }
  }
}
</style>
