<template>
	<view class="container">
		<!-- <sm-custom-head title="扫码充电" :arrowStatus="false"></sm-custom-head> -->
		<view class="container_content">
			<!-- 占用订单 -->
			<OccupyOrder ref="occupyOrderRef"></OccupyOrder>
			<view class="container_flex">
				<ongoing-orders ref="ongoingOrdersRef"></ongoing-orders>
			</view>
		</view>
		<view class="bottom_but">
			<view class="saoma_black" @click="$noMultipleClicks(goToPage,'/fourthPackage/pages/scanCode')">
				<text class="iconfont icon-saoma"></text>
				<text class="text">扫一扫充电</text>
			</view>
		</view>
	</view>
</template>

<script>
	import OccupyOrder from "./components/OccupyOrder.vue";
	import OngoingOrders from "./components/OngoingOrders.vue";
	import { queryMyOrderListByPage } from "@/secondPackage/api/index.js";
	
	export default {
		name: "charging",
		components:{OccupyOrder,OngoingOrders},
		data() {
			return {
				noClick: true,
			}
		},
		onShow() {
			this.pagingReload();
		},
		onHide() {
			this.$refs.ongoingOrdersRef.closeWebSocket();
		},
		methods: {
			pagingReload() {
				// 查看用户是否登录
				if(this.$lockUserIfLogin()){
					this.$refs.ongoingOrdersRef.connectSocketInit();
					// this.$refs.occupyOrderRef.queryAppInHandOrderListByMemberId();
				} else {
					this.$refs.ongoingOrdersRef.orderList = [];
					// this.$refs.occupyOrderRef.occupyArray = [];
				}
			},
			goToPage(pageName) {
				// 查看用户是否登录
				if (this.$lockUserIfLogin(2)) {
					uni.navigateTo({ url: pageName });
				}
			}
		}
	}
</script>

<style scoped lang="scss">
	.container_flex{
		display: block !important;
		padding: 24rpx 32rpx;
	}
	
	.bottom_but{
		width: 100%;
		padding: 12rpx 32rpx;
		box-sizing: border-box;
		background-color: #FFFFFF;
		box-shadow: 0px 0px 16rpx 0px rgba(0, 0, 0, 0.1);
		
		.saoma_black{
			height: 88rpx;
			background: #476AE2;
			border-radius: 16rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			
			.iconfont,.text {
				color: #FFFFFF;
				font-size: 36rpx;
				font-weight: 500;
			}
		}
	}
</style>