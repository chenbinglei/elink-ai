<template>
	<view class="order_card">
		<view class="order_card_top">
			<view class="order_card_top_left" @click="clickSiteName">
				<text class="siteName twoShowText">{{ orderInfo.siteName | moreData }}</text>
				<text class="u-arrow u-arrow-right"></text>
			</view>
			<view class="order_card_top_right" @click="displayVisible = !displayVisible">
				<text class="textClass">{{ displayVisible ? '收起' : '展开' }}</text>
				<text class="u-arrow" :class="[displayVisible ? 'u-arrow-up' : 'u-arrow-down']"></text>
			</view>
		</view>
		
		<ChargingAndDisAnimation :startSoc="orderInfo.startSoc" :batterySOC="orderInfo.batterySoc" :startMode="startMode"
		        :workState="orderInfo.workState"></ChargingAndDisAnimation>
				
		<view class="content_body" v-if="displayVisible">
			
			<!-- 充电或者放电中展示 -->
			<template v-if="orderInfo.workState != 8">
				<view class="content_body_list">
					<view class="content_body_li">
						<view class="content_body_li_top">
							<text class="number">{{ orderInfo.voltage | moreData }}</text>
							<text class="unit">V</text>
						</view>
						<view class="content_body_li_bottom">电压</view>
					</view>
					<view class="content_body_li">
						<view class="content_body_li_top">
							<text class="number">{{ orderInfo.current | moreData }}</text>
							<text class="unit">A</text>
						</view>
						<view class="content_body_li_bottom">电流</view>
					</view>
					<view class="content_body_li">
						<view class="content_body_li_top">
							<text class="number">{{ orderInfo.power | moreData }}</text>
							<text class="unit">kW</text>
						</view>
						<view class="content_body_li_bottom">功率</view>
					</view>
					<view class="content_body_li">
						<view class="content_body_li_top">
							<text class="number">{{ orderInfo.batterySoc | moreData }}</text>
							<text class="unit">%</text>
						</view>
						<view class="content_body_li_bottom">Soc</view>
					</view>
				</view>
				
				<view class="content_card_list">
					<view class="content_card_li">
						<view class="content_card_li_left">
							<text class="iconfont icon-dianliang"></text>
							<text class="textClass">{{ "已充电量" | textValue(startMode) }}</text>
						</view>
						<view class="content_card_li_right">
							<text class="number">{{ orderInfo.totalQt | moreData }}</text>
							<text class="unit">kW·h</text>
						</view>
					</view>
					<view class="content_card_li">
						<view class="content_card_li_left">
							<text class="iconfont icon-shijian"></text>
							<text class="textClass">{{ "已充时长" | textValue(startMode) }}</text>
						</view>
						<view class="content_card_li_right">
							<text class="number">{{ setDurationFun(orderInfo.chargeTime) | moreData }}</text>
							<text class="margin_class">剩余</text>
							<text class="number">{{ setDurationFun(orderInfo.remainTime) | moreData }}</text>
						</view>
					</view>
					
					<view class="content_card_li" v-if="orderInfo.prepayMoney > 0 && startMode == 1">
						<view class="content_card_li_left">
							<text class="iconfont icon-jine"></text>
							<text class="textClass">已充金额</text>
						</view>
						<view class="content_card_li_right">
							<text class="number">{{ orderInfo.totalCost | moneyTwoNum }}</text>
							<text class="unit">元</text>
						</view>
					</view>
					
					<view class="content_card_li" v-if="startMode == 2">
						<view class="content_card_li_left">
							<text class="iconfont icon-jine"></text>
							<text class="textClass">收益金额</text>
						</view>
						<view class="content_card_li_right">
							<text class="number">{{ orderInfo.totalCost | moneyTwoNum }}</text>
							<text class="unit">元</text>
						</view>
					</view>
				</view>
				
				<view class="stop_button">
					<progress-button ref="progressButtonRef" @changEvent="changEvent"></progress-button>
				</view>
			</template>

			<!-- 预约中展示 -->
			<template v-if="orderInfo.workState == 8">
				<view class="content_card_list">
					<view class="content_card_li" v-if="orderInfo.prepayMoney > 0 && startMode == 1">
						<view class="content_card_li_left">
							<text class="iconfont icon-jine"></text>
							<text class="textClass">预付金额</text>
						</view>
						<view class="content_card_li_right">
							<text class="number">{{ orderInfo.prepayMoney | moneyTwoNum }}</text>
							<text class="unit">元</text>
						</view>
					</view>
					<view class="content_card_li">
						<view class="content_card_li_left">
							<text class="iconfont icon-shijian"></text>
							<text class="textClass">预约时间</text>
						</view>
						<view class="content_card_li_right twoShowText">
							<text class="number">{{ orderInfo.clockingTime | moreData }}</text>
						</view>
					</view>
					<view class="content_card_li">
						<view class="content_card_li_left">
							<text class="iconfont icon-dingdanbianhao"></text>
							<text class="textClass">订单编号</text>
						</view>
						<view class="content_card_li_right twoShowText">
							<text class="number">{{ orderInfo.orderNum | moreData }}</text>
						</view>
					</view>
				</view>
				<view class="content_button">
					<view class="button cancelBut" @click="clickCancelBut(2)">取消预约</view>
					<view class="button startBut uni-flex-item" @click="clickStartBut">立即启动</view>
				</view>
			</template>
			
		</view>
	</view>
