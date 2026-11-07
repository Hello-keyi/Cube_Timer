<script setup>
import { computed, onMounted, ref } from 'vue'
import TimerDisplay from '@/components/TimerDisplay.vue'
import StatCard from '@/components/StatCard.vue'
import RecordList from '@/components/RecordList.vue'
import { useRecords } from '@/composables/useRecords'
import { fetchStats } from '@/api/records'
import ScoreInput from '@/components/ScoreInput.vue'

const { records, load, add } = useRecords()

// 最近 5 次（后端已经是新 → 旧）
const recentRecords = computed(() => records.value.slice(0, 5))

// 统计全部来自后端 D12 的聚合接口（前端不再自己算）
const stats = ref(null)

const avg5 = computed(() => stats.value?.avg5Text ?? '--')
const avg12 = computed(() => stats.value?.avg12Text ?? '--')
const best = computed(() => stats.value?.bestText ?? '--')
const worst = computed(() => stats.value?.worstText ?? '--')

async function loadStats() {
  try {
    stats.value = await fetchStats()
  } catch {
    // 统计失败不阻塞页面；具体错误看 F12 / 全局提示
  }
}

onMounted(() => {
  load()
  loadStats()
})

async function handleFinish(timeMs) {
  try {
    await add(timeMs)
    await loadStats()
  } catch {
    // 错误信息已经由 useRecords 写到全局的 error 里
  }
}

async function handleManualAdd(timeMs) {
  try {
    await add(timeMs)
    await loadStats()
  } catch { /* 错误已由 useRecords 写到全局 */ }
}
</script>

<template>
  
  <section class="timer-view">
  
    <TimerDisplay @finish="handleFinish" />
    <ScoreInput @add="handleManualAdd" />
    <div class="stats">
      <StatCard label="AVG5" :value="avg5" />
      <StatCard label="AVG12" :value="avg12" />
      <StatCard label="BEST" :value="best" />
      <StatCard label="WORST" :value="worst" />
    </div>
    <h2 class="section-title">最近 5 次</h2>
    
    <RecordList :records="recentRecords" empty-text="还没有成绩，点上面的按钮开始计时" />
    
  </section>
</template>
