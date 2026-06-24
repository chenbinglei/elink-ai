<template>
	<view class="priceInfo">
		<view class="card_list">
			<view class="card_li">
				<view class="card_left">{{ "充电价格" | textValue(startMode) }}</view>
				<view class="card_right text">
					<view class="li_top">
						<text>{{ priceInfo.startTime | moreData }}~{{ priceInfo.endTime | moreData }} (当前时段)</text>
						<text class="details" @click="goPriceFun">价格详情 ></text>
					</view>
					<view class="li_center">
						<text class="money">
							<text>¥</text>
							<text class="totalCost">{{ priceInfo.totalCost | moneyTwoNum }}</text>
						</text>
						<text>元/度</text>
					</view>
					<view class="li_btm" v-if="startMode == 1">
						<text>电价 </text>
						<text class="font-weight">{{ priceInfo.electMoney | moneyTwoNum }}</text>
						<text>元/度</text>
						<text class="margin_class">|</text>
						<text>服务费 </text>
						<text class="font-weight">{{ priceInfo.serviceMoney | moneyTwoNum }} </text>
						<text>元/度</text>
					</view>
					<view class="overLine" v-if="isParkingFees || deviceType === 2"></view>
				</view>
			</view>
			<!-- 设备详情展示站桩费用 -->
			<view class="card_li" v-if="deviceType === 2">
				<view class="card_left">占桩费用</view>
				<view class="card_right text">
					<view class="li_btm">
						<text class="font-weight">{{ occupyingPileCost.avoidDuration | moreData }}</text>
						<text>分钟免费，</text>
						<template v-if="occupyingPileCost.configType === 1">
						  <text>固定价格：</text>
						  <text class="font-weight">{{ occupyingPileCost.configPriceInfoList[0].chargePrice | moreData }}</text>
						  <text>元</text>
						</template>
						<template v-if="occupyingPileCost.configType === 2">
							<template v-for="(item,index) in occupyingPileCost.configPriceInfoList">
								<text>超时大于等于</text>
								<text class="font-weight">{{ item.timeoutDuration }}</text>
								<text>分钟，</text>
								<text v-if="item.chargeType === 1">
									<text class="font-weight">{{ item.chargePrice | moreData }}</text>
									<text>元/分钟；</text>
								</text>
								<text v-else>
									<text>固定金额</text>
									<text class="font-weight">{{ item.chargePrice | moreData }}</text>
									<text>元</text>
								</text>
							</template>
						</template>
					</view>
					<view class="overLine" v-if="isParkingFees"></view>
				</view>
			</view>
			<view class="card_li" v-if="isParkingFees">
				<view class="card_left">停车费用</view>
				<view class="card_right text">
					<view class="li_top"><text>{{ parkCostType | parkCostType }}</text></view>
				</view>
			</view>
		</view>

		<!--  价格详情-->
		<sm-popup ref="smPopupRef" title="价格详情" :footerVisible="false" footerSlot popupType="center" @popupEvent="popupShow = false">
			<template v-slot:content>
				<view class="price_content">
					<view class="price_title">不同功率电桩价格或有不同，实际价格以{{ startMode === 1 ? '充电' : '放电' }}扫码为准</view>
					<view class="price_view" v-if="popupShow">
						<!-- 价格曲线 -->
						<PriceChart :chartColor="chartColor" :seriesName="seriesName" :seriesData="seriesData" :xAxisData="xAxisData" 
						            :chartNum="chartNum" v-if="isChart"></PriceChart>
						<!-- 价格表格 -->
						<PriceTable v-else :tableData="tableData" :strategyType="strategyType"></PriceTable>
					</view>
				</view>
			</template>
			<template v-slot:footer>
				<view class="buttons" @click="switchFun">{{ butText }}</view>
			</template>
		</sm-popup>

	</view>
</template>

