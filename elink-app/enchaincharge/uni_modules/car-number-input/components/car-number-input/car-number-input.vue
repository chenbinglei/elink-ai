<template>
	<view class="car-number-input">
		<view class="car-input-container">
			<view class="car-input-box" v-for="(item, index) in inputList" :key="index" @click="plateInput(index)">
				<view class="car-input-item" :class="[curInput == index ? 'sel-item' : '', maxNum - 1 == index ? 'last-item' : '', item ? 'completed' : 'notFilledIn']">
					<view :class="curInput == index ? 'sel-item-line' : ''"></view>

					<view class="new-item-img" v-if="maxNum - 1 == index">
						<view class="text">新</view>
						<view class="text">能</view>
						<view class="text">源</view>
					</view>

					<text class="text">{{ item }}</text>
				</view>
			</view>
		</view>

		<view class="car-number-container" v-show="showKeyPop1">
			<view class="plate-close" @click="closeKeyboard"><text class="plate-close-btn">关闭</text></view>
			<view class="plate-popup-list">
				<view class="plate-popup-item province-item" v-for="(item, index) in keyProvince1" :key="index" @click="tapKeyboard(item)">{{ item }}</view>
			</view>
			<view class="plate-popup-list">
				<view class="plate-popup-item province-item" v-for="(item, index) in keyProvince2" :key="index" @click="tapKeyboard(item)">{{ item }}</view>
			</view>
			<view class="plate-popup-list">
				<view class="plate-popup-item province-item" v-for="(item, index) in keyProvince3" :key="index" @click="tapKeyboard(item)">{{ item }}</view>
			</view>
			<view class="plate-popup-list">
				<view class="plate-popup-item province-item" v-for="(item, index) in keyProvince4" :key="index" @click="tapKeyboard(item)">{{ item }}</view>
				<!-- 删除 -->
				<view class="plate-popup-item province-item del" @click="onPlateDelTap"><image :src="deleteImgBase64" /></view>
			</view>
		</view>

		<view class="car-number-container" v-show="showKeyPop2">
			<view class="plate-close" @click="closeKeyboard"><text class="plate-close-btn">关闭</text></view>
			<view class="plate-popup-list">
				<view class="plate-popup-item" :class="lockInput.includes(item) ? 'lock-item' : ''" v-for="(item, index) in keyEnInput1" :key="index" @click="tapKeyboard(item)">
					{{ item }}
				</view>
			</view>
			<view class="plate-popup-list">
				<view class="plate-popup-item" :class="lockInput.includes(item) ? 'lock-item' : ''" v-for="(item, index) in keyEnInput2" :key="index" @click="tapKeyboard(item)">
					{{ item }}
				</view>
			</view>
			<view class="plate-popup-list">
				<view class="plate-popup-item" :class="lockInput.includes(item) ? 'lock-item' : ''" v-for="(item, index) in keyEnInput3" :key="index" @click="tapKeyboard(item)">
					{{ item }}
				</view>
			</view>
			<view class="plate-popup-list">
				<view class="plate-popup-item" :class="lockInput.includes(item) ? 'lock-item' : ''" v-for="(item, index) in keyEnInput4" :key="index" @click="tapKeyboard(item)">
					{{ item }}
				</view>
				<!-- 删除 -->
				<view class="plate-popup-item del" @click="onPlateDelTap"><image :src="deleteImgBase64" /></view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	name: 'car-number-input',
	emits: ['numberInputResult'],
	props: {
		defaultStr: {
			type: String,
			default: ''
		},
		plateNum: {
			type: String,
			default: ''
		}
		// maxNum: {
		// 	type: Number,
		// 	default: 8
		// },
	},
	data() {
		return {
			inputList: ['', '', '', '', '', '', '', ''],
			curInput: -1,
			maxNum: 8,
			showKeyPop1: false,
			showKeyPop2: false,
			keyProvince1: ['京', '津', '晋', '冀', '蒙', '辽', '吉', '黑', '沪'],
			keyProvince2: ['苏', '浙', '皖', '闽', '赣', '鲁', '豫', '鄂', '湘'],
			keyProvince3: ['粤', '桂', '琼', '渝', '川', '贵', '云', '藏'],
			keyProvince4: ['陕', '甘', '青', '宁', '新', 'W'],
			keyEnInput1: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '0'],
			keyEnInput2: ['Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'P', '学', '军'],
			keyEnInput3: ['A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', '警'],
			keyEnInput4: ['Z', 'X', 'C', 'V', 'B', 'N', 'M', '港', '澳'],
			lockInput: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '0'],
			deleteImgBase64:
				'data:image/png;base64,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'
		};
	},
	watch: {
		defaultStr(val) {
			if (val != '' && val != null) {
				const valList = val.split('');
				for (let i in valList) {
					// this.inputList[i] = valList[i];
					this.$set(this.inputList,i,valList[i])
				}
				// this.$forceUpdate();
			}
		},
		curInput(val) {
			this.showOrHidePop(val);

			this.keyEnInput2 = ['Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'O', 'P', '军'];
			switch (val) {
				case 1:
					this.lockInput = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '学', '军', '警', '港', '澳'];
					break;
				case 2:
					this.lockInput = ['O', '学', '军', '警', '港', '澳'];
					break;
				case 3:
					this.lockInput = ['O', '学', '军', '警', '港', '澳'];
					break;
				case 4:
					this.lockInput = ['O', '学', '军', '警', '港', '澳'];
					break;
				case 5:
					this.lockInput = ['O', '学', '军', '警', '港', '澳'];
					break;
				case 6:
					this.lockInput = ['O'];
					this.keyEnInput2 = ['Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'P', '学', '军'];
					break;
				case 7:
					this.lockInput = ['O', '学', '军', '警', '港', '澳'];
					break;
				default:
					this.lockInput = [];
					break;
			}
		}
	},
	created() {
		if (this.defaultStr != '' && this.defaultStr != null) {
			const valList = this.defaultStr.split('');
			for (let i in valList) {
				this.inputList[i] = valList[i];
			}
		}
	},
	methods: {
		plateInput(e) {
			this.curInput = e;
			this.showOrHidePop(e);
		},
		showOrHidePop(val) {
			if (val == -1) {
				this.showKeyPop1 = false;
				this.showKeyPop2 = false;
			} else if (val == 0) {
				this.showKeyPop1 = true;
				this.showKeyPop2 = false;
			} else {
				this.showKeyPop1 = false;
				this.showKeyPop2 = true;
			}
		},
		tapKeyboard(e) {
			if (this.lockInput.includes(e)) {
				return;
			}

			this.inputList[this.curInput] = e;
			if (this.curInput < this.maxNum - 2) {
				this.curInput++;
			} else {
				this.curInput = -1;
			}

			this.emitResult();
		},
		closeKeyboard() {
			this.curInput = -1;
		},
		onPlateDelTap() {
			this.inputList[this.curInput] = '';
			this.curInput--;
			this.emitResult();
		},
		emitResult() {
			const returnResult = this.inputList.join('');
			this.$emit('numberInputResult', returnResult);
		}
	}
};
</script>

<style scoped lang="scss">
.car-number-input {
	width: 100%;

	.car-input-container {
		width: 100%;
		height: 44px;
		display: flex;
		align-items: center;

		.car-input-box {
			flex: 1;
			display: inline-block;
			vertical-align: middle;

			.car-input-item {
				position: relative;
				height: 80rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				background: #E2E2E2;
				border-radius: 12rpx;
				margin-left: 10%;
				box-sizing: border-box;

				.sel-item-line {
					width: 70%;
					height: 4rpx;
					position: absolute;
					bottom: 6rpx;
					background-color: #476AE2;
				}
			}

			.completed {
				font-size: 36rpx;
				font-weight: 600;
				color: #28292a;
				background: #f5f5f5 !important;

				.new-item-img {
					display: none !important;
				}
			}

			.sel-item {
				color: #476AE2;
			}

			.last-item {
				position: relative;
				// background: rgba(245, 245, 245, 0.65);
				background: #E2E2E2;
				border: 2rpx dashed #ededed;

				.new-item-img {
					font-size: 16rpx;
					color: #000000;
					background: #E2E2E2;
					display: flex;
					flex-direction: column;
					align-items: center;
					justify-content: center;
				}
			}
		}
	}
	.car-number-container {
		position: fixed;
		z-index: 999;
		bottom: 0;
		left: 0;
		width: 100%;
		height: 254px;
		background-color: #e3e2e7;
		-webkit-box-shadow: 0 0 30upx rgba(0, 0, 0, 0.1);
		box-shadow: 0 0 30upx rgba(0, 0, 0, 0.1);
		overflow: hidden;
		text-align: center;
		.plate-close {
			height: 40px;
			line-height: 40px;
			text-align: right;
			background-color: #fff;
			.plate-close-btn {
				font-size: 13.5px;
				color: #555;
				margin-right: 15px;
			}
		}
		//键盘主体内容-单行
		.plate-popup-list {
			margin: 0 auto;
			overflow: hidden;
			display: inline-block;
			display: table;

			&:last-child {
				margin-bottom: 2px;
			}
		}
		//键盘主体内容-单个
		.plate-popup-item {
			float: left;
			font-size: 16px;
			width: 8vw;
			margin: 0 1vw;
			margin-top: 8px;
			height: 40px;
			line-height: 40px;
			background: #ffffff;
			border-radius: 5px;
			color: #4a4a4a;
			image {
				width: 16px;
				height: 16px;
				margin: 12px auto;
			}
		}
		.plate-popup-item:active {
			background-color: #eaeaea;
		}
		.province-item {
			width: 8.8vw;
		}
		.lock-item {
			color: #aaa;
		}
	}
}
</style>
