<template>
	<view class="container">
		
		<view class="container_top">
			<sm-tabs class="tabs_left" :tabsArray="tabsArray" :tabsIndex="tabsIndex" @changEvent="changEvent"></sm-tabs>
			<view class="timer_right" v-if="tabsIndex <= 2" @click="clickOpenDateTime">
				<view class="date_text" v-if="tabsIndex === 1">{{ selectFromData.sunDateTime }}</view>
				<view class="date_text" v-if="tabsIndex === 2">{{ selectFromData.monthDateTime }}</view>
				<text class="iconfont icon-rili"></text>
			</view>
		</view>
		
		<view class="container_content">
			<MonthData v-if="tabsIndex === 2" :selectFromData="selectFromData"></MonthData>
			<SunData v-if="tabsIndex !== 2" :tabsIndex="tabsIndex" :selectFromData="selectFromData"></SunData>
		</view>
		
		<sm-time-selector ref="timeSelectorRef" :showType="timeType" :isSplicingDay="false" @btnConfirm="btnConfirm"></sm-time-selector>
	</view>
</template>

<script>
	import { getDaysFromCurrentTime,getNowDate } from "@/common/dateTime.js";
	import SunData from "@/firstPackage/components/dataStatistics/SunData.vue"
	import MonthData from "@/firstPackage/components/dataStatistics/MonthData.vue"
	
	export default {
		name: "dataStatistics",
		components: {SunData,MonthData},
		data() {
			return {
				tabsIndex: 1,
				timeType: "date",
				tabsArray: [{ id: 1, name: "日" }, { id: 2, name: "月" }, { id: 3, name: "累计" }],
				selectFromData: {
					sunDateTime: getNowDate(),
					monthDateTime: getDaysFromCurrentTime(0,1)
				}
			}
		},
		methods: {
			changEvent(tabsIndex){
				this.tabsIndex = tabsIndex;
			},
			clickOpenDateTime(){
				this.timeType = "date";
				let timer = this.selectFromData.sunDateTime;
				
				if(this.tabsIndex === 2){
					this.timeType = "yearAndMonth";
					timer = this.selectFromData.monthDateTime;
				}
				
				this.$refs.timeSelectorRef.openSelect(timer);
			},
			btnConfirm(timer){
				if(this.tabsIndex === 1){
					this.$set(this.selectFromData,'sunDateTime',timer);
				}
				if(this.tabsIndex === 2){
					this.$set(this.selectFromData,'monthDateTime',timer);
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	.container {
		padding: 12rpx 0;
		box-sizing: border-box;
		
		.container_top{
			display: flex;
			align-items: center;
			padding: 0 32rpx;
			box-sizing: border-box;
			margin-bottom: 12rpx;
			
			.tabs_left{
				flex: 1;
			}
			
			.timer_right{
				height: 100%;
				margin-left: 10rpx;
				border-radius: 6rpx;
				background-color: #ffffff;
				padding: 0 10rpx;
				box-sizing: border-box;
				display: flex;
				align-items: center;
				
				.date_text{
					font-size: 28rpx;
					margin-right: 12rpx;
				}
				
				.iconfont{
					font-size: 38rpx;
				}
			}
		}
		
		.container_content{
			box-sizing: border-box;
			background-color: #ffffff;
			padding: 16rpx 32rpx 12rpx 32rpx;
		}
	}
</style>