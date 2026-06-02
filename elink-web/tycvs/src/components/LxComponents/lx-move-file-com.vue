<template>
  <div class="dialog-main">
    <div class="content_top">
      <template v-for="(item,index) in selectFileRouteList" :key="index">
        <div :class="{ last_list_class: index === selectFileRouteList.length }" class="list_name">
          <span class="text">{{ item.name }}</span>
          <span v-if="index + 1 < selectFileRouteList.length" class="arrow">></span>
        </div>
      </template>
    </div>
    <div class="content_bottom">
      <HandleMenus ref="handleMenusRef" :handleMenuArray="handleMenuArray" :isShowHeader="isShowHeader" defaultExpandAll defaultIsChecked
                   @handleMenuEvent="handleMenuEvent"></HandleMenus>
    </div>
  </div>
</template>

<script>
import {setTreeData} from "@/utils";
import {getCurrentInstance, onMounted, reactive, toRefs, watch, defineComponent, ref, nextTick} from "vue";

export default defineComponent({
  name: 'lx-move-file-com',
  props: {
    fileMenuArray:{
      type: Array,
      default: ()=> []
    },
    activeFileRouteId: {
      type: [String,Number],
      default: ""
    },
  },
  emits:["update:activeFileRouteId"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      isShowHeader: false,
      checkFileRouteId: "",
      selectFileRouteList: [], // 选中文件的路径
      handleMenuArray: [],
      handleTreeMenuArray: [],
    })

    const handleMenuEvent = (data) => {
      // console.log(data);
      if (data.menuType === "clickTreeNode") {
        emit("update:activeFileRouteId",data.id);
        that.checkFileRouteId = data.id;
        querySlFileRouteFun();
      }
    }

    const querySlFileRouteFun = () => {
      let currentNode = findHandleMenuList(that.checkFileRouteId).reverse();
      currentNode.unshift({id: "quanBuId", name: "全部文件"});
      that.selectFileRouteList = JSON.parse(JSON.stringify(currentNode));
      // console.log(that.selectFileRouteList);
    }

    const findHandleMenuList = (checkFileRouteId, currentNode = []) => {
      for (let i = 0; i < that.handleTreeMenuArray.length; i++) {
        if (checkFileRouteId === that.handleTreeMenuArray[i].id) {
          currentNode.push(that.handleTreeMenuArray[i]);

          if (that.handleTreeMenuArray[i].parentId) {
            findHandleMenuList(that.handleTreeMenuArray[i].parentId, currentNode);
          }
          break
        }
      }

      return currentNode
    }

    const handleMenusRef = ref(null);
    const initParamConfigFun = () => {
      that.checkFileRouteId = props.activeFileRouteId;
      that.handleMenuArray = setTreeData(props.fileMenuArray,'folderId');
      that.handleTreeMenuArray = JSON.parse(JSON.stringify(props.fileMenuArray));

      nextTick(() => {
        handleMenusRef.value.setCheckedKeysFun([that.checkFileRouteId]);
      })
    }

    const watchFileMenuArray = watch(()=>props.fileMenuArray,(newFileMenuArray)=>{
      initParamConfigFun();
    },{ deep:true })

    onMounted(() => {
      initParamConfigFun();
    })

    return {...toRefs(that), initParamConfigFun, handleMenuEvent, querySlFileRouteFun, findHandleMenuList, handleMenusRef, watchFileMenuArray}
  }
})
</script>

<style lang="scss" scoped>
.dialog-main {
  width: 100%;

  .content_top {
    display: flex;
    align-items: center;
    margin-bottom: 12px;

    .list_name {
      color: #666666;
      font-size: 12px;

      .arrow {
        margin: 0 8px;
      }

      &:last-child {
        color: #000000;
        font-weight: bold;

        .arrow {
          margin: 0;
        }
      }
    }
  }

  .content_bottom {
    height: 320px;

    :deep(.handleMenu) {
      width: 100%;
      height: 100%;
      border-right: none;

      .bottomContent {
        padding: 0;
      }
    }
  }
}
</style>