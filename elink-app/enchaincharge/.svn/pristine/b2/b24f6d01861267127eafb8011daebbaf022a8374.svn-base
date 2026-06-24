<template>
	<view class="card_list">
		<view class="head_text">电站信息</view>
		<view class="card_li">
			<view class="card_left">开放时间</view>
			<view class="card_right text">
				<view class="li">
					<text>{{ siteInfo.busineHours | moreData }}</text>
				</view>
			</view>
		</view>
		<view class="card_li">
			<view class="card_left">客服电话</view>
			<view class="card_right text">
				<view class="li">
					<text>{{ siteInfo.serviceTel | moreData }}</text>
				</view>
			</view>
		</view>
		<view class="card_li">
			<view class="card_left">运营商</view>
			<view class="card_right text">
				<view class="li">
					<text>{{ siteInfo.operatorName | moreData }}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import { findCompanyList } from '@/firstPackage/api/index.js';
	export default {
		name: "StationInfo",
		props: {
			siteInfo: {
				type: Object,
				default: () => {
					return {}
				}
			}
		},
		data() {
			return {
				
			}
		},
		methods: {
			setWeekTime(array) {
				let weekTime = [];
				if(array && array.length){
					if(array.length === 7)return '周一至周日';
					for(let i = 0;i < array.length ;i++){
					    let str = '';
					    if(String(array[i]) === "1")str = '星期一';
					    if(String(array[i]) === "2")str = '星期二';
					    if(String(array[i]) === "3")str = '星期三';
					    if(String(array[i]) === "4")str = '星期四';
					    if(String(array[i]) === "5")str = '星期五';
					    if(String(array[i]) === "6")str = '星期六';
					    if(String(array[i]) === "7")str = '星期日';
					    weekTime.push(str)
					}
				}
			    return weekTime.join(',')
			}
		}
	}
</script>

<style scoped lang="scss">
.card_list {
	padding: 32rpx;
	box-sizing: border-box;
		
	.head_text {
		font-size: 18px;
		margin-bottom: 24rpx;
	}
	
	.card_li {
		display: flex;
		align-items: flex-start;
		font-size: 12px;
		margin-bottom: 24rpx;
		
		&:last-child {
			margin-bottom: 0;
		}
		
		.card_left{
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
			padding: 16rpx 0;
			color: #989898;
			
			.li_top{
				display: flex;
				justify-content: space-between;
			}
			
			.li_center{
				padding: 24rpx 0;
				
				.money {
					color: #476ae2;
					margin-right: 16rpx;
					font-size: 16px;
				}
			}
			
			.overLine {
				width: 100%;
				height: 2rpx;
				margin-top: 32rpx;
				border-bottom: 2rpx solid #E1E1E1;
			}
			
		}
	}
}
</style>
