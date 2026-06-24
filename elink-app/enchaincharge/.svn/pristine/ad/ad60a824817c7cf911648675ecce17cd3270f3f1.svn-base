<template>
	<view class="monthData">
		<template v-for="(item,index) in list">
			<sm-title :title="item.titleName" :key="index">
				<template v-slot:content>
					<view class="content_body">
						<view class="content_body_top">
							<view class="body_top_li" v-for="(itam,indax) in item.children" :key="indax">
								<view class="title_name">
									<text class="titleName">{{ itam.titleName }}</text>
									<text class="unit" v-if="item.unit">（{{ item.unit }}）</text>
								</view>
								<view class="number">{{ returnDisplayData[itam.fieldName] | moreData }}</view>
							</view>
						</view>
						<view class="content_body_bottom">
							<template v-if="returnDisplayData.xaxisList && returnDisplayData.xaxisList.length">
								<MonthDataChart :seriesList="item.children" :returnDisplayData="returnDisplayData"></MonthDataChart>
							</template>
							<view v-else class="null_data flex-jc-ai-center"><sm-null-data></sm-null-data></view>
						</view>
					</view>
				</template>
			</sm-title>
		</template>
	</view>
</template>

<script>
	import MonthDataChart from "./MonthDataChart.vue";
	import { findDataCountByMemberId } from "@/firstPackage/api/index.js";
	import { getCurrentMonthFirstDay,getCurrentMonthLastDay } from "@/common/dateTime.js";
	
	export default{
		name:"MonthData",
		components:{ MonthDataChart },
		options: { styleIsolation: 'shared' }, //解决/deep/不生效**
		props:{
			selectFromData:{
				type: Object,
				default:()=>{
					return { }
				}
			}
		},
		watch: {
			selectFromData: {
				deep: true,
				handler(newValue, oldValue) {
					this.queryDataCountByMemberId();
				}
			}
		},
		data(){
			return{
				returnDisplayData: {},
				list:[
					{
						titleName: "充电数据",
						children:[
							{ titleName:"充电电量",fieldName:"chargeQt",unit: "度", color: '#328EEC' },
							{ titleName:"充电支出",fieldName:"chargePay",unit: "元",color: '#E9B50F' },
						]
					},
					{
						titleName: "V2G放电数据",
						children:[
							{ titleName:"放电电量",fieldName:"dischargeQt",unit: "度", color: '#328EEC' },
							{ titleName:"放电收入",fieldName:"dischargeIncome",unit: "元",color: '#E9B50F' },
						]
					},
					{
						titleName: "占桩数据",
						children:[
							{ titleName:"占桩时长",fieldName:"occupyDuration",unit: "", color: '#328EEC' },
							{ titleName:"占桩支出",fieldName:"occupyPay",unit: "元",color: '#E9B50F' },
						]
					}
				]
			}
		},
		mounted() {
			this.queryDataCountByMemberId();
		},
		methods:{
			queryDataCountByMemberId(){
				let formInline = JSON.parse(JSON.stringify(this.selectFromData));
				formInline.startTime = getCurrentMonthFirstDay(formInline.monthDateTime);
				formInline.endTime = getCurrentMonthLastDay(formInline.monthDateTime);
				findDataCountByMemberId({ queryType: 2, ...formInline }).then((res)=>{
					this.returnDisplayData = res.data;
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.content_body{
		margin-bottom: 24rpx;
		
		.content_body_top{
			display: flex;
			align-items: center;
			margin-bottom: 12rpx;
			
			.body_top_li{
				flex: 1;
				margin-right: 16rpx;
				border-radius: 8rpx;
				background: #F5F5F7;
				padding: 16rpx 20rpx;
				box-sizing: border-box;
				
				.title_name{
					color: #989898;
					font-size: 24rpx;
					margin-bottom: 12rpx;
				}
				
				.number{
					color: #1090CA;
					font-size: 24rpx;
				}
				
				&:last-child{
					margin-right: 0;
				}
			}
		}
		
		.content_body_bottom{
			height: 460rpx;
			
			.null_data{
				height: 100%;
				
				/deep/ .nullDataImage{
					width: 200rpx;
					height: 200rpx;
				}
			}
		}
	}
</style>