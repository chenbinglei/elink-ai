<template>
	<view class="startupParameter">
		
		<view class="content_list">
			<view class="content_list_left">启动方式</view>
			<view class="content_list_right">
				<template v-for="(item,index) in powerWayArray">
					<view class="content_li" :key="index">
						<sm-checkbox type="radio" :activeInfo="item" isClick :checked="startConfigParame.powerWay == item.id" isReturnObject
						        :lable="item.name" :startMode="startMode" @change="clickSlecetPowerWay"></sm-checkbox>
					</view>
				</template>
			</view>
		</view>
		
		<!-- 预约充电才进行展示 -->
		<template v-if="startConfigParame.powerWay == 1">
			<view class="clockingTime flex-between">
				<view class="clockingTime_left">
					<template v-for="(item,index) in dateArray">
						<view class="leftButton" :key="index" :class="{ activeButton: item.id == startConfigParame.dateType }" @click="clickDateType(item.id)">
							<text class="text">{{ item.name }}</text>
						</view>
					</template>
				</view>
				<view class="clockingTime_right" @click="clickClockingTime">{{ startConfigParame.clockingTime }}</view>
			</view>
		</template>
		
		<view class="content_list">
			<view class="content_list_left">启动策略</view>
			<view class="content_list_right"></view>
		</view>
		
		<view class="strategy_list">
			<template v-for="(item,index) in strategyArray">
				<view class="strategy_li" :key="index" :class="{activeClass: item.id === startConfigParame.strategy}" @click="clickStrategyFun(item.id)">
					<view class="strategy_li_left">
						<text class="iconfont" :class="item.iconName"></text>
						<text class="text">{{ item.name | textValue(startMode) }}</text>
					</view>
					<view class="strategy_li_right" v-if="item.id === startConfigParame.strategy && item.id">
						<uni-easyinput :type="item.id === 3 ? 'digit' : 'number'" v-model="startConfigParame.strategyCfg" :inputBorder="false"
						    :placeholder="item.placeholder" :styles="inputStyle1" :clearable="false" @input="inputChange">
							<template v-slot:right>
								<view class="unit" v-if="item.unit">{{ item.unit }}</view>
							</template>
						</uni-easyinput>
					</view>
				</view>
			</template>
		</view>
		<template v-if="userIsPay && startMode == 1">
			<view class="content_list">
				<view class="content_list_left">充电预付金额</view>
				<view class="content_list_right">
					<template v-for="(item,index) in payWayArray">
						<view class="content_li" :key="index">
							<sm-checkbox type="radio" :activeInfo="item" isClick :checked="startConfigParame.payWay == item.id" isReturnObject
							        :lable="item.name" @change="clickSlecetPayWay"></sm-checkbox>
						</view>
					</template>
				</view>
			</view>
			<!-- 放电钱包余额 -->
			<!-- <view class="balance_class" v-if="startConfigParame.payWay == 4">
				<text>账户可用余额:：</text>
				<text class="number">{{ startConfigParame.balance | moneyTwoNum }}</text>
				<text class="">元</text>
			</view> -->
			<view class="balance_input">
				<uni-easyinput type="digit" class="uni-input" v-model="startConfigParame.prepayMoney" placeholder="请输入预付金额" :styles="inputStyle">
					<template v-slot:right>
						<view class="unit">元</view>
					</template>
				</uni-easyinput>
			</view>
		</template>
		
		<sm-time-selector ref="timeSelector" showType="hourToMinute" @btnConfirm="btnConfirm"></sm-time-selector>
	</view>
</template>

