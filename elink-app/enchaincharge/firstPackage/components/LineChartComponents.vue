<template>
	<view class="LineChart">
		<l-echart ref="chartRef" is-disable-scroll @finished="initEcharts"></l-echart>
		
		<view class="null-data" v-if="!chartNullShow">
			<sm-null-data slot="empty"></sm-null-data>
		</view>
	</view>
</template>

<script>
	import * as echarts from 'echarts';
	import { colorHexTurnRgba } from "@/common/common.js";

	export default {
		name: "LineChartComponents",
		props: {
			// 图表数据
			chartData: {
				type: Object,
				default: () => {
					return {}
				}
			}
		},
		data() {
			return {
				chartNullShow: true,
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
						boundaryGap: false,
						splitNumber: 3,
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
					yAxis: {
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
					},
					series: [{
						name: '价格',
						type: 'line',
						smooth: true,
						data: [],
						areaStyle: {
							color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
									offset: 0,
									color: '#00E6FE'
								},
								{
									offset: 1,
									color: colorHexTurnRgba('#00E6FE', 0.2)
								}
							])
						}
					}]
				}
			}
		},
		watch: {
			chartData: {
				deep: true,
				handler(newValue, oldValue) {
					this.getChartData();
				}
			}
		},
		methods: {
			getChartData() {
				this.optsEchart.color = [this.chartData.chartColor];
				this.optsEchart.yAxis.name = this.chartData.yAxisName;
				this.optsEchart.series[0].name = this.chartData.seriesName;
				this.optsEchart.xAxis.data = JSON.parse(JSON.stringify(this.chartData.xaxisList));
				this.optsEchart.series[0].data = JSON.parse(JSON.stringify(this.chartData.dataList));
				this.chartNullShow = this.chartData.xaxisList && this.chartData.xaxisList.length;
				
				this.optsEchart.series[0].areaStyle = {
					color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
							offset: 0,
							color: this.chartData.chartColor
						},
						{
							offset: 1,
							color: colorHexTurnRgba(this.chartData.chartColor, 0.2)
						}
					])
				};
				
				setTimeout(() => {
					this.$refs.chartRef.setOption(this.optsEchart);
				}, 200)
			},
			initEcharts() {
				this.$refs.chartRef.init(echarts, chart => {
					chart.setOption(this.optsEchart);
				});
			}
		}
	}
</script>

<style scoped lang="scss">
	.LineChart {
		width: 100%;
		height: 100%;
		position: relative;
		
		/deep/ .null-data {
			width: 100%;
			height: 100%;
			display: flex;
			align-items: center;
			justify-content: center;
			z-index: 1000;
			position: absolute;
			left: 0;
			top: 0;
			background-color: #ffffff;
			
			.nullDataImage {
				width: 202rpx;
				height: 202rpx;
			}
		}
	}
</style>