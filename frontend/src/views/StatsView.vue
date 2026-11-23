<script setup>
import {onBeforeUnmount, ref , onMounted} from 'vue'
import * as echarts from 'echarts/core'
import { BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([BarChart, GridComponent, TooltipComponent, CanvasRenderer])
import { fetchStats } from '@/api/records'

const chartRef = ref(null)
const loading = ref(false)
const errorMsg = ref('')
let chart = null

function toSeconds(text) {
    if (text == null || text === '') return 0
    const n = Number(text)
    return Number.isNaN(n) ? 0 : n
}

function renderChart(stats) {
    const labels = ['AVG5', 'AVG12', 'BEST', 'WORST', 'TODAY BEST']
    const values = [
        toSeconds(stats.avg5Text),
        toSeconds(stats.avg12Text),
        toSeconds(stats.bestText),
        toSeconds(stats.worstText),
        toSeconds(stats.todayBestText)
    ]

    chart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 40, right: 20, top: 30, bottom: 40 },
        xAxis: { type: 'category', data: labels },
        yAxis: { type: 'value', name: '秒' },
        series: [
            {
                name: '成绩（秒）',
                type: 'bar',
                barWidth: '50%',
                data: values,
                itemStyle: { color: '#4c9aff' },
            },
        ],
    })
}

async function load() {
    loading.value = true
    errorMsg.value = ''
    try {
        const stats = await fetchStats()
        renderChart(stats)
    } catch (e) {
        errorMsg.value = '加载统计失败：' + e.message
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    chart = echarts.init(chartRef.value)
    load()
})

onBeforeUnmount(() => {
    if (chart) {
        chart.dispose()
        chart = null
    }
})
</script>

<template>
    <section class = "stats-view">
        <h2 class = "section-title">成绩统计</h2>
        <p v-if="loading" class="stats-msg">加载中...</p>
        <p v-else-if ="errorMsg" class="stats-msg stats-msg-error">{{ errorMsg }}</p>
        <div ref = "chartRef" class="chart"></div>
    </section>
</template>

<style scoped>
.stats-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px;
}
.chart {
  width: 640px;
  max-width: 90vw;
  height: 360px;
}
.stats-msg {
  color: #888;
  font-size: 14px;
}
.stats-msg-error {
  color: #c05050;
}
</style>