<script setup>
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useRecords } from '@/composables/useRecords'
import { useAuth } from '@/composables/useAuth'

// 全局错误提示（保存/读取失败都会显示在底部）
const { error } = useRecords()

const { user, isLoggedIn, logout } = useAuth()
const router = useRouter()

function handleLogout() {
  logout()
  router.push('/login')
}
</script>

<template>
  <nav class="tabs">
    <RouterLink class="tab" to="/">计时</RouterLink>
    <RouterLink class="tab" to="/history">历史</RouterLink>

    <template v-if="isLoggedIn">
      <RouterLink class="tab" to="/record/new">录入</RouterLink>
      <RouterLink class="tab" to="/stats">统计</RouterLink>
      <span class="tab tab-user">{{ user?.nickname || user?.username }}</span>
      <button class="tab tab-logout" @click="handleLogout">退出登录</button>
    </template>
    <template v-else>
      <RouterLink class="tab" to="/login">登录</RouterLink>
      <RouterLink class="tab" to="/register">注册</RouterLink>
    </template>
  </nav>

  <main class="content">
    <RouterView />
  </main>

  <p v-if="error" class="msg msg-error">{{ error }}</p>
</template>

<style scoped>
.tab-user {
  color: #888;
}
.tab-logout {
  font: inherit;
  border: none;
  background: none;
  cursor: pointer;
}
</style>
