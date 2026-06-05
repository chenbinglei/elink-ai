<template>
  <Dialog v-model:isVisible="dialog_visible" :manualEnterClose="false" customClass="clearDialogPadding" :title="titleName" width="65vw" @confirm="confirmDialog">
    <!--  本文件为地图选点， 框选区域面积 功能-->

    <template v-loading="mapLoading" v-slot:content>
      <div class="content_body">

        <div v-if="operationType === 1" class="displayFlex">
          <el-input v-model="searchLocation" :suffix-icon="Search" placeholder="输入要搜索的地点" clearable @keyup.enter="searchAddress"></el-input>
          <div class="drawDots noSelect pointer" @click="drawDotsFun">
            <span class="iconfont icon-dingwei"></span>
            <span>画点</span>
          </div>
        </div>

        <div v-if="operationType === 2" class="displayFlex">
          <div class="flex_left">
            <el-tooltip v-for="(item,index) in polygonLeftArray" :key="index" :content="item.name" effect="dark" placement="right">
              <div :class="{ broderTopAndBot: index === 0 || index === polygonLeftArray.length,activeType:mapType === item.type }"
                   class="flex_li noSelect" @click="drawFunction(item.type)">
              </div>
            </el-tooltip>
          </div>
          <div class="flex_right">
            <el-tooltip v-for="(item,index) in polygonRightArray" :key="index" :content="item.name" effect="dark" placement="left">
              <div class="flex_li noSelect" @click="clickRightFun(item.type)"></div>
            </el-tooltip>
          </div>
        </div>

        <!--        地图实例-->
        <div id="container" class="formContainerMap"></div>

      </div>
    </template>
  </Dialog>
</template>

<script>
// import {Mapbox} from '@antv/l7-maps';
import {ElMessage} from "element-plus";
import {Search} from "@element-plus/icons-vue";
// import {Scene, Scale, Popup, Marker} from '@antv/l7';
// import {DrawCircle, DrawRect, DrawPolygon} from '@antv/l7-draw';
import {getLonAndLatByAddress} from "@/api/siteCenter/siteManagement";
import {reactive, toRefs, watch, getCurrentInstance, onMounted, onBeforeUnmount, nextTick} from "vue";

