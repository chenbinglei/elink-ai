<template>
	<view class="stationMap">
		<map id="map" class="map" :markers="markerList" @markertap="markertap" @regionchange="regionchange"
			:include-points="pointsArray" :show-scale="optionsMap.showScale" :show-compass="optionsMap.showCompass"
			:latitude="latAndLongitude.centerLat" :longitude="latAndLongitude.centerLon" :enable-3D="optionsMap.enable3D"
			:show-location="optionsMap.showlocation" :enable-rotate="optionsMap.enableRotate"
			:enable-overlooking="optionsMap.enableOverlooking"></map>
			
		<sm-popup ref="smPopupRef" :cancelClosePopup="false" title="筛选" cancelText="重置" @popupEvent="popupEvent">
			<template v-slot:content>
				<view class="content_body">
					<view class="title"><text class="text">充电方式</text></view>
					<view class="list">
						<template v-for="(item, index) in chargeModeArray">
							<view class="list_li_content" :key="index" @click="clickItemButton('chargeMode',item.id)">
								<view class="list_li" :class="{ active_li: oldSelectFromData.chargeMode == item.id }">
									<text>{{ item.name }}</text>
								</view>
							</view>
						</template>
					</view>
					<view class="title"><text class="text">停车费</text></view>
					<view class="list">
						<template v-for="(item, index) in parkCostArray">
							<view class="list_li_content" :key="index" @click="clickItemButton('parkCostType',item.id)">
								<view class="list_li"  :class="{ active_li: oldSelectFromData.parkCostType === item.id }">
									<text>{{ item.name }}</text>
								</view>
							</view>
						</template>
					</view>
					<view class="title"><text class="text">设备状态</text></view>
					<view class="list">
						<template v-for="(item, index) in isIdleArray">
							<view class="list_li_content" :key="index" @click="clickItemButton('isIdle',item.id)">
								<view class="list_li"  :class="{ active_li: oldSelectFromData.isIdle == item.id }" >
									<text>{{ item.name }}</text>
								</view>
							</view>
						</template>
					</view>
				</view>
			</template>
		</sm-popup>
	</view>
</template>

