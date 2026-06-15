<template>
  <div class="stationMenuListCom">
    <div class="stationMenuListComLeft" v-if="handleMenuStatus">

      <div class="content_title_top">
        <div class="content_title">
          <span class="iconfont icon-dianwang"></span>
          <span class="title_name">场站列表</span>
        </div>
        <div class="content_right">共{{ handleMenuArray?.length }}座</div>
      </div>

      <div class="content_search">
        <el-input v-model="siteName" placeholder="请输入关键词" @keydown.enter="querySiteListByUserId">
          <template #prefix><el-icon><Search/></el-icon></template>
          <template #suffix v-if="comeFromName === 'default'">
            <StationMenuFilterCom ref="stationMenuFilterComRef" @changeEvent="querySiteListByUserId"></StationMenuFilterCom>
          </template>
        </el-input>
      </div>
      <div class="content_list scrollbarStyle" v-loading="loading">
          <StationInfoCard v-for="(item,index) in handleMenuArray" :key="index" :comeFromName="comeFromName" :isActiveCard="item.id === active_site_id" :stationInfo="item" :siteStateArray="siteStateArray" @changeEvent="changeEvent" />
      </div>
      <div class="content_bottom" v-if="comeFromName === 'default'">
          <div v-for="(item,index) in siteStateArray" :key="index" class="state_list">
            <div class="round_dot" :style="{ backgroundColor: item.color }"></div>
            <div class="state_text" :style="{ color: item.color }">{{ item.name }}</div>
          </div>
      </div>
    </div>
    <div class="arrow_right" @click="clickArrowButFun">
      <div :class="['arrow',handleMenuStatus ? 'rotateLeft' : 'rotateRight']"></div>
    </div>
  </div>
</template>

<script lang="ts">
import {Search} from '@element-plus/icons-vue';
import StationInfoCard from '@/views/centralMonitoring/_components/centralMonitoring/StationMenuListCom/StationInfoCard.vue';
import {findSiteListByUserId} from "@/api/centralMonitoring/centralMonitoring";
import { ElMessage, ElMessageBox, ElLoading } from "element-plus";
import StationMenuFilterCom from "@/views/centralMonitoring/_components/centralMonitoring/StationMenuListCom/StationMenuFilterCom.vue";
import {reactive, defineComponent, toRefs, onMounted, getCurrentInstance, ref} from "vue";

export default defineComponent({
  name: "StationMenuListCom",
  emits: ["changeEvent","update:activeSiteId","update:activeSiteName"],

  components: {Search, StationMenuFilterCom,StationInfoCard},
  props:{
    activeSiteId:{
      type: [Number,String],
      default: ""
    },
    comeFromName:{
      type: String,
      default: "default"
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      siteName: "",
      loading: false,
      isFirstCom: true,
      handleMenuArray: [],
      handleMenuStatus: true,
      active_site_id: props.activeSiteId || history.state?.siteId,
      siteStateArray: [
        {id: 1, name: "正常", color: "#11AB84"},
        // {id: 2, name: "预警", color: "#FAAD14"},
        {id: 3, name: "通信异常", color: "#FF7D1E"},
        {id: 4, name: "故障", color: "#FD393A"}
      ]
    });

    // 查询站点列表
    const stationMenuFilterComRef = ref(null);
    const querySiteListByUserId = () => {
      that.loading = true;
      let formInline = {};
      if(stationMenuFilterComRef.value){
        formInline = JSON.parse(JSON.stringify(stationMenuFilterComRef.value.formInline));
        if(formInline.scenarioTypes) formInline.scenarioTypes = formInline.scenarioTypes.join(',');
        if(formInline.areaValue) formInline.areaValue = formInline.areaValue[formInline.areaValue.length - 1];
      }
      findSiteListByUserId({ ...formInline,siteName: that.siteName,timer: new Date() }).then(res => {
        let handleMenuArray = res.data ? res.data : [];
        that.handleMenuArray = JSON.parse(JSON.stringify(handleMenuArray));
        if(that.handleMenuArray && that.handleMenuArray.length){
          let findItem = that.handleMenuArray.find(item=> item.id === that.active_site_id);
          // console.log(findItem);
          if(!that.active_site_id || (that.active_site_id && !findItem)) changeEvent(that.handleMenuArray[0]);
          if(that.isFirstCom && findItem) changeEvent(findItem);
          that.isFirstCom = false;
        }
        that.loading = false;
      }).catch(()=>{
        that.loading = false;
      });
    };

    const clickArrowButFun = () => {
      that.handleMenuStatus = !that.handleMenuStatus;
    };

    const changeEvent = (data)=>{
      console.log(data,'changeEvent');
      that.active_site_id = data.id;
      emit("changeEvent", data);
      emit("update:activeSiteId", that.active_site_id);
      emit("update:activeSiteName", data.siteName);
    };

    onMounted(() => {
      querySiteListByUserId();
    });

    return {...toRefs(that), querySiteListByUserId, changeEvent, clickArrowButFun, stationMenuFilterComRef};
  }
});
</script>
<style lang="scss" scoped>
.stationMenuListCom {
  height: 100%;
  display: flex;
  align-items: center;
  padding-right: 10px;
  box-sizing: border-box;
  margin-right: 2px;
  position: relative;

  .stationMenuListComLeft{
    width: 320px;
    height: 100%;
    display: flex;
    flex-direction: column;
    transition: all .28s;
    border-radius: 4px;
    box-sizing: border-box;
    border: 1px solid var(--el-border-color);

    .content_title_top{
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding-right: 12px;
      box-sizing: border-box;

      .content_title {
        color: #FFFFFF;
        font-size: 14px;
        padding: 10px 12px 0 12px;
        box-sizing: border-box;

        .title_name {
          margin-left: 4px;
          font-weight: bold;
        }
      }

      .content_right{
        font-size: 12px;
        color: #ffffffd9;
      }
    }

    .content_search {
      padding: 14px 12px 6px 12px;
      box-sizing: border-box;
    }

    .content_list{
      flex: 1;
      height: 2px;
      flex-basis: auto;
      overflow-y: auto;
      padding: 12px 12px;
      box-sizing: border-box;
    }

    .content_bottom{
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 8px 16px;
      box-sizing: border-box;
      background: #071527;

      .state_list{
        display: flex;
        align-items: center;

        .round_dot{
          width: 8px;
          height: 8px;
          border-radius: 1px;
          margin-right: 4px;
        }

        .state_text{
          font-size: 12px;
        }
      }
    }
  }

  .arrow_right {
    width: 10px;
    height: 50px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    box-sizing: border-box;
    border-radius: 0 8px 8px 0;
    background: rgba(7, 156, 235,0.4);
    position: absolute;
    right: 0;

    .arrow {
      cursor: pointer;
      transition: all .28s;

      &:after {
        content: '';
        display: block;
        border-style: solid;
        border-color: #FFFFFF transparent;
        border-width: 5px 5px 0 5px;
      }
    }

    .rotateLeft {
      transform: rotate(90deg);
    }

    .rotateRight {
      transform: rotate(-90deg);
    }
  }
}
</style>