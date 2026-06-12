<template>
  <div class="routeOperationMenu">
  </div>
</template>

<script>
import { useAppStore } from '@/stores/index';

import {useRoute, useRouter} from "vue-router";
import {toRefs, reactive, defineComponent} from "vue";

export default defineComponent({
  name: "RouteOperationMenu",
  setup() {

    const appStore = useAppStore();
    const route = useRoute();
    const vueRouter = useRouter();

    const that = reactive({
      routesArray: JSON.parse(localStorage.getItem("SIDEBAR"))
    });

    const inputChange = () => {
      let routesArray = JSON.parse(localStorage.getItem("SIDEBAR"));
      // if (that.modelName) routesArray = selectTreeData(that.modelName, "title", routesArray, 'meta');
      that.routesArray = JSON.parse(JSON.stringify(routesArray));
      // console.log(that.routesArray);
    };


    return {...toRefs(that), inputChange };
  }
});
</script>

<style lang="scss" scoped>
.routeOperationMenu {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;

  .menus {
    width: 180px;
    height: 32px;
    //background: $ucBgC1;
    border-radius: 4px;
    //border: 1px solid $fgC1;
    padding-left: 12px;
    box-sizing: border-box;
    display: flex;
    align-items: center;

    .input_icont {
      .sunicon {
        //color: $fgGray;
        font-size: 16px;
      }
    }

    :deep(.el-input) {
      flex: 1;
      height: 100%;
      --el-border-color: none;
      --el-input-hover-border-color: none;
      --el-input-focus-border-color: none;

      .el-input__wrapper {
        height: 100%;
        padding: 0 12px 0 8px;

        .el-input__inner {
          text-align: center;
        }
      }
    }

    .input_right {
      //color: $fgC1;
      font-size: 16px;
      padding-right: 12px;

      .sunicon {
        transition: all .18s;
      }

      .rotateClass {
        transform: rotate(180deg);
      }
    }
  }

  .menu-dropdown {
    height: 520px;
    border-radius: 4px;
    //background: $ucBgC1;
    //border: 1px solid $ucBgC2;
    //box-shadow: 0 2px 8px 0 $bgC2-L100-A80;
    padding: 16px 16px;
    box-sizing: border-box;
    overflow-y: auto;

    :deep(.titleView) {
      .headerLeft {
        font-weight: initial !important;
      }

      .collapse_Content {
        margin-bottom: 16px;
        //border-bottom: 1px solid $fgC1-A90;
      }

      .dropdown_item {
        width: 100%;
        height: 80px;
        border-radius: 8px;
        //background: $fgC1-A90;
        padding: 2px 10px;
        box-sizing: border-box;
        display: flex;
        flex-direction: column;
        margin-bottom: 16px;

        .route_icon {
          flex: 1;
          height: 2px;
          display: flex;
          align-items: flex-end;
          justify-content: center;

          .sunicon {
            //color: $fgGray;
            font-size: 24px;
          }
        }

        .route_text {
          height: 32px;
          font-size: 12px;
          //color: $fgGray-A20;
          text-align: center;
        }

        &:hover {
          //background: $fgC1-A40;

          .route_text {
            //color: $fgGray;
          }
        }
      }

      .dropdown_item_active {
        //background: $fgC1;

        .route_text {
          //color: $fgGray;
        }
      }

      &:last-child {
        .collapse_Content {
          margin-bottom: 0;
          border-bottom: none;
        }
      }
    }
  }

  .line_content {
    width: 100%;
    height: 6px;
    position: absolute;
    left: 0;
    bottom: -3px;
    display: flex;
    justify-content: space-around;

    .line {
      //width: $header-dot-width;
      //height: $header-dot-width;
      border-radius: 50%;
      //background: $fgC1;
    }
  }

  .borderClass {
    width: 100%;
    height: 2px;
    position: absolute;
    left: 0;
    bottom: 0;
    //background: $header-border-color;
  }
}
</style>
