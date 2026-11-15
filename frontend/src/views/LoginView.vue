<script setup>
// F9 登录页（骨架 —— 你来实现 TODO）
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { login } from '@/api/user'
import { useAuth } from '@/composables/useAuth'

const router = useRouter()
const route = useRoute()
const { saveLogin } = useAuth()

const username = ref('')
const password = ref('')
const loading = ref(false)
const errorMsg = ref('')

async function submit() {
  if (!username.value) {
    errorMsg.value = '请输入用户名'
    return
  }
  if (!password.value) {
    errorMsg.value = '请输入密码'
    return
  }

  loading.value = true
  errorMsg.value = ''

  try {
    const data = await login(username.value, password.value)
    saveLogin(data)
    router.push(route.query.redirect || '/')
  } catch (e) {
    errorMsg.value = e.message
  } finally {
    loading.value = false
  }

}

</script>

<template>
  <section class="auth-view">
    <h2 class="section-title">登录</h2>
    <form class="auth-form" @submit.prevent="submit">
      <input v-model="username" class="auth-input" placeholder="用户名（3-20 位）" autocomplete="username" />
      <input
        v-model="password"
        class="auth-input"
        type="password"
        placeholder="密码（6-32 位）"
        autocomplete="current-password"
      />
      <p v-if="errorMsg" class="auth-error">{{ errorMsg }}</p>
      <button class="btn" type="submit" :disabled="loading">
        {{ loading ? '登录中…' : '登录' }}
      </button>
    </form>
    <p class="auth-tip">还没有账号？<RouterLink to="/register">去注册</RouterLink></p>
  </section>
</template>

<style scoped>
.auth-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  padding: 40px 20px;
}
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 280px;
}
.auth-input {
  padding: 8px 10px;
  font-size: 14px;
  color: #ddd;
  background: #262626;
  border: 1px solid #3a3a3a;
  border-radius: 4px;
}
.auth-error {
  color: #c05050;
  font-size: 13px;
}
.auth-tip {
  color: #888;
  font-size: 13px;
}
.auth-tip a {
  color: #4c9aff;
}
</style>
