<template>
  <div v-loading="listLoading" class="deviceDetailsCard">
    <div class="content_list_left">
      <el-image :src="deviceInfo.sortLogo">
        <template #error>
          <div class="image-slot">
            <el-icon><Picture/></el-icon>
          </div>
        </template>
      </el-image>
    </div>
    <div class="content_list_right">
      <div class="deviceName">{{ $filters.moreData(deviceInfo.deviceName) }}</div>
      <div class="bottom_list">
        <el-row :gutter="12">
          <template v-for="(item,index) in list" :key="index">
            <el-col :lg="8" :md="12" :xs="24">
              <div class="content_list">
                <div class="content_list_left_text">{{ item.name }}</div>
                <div class="content_list_right" :class="[item.className ? item.className + deviceInfo[item.fieldName] : '' ]">
                  <span v-if="item.filterName">{{ $filters[item.filterName](deviceInfo[item.fieldName]) }}</span>
                  <span v-else>
                    <span>{{ $filters.moreData(deviceInfo[item.fieldName]) }}</span>
                    <span v-if="item.nextFieldName">，{{ $filters.moreData(deviceInfo[item.nextFieldName]) }}</span>
                  </span>
                </div>
              </div>
            </el-col>
          </template>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {Box, Picture} from '@element-plus/icons-vue';
import {findDeviceBasicInfoById} from "@/api/deviceCenter/deviceList";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "DeviceDetailsCard",
  components: {Box, Picture},
  props: {
    activeDeviceId: {
      type: [Number, String],
      default: ""
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();
    const that = reactive({
      deviceInfo: {},
      listLoading: false,
      list: [
        {name: "设备ID：", fieldName: "id"},
        {name: "设备序列号：", fieldName: "deviceNumber"},
        {name: "所属模型：", fieldName: "modelName"},
        {name: "所属场站：", fieldName: "siteName"},
        {name: "接入类型：", fieldName: "accessType", filterName: "accessType"},
        {name: "通信状态：", fieldName:"txStatus", filterName:"deviceStatus",className: "deviceStatus" },
        {name: "设备描述：", fieldName: "deviceDesc"},
      ]
    })

    // 根据设备id查询设备基本信息数据
    const queryDeviceBasicInfoById = () => {
      that.listLoading = true;
      findDeviceBasicInfoById({ deviceId: props.activeDeviceId }).then(res => {
        that.deviceInfo = res.data ? res.data : {};
        emit("changeEvent",{ operateType: "deviceDetailsCard",...that.deviceInfo });
        that.listLoading = false;
      }).catch(() => {
        that.deviceInfo = {};
        emit("changeEvent",{ operateType: "deviceDetailsCard" });
        that.listLoading = false;
      })
    }

    const watchDeviceId = watch(() => props.activeDeviceId, (newActiveDeviceId) => {
      queryDeviceBasicInfoById();
    }, {deep: true, immediate: true})

    return {...toRefs(that), watchDeviceId, queryDeviceBasicInfoById}
  }
})
</script>

<style lang="scss" scoped>
.deviceDetailsCard {
  display: flex;
  align-items: center;

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
    display: flex;
    flex-direction: column;
    padding-left: 16px;
    box-sizing: border-box;

    .deviceName {
      font-size: 20px;
      font-weight: bold;
      margin-bottom: 12px;
    }

    .bottom_list {
      flex: 1;
      height: 2px;

      .content_list {
        display: flex;
        align-items: center;
        margin-bottom: 12px;

        .content_list_left_text {
          font-size: 14px;
          color: #BBBBBB;
        }

        .content_list_right {
          font-size: 14px;
          color: #242424;
        }
        .deviceStatus0{
          color: #979797;
        }
        .deviceStatus1{
          color: #199D7C;
        }
        .deviceStatus2{
          color: #FF9C02;
        }
        .deviceStatus3{
          color: #F3615B;
        }
        .deviceStatus88{
          color: #A1A1A1;
        }
      }
    }
  }
}
</style>
