<script setup>
// F9 注册页（骨架 —— 你来实现 TODO）
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'

const router = useRouter()

const username = ref('')
const password = ref('')
const nickname = ref('')
const loading = ref(false)
const errorMsg = ref('')

async function submit() {
  // TODO(F9-5)：
  //  1) 非空校验 + 长度校验（用户名 3-20、密码 6-32）→ errorMsg
  //  2) loading = true; errorMsg = ''
  //  3) await register({ username: username.value, password: password.value, nickname: nickname.value })
  //  4) 成功 → router.push('/login')（提示注册成功可选）
  //  5) catch (e) { errorMsg.value = e.message } finally { loading.value = false }
}

</script>

<template>
  <section class="auth-view">
    <h2 class="section-title">注册</h2>
    <form class="auth-form" @submit.prevent="submit">
      <input v-model="username" class="auth-input" placeholder="用户名（3-20 位）" autocomplete="username" />
      <input
        v-model="password"
        class="auth-input"
        type="password"
        placeholder="密码（6-32 位）"
        autocomplete="new-password"
      />
      <input v-model="nickname" class="auth-input" placeholder="昵称（可选）" />
      <p v-if="errorMsg" class="auth-error">{{ errorMsg }}</p>
      <button class="btn" type="submit" :disabled="loading">
        {{ loading ? '注册中…' : '注册' }}
      </button>
    </form>
    <p class="auth-tip">已有账号？<RouterLink to="/login">去登录</RouterLink></p>
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