<script>
	import { mapState } from 'vuex';
	import { queryMapSiteList } from '@/api/home.js';
	
	export default {
		name: 'StationMap',
		computed: {
			...mapState(['latAndLongitude'])
		},
		data() {
			return {
				map: null,
				markerList: [], // 
				pointsArray: [], // 所有经纬度的数据
				mapSiteList: [], //当前站点列表
				pointsArray: [], // 所有经纬度的数据

				// 地图配置信息
				optionsMap: {
					scale: 11,
					minScale: 5, //最小缩放级别
					enable3D: true, //	是否显示3D楼块
					showScale: false, // 显示比例尺。
					showCompass: false, //是否显示指南针
					enableRotate: true, //是否支持旋转
					showlocation: true, //显示带有方向的当前定位点
					enableOverlooking: true ,// 是否开启俯视
				},
				markerStyle: {
					width: 30,
					height: 45,
					joinCluster: true
				},
				// 点聚合样式配置
				markerClusterStyle: {
					width: 0,
					height: 0,
					label: {
						width: 38,
						height: 38,
						fontSize: 16,
						color: '#ffffff',
						bgColor: '#09C095',
						borderWidth: 2,
						borderColor: '#ffffff',
						borderRadius: 19,
						textAlign: 'center',
						anchorX: -15,
						anchorY: -45
					}
				},
				
				selectFromData: {
					size: 0,
					isIdle: "",
					chargeMode: "",
				},
				
				activeMarkerId: "",
				activeClickIndex: -1,
				oldSelectFromData: {},
				isIdleArray: [{ id: 0, name: '全部' },{ id: 1, name: '只看空闲' }],
				chargeModeArray: [{ id: "", name: '全部' },{ id: 29, name: '快充' }, { id: 28, name: '慢充' }],
				parkCostArray: [{ id: 1, name: '免费停车' }, { id: 2, name: '停车收费' }, { id: 3, name: '限时免费' }, { id: 4, name: '充电限免' }],
			}
		},
		mounted() {
			this.initCreatedMap();
		},
		methods: {
			// 创建地图实例
			initCreatedMap() {
				this.map = uni.createMapContext('map', this);
				// console.log(this.map);
				this.createdMarkerCluster(); // 创建地图
			},
			// 创建点聚合
			createdMarkerCluster() {
				//初始化点聚合的配置，未调用时采用默认配置
				this.map.initMarkerCluster({
					enableDefaultStyle: false, //启用默认的聚合样式
					zoomOnClick: true, //点击已经聚合的标记点时是否实现聚合分离
					gridSize: 60, // 聚合算法的可聚合距离
					success: res => {
						console.log('点聚合重置成功！');
					}
				});

				// #ifdef MP-WEIXIN 
				// 监听聚合事件
				this.map.on('markerClusterCreate', e => {
					let clusterMarkers = [];
					const clusters = e.clusters; // 新产生的聚合簇
					clusters.forEach((cluster, index) => {
						const { center, clusterId, markerIds } = cluster;
						this.markerClusterStyle.label.content = markerIds.length + '';
						let clusterObj = { ...center, clusterId, ...this.markerClusterStyle };
						clusterMarkers.push(clusterObj);
					});

					this.map.addMarkers({
						clear: false, //是否先清空地图上所有的marker
						markers: clusterMarkers
					});
				});
				// #endif
			},
			//视野发生变化时触发
			regionchange(e) {
				// console.log(e)
				this.clearActiveMarkerFun();
				this.$emit("changEvent",{ markerStatus: false,markerInfo: {} });
			},
			clearActiveMarkerFun(){
				if(this.activeClickIndex !== -1){
					this.markerList[this.activeClickIndex].iconPath = this.getStaticFilePath('mapIcon2.png');
					this.activeClickIndex = -1;
					this.activeMarkerId = "";
				}
			},
			//点击标记点时触发，e.detail = {markerId}
			markertap(e) {
				// console.log(e);
				let { markerId } = e.detail; // marker 对应的 id；
				// 点击之前先消除一次选中种状态
				if(this.activeMarkerId != markerId)this.clearActiveMarkerFun();
				
				let findItem = this.mapSiteList.find(item => item.siteIndex === markerId);
				// 设置当前站点图标高亮
				this.activeClickIndex = this.markerList.findIndex(item => item.siteIndex === markerId);
				if(this.activeClickIndex !== -1){
					// console.log(this.markerList);
					// console.log(this.activeClickIndex);
					this.markerList[this.activeClickIndex].iconPath = this.getStaticFilePath('mapIcon1.png');
				}
				this.activeMarkerId = markerId;
				this.$emit("changEvent",{ markerStatus: true,markerInfo: findItem });
			},
			// 点击回到自身位置
			moveTolocation() {
				// 移动到中心点
				this.map.moveToLocation({
					// longitude: this.latAndLongitude.longitude,
					// latitude: this.latAndLongitude.latitude,
					success: res => {
						console.log('移动完成:', res);
					},
					fail: err => {
						uni.showToast({
							icon: 'none',
							title: '请点击右上角打开定位权限'
						});
					}
				});
			},
			// 获取站点列表
			getMapSiteList() {
				// 地图拥有数据不在获取
				// if(this.mapSiteList && this.mapSiteList.length)return
				
				queryMapSiteList({ ...this.latAndLongitude, ...this.selectFromData }).then(res => {
					let markerList = [];
					let mapSiteList = [];
					let pointsArray = [];
					let allSiteList = res.data;
			
					//把自身push进去
					pointsArray.push({ latitude: this.latAndLongitude.centerLat, longitude: this.latAndLongitude.centerLon });
			
					if (allSiteList && allSiteList.length) {
						for (let i = 0; i < allSiteList.length; i++) {
							if (allSiteList[i].location) {
								allSiteList[i].siteIndex = i + 1;
								allSiteList[i].id = allSiteList[i].siteId;
								let coordinates = JSON.parse(allSiteList[i].location);
								coordinates.stationAddress = coordinates.address;
		
								mapSiteList.push({ ...allSiteList[i], ...coordinates });
								markerList.push({
									...this.markerStyle,
									id: allSiteList[i].siteIndex,
									latitude: coordinates.latitude,
									longitude: coordinates.longitude,
									siteIndex: allSiteList[i].siteIndex,
									iconPath: this.getStaticFilePath( allSiteList[i].siteId == this.activeMarkerId ? 'mapIcon1.png' : 'mapIcon2.png')
								});
								pointsArray.push({ latitude: coordinates.latitude, longitude: coordinates.longitude });
							}
						}
					};
			
					this.mapSiteList = mapSiteList;
					this.pointsArray = pointsArray;
					this.markerList = markerList;
					// console.log(this.mapSiteList);
				});
			},
			openScreenPopup(){
				this.oldSelectFromData = JSON.parse(JSON.stringify(this.selectFromData));
				this.$refs.smPopupRef.openPopupFun();
			},
			// 设备偏好里的查询条件
			clickItemButton(fieldName,id){
				// 两次点击相同
				if(this.oldSelectFromData[fieldName] === id){
					this.$set(this.oldSelectFromData,fieldName,"");
					return
				}
				this.$set(this.oldSelectFromData,fieldName,id);
			},
			popupEvent(data){
				if(data.operationType === "enter"){
					this.selectFromData = JSON.parse(JSON.stringify(this.oldSelectFromData));
					this.getMapSiteList();
				}
				if(data.operationType === "cancel"){
					this.selectFromData = JSON.parse(JSON.stringify(this.oldSelectFromData));
					uni.showToast({ icon:"none",title:"重置成功！" });
				}
			}
		}
	}
</script>

<style scoped lang="scss">
	.stationMap {
		width: 100%;
		height: 100%;
		overflow: hidden;

		.map {
			width: 100%;
			height: 105%;
		}
	}
	
	.content_body{
		padding: 24rpx 32rpx;
		box-sizing: border-box;
		
		.title {
			margin-bottom: 18rpx;
			display: flex;
			align-items: center;
			justify-content: space-between;
		
			.text {
				font-size: 30rpx;
				font-weight: 600;
			}
		}
		
		.list {
			display: flex;
			flex-wrap: wrap;
			
			.list_li_content{
				width: 33.33%;
				height: 68rpx;
				display: flex;
				justify-content: center;
				margin-bottom: 20rpx;
				
				.list_li {
					width: 95%;
					height: 100%;
					border-radius: 8rpx;
					font-size: 24rpx;
					text-align: center;
					line-height: 68rpx;
					border: 2rpx solid #107be9;
					box-sizing: border-box;
				}
				
				.active_li {
					color: #ffffff;
					background: #107be9;
				}
			}
		}
	}
</style>