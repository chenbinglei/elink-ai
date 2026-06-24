<template>
	<view class="container">
		<view class="container_content">
			<view class="card_list" @click="clickOpenGunPopup">
				<pile-details-card ref="pileDetailsCardRef" :gunCode="gunCode" :pileData="pileData" @changEvent="changEvent"></pile-details-card>
			</view>

			<switch-table-card :tabsArray="tabsArray" :tabsIndex="tabsIndex" @changEvent="changEvent">
				<template v-slot:slotContent>
					<view class="content_body">
						<StartupParameter ref="startupParameterRef" v-show="tabsIndex === 1" :startMode="startMode" :userIsPay="userIsPay"
						        :siteRecordId="pileData.siteRecordId" @changEvent="changEvent"></StartupParameter>
						<PriceInformation v-if="tabsIndex === 2" :isParkingFees="false" :startMode="startMode" :deviceType="2"
						        :deviceId="pileData.deviceId"></PriceInformation>
					</view>
				</template>
			</switch-table-card>
		</view>
		
		<view class="alter_text">
			<text v-if="startMode == 1">
				<text v-if="strategy === 0">预付金额用完后，自动结束充电；或电池充满后，自动结束充电，余额原路退回</text>
				<text v-else>预付金额用完后，自动结束充电；或充电量达到设定值后，自动结束充电，余额原路退回</text>
			</text>
			<text v-else>达到放电策略值后，自动结束放电</text>
		</view>
		
		<!-- 立即充电、放电按钮 -->
		<StartChargingButton ref="startChargingButtonRef" :startMode="startMode" @startUpPileFun="startUpPileFun" @changEvent="changEvent"></StartChargingButton>
		
		<sm-popup ref="smPopupRef" title="选择枪" :footerVisible="false">
			<template v-slot:content>
				<view class="gun_list_body">
					<sm-select-list textKey="gunName" onlyKey="gunCode" :list="pileData.gunDataList" :active="gunCode"
					        selectIcon="checkbox" @changEvent="selectGunCodeFun"></sm-select-list>
				</view>
			</template>
		</sm-popup>
		
		<!-- 启动中弹框 -->
		<template v-if="smPopupStartRef">
			<sm-popup ref="smPopupStartRef" popupType="center" :isMaskClick="false" :headerVisible="false" :footerVisible="false">
				<template v-slot:content>
					<view class="content_body">
						<StartWaitingPopUp ref="StartWaitingPopUpRef" :startMode="startMode" :pileCode="pileCode" :gunCode="gunCode"
						        :powerWay="powerWay" @changEvent="changEvent"></StartWaitingPopUp>
					</view>
				</template>
			</sm-popup>
		</template>
		
	</view>
</template>

