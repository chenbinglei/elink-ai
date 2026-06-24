<template>
	<view class="orderListCard" @click="clickOrderCard">
		<view class="order_li_title">
			<view class="order_li_title_left">
				<text class="text twoShowText">{{ orderInfo.siteName | moreData }}</text>
				<text class="u-arrow u-arrow-right"></text>
			</view>
			<view class="order_li_title_right">
				<view class="orderTime" v-if="orderInfo.orderState == 1">
					<text class="iconfont icon-a-shijian1"></text>
					<text>进行中</text>
				</view>
				<view class="canceled" v-if="orderInfo.orderState == 2">待支付</view>
				<view class="orderTime" v-if="orderInfo.orderState == 3">
					<text class="iconfont icon-a-shijian1"></text>
					<text>已完成</text>
				</view>
				<view class="canceled" v-if="orderInfo.orderState == 9">订单异常</view>
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
			<view class="order_li_centent_li">
				<view class="left_text">
					<text class="iconfont icon-shijian"></text>
					<text>开始时间：</text>
				</view>
				<view class="right_text">{{ orderInfo.startTime | moreData }}</view>
			</view>
			<view class="order_li_centent_li" v-if="orderInfo.orderState >= 2">
				<view class="left_text">
					<text class="iconfont icon-jine"></text>
					<text>订单金额：</text>
				</view>
				<view class="right_text">{{ orderInfo.orderMoney | moneyTwoNum }}元</view>
			</view>
			<view class="order_li_centent_li" v-if="orderInfo.orderState >= 2">
				<view class="left_text">
					<text class="iconfont icon-shijian"></text>
					<text>订单时长：</text>
				</view>
				<view class="right_text">{{ setDurationFun(orderInfo.duration) | moreData }}</view>
			</view>
		</view>
		<view class="order_li_bottom">
			<view class="text" v-if="orderInfo.orderState == 1">订单正在进行中</view>
			<view class="text" v-if="orderInfo.orderState == 2">订单已结束，请尽快进行支付</view>
			<view class="button payButton" v-if="orderInfo.orderState == 2" @click="clickPayBut">立即支付</view>
			<view class="text" v-if="orderInfo.orderState == 3">结算完成</view>
			<view class="text" v-if="orderInfo.orderState == 9">订单异常</view>
		</view>

		<sm-call-pay ref="callPayRef" :payParamsData="payParamsData" @payResultFun="payResultFun"></sm-call-pay>
	</view>
</template>

<script>
	import {
		orderHolderPay
	} from "@/secondPackage/api/index.js";

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
				authCode: '', // 支付宝 授权code
				payType: 1, //支付类型 1-微信支付 2-支付宝支付
				payParamsData: {} // 支付参数
			}
		},
		methods: {
			clickOrderCard() {
				// 已完成订单才可进入订单详情
				if (this.orderInfo.orderState == 3) {
					uni.navigateTo({
						url: `/secondPackage/orderManagement/occupyDetails?id=${ this.orderInfo.id }`
					});
				}
			},
			setDurationFun(duration) {
				let durationStr = '';
				if (duration) {
					let durationArr = duration.split(":");
					durationStr = `${ durationArr[0]*1 }小时${ durationArr[1]*1 }分${ durationArr[2]*1 }秒`
				}
				return durationStr
			},
			clickCopyBut() {
				uni.setClipboardData({
					data: this.orderInfo.orderNum,
					success: function() {
						console.log('success');
					}
				});
			},
			// 点击进行订单支付
			clickPayBut() {
				// #ifdef MP-ALIPAY 
				my.getAuthCode({
					scopes: 'auth_user',
					success: ({
						authCode
					}) => {
						this.payType = 2;
						this.authCode = authCode;
						this.startPayFunction();
					},
					fail: () => {
						uni.showToast({
							icon: 'none',
							title: '权限获取失败！'
						});
					}
				});
				// #endif

				// #ifdef MP-WEIXIN
				this.payType = 1;
				this.startPayFunction();
				// #endif
			},
			// 开始进行支付
			startPayFunction() {
				orderHolderPay({
					authCode: this.authCode,
					payType: this.payType,
					orderHolderId: this.orderInfo.id
				}).then((res) => {
					this.payParamsData = res.data;
					this.$nextTick(() => {
						this.$refs.callPayRef.setPayParamsFun();
					});
				})
			},
			// 支付成功进行回调
			payResultFun() {
				this.$emit("changeEvent", {
					type: 'paysuccess'
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
						font-size: 34rpx;
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
						margin-left: 12rpx;
						padding: 4rpx 12rpx;
						border-radius: 6rpx;
						border: 2rpx solid #476AE2;
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

			.payButton {
				border: 2rpx solid #ec8d4a;
				color: #ec8d4a;
			}
		}
	}
</style>