<script>
	import { getNowDate } from '@/common/dateTime.js';
	import { calcNumberFun } from '@/common/common.js';
	import PriceChart from './PriceInformation/PriceChart.vue';
	import PriceTable from './PriceInformation/PriceTable.vue';
	import {findBillStrategyById,findDevicePriceById} from "@/firstPackage/api/index.js";
	
	export default {
		name: "PriceInformation",
		components: { PriceChart, PriceTable },
		props: {
			// 场站id  或者 设备id
			deviceId: {
				type: [Number, String],
				default: 1
			},
			isParkingFees: {
				type: Boolean,
				default: true
			},
			// 停车费用类型
			parkCostType: {
				type: [Number, String],
				default: ""
			},
			// 1 场站   2  设备
			deviceType: {
				type: [Number, String],
				default: 1
			},
			// 1 充   2 放
			startMode: {
				type: [Number, String],
				default: 1
			},
		},
		data() {
			return {
				priceInfo: {}, //  当前价格信息
				occupyingPileCost: {}, //  占桩费用信息
				returnDataInfo: null, // 当前接口返回的计费信息
				
				isChart: true,
				chartNum: '',
				tableData: {},
				xAxisData: [],
				seriesData: [],
				strategyType: 1,
				popupShow: false,
				butText: "查看表格",
				chartColor: '#079CEB',
				seriesName: '充电价格',
			}
		},
		mounted() {
			this.deviceType === 1 ? this.queryBillStrategyById() : this.queryDevicePriceById();
		},
		methods: {
			// 根据站点id查询站点充放电计费策略数据
			queryBillStrategyById(){
				findBillStrategyById({ siteId: this.deviceId,priceType: this.startMode }).then(res=>{
					this.returnDataInfo = res.data ? res.data : [];
					this.setBillStrategyFun();
				})
			},
			// 根据设备id查询充放电和占桩价格信息
			queryDevicePriceById(){
				findDevicePriceById({ deviceId: this.deviceId,priceType: this.startMode }).then(res=>{
					let returnDataInfo = res.data ? res.data : {};
					this.returnDataInfo = JSON.parse(JSON.stringify(returnDataInfo));
					this.occupyingPileCost = returnDataInfo.occupyPriceInfoData ?? {};
					// this.priceInfo = returnDataInfo.chargerPrice;
					this.setBillStrategyFun();
				})
			},
			// 设置页面表格 以及 曲线数据
			setBillStrategyFun(){
				// console.log(this.returnDataInfo);
				let activeTime = new Date().getTime(); // 获取当前时间戳
				let getYearMonthDay = getNowDate(null,0,'/'); // 获取当前年月日
				let xAxisData = [],seriesData = [],chargerPriceList = [];
				if(this.deviceType === 1) chargerPriceList = JSON.parse(JSON.stringify(this.returnDataInfo));
				if(this.deviceType === 2) chargerPriceList = JSON.parse(JSON.stringify(this.returnDataInfo.chargerPriceList));
				
				if (chargerPriceList && chargerPriceList.length) {
					for (let i = 0; i < chargerPriceList.length; i++) {
						xAxisData.push(chargerPriceList[i].startTime + ' ~ ' + chargerPriceList[i].endTime);
						
						let electMoney = chargerPriceList[i].electMoney ?? 0;
						let serviceMoney = chargerPriceList[i].serviceMoney ?? 0;
						chargerPriceList[i].totalCost = calcNumberFun(electMoney,serviceMoney,'+');
						seriesData.push(chargerPriceList[i].totalCost);
				
						let startTime = new Date(`${getYearMonthDay} ${chargerPriceList[i].startTime}:00`).getTime();
						let endTime = new Date(`${getYearMonthDay} ${chargerPriceList[i].endTime}:59`).getTime();
				
						if (activeTime >= startTime && activeTime < endTime) {
							this.priceInfo = chargerPriceList[i];  // 设置站点当前计费信息
							chargerPriceList[i].isCurrent = true;
							this.chartNum = i;
						}
					}
				}
				
				this.strategyType = this.startMode;
				this.xAxisData = JSON.parse(JSON.stringify(xAxisData));
				this.seriesData = JSON.parse(JSON.stringify(seriesData));
				this.seriesName = this.startMode == 1 ? "充电价格" : "放电价格";
				this.chartColor = this.startMode == 1 ? "#079CEB" : "#FFF78C";
				this.tableData = JSON.parse(JSON.stringify(chargerPriceList));
			},
			//  打开弹窗价格详情
			goPriceFun() {
				this.$refs.smPopupRef.openPopupFun();
				this.$nextTick(() => {
					this.popupShow = true;
				})
			},
			switchFun() {
				this.isChart = !this.isChart;
				this.butText = this.isChart ? "查看表格" : "查看曲线";
			}
		}
	}
</script>

<style scoped lang="scss">
	.card_list {
		.card_li {
			display: flex;
			align-items: flex-start;
			font-size: 12px;

			.card_left {
				display: flex;
				justify-content: center;
				background-color: rgba(31, 116, 226, 0.2);
				margin-right: 32rpx;
				border-radius: 12rpx;
				padding: 8rpx 12rpx;
				box-sizing: border-box;
				font-size: 28rpx;
			}

			.card_right {
				flex: 1;
				color: #989898;
				padding-bottom: 16rpx;

				.li_top {
					display: flex;
					justify-content: space-between;

					.details {
						color: #ec8d4a;
					}
				}

				.li_center {
					padding: 24rpx 0;

					.money {
						color: #476ae2;
						margin-right: 16rpx;
					}
					
					.totalCost{
						font-size: 36rpx;
						font-weight: bold;
					}
				}

				.overLine {
					width: 100%;
					height: 2rpx;
					margin-top: 32rpx;
					border-bottom: 2rpx solid #E1E1E1;
				}
				
				.font-weight{
					font-weight: bold;
				}
				
				.margin_class{
					margin: 0 10rpx;
				}
			}
		}
	}

	.price_content {
		width: 92vw;

		.price_title {
			font-size: 24rpx;
			color: #989898;
			padding: 24rpx;
			box-sizing: border-box;
		}

		.price_view {
			width: 100%;
		}
	}

	.buttons {
		flex: 1;
		height: 80rpx;
		font-size: 28rpx;
		border-radius: 12rpx;
		text-align: center;
		line-height: 80rpx;
		color: #ffffff;
		background: #476ae2;
		margin-bottom: 24rpx;
	}
</style>