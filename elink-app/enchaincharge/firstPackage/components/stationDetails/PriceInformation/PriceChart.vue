<template>
	<view class="priceChart">
		<l-echart ref="chartRef" is-disable-scroll @finished="initEcharts"></l-echart>
	</view>
</template>

<script>
	import * as echarts from 'echarts';

	export default {
		name: 'PriceChart',
		options: { styleIsolation: 'shared' }, //解决/deep/不生效**
		props: {
			// 图表颜色
			chartColor: {
				type: String,
				default: ''
			},
			xAxisData: {
				type: Array,
				default: () => []
			},
			seriesData: {
				type: Array,
				default: () => []
			},
			seriesName: {
				type: String,
				default: '充电价格'
			},
			chartNum: {
				type: String,
				default: ''
			},
		},
		data() {
			return {
				optsEchart: {
					color: ["#079CEB"],
					tooltip: {
						trigger: 'axis',
					},
					grid: {
						top: '15%',
						left: '2%',
						right: '2%',
						bottom: '2%',
						containLabel: true
					},
					dataZoom: [{
						type: 'inside',
						start: 0,
						end: 100
					}],
					xAxis: {
						data: [],
						type: 'category',
						splitNumber: 3,
						boundaryGap: false,
						axisTick: {
							show: false
						},
						axisLine: {
							lineStyle: {
								color: '#F7F7F7'
							}
						},
						axisLabel: {
							color: '#989898'
						}
					},
					yAxis: [{
						type: 'value',
						name: '元/度',
						splitNumber: 3,
						axisLine: {
							show: false
						},
						axisTick: {
							show: false
						},
						axisLabel: {
							color: '#989898'
						}
					}],
					series: [{
						name: '价格',
						type: 'line',
						smooth: true,
						data: []
					}]
				},
			};
		},
		methods: {
			initEcharts() {
				this.$refs.chartRef.init(echarts, chart => {
					this.optsEchart.color = [this.chartColor];
					this.optsEchart.xAxis.data = this.xAxisData;
					this.optsEchart.series[0].data = this.seriesData;
					this.optsEchart.series[0].name = this.seriesName;
					chart.setOption(this.optsEchart);
					
					// 默认显示当前 费率的提示框
					setTimeout(() => {
						if (this.chartNum >= 0) {
							chart.dispatchAction({
								type: 'showTip',
								seriesIndex: 0, // 第几条series
								dataIndex: this.chartNum // 显示第几个数据的tooltip
							});
						}
					}, 1000)
				});
			}
		}
	};
</script>

<style scoped lang="scss">
	.priceChart {
		width: 100%;
		height: 420rpx;
		padding: 0 24rpx 24rpx;
		box-sizing: border-box;
		
		/deep/ .lime-echart{
			.lime-echart__canvas{
				width: 100% !important;
				height: 100% !important;
			}
		}
	}
</style>