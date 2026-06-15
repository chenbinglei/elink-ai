<template>
  <div class="directConnectionComponent" v-loading="listLoading">
    <div class="table_list">
      <div class="table_list_left">协议类型</div>
      <div class="table_list_right">{{ $filters.moreData(activeChannelInfo.protocolType) }}</div>
    </div>
    <div class="table_list">
      <div class="table_list_left">接入协议</div>
      <div class="table_list_right">{{ $filters.moreData(activeChannelInfo.accessProtocol) }}</div>
    </div>
    <div class="table_list">
      <div class="table_list_left">IP地址/端口</div>
      <div class="table_list_right">
        <span>{{ $filters.moreData(activeChannelInfo.ip) }}</span>
        <span>:</span>
        <span>{{ $filters.moreData(activeChannelInfo.port) }}</span>
      </div>
    </div>
    <div class="table_list">
      <div class="table_list_left">连接状态</div>
      <div class="table_list_right">
        <span class="txStatus" :class="'txStatus' + activeChannelInfo.txStatus">
          <span>{{ activeChannelInfo.txStatus === 0 ? '连接' : '断开' }}</span>
        </span>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {getCurrentInstance, reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "GatewayChannelComponent",
  props: {
    activeDeviceId: {
      type: [Number, String],
      default: ""
    },
    activeChannelInfo: {
      type: Object,
      default: ()=>{
        return { }
      }
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      listLoading: false,
    })

    return { ...toRefs(that) }
  }
})
</script>

<style scoped lang="scss">
.directConnectionComponent{
  width: 100%;
  border: 1px solid #DBDBDD;
  box-sizing: border-box;
  margin: 12px 0;

  .table_list{
    height: 32px;
    display: flex;
    border-bottom: 1px solid #DBDBDD;
    box-sizing: border-box;

    .table_list_left{
      width: 112px;
      height: 100%;
      background: #E8F2FF;
      text-align: center;
      line-height: 32px;
      font-size: 12px;
      color: #242424;
    }

    .table_list_right{
      flex: 1;
      padding: 0 16px;
      box-sizing: border-box;
      display: flex;
      align-items: center;

      .txStatus{
        color: #FF1515;
      }
      .txStatus0{
        color: #41CB4A;
      }
    }

    &:last-child{
      border-bottom: none;
    }
  }
}
</style>
