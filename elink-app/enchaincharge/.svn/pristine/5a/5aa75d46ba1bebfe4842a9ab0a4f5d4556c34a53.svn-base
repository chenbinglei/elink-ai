<template>
	<view class="container">

		<!-- 订单正在结算中 -->
		<template v-if="orderSettlement == 1">
			<view class="flex-center uni-column align-center">
				<ourLoading componentName="loop" active :text="pileStaticData.operationType == 2 ? '取消预约中...' : '订单结束中...' " />
				<view class="text">请耐心等待，会自动完成</view>
			</view>
		</template>

		<template v-if="orderSettlement == 2">
			<view class="order_details">
				<view class="order_top"></view>
				<view class="order_centent">
					<view class="order_centent_card">
						<view class="centent_card_top">
							<text class="iconfont icon-zhengchang"></text>
							<view class="text" v-if="pileStaticData.operationType == 2">取消预约成功</view>
							<view class="text" v-else>订单已结束</view>
						</view>
						
						<view class="card_list">
							<view class="card_list_left">订单编号</view>
							<view class="card_list_right">{{ pileStaticData.orderNum | moreData }}</view>
						</view>
						
						<view class="card_list" v-if="pileStaticData.operationType != 2">
							<view class="card_list_left">{{ orderDetails.orderType == 2 ? '收益' : '充电' }}金额</view>
							<view class="card_list_right">{{ orderDetails.totalCost | moneyTwoNum }}元</view>
						</view>
							
						<view class="card_list" v-if="orderDetails.orderType == 1 && orderDetails.prepayMoney > 0">
							<view class="card_list_left">退回金额</view>
							<view class="card_list_right">{{ orderDetails.refundMoney | moneyTwoNum }}元</view>
						</view>
						
						<template v-if="pileStaticData.operationType != 2">
							<view class="card_list">
								<view class="card_list_left">{{ '充电' | textValue(orderDetails.orderType) }}电量</view>
								<view class="card_list_right">{{ orderDetails.totalQt | moreData}}度</view>
							</view>
							
							<view class="card_list">
								<view class="card_list_left">{{ '充电' | textValue(orderDetails.orderType) }}时长</view>
								<view class="card_list_right">{{ setDurationFun(orderDetails.chargeDuration) | moreData }}</view>
							</view>
						</template>

					</view>
				</view>
			</view>
		</template>
		

		<template v-if="orderSettlement == 3">
			<view class="flex-center uni-column align-center">
				<image class="error_image" src="@/secondPackage/static/image/errorImage.png"></image>
				<view class="text">订单结束失败</view>
				<view class="text">{{ errorMsg }}</view>
			</view>
		</template>

		<view class="content_bottom" v-if="orderSettlement != 1">
			<template v-if="orderSettlement == 2 && pileStaticData.operationType != 2">
				<view class="button buttonBg" @click="goToPage(1)">查看订单</view>
			</template>
			<view class="button" @click="goToPage(2)">返回主页</view>
		</view>

	</view>
</template>

