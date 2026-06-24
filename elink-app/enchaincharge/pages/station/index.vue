<template>
	<view class="container">
		<!-- <sm-custom-head title="地图找桩" :arrowStatus="false"></sm-custom-head> -->
		<view class="container_content">
			<StationMap ref="stationMapRef" @changEvent="changEvent"></StationMap>
			<view class="content_bottom">
				<view class="mapBottomBut">
					<view class="but_class" @click="clickMapScreen">
						<text class="iconfont icon-shaixuan"></text>
					</view>
					<view class="but_class" @click="clickMapToLocation">
						<text class="iconfont icon-weizhigengxin"></text>
					</view>
				</view>
				<view class="site-card">
					<sm-station-card v-if="markerStatus" :siteInfo="markerInfo"></sm-station-card>
				</view>
			</view>
		</view>
		<view class="bottom_search">
			<view class="search_left" @click="goToPage('/fourthPackage/pages/stationSearch')">
				<uni-easyinput type="search" v-model="siteNmae" prefixIcon="search" placeholder="搜索充电站" :inputBorder="false" 
				            :styles="inputStyle" disabled></uni-easyinput>
			</view>
			<view class="search_right" @click="goToPage('/fourthPackage/pages/stationList')">
				<text class="iconfont icon-liebiao"></text>
			</view>
		</view>
	</view>
</template>

<script>
	import StationMap from "./components/StationMap.vue";
	export default {
		name: "station",
		components: { StationMap },
		data() {
			return {
				siteNmae: '',
				markerInfo: {},
				locationInfo: null, // 当前经纬度坐标
				markerStatus: false, // 现在当前点击的站点卡片
				
				inputStyle: {
					height: '68rpx',
					color: '#C9C9C9',
					disableColor: '#F7F7F7',
					borderColor: '#F7F7F7',
					borderRadius: '12rpx',
					fontSize: '28rpx'
				},
			}
		},
		onShow() {
			this.getLocation();
		},
		methods: {
			// 获取当前位置
			getLocation() {
				uni.getLocation({
					type: 'wgs84',
					success: (res) => {
						// console.log(res);
						this.locationInfo = {
							centerLon: res.longitude, //中心经度
							centerLat: res.latitude //中心纬度
						};
					},
					fail: eroor => {
						// console.log(eroor);
						this.locationInfo = {
							centerLon: 120.15507, //中心经度
							centerLat: 30.274084 //中心纬度
						};
					},
					complete: () => {
						// console.log(this.locationInfo);
						this.$store.dispatch('alterlatAndLon', this.locationInfo);
						this.$refs.stationMapRef.getMapSiteList(); // 获取地图站点列表
					}
				});
			},
			// 点击回到自身位置
			clickMapToLocation(){
				this.$refs.stationMapRef.moveTolocation(); 
			},
			// 打开当前站点卡片
			changEvent(data){
				this.markerInfo = data.markerInfo;
				this.markerStatus = data.markerStatus;
			},
			goToPage(pageName) {
				// 查看用户是否登录
				if (this.$lockUserIfLogin(2)) {
					uni.navigateTo({ url: pageName });
				}
			},
			clickMapScreen(){
				this.$refs.stationMapRef.openScreenPopup();
			}
		}
	}
</script>

<style scoped lang="scss">
	.container_content {
		display: initial;
		position: relative;

		.content_bottom {
			width: 100%;
			z-index: 10000;
			position: absolute;
			left: 0;
			bottom: 0;

			.mapBottomBut {
				display: flex;
				flex-direction: column;
				align-items: flex-end;
				box-sizing: border-box;
				position: absolute;
				right: 32rpx;
				top: -178rpx;

				.but_class {
					width: 72rpx;
					height: 72rpx;
					background: #FFFFFF;
					border-radius: 16rpx;
					text-align: center;
					line-height: 72rpx;
					opacity: 0.8;
					margin-bottom: 12rpx;
					
					.iconfont{
						color: #242424;
						font-size: 48rpx;
					}
				}
			}
			
			.site-card{
				padding: 0 32rpx;
				box-sizing: border-box;
			}
		}
	}
	.bottom_search{
		width: 100%;
		padding: 22rpx 32rpx;
		box-sizing: border-box;
		background-color: #FFFFFF;
		display: flex;
		align-items: center;
		justify-content: space-between;
		box-shadow: 0px 0px 16rpx 0px rgba(0, 0, 0, 0.1);
		
		.search_left{
			flex: 1;
		}
		
		.search_right{
			padding-left: 16rpx;
			
			.iconfont{
				color: #242424;
				font-size: 48rpx;
				font-weight: bold;
			}
		}
	}
</style>