<template>
	<view class="container">
		<view :class="['top_content',!activeVehicleId && vehicleStatus ? 'marginBottom' : '']">
			<view class="card_list" @click="goToPage('/secondPackage/vehicleManagement/vehicleBrand')">
				<view class="li_left">品牌车型</view>
				<view class="li_right">
					<view class="text" v-if="!bindCarData">当前请选择品牌车型</view>
					<view class="text" v-else>
						<text>{{ bindCarData.brandName | moreData }} </text>
					    <text style="margin-left: 6rpx;">{{ bindCarData.seriesName | emptyReplaceData }} </text>
					    <text style="margin-left: 6rpx;">{{ bindCarData.typeName | emptyReplaceData }}</text>
					</view>
					<view class="u-arrow u-arrow-right"></view>
				</view>
			</view>
			<view class="card_list">
				<view class="li_left">车牌号</view>
				<view class="li_right displayNoFlex">
					<car-number-input @numberInputResult="numberInputResult" :defaultStr="numberPlate"></car-number-input>
				</view>
			</view>
			
			<template v-if="vehicleStatus">
				<view class="card_list" >
					<view class="li_left">发动机号</view>
					<view class="li_right">{{ drivinglicenseInfo.motorNumber | moreData}}</view>
				</view>
				<view class="card_list" v-if="vehicleStatus">
					<view class="li_left">车架号码</view>
					<view class="li_right">{{ drivinglicenseInfo.vinCode | moreData}}</view>
				</view>
				<view class="card_list" v-if="vehicleStatus">
					<view class="li_left">注册日期</view>
					<view class="li_right">{{ drivinglicenseInfo.registerDate | moreData}}</view>
				</view>
			</template>
			
			
			<view class="card_list">
				<view class="li_left">续航里程</view>
				<view class="li_right">
					<uni-easyinput
						type="number"
						class="uni-input"
						v-model="totalKm"
						placeholder="请输入续航里程"
						:inputBorder="false"
						:clearable="false"
						:styles="inputStyle"
					>
						<template v-slot:right>
							<view class="unit">公里</view>
						</template>
					</uni-easyinput>
				</view>
			</view>
			<view class="card_list">
				<view class="li_left">电池容量</view>
				<view class="li_right">
					<uni-easyinput
						type="number"
						class="uni-input"
						v-model="batteryCap"
						placeholder="请输入电池容量"
						:inputBorder="false"
						:clearable="false"
						:styles="inputStyle"
					>
						<template v-slot:right>
							<view class="unit">千瓦时</view>
						</template>
					</uni-easyinput>
				</view>
			</view>
			<view class="card_list">
				<view class="li_left">常用车辆</view>
				<view class="li_right">
					<sm-switch :checked="vehicleType" @change="switchChange"></sm-switch>
				</view>
			</view>
		</view>
		
		<view class="drivinglicense" v-if="!activeVehicleId && vehicleStatus">
			<image :src="drivinglicenseInfo.licensePagePath" ></image>
		</view>
			
		<view class="bottom_content">
			<view class="button" @click="clickAddVehicle">{{ activeVehicleId ? '确认' : '添加'}}</view>
			<view class="button unbindBut" v-if="activeVehicleId" @click="unbindVehicle">解除绑定</view>
			<view class="bottom_agreement flex-center align-center">
				<sm-checkbox type="radio" lable="我已阅读并同意_" isClick :checked="agreeAgreement" @change="changeAgreement"></sm-checkbox>
				<text class="fontColor" @click="goToPage('/thirdPackage/login/userAgreement')">用户协议</text>
		
			</view>
		</view>
	</view>
</template>

