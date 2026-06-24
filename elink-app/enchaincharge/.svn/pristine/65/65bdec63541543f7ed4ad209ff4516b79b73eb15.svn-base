<template>
	<view class="container">
		<view class="container_content">
			<view class="container_flex">
				<view class="container_top">
					<view class="swiper_top" v-if="siteImage && siteImage.length">
						<swiper circular class="swiper" :autoplay="autoplay" :interval="interval" :indicator-dots="indicatorDots"
							:indicator-color="indicatorColor" :indicator-active-color="indicatorActiveColor" >
							<swiper-item v-for="(item, index) in siteImage" :key="index">
								<image class="swiper-item" :src="item"></image>
							</swiper-item>
						</swiper>
					</view>
					<view class="site_card">
						<sm-station-card :siteInfo="siteInfo" :isClick="false" isShowRight></sm-station-card>
					</view>
					<view class="out_card">
						<SwitchTableCard :tabsArray="tabsArray" :tabsIndex="tabsIndex" @changEvent="changeEvent">
							<template v-slot:slotContent>
								<view class="content_body">
									<!-- 价格信息 -->
									<PriceInformation v-if="tabsIndex === 1" :deviceId="siteRecordId" :parkCostType="parkCostType"></PriceInformation>
									<FastAndSlowCharging :tabsIndex="tabsIndex" v-if="tabsIndex === 2 || tabsIndex === 3" :pileInfo="pileInfo"></FastAndSlowCharging>
								</view>
							</template>
						</SwitchTableCard>
					</view>
					<!-- 电站信息 -->
					<view class="out_card"><StationInfo :siteInfo="siteInfo" :operator="operator"></StationInfo></view>
				</view>
				
				<view class="container_bottom">
					<view class="bottom_left">
						<view class="number">{{ siteInfo.chargePrice | moneyTwoNum }}</view>
						<view class="unit">元/度</view>
					</view>
					<view class="saoma_black" @click="$noMultipleClicks(goToPage,'/fourthPackage/pages/scanCode')">
						<text class="iconfont icon-saoma"></text>
						<text class="text">立即充电</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import SwitchTableCard from '@/firstPackage/components/SwitchTableCard.vue';
	import StationInfo from '@/firstPackage/components/stationDetails/StationInfo.vue';
	import FastAndSlowCharging from '@/firstPackage/components/stationDetails/FastAndSlowCharging.vue';
	import PriceInformation from '@/firstPackage/components/stationDetails/PriceInformation.vue';
	import { querySiteDetailsDataById, querySitePileDetailsDataById } from '@/firstPackage/api/index.js';
	
	export default {
		name: "stationDetails",
		options: { styleIsolation: 'shared' }, //解决/deep/不生效**
		components: { SwitchTableCard, PriceInformation, FastAndSlowCharging, StationInfo },
		data() {
			return {
				siteInfo: {},
				priceInfo: {},
				chargerPriceList: [], // 价格详情数据
				
				siteRecordId: "",
				distance: "", // 距离
				operator: "", // 运营商
				parkCostType: "", // 停车费用类型
				occupyingPileCost: null, // 占桩费用信息
				siteImage: [], // 站点图片，
				pileInfo: {}, // 电桩信息
				
				noClick: true,
				interval: 3000, //自动切换时间间隔
				autoplay: true, //是否自动切换
				indicatorDots: true, //是否显示面板指示点
				indicatorActiveColor: '#FFFFFF', //当前选中的指示点颜色
				indicatorColor: 'rgba(255, 255, 255, .5)', //	指示点颜色
				
				tabsIndex: 1,
				tabsArray: [
					{ id: 1, name: '价格信息' },
					{ id: 2, name: '快充' },
					{ id: 3, name: '慢充' }
				],
			}
		},
		onLoad: function (options) {
			this.siteRecordId = options.siteRecordId;
			this.distance = options.distance;
		},
		mounted() {
			this.getStationInfo();
			this.getSitePileDetails();
		},
		methods: {
			// 获取站点详情
			getStationInfo() {
				querySiteDetailsDataById({ siteId: this.siteRecordId }).then(res => {
					let siteImage = [];  // 站点图片
					let dataInfo = res.data ? res.data : {};
					let siteInfo = res.data ? res.data : {};
					this.parkCostType = dataInfo.parkCostType;
					
					// // 当前价格信息
					// if(dataInfo.chargerPrice){
					// 	let electMoney = dataInfo.chargerPrice.electMoney ?? 0;
					// 	let serviceMoney = dataInfo.chargerPrice.serviceMoney ?? 0;
					// 	dataInfo.chargerPrice.totalCost = electMoney + serviceMoney;
					// 	this.priceInfo = dataInfo.chargerPrice; 
					// }
					
					if(dataInfo.location) siteInfo.stationAddress = JSON.parse(dataInfo.location).address;
					
					siteInfo.distance = this.distance;
					if (siteInfo.image) siteImage = siteInfo.image.split(',');
					if(siteInfo.chargerPrice) siteInfo.chargePrice = dataInfo.chargerPrice.electMoney;
					
					this.siteImage = siteImage;
					// this.chargerPriceList = dataInfo.chargerPriceList;
					this.siteInfo = JSON.parse(JSON.stringify(siteInfo));
				})
			},
			// 查询站点下电桩详情信息
			getSitePileDetails() {
				querySitePileDetailsDataById({ siteId: this.siteRecordId }).then(res => {
					let reaData = res.data ? res.data : {};
					this.pileInfo = res.data ? res.data : {};
					this.tabsArray = [
						{ 
							id: 1, 
							name: `<div class="uni-flex uni-column align-center">价格信息</div>`
						},
						{ 
							id: 2, 
							name: `<div class="uni-flex uni-column align-center">
							            <span>快充</span>
									    <div class="uni-flex" style="color: #989898;">
										    <span style="padding-right: 12rpx;box-sizing: border-box;">空闲</span>
											<span>${ reaData.fastIdleNum }/${ reaData.fastTotalNum }</span>
										 </div>
								    </div>` 
						},
						{ 
							id: 3, 
							name: `<div class="uni-flex uni-column align-center">
							            <span>慢充</span>
									    <div class="uni-flex" style="color: #989898;">
										    <span style="padding-right: 12rpx;box-sizing: border-box;">空闲</span>
											<span>${ reaData.slowIdleNum }/${ reaData.slowTotalNum }</span>
										 </div>
								    </div>` 
						}
					]
				})
			},
			changeEvent(data) {
				if(data.type === "SwitchTableCard"){
					uni.showLoading({ mask: true, title: "加载中.." });
					this.tabsIndex = data.tabsIndex;
					this.$nextTick(()=>{
						setTimeout(()=>{
							uni.hideLoading();
						},200)
					})
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
.container_flex {
	.container_top {
		flex: 1;
		height: 2rpx;
		box-sizing: border-box;
		overflow: auto;
		
		
		.swiper_top {
			height: 440rpx;
		
			.swiper {
				width: 100%;
				height: 100%;
		
				.swiper-item {
					width: 100%;
					height: 100%;
				}
			}
		
			/deep/ .nullData {
				height: 100%;
				display: flex;
				align-items: center;
				justify-content: center;
				padding: 32rpx 0;
				box-sizing: border-box;
		
				.nullDataImage {
					width: 280rpx;
					height: 200rpx;
				}
			}
		}
		
		.site_card {
			padding: 32rpx 32rpx 0;
			box-sizing: border-box;
			transition: all 0.28s;
		}
		
		.out_card{
			background-color: #fff;
			margin: 0 32rpx 32rpx;
			border-radius: 36rpx;
			box-sizing: border-box;
		}
	}
	
	.container_bottom{
		width: 100%;
		display: flex;
		justify-content: space-between;
		padding: 12rpx 32rpx 50rpx;
		box-sizing: border-box;
		background-color: #FFFFFF;
		box-shadow: 0px 0px 16rpx 0px rgba(0, 0, 0, 0.1);
		
		.bottom_left {
			color: #7b7d7f;
			font-size: 24rpx;
			display: flex;
			align-items: flex-end;
			margin-right: 32rpx;
		
			.number {
				color: #f3615b;
				font-size: 48rpx;
				font-weight: 500;
				margin-left: 4rpx;
			}
		}
		
		.saoma_black{
			flex: 1;
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
}
</style>