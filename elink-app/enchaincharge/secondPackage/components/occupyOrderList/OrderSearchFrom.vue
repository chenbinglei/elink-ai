<template>
	<view class="orderSearchFrom">
		
		<view class="from_list">
			<view class="from_li" @click="clickSettingBut(1)">
				<template v-if="pageType === 'occupyOrderList'">
					<text class="text">{{ selectFromData.orderState ? selectFromData.orderStateText : '全部订单' }}</text>
				</template>
				<template v-if="pageType === 'orderlist'">
					<text class="text">{{ selectFromData.orderLogoText }}</text>
				</template>
				<view :class="['triangle-down', orderStateVisible ? 'arrowTop' : 'arrowBottom']"></view>
			</view>
			<view class="from_li" @click="clickSettingBut(2)">
				<text class="text">时间</text>
				<view :class="['triangle-down', deteTimeVisible ? 'arrowTop' : 'arrowBottom']"></view>
			</view>
		</view>
		
		<view class="from_content_body">
			<view class="from_content" v-if="orderStateVisible">
				<template v-if="pageType === 'occupyOrderList'">
					<sm-select-list :list="orderStateArray" :active="selectFromData.orderState" @changEvent="clickLeftSelect"></sm-select-list>
				</template>
				<template v-if="pageType === 'orderlist'">
					<sm-select-list :list="orderLogoArray" :active="selectFromData.orderLogo" @changEvent="clickLeftSelect"></sm-select-list>
				</template>
			</view>
			<view class="from_content" v-if="deteTimeVisible">
				<view class="from_flex">
					<view class="list">
						<template v-for="(item, index) in dateTimeArray">
							<view class="list_li_content" :key="index" @click="clickItemButton('dateTime',item.id)">
								<view class="list_li">
									<text>{{ item.name }}</text>
								</view>
							</view>
						</template>
					</view>
					<view class="startAndEndDate">
						<view class="dateTime" :class="{ null_bg_color: oldSelectFromData.startDate }" @click="clickSelectTime(1)">
							<text class="null_time" v-if="!oldSelectFromData.startDate">请选择日期</text>
							<text class="timer">{{ oldSelectFromData.startDate }}</text>
						</view>
						<view class="text">~</view>
						<view class="dateTime" :class="{ null_bg_color: oldSelectFromData.endDate }" @click="clickSelectTime(2)">
							<text class="null_time" v-if="!oldSelectFromData.endDate">请选择日期</text>
							<text class="timer">{{ oldSelectFromData.endDate }}</text>
						</view>
					</view>
				</view>
				<view class="bottom_button">
					<view class="button resetBut" @click="clickReset">重置</view>
					<view class="button enterBut" @click="clickEnter">确定</view>
				</view>
			</view>
		</view>
		
		<sm-time-selector ref="timeSelectorRef" @btnConfirm="btnConfirm"></sm-time-selector>
		
	</view>
</template>