</template>

<script>
	import { mapState } from 'vuex';
	import { pileStart } from '@/api/home.js';
	import ProgressButton from "./ProgressButton.vue";
	import { gunStatusToastTitle } from "@/common/common.js";
	import ChargingAndDisAnimation from "./ChargingAndDisAnimation.vue"
	
	export default {
		name: "OngoingOrdersCard",
		components:{ ProgressButton,ChargingAndDisAnimation },
		props: {
			orderInfo: {
				type: Object,
				default: () => {
					return {}
				}
			},
			// 1: 充  2：放
			startMode: {
				type: [String, Number],
				default: 1
			},
		},
		computed: {
			...mapState(['userInfo', 'appConfig'])
		},
		data() {
			return {
				displayVisible: false,
			}
		},
		methods: {
			clickSiteName() {
				uni.navigateTo({
					url: `/firstPackage/pages/stationDetails?siteRecordId=${this.orderInfo.siteId}`,
				})
			},
			changEvent(data){
				if(data.type === "clickCloseBut"){
					
					if (this.orderInfo.workState > 2) {
						let alterTiltle = gunStatusToastTitle(this.orderInfo.workState);
						uni.showToast({ icon:"none", title: alterTiltle });
						return;
					}
					
					if (!this.orderInfo.orderNum || this.orderInfo.orderNum === 'undefined') {
						uni.showToast({ icon:"none", title:"系统错误，请稍后重试！" })
						return;
					}
					
					let data = {
						operationType: 1, // 1: 停止  2；取消预约
						gunCode: this.orderInfo.gunCode,
						orderNum: this.orderInfo.orderNum, // 订单编号
						pileCode: this.orderInfo.pileCode,
					};
					
					uni.navigateTo({
						url: `/secondPackage/orderManagement/orderSettlement?data=${encodeURIComponent(JSON.stringify(data))}`,
						success: () => {
							this.$refs.progressButtonRef.schedule = 0;
							this.$refs.progressButtonRef.setRenderRateFun();
						}
					});
				}
			},
			setDurationFun(duration) {
			    let durationStr = "";
			    if (duration) {
					let durationArr = duration.split(":");
					durationStr = `${durationArr[0] * 1}小时${durationArr[1] * 1}分`;
					if(durationArr[2]) durationStr = `${ durationStr }${durationArr[2] * 1}秒`;
			    }
			    return durationStr;
			},
			clickCancelBut(operationType){
				uni.showModal({
					title: '提示',
					content: `确定取消${ operationType == 2 ? '预约' : '充电' }吗？`,
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
			},
			clickStartBut(){
				uni.showModal({
					title: '提示',
					content: `确定启动充电桩吗？`,
					success: (res) => {
						if (res.confirm) {
							pileStart({
								type: 0, //充电方式 0-立即充电 1-定时充电 2-自动充电
								starter: 1, // 发起者 1-APP 2-电桩屏幕登录启动 3-电卡 4-VIN码
								accountType: 3, //账号类型 1-充/放电卡 2-VIN码 3-手机号
								siteId: this.orderInfo.siteId,
								gunCode: this.orderInfo.gunCode,
								pileCode: this.orderInfo.pileCode,
								runMode: this.startMode - 1,  // 运行模式 -1-未知 0-充电模式 1-放电模式
								serialNum: this.orderInfo.orderNum,
								accountData: this.userInfo.mobile, //账号数据  手机号
								strategy: this.orderInfo.strategy, //0:自动充满  1 ：soc电量 2：金额 3：电量
								prepayMoney: this.orderInfo.prepayMoney, //账户余额(冻结金额，本次充电金额)
								stopCode: String(this.userInfo.mobile).substr(7), //停止码
								strategyCfg: this.orderInfo.strategy != 1 ? this.orderInfo.strategyCfg * 1000 : this.orderInfo.strategyCfg, // 0:自动充满  1 ：soc电量 2：金额 3：电量
							}).then(res=>{
								uni.showToast({ icon:"success", title:"启动成功，请查看！" });
							})
						}
					}
				});
			}
		}
	}
</script>

<style scoped lang="scss">
	.order_card {
		border-radius: 12rpx;
		padding: 28rpx 0;
		box-sizing: border-box;
		background-color: #ffffff;
		margin-bottom: 20rpx;
		box-shadow: 0rpx 0rpx 16rpx rgba(244, 245, 249, 0.5);

		.order_card_top {
			display: flex;
			align-items: center;
			padding: 0 24rpx;
			box-sizing: border-box;

			.order_card_top_left {
				flex: 1;
				display: flex;
				align-items: center;

				.siteName {
					color: #030303;
					font-size: 28rpx;
					font-weight: 600;
					-webkit-line-clamp: 1;
				}

				.u-arrow {
					margin-left: 12rpx;
					border-color: #A1A1A1;
				}
			}

			.order_card_top_right {
				color: #979797;
				font-size: 24rpx;
				margin-left: 12rpx;
				display: flex;
				align-items: center;

				.u-arrow {
					margin-left: 12rpx;
					transition: all 0.28s;
					border-color: #A1A1A1;
				}
			}
		}

		.content_body {
			// padding-top: 12rpx;
			transition: all 0.28s;
			
			.content_button{
				display: flex;
				align-items: center;
				padding: 0 24rpx;
				box-sizing: border-box;
				
				.button{
					height: 68rpx;
					background: #476AE2;
					border-radius: 10rpx;
					margin-right: 24rpx;
					box-sizing: border-box;
					display: flex;
					align-items: center;
					justify-content: center;
					color: #FFFFFF;
					font-size: 28rpx;
					
					&:last-child{
						margin-right: 0;
					}
				}
				
				.cancelBut{
					width: 200rpx;
					color: #242424;
					background: none;
					border: 2rpx solid #B0B0B0;
				}
			}

			.content_body_list {
				display: flex;
				padding: 14rpx 0;
				border-top: 2rpx solid #F4F5F9;

				.content_body_li {
					flex: 1;
					display: flex;
					flex-direction: column;
					align-items: center;
					border-right: 2rpx solid #F4F5F9;

					.content_body_li_top {
						color: #476AE2;
						font-size: 32rpx;
						font-weight: 500;
					}

					.content_body_li_bottom {
						margin-top: 10rpx;
						font-size: 24rpx;
						color: #7E89B2;
					}
				}
			}

			.content_card_list {
				padding: 12rpx 0 18rpx 0;
				border-top: 2rpx dashed #F4F5F9;

				.content_card_li {
					display: flex;
					align-items: center;
					padding: 18rpx 32rpx;
					box-sizing: border-box;

					.content_card_li_left {
						font-size: 24rpx;
						color: #7E89B2;

						.iconfont {
							font-size: 32rpx;
							margin-right: 12rpx;
						}

						.icon-dianliang {
							font-size: 37rpx;
						}
					}

					.content_card_li_right {
						flex: 1;
						color: #476AE2;
						font-size: 28rpx;
						font-weight: 500;
						margin-left: 12rpx;
						text-align: right;
						-webkit-line-clamp: 1;
						
						.margin_class{
							margin: 0 12rpx;
						}
					}
				}
			}

			.stop_button {
				display: flex;
				flex-direction: column;
				align-items: center;
				padding-top: 28rpx;
				border-top: 2rpx dashed #F4F5F9;
			}
		}
	}
</style>