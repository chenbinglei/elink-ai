<template>
	<view class="container">
		<view class="from_header">
			<uni-easyinput type="search" class="uni-input" prefixIcon="search" v-model="keyword" placeholder="请输入目的地或者站点名称" :inputBorder="false"
				:styles="inputStyle" @input="keywordChange"></uni-easyinput>
		</view>
		<view class="container_content">
			<template v-if="selectSiteList && selectSiteList.length">
				<template v-for="(item, index) in selectSiteList">
					<view class="uni-flex-item">
						<sm-station-card :key="index" :siteInfo="item"></sm-station-card>
					</view>
				</template>
			</template>
			<view class="null_data uni-flex-item uni-flex flex-center align-center" v-else><sm-null-data title="未搜索到对应站点"></sm-null-data></view>
		</view>
	</view>
</template>

<script>
	import { mapState } from 'vuex';
	import { queryMapSiteList } from '@/api/home.js';
	
	export default{
		name: "stationSearch",
		computed: {
			...mapState(['latAndLongitude'])
		},
		data(){
			return{
				keyword: '',
				inputStyle: {
					height: '80rpx',
					color: '#030303',
					backgroundColor: '#ECEAEA',
					borderColor: '#FFFFFF',
					borderRadius: '10rpx',
					fontSize: '32rpx'
				},
				oldAllSiteList: [],
				selectSiteList: []
			}
		},
		onLoad() {
			this.queryAllSiteList();
		},
		methods:{
			queryAllSiteList(){
				queryMapSiteList({ ...this.latAndLongitude,size: 0 }).then(res=>{
					res.data && res.data.forEach(item => {
						if (item.siteLabel)item.siteLabel = JSON.parse(item.siteLabel);
						if (item.location){
							let coordinates = JSON.parse(item.location);
							item.stationAddress = coordinates.address;
						} 
					});
					
					this.oldAllSiteList = res.data;
					this.selectSiteList = res.data;
				})
			},
			// 搜索框名称发生改变执行
			keywordChange() {
				if (this.keyword) {
					let selectSiteList = [];
					let allSiteList = JSON.parse(JSON.stringify(this.oldAllSiteList));
					for (let i = 0; i < allSiteList.length; i++) {
						if (allSiteList[i].siteName.indexOf(this.keyword) != -1 || (allSiteList[i].stationAddress && allSiteList[i].stationAddress.indexOf(this.keyword) != -1) ) {
							let findItem = selectSiteList.find(item => item.siteId == allSiteList[i].siteId);
							if (!findItem) {
								let siteList = JSON.parse(JSON.stringify(allSiteList[i]));
								if (siteList.siteName && siteList.siteName.indexOf(this.keyword) != -1) {
									siteList.siteName = siteList.siteName.replaceAll(this.keyword, `<span class="searchName">${this.keyword}</span>`);
								}
								selectSiteList.push(siteList);
							}
						}
					}
					this.selectSiteList = selectSiteList;
				} else {
					this.selectSiteList = JSON.parse(JSON.stringify(this.oldAllSiteList));
				}
			},
		}
	}
</script>

<style scoped lang="scss">
	.container{
		
		.from_header{
			padding: 12rpx 32rpx;
			box-sizing: border-box;
			background-color: #ffffff;
		}
		
		.container_content{
			display: initial;
			overflow-y: auto;
			padding: 24rpx 32rpx;
			box-sizing: border-box;
			
			/deep/ .site_li {
				.li_title {
					.searchName {
						color: #1791FF;
						font-weight: bold;
					}
				}
			}
			
			.null_data{
				height: 100%;
			}
		}
	}
</style>