<template>
	<view class="monthDataChart">
		<l-echart ref="chartRef" is-disable-scroll @finished="initEcharts"></l-echart>
	</view>
</template>

<script>
	import * as echarts from 'echarts';
	import {
		colorHexTurnRgba
	} from "@/common/common.js";

	export default {
		name: "MonthDataChart",
		props: {
			seriesList: {
				type: Array,
				default: () => {
					return []
				}
			},
			returnDisplayData: {
				type: Object,
				default: () => {
					return {}
				}
			}
		},
		computed: {
			watchReturnListData() {
				const {
					seriesList,
					returnDisplayData
				} = this;
				return {
					seriesList,
					returnDisplayData
				};
			}
		},
		watch: {
			watchReturnListData: {
				deep: true,
				immediate: true,
				handler(newValue, oldValue) {
					this.renewChartFun();
				}
			}
		},
		data() {
			return {
				optsEchart: {
					color: ["#328EEC", "#E9B50F"],
					tooltip: {
						trigger: 'axis',
					},
					legend: {
						icon: "rect",
						top: 'bottom',
						left: "center"
					},
					grid: {
						top: '8%',
						left: '2%',
						right: '2%',
						bottom: '12%',
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
					series: []
				}
			}
		},
		methods: {
			initEcharts() {
				this.$refs.chartRef.init(echarts, chart => {
					chart.setOption(this.optsEchart);
				});
			},
			renewChartFun() {
				let seriesList = [];
				for (let i = 0; i < this.seriesList.length; i++) {
					seriesList.push({
						type: 'line',
						smooth: true,
						name: this.seriesList[i].titleName,
						data: this.returnDisplayData[this.seriesList[i].fieldName + 'List'],
						areaStyle: {
							color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
									offset: 0,
									color: this.seriesList[i].color
								},
								{
									offset: 1,
									color: colorHexTurnRgba(this.seriesList[i].color, 0.2)
								}
							])
						}
					})
				}
				this.optsEchart.series = JSON.parse(JSON.stringify(seriesList));
				this.optsEchart.xAxis.data = JSON.parse(JSON.stringify(this.returnDisplayData.xaxisList));

				this.$nextTick(() => {
					this.$refs.chartRef.setOption(this.optsEchart);
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.monthDataChart {
		width: 100%;
		height: 100%;
	}
</style>