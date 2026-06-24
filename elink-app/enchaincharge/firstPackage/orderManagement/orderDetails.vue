<template>
	<view class="container">
		<view class="container_top">
			<view class="site_info" @click="goToPage">
				<view class="site_left">
					<view>{{ resData.siteName | moreData }}</view>
					<view class="address" v-if="resData.address">{{ resData.address | moreData }}</view>
				</view>
				<view class="u-arrow u-arrow-right"></view>
			</view>
		</view>
		<view class="container_center">
			<view class="card_li">
				<view class="card_left">订单编号：</view>
				<view class="card_right">
					<text class="twoShowText">{{ resData.orderNum | moreData }}</text>
					<view class="copy_but" @click.stop="clickCopyBut">复制</view>
				</view>
			</view>
			<view class="card_li">
				<view class="card_left">{{ "开始充电：" | textValue(resData.chargeMode)}}</view>
				<view class="card_right">{{ resData.startTime | moreData }}</view>
			</view>
			<view class="card_li">
				<view class="card_left">{{ "结束充电：" | textValue(resData.chargeMode)}}</view>
				<view class="card_right">{{ resData.endTime | moreData }}</view>
			</view>
			<view class="card_li">
				<view class="card_left">{{ "充电电量：" | textValue(resData.chargeMode)}}</view>
				<view class="card_right">{{ resData.totalQt | moreData }}度</view>
			</view>
			<view class="card_li">
				<view class="card_left">{{ "充电时长：" | textValue(resData.chargeMode)}}</view>
				<view class="card_right">{{ setDurationFun(resData.chargeDuration) | moreData }}</view>
			</view>
			<view class="card_li">
				<view class="card_left">启动策略：</view>
				<view class="card_right">{{ resData.strategyType | strategyType(resData.chargeMode) }}</view>
			</view>
			
			<template v-if="resData.prepayMoney > 0 && resData.chargeMode == 1">
				<view class="card_li">
					<view class="card_left">预付金额：</view>
					<view class="card_right">{{ resData.prepayMoney | moneyTwoNum }}元</view>
				</view>
			</template>
			
			<view class="card_li">
				<view class="card_left">订单金额：</view>
				<view class="card_right">{{ resData.totalCost | moneyTwoNum }}元</view>
			</view>
			
			<view class="card_li" v-if="resData.chargeMode == 1">
				<view class="card_left">折扣金额：</view>
				<view class="card_right">{{ resData.saleMoney | moneyTwoNum }}元</view>
			</view>
			
			<view class="card_li">
				<view class="card_left">结束原因：</view>
				<view class="card_right">{{ resData.stopDetailReason | moreData }}</view>
			</view>
			
			<!-- 充电订单 （只有交易订单才会展示实际支付金额） -->
			<view class="real_li" v-if="resData.chargeMode == 1">
				<text>实际支付金额：</text>
				<text class="real_money" v-if="resData.prepayMoney > 0">{{ resData.settlementRecord.actualTotalCost | moneyTwoNum }}</text>
				<text class="real_money" v-else>0.00</text>
				<text>元</text>
			</view>
			<!-- 放电订单	 -->
			<view class="real_li" v-if="resData.chargeMode == 2">
				<text>实际收益金额：</text>
				<text class="real_money">{{ resData.totalCost | moneyTwoNum }}</text>
				<!-- <text class="real_money">{{ resData.settlementRecord.actualTotalCost | moneyTwoNum }}</text> -->
				<text>元</text>
			</view>
		</view>
		<view class="container_bottom">
			<sm-tabs :tabsArray="tabsArray" :tabsIndex="tabsIndex" @changEvent="changEvent"></sm-tabs>
			<view class="line_chart">
				<LineChartComponents ref="lineChartRef" :chartData="chartData"></LineChartComponents>
			</view>
		</view>
	</view>
</template>

