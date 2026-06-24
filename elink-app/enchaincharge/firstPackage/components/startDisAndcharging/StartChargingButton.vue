<template>
	<view class="StartChargingButton">
		<view class="buttonClass" :class="'buttonClass' + startMode" @click="$noMultipleClicks(clickChargeNowBut)">
			<text class="iconfont icon-chongdianfangshi"></text>
			<text class="text">{{ "立即充电" | textValue(startMode)}}</text>
		</view>
		<sm-call-pay ref="callPayRef" :payParamsData="payParamsData" @payResultFun="goToPileRealTimePage"></sm-call-pay>
	</view>
</template>

<script>
	import { mapState } from 'vuex';
	import { memberChargeStart } from '@/firstPackage/api/index.js';

	export default {
		name: "StartChargingButton",
		props:{
			startMode:{
				type:[String,Number],
				default: 1
			},
		},
		computed: {
			...mapState(['userInfo', 'appConfig'])
		},
		data() {
			return {
				authCode: '', // 支付宝 授权code
				noClick: true, // 防止重复点击
				payParamsData: {}, // 支付参数
				startUpPileParame: null, // 启动桩的所有参数
			}
		},
		methods: {
			clickChargeNowBut() {
				// 发起客户端 订阅消息
				uni.requestSubscribeMessage({
					tmplIds: this.appConfig.chargeTmplIds,
					complete: () => {
						// #ifdef MP-ALIPAY
						my.getAuthCode({
							scopes: 'auth_user',
							success: ({ authCode }) => {
								this.authCode = authCode;
								this.startUpPileParame = null;
								//调用父组件方法，刷新数据，然后获取启动参数，进行预付款等
								this.$emit('startUpPileFun');
							},
							fail: () => {
								uni.showToast({ icon: 'none', title: '权限获取失败！' });
							}
						});
						// #endif
						// #ifdef MP-WEIXIN
						this.startUpPileParame = null;
						//调用父组件方法，刷新数据，然后获取启动参数，进行预付款等
						this.$emit('startUpPileFun');
						// #endif
					}
				});
			},
			// 二次确认启动弹框
			twoEnterAlterFun(data = { }) {
				this.startUpPileParame = data; // 父组件传递的参数 (包含，站点、桩、启动参数、预付参数)

				// if (!this.startUpPileParame) {
				// 	uni.showToast({ icon: 'none', title: '参数获取失败，请重试！' });
				// 	return;
				// }
				
				let strategyCfg = this.startUpPileParame.strategyCfg; // 默认值
				if(this.startUpPileParame.strategy <= 0){
					// 充电模式
					if(this.startUpPileParame.startMode == 1) strategyCfg = 100; // 自动充满默认值给100
					// 放电模式
					if(this.startUpPileParame.startMode == 2) strategyCfg = 0; // 自动放空默认值给0
				}
		
				let startConfigParame = {
					starter: 1, // 发起者 1-APP 2-电桩屏幕登录启动 3-电卡 4-VIN码
					userType: 1, //用户类型 1-个人会员 2-企业会员
					isOrderly: 2, //是否有序 1-是 2-否
					accountType: 3, //账号类型 1-充/放电卡 2-VIN码 3-手机号
					authCode: this.authCode, //支付宝授权code
					strategyCfg: strategyCfg, //启动策略值  soc    金额   电量
					accountData: this.userInfo.mobile, //账号数据  手机号
					gunCode: this.startUpPileParame.gunCode, // 枪编号
					pileCode: this.startUpPileParame.pileCode, //充电桩编号
					platformLogo: this.appConfig.platformLogo, // 平台标识
					strategyType: this.startUpPileParame.strategy, //0:自动充满  1 ：soc电量 2：金额 3：电量
					type: this.startUpPileParame.powerWay, //充电方式 0-立即充电 1-定时充电 2-自动充电
					runMode: this.startUpPileParame.startMode - 1, //订单类型 0-充电订单 1-放电订单
					balance: this.startUpPileParame.prepayMoney, //账户余额(冻结金额，本次充电金额)
					userIsPay: this.startUpPileParame.userIsPay, // 0： 不需要支付  1：需要支付
					siteId: this.startUpPileParame.siteRecordId, // 站点id
					deviceId: this.startUpPileParame.dataRecordId, //数据记录id
					stopCode: String(this.userInfo.mobile).substr(7), //停止码
					prepayMoney: this.startUpPileParame.prepayMoney, //预付金额 ---> 冻结金额
					clockingTime: this.startUpPileParame.appointmentTime ?? "",//只有为定时充电才有定时时间
					//支付方式 1-免支付 2-微信支付 3-支付宝支付 4-放电钱包余额
					payWay: this.startUpPileParame.startMode == 1 && this.startUpPileParame.userIsPay ? this.startUpPileParame.payWay : 1,
					dealType: this.startUpPileParame.startMode == 1 && this.startUpPileParame.userIsPay ? 1 : 2, //1-交易订单(付钱)  2-非交易订单 (包含放电订单)
				};

				// 使用新流程进行启动充电桩
				memberChargeStart(startConfigParame).then(res => {
					// 充电
					if(this.startUpPileParame.startMode == 1){
						// 需要进行支付 
						if(this.startUpPileParame.userIsPay && this.startUpPileParame.payWay > 1 && this.startUpPileParame.payWay < 4){
							this.payParamsData = res.data;
							this.$nextTick(() => {
								this.$refs.callPayRef.setPayParamsFun();
							});
						} else {
							this.goToPileRealTimePage();
						}
					}
					
					// 放电 不需要付钱
					if(this.startUpPileParame.startMode == 2) this.goToPileRealTimePage();
				}).catch((err) => {
					// 检测该用户存在未支付的占用订单
					if (err.code === 20001) {
						uni.showModal({
							title: '提示',
							content: err.message,
							confirmText: "去支付",
							confirmColor: '#476AE2',
							success: (result) => {
								if (result.confirm) {
									let data = {};
									uni.navigateTo({
										url: `/secondPackage/orderManagement/occupyOrderList?data=${encodeURIComponent(JSON.stringify(data))}`
									});
								}
							},
							complete: () => {
								uni.hideToast();
							}
						});
					}
				})
			},
			// 跳转 实时数据页面
			goToPileRealTimePage() {
				console.log("可以进行等待弹框操作了!");
				this.$emit("changEvent",{ type: "openWaitPopup" });
			}
		}
	}
</script>

<style scoped lang="scss">
	.StartChargingButton {
		padding: 32rpx 32rpx 48rpx 32rpx;
		background-color: #ffffff;

		.buttonClass {
			height: 88rpx;
			background: #476AE2;
			border-radius: 16rpx;
			display: flex;
			align-items: center;
			justify-content: center;

			.iconfont,
			.text {
				color: #FFFFFF;
				font-size: 36rpx;
				font-weight: 500;
			}
		}
		
		.buttonClass2{
			background: #FFA126;
		}
	}
</style>