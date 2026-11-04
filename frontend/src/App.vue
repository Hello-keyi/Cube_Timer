<script setup>
import { RouterLink, RouterView } from 'vue-router'
import { useRecords } from '@/composables/useRecords'

// 全局错误提示（保存/读取失败都会显示在底部）
const { error } = useRecords()
</script>

<template>
  <nav class="tabs">
    <RouterLink class="tab" to="/">计时</RouterLink>
    <RouterLink class="tab" to="/history">历史</RouterLink>
  </nav>

  <main class="content">
    <RouterView />
  </main>

  <p v-if="error" class="msg msg-error">{{ error }}</p>
</template>
