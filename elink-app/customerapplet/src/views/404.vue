<template>
	<div class="app-container">
		<div class="wscn-http404">
			<div class="pic-404">
				<img class="pic-404__parent" :src="img_404" alt="404">
				<img class="pic-404__child left" :src="img_404_cloud" alt="404">
				<img class="pic-404__child mid" :src="img_404_cloud" alt="404">
				<img class="pic-404__child right" :src="img_404_cloud" alt="404">
			</div>
			<div class="bullshit">
				<div class="bullshit__oops">喔唷!</div>
				<div class="bullshit__headline">{{ message }}</div>
				<div class="bullshit__info">请检查您输入的网址是否正确，请点击以下按钮返回主页或者发送错误报告</div>
        <!--        <div class="bullshit__return-home" @click="clickBack">返回上一级</div>-->
			</div>
		</div>
	</div>
</template>

<script>
  import { useRouter } from "vue-router";
	import img_404 from '@/assets/images/404.png'
	import img_404_cloud from '@/assets/images/404_cloud.png'
  import { shallowRef, onMounted, onUnmounted, toRefs, ref, reactive, computed } from "vue";
	export default {
    name:"404",
    setup(props){
      const vueRouter = useRouter();
      const that = reactive({
        img_404, img_404_cloud,
        message: '大佬，你输入的页面我们找不到啊......'
      });

      const clickBack = ()=>{
        vueRouter.back();
      }

      return { ...toRefs(that),clickBack };
    }
	}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.app-container{
  background-color: #f0f2f5;
  display: flex;
  justify-content: center;
  align-items: center;

  .wscn-http404 {
    width: 100%;
    position: relative;
    padding: 0 .12rem;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    align-items: center;

    .pic-404 {
      position: relative;
      width: 100%;
      padding: .15rem 0;
      overflow: hidden;

      &__parent {
        width: 100%;
      }

      &__child {
        position: absolute;

        &.left {
          width: .8rem;
          top: .17rem;
          left: .22rem;
          opacity: 0;
          animation-name: cloudLeft;
          animation-duration: 2s;
          animation-timing-function: linear;
          animation-fill-mode: forwards;
          animation-delay: 1s;
        }

        &.mid {
          width: .46rem;
          top: .1rem;
          left: .42rem;
          opacity: 0;
          animation-name: cloudMid;
          animation-duration: 2s;
          animation-timing-function: linear;
          animation-fill-mode: forwards;
          animation-delay: 1.2s;
        }

        &.right {
          width: .62rem;
          top: 1rem;
          left: 5rem;
          opacity: 0;
          animation-name: cloudRight;
          animation-duration: 2s;
          animation-timing-function: linear;
          animation-fill-mode: forwards;
          animation-delay: 1s;
        }

        @keyframes cloudLeft {
          0% {
            top: .17rem;
            left: 2.2rem;
            opacity: 0;
          }

          20% {
            top: .33rem;
            left: 1.88rem;
            opacity: 1;
          }

          80% {
            top: .81rem;
            left: .92rem;
            opacity: 1;
          }

          100% {
            top: .97rem;
            left: .6rem;
            opacity: 0;
          }
        }

        @keyframes cloudMid {
          0% {
            top: .1rem;
            left: 4.2rem;
            opacity: 0;
          }

          20% {
            top: .4rem;
            left: 3.6rem;
            opacity: 1;
          }

          70% {
            top: 1.3rem;
            left: 1.8rem;
            opacity: 1;
          }

          100% {
            top: 1.6rem;
            left: 1.2rem;
            opacity: 0;
          }
        }

        @keyframes cloudRight {
          0% {
            top: 1rem;
            left: 5rem;
            opacity: 0;
          }

          20% {
            top: 1.2rem;
            left: 4.6rem;
            opacity: 1;
          }

          80% {
            top: 1.8rem;
            left: 3.4rem;
            opacity: 1;
          }

          100% {
            top: 2rem;
            left: 3rem;
            opacity: 0;
          }
        }
      }
    }

    .bullshit {
      position: relative;
      float: left;
      width: 3.6rem;
      padding: .15rem 0;
      overflow: hidden;

      &__oops {
        font-size: .32rem;
        font-weight: bold;
        line-height: .4rem;
        color: #1482f0;
        opacity: 0;
        margin-bottom: .2rem;
        animation-name: slideUp;
        animation-duration: 0.5s;
        animation-fill-mode: forwards;
      }

      &__headline {
        font-size: .2rem;
        line-height: .24rem;
        color: #1482f0;
        opacity: 0;
        margin-bottom: .1rem;
        animation-name: slideUp;
        animation-duration: 0.5s;
        animation-delay: 0.1s;
        animation-fill-mode: forwards;
      }

      &__info {
        font-size: .13rem;
        line-height: .21rem;
        color: grey;
        opacity: 0;
        margin-bottom: .3rem;
        animation-name: slideUp;
        animation-duration: 0.5s;
        animation-delay: 0.2s;
        animation-fill-mode: forwards;
      }

      &__return-home {
        display: block;
        float: left;
        width: 1.1rem;
        height: .36rem;
        background: #1482f0;
        border-radius: 1rem;
        text-align: center;
        color: #ffffff;
        opacity: 0;
        font-size: .14rem;
        line-height: .36rem;
        cursor: pointer;
        animation-name: slideUp;
        animation-duration: 0.5s;
        animation-delay: 0.3s;
        animation-fill-mode: forwards;
      }

      @keyframes slideUp {
        0% {
          transform: translateY(.6rem);
          opacity: 0;
        }

        100% {
          transform: translateY(0);
          opacity: 1;
        }
      }
    }
  }
}
</style>