<script>
	import { mapState } from 'vuex';
	import { pileStop } from '@/api/home.js';
	import ourLoading from '@/components/our-loading/our-loading.vue';
	import { queryMyOrderDetailsByOrderNum } from '@/secondPackage/api/index.js';
	
	export default {
		name: 'orderSettlement',
		components: { ourLoading },
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {
				pileStaticData: {
					// operationType: 1,  // 1: 停止  2；取消预约
					// isAutoPileStop: false, // 是否自动或者手动停止
					// orderNum: this.orderNum, // 订单编号
					// pileCode: this.pileStaticData.pileCode,
					// gunCode: this.pileStaticData.gunCode
				}, // 停止或者取消预约数据
				errorNum: 1, // 失败次数
				orderDetails: {}, // 订单结束的数据
				errorMsg: '', // 失败提示语
				orderSettlement: 1, // 1: 订单正在结算中   2:成功    3：失败
			};
		},
		onLoad(options) {
			this.pileStaticData = JSON.parse(decodeURIComponent(options.data));
			this.stopCmdSet();
		},
		methods: {
			// 停止充电 (手动停止充电或者预约)
			stopCmdSet() {
				pileStop({
					// userid: this.userInfo.id,
					gunCode: this.pileStaticData.gunCode,
					pileCode: this.pileStaticData.pileCode,
					serialNum: this.pileStaticData.orderNum,
					type: this.pileStaticData.operationType, //1 -> 结束充电  2 ->取消预约
				}).then(res => {
					this.getOrderEndInfoByOrderId();
				}).catch(err => {
					setTimeout(() => {
						this.orderSettlement = 3;
						this.errorMsg = err.message;
					}, 2000);
				});
			},
			// 根据订单号查询订单结束完成界面信息
			getOrderEndInfoByOrderId() {
				queryMyOrderDetailsByOrderNum({ orderNum: this.pileStaticData.orderNum,showLoading: true }).then(res => {
					let resData = res.data ? res.data : {};
					resData.orderType = resData.runMode + 1; // 运行模式 1-充电订单 2-放电订单
					resData.refundMoney = resData.settlementRecord.refundMoney; // 退回金额
					this.orderDetails = JSON.parse(JSON.stringify(resData));
					
					if (resData.refundMoney === '' || resData.refundMoney === null || resData.refundMoney === 'null') {
						// 再次重新查询
						if (this.errorNum >= 10) {
							// 大于等于十次，都算成功
							this.orderSettlement = 2;
							// this.errorMsg = '系统错误，请稍后重试！';
						} else {
							setTimeout(() => {
								this.errorNum++;
								this.getOrderEndInfoByOrderId();
							}, 2000);
						}
						return;
					}

					setTimeout(() => {
						this.orderSettlement = 2;
					}, 2000);
				}).catch(err => {
					setTimeout(() => {
						this.orderSettlement = 3;
						this.errorMsg = err.message || '系统错误，请稍后重试！';
					}, 2000);
				});
			},
			setDurationFun(duration) {
				let durationStr = '';
				if (duration) {
					let durationArr = duration.split(":");
					durationStr = `${ durationArr[0]*1 }小时${ durationArr[1]*1 }分${ durationArr[2]*1 }秒`
				}
				return durationStr
			},
			goToPage(pageType) {
				if (pageType == 1) {
					if (this.orderSettlement == 2 && this.pileStaticData.operationType != 2) {
						uni.redirectTo({
							url: `/firstPackage/orderManagement/orderDetails?orderNum=${this.pileStaticData.orderNum}`
						});
						return;
					}
					uni.navigateBack({
						delta: 1
					});
				}
				if (pageType == 2) {
					uni.switchTab({
						url: '/pages/charging/index'
					});
				}
			}
		}
	}
</script>

<style scoped lang="scss">
	.container {

		.flex-center {
			padding-top: 240rpx;

			.text {
				font-size: 28rpx;
				margin-top: 24rpx;
				color: rgba(0, 0, 0, 0.9);
			}

			/deep/ .mask {
				position: initial !important;
				background: none !important;
			}
		}

		.error_image {
			width: 112rpx;
			height: 112rpx;
			margin-bottom: 24rpx;
		}

		.order_details {
			padding: 32rpx 24rpx;
			box-sizing: border-box;

			.order_top {
				width: 100%;
				height: 32rpx;
				border-radius: 20rpx;
				background-color: #ffffff;
				border: 2rpx solid #476AE2;
				box-shadow: inset 0px 0rpx 10rpx 0px #476AE2;
			}

			.order_centent {
				padding: 0 10rpx;
				margin-top: -16rpx;
				box-sizing: border-box;

				.order_centent_card {
					box-sizing: border-box;
					background-color: #ffffff;
					padding: 86rpx 20rpx 48rpx 20rpx;
					border-radius: 4rpx 4rpx 12rpx 12rpx;

					.centent_card_top {
						display: flex;
						align-items: center;
						justify-content: center;
						margin-bottom: 120rpx;

						.text {
							margin-left: 8rpx;
							font-weight: bold;
							font-size: 36rpx;
							color: #476AE2;
						}

						.iconfont {
							color: #476AE2;
							font-size: 56rpx;
						}
					}

					.card_list {
						display: flex;
						align-items: center;
						justify-content: space-between;
						margin-bottom: 24rpx;
						padding: 12rpx 24rpx;
						box-sizing: border-box;

						.card_list_left {
							font-size: 28rpx;
							color: rgba(0, 0, 0, 0.4);
						}

						.card_list_right {
							font-size: 24rpx;
							color: rgba(0, 0, 0, 0.9);
						}

						&:last-child {
							margin-bottom: 0;
						}

					}

				}
			}
		}


		.content_bottom {
			padding: 24rpx 32rpx 64rpx 32rpx;
			box-sizing: border-box;

			.button {
				height: 92rpx;
				font-size: 36rpx;
				color: #476AE2;
				text-align: center;
				line-height: 92rpx;
				border-radius: 14rpx;
				border: 2rpx solid #476AE2;
				border-radius: 12rpx;
			}

			.buttonBg {
				color: #ffffff;
				background: #476AE2;
				margin-bottom: 18rpx;
				border: none;
			}
		}
	}
</style>