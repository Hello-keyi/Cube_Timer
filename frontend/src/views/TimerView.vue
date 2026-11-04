<script setup>
import { computed, onMounted } from 'vue'
import TimerDisplay from '@/components/TimerDisplay.vue'
import StatCard from '@/components/StatCard.vue'
import RecordList from '@/components/RecordList.vue'
import { useRecords } from '@/composables/useRecords'
import { averageMs, formatMs } from '@/utils/format'
import ScoreInput from '@/components/ScoreInput.vue'

const { records, load, add } = useRecords()

// 最近 5 次（后端已经是新 → 旧）
const recentRecords = computed(() => records.value.slice(0, 5))

const avg5 = computed(() => {
  const avg = averageMs(records.value, 5)
  return avg === null ? '--' : formatMs(avg)
})

const avg12 = computed(() => {
  const avg = averageMs(records.value, 12)
  return avg === null ? '--' : formatMs(avg)
})

onMounted(() => load())

async function handleFinish(timeMs) {
  try {
    await add(timeMs)
  } catch {
    // 错误信息已经由 useRecords 写到全局的 error 里
  }
}

async function handleManualAdd(timeMs) {
  try { await add(timeMs) } catch { /* 错误已由 useRecords 写到全局 */ }
}
</script>

<template>
  
  <section class="timer-view">
  
    <TimerDisplay @finish="handleFinish" />
    <ScoreInput @add="handleManualAdd" />
    <div class="stats">
      <StatCard label="AVG5" :value="avg5" />
      <StatCard label="AVG12" :value="avg12" />
    </div>
    <h2 class="section-title">最近 5 次</h2>
    
    <RecordList :records="recentRecords" empty-text="还没有成绩，点上面的按钮开始计时" />
    
  </section>
</template>
