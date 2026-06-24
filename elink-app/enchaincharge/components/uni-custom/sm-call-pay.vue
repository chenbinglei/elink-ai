<template>
	<view class="callPay">
		<uni-popup ref="callPayPopup" type="bottom">
			<view class="pay_content">
				<view class="title">
					<view class="title_left" @click="clickClose"><uni-icons type="closeempty" size="24"></uni-icons></view>
					<view class="title_center">请选择</view>
					<view class="title_right"><uni-icons type="contact" size="30"></uni-icons></view>
				</view>

				<view class="list_content">
					<view class="li_content" v-for="(item, index) in list" :key="index" @click="clickSelectPay(item)">
						<view class="li_left">
							<image class="image" :src="getStaticFilePath(item.image)"></image>
							<text class="text">{{ item.name }}</text>
						</view>
						<view class="li_right u-arrow u-arrow-right"></view>
					</view>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
export default {
	name: 'CallPay',
	props: {
		// 支付签名参数
		payParamsData:{
			type: Object,
			default:()=>{ 
				return { }
			}
		},
		// 是否拉起底部选择框
		isPullUpPopus: {
			type: Boolean,
			default: false
		},
	},
	data() {
		return {
			payType: 1, //1-微信支付 2-支付宝支付
			activePay: 'wxpay', // 当前选择的支付环境
			list: [
				{ name: '微信支付', provider: 'wxpay', payType: 1, image: 'wxpay.png' },
				{ name: '支付宝支付', provider: 'alipay', payType: 2, image: 'applypay.png' },
			],
		};
	},
	methods: {
		setPayParamsFun() {
			// #ifdef MP-WEIXIN
			this.payType = 1;
			this.activePay = 'wxpay';
			// #endif
			// #ifdef MP-ALIPAY
			this.payType = 2;
			this.activePay = 'alipay';
			// #endif
		
			if (this.isPullUpPopus) {
				// 拉起底部弹框
				this.$refs.callPayPopup.open('bottom');
			} else {
				this.startlaunchPay();
			}
		},
		// 点击选择支付环境
		clickSelectPay(item) {
			this.payType = item.payType;
			this.activePay = item.provider;
			this.startlaunchPay();
		},
		//开始发起支付
		startlaunchPay(){
			// #ifdef MP-WEIXIN
			uni.requestPayment({
				provider: this.activePay,
				paySign: this.payParamsData.paySign,
				signType: this.payParamsData.signType,
				nonceStr: this.payParamsData.nonceStr,
				package: this.payParamsData.packageVal,
				timeStamp: this.payParamsData.timeStamp,
				success: result => {
					// console.log(result);
					this.clickClose(); // 关闭选择支付弹框
					this.$emit('payResultFun', { code: 20000, ...this.payParamsData });
				},
				fail(err) {
					console.log(err);
					uni.showToast({ icon:'none',title: '取消支付！' });
				}
			});
			// #endif
			
			// #ifdef MP-ALIPAY
			uni.requestPayment({
				provider: this.$platform,
				orderInfo: this.payParamsData,
				success: (res) => {
					// console.log(res);
					if (res && res.resultCode == 9000) {
						// 支付成功 执行想要的操作
						this.clickClose(); // 关闭选择支付弹框
						this.$emit('payResultFun', { code: 20000, ...this.payParamsData });
					} else {
						uni.showToast({ icon:'none',title: '支付失败！' });
					}
				},
				fail: () => {
					uni.showToast({ icon:'none',title: '取消支付！' });
				},
			});
			// #endif
		},
		clickClose() {
			this.$refs.callPayPopup.close();
		}
	}
};
</script>

<style scoped lang="scss">
.pay_content {
	z-index: 1000;
	padding: 48rpx 40rpx;
	box-sizing: border-box;
	background-color: #f6f7f8;
	border-radius: 48rpx 48rpx 0 0;

	.title {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-bottom: 38rpx;

		.title_center {
			color: #030303;
			font-size: 28rpx;
		}

		.title_right {
			visibility: hidden;
		}
	}

	.list_content {
		.li_content {
			display: flex;
			align-items: center;
			justify-content: space-between;
			background-color: #ffffff;
			margin-bottom: 20rpx;
			padding: 30rpx 28rpx 30rpx 42rpx;
			border-radius: 20rpx;
			box-sizing: border-box;

			.li_left {
				display: flex;
				align-items: center;

				.image {
					width: 62rpx;
					height: 56rpx;
					margin-right: 48rpx;
				}

				.text {
					color: #030303;
					font-size: 32rpx;
					font-weight: bold;
				}
			}

			.li_right {
				border-color: #b3b4b5;
			}
		}
		.li_content:last-child {
			margin-bottom: 0;
		}
	}
}
</style>
