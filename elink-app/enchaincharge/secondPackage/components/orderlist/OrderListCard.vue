<template>
	<view class="orderListCard" @click="clickOrderCard">
		<view class="order_li_title">
			<view class="order_li_title_left">
				<text class="text twoShowText">{{ orderInfo.siteName | moreData }}</text>
				<text class="u-arrow u-arrow-right"></text>
			</view>
			<view class="order_li_title_right">
				<view class="orderTime" v-if="orderInfo.orderStatus == 1">
					<text class="iconfont icon-a-shijian1"></text>
					<text>进行中</text>
				</view>
				<view class="orderTime" v-if="orderInfo.orderStatus == 2">
					<text class="iconfont icon-a-shijian1"></text>
					<text>已完成</text>
				</view>
				<view class="canceled error" v-if="orderInfo.orderStatus == 3">启动失败</view>
				<view class="canceled error" v-if="orderInfo.orderStatus == 4">订单挂起</view>
				<view class="canceled" v-if="orderInfo.orderStatus == 5">已取消</view>
			</view>
		</view>
		<view class="order_li_centent">
			<view class="order_li_centent_li">
				<view class="left_text">
					<text class="iconfont orderNum icon-dingdanbianhao"></text>
					<text>订单编号：</text>
				</view>
				<view class="right_text">
					<text class="twoShowText">{{ orderInfo.orderNum | moreData }}</text>
					<!-- <view class="copy_but" @click.stop="clickCopyBut">复制</view> -->
				</view>
			</view>
			<template v-if="orderInfo.orderStatus == 6">
				<view class="order_li_centent_li">
					<view class="left_text">
						<text class="iconfont icon-shijian"></text>
						<text>预约时间：</text>
					</view>
					<view class="right_text">{{ orderInfo.clockingTime | moreData }}</view>
				</view>
			</template>
			<view class="order_li_centent_li" v-if="orderInfo.orderStatus == 1 || orderInfo.orderStatus == 2">
				<view class="left_text">
					<text class="iconfont icon-shijian"></text>
					<text>开始时间：</text>
				</view>
				<view class="right_text">{{ orderInfo.startTime | moreData }}</view>
			</view>
			<view class="order_li_centent_li">
				<view class="left_text">
					<text class="iconfont icon-shijian"></text>
					<text>创建时间：</text>
				</view>
				<view class="right_text">{{ orderInfo.createTime | moreData }}</view>
			</view>
			<view class="order_li_centent_li" v-if="orderInfo.orderStatus == 1 || orderInfo.orderStatus == 2">
				<view class="left_text">
					<text class="iconfont icon-shijian"></text>
					<text>订单时长：</text>
				</view>
				<view class="right_text">{{ orderInfo.chargeDuration | moreData }}</view>
			</view>
			<view class="order_li_centent_li" v-if="orderInfo.prepayMoney > 0">
				<view class="left_text">
					<text class="iconfont icon-jine"></text>
					<text v-if="orderInfo.orderStatus == 6">预付金额：</text>
					<text v-else>订单金额：</text>
				</view>
				<view class="right_text" v-if="orderInfo.orderStatus == 6">{{ orderInfo.prepayMoney | moneyTwoNum }}元</view>
				<view class="right_text" v-else>{{ orderInfo.totalCost | moneyTwoNum }}元</view>
			</view>
			<view class="order_li_centent_li" v-if="orderInfo.orderStatus == 1 || orderInfo.orderStatus == 2">
				<view class="left_text">
					<text class="iconfont icon-chongdianfangshi"></text>
					<text>{{ '充电电量' | textValue(orderInfo.chargeMode) }}：</text>
				</view>
				<view class="right_text">{{ orderInfo.totalQt | moreData }}度</view>
			</view>
		</view>
		<view class="order_li_bottom">
			<view class="text" v-if="!orderInfo.orderStatus">订单未进行</view>
			<view class="button " v-if="orderInfo.orderStatus == 1" :class="orderInfo.chargeMode == 1 ? 'charge' : 'disCharge'" @click.stop="clickStopPile(1)">
				{{ '停止充电' | textValue(orderInfo.chargeMode) }}
			</view>
			<view class="text" v-if="orderInfo.orderStatus == 2">
				{{ orderInfo.endTime | moreData }} 
				<template v-if="orderInfo.stopDetailReason">{{ orderInfo.stopDetailReason | moreData }}</template>
			</view>
			<view class="text" v-if="orderInfo.orderStatus == 3">充电桩启动失败！</view>
			<view class="text" v-if="orderInfo.orderStatus == 4">订单挂起，可联系客服处理！</view>
			<view class="text" v-if="orderInfo.orderStatus == 5">订单已关闭</view>
			<view class="button subscribe" v-if="orderInfo.orderStatus == 6" @click="clickStopPile(2)">取消预约</view>
			<view class="text" v-if="orderInfo.orderStatus == 8">{{ orderInfo.endTime | moreData }} 取消预约，结算完成</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: "OrderListCard",
		props: {
			orderInfo: {
				type: Object,
				default: () => {
					return {}
				}
			}
		},
		data() {
			return {

			}
		},
		methods: {
			clickOrderCard() {
				// 已完成订单才可进入订单详情
				if (this.orderInfo.orderStatus == 2) {
					uni.navigateTo({
						url: `/firstPackage/orderManagement/orderDetails?orderNum=${ this.orderInfo.orderNum }`
					});
				}
			},
			clickCopyBut() {
				uni.setClipboardData({
					data: this.orderInfo.orderNum,
					success: function() {
						console.log('success');
					}
				});
			},
			// 取消预约 停止充电
			clickStopPile(operationType) {
				let chargeText  = "充电";
				if(this.orderInfo.chargeMode === 2) chargeText  = "放电";
				
				uni.showModal({
					title: '提示',
					content: `确定取消${ operationType == 2 ? '预约' : chargeText }吗？`,
					success: (res) => {
						if (res.confirm) {
							let data = {
								gunCode: this.orderInfo.gunCode,
								pileCode: this.orderInfo.pileCode,
								orderNum: String(this.orderInfo.orderNum), // 订单编号
								operationType: operationType //1 -> 结束充电 2 ->取消预约
							};
							
							uni.navigateTo({
								url: `/secondPackage/orderManagement/orderSettlement?data=${encodeURIComponent(JSON.stringify(data))}`
							});
						}
					}
				});
			}
		}
	}