<script>
	import { getNowDateMin } from '@/common/dateTime.js';
	// import { getMemberBagBalance } from "@/firstPackage/api/index.js";
	
	export default {
		name: "StartupParameter",
		props:{
			userIsPay:{
				type:[Number,String],
				default:""
			},
			startMode:{
				type:[Number,String],
				default: 1
			},
			siteRecordId:{
				type:[Number,String],
				default: ""
			},
		},
		data() {
			return {
				startConfigParame:{
					balance: 0, // 放电账户余额
					payWay: 2, //支付方式 1-免支付 2-微信支付 3-支付宝支付 4-放电钱包余额
					prepayMoney: "", //预付金额
					powerWay: 0,  //充电方式 0-立即充电 1-定时充电 2-自动充电
					strategy: 0,  //0:自动充满  1 ：soc电量 2：金额  3：电量
					strategyCfg: "", //soc    金额   电量
					
					dateType: 1, // 1：今日  2： 明日
					clockingTime: "",
				},
				strategyArray: [
					{ id: 0,name: "自动充满",iconName:"icon-chongdianzhuang" },
					{ id: 1,name: "定Soc",iconName:"icon-progress", placeholder: "请输入Soc", unit:"%" },
					{ id: 3,name: "定电量",iconName:"icon-dianchi", placeholder: "请输入电量", unit:"度" },
				],
				payWayArray:[
					// #ifdef MP-WEIXIN
					{ id: 2,name: "微信支付" },
					// #endif
					// #ifdef MP-ALIPAY 
					{ id: 3,name: "支付宝支付" },
					// #endif
					// { id: 4,name: "放电钱包" },
				],
				dateArray: [{ id: 1, name: '今日' }, { id: 2, name: '次日' }],
				powerWayArray: [{ id: 0, name: '立即充电' }, { id: 1, name: '预约充电' }],
				inputStyle: {
					height: '96rpx',
					color: '#454545',
					backgroundColor: '#F1F1F1',
					borderColor: '#ffffff',
					fontSize: '32rpx'
				},
				inputStyle1:{
					height: '84rpx',
					color: '#454545',
					textAlign: "right",
					backgroundColor: '#ffffff',
					borderColor: '#ffffff',
					fontSize: '32rpx'
				}
			}
		},
		methods: {
			inputChange(val) {
				if(this.startConfigParame.strategy == 1){
					if (val > 100){
						this.$set(this.startConfigParame,'strategyCfg',100);
					};
					
					// 放电设置最低电量
					if (val < 30 && this.startMode == 2){
						this.$set(this.startConfigParame,'strategyCfg',30);
					};
				}
			},
			// // 获取会员放电钱包可用余额
			// findMemberBagBalance(){
			// 	getMemberBagBalance({ siteRecordId: this.siteRecordId }).then(res=>{
			// 		let balance = res.data ? res.data : 0;
			// 		this.$set(this.startConfigParame,'balance', balance);
			// 	})
			// },
			// 选择支付方式
			clickSlecetPayWay(item){
				this.$set(this.startConfigParame,'payWay',item.id);
			},
			// 选择启动方式
			clickSlecetPowerWay(item){
				if(item.id == 1)this.getNowDateTimer();
				this.$set(this.startConfigParame,'powerWay',item.id);
			},
			//获取当前时间
			getNowDateTimer(time = null) {
				let timer = time ? time : getNowDateMin(5);
				// console.log(timer);
				let timer1 = timer.split(' ')[1]; // 获取当前时分秒
				let timer2 = timer1.split(':');
				let hourToMinute = timer2[0] + ':' + timer2[1];
				this.$set(this.startConfigParame,'clockingTime',hourToMinute);
			},
			// 打开时间选择器
			clickClockingTime() {
				this.$refs.timeSelector.openSelect(this.clockingTime);
			},
			// 确认时间选择
			btnConfirm(hourToMinute) {
				// console.log(hourToMinute);
				this.$set(this.startConfigParame,'clockingTime',hourToMinute);
			},
			clickStrategyFun(strategy){
				this.$set(this.startConfigParame,'strategy',strategy);
				this.$emit("changEvent",{ type: 'setStrategy', strategy: strategy });
			},
			//选择 今日 还是 次日
			clickDateType(dateType) {
				this.$set(this.startConfigParame,'dateType',dateType);
			},
		}
	}
</script>

<style scoped lang="scss">
	.startupParameter {
		
		.content_list{
			display: flex;
			align-items: center;
			justify-content: space-between;
			margin-bottom: 24rpx;
			
			.content_list_left{
				color: #030303;
				font-size: 28rpx;
				font-weight: bold;
			}
			
			.content_list_right{
				display: flex;
				align-items: center;
				
				.content_li{
					margin-right: 32rpx;
					
					&:last-child{
						margin-right: 0;
					}
				}
				
				.unit {
					color: #454545;
					font-size: 28rpx;
				}
			}
		}
		
		.balance_class{
			display: flex;
			align-items: center;
			margin-bottom: 14rpx;
			font-size: 28rpx;
			color: #B0B0B0;
			
			.number{
				color: #267BFB;
				font-weight: bold;
				margin-right: 4rpx;
			}
		}
		
		.balance_input{
			
			.unit {
				padding-right: 32rpx;
			}
		}
		
		.clockingTime{
			width: 100%;
			height: 68rpx;
			padding: 0 14rpx;
			background: #F1F1F1;
			margin-bottom: 24rpx;
			border-radius: 10rpx;
			box-sizing: border-box;
			display: flex;
			align-items: center;
			
			.clockingTime_left {
				display: flex;
				align-items: center;
			
				.leftButton {
					width: 88rpx;
					height: 48rpx;
					background: #e2e2e2;
					border-radius: 10rpx;
					font-size: 12px;
					font-weight: 400;
					color: #a1a1a1;
					text-align: center;
					line-height: 48rpx;
				}
				.activeButton {
					color: #ffffff;
					font-weight: 600;
					background: #267BFB;
				}
			}
			
			.clockingTime_right {
				font-size: 40rpx;
				font-weight: bold;
				color: #454545;
			}
		}
		
		.strategy_list{
			margin-bottom: 24rpx;
			
			.strategy_li{
				width: 100%;
				height: 96rpx;
				border-radius: 10rpx;
				border: 2rpx solid #DBD9D9;
				margin-bottom: 24rpx;
				padding: 0 22rpx;
				box-sizing: border-box;
				display: flex;
				align-items: center;
				
				.strategy_li_left{
					color: #242424;
					font-size: 32rpx;
					display: flex;
					align-items: center;
					
					.iconfont{
						font-size: 38rpx;
						margin-right: 12rpx;
					}
				}
				
				.strategy_li_right{
					flex: 1;
					margin-left: 8rpx;
				}
				
				&:last-child{
					margin-bottom: 0;
				}
			}
			
			.activeClass{
				border: 2rpx solid #476AE2;
				
				.iconfont{
					color: #476AE2;
				}
			}
		}
	}
</style>