<script>
	import { pickerDateToYear,compareTime } from "@/common/dateTime.js";
	
	export default{
		name:"OrderSearchFrom",
		props:{
			pageType:{
				type: String,
				default: "occupyOrderList"
			}
		},
		data(){
			return{
				operateType: 1,
				selectFromData: {
					orderLogo: "",
					orderLogoText: '全部订单'
				},
				operateTimeType: 1,
				oldSelectFromData: {},
				deteTimeVisible: false,
				orderStateVisible: false,
				
				orderLogoArray: [{ id: "", name: '全部订单' },{ id: 1, name: '进行中' }, { id: 2, name: '已完成' }],
				dateTimeArray: [{ id: 1, name: '近1个月' },{ id: 3, name: '近3个月' }, { id: 6, name: '近6个月' }],
				orderStateArray: [{ id: null, name: '全部订单' },{ id: 1, name: '在途' }, { id: 2, name: '待支付' }, { id: 3, name: '已完成' }, { id: 4, name: '异常' }],
			}
		},
		methods:{
			clickLeftSelect(item){
				let fieldName = "orderState";
				if(this.pageType === "orderlist")fieldName = "orderLogo";
				this.selectFromData[fieldName + 'Text'] = item.name;
				this.selectFromData[fieldName] = item.id;
				this.orderStateVisible = false;
				this.$emit("headerFormEvent",{ type:"pagingReload" });
			},
			clickSettingBut(operateType){
				this.operateType = operateType;
				
				if(operateType === 1){
					this.orderStateVisible = !this.orderStateVisible;
					if(this.orderStateVisible)this.deteTimeVisible = false;
				}
				
				if(operateType === 2){
					this.deteTimeVisible = !this.deteTimeVisible;
					if(this.deteTimeVisible){
						this.orderStateVisible = false;
						this.oldSelectFromData = JSON.parse(JSON.stringify(this.selectFromData));
					}
				}
			},
			clickEnter(){
				if(this.operateType === 2){
					this.deteTimeVisible = false;
					this.selectFromData = JSON.parse(JSON.stringify(this.oldSelectFromData));
					this.$emit("headerFormEvent",{ type:"pagingReload" });
				}
			},
			clickReset(){
				this.oldSelectFromData = JSON.parse(JSON.stringify(this.selectFromData));
			},
			clickItemButton(fieldName,id){
				if(fieldName === "dateTime"){
					let dateTime = pickerDateToYear(id * 30,true);
					this.$set(this.oldSelectFromData,'endDate',dateTime[1]);
					this.$set(this.oldSelectFromData,'startDate',dateTime[0]);
				}
			},
			// 打开选择时间弹框
			clickSelectTime(operateTimeType){
				let timer = operateTimeType === 1 ? this.oldSelectFromData.startDate : this.oldSelectFromData.endDate;
				this.$refs.timeSelectorRef.openSelect(timer);
				this.operateTimeType = operateTimeType;
			},
			btnConfirm(timer){
				// console.log(timer);
				let endDate = this.oldSelectFromData.endDate;
				let startDate = this.oldSelectFromData.startDate;
				if(this.operateTimeType === 1)startDate = timer;
				if(this.operateTimeType === 2)endDate = timer;
				
				let objTime = compareTime(startDate,endDate);
				this.$set(this.oldSelectFromData,'endDate',objTime.endTime);
				this.$set(this.oldSelectFromData,'startDate',objTime.startTime);
			}
		}
	}
</script>

<style scoped lang="scss">
	.orderSearchFrom{
		position: relative;
		background-color: #ffffff;
		
		.from_list{
			display: flex;
			align-items: center;
			
			.from_li{
				flex: 1;
				height: 68rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				
				.text{
					color: #242424;
					font-size: 24rpx;
					margin-right: 12rpx;
				}
				
				.triangle-down{
					transition: all 0.28s;
				}
				
				.arrowTop {
					transform: rotate(180deg) translateX(-5px);
				}
			}
		}
		
		.from_content_body{
			width: 100%;
			z-index: 10001;
			position: absolute;
			left: 0;
			top: 68rpx;
			
			.from_content{
				width: 100%;
				transition: all 0.28s;
				background: #ffffff;
				box-sizing: border-box;
				padding: 20rpx 44rpx 44rpx 44rpx;
				box-shadow: 0px 2rpx 12rpx 0px rgba(0, 0, 0, 0.1);
				
				.from_flex{
					
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
								width: 92%;
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
				
				.startAndEndDate{
					display: flex;
					align-items: center;
					margin: 32rpx 0 24rpx 0;
					
					.dateTime{
						flex: 1;
						height: 72rpx;
						display: flex;
						align-items: center;
						justify-content: center;
						border-radius: 8rpx;
						border: 2rpx solid #107be9;
						
						.null_time{
							font-size: 28rpx;
							color: rgba(0,0,0,0.1);
						}
					}
					
					.text{
						margin: 0 50rpx;
					}
					
					.null_bg_color{
						border: none;
						background: rgba(31,116,226,0.1);
						
						.timer{
							font-size: 28rpx;
							color: rgba(0,0,0,0.6);
						}
					}
				}
				
				.bottom_button {
					display: flex;
					align-items: center;
					justify-content: space-between;
					margin-top: 44rpx;
					
					.radio{
						color: #7B7D7F;
						font-size: 24rpx;
						display: flex;
						align-items: center;
					}
					
					.resetBut{
						height: 80rpx;
						color: #107be9;
						padding: 0 32rpx;
						text-align: center;
						line-height: 80rpx;
					}
				
					.enterBut {
						flex: 1;
						color: #ffffff;
						background: #107be9;
						height: 80rpx;
						border-radius: 8rpx;
						font-size: 34rpx;
						font-weight: 500;
						text-align: center;
						line-height: 80rpx;
					}
				}
			}
		}
	}
</style>