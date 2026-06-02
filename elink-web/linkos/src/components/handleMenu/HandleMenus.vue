<template>
  <div class="handleMenu">
    <!-- 头部 -->
    <div v-if="isShowHeader" class="title">
      <span>{{ title }}</span>
      <div class="rightTopButIcon">
        <template v-if="isCollapseBut">
          <span class="iconfont pointer" :class="[defaultExpandAll ? 'icon-zuocelan-lanzhankai' : 'icon-zuocelan-lansuohui']" @click="clickRightBut(1)"></span>
        </template>
        <template v-if="showAddButton">
          <span class="rightTopButImg pointer iconfont icon-treeAdd" @click="clickRightBut(2)"></span>
        </template>
        <el-popover v-if="showCompileButton" :teleported="false" placement="bottom" trigger="hover" width="90">
          <template #reference>
            <span class="rightTopButImg iconfont icon-treeSetting"></span>
          </template>
          <div class="moreMenu">
            <template v-for="(item,index) in compileArray" :key="index">
              <div class="menu_li" @click="clickBatchButton(item.id)">{{ item.name }}</div>
            </template>
          </div>
        </el-popover>
      </div>
    </div>

    <div class="bottomContent">
      <!--      标签页-->
      <Tabs v-if="showTopSelect" v-model:tabsIndex="handle_menu_top_index" :tabsArray="handleMenuTopArray"></Tabs>

      <!-- 搜索框 -->
      <div class="searchInput" v-if="isSearchInput">
        <el-input v-model="keyword" :placeholder="placeholderText" clearable @input="searchInput">
          <template #suffix><el-icon><Search/></el-icon></template>
        </el-input>
      </div>

      <!-- 数据列表 -->
      <div class="list_menu">
        <div class="list_menu_content scrollbarStyle">
          <el-tree ref="treeBoxRef" :current-node-key="currentLivingId" :data="list" :default-checked-keys="defaultCheckedKeys"
                   :default-expand-all="defaultExpandAll" :default-expanded-keys="defaultExpandedKeys" :filter-node-method="filterTreeNode"
                   :highlight-current="!show_checkbox" :indent="12" :props="treeProps" :show-checkbox="show_checkbox" :expand-on-click-node="expandOnClickNode"
                   auto-expand-parent check-on-click-node class="rightArrowClass tree-line" node-key="id" @check-change="checkChangeFun"
                   @node-click="clickTreeNode">
            <template #default="{ node, data }">
              <div class="custom-tree-node">
                <div class="left_content">
                  <div v-if="data.iconName" :class="[data.iconName,'iconfont']"></div>
                  <div class="label textTwo" v-html="node.label"></div>
                  <div v-if="displayClosed" class="iconfont icon-chahao" @click="clickClosedIcon(data)"></div>
                </div>
              </div>
            </template>
          </el-tree>
        </div>
      </div>

      <div v-if="show_checkbox && isBottomBut" class="displayBottomBut">
        <div class="button confirm" @click="confirmSelect">确 定</div>
        <div class="button cancel" @click="cancelSelect">取 消</div>
      </div>

    </div>
  </div>
</template>

<script>
import {treeToArray} from '@/utils';
import {ElMessageBox} from 'element-plus';
import {Search, ArrowDown} from "@element-plus/icons-vue";
import {getCurrentInstance, defineComponent, onMounted, toRefs, ref, reactive, watch, nextTick} from "vue";

