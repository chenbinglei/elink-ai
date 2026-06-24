<template>
	<!-- 日期时间选择器 -->
	<view class="timeSelector">
		<uni-popup ref="timePopup" type="bottom">
			<view class="popup-box">
				<view class="header">
					<view class="cancel" @click="cancelSelect">取消</view>
					<view class="enter" @click="btnConfirm">确定</view>
				</view>
				<view class="time">
					<picker-view class="pickerView" :value="pickerArray" indicator-class="picker-box" :indicator-style="indicatorStyle" @change="bindTimeChange">
						<picker-view-column v-if="showType == 'date' || showType == 'yearAndMonth'">
							<view class="item" :class="index == pickerArray[0] ? 'activeStyle' : ''" v-for="(item, index) in yearArray" :key="index">{{ item }}</view>
						</picker-view-column>
						<picker-view-column v-if="showType == 'date' || showType == 'yearAndMonth'">
							<view class="item" :class="index == pickerArray[1] ? 'activeStyle' : ''" v-for="(item, index) in monthArray" :key="index">{{ item }}</view>
						</picker-view-column>
						<picker-view-column v-if="showType == 'date'">
							<view class="item" :class="index == pickerArray[2] ? 'activeStyle' : ''" v-for="(item, index) in dateArray" :key="index">{{ item }}</view>
						</picker-view-column>

						<picker-view-column v-if="showType == 'hourToMinute'">
							<view class="item" :class="item == pickerArray[0] ? 'activeStyle' : ''" v-for="(item, index) in hourArray" :key="index">{{ item }}</view>
						</picker-view-column>
						<picker-view-column v-if="showType == 'hourToMinute'">
							<view class="item" :class="item == pickerArray[1] ? 'activeStyle' : ''" v-for="(item, index) in minuteArray" :key="index">{{ item }}</view>
						</picker-view-column>
					</picker-view>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
import { dateTimePicker, getMonthDay, generateTimeStr, getNewDateArry } from '@/common/dateTimePicker.js';

export default {
	name: 'TimeSelector',
	props: {
		//时间选择器的显示模式，默认可以不填  date: 年月日  yearAndMonth : 年月   hourToMinute: 时分
		showType: {
			type: String,
			default: 'date'
		},
		//年的开始时间
		startYear: {
			type: [Number, String],
			default: 1921
		},
		// 确定是否关闭弹框
		confirmClose: {
			type: Boolean,
			default: true
		},
		// 年月类型 是否拼接 日
		isSplicingDay: {
			type: Boolean,
			default: true
		},
	},
	data() {
		return {
			pickerArray: [], // 当前选择框里的 时间

			yearArray: [],
			monthArray: [],
			dateArray: [],
			hourArray: [],
			minuteArray: [],
			indicatorStyle: `height: 40px;`,

			oldMonthArray: [],
		};
	},
	mounted() {
		this.createArray();
	},
	methods: {
		//确定选择
		btnConfirm() {
			let timer = null;

			if (this.showType == 'date') {
				let year = this.yearArray[this.pickerArray[0]];
				let mont = this.monthArray[this.pickerArray[1]];
				let date = this.dateArray[this.pickerArray[2]];
				timer = `${year}-${mont}-${date}`;
			}

			if (this.showType == 'yearAndMonth') {
				// console.log(this.pickerArray);
				let year = this.yearArray[this.pickerArray[0]];
				let mont = this.monthArray[this.pickerArray[1]];
				timer = `${year}-${mont}`;
				
				if(this.isSplicingDay){
					timer = timer + "-01";
				}
			}

			if (this.showType == 'hourToMinute') {
				for (let i = 0; i < this.pickerArray.length; i++) {
					if (this.pickerArray[i] < 10) this.pickerArray[i] = '0' + this.pickerArray[i];
				}

				timer = this.pickerArray.join(':');
			}

			this.$emit('btnConfirm', timer);

			if (this.confirmClose) this.$refs.timePopup.close();
		},
		openSelect(time = null) {
			console.log(time,'*****');
			if (time) {
				if (this.showType == 'date') {
					let yearAndMonth = time.split('-');
					this.pickerArray = [
						this.returnArrayIndex(this.yearArray, yearAndMonth[0]),
						this.returnArrayIndex(this.monthArray, yearAndMonth[1]),
						this.returnArrayIndex(this.dateArray, yearAndMonth[2])
					];
					this.setMonthArray();
				}

				if (this.showType == 'yearAndMonth') {
					let yearAndMonth = time.split('-');
					this.pickerArray = [this.returnArrayIndex(this.yearArray, yearAndMonth[0]), this.returnArrayIndex(this.monthArray, yearAndMonth[1])];
					this.setMonthArray();
				}

				if (this.showType == 'hourToMinute') {
					this.pickerArray = time.split(':');
				}
			} else {
				let getNewDate = getNewDateArry();
				if (this.showType == 'date') {
					this.pickerArray = [
						this.returnArrayIndex(this.yearArray, getNewDate[0]),
						this.returnArrayIndex(this.monthArray, getNewDate[1]),
						this.returnArrayIndex(this.dateArray, getNewDate[2])
					];
					this.setMonthArray();
				}

				if (this.showType == 'yearAndMonth') {
					this.pickerArray = [this.returnArrayIndex(this.yearArray, getNewDate[0]), this.returnArrayIndex(this.monthArray, getNewDate[1])];
					this.setMonthArray();
				}

				if (this.showType == 'hourToMinute') {
					this.pickerArray = [getNewDate[3], getNewDate[4]];
				}
			}

			this.$refs.timePopup.open();
		},
		//取消选择
		cancelSelect() {
			this.$refs.timePopup.close();
			this.$emit('btnCancel');
		},
		// 选择时间 触发
		bindTimeChange(e) {
			this.pickerArray = e.detail.value;
			this.setMonthArray();
		},
		// 年,月切换时重新更新计算
		setMonthArray() {
			// this.pickerArray
			let newDate = new Date();
			let year = newDate.getFullYear(),
				mont = newDate.getMonth() + 1,
				date = newDate.getDate();
				
			if (this.showType == 'date' || this.showType == 'yearAndMonth') {
				if (this.yearArray[this.pickerArray[0]] == year) {
					let monthArray = [];

					//重新设置月与 日的数据，不可大于当天
					for (let i = 0; i < this.oldMonthArray.length; i++) {
						if (this.oldMonthArray[i] <= mont) monthArray.push(this.oldMonthArray[i]);
					}
					this.monthArray = monthArray;
				} else {
					this.monthArray = this.oldMonthArray;
				}
				
				// 重新设置日期
				if (this.showType == 'date') {
					let slyear = this.yearArray[this.pickerArray[0]];
					let slmont = this.monthArray[this.pickerArray[1]];
					let dateArray = getMonthDay(slyear, slmont); // 当前选择的年月
					if (this.monthArray[this.pickerArray[1]] == mont) {
						let dateIndex = dateArray.findIndex(item=> item*1 == date) + 1;
						dateArray = dateArray.slice(0,dateIndex);
					}
					
					this.dateArray = dateArray;
				}
			}
		},
		// 生成时分  ，首次进来选择当前时间
		createArray() {
			let date = new Date();
			let endYear = date.getFullYear();
			// 获取完整的年月日 时分秒，以及默认显示的数组
			let obj = dateTimePicker(this.startYear, endYear);
			// 精确到分的处理，将数组的秒去掉
			// let lastArray = obj.dateTimeArray.pop();
			// let lastTime = obj.dateTime.pop();
			// console.log(obj)
			this.dateTimeArray = obj.dateTimeArray;
			this.yearArray = obj.dateTimeArray[0];
			this.monthArray = obj.dateTimeArray[1];
			this.oldMonthArray = obj.dateTimeArray[1];
			this.dateArray = obj.dateTimeArray[2];
			this.pickerArray = JSON.parse(JSON.stringify(obj.dateTime));

			// 生成小时数组数据
			for (let i = 0; i < 60; i++) {
				let num = i < 10 ? '0' + i : i;
				this.minuteArray.push(num);
				if (i < 24) {
					this.hourArray.push(num);
				}
			}
		},
		//查看数组中是否存在某值，并返回对应的下标
		returnArrayIndex(arrAy, item) {
			let index = 0;
			for (let i = 0; i < arrAy.length; i++) {
				if (arrAy[i] == item) {
					index = i;
				}
			}
			return index;
		}
	}
};
</script>

<style scoped lang="scss">
.timeSelector {
	.popup-box {
		width: 100%;
		min-height: 300upx;
		padding: 32upx;
		box-sizing: border-box;
		background: #ffffff;
		border-radius: 24rpx 40rpx 0px 0px;

		.header {
			width: 100%;
			margin-bottom: 72rpx;
			display: flex;
			justify-content: space-between;

			view {
				font-size: 28rpx;
				color: #476ae2;
			}

			.cancel {
				color: rgba(0, 0, 0, 0.2);
			}
		}
		.time {
			width: 100%;
			height: 424rpx;
			.pickerView {
				height: 100%;

				.item {
					text-align: center;
					line-height: 40px;
					font-size: 32rpx;
				}
				.activeStyle {
					color: #476ae2;
				}
			}
		}
	}
}
</style>