export default {
  name: "LocationAndRegionDialog",
  props: {
    titleName: {
      type: String,
      default: "坐标拾取"
    },
    isVisible: {
      type: Boolean,
      default: false
    },
    // 1: 选择经纬度  2：选择区域
    operateType: {
      type: Number,
      default: 1
    },
    // 选点的经纬度坐标
    latAndLongitude: {
      type: String,
      default: ""
    },
  },
  emits: ["update:isVisible"],
  setup(props) {
    const {emit} = getCurrentInstance();
    const that = reactive({
      Search,
      searchLocation: "", // 搜索的地点
      dialog_visible: props.isVisible,
      operationType: props.operateType,  // 1: 坐标拾取  2：区域边界选取

      // 框选地图区域边界
      polygonLeftArray: [
        {id: 1, name: "矩形", icon: "map_rect", type: "rectangle"},
        {id: 2, name: "圆形", icon: "map_circular", type: "circle"},
        {id: 3, name: "多边形", icon: "map_polygon", type: "polygon"}
      ],
      polygonRightArray: [
        {id: 1, name: "删除", icon: "map_delete", type: "delete"},
        {id: 2, name: "重置", icon: "map_reduction", type: "reset"}
      ],

      sceneMap: null, // 地图实例
      mapLoading: false, // 地图加载状态

      getLng: null,
      getLat: null, // 当前经纬度点坐标
      isClickMap: false, // 地图是否可进行选点
      markerInfo: null, //marker 实例

      mapType: "", // 默认为多边形
      pointDrawer: null, //多边形 实例
      areaBorderInfo: null, // 区域边界数据
      isEnableStatus: true,
      pointOptions: {}
    });

    const initL7Map = () => {
      that.mapLoading = true;

      mapboxgl.accessToken = process.env.VUE_APP_MAPBOX_ACCESS_TOKEN;
      const map = new mapboxgl.Map({
        zoom: 5,//初始化地图级别
        minZoom: 3,
        viewMode: "3D", //是否为3D地图模式
        container: 'container', // container id
        center: [120.24, 30.19], //初始化地图中心点位置
        style: "mapbox://styles/sunmax/clwa32q27009x01rd1afk125r",
      });

      that.sceneMap = new L7.Scene({
        id: 'container',
        logoVisible: false, // logo 是否可见
        preserveDrawingBuffer: true, //是否保留缓冲区数据
        resizeEnable: true, // 是否监控地图容器尺寸变化
        autoFit: true, //初始化完成之后，地图是否自动缩放到图层范围
        map: new L7.Mapbox({
          mapInstance: map
        }),
      });

      // 地图加载成功执行
      that.sceneMap.on('loaded', () => {
        
        // 地图绑定点击事件
        that.sceneMap.on("click", showInfoClick);
        createdMapFun();//进入创建地图信息
        console.log("地图初始化成功！");
        
      })
    };

    // 地图上搜索地址
    const searchAddress = () => {
      if (that.searchLocation) {
        getLonAndLatByAddress({ address:that.searchLocation,timer: new Date() }).then(res=>{
          let fitBounds = [],options = {};
          let lngAndLatArray = res.data ? res.data : [];
          if(lngAndLatArray.length <= 1){
            options = { zoom: 15 };
            lngAndLatArray = [...lngAndLatArray,...lngAndLatArray];
          }
          for(let item of lngAndLatArray)fitBounds.push([item.lng,item.lat]);
          that.sceneMap.fitBounds(fitBounds,options);
          ElMessage({type: "success", showClose: true, message: "搜索成功"});
        })
      }
    }

    //进入创建地图信息
    const createdMapFun = () => {

      // 地图选点操作
      if(that.operationType === 1){

        if(props.latAndLongitude){
          that.isClickMap = true;
          let latAndLongitude = props.latAndLongitude.split(',');
          // console.log(latAndLongitude);
          let lngLat = {lat: latAndLongitude[1], lng: latAndLongitude[0]};
          showInfoClick({ lngLat: lngLat });
          //设置地图缩放等级和中心点
          that.sceneMap.setZoomAndCenter(15, latAndLongitude);
        }
      }

      // 框选地图区域边界
      if (that.operationType === 2) clickRightFun("reset", false);

      that.mapLoading = false;
    }

    // 选择绘画类型
    const drawFunction = (mapType) => {
      if (that.mapType !== mapType) {
        that.mapType = mapType;
        that.pointOptions = {};
        that.isEnableStatus = true;
        that.sceneMap.removeAllLayer();

        setPointDrawerFun();
        ElMessage({type: "success", showClose: true, message: "选择成功，请在地图上进行框选"});
      }
    }

    // 重新绘画
    const setPointDrawerFun = () => {
      // console.log(that.pointDrawer)
      that.pointDrawer && that.pointDrawer.clear();

      // 矩形
      if (that.mapType === "rectangle") {
        that.pointDrawer = new L7.Draw.DrawRect(that.sceneMap, {
          multiple: false,  // 禁止绘画多个
          areaOptions: {}, // 展示面积
          distanceOptions: {}, // 展示距离
          adsorbOptions: {}, // 吸附能力
          ...that.pointOptions
        });
      }

      // 圆形
      if (that.mapType === "circle") {
        that.pointDrawer = new L7.Draw.DrawCircle(that.sceneMap, {
          multiple: false,  // 禁止绘画多个
          areaOptions: {}, // 展示面积
          distanceOptions: {}, // 展示距离
          adsorbOptions: {}, // 吸附能力
          ...that.pointOptions
        });
      }

      // 多边形
      if (that.mapType === "polygon") {
        that.pointDrawer = new L7.Draw.DrawLine(that.sceneMap, {
          multiple: false,  // 禁止绘画多个
          // adsorbOptions: {}, // 吸附能力
          distanceOptions: {},
          ...that.pointOptions
        });
      }

      if(that.pointDrawer){
        // 第一次进入需要执行，编辑不需要
        if (that.isEnableStatus) that.pointDrawer.enable(); //开始绘制

        that.pointDrawer.on(L7.Draw.DrawEvent.Change, e => {
          that.areaBorderInfo = JSON.parse(JSON.stringify(e));
        });
      }
    }

    const clickRightFun = (type, alterMessage = true) => {
      if (type === "delete") {
        that.pointOptions = {};
        ElMessage({type: "success", showClose: true, message: "删除成功。"});
      }

      if (type === "reset") {
        that.isEnableStatus = true;

        if (that.mapInfo.regionBorderStatus) {
          that.mapType = that.mapInfo.mapType;

          if(that.mapInfo.positPath){
            that.isEnableStatus = false;
            that.areaBorderInfo = JSON.parse(JSON.stringify(that.mapInfo.positPath));
            that.pointOptions = {autoActive: true, initialData: that.mapInfo.positPath};
          }

          if(that.mapInfo.mapZoom && that.mapInfo.mapCenter){
            that.sceneMap.setZoomAndCenter(that.mapInfo.mapZoom, that.mapInfo.mapCenter);
          }
        }

        // console.log(that.areaBorderInfo);
        if (alterMessage) ElMessage({type: "success", showClose: true, message: "重置成功。"});
      }

      setPointDrawerFun();
    };

    // 点击绑定事件(画点)
    const drawDotsFun = () => {
      if (!that.mapLoading) {
        that.isClickMap = true;
        ElMessage({type: "success", showClose: true, message: "请在地图上进行选点操作。"});
      }
    };

    // 地图选点方法
    const showInfoClick = (e) => {
      if(that.isClickMap){
        const { lngLat } = e;
        // console.log(lnglat);
        that.markerInfo && that.markerInfo.remove();
        const element = document.createElement('div');
        element.className = "homeMapMarker";
        element.innerHTML = `<div class="markerClass">
          <div class="marker_top">坐标：${lngLat.lat},${lngLat.lng}</div>
          <div class="marker_bottom"><span class="iconfont icon-dingwei"></span></div>
        </div>`;

        that.markerInfo = new L7.Marker({element: element}).setLnglat(lngLat);
        that.sceneMap.addMarker(that.markerInfo);
        that.getLng = lngLat.lng;
        that.getLat = lngLat.lat;
      }
    }

    // 确认选择
    const confirmDialog = () => {

      // 当前操作类型
      let data = { operationType: that.operationType };
      if (that.operationType === 1) {
        data = { latAndLongitude: `${that.getLng},${that.getLat}`, ...data };
      }

      if (that.operationType === 2) {
        let areaBorderInfo = JSON.parse(JSON.stringify(that.areaBorderInfo))
        if (areaBorderInfo && !areaBorderInfo.length) areaBorderInfo = null;

        if (areaBorderInfo) for (let i = 0; i < areaBorderInfo.length; i++) areaBorderInfo[i].properties = {};

        let mapCenter = that.sceneMap.getCenter();
        let mapZoom = that.sceneMap.getZoom();

        data = {
          ...data,
          mapZoom: mapZoom,
          mapCenter: mapCenter,
          mapType: that.mapType,
          positPath: areaBorderInfo
        };
      }

      emit("mapEvents", data);
    }


    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      nextTick(()=> initL7Map());
    });

    onBeforeUnmount(() => {
      if (that.sceneMap) that.sceneMap.destroy(); //销毁地图
    });

    return {...toRefs(that), watchVisible, watchDialogVisible, searchAddress, initL7Map, confirmDialog, createdMapFun, drawDotsFun, showInfoClick,
      setPointDrawerFun, clickRightFun, drawFunction};
  }
};
</script>

