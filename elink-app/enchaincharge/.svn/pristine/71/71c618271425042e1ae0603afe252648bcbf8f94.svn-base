<template>
	<view class="chargingAndDisAnimation" :class="startMode == 2 ? 'disChargeAnimation' : 'chargeAnimation'">
		<view class="card_pile_class">
			<template v-if="workState && workState <= 2">
				<image v-if="startMode == 1" :src="getStaticFilePath('cardAndPileCharge.gif')"></image>
				<image v-if="startMode == 2" :src="getStaticFilePath('cardAndPileDisCharge.gif')"></image>
			</template>
			<image v-else :src="getStaticFilePath('cardAndPile.png')"></image>
		</view>
		<view class="battery_class" :class="{ low_electric_class: curSocProgress <= 2,tall_electric_class: curSocProgress >= 9 }">
			<template v-for="item in latticeNumber">
				<view :key="item" class="battery_li">
					<template v-if="startMode == 2">
						<template v-if="item >= curSocProgress && item >= (curSocProgress - variableProgress + curSocProgress) ">
							<view class="electric_class" :class="{ activeClass: item == variableProgress }"></view>
						</template>
					</template>
					<template v-else>
						<template v-if="item >= (latticeNumber - curSocProgress) || item >= (latticeNumber - variableProgress)">
							<view class="electric_class" :class="{ activeClass: item == (latticeNumber - variableProgress) }"></view>
						</template>
					</template>
				</view>
			</template>
		</view>
		
		<view class="pile_text_class uni-flex-item flex-center" :class="'work_state_class' + workState">
			<view class="status_text">{{ workState | gunWorkState(workState) }}</view>
			<text class="iconfont icon-shandian"></text>
		</view>
	</view>
</template>

<script>
	export default {
		name: "ChargingAndDisAnimation",
		props: {
			startSoc: {
				type: [Number, String],
				default: 0
			},
			batterySOC: {
				type: [Number, String],
				default: 0
			},
			startMode: {
				type: [Number, String],
				default: 1
			},
			workState: {
				type: [Number, String],
				default: 1
			},
		},
		data() {
			return {
				timer: null,
				latticeNumber: 10,
				curSocProgress: 0,
				variableProgress: 0
			}
		},
		watch: {
			batterySOC: {
				deep: true,
				immediate: true,
				handler(val, old) {
					this.setchargeAnimation();
				}
			},
			workState: {
				deep: true,
				immediate: true,
				handler(val, old) {
					if(this.workState <= 2){
						this.initAnimation();
					} else {
						clearInterval(this.timer);
					}
				}
			},
		},
		methods: {
			initAnimation() {
				// 初始化充电动画 动作
				this.timer = setInterval(() => {
					if(this.workState == 1){
						this.variableProgress += 1;
						if (this.variableProgress > this.latticeNumber) {
							this.variableProgress = this.curSocProgress;
						}
					}
					
					if(this.workState == 2){
						this.variableProgress -= 1;
						if (this.variableProgress <= 0) {
							this.variableProgress = this.curSocProgress;
						}
					}
				}, 500);
			},
			// 计算 充电动画开始位置
			setchargeAnimation() {
				let curSocProgress = 0;
				if (this.batterySOC) {
					curSocProgress = Math.trunc(this.batterySOC / this.latticeNumber);
				} else {
					curSocProgress = Math.trunc(this.startSoc / this.latticeNumber);
				}
				this.curSocProgress = curSocProgress;
				this.variableProgress = this.curSocProgress;
			}
		},
		beforeDestroy() {
			clearInterval(this.timer);
		}
	}
</script>

<style scoped lang="scss">
	.chargingAndDisAnimation {
		width: 100%;
		position: relative;
		box-sizing: border-box;
		padding: 48rpx 32rpx 32rpx 32rpx;
		
		.card_pile_class{
			width: 100%;
			
			image{
				width: 100%;
				height: 240rpx;
			}
		}
		
		.battery_class{
			width: 108rpx;
			height: 26rpx;
			display: flex;
			border-radius: 4rpx;
			box-sizing: border-box;
			background-color: #8DCBBC;
			border: 2rpx solid #199D7C;
			position: absolute;
			left: 192rpx;
			bottom: 104rpx;
			transform-style: preserve-3d;
			transform: rotateZ(-18deg) skew(15deg, 0deg);
			
			.battery_li{
				flex: 1;
				height: 100%;
				padding: 2rpx 1rpx;
				box-sizing: border-box;
				
				.electric_class{
					width: 100%;
					height: 100%;
					border-radius: 2rpx;
					background-color: #199D7C;
				}
			}
		}
		
		// 高电量展示的颜色
		.tall_electric_class{
			border: 2rpx solid #56E540;
			background-color: rgba(86, 229, 64, .5);
			
			.battery_li{
				.electric_class{
					background-color: #56E540;
				}
			}
		}
		
		// 低电量展示
		.low_electric_class{
			border: 2rpx solid #FD393A;
			background-color: rgba(253, 57, 58, .5);
			
			.battery_li{
				.electric_class{
					background-color: #FF7D1E;
				}
			}
		}
		
		
		.pile_text_class{
			width: 20rpx;
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;
			transform-style: preserve-3d;
			transform: rotateZ(4deg) skew(0deg, 0deg);
			position: absolute;
			right: 100rpx;
			top: 75rpx;
			
			.status_text{
				font-size: 18rpx;
				margin-bottom: 2rpx;
			}
			
			.iconfont{
				font-size: 24rpx;
			}
		}
		.work_state_class1{
			color: #199D7C;
		}
		.work_state_class2{
			color: #EC8D4A;
		}
		.work_state_class5{
			color: #F3615B;
		}
		.work_state_class6{
			color: #A1A1A1;
		}
		.work_state_class7{
			color: #979797;
		}
		.work_state_class8{
			color: #979797;
		}
		.work_state_class9{
			color: #BF6627;
		}
	}
</style>