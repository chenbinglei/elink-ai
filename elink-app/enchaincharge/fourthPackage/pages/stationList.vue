<template>
	<view class="container">
		<station-search-from ref="stationSearchFromRef" @headerFormEvent="pagingReload"></station-search-from>
		<view class="container_content" @click="clickBlankSpace">
			<z-paging ref="paging" :fixed="false" :auto="false" v-model="dataList" @query="getSiteList">
				<!-- 设置自己的emptyView组件，非必须。空数据时会自动展示空数据组件，不需要自己处理 -->
				<sm-null-data slot="empty" title="未搜索到站点"></sm-null-data>
				<view class="station_list">
					<sm-station-card v-for="(item, index) in dataList" :key="index" :siteInfo="item"></sm-station-card>
				</view>
			</z-paging>
		</view>
	</view>
</template>

<script> 
import { mapState } from 'vuex';
import { queryMapSiteList } from '@/api/home.js';
import StationSearchFrom from "@/fourthPackage/components/stationList/StationSearchFrom.vue";

export default{
	name: "stationList",
	components:{StationSearchFrom},
	computed: { ...mapState(['latAndLongitude']) },
	data(){
		return {
			dataList: [],
			selectFromData:{},
		}
	},
	onLoad() {
		this.initStorageSync();
	},
	methods:{
		initStorageSync(){
			let preferenceFromData = uni.getStorageSync('STATION_FORM_DATA');
			// console.log(preferenceFromData);
			if(preferenceFromData){
				this.$refs.stationSearchFromRef.preferenceFromData = JSON.parse(JSON.stringify(preferenceFromData));
			}
			this.pagingReload();
		},
		pagingReload() {
			this.selectFromData = {
				...this.$refs.stationSearchFromRef.selectFromData,
				...this.$refs.stationSearchFromRef.preferenceFromData,
			};
			this.$refs.paging.reload();
		},
		// 查询站点列表
		getSiteList(pageNo, pageSize) {
			//这里的pageNo和pageSize会自动计算好，直接传给服务器即可
			queryMapSiteList({ page: pageNo, size: pageSize, ...this.selectFromData, ...this.latAndLongitude }).then(res => {
				//请勿在网络请求回调中给dataList赋值！！只需要调用complete就可以了
				
				let stationList = [];
				if(res.data.items && res.data.items.length){
					stationList = JSON.parse(JSON.stringify(res.data.items));
					stationList.forEach(item=>{
						if(item.location){
							let coordinates = JSON.parse(item.location);
							item.stationAddress = coordinates.address;
						}
					})
				}
				
				this.$refs.paging.complete(stationList);
			}).catch(res => {
				//如果请求失败写this.$refs.paging.complete(false)，会自动展示错误页面
				//注意，每次都需要在catch中写这句话很麻烦，z-paging提供了方案可以全局统一处理
				//在底层的网络请求抛出异常时，写uni.$emit('z-paging-error-emit');即可
				this.$refs.paging.complete(false);
			});
		},
		clickBlankSpace(){
			this.$refs.stationSearchFromRef.kilometerVisible = false;
			this.$refs.stationSearchFromRef.distanceAndPrice = false;
			this.$refs.stationSearchFromRef.fromContentVisible = false;
		}
	}
}
</script>

<style lang="scss" scoped>
	.container_content{
		overflow-y: auto;
		display: initial;
		
		.station_list {
			padding: 42rpx 32rpx 0 32rpx;
			box-sizing: border-box;
		}
	}
</style>