<style lang="scss" scoped>
.content_body {
  height: 60vh;
  position: relative;

  .displayFlex {
    z-index: 1000;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 16px 0 16px;
    box-sizing: border-box;

    .el-input {
      width: 228px;
      z-index: 1000;
      //background: rgba(4, 22, 36, .7);
    }

    .drawDots {
      z-index: 1000;
      color: #FFFFFF;
      font-size: 14px;
      padding: 6px 10px;
      border-radius: 4px;
      box-sizing: border-box;
      background: #007FEB;
      display: flex;
      align-items: center;

      .iconfont {
        margin-right: 5px;
      }
    }

    .flex_left, .flex_right {
      display: flex;
      flex-direction: column;
      z-index: 1000;

      .activeType {
        box-shadow: 0 0 10px 0 #10A6EC inset;
      }

      .flex_li {
        width: 32px;
        height: 32px;
        background: #007FEB;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;

        img {
          width: 16px;
          height: 16px;
        }
      }

      .flex_li:last-child {
        border-radius: 0 0 4px 4px;
      }
    }

    .broderTopAndBot {
      border-top: none !important;
      border-bottom: none !important;
    }

    .flex_left {
      .flex_li:first-child {
        border-top: 1px solid rgba(0, 231, 255, .5) !important;
        border-radius: 4px 4px 0 0;
      }

    }

    .flex_right {
      .flex_li:first-child {
        margin-bottom: 17px;
        border-radius: 4px;
      }

      .flex_li:nth-of-type(2) {
        border-radius: 4px 4px 0 0;
      }
    }

  }

  :deep(.formContainerMap) {
    width: 100%;
    height: 100%;
    z-index: 100;
    position: absolute;
    left: 0;
    top: 0;

    .markerClass {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;

      .iconfont {
        color: #007FEB;
        font-size: 16px;
      }

      .marker_top{
        color: #007FEB;
        font-size: 14px;
        font-weight: bold;
      }
    }
  }
}
</style>
