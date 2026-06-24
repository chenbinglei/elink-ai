<template>
	<view class="stationSearchFrom">	
		<view class="from_list">
			<view class="from_li" @click="clickSettingBut(1)">
				<text class="text">{{ selectFromData.kilometerText }}</text>
				<view :class="['triangle-down', kilometerVisible ? 'arrowTop' : 'arrowBottom']"></view>
			</view>
			<view class="from_li" @click="clickSettingBut(2)">
				<text class="text">{{ selectFromData.queryTypeText }}</text>
				<view :class="['triangle-down', distanceAndPrice ? 'arrowTop' : 'arrowBottom']"></view>
			</view>
			<view class="from_li" @click="clickSettingBut(3)">
				<text class="text">偏好</text>
				<view :class="['triangle-down', fromContentVisible ? 'arrowTop' : 'arrowBottom']"></view>
			</view>
		</view>
		
		<view class="from_content_body">
			<view class="from_content" v-if="distanceAndPrice || kilometerVisible">
				<sm-select-list :list="leftSelectArray" :active="selectedId" @changEvent="clickLeftSelect"></sm-select-list>
			</view>
			<view class="from_content" v-if="fromContentVisible">
				<view class="from_flex">
					<view class="title"><text class="text">充电方式</text></view>
					<view class="list">
						<template v-for="(item, index) in chargeModeArray">
							<view class="list_li_content" :key="index" @click="clickItemButton('chargeMode',item.id)">
								<view class="list_li" :class="{ active_li: oldPreferenceFromData.chargeMode == item.id }">
									<text>{{ item.name }}</text>
								</view>
							</view>
						</template>
					</view>
					<view class="title"><text class="text">停车费</text></view>
					<view class="list">
						<template v-for="(item, index) in parkCostArray">
							<view class="list_li_content" :key="index" @click="clickItemButton('parkCostType',item.id)">
								<view class="list_li"  :class="{ active_li: oldPreferenceFromData.parkCostType === item.id }">
									<text>{{ item.name }}</text>
								</view>
							</view>
						</template>
					</view>
					<view class="title"><text class="text">设备状态</text></view>
					<view class="list">
						<template v-for="(item, index) in isIdleArray">
							<view class="list_li_content" :key="index" @click="clickItemButton('isIdle',item.id)">
								<view class="list_li"  :class="{ active_li: oldPreferenceFromData.isIdle == item.id }" >
									<text>{{ item.name }}</text>
								</view>
							</view>
						</template>
					</view>
				</view>
				<view class="bottom_button">
					<view class="radio" @click="oldPreferenceFromData.savePreferences = !oldPreferenceFromData.savePreferences">
						<radio style="transform: scale(0.7);" :checked="oldPreferenceFromData.savePreferences" color="#476AE2" />
						<text class="text">保存偏好</text>
					</view>
					<view class="button resetBut" @click="clickReset">清空</view>
					<view class="button enterBut" @click="clickEnter">确定</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: "StationSearchFrom",
		data() {
			return {
				selectFromData: {
					queryType: 1,
					queryTypeText: "距离近",
					kilometerText: "10km",
					howMuchKilometer: 10, // 默认为10
				},
				
				preferenceFromData: {}, // 偏好设置
				oldPreferenceFromData:{}, // 保存旧的
				
				selectedId: "",
				operateType: "",
				leftSelectArray: [],
				
				kilometerVisible: false,
				distanceAndPrice: false,
				fromContentVisible: false,
				
				isIdleArray: [{ id: 1, name: '只看空闲' }],
				queryTypeArray: [{ id: 1, name: '距离近' }, { id: 2, name: '价格低' }],
				chargeModeArray: [{ id: 28, name: '慢充' }, { id: 29, name: '快充' }, { id: 30, name: 'V2G' }],
				parkCostArray: [{ id: 0, name: '免费停车' }, { id: 1, name: '停车收费' }, { id: 2, name: '限时免费' }, { id: 3, name: '充电限免' }],
				kilometerArray: [{ id: 3, name: '3km' }, { id: 5, name: '5km' }, { id: 10, name: '10km' }, { id: 20, name: '20km' }, { id: 50, name: '50km' }, { id: 100, name: '100km' }],
			}
		},
		methods: {
			clickSettingBut(operateType){
				this.operateType = operateType;
				if(operateType === 1){
					this.kilometerVisible = !this.kilometerVisible;
					if(this.kilometerVisible){
						this.distanceAndPrice =  false;
						this.fromContentVisible =  false;
						this.selectedId = this.selectFromData.howMuchKilometer;
						this.leftSelectArray = JSON.parse(JSON.stringify(this.kilometerArray));
					}
				}
				
				if(operateType === 2){
					this.distanceAndPrice = !this.distanceAndPrice;
					if(this.distanceAndPrice){
						this.kilometerVisible =  false;
						this.fromContentVisible =  false;
						this.selectedId = this.selectFromData.queryType;
						this.leftSelectArray = JSON.parse(JSON.stringify(this.queryTypeArray));
					}
				}
				
				if(operateType === 3){
					this.fromContentVisible = !this.fromContentVisible;
					if(this.fromContentVisible){
						this.distanceAndPrice =  false;
						this.kilometerVisible =  false;
						this.oldPreferenceFromData = JSON.parse(JSON.stringify(this.preferenceFromData));
					}
				}
			},
			clickLeftSelect(item){
				if(this.operateType === 1){
					this.selectFromData.kilometerText = item.name;
					this.selectFromData.howMuchKilometer = item.id;
					this.kilometerVisible = false;
				}
				
				if(this.operateType === 2){
					this.selectFromData.queryTypeText = item.name;
					this.selectFromData.queryType = item.id;
					this.distanceAndPrice = false;
				}
				
				this.$emit("headerFormEvent",{ type:"pagingReload" });
			},
			// 设备偏好里的查询条件
			clickItemButton(fieldName,id){
				// 两次点击相同
				if(this.oldPreferenceFromData[fieldName] === id){
					this.$set(this.oldPreferenceFromData,fieldName,"");
					return
				}
				this.$set(this.oldPreferenceFromData,fieldName,id);
			},
			clickReset(){
				this.oldPreferenceFromData = {
					savePreferences: this.preferenceFromData.savePreferences
				};
			},
			clickEnter(){
				this.preferenceFromData = JSON.parse(JSON.stringify(this.oldPreferenceFromData));
				if(this.preferenceFromData.savePreferences){
					uni.setStorageSync('STATION_FORM_DATA',this.preferenceFromData);
				} else{
					uni.removeStorageSync("STATION_FORM_DATA");
				}
				this.$emit("headerFormEvent",{ type:"pagingReload" });
				this.fromContentVisible = false;
			}
		}
	}
</script>

<style lang="scss" scoped>
	.stationSearchFrom {
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