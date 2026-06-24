<template>
	<!-- #ifdef H5 -->
	<transition name="fade">
		<!-- #endif -->
		<view class="mask" v-show="isActive" :class="{ 'full-screen': isFullScreen }" :style="{ backgroundColor }">
			<view class="spinner">
				<slot>
					<loop v-if="componentName === 'loop'" :color="color" :size="size" />
					<bounce v-if="componentName === 'bounce'" :color="color" :size="size" />
					<scaleOut v-if="componentName === 'scaleOut'" :color="color" :size="size" />
					<doubleDot v-if="componentName === 'doubleDot'" :color="color" :size="size" />
					<doubleCube v-if="componentName === 'doubleCube'" :color="color" :size="size" />
					<shrinkRect v-if="componentName === 'shrinkRect'" :color="color" :size="size" />
					<rotatePlane v-if="componentName === 'rotatePlane'" :color="color" :size="size" />
					<doubleBounce v-if="componentName === 'doubleBounce'" :color="color" :size="size" />
				</slot>
				<view v-if="text.length" :style="{ color: textColor }">{{ text }}</view>
			</view>
		</view>
		<!-- #ifdef H5 -->
	</transition>
	<!-- #endif -->
</template>

<script>
import loop from './loaders/loop.vue';
import bounce from './loaders/bounce.vue';
import scaleOut from './loaders/scale-out.vue';
import doubleDot from './loaders/double-dot.vue';
import doubleCube from './loaders/double-cube.vue';
import shrinkRect from './loaders/shrink-rect.vue';
import rotatePlane from './loaders/rotate-plane.vue';
import doubleBounce from './loaders/double-bounce.vue';

export default {
	name: 'ourLoading',
	components: {
		loop,
		bounce,
		scaleOut,
		doubleDot,
		doubleCube,
		shrinkRect,
		rotatePlane,
		doubleBounce
	},
	props: {
		active: Boolean,
		componentName: {
			type: String,
			default: 'shrinkRect'
		},
		text: {
			type: String,
			default: ''
		},
		color: {
			type: String,
			default: '#333'
		},
		textColor: {
			type: String,
			default: '#333'
		},
		isFullScreen: {
			type: Boolean,
			default: false
		},
		backgroundColor: {
			type: String,
			default: 'rgba(255, 255, 255, .9)'
		},
		size: {
			type: Number,
			default: 40
		}
	},
	data() {
		return {
			isActive: this.active || false
		};
	},
	watch: {
		active(value) {
			this.isActive = value;
		}
	}
};
</script>

<style scoped>
.mask {
	position: absolute;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
	z-index: 3000;
	transition: opacity 0.3s linear;
}

.full-screen {
	position: fixed;
	top: 0;
	right: 0;
	bottom: 0;
	left: 0;

	display: flex;
	align-items: center;
	justify-content: center;
}

.spinner {
	text-align: center;
}

/* #ifdef H5 */
.fade-enter-active,
.fade-leave-active {
	transition: opacity 0.3s;
}

.fade-enter,
.fade-leave-to {
	opacity: 0;
}
/* #endif */
</style>