<script>
	import { queryMyOrderDetailsByOrderNum } from '@/secondPackage/api/index.js';
	import LineChartComponents from '@/firstPackage/components/LineChartComponents.vue';
	
	export default {
		name: "orderDetails",
		components: { LineChartComponents },
		data() {
			return {
				orderNum: "",
				chartData: {},
				
				resData: {},
				powerData: {},
				currentData: {},
				voltageData: {},
				
				tabsIndex: "powerData",
				tabsArray: [{ id: "powerData", name: '功率' },{ id: "currentData", name: '电流' },{ id: "voltageData", name: '电压' }],
			}
		},
		onLoad: function (options) {
			this.orderNum = options.orderNum;
			this.getMyOrderDetails();
		},
		methods: {
			// 根据订单编号获取订单详情
			getMyOrderDetails(){
				queryMyOrderDetailsByOrderNum({ orderNum: this.orderNum }).then(res => {
					let resData = res.data ? res.data : {};
					let totalElectReduction = resData.settlementRecord.totalElectReduction ?? 0;
					let totalFeeReduction = resData.settlementRecord.totalFeeReduction ?? 0;
					resData.saleMoney = totalElectReduction + totalFeeReduction;
					resData.chargeMode = resData.runMode + 1; // 运行模式 1-充电订单 2-放电订单
					if(resData.siteLocation)resData.address = JSON.parse(resData.siteLocation).address;
					this.resData = JSON.parse(JSON.stringify(resData));
					
					this.powerData = { 
						yAxisName: "kW",
						seriesName: '功率', 
						chartColor: "#00E6FE", 
						dataList: this.resData.powerList,
						xaxisList: this.resData.xaxisList
					};
					
					this.currentData = { 
						yAxisName: "A",
						seriesName: '电流',
						chartColor: "#06DE6F",
						dataList: this.resData.currentList,
						xaxisList: this.resData.xaxisList
					};
					
					this.voltageData = {
						yAxisName: "V",
						seriesName: '电压',
						chartColor: "#FFF886", 
						dataList: this.resData.voltageList,
						xaxisList: this.resData.xaxisList
					};
					
					this.changEvent(this.tabsIndex);
				})
			},
			goToPage() {
				uni.navigateTo({ 
					url: `/firstPackage/pages/stationDetails?siteRecordId=${this.resData.siteId}` 
				});
			},
			clickCopyBut() {
				uni.setClipboardData({
					data: this.resData.orderNum,
					success: function() {
						console.log('success');
					}
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
			changEvent(tabsIndex) {
				this.tabsIndex = tabsIndex;
				this.chartData = this[tabsIndex];
			}
		}
	}
</script>

<style scoped lang="scss">
.container {
	padding: 32rpx 32rpx 0;
	box-sizing: border-box;
	
	.container_top {
		
		.site_info {
			display: flex;
			justify-content: space-between;
			align-items: center;
			border-radius: 12rpx;
			padding: 28rpx 24rpx;
			box-sizing: border-box;
			background-color: #ffffff;
			margin-bottom: 20rpx;
			
			.site_left {
				.address {
					color: #929395;
				}
			}
			
			.u-arrow {
				border-color: #107BE9;
			}
		}
	}
		
	.container_center{
		border-radius: 12rpx;
		padding: 28rpx 24rpx;
		box-sizing: border-box;
		background-color: #ffffff;
		margin-bottom: 20rpx;
		
		.card_li{
			display: flex;
			align-items: center;
			margin-bottom: 24rpx;
			
			.card_left{
				font-size: 24rpx;
				color: rgba(0,0,0,0.4);
				margin-right: 16rpx;
				white-space: nowrap;
			}
			
			.card_right{
				flex: 1;
				font-size: 24rpx;
				color: rgba(0,0,0,0.9);
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
		
		.real_li {
			display: flex;
			justify-content: flex-end;
			align-items: center;
			margin-top: 16rpx;
			
			.real_money{
				color: #FD3836;
				font-size: 36rpx;
				font-weight: bold;
				margin: 0 16rpx;
			}
		}
	}
	
	.container_bottom {
		border-radius: 12rpx;
		margin-bottom: 20rpx;
		
		.line_chart {
			height: 460rpx;
			margin-top: 32rpx;
			padding: 28rpx 24rpx;
			box-sizing: border-box;
			background-color: #ffffff;
		}
	}
}
</style>