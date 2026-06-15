<template xmlns="">
  <div class="topologyMap">
    <v-chart ref="modelInfoRef" autoresize :option="chartOption"></v-chart>
  </div>
</template>

<script lang="ts">
import {useRoute} from "vue-router";
import {reactive, toRefs, watch} from "vue";

export default {
  name: "TopologyMap",
  props: {
    list: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  setup(props){

    const route = useRoute();

    const that = reactive({
      activeDeviceId: route.query.id, // 当前设备模型id
      activeDeviceName: route.query.subTitle, // 当前设备模型id

      chartOption:{
        tooltip: {},
        series: [{
          type: 'graph',
          layout: 'force', //图的布局，类型为力导图
          symbolSize: 70, // 调整节点的大小
          roam: false,  // 是否开启鼠标缩放和平移漫游
          edgeSymbol: ['pin', 'pin'],
          edgeSymbolSize: [4, 10],
          animationDurationUpdate: 1500,
          animationEasingUpdate: 'quinticInOut',
          draggable: true, //这里设置为false，不然拖拽鼠标和节点有偏移
          emphasis: {
            scale: true, //是否开启高亮后节点的放大效果。
            focus: 'adjacency'
          },
          edgeLabel: {
            show: true,
            fontSize: 12,
            formatter: `{@headEnd}`
          },
          lineStyle: {
            width: 1,
            curveness: 0, //关系线的曲度，支持从 0 到 1 的值，值越大曲度越大
            color: "#242424",
          },
          categories: [
            {
              itemStyle: {
                shadowBlur: 20,
                color: "#F5F5F5",
                shadowColor: 'rgba(175, 175, 175, 0.8)'
              },
              label: {
                show: true,
                fontSize: 10,
                color: "#242424"
              }
            },
            {
              itemStyle: {
                shadowBlur: 20,
                color: "#E8F2FF",
                shadowColor: 'rgba(10, 116, 252, 0.5)'
              },
              label: {
                show: true,
                fontSize: 10,
                color: "#1F74E2"
              },
            },
            {
              itemStyle: {
                shadowBlur: 20,
                color: "#FFEBD6",
                shadowColor: 'rgba(255, 128, 0, 0.5)'
              },
              label: {
                show: true,
                fontSize: 10,
                color: "#FF8000"
              },
            }
          ],  // 颜色设置 也可以是对象
          force: {
            // gravity: 0.1,
            repulsion: [600, 880], //节点之间的斥力因子,支持设置成数组表达斥力的范围
            edgeLength: [240, 60],  //两个节点之间的距离，这个距离也会受 repulsion影响
          },
          labelLayout: {
            moveOverlap: 'shiftX', //在标签重叠的时候是否挪动标签位置以防止重叠。
          },
          data: [],
          links: []
        }]
      }
    })

    const setChartDataFun = ()=>{
      let links = [];
      let data = [{ id: that.activeDeviceId,name: that.activeDeviceName,category: 1 }]; // 把当前设备作为顶级

      if(props.list && props.list.length){
        for(let i = 0;i < props.list.length;i++){
          links.push({ source: that.activeDeviceId, target: props.list[i].nodeId,symbol: ['pin', 'triangle'] });
          data.push({ id: props.list[i].nodeId,name: props.list[i].nodeName,category: 2 });

          if(props.list[i].otherNodeList && props.list[i].otherNodeList.length){
            for(let j = 0;j < props.list[i].otherNodeList.length;j++){
              links.push({ source: props.list[i].nodeId, target: props.list[i].otherNodeList[j].nodeId,symbol: ['pin', 'triangle'] });
              // data.push({ id: props.list[i].nodeId,name: props.list[i].nodeName,category: 1 });
            }
          }
        }
      }

      that.chartOption.series[0].data = data;
      that.chartOption.series[0].links = links;
    }

    const watchList = watch(()=>props.list,(newList)=>{
      setChartDataFun();
    },{ deep:true,immediate:true })

    return {...toRefs(that), watchList, setChartDataFun }
  }
}
</script>

<style lang="scss" scoped>
.topologyMap {
  width: 100%;
  height: 100%;
}
</style>
