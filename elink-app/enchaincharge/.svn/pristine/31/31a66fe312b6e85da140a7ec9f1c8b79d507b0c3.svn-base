<template>
	<view class="container">
		<view class="l-echart">
			<l-echart ref="chartRef" is-disable-scroll @finished="initEcharts"></l-echart>
		</view>
	</view>
</template>

<script>
	export default {
		name: "echarts",
		data() {
			return {
				optsEchart: {
					xAxis: {
						type: 'category',
						data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
					},
					yAxis: {
						type: 'value'
					},
					series: [{
						data: [820, 932, 901, 934, 1290, 1330, 1320],
						type: 'line',
						smooth: true
					}]
				}
			}
		},
		methods: {
			// initEcharts() {
			// 	this.$refs.chartRef.init(this.$echarts, chart => {
			// 		chart.setOption(this.optsEchart);
			// 	});
			// },
		}
	}
</script>

<style scoped lang="scss">
	.l-echart {
		width: 100%;
		height: 520rpx;
	}
</style>