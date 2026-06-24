<template>
	<view class="detailSearchFrom">

		<view class="from_list">
			<view class="from_li" @click="clickSettingBut(2)">
				<text class="text">时间</text>
				<view :class="['triangle-down', deteTimeVisible ? 'arrowTop' : 'arrowBottom']"></view>
			</view>
			<view class="from_li" @click="clickSettingBut(1)">
				<text class="text">筛选</text>
				<view :class="['triangle-down', screenStateVisible ? 'arrowTop' : 'arrowBottom']"></view>
			</view>

		</view>

		<view class="from_content_body">
			<view class="from_content" v-if="screenStateVisible">
				<view class="from_flex">
					<!-- <view class="title">
						<text class="text">交易金额</text>
					</view>
					<view class="uni-flex align-center displayFlex">
						<view class="uni-flex-item">
							<uni-easyinput type="digit" class="uni-input" v-model="oldSelectFromData.minAmount" placeholder="最低金额"
								:styles="inputStyle"></uni-easyinput>
						</view>
						<view class="text_center">~</view>
						<view class="uni-flex-item">
							<uni-easyinput type="digit" class="uni-input" v-model="oldSelectFromData.maxAmount" placeholder="最高金额"
								:styles="inputStyle"></uni-easyinput>
						</view>
					</view> -->
					<view class="title">
						<text class="text">收支类型</text>
					</view>
					<view class="list">
						<template v-for="(item, index) in transactionTypeArray">
							<view class="list_li_content" :key="index" @click="clickItemButton('tradeType', item.id)">
								<view class="list_li" :class="{ active_li: oldSelectFromData.tradeType == item.id }">
									<text>{{ item.name }}</text>
								</view>
							</view>
						</template>
					</view>
				</view>
				<view class="bottom_button">
					<view class="button resetBut" @click="clickReset">重置</view>
					<view class="button enterBut" @click="clickEnter">确定</view>
				</view>
			</view>
			<view class="from_content" v-if="deteTimeVisible">
				<view class="from_flex">
					<view class="list">
						<template v-for="(item, index) in dateTimeArray">
							<view class="list_li_content" :key="index" @click="clickItemButton('dateTime', item.id)">
								<view class="list_li" :class="{ active_li: item.id == selectType }">
									<text>{{ item.name }}</text>
								</view>
							</view>
						</template>
					</view>
					<view class="startAndEndDate" v-if="selectType == 6">
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

					<!-- <view class="title">
						<text class="text">排序</text>
					</view>
					<view class="list">
						<template v-for="(item, index) in sortTypeArray">
							<view class="list_li_content" :key="index" @click="clickItemButton('sortType',item.id)">
								<view class="list_li"  :class="{ active_li: oldSelectFromData.sortType == item.id }">
									<text>{{ item.name }}</text>
								</view>
							</view>
						</template>
					</view> -->
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
import { pickerDateToYear, compareTime, getDistanceNowDate, getOneYearRange } from "@/common/dateTime.js";