<script>
	import { mapState } from 'vuex';
	import { memberCarBind,memberCarUnbind } from "@/secondPackage/api/index.js";
	import { isLicenseNo, isCheckVIN, isOrdinaryLicenseNo,num0to9999999 } from '@/common/validate.js';
	export default {
		name: "addVehicle",
		computed: {
			...mapState(['userInfo'])
		},
		data() {
			return {
				activeVehicleId: "", // 当前车辆id
				agreeAgreement: false,
				
				inputStyle: {
					height: '100rpx',
					color: '#242424',
					fontSize: '24rpx',
					textAlign: 'end',
					backgroundColor: 'transparent'
				},
				
				vehicleType: 0, // 车辆类型 0-不常用  1-常用
				bindCarData: null, //绑定车辆品牌车型数据
				
				totalKm: '', // 满电续航里程
				batteryCap: '', // 电池容量
				numberPlate: '' ,// 车牌号
				
				imageFile: [], //行驶证图片路径
				vehicleStatus: 0, // 车辆状态 0-不认证  1-认证
				drivinglicenseInfo: null, // 行驶证信息
			}
		},
		onLoad(options) {
			// console.log(options.data);
			if(options.data){
				let currentVehicleInfo = JSON.parse(decodeURIComponent(options.data));
				if(currentVehicleInfo && !currentVehicleInfo.islastChild){
					
					this.agreeAgreement = true;
					uni.setNavigationBarTitle({ title: '编辑车辆' });
					
					this.totalKm = currentVehicleInfo.totalKm;  // 满电续航里程
					this.activeVehicleId = currentVehicleInfo.id; // 当前车辆id
					this.batteryCap = currentVehicleInfo.batteryCap;  // 电池容量
					this.numberPlate = currentVehicleInfo.numberPlate; // 车牌号
					this.vehicleType = currentVehicleInfo.vehicleType; // 车辆类型
					this.vehicleStatus = currentVehicleInfo.vehicleStatus; // 车辆认证状态
					
					this.bindCarData = {
						brandId: currentVehicleInfo.brandId,
						brandName: currentVehicleInfo.brandName,
						seriesName: currentVehicleInfo.seriesName,
						seriesId: currentVehicleInfo.seriesId,
						typeName: currentVehicleInfo.typeName,
						typeId: currentVehicleInfo.typeId?currentVehicleInfo.typeId:""
					}
					
					if(this.vehicleStatus){
						this.drivinglicenseInfo = {
							vinCode: currentVehicleInfo.vinCode,//VIN码
							plate_num: currentVehicleInfo.numberPlate, // 车牌号
							motorNumber: currentVehicleInfo.motorNumber, //发动机号
							registerDate: currentVehicleInfo.registerDate, // 注册日期
							licensePagePath: currentVehicleInfo.licensePath, // 行驶证图片路径 (页面展示使用)
							licensePath: currentVehicleInfo.licensePath, // 行驶证图片路径
						}
					}
					
					// console.log(this.currentVehicleInfo)
					// console.log(this.bindCarData)
				}
			}
		},
		methods: {
			goToPage(pageName) {
				uni.navigateTo({ url: pageName });
			},
			numberInputResult(e) {
				// console.log('结果--' + e);
				this.numberPlate = e;
			},
			clickAddVehicle() {
				
				if(!this.agreeAgreement){
					uni.showToast({ icon: 'none', title: '请先阅读并同意用户协议' });
					return;
				}
				
				if (!this.bindCarData) {
					uni.showToast({ icon: 'none', title: '请选择品牌车型' });
					return;
				}
			
				if (!isLicenseNo(this.numberPlate) && !isOrdinaryLicenseNo(this.numberPlate)) {
					uni.showToast({ icon: 'none', title: '请输入正确的车牌号' });
					return;
				}
				
				if (!num0to9999999(this.totalKm)) {
					uni.showToast({ icon: 'none', title: '请输入正确的满电续航里程' });
					return;
				}
				
				if (!num0to9999999(this.totalKm)) {
					uni.showToast({ icon: 'none', title: '请输入正确的电池容量' });
					return;
				}
				
				if(!this.vehicleStatus){
					uni.showModal({
						title: '完成认证需要扫描行驶证上传',
						content: '完成认证享受即插即充、V2G等服务',
						cancelText: '先不认证',
						confirmText: '去认证',
						confirmColor: '#476AE2',
						success: res => {
							if (res.confirm) {
								console.log('用户点击确定');
								uni.navigateTo({
									url: '/secondPackage/vehicleManagement/drivinglicenseUpload'
								});
							} else if (res.cancel) {
								console.log('用户点击取消');
								this.enterMemberCarBind();
							}
						}
					});
				} else {
					this.enterMemberCarBind();
				}
			},
			enterMemberCarBind(){
				let data = {
					...this.bindCarData,
					id: this.activeVehicleId, //唯一id
					numberPlate: this.numberPlate, //车牌号
					totalKm: this.totalKm, //满电续航里程
					batteryCap: this.batteryCap, //电池容量
					vehicleStatus: this.vehicleStatus, //车辆状态 0-不认证 1-认证
					vehicleType: this.vehicleType, //车辆类型 0-不常用 1-常用
					memberCode: this.userInfo.userCode,
					createUserName: this.userInfo.userName,
					updateUserName: this.userInfo.userName,
				}
				
				// 认证车辆 字段信息
				if(this.vehicleStatus){
					
					if(this.drivinglicenseInfo.plate_num !== this.numberPlate){
						uni.showToast({ icon: 'none', title: '请确保认证车辆与输入车牌号一致！' });
						return;
					}
					
					data = { ...data,...this.drivinglicenseInfo };
				}
				
				console.log(data);
				// console.log(this.imageFile);
				memberCarBind(this.imageFile,data).then(res=>{
					uni.showToast({
						icon:'none',
						title: `${ this.activeVehicleId ? '车辆信息修改成功！' : '车辆添加成功！' }`,
						success: () => {
							uni.navigateBack({ delta: 1 });
						}
					})
				})
			},
			// 解绑车辆
			unbindVehicle(){
				uni.showModal({
					title: '即将解绑您的爱车',
					content: `${ this.bindCarData.brandName } ${ this.bindCarData.seriesName } ${ this.bindCarData.typeName }`,
					confirmText: '解绑',
					confirmColor: '#FD393A',
					success: res => {
						if (res.confirm) {
							memberCarUnbind({ carId: this.activeVehicleId }).then(res=>{
								uni.showToast({
									icon:'none',
									title: "车辆解绑成功！",
									success: () => {
										uni.navigateBack({ delta: 1 });
									}
								})
							})
						}
					}
				});
			},
			// 车辆类型 发生改变
			switchChange(data) {
				this.vehicleType = data ? 1 : 0;
			},
			changeAgreement(isChecked){
				this.agreeAgreement = isChecked;
			}
		}
	}
