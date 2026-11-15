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
    <RouterLink class="tab" to="/login">登录</RouterLink>
    <RouterLink class="tab" to="/register">注册</RouterLink>
    <!-- TODO(F10-4)：登录后改为显示「昵称 / 退出登录」，未登录才显示登录/注册 -->
  </nav>

  <main class="content">
    <RouterView />
  </main>

  <p v-if="error" class="msg msg-error">{{ error }}</p>
</template>
