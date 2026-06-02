<template>
  <div v-loading="mapLoading" class="content_body">
    <div id="container" class="map">
      <div class="location_class">
        <template v-for="(item, index) in location_list" :key="index">
          <template v-if="!item.isSiteData">
            <div class="location_li" @click="clickBackMapLevelFun(item, index)">
              <span class="iconfont icon-dingwei" v-if="item.areaType === 0"></span>
              <span class="location_li_text">{{ item.name }}</span>
            </div>
            <div class="line_class" v-if="(index + 1) < location_list.length">/</div>
          </template>
          <template v-else>
            <el-popover v-model:visible="siteSelVisible" :teleported="false" placement="bottom" trigger="click"
              width="200">
              <template #reference>
                <div class="location_li location_li_site">
                  <span class="location_li_text">{{ item.name }}</span>
                  <span :class="{ iconRouteClass: siteSelVisible }" class="iconfont jianTou icon-shangjiantou"></span>
                </div>
              </template>
              <template #default>
                <div class="site_list_map">
                  <template v-for="(item, index) in siteAllList" :key="index">
                    <div :class="{ active_class: activeSiteId === item.id }" class="site_list_li textTwo"
                      @click="clickMarkerInfoFun(item)">
                      <span class="siteName">{{ item.name }}</span>
                    </div>
                  </template>
                </div>
              </template>
            </el-popover>
          </template>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import { useStore } from "vuex";
import { Mapbox } from '@antv/l7-maps';
import { querySiteNum } from "@/api/assetManagement/assetManagement";
import { reactive, defineComponent, toRefs, onMounted, computed, watch, nextTick } from "vue";
import { LineLayer, PointLayer, PolygonLayer, Scene, Marker, MarkerLayer, Popup, Fullscreen } from '@antv/l7';
import { getNowDateAll } from "@/utils/dateTime";