</script>

<style scoped lang="scss">
.container {
	box-sizing: border-box;
	background-color: #ffffff;
	padding: 28rpx 24rpx 50rpx 24rpx;

	.top_content {
		margin-bottom: 32rpx;

		.card_list {
			height: 100rpx;
			display: flex;
			align-items: center;
			padding: 0 38rpx;
			margin-bottom: 18rpx;
			border-radius: 16rpx;
			border: 2rpx solid rgba(108, 108, 108, 0.47);
			box-sizing: border-box;
			.li_left {
				font-size: 16px;
				font-weight: 600;
				color: #242424;
				margin-right: 10rpx;
			}
			.li_right {
				flex: 1;
				display: flex;
				align-items: center;
				justify-content: flex-end;

				font-size: 24rpx;
				font-weight: 300;
				color: #242424;

				.u-arrow {
					margin-left: 10rpx;
					color: #c7c7c7 !important;
				}
				.unit {
					color: #242424;
					width: 100rpx;
					text-align: end;
				}
			}

			.displayNoFlex {
				display: initial;
			}
		}
	}
	.marginBottom{
		margin-bottom: 32rpx !important;
	}
	
	.drivinglicense{
		width: 100%;
		margin-bottom: 36rpx;
		
		image{
			width: 100%;
			height: 420rpx;
		}
	}

	.bottom_content {
		width: 100%;

		.button {
			height: 98rpx;
			background: #476AE2;
			border-radius: 12rpx;
			font-size: 36rpx;
			font-weight: 600;
			color: #FFFFFF;
			text-align: center;
			line-height: 98rpx;
			margin-bottom: 34rpx;
		}
		
		.unbindBut{
			color: #242424;
			font-weight: 500;
			background: none;
			border: 2rpx solid #242424;
		}

		.bottom_agreement {
			font-size: 26rpx;
			color: #242424;

			.agreement_left {
				width: 28rpx;
				height: 28rpx;
				margin-right: 16rpx;
			}

			.fontColor {
				color: #1791ff;
				font-weight: bold;
			}
		}
	}
}
</style>
