<script setup>
import { ref } from 'vue'
import { formatMs } from '@/utils/format'
const visible = ref(true)

defineProps({
  scores:{ type: Array, default: () => [] },
})
</script>

<style scoped>
.score-list {
  background-color: #333;
  width: 300px;
}
.score-list .score-item {
  justify-content: space-between;
  padding: 10px;
  border-bottom: 1px solid #555;
}
.score-list .score-item .score-timeMs-late {
  color: red;
}
.score-list .score-item .score-timeMs {
  color: green;
}
.score-list .score-empty {
  text-align: center;
  padding: 20px;
}
</style>

<template>
  <button @click="visible = !visible" v-if="visible">close</button>
  <button @click="visible = !visible" v-else>open</button>
  <div class="score-list" v-show="visible">
    <div v-for="(item , index) in scores" :key="item.id" class="score-item">
        <span class="score-id">{{ index + 1 + ":" }}</span>
        <span v-if="item.timeMs > 60000" class="score-timeMs-late">{{ formatMs(item.timeMs) }}</span>
        <span v-else class="score-timeMs">{{  formatMs(item.timeMs) }}</span>
        <span class="score-creatAt">{{ item.creatAt }}</span>
    </div>
  </div>
</template>