</script>

<style scoped lang="scss">
	.orderListCard {
		border-radius: 12rpx;
		padding: 28rpx 24rpx;
		box-sizing: border-box;
		background-color: #ffffff;
		margin-bottom: 20rpx;

		.order_li_title {
			display: flex;
			align-items: center;
			justify-content: space-between;
			padding-bottom: 24rpx;

			.order_li_title_left {
				flex: 1;
				display: flex;
				align-items: center;

				.text {
					font-size: 28rpx;
					font-weight: 600;
					color: #030303;
					-webkit-line-clamp: 1;
				}

				.u-arrow {
					border-color: #c7c7c7;
				}
			}

			.order_li_title_right {
				display: flex;
				align-items: center;
				margin-left: 10rpx;

				.orderTime {
					display: flex;
					align-items: center;
					color: #199d7c;
					font-size: 24rpx;

					.iconfont {
						color: #199D7C;
						font-size: 28rpx;
						margin-right: 6rpx;
					}
				}

				.canceled {
					font-size: 24rpx;
					color: #979797;
				}

				.error {
					font-size: 24rpx;
					color: #FC363F;
				}
			}
		}

		.order_li_centent {
			border-top: 2rpx solid #ebebeb;
			border-bottom: 2rpx solid #ebebeb;
			padding: 16rpx 0 20rpx 0;

			.order_li_centent_li {
				display: flex;
				align-items: center;
				margin-bottom: 10rpx;

				.left_text {
					font-size: 12px;
					color: #929395;
					display: flex;
					align-items: center;

					.iconfont {
						color: #A1A1A1;
						font-size: 28rpx;
						margin-right: 8rpx;
					}

					.orderNum {
						font-size: 32rpx;
					}
				}

				.right_text {
					flex: 1;
					color: #4e4e4e;
					font-size: 24rpx;
					display: flex;
					align-items: center;

					.twoShowText {
						flex: 1;
						-webkit-line-clamp: 1;
					}

					.copy_but {
						color: #476ae2;
						margin-left: 12rpx;
						padding: 4rpx 12rpx;
						border-radius: 6rpx;
						border: 2rpx solid #476ae2;
					}
				}
			}

			.order_li_centent_li:last-child {
				margin-bottom: 0;
			}
		}

		.order_li_bottom {
			display: flex;
			align-items: center;
			justify-content: flex-end;
			padding-top: 24rpx;

			.text {
				font-size: 24rpx;
				color: #979797;
			}

			.button {
				width: 134rpx;
				height: 54rpx;
				border-radius: 8rpx;
				margin-left: 16rpx;
				font-size: 24rpx;

				text-align: center;
				line-height: 54rpx;
			}

			.disCharge {
				border: 2rpx solid rgba(235, 230, 133, 1);
				color: rgba(235, 230, 133, 1);
			}

			.charge {
				border: 2rpx solid rgba(26, 150, 223, 1);
				color: rgba(26, 150, 223, 1);
			}
			
			.subscribe{
				border: 2rpx solid #199D7C;
				color: #199D7C;
			}
		}
	}
</style>