export default defineComponent({
  name: "HandleMenus",
  components: {Search, ArrowDown},
  props: {
    isShowHeader: {
      type: Boolean,
      default: true,
    },
    // 是否展示 全部折叠/ 展开功能
    isCollapseBut: {
      type: Boolean,
      default: false,
    },
    //标题
    title: {
      type: String,
      default: "标题",
    },
    //input中的placeholder
    placeholderText: {
      type: String,
      default: "请输入关键词",
    },
    // 是否显示右上角添加按钮
    showAddButton: {
      type: Boolean,
      default: false
    },
    // 是否显示右上角 编辑按钮
    showCompileButton: {
      type: Boolean,
      default: false
    },
    //是否显示 顶部 列表选择
    showTopSelect: {
      type: Boolean,
      default: false
    },
    //编辑按钮列表数据    1:批量删除   2：批量导出
    compileArray: {
      type: Array,
      default: () => []
    },
    // 列表数组
    handleMenuArray: {
      type: Array,
      default: () => []
    },
    // 顶部 列表数组
    handleMenuTopArray: {
      type: Array,
      default: () => []
    },
    // 顶部 列表数组 默认选择
    handleMenuTopIndex: {
      type: [Number, String],
      default: 1
    },
    // 是否展示底部按钮
    isBottomBut: {
      type: Boolean,
      default: true
    },
    // 配合 firstEmit：false 使用
    id: {
      type: [Number, String],
      default: ""
    },
    // 树形结构是否显示选择框
    showCheckbox: {
      type: Boolean,
      default: false
    },
    // 是否显示搜索框
    isSearchInput: {
      type: Boolean,
      default: true
    },
    //默认进去是否获取全部的id  ， 配合 showCheckbox
    isSelectAll: {
      type: Boolean,
      default: false
    },
    // 右侧是否展示关闭图标
    displayClosed: {
      type: Boolean,
      default: false
    },
    // 是否默认展开所有的
    defaultExpandAll: {
      type: Boolean,
      default: false
    },
    // 数据更新是否重新获取第一项
    dataRenewIsFun: {
      type: Boolean,
      default: false
    },
    // 是否在点击节点的时候展开或者收缩节点， 默认值为 true，如果为 false，则只有点箭头图标的时候才会展开或者收缩节点。
    expandOnClickNode: {
      type: Boolean,
      default: true
    },
    treeProps: {
      type: Object,
      default: () => {
        return {
          value: 'id',
          label: 'name',
          children: 'children'
        }
      }
    }
  },
  setup(props) {

    const {emit} = getCurrentInstance();
    const treeBoxRef = ref(null);

    const that = reactive({
      keyword: "",
      batchType: '',
      firstPage: true, // 第一次进入页面
      checkAll: false,
      checkAllArray: [],  // 所有全选的列表的id
      defaultCheckedKeys: [], // 默认选中
      defaultExpandedKeys: [],//默认展开
      currentLivingId: props.id,  //第一次进来的默认 id
      treeProps: props.treeProps,
      list: props.handleMenuArray,
      oldAllList: props.handleMenuArray, // 原始 树形结构数据
      show_checkbox: props.showCheckbox,
      defaultExpandAll: props.defaultExpandAll,
      handle_menu_top_index: props.handleMenuTopIndex,
    })

    //如果有数据了，设置默认选中的id，以及选中的样式
    const listLengthFun = () => {
      if (that.list && that.list.length) {
        that.defaultExpandedKeys = [];
        getChildrenFirstFun(that.list[0]);
      }
    };

    const getChildrenFirstFun = (item) => {
      if (item.children && item.children.length) {
        getChildrenFirstFun(item.children[0]);
        that.defaultExpandedKeys.push(item.id);
      } else {
        nextTick(() => {
          treeBoxRef.value.setCurrentKey(item.id);
          clickTreeNode(item);
        })
      }
    };

    // 点击树形结构数据
    const clickTreeNode = (item) => {
      if (that.show_checkbox) return
      that.currentLivingId = item.id;
      emit("handleMenuEvent", {menuType: "clickTreeNode", ...item});
    }

    //点击右上角添加按钮
    const clickRightBut = (operationType) => {

      // 一键展开折叠
      if(operationType === 1){
        try {
          that.defaultExpandAll = !that.defaultExpandAll;
          Object.values(treeBoxRef.value.store.nodesMap).forEach(v => v[that.defaultExpandAll ? 'expand' : 'collapse']());
        } catch (e) {}
      }

      if(operationType === 2){
        emit("handleMenuEvent", {menuType: "clickAddBut"});
      }
    }

    // 选择框 发生改变执行
    const checkChangeFun = () => {
      if (!that.show_checkbox) return
      let selectIdArray = [];
      nextTick(() => {
        let allSelectIdArray = treeBoxRef.value.getCheckedKeys();
        for (let i = 0; i < allSelectIdArray.length; i++) selectIdArray.push(allSelectIdArray[i]);
        // console.log("checkChangeFun" +  selectIdArray);
        emit("handleMenuEvent", {type: "clickTreeNode", allSelectIdArray: selectIdArray});
      })
    }

    // 设置全部选中或者，取消全部不选中
    const setTreeSelectCheck = (isSelectAll) => {
     
      let defaultCheckedKeys = [];
      if (isSelectAll) {
        let allCheckArray = treeToArray(that.list);
        for (let i = 0; i < allCheckArray.length; i++) defaultCheckedKeys.push(allCheckArray[i].id);
        // console.log(defaultCheckedKeys)
      }
      nextTick(() => {
        that.defaultCheckedKeys = defaultCheckedKeys;
        treeBoxRef.value.setCheckedKeys(defaultCheckedKeys);
        // 向父组件传递值
        if (props.isSelectAll) checkChangeFun();
      })
    }

    // 搜索框搜索
    const searchInput = () => {
      treeBoxRef.value.filter(that.keyword);
    };

    // 过滤树形控件数据
    const filterTreeNode = (value, data) => {
      if (!value) return true
      return data[that.treeProps.label].includes(value)
    }

    const clickBatchButton = (batchType) => {
      setCheckedKeysFun();
      that.batchType = batchType;
      that.show_checkbox = true;
    }

    // 取消选择
    const cancelSelect = () => {
      setCheckedKeysFun();
      that.show_checkbox = false;
    }

    // 点击某一条数据后的叉号
    const clickClosedIcon = (data)=>{
      emit("handleMenuEvent", {menuType: "clickClosedIcon", activeClickId: data.id });
    }

    const confirmSelect = () => {
      let selectIdArray = [];
      let allSelectIdArray = treeBoxRef.value.getCheckedKeys();
      for (let i = 0; i < allSelectIdArray.length; i++) selectIdArray.push(allSelectIdArray[i]);

      if (that.batchType === 1) {
        ElMessageBox.confirm(`您确定要删除选择的${props.title}吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning' }).then(() => {
          that.show_checkbox = false;
          emit("handleMenuEvent", {menuType: "clickBatchButton", batchType: that.batchType, selectCheckedArray: selectIdArray});
        }).catch(() => {
          console.log("取消删除");
        });
      }

      if (that.batchType === 2) {
        that.show_checkbox = false;
        emit("handleMenuEvent", {menuType: "clickBatchButton", batchType: that.batchType, selectCheckedArray: selectIdArray});
      }
    }

    // 设置节点选中的数据
    const setCheckedKeysFun = (checkedIdArray = []) => {
      that.defaultCheckedKeys = checkedIdArray;
      props.showCheckbox ? treeBoxRef.value.setCheckedKeys(checkedIdArray) : treeBoxRef.value.setCurrentKey(checkedIdArray[0]);
      emit("handleMenuEvent", {type: "clickTreeNode", allSelectIdArray: that.defaultCheckedKeys});
    }

    // id 发生改变重新设置选中的样式
    const watchId = watch(() => props.id, (newId) => {
      that.currentLivingId = newId;
      treeBoxRef.value.setCurrentKey(that.currentLivingId);
    }, { deep: true });

    // 顶部 选择列表
    const watchHandleMenuTopIndex = watch(() => that.handle_menu_top_index, (newHandleMenuTopIndex) => {
      emit("handleMenuEvent", {menuType: "searchInput", type: newHandleMenuTopIndex});
    }, {deep: true});

    const watchArray = watch([() => props.handleMenuArray, () => props.showCheckbox], ([newHandleMenuArray, newShowCheckbox]) => {
      that.show_checkbox = newShowCheckbox;
      if(JSON.stringify(newHandleMenuArray) !== JSON.stringify(that.oldAllList)){
        that.list = JSON.parse(JSON.stringify(newHandleMenuArray));
        that.oldAllList = JSON.parse(JSON.stringify(newHandleMenuArray));
      }
      if (props.dataRenewIsFun || that.firstPage || !that.currentLivingId || (!props.dataRenewIsFun && JSON.stringify(that.oldAllList).indexOf(that.currentLivingId) === -1)) {
        that.firstPage = false;
        that.show_checkbox ? setTreeSelectCheck(props.isSelectAll) : listLengthFun();
        nextTick(()=>{
          emit("handleMenuEvent", { type: "initHandleMenu",msg: "数据更新完成！" });
        })
      } else {
        nextTick(()=>{
          treeBoxRef.value.setCurrentKey(that.currentLivingId); // 单选
        })
      }
    }, {deep: true});

    onMounted(() => {
      listLengthFun();
    });

    return {...toRefs(that), listLengthFun, getChildrenFirstFun, clickTreeNode, treeBoxRef, watchArray, setTreeSelectCheck, searchInput, clickRightBut,
      clickBatchButton, cancelSelect, confirmSelect, setCheckedKeysFun, checkChangeFun, filterTreeNode, watchId, watchHandleMenuTopIndex,clickClosedIcon}
  }
})
</script>

<style lang="scss" scoped>
.handleMenu {
  width: 220px;
  min-width: 220px;
  //height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 2px;
  background: #FFFFFF;
  box-sizing: border-box;
  border-right: 1px solid #E3E3E3;

  .title {
    //height: 58px;
    color: #242424;
    font-size: 18px;
    font-weight: bold;
    padding: 4px 7px 12px 0;
    box-sizing: border-box;
    border-bottom: 1px solid #E3E3E3;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .rightTopButImg {
      color: #1F74E2;
      font-size: 22px;
      cursor: pointer;
      margin-left: 12px;
    }

    .rightTopButIcon {
      display: flex;
      align-items: center;

      .rightTopButImg:first-child {
        margin-left: 0;
      }

      .moreMenu {

        .menu_li {
          height: 25px;
          line-height: 25px;
          text-align: center;
          font-size: 14px;
          color: #242424;
          font-weight: 400;

          &:hover {
            cursor: pointer;
            background: #1F74E2;
          }
        }
      }
    }
  }

  .bottomContent {
    flex: 1;
    height: 2px;
    display: flex;
    flex-direction: column;
    box-sizing: border-box;
    padding: 12px 12px 12px 12px;

    :deep(.tabs){
      margin-bottom: 8px;
      .tabs_list{
        padding: 0;

        .tabs_left{
          width: 100%;

          .tabs_li{
            flex: 1;
            padding: 0;
            text-align: center;
          }
        }
      }
    }

    .searchInput{
      margin-bottom: 5px;
    }

    .list_menu {
      flex: 1;
      height: 2px;
      box-sizing: border-box;

      .list_menu_content {
        width: 100%;
        height: 100%;
        overflow-y: auto;
      }

      :deep(.rightArrowClass) {

        .el-tree-node__content {
          height: 40px;
          font-size: 14px;
          border-radius: 4px;
          box-sizing: border-box;

          .el-tree-node__label {
            flex: 1;
            width: 2px;
            display: block;
          }

          .custom-tree-node {
            width: 100%;
            display: flex;
            align-items: center;

            .el-tree-node__label {
              flex: 1;
            }

            .left_content {
              flex: 1;
              width: 2px;
              display: flex;
              align-items: center;

              .svg-icon, .iconfont {
                font-size: 16px;
                font-weight: bold;
                margin-right: 10px;
              }

              .icon-zhucaidan {
                color: #F9A230;
              }

              .icon-moxinglei {
                color: #1F74E2;
              }

              .label {
                flex: 1;
                display: flex;
                flex-wrap: wrap;
                align-items: center;
                padding-right: 2px;
                box-sizing: border-box;
                white-space: initial;
                -webkit-line-clamp: 1;
              }
            }

          }
        }

        .is-current {

          .el-tree-node__content {
            color: #1F74E2;
            font-weight: bold;
            //background: $subMenuBg;
          }

          .el-tree-node__children {
            .el-tree-node__content {
              color: #606266;
              font-weight: 400;
              background: none;
            }
          }

          & > .el-tree-node {
            color: #1F74E2 !important;
          }
        }

        .is-expanded {
          .right_content {
            transform: rotate(180deg);
          }
        }

      }
    }

    .displayBottomBut {
      width: 100%;
      height: 24px;
      display: flex;

      .button {
        flex: 1;
        cursor: pointer;
        font-size: 12px;
        color: #FFFFFF;
        background: #61A1EE;
        line-height: 24px;
        text-align: center;
      }

      .cancel {
        background: #EEEEEE;
        color: #BBBBBB;
      }
    }
  }

  :deep(.typeDetailClass) {
    height: 16px;
    color: #FFFFFF;
    font-size: 12px;
    padding: 0 4px;
    margin-left: 4px;
    border-radius: 4px;
    line-height: 16px;
    background-color: #1F74E2;
    box-sizing: border-box;
  }
}
</style>

<style lang="scss">
.leftArrowClass {
  --el-fill-color-light: none;
  --el-fill-color-blank: none;

  .el-tree-node {
    position: relative;
    padding: 7px 0 7px 16px; // 缩进量

    .el-tree-node__content {
      height: 32px;

      .el-tree-node__label {
        flex: 1;
        border-radius: 4px;
        padding-left: 10px;
        box-sizing: border-box;
        background: none;
        color: #333333;
        transition: all 0.28s;

        display: -webkit-box;
        white-space: normal;
        word-break: break-all;
        text-overflow: ellipsis;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        overflow: hidden;
      }

      .el-icon {
        width: 14px;
        height: 14px;
        display: flex;
        align-items: center;
        justify-content: center;
        border: 1px solid #1E71EC;
        border-radius: 2px;
        padding: 0;
        transform: none;
        margin-right: 8px;
        margin-left: 0;
        visibility: initial;

        svg {
          display: none;
        }

        &::after {
          content: "+";
          font-size: 14px;
          color: #1E71EC;
        }
      }

      .el-checkbox__input.is-disabled.is-checked .el-checkbox__inner {
        background-color: #04142c;
        border-color: var(--el-checkbox-disabled-checked-input-border-color);
      }

      .expanded {
        &::after {
          content: "-";
        }
      }
    }

    // 竖线
    &::before {
      content: "";
      height: 100%;
      width: 1px;
      position: absolute;
      left: -8px;
      top: -22px;
      border-width: 1px;
      border-left: 1px solid rgba(189, 202, 212, 0.6);
    }

    // 当前层最后⼀个节点的竖线⾼度固定
    &:last-child::before {
      height: 45px; // 可以⾃⼰调节到合适数值
    }

    .el-tree-node__children {
      padding-left: 16px; // 缩进量

      .is-leaf {
        width: 1px;
        border: none;
        //margin-right: 0;
        position: relative;

        &::after {
          content: "" !important;
          width: 1px;
          height: 12px;
          background: rgba(30, 113, 236, 0.6);
          border-radius: 1px;
          position: absolute;
          left: 0;
          top: 1px;
        }

      }
    }

    // 横线
    &::after {
      content: "";
      width: 20px;
      height: 8px;
      position: absolute;
      left: -7px;
      top: 23px;
      border-width: 1px;
      border-top: 1px solid rgba(189, 202, 212, 0.6);
    }
  }

  // 去掉最顶层的虚线，放最下⾯样式才不会被上⾯的覆盖了
  & > .el-tree-node::after {
    border-top: none;
  }

  & > .el-tree-node::before {
    border-left: none;
  }

  // 叶⼦节点（⽆⼦节点）
  .is-leaf {
    &::after {
      content: "-" !important;
    }
  }

  .is-current {

    // 只有最后一级才有选中的状态
    .is-leaf + .el-tree-node__label {
      height: 100%;
      font-weight: bold;
      line-height: 32px;
      -webkit-line-clamp: 1;
      color: #333333;
      background: rgba(30, 113, 236, 0.6);
    }

    .el-tree-node__children {
      .el-tree-node__label {
        background: none;
        font-weight: 400;
        color: #333333;
      }
    }
  }

  .is-focusable {
    background: none;
  }
}
</style>
