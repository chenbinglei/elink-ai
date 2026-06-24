<template>
	<view class="sunData">
		<template v-for="(item,index) in list">
			<sm-title :title="item.titleName" :key="index">
				<template v-slot:content>
					<view class="content_body" :class="item.className">
						<view class="content_list" v-for="(itam,indax) in item.children" :key="indax">
							<view class="content_list_left">
								<view class="title_name">{{ itam.titleName }}</view>
								<view class="number">{{ returnDisplayData[itam.fieldName] | moreData }}</view>
							</view>
							<view class="content_list_right">
								<text class="iconfont" :class="itam.iconName"></text>
							</view>
						</view>
					</view>
				</template>
			</sm-title>
		</template>
	</view>
</template>

<script>
	import { getNowDateStartOrEnd } from "@/common/dateTime.js";
	import { findDataCountByMemberId } from "@/firstPackage/api/index.js";
	
	export default{
		name:"SunData",
		props:{
			tabsIndex:{
				type: Number,
				default: 1
			},
			selectFromData:{
				type: Object,
				default:()=>{
					return { }
				}
			}
		},
		computed: {
			watchSelectFromData() {
				const { tabsIndex, selectFromData } = this;
				return { tabsIndex, selectFromData };
			}
		},
		watch: {
			watchSelectFromData: {
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
						className: "charge_content",
						children:[
							{ titleName:"充电电量（度）",fieldName:"chargeQt",iconName:"icon-chongdianliang" },
							{ titleName:"充电支出（元）",fieldName:"chargePay",iconName:"icon-feiyong" },
						]
					},
					{
						titleName: "V2G放电数据",
						className: "disCharge_content",
						children:[
							{ titleName:"放电电量（度）",fieldName:"dischargeQt",iconName:"icon-fangdianliang" },
							{ titleName:"放电收入（元）",fieldName:"dischargeIncome",iconName:"icon-feiyong" },
						]
					},
					{
						titleName: "占桩数据",
						className: "content_occupying",
						children:[
							{ titleName:"占桩时长",fieldName:"occupyDuration",iconName:"icon-shichang" },
							{ titleName:"占桩支出（元）",fieldName:"occupyPay",iconName:"icon-feiyong" },
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
				if(this.tabsIndex === 1){
					formInline.startTime = getNowDateStartOrEnd(formInline.sunDateTime);
					formInline.endTime = getNowDateStartOrEnd(formInline.pickerDate,"end");
				}
				// console.log(formInline);
				findDataCountByMemberId({ queryType: this.tabsIndex, ...formInline }).then((res)=>{
					this.returnDisplayData = res.data;
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.content_body{
		display: flex;
		align-items: center;
		margin-bottom: 32rpx;
		
		.content_list{
			flex: 1;
			border-radius: 12rpx;
			padding: 52rpx 32rpx;
			box-sizing: border-box;
			margin-right: 30rpx;
			display: flex;
			align-items: center;
			justify-content: space-between;
			
			.content_list_left{
				color: #989898;
				font-size: 24rpx;
				margin-bottom: 24rpx;
				
				.number{
					color: #2A2A2A;
					font-size: 32rpx;
				}
			}
			
			.content_list_right{
				display: flex;
				align-items: center;
				
				.iconfont{
					font-size: 72rpx;
				}
			}
			
			&:last-child{
				margin-right: 0;
			}
		}
	}
	
	.charge_content{
		
		.content_list{
			background: #F1FBFD;
		}
		
		.iconfont{
			color: #189AEC;
		}
	}
	
	.disCharge_content{
		.content_list{
			background: #F3F2FE;
		}
		
		.iconfont{
			color: #774AFE;
		}
	}
	
	.content_occupying{
		.content_list{
			background: #FEFAF2;
		}
		
		.iconfont{
			color: #FCCD3F;
		}
	}
</style>