<script>
	import { money } from '@/common/validate.js';
	import { gunStatusToastTitle } from '@/common/common.js';
	import { queryDeviceInfoByPileCode } from "@/firstPackage/api/index.js";
	import SwitchTableCard from "@/firstPackage/components/SwitchTableCard.vue";
	import { getNowDateMin, getDaysFromCurrentTime, compareTime } from '@/common/dateTime.js';
	import PriceInformation from "@/firstPackage/components/stationDetails/PriceInformation.vue";
	import PileDetailsCard from "@/firstPackage/components/startDisAndcharging/PileDetailsCard.vue";
	import StartupParameter from "@/firstPackage/components/startDisAndcharging/StartupParameter.vue";
	import StartWaitingPopUp from "@/firstPackage/components/startDisAndcharging/StartWaitingPopUp.vue";
	import StartChargingButton from "@/firstPackage/components/startDisAndcharging/StartChargingButton.vue";

	export default {
		name: "startDisAndcharging",
		components: { PileDetailsCard, SwitchTableCard, StartupParameter, PriceInformation, StartChargingButton, StartWaitingPopUp },
		data() {
			return {
				strategy: 0,  //0:自动充满  1 ：soc电量 2：金额  3：电量 （只用底部文字切换使用）
				powerWay: 0,  //充电方式 0-立即充电 1-定时充电 2-自动充电
				gunCode: "",
				pileCode: "",
				pileData: {},
				userIsPay: 1,   //是否要付费  0：否  1：是
				startMode: 1,  // 启动方式  1：充 2：放
				tabsIndex: 1,
				priceInfo: {},
				noClick: true, // 防止重复点击
				occupyingPileCost: {},
				smPopupStartRef: false,
				gunDataList: [], // 当前桩 枪 的 列表
				tabsArray: [{ id: 1, name: "启动参数" }, { id: 2, name: "价格信息" }]
			}
		},
		onLoad(options) {
			// console.log(options);
			this.gunCode = options.gunCode;
			this.pileCode = options.pileCode;
			this.startMode = options.startMode || 1;
			uni.setNavigationBarTitle({
				title: `${this.startMode == 1 ? '启动充电' : '启动放电'}`
			});
		},
		onShow() {
			this.findDeviceInfoByPileCode();
			// 启动途中切入后台操作，如果为启动中的时候，需要重新连接
			if(this.smPopupStartRef){
				console.log("重新进入小程序，连接！！！！");
				this.$refs.StartWaitingPopUpRef.closeWebSocket();
			}
		},
		methods: {
			// 输入终端编号获取设备详情
			findDeviceInfoByPileCode() {
				queryDeviceInfoByPileCode({ pileCode: this.pileCode }).then(res => {
					let returnDataInfo = res.data ? res.data : {};
					this.userIsPay = returnDataInfo.isPay; //是否要付费
					this.pileData = JSON.parse(JSON.stringify(returnDataInfo));
					
					this.$nextTick(() => {
						// 只有 充电才会 获取放电钱包 余额
						// if(this.startMode == 1 && this.userIsPay){
						// 	this.$refs.startupParameterRef.findMemberBagBalance();
						// }
						this.$refs.pileDetailsCardRef.switchActiveGunData(this.gunCode);
					})
				})
			},
			// 桩超过两把枪才可以点击选择枪
			clickOpenGunPopup() {
				if(this.pileData.gunDataList && this.pileData.gunDataList.length > 1){
					this.$refs.smPopupRef.openPopupFun();
				}
			},
			// 成功选择枪
			selectGunCodeFun(data){
				this.$refs.pileDetailsCardRef.switchActiveGunData(data.gunCode);
				this.$refs.smPopupRef.closePopupFun();
			},
			changEvent(data){
				if(data.type === "SwitchTableCard"){
					this.tabsIndex = data.tabsIndex;
				}
				
				// 枪切换完成 同步更新枪编码
				if(data.type === "PileDetailsCard"){
					this.gunCode = data.gunCode;
				}
				
				// 打开等待弹框
				if(data.type === "openWaitPopup"){
					this.smPopupStartRef = true;
					this.$nextTick(()=>{
						this.$refs.smPopupStartRef.openPopupFun();
					})
				}
				
				if(data.type === "pileStartError"){
					uni.showToast({ icon:"none", title: "启动失败，请重新检查设备！" });
					this.$refs.smPopupStartRef.closePopupFun();
					this.smPopupStartRef = false;
				}
				
				if(data.type === "setStrategy"){
					this.strategy = data.strategy;
				}
			},
			startUpPileFun(){
				
				// const p2 = new Promise((resolve, reject)=>{
				// 	this.findDeviceInfoByPileCode();
				// 	this.$nextTick(()=>{
				// 		resolve(p1); //表示成功后返回的数据
				// 	})
				// });
				
				const p1 = new Promise((resolve, reject)=>{
					// 预付费用 策略等参数
					let startConfigParame = this.$refs.startupParameterRef.startConfigParame;
					// console.log(startConfigParame);
					
					// 获取到充放电启动参数
					if (!startConfigParame) {
						reject({ message: '启动参数获取失败!', code: 40002 });
						return;
					}
					
					this.powerWay = startConfigParame.powerWay;
					
					//根据启动方式进行判断（预约时间）
					if(startConfigParame.powerWay == 1){
						let currentTimer = getNowDateMin(); // 获取当前时间
						let timer = getDaysFromCurrentTime(startConfigParame.dateType - 1);
						let selectTimer = timer + ' ' + startConfigParame.clockingTime + ':00'; // 获取当前预约时间
						let timerStatus = compareTime(currentTimer, selectTimer, 2);
						if (!timerStatus) {
							reject({ message: '请重新选择预约时间，预约时间最快也要为1分钟之后!', code: 20001 });
						}
						
						startConfigParame.appointmentTime = selectTimer; // 预约时间
					}
					
					// 站点需 预支付金额
					if(this.userIsPay && this.startMode == 1){
						
						if (!money(startConfigParame.prepayMoney)) {
							reject({ message: '请输入预付金额!', code: 30005 });
							return;
						}
						
						if(startConfigParame.prepayMoney < 0.1){
							reject({ message: '预付金额不能低于5元!', code: 30006 });
							return;
						}
						
						if (startConfigParame.prepayMoney > 5000) {
							reject({ message: '金额过大，请重新设置!', code: 30007 });
							return;
						}
						
						// 支付方式走放电钱包
						if(startConfigParame.payWay == 4){
							if(startConfigParame.prepayMoney > startConfigParame.balance){
								reject({ message: '钱包余额不足!', code: 30008 });
								return;
							}
						}
						
						startConfigParame.prepayMoney = Math.abs(startConfigParame.prepayMoney);
					}
					
					
					// 不是自动充满
					if(startConfigParame.strategy > 0){
						if(!startConfigParame.strategyCfg){
							reject({ message: '请设置策略值!', code: 30008 });
							return;
						}
					}
					
					// 查看枪状态
					let activeGunData = this.$refs.pileDetailsCardRef.activeGunData;
					// console.log(activeGunData);
					
					// 判断枪状态是否可启动
					if(activeGunData.gunWorkState != 4){
						let alterTiltle = gunStatusToastTitle(activeGunData.gunWorkState);
						reject({ message: alterTiltle, code: 40008 });
					}
					
					//表示成功后返回的数据
					resolve({
						...startConfigParame, // 获取到充放电启动参数
						gunCode: this.gunCode,  // 枪编号
						pileCode: this.pileCode, // 桩编号
						startMode: this.startMode, // 启动方式  1：充 2：放
						userIsPay: this.userIsPay, //是否要付费  0：否  1：是
						siteRecordId: this.pileData.siteId, // 站点id
						dataRecordId: this.pileData.deviceId, //数据记录id
					});
				});
				
				p1.then(res=>{
					console.log("可以进行启动充电桩了!");
					this.$refs.startChargingButtonRef.twoEnterAlterFun(res);
				}).catch(err => {
					// console.log(err);
					if(err.code && err.code >= 40000)this.findDeviceInfoByPileCode();
					uni.showToast({ icon:"none",duration: 3000, title: err.message || err.msg });
				});
			},
		}
	}
</script>

<style scoped lang="scss">
	.container_content {
		padding: 24rpx 32rpx;
		box-sizing: border-box;

		.card_list {
			margin-bottom: 24rpx;
			border-radius: 12rpx;
			background-color: #FFFFFF;

			&:last-child {
				margin-bottom: 0;
			}
		}
	}
	
	.alter_text{
		color: #FFA126;
		font-size: 20rpx;
		padding: 8rpx 12rpx;
		box-sizing: border-box;
		background-color: #FDF1DB;
	}
	
	.gun_list_body{
		max-height: 480rpx;
		overflow-y: auto;
		padding: 24rpx 32rpx;
		box-sizing: border-box;
	}
</style>