export default defineComponent({
  name: "AssetStatisticsMap",
  setup() {

    const store = useStore();
    const scenarioType = computed(() => {
      return store.state.assetManagement.scenarioType;
    });
    const activeSiteId = computed(() => {
      return store.state.assetManagement.activeSiteId;
    });

    const that = reactive({
      areaType: 0, //  0: 全国  1： 省  2： 市
      areaName: "",
      mapPopup: null,
      sceneMap: null,
      siteAllList: [],
      mapLoading: false,
      siteSelVisible: false,
      activeMapShowTextList: [], // 当前地图上显示的所有省市名称、中心点定位
      location_list: [{ areaType: 0, name: "中国", adcode: 100000, level: "country" }], // 定位记录
    });

    const initL7MapFun = () => {
      that.mapLoading = true;
      that.sceneMap = new Scene({
        id: 'container',
        logoVisible: false, // logo 是否可见
        resizeEnable: true, // 是否监控地图容器尺寸变化
        preserveDrawingBuffer: true, //是否保留缓冲区数据
        map: new Mapbox({
          zoom: 4,//初始化地图级别
          minZoom: 3,
          style: "blank"
        })
      });

      // 地图加载成功执行
      that.sceneMap.on('loaded', () => {
        that.mapPopup = new Popup({
          maxWidth: 280, //弹框最宽值
          closeButton: false, //是否显示关闭按钮
          closeOnClick: false, //是否在点击地图的时候关闭弹框
        });

        const fullscreen = new Fullscreen({
          btnText: '全屏',
          exitBtnText: '退出全屏',
        });

        console.log("地图初始化成功！");
        that.sceneMap.addControl(fullscreen);  // 全屏
        that.sceneMap.addPopup(that.mapPopup); // 地图添加 信息窗体
        setTimeout(() => createMapPolygonLayerFun(), 100); // 创建底层地图图层
      });
    };

    // 请求geojson数据
    const getGeoJsonData = async (adCodeName) => {
      return await fetch(`/geoJson/${adCodeName}_full.json`).then((res) => {
        if (res.status === 200) {
          return res.json();
        } else {
          console.log("获取地图数据失败！", res);
          return null;
        }
      }).catch((err) => {
        console.log("获取地图数据失败！", err);
        return null;
      });
    };

    // 创建底层地图图层
    const createMapPolygonLayerFun = async (adCodeName = 100000) => {
      let mapGeoJson;
      that.mapLoading = true;

      try {
        mapGeoJson = await getGeoJsonData(adCodeName); // 获取地图数据
        that.mapPopup?.close(); // 关闭信息框
        that.sceneMap.removeAllLayer(); //移除所有的图层对象
        that.sceneMap.removeAllMarkers(); //移除所有的图层对象
      } catch (e) {
        // ElMessage({type: "warning", message: "该地域文件配置获取失败！", showClose: true});
        let lastIndex = that.location_list.length - 2;
        let lastItem = that.location_list[lastIndex];
        clickBackMapLevelFun(lastItem, lastIndex, false);
        console.log("地图文件获取失败！！！", lastItem, lastIndex);
        that.mapLoading = false;
        return;
      }

      that.activeMapShowTextList = [];
      mapGeoJson.features.map((option) => {
        const { name, adcode, center, level } = option.properties;
        try {
          const [lng, lat] = center;
          that.activeMapShowTextList.push({ name, adcode, level, lng, lat });
        } catch (e) { }
      });

      const textLayer = new PointLayer({ zIndex: 4 }).source(that.activeMapShowTextList, {
        parser: { type: 'json', x: 'lng', y: 'lat' }
      }).shape('name', 'text').size(10).color('#ffffff99').style({
        textAnchor: 'center', // 文本相对锚点的位置 center|left|right|top|bottom|top-left
        spacing: 1, // 字符间距
        padding: [1, 1], // 文本包围盒 padding [水平，垂直]，影响碰撞检测结果，避免相邻文本靠 的太近
        stroke: '#FFFFFF', // 描边颜色
        strokeWidth: 0.3, // 描边宽度
        textAllowOverlap: true,
        // raisingHeight: 100000,
      });

      const lineDown = new LineLayer({ zIndex: 1 }).source(mapGeoJson).shape('line').color("#2354B2").size(1);
      const lineLayer = new LineLayer({ zIndex: 2 }).source(mapGeoJson).shape('wall').size(15000).style({
        opacity: 0.6,
        heightfixed: true,
        sourceColor: '#191C47',
        targetColor: 'rbga(255,255,255, 0)',
      });

      const provinceLayer = new PolygonLayer({ autoFit: true, zIndex: 3 }).source(mapGeoJson).size(10000).shape('extrude')
        .color('#1D3FA0').active({ color: '#328099' }).style({
          opacity: 0.8,
          pickLight: true,
          heightfixed: true,
          raisingHeight: 5000,
        });

      // provinceLayer.on('click', clickMapRegionFun);  // 监听图层点击事件
      that.sceneMap.addLayer(lineDown);
      that.sceneMap.addLayer(lineLayer);
      that.sceneMap.addLayer(provinceLayer);
      that.sceneMap.addLayer(textLayer);
      nextTick(() => findAssetSiteList()); // 查询资产站点列表
    };

    // 点击地图某一 区域
    const clickMapRegionFun = (e) => {
      // console.log(e);
      let properties = e.feature?.properties ?? {};
      let { name, adcode, level } = { ...e, ...properties };
      console.log(`当前${name}的adcode码：${adcode}，级别：${level}`);

      let areaType = 0;  //国
      if (level === "province") areaType = 1; // 省
      if (level === "city") areaType = 2; // 市
      // if(level === "district") areaType = 3; // 区

      that.areaName = name;
      that.areaType = areaType;
      that.location_list.push({ areaType: that.areaType, name: name, adcode: adcode, level: level });
      createMapPolygonLayerFun(adcode); // 重新构建地图
    };

    // 返回地图某个层级
    const clickBackMapLevelFun = (data, index, isCreateMap = true) => {
      if (data?.isSiteData || (that.areaType === data?.areaType && !activeSiteId?.value)) return;

      that.areaType = data?.areaType;
      that.areaName = data?.name ?? "";
      store.dispatch("updateActiveSiteId", ""); // 清除当前选中的站点
      const location_list = that.location_list.slice(0, index + 1);
      that.location_list = JSON.parse(JSON.stringify(location_list));
      if (isCreateMap) createMapPolygonLayerFun(data?.adcode); // 重新构建地图
    };

    // 查询资产站点列表
    const findAssetSiteList = () => {
      that.mapLoading = true;
      let formInline = { areaName: that.areaName, areaType: that.areaType ? that.areaType : '' };
      querySiteNum({ scenarioType: scenarioType.value, ...formInline }).then(res => {
        let returnDataInfo = res.data ? res.data : [];
        let siteAllInfoList = [], siteAllIds = [], siteAllNumList = [];
        for (let i = 0; i < returnDataInfo.length; i++) {

          // 省市 站点数量
          let findItem = that.activeMapShowTextList.find(item => item.name === returnDataInfo[i].name);
          if (findItem) {
            let data = { lat: findItem.lat, lng: findItem.lng, level: findItem.level, adcode: findItem.adcode, name: returnDataInfo[i].name, size: returnDataInfo[i].size };
            siteAllNumList.push(data);
          }

          if (returnDataInfo[i].siteInfoList && returnDataInfo[i].siteInfoList.length) {
            for (let j = 0; j < returnDataInfo[i].siteInfoList.length; j++) {
              siteAllIds.push(returnDataInfo[i].siteInfoList[j].id); // 获取全部站点id
              // 区域站点坐标位置
              if (returnDataInfo[i].siteInfoList[j].latitude && returnDataInfo[i].siteInfoList[j].longitude) {
                returnDataInfo[i].siteInfoList[j].name = returnDataInfo[i].siteInfoList[j].siteName;
                returnDataInfo[i].siteInfoList[j].lat = returnDataInfo[i].siteInfoList[j].latitude;
                returnDataInfo[i].siteInfoList[j].lng = returnDataInfo[i].siteInfoList[j].longitude;
                siteAllInfoList.push(returnDataInfo[i].siteInfoList[j]);
              }
            }
          }
        }

        // that.siteAllIds = JSON.parse(JSON.stringify(siteAllIds));
        store.dispatch("updateSiteAllIds", siteAllIds); // 全部站点ids
        store.dispatch("updateTimeNumFun", getNowDateAll()); // 更新资产数据
        that.siteAllList = JSON.parse(JSON.stringify(that.areaType <= 1 ? siteAllNumList : siteAllInfoList));
        that.sceneMap?.removeAllMarkers(); //移除所有的图层对象
        createMapAggregationNumFun();
      }).catch(() => {
        that.mapLoading = false;
        that.sceneMap?.removeAllMarkers(); //移除所有的图层对象
        store.dispatch("updateSiteAllIds", []); // 全部站点ids
        store.dispatch("updateTimeNumFun", getNowDateAll()); // 更新资产数据
      });
    };

    // 创建全国、省聚合 数量显示
    const createMapAggregationNumFun = () => {
      const markerLayer = new MarkerLayer();
      if (that.siteAllList && that.siteAllList.length) {
        for (let i = 0; i < that.siteAllList.length; i++) {
          const element = document.createElement('div');
          element.className = "homeMapMarker";
          // 显示站点数量
          if (that.areaType <= 1) {
            const parentElement = document.createElement('div');
            parentElement.className = "homeMapMarkerInfo";
            parentElement.innerHTML = `<div class="markerInfoLeft"><span class="iconfont icon-shebeidingwei"></span></div>`;
            //数量标签
            const childrenElement = document.createElement('div');
            childrenElement.className = "markerInfoRight";
            childrenElement.innerHTML = `<span class="name">${that.siteAllList[i].name}</span><span class="number">${that.siteAllList[i].size}</span>`;
            childrenElement.addEventListener('click', () => clickMapRegionFun(that.siteAllList[i]));
            // 父级元素
            parentElement.appendChild(childrenElement);
            element.appendChild(parentElement);
          }
          // 显示单站点光柱
          if (that.areaType === 2) {
            element.addEventListener('click', () => clickMarkerInfoFun(that.siteAllList[i]));
            element.innerHTML = `<div class="siteMapMarkerInfo"></div>`;
          }
          const marker = new Marker({ element: element }).setLnglat(that.siteAllList[i]);
          markerLayer.addMarker(marker);
        }
        that.sceneMap.addMarkerLayer(markerLayer);
      }
      that.mapLoading = false;
    };

    // 点击选中某个站点
    const clickMarkerInfoFun = (properties) => {
      const mapPopupShowHtml = `<div class="markerActiveInfo">
          <div class="siteName">${properties.siteName}</div>
          <div class="siteLine"></div>
       </div>`;

      let findIndex = that.location_list.findIndex(item => item.isSiteData === true);
      if (findIndex === -1) {
        that.location_list.push({ isSiteData: true, ...properties });
      } else {
        that.location_list[findIndex] = { isSiteData: true, ...properties };
      }

      store.dispatch("updateActiveSiteId", properties.id);
      store.dispatch("updateSiteAllIds", [properties.id]); // 全部站点ids
      store.dispatch("updateTimeNumFun", getNowDateAll()); // 更新资产数据
      that.mapPopup.setLnglat(properties); //设置信息框展示经纬度
      that.mapPopup.setHTML(mapPopupShowHtml);//设置信息框展示html
      that.mapPopup.open();
    };

    // 监听 scenarioType 重新获取资产数据
    const watchScenarioType = watch(() => scenarioType, () => {
      // 最后一级为单站点则关闭单站点层
      const findLastItem = that.location_list[that.location_list.length - 1];
      // console.log(findLastItem);
      if (findLastItem && findLastItem.isSiteData) {
        that.location_list.pop(); // 删除最后一个元素
        that.mapPopup?.close(); // 关闭信息框
      }
      findAssetSiteList();
    }, { deep: true });

    onMounted(() => {
      initL7MapFun();
    });

    return {
      ...toRefs(that), initL7MapFun, createMapPolygonLayerFun, findAssetSiteList, scenarioType, createMapAggregationNumFun, clickBackMapLevelFun,
      clickMarkerInfoFun, activeSiteId, watchScenarioType
    };
  }
});
</script>

<style lang="scss" scoped>
.content_body {
  flex: 1;
  width: 2px;
  height: 100%;
  flex-basis: auto;

  .map {
    width: 100%;
    height: 100%;
    position: relative;

    :deep(.l7-control-container) {
      .l7-button-control {
        color: #a1ddfe80;
        background-color: #ffffff08;

        .l7-iconfont {
          fill: #a1ddfe80;
        }
      }
    }

    //单个marker样式
    :deep(.homeMapMarker) {
      z-index: 9990;
      //display: inline-block;

      .homeMapMarkerInfo {
        display: flex;
        align-items: center;
        position: relative;

        .markerInfoLeft {
          .iconfont {
            font-size: 28px;
            font-weight: bold;
            background: linear-gradient(90deg, #ffffff33 21.43%, #00000018 48.86%, #ffffff33 78.57%), linear-gradient(180deg, #53d6ff 32.37%, #328099 99.21%);
            -webkit-text-fill-color: transparent;
            -webkit-background-clip: text;
          }
        }

        .markerInfoRight {
          position: absolute;
          left: 30px;
          top: -4px;
          color: #ffffff;
          font-size: 14px;
          font-weight: bold;
          font-family: Inter, serif;
          padding: 2px 14px;
          box-sizing: border-box;
          background-size: 100% 100%;
          background-repeat: no-repeat;
          background-image: url("@/assets/image/map_marker.png");

          .number {
            margin-left: 10px;
          }
        }
      }

      .siteMapMarkerInfo {
        width: 25px;
        height: 92px;
        background-size: 100% 100%;
        background-repeat: no-repeat;
        background-image: url("@/assets/image/mapSiteIcon.png");
      }
    }

    :deep(.markerActiveInfo) {
      display: flex;
      flex-direction: column;
      align-items: center;

      .siteName {
        color: #ffffff;
        font-size: 16px;
        font-weight: 700;
        padding: 4px 24px;
        border-radius: 4px;
        box-sizing: border-box;
        background: linear-gradient(90deg, #02a5ff00 0%, #0066c4cc 48%, #00a3ff00 100%);
      }

      .siteLine {
        width: 1px;
        height: 130px;
        margin-top: 2px;
        background: #A9E0FF;
      }
    }

    :deep(.l7-popup) {
      .l7-popup-tip {
        display: none;
      }

      .l7-popup-content {
        padding: 0 !important;
        box-shadow: none !important;
        background-color: transparent !important;
      }
    }
  }

  .location_class {
    z-index: 100;
    position: absolute;
    top: 12px;
    left: 12px;
    display: flex;
    align-items: center;

    .line_class {
      padding: 0 5px;
      font-size: 16px;
      color: #ffffff99;
      box-sizing: border-box;
    }

    .location_li {
      cursor: pointer;
      color: #ffffff99;
      font-size: 16px;

      .iconfont {
        font-size: 16px;
        margin-right: 8px;
      }

      &:last-child {
        color: #FFFFFF;
      }
    }

    .location_li_site {
      color: #FFFFFF;

      .jianTou {
        margin-left: 4px;
        transition: all .25s;
        display: inline-block;
      }

      .iconRouteClass {
        transform: rotate(180deg);
      }
    }

    .site_list_map {
      width: 100%;

      .site_list_li {
        height: 32px;
        color: #c7e3f3;
        font-size: 14px;
        text-align: center;
        line-height: 32px;
        -webkit-line-clamp: 1;

        &:hover {
          cursor: pointer;
          color: #00f7ff;
        }
      }

      .active_class {
        color: #00f7ff;
        background: #0094ff66;
      }
    }
  }
}
</style>