export default {
	name: "DetailSearchFrom",
	data () {
		return {
			operateType: 1,
			selectFromData: {
				// sortType: 1,
				tradeType:'',
				endDate: '',
				startDate:'',
			},
			operateTimeType: 1,
			oldSelectFromData: {},
			deteTimeVisible: false,
			screenStateVisible: false,
			// sortTypeArray: [{ id: 1, name: '正序' }, { id: 3, name: '倒序' }],
			transactionTypeArray: [{ id: '', name: '全部' }, { id: 1, name: 'V2G收入' }, { id: 2, name: '余额提现' }],
			dateTimeArray: [{ id: 1, name: '全部' }, { id: 2, name: '近一年' }, { id: 6, name: '自定义' }],
			selectType: "",

			inputStyle: {
				height: '80rpx',
				color: '#030303',
				backgroundColor: '#FFFFFF',
				borderColor: '#ECEAEA',
				borderRadius: '10rpx',
				fontSize: '32rpx'
			},
		}
	},
	methods: {
		clickSettingBut (operateType) {
			console.log(operateType, 'SHENM');
			this.operateType = operateType;
			if (operateType === 1) {
				this.screenStateVisible = !this.screenStateVisible;
				if (this.screenStateVisible) {
					this.deteTimeVisible = false;
					this.oldSelectFromData = JSON.parse(JSON.stringify(this.selectFromData));
				}
			}

			if (operateType === 2) {
				this.deteTimeVisible = !this.deteTimeVisible;
				if (this.deteTimeVisible) {
					this.screenStateVisible = false;
					this.oldSelectFromData = JSON.parse(JSON.stringify(this.selectFromData));
				}
			}
		},
		clickItemButton (fieldName, id) {
			console.log('设置为空', id, this.oldSelectFromData)

			if (fieldName === "dateTime") {
				this.selectType = id
				if (id === 1) {
					this.oldSelectFromData.endDate = '';
					this.oldSelectFromData.startDate = '';
				} else {
					let dateTime = id == 2 ? getOneYearRange() : pickerDateToYear(id * 30, true);
					this.$set(this.oldSelectFromData, 'endDate', dateTime[1]);
					this.$set(this.oldSelectFromData, 'startDate', dateTime[0]);
				}
			}

			if (fieldName !== "dateTime") {
				this.$set(this.oldSelectFromData, fieldName, id);
			}

		},
		// 打开选择时间弹框
		clickSelectTime (operateTimeType) {
			let timer = operateTimeType === 1 ? this.oldSelectFromData.startDate : this.oldSelectFromData.endDate;
			this.$refs.timeSelectorRef.openSelect(timer);
			this.operateTimeType = operateTimeType;
		},
		btnConfirm (timer) {
			// console.log(timer);
			let endDate = this.oldSelectFromData.endDate;
			let startDate = this.oldSelectFromData.startDate;
			if (this.operateTimeType === 1) startDate = timer;
			if (this.operateTimeType === 2) endDate = timer;

			let objTime = compareTime(startDate, endDate);
			this.$set(this.oldSelectFromData, 'endDate', objTime.endTime);
			this.$set(this.oldSelectFromData, 'startDate', objTime.startTime);
		},
		clickReset () {
			this.oldSelectFromData = JSON.parse(JSON.stringify(this.selectFromData));
		},
		clickEnter () {
			console.log(this.oldSelectFromData, 'this.oldSelectFromData');
			this.deteTimeVisible = false;
			this.screenStateVisible = false;
			this.selectFromData = JSON.parse(JSON.stringify(this.oldSelectFromData));
			this.$emit("headerFormEvent", { type: "pagingReload" });
		},
	}
}
</script>

<style scoped lang="scss">
.detailSearchFrom {
	position: relative;
	background-color: #ffffff;

	.from_list {
		display: flex;
		align-items: center;

		.from_li {
			flex: 1;
			height: 68rpx;
			display: flex;
			align-items: center;
			justify-content: center;

			.text {
				color: #242424;
				font-size: 24rpx;
				margin-right: 12rpx;
			}

			.triangle-down {
				transition: all 0.28s;
			}

			.arrowTop {
				transform: rotate(180deg) translateX(-5px);
			}
		}
	}

	.from_content_body {
		width: 100%;
		z-index: 10001;
		position: absolute;
		left: 0;
		top: 68rpx;

		.from_content {
			width: 100%;
			transition: all 0.28s;
			background: #ffffff;
			box-sizing: border-box;
			padding: 20rpx 44rpx 44rpx 44rpx;
			box-shadow: 0px 2rpx 12rpx 0px rgba(0, 0, 0, 0.1);

			.from_flex {

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

				.displayFlex {
					margin-bottom: 12rpx;

					.text_center {
						padding: 0 24rpx;
					}
				}

				.list {
					display: flex;
					flex-wrap: wrap;

					.list_li_content {
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
							background: rgba(236, 236, 236, 1);
							// border: 2rpx solid #107be9;
							box-sizing: border-box;
						}

						.active_li {
							background: rgba(30, 144, 255, 0.3);
							color: rgba(30, 144, 255, 1);
						}
					}
				}
			}

			.startAndEndDate {
				display: flex;
				align-items: center;
				margin: 32rpx 0 24rpx 0;

				.dateTime {
					flex: 1;
					height: 72rpx;
					display: flex;
					align-items: center;
					justify-content: center;
					border-radius: 8rpx;
					border: 2rpx solid #107be9;

					.null_time {
						font-size: 28rpx;
						color: rgba(0, 0, 0, 0.1);
					}
				}

				.text {
					margin: 0 50rpx;
				}

				.null_bg_color {
					border: none;
					background: rgba(31, 116, 226, 0.1);

					.timer {
						font-size: 28rpx;
						color: rgba(0, 0, 0, 0.6);
					}
				}
			}

			.bottom_button {
				display: flex;
				align-items: center;
				justify-content: space-between;
				margin-top: 44rpx;

				.resetBut {
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