<template>
  <div>
    <div class="block-box" v-for="(data, id) in list" :key="'box' + id">
      <div class="block-box-title">{{ data.name }}</div>
      <div class="block-list">
        <div
          class="block-list-content"
          v-for="(item, index) in data.list"
          :key="'gfblock' + index"
        >
          <div class="block" :class="item.color">
            <div class="block-title">{{ item.title }}</div>
            <div class="block-content">
              <div class="block-left">
                <div class="block-icon">
                  <i :class="data.class"></i>
                  <span>{{ item.text }}</span>
                </div>
              </div>
              <div class="block-right">
                <div
                  class="block-text"
                  v-for="(child, idx) in item.details"
                  :key="'blocktext' + idx"
                >
                  <p>{{ child.text }}</p>
                  <div class="flex_e_c">
                    <span>{{ child.value }}</span>
                    <p>{{ child.unit }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { defineProps} from "vue";
defineProps({
   list: {
    type: Array,
    default: () => [],
  }
})
</script>

<style scoped lang="scss">
.flex_e_c {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}
.block-box {
  padding: 24px 14px 0 24px;
  height: 100%;
  overflow-y: scroll;

  .block-box-title {
    font-size: 18px;
    margin-bottom: 20px;

    &::before {
      display: inline-block;
      margin-right: 22px;
      content: "";
      width: 0px;
      height: 10px;
      box-shadow: 0px 0px 6px 1px #ffffff;
      border: 2px solid #ffffff;
    }
  }

  .block-list {
    display: flex;
    flex-wrap: wrap;
    font-size: 14px;
    .block-list-content {
      width: 25%;
      height: 100px;
      padding-right: 10px;
      margin-bottom: 6px;
      box-sizing: border-box;
      .block {
        margin-bottom: 6px;
        .block-content {
          display: flex;
          align-items: center;
        }
        .block-title {
          font-size: 14px;
          color: #fff;
          line-height: 20px;
          text-align: center;
        }
        .block-icon {
          width: 44px;
          height: 80px;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-direction: column;
          font-size: 12px;

          i {
            display: block;
          }

          .nbq {
            width: 20px;
            height: 18px;
            margin-bottom: 7px;
          }

          .qxz {
            width: 23px;
            height: 26px;
            margin-bottom: 1px;
          }

          .hdc {
            width: 20px;
            height: 14px;
            margin-bottom: 9px;
          }

          .wdjc {
            width: 11px;
            height: 21px;
            margin-bottom: 7px;
          }

          .cdq {
            width: 20px;
            height: 18px;
            margin-bottom: 7px;
          }

          .dcc {
            width: 18px;
            height: 22px;
            margin-bottom: 4px;
          }

          .pcs {
            width: 19px;
            height: 20px;
            margin-bottom: 6px;
          }
        }

        .block-right {
          padding-left: 8px;
          width: 165px;
          height: 60px;
          font-size: 14px;
          display: flex;
          flex-direction: column;
          justify-content: space-around;
          align-items: center;

          .block-text {
            width: 100%;
            display: flex;
            align-items: center;
            justify-content: space-between;
            line-height: 21px;
            font-size: 12px;
            span {
              font-size: 18px;
              color: #00ccff;
            }
            .flex_e_c {
              p {
                margin-left: 4px;
                width: 35px;
                font-size: 14px;
              }
            }
          }
        }

        &.green {
          background: url("@/assets/image/station-details/item-bg-green.png")
            no-repeat;
          background-size: 100% 100%;

          .block-icon {
            color: #34e800;

            .nbq {
              background: url("@/assets/image/station-details/inverter-icon1.png")
                top left no-repeat;
            }

            .qxz {
              background: url("@/assets/image/station-details/run-icon1.png")
                top left no-repeat;
            }

            .hdc {
              background: url("@/assets/image/station-details/battery-icon1.png")
                top left no-repeat;
            }

            .wdjc {
              background: url("@/assets/image/station-details/temperature-icon1.png")
                top left no-repeat;
            }

            .cdq {
              background: url("@/assets/image/station-details/charginggun-icon1.png")
                top left no-repeat;
            }

            .dcc {
              background: url("@/assets/image/station-details/power-icon1.png")
                top left no-repeat;
            }

            .pcs {
              background: url("@/assets/image/station-details/pcs-icon1.png")
                top left no-repeat;
            }
          }
        }

        &.blue {
          background: url("@/assets/image/station-details/item-bg-blue.png")
            no-repeat;
          background-size: 100% 100%;

          .block-icon {
            color: #61b2ff;

            .nbq {
              background: url("@/assets/image/station-details/inverter-icon2.png")
                top left no-repeat;
            }

            .hdc {
              background: url("@/assets/image/station-details/battery-icon2.png")
                top left no-repeat;
            }

            .cdq {
              background: url("@/assets/image/station-details/charginggun-icon6.png")
                top left no-repeat;
            }

            .dcc {
              background: url("@/assets/image/station-details/power-icon2.png")
                top left no-repeat;
            }

            .pcs {
              background: url("@/assets/image/station-details/pcs-icon2.png")
                top left no-repeat;
            }
          }
        }

        &.red {
          background: url("@/assets/image/station-details/item-bg-red.png")
            no-repeat;
          background-size: 100% 100%;

          .block-icon {
            color: #ff0000;

            .nbq {
              background: url("@/assets/image/station-details/inverter-icon3.png")
                top left no-repeat;
            }

            .hdc {
              background: url("@/assets/image/station-details/battery-icon3.png")
                top left no-repeat;
            }

            .wdjc {
              background: url("@/assets/image/station-details/temperature-icon3.png")
                top left no-repeat;
            }

            .cdq {
              background: url("@/assets/image/station-details/charginggun-icon3.png")
                top left no-repeat;
            }

            .dcc {
              background: url("@/assets/image/station-details/power-icon3.png")
                top left no-repeat;
            }

            .pcs {
              background: url("@/assets/image/station-details/pcs-icon3.png")
                top left no-repeat;
            }
          }
        }

        &.cyan {
          background: url("@/assets/image/station-details/item-bg-cyan.png") top
            left no-repeat;
        }

        &.yellow {
          background: url("@/assets/image/station-details/item-bg-yellow.png")
            no-repeat;
          background-size: 100% 100%;

          .block-icon {
            color: #ffbb00;

            .wdjc {
              background: url("@/assets/image/station-details/temperature-icon2.png")
                top left no-repeat;
            }

            .cdq {
              background: url("@/assets/image/station-details/charginggun-icon7.png")
                top left no-repeat;
            }
          }
        }

        &.skyblue {
          background: url("@/assets/image/station-details/item-bg-skyblue.png")
            no-repeat;
          background-size: 100% 100%;

          .block-icon {
            color: #9fdeff;

            .hdc {
              background: url("@/assets/image/station-details/battery-icon4.png")
                top left no-repeat;
            }

            .cdq {
              background: url("@/assets/image/station-details/charginggun-icon2.png")
                top left no-repeat;
            }
          }
        }

        &.purple {
          background: url("@/assets/image/station-details/item-bg-purple.png")
            no-repeat;
          background-size: 100% 100%;

          .block-icon {
            color: #fa64ff;

            .nbq {
              background: url("@/assets/image/station-details/inverter-icon4.png")
                top left no-repeat;
            }

            .dcc {
              background: url("@/assets/image/station-details/power-icon4.png")
                top left no-repeat;
            }

            .pcs {
              background: url("@/assets/image/station-details/pcs-icon4.png")
                top left no-repeat;
            }
          }
        }

        &.gray {
          background: url("@/assets/image/station-details/item-bg-gray.png")
            no-repeat;
          background-size: 100% 100%;

          .block-icon {
            color: #aaaaaa;

            .hdc {
              background: url("@/assets/image/station-details/battery-icon5.png")
                top left no-repeat;
            }

            .wdjc {
              background: url("@/assets/image/station-details/temperature-icon4.png")
                top left no-repeat;
            }

            .cdq {
              background: url("@/assets/image/station-details/charginggun-icon4.png")
                top left no-repeat;
            }

            .dcc {
              background: url("@/assets/image/station-details/power-icon5.png")
                top left no-repeat;
            }
          }
        }
      }
    }
  }
}
</style>