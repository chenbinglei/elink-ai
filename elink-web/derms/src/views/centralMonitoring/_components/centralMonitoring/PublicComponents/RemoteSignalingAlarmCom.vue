<template>
  <TitleView :isTitleIcon="false" :title="titleName" is-content-height>
    <template #content>
      <div v-loading="listLoading" class="content—body">
        <el-row v-if="list && list.length" :gutter="10" class="content_list">
          <el-col v-for="(item,index) in list" :key="index" :sm="24" :xl="12">
            <div class="card_li_class flex-ai-center" :class="'card_li_class' + item.eventLevel">
              <div class="card_li_left">
                <div class="card_li_left_top textTwo">{{ $filters.moreData(item.eventName) }}</div>
                <div class="card_li_left_bottom">{{ $filters.moreData(item.createTime) }}</div>
              </div>
              <div class="card_li_right">
                <span class="iconfont icon-shangjiantou"></span>
              </div>
            </div>
          </el-col>
        </el-row>
        <div v-else class="flex-jc-ai-center null_data">
          <img alt="" src="@/assets/image/null_data_icon.png"/>
          <div class="alter_text">暂无告警</div>
        </div>
      </div>
    </template>
  </TitleView>
</template>

<script lang="ts">
import {defineComponent, reactive, toRefs, watch} from "vue";
import {findNotRecoveEventList} from "@/api/centralMonitoring/centralMonitoring";

export default defineComponent({
  name: "RemoteSignalingAlarmCom",
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

    const queryNotRecoveEventList = () => {
      that.listLoading = true;
      findNotRecoveEventList({deviceId: props.activeDeviceId,timer: new Date()}).then(res => {
        that.list = res.data ? res.data : [];
        that.listLoading = false;
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return;
        that.list = [];
      });
    };

    const watchActiveDeviceId = watch(()=> props.activeDeviceId,(newActiveDeviceId)=>{
      if(newActiveDeviceId) queryNotRecoveEventList();
    },{ deep: true,immediate: true });

    return {...toRefs(that), queryNotRecoveEventList, watchActiveDeviceId};
  }
});
</script>

<style lang="scss" scoped>
.card_li_class {
  padding: 10px 10px;
  margin-bottom: 12px;
  box-sizing: border-box;
  border-left: 1px solid #0E9971;
  border-right: 1px solid #0E9971;
  background-color: rgba(14, 152, 112, 0.3);

  .card_li_left {
    flex: 1;
    padding-right: 2px;

    .card_li_left_top {
      color: #FFFFFF;
      font-size: 12px;
      margin-bottom: 4px;
      -webkit-line-clamp: 1;
    }

    .card_li_left_bottom {
      color: #ffffffcc;
      font-size: 10px;
    }
  }

  .card_li_right {
    transform: rotate(90deg);

    .iconfont {
      color: #FFFFFF;
      font-size: 10px;
    }
  }
}

.card_li_class2 {
  border-left: 1px solid #C47B2B;
  border-right: 1px solid #C47B2B;
  background-color: rgba(213, 133, 43, 0.3);
}

.card_li_class3 {
  border-left: 1px solid #AD0B12;
  border-right: 1px solid #AD0B12;
  background-color: rgba(196, 7, 12, 0.3);
}

.null_data {
  height: 100%;
  flex-direction: column;

  img {
    width: 110px;
  }

  .alter_text {
    color: #ffffffcc;
    font-size: 12px;
  }
}
</style>