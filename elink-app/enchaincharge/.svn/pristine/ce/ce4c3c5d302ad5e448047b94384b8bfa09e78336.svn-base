<template>
	<view
		class="slider-range"
		:class="{ disabled: disabled }"
		:style="{ paddingLeft: blockWidth / 2 + 'px', paddingRight: blockWidth / 2 + 'px', paddingTop: blockHeight / 2 + 'px', paddingBottom: blockHeight / 2 + 'px' }"
	>
		<view class="slider-range-inner" :style="{ height: height + 'px' }">
			<view
				class="slider-bar"
				:style="{
					height: barHeight + 'px'
				}"
			>
				<!-- 背景条 -->
				<view
					class="slider-bar-bg"
					:style="{
						backgroundColor: backgroundColor
					}"
				></view>

				<!-- 滑块实际区间 -->
				<view
					class="slider-bar-inner"
					:style="{
						width: sliderShowTwo ? ((values[1] - values[0]) / (max - min)) * 100 + '%' : (values[1] / (max - min)) * 100 + '%',
						left: lowerHandlePosition + '%',
						backgroundColor: activeColor
					}"
				></view>
				<!-- width: ((values[1] - values[0]) / (max - min)) * 100 + '%', -->
			</view>

			<!-- 滑动块-左 -->
			<view
				class="slider-handle-block"
				:class="{ decoration: decorationVisible }"
				:style="{
					backgroundColor: blockColor,
					width: blockWidth + 'px',
					height: blockHeight + 'px',
					left: lowerHandlePosition + '%'
				}"
				@touchstart="_onTouchStart"
				@touchmove="_onBlockTouchMove"
				@touchend="_onBlockTouchEnd"
				data-tag="lowerBlock"
				v-if="sliderShowTwo"
			></view>

			<!-- 滑动块-右 -->
			<view
				class="slider-handle-block"
				:class="{ decoration: decorationVisible }"
				:style="{
					backgroundColor: blockColor,
					width: blockWidth + 'px',
					height: blockHeight + 'px',
					left: higherHandlePosition + '%'
				}"
				@touchstart="_onTouchStart"
				@touchmove="_onBlockTouchMove"
				@touchend="_onBlockTouchEnd"
				data-tag="higherBlock"
			></view>

			<!-- 滑块值提示 -->
			<view v-if="tipVisible" class="range-tip" :style="lowerTipStyle">{{ format(values[0]) }}</view>
			<view v-if="tipVisible" class="range-tip" :style="higherTipStyle">{{ format(values[1]) }}</view>
		</view>
	</view>
</template>
<script>
export default {
	components: {},
	props: {
		//滑块区间当前取值
		value: {
			type: Array,
			default: function() {
				return [0, 100];
			}
		},

		//最小值
		min: {
			type: Number,
			default: 0
		},
		//最大值
		max: {
			type: Number,
			default: 100
		},
		step: {
			type: Number,
			default: 1
		},
		format: {
			type: Function,
			default: function(val) {
				return val;
			}
		},
		disabled: {
			type: Boolean,
			default: false
		},
		//滑块容器高度
		height: {
			height: Number,
			default: 20
		},
		//区间进度条高度
		barHeight: {
			type: Number,
			default: 5
		},
		//背景条颜色
		backgroundColor: {
			type: String,
			default: '#e9e9e9'
		},
		//已选择的颜色
		activeColor: {
			type: String,
			default: '#1aad19'
		},
		//滑块大小
		blockWidth: {
			type: Number,
			default: 19
		},
		blockHeight: {
			type: Number,
			default: 27
		},
		blockColor: {
			type: String,
			default: '#fff'
		},
		// 可拖动的最小值
		blockMin: {
			type: Number,
			default: 0
		},
		tipVisible: {
			type: Boolean,
			default: false
		},
		decorationVisible: {
			type: Boolean,
			default: true
		},
		//是否显示两个滑块
		sliderShowTwo: {
			type: Boolean,
			default: false
		}
	},
	data() {
		return {
			values: [this.min, this.max],
			startDragPos: 0, // 开始拖动时的坐标位置
			startVal: 0 //开始拖动时较小点的值
		};
	},
	computed: {
		// 较小点滑块的坐标
		lowerHandlePosition() {
			return ((this.values[0] - this.min) / (this.max - this.min)) * 100;
		},
		// 较大点滑块的坐标
		higherHandlePosition() {
			return ((this.values[1] - this.min) / (this.max - this.min)) * 100;
		},
		lowerTipStyle() {
			if (this.lowerHandlePosition < 90) {
				return `left: ${this.lowerHandlePosition}%;`;
			}
			return `right: ${100 - this.lowerHandlePosition}%;transform: translate(50%, -100%);`;
		},
		higherTipStyle() {
			if (this.higherHandlePosition < 90) {
				return `left: ${this.higherHandlePosition}%;`;
			}
			return `right: ${100 - this.higherHandlePosition}%;transform: translate(50%, -100%);`;
		}
	},
	watch: {
		//滑块当前值
		value: {
			immediate: true,
			handler(newVal, oldVal) {
				if (this._isValuesValid(newVal) && (newVal[0] !== this.values[0] || newVal[1] !== this.values[1])) {
					this._updateValue(newVal);
				}
			}
		}
	},
	methods: {
		_updateValue(newVal) {
			// 步长大于区间差，或者区间最大值和最小值相等情况
			if (this.step >= this.max - this.min) {
				throw new RangeError('Invalid slider step or slider range');
			}

			let newValues = [];
			if (Array.isArray(newVal)) {
				newValues = [newVal[0], newVal[1]];
			}
			if (typeof newValues[0] !== 'number') {
				newValues[0] = this.values[0];
			} else {
				newValues[0] = Math.round((newValues[0] - this.min) / this.step) * this.step + this.min;
			}
			if (typeof newValues[1] !== 'number') {
				newValues[1] = this.values[1];
			} else {
				newValues[1] = Math.round((newValues[1] - this.min) / this.step) * this.step + this.min;
			}

			// 新值与原值相等，不做处理
			if (this.values[0] === newValues[0] && this.values[1] === newValues[1]) {
				return;
			}

			//单个滑块达到最小值
			if (!this.sliderShowTwo) {
				if (newValues[1] <= this.blockMin) {
					newValues[1] = this.blockMin;
				}
			}

			// 左侧滑块值小于最小值时，设置为最小值
			if (newValues[0] < this.min) {
				newValues[0] = this.min;
			}

			// 右侧滑块值大于最大值时，设置为最大值
			if (newValues[1] > this.max) {
				newValues[1] = this.max;
			}
			// 两个滑块重叠或左右交错，使两个滑块保持最小步长的间距
			if (newValues[0] >= newValues[1]) {
				// 左侧未动，右侧滑块滑到左侧滑块之左
				if (newValues[0] === this.values[0]) {
					newValues[1] = newValues[0] + this.step;
				} else {
					// 右侧未动， 左侧滑块滑到右侧之右
					newValues[0] = newValues[1] - this.step;
				}
			}

			this.values = newValues;
			this.$emit('change', this.values);
		},
		_onTouchStart: function(event) {
			if (this.disabled) {
				return;
			}

			this.isDragging = true;
			let tag = event.target.dataset.tag;

			//兼容h5平台及某版本微信
			let e = event.changedTouches ? event.changedTouches[0] : event;
			this.startDragPos = e.pageX;

			this.startVal = tag === 'lowerBlock' ? this.values[0] : this.values[1];
		},
		_onBlockTouchMove: function(e) {
			if (this.disabled) {
				return;
			}
			this._onDrag(e);
		},
		_onBlockTouchEnd: function(e) {
			if (this.disabled) {
				return;
			}
			this.isDragging = false;
			this._onDrag(e);
		},
		_onDrag(event) {
			if (!this.isDragging) {
				return;
			}
			let view = uni
				.createSelectorQuery()
				.in(this)
				.select('.slider-range-inner');

			view.boundingClientRect(data => {
				let sliderWidth = data.width;
				const tag = event.target.dataset.tag;
				let e = event.changedTouches ? event.changedTouches[0] : event;
				let diff = ((e.pageX - this.startDragPos) / sliderWidth) * (this.max - this.min);
				let nextVal = this.startVal + diff;

				if (tag === 'lowerBlock') {
					this._updateValue([nextVal, null]);
				} else {
					this._updateValue([null, nextVal]);
				}
			}).exec();
		},
		_isValuesValid: function(values) {
			return Array.isArray(values) && values.length == 2;
		}
	}
};
</script>

<style scoped>
.slider-range {
	position: relative;
	/* padding-top: 40rpx; */
}

.slider-range-inner {
	position: relative;
	width: 100%;
}

.slider-range.disabled .slider-bar-inner {
	opacity: 0.35;
}

.slider-range.disabled .slider-handle-block {
	cursor: not-allowed;
}

.slider-bar {
	position: absolute;
	top: 50%;
	left: 0;
	right: 0;
	transform: translateY(-50%);
}

.slider-bar-bg {
	position: absolute;
	width: 100%;
	height: 100%;
	/* border-radius: 10000px; */
	z-index: 10;
}

.slider-bar-inner {
	position: absolute;
	width: 100%;
	height: 100%;
	/* border-radius: 10000px; */
	z-index: 11;
}

.slider-handle-block {
	position: absolute;
	top: 50%;
	transform: translate(-50%, -50%);
	border: 2rpx solid #d0d0d0;
	border-radius: 4rpx;
	box-shadow: 0px 2rpx 2rpx 0px rgba(0, 0, 0, 0.23);
	z-index: 12;
}

.slider-handle-block.decoration::before {
	position: absolute;
	content: '';
	width: 6rpx;
	height: 24rpx;
	top: 50%;
	left: 29%;
	transform: translateY(-50%);
	background: #eeedf2;
	border-radius: 3rpx;
	z-index: 13;
}

.slider-handle-block.decoration::after {
	position: absolute;
	content: '';
	width: 6rpx;
	height: 24rpx;
	top: 50%;
	right: 29%;
	transform: translateY(-50%);
	background: #eeedf2;
	border-radius: 3rpx;
	z-index: 13;
}

.range-tip {
	position: absolute;
	top: 0;
	font-size: 24rpx;
	color: #666;
	transform: translate(-50%, -100%);
}
</style>
