<script setup>
import { onBeforeUnmount, ref } from 'vue'
import { formatMs } from '@/utils/format'

// 子组件只负责"计时"这一件事；结束时把毫秒数告诉父组件
const emit = defineEmits(['finish'])

const display = ref('0.000')
const running = ref(false)

let startTime = 0
let rafId = 0

function tick() {
  display.value = formatMs(performance.now() - startTime)
  rafId = requestAnimationFrame(tick)
}

function start() {
  running.value = true
  startTime = performance.now()
  tick()
}

function stop() {
  running.value = false
  cancelAnimationFrame(rafId)
  const total = performance.now() - startTime
  display.value = formatMs(total)
  emit('finish', Math.round(total)) // 数据库存整数毫秒
}

function toggle() {
  if (running.value) {
    stop()
  } else {
    start()
  }
}

// 组件卸载时（比如切页面）停掉动画，避免内存泄漏
onBeforeUnmount(() => cancelAnimationFrame(rafId))
</script>

<template>
  <div class="timer-display">
    <div class="timer">{{ display }}</div>
    <button class="btn" :class="{ running }" @click="toggle">
      {{ running ? '停止' : '开始' }}
    </button>
  </div>
</template>

