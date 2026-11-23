// 全局登录态：模块级 ref，所有组件共享同一份（类似 useRecords 的写法）
import { computed, ref } from 'vue'
import { clearToken, getToken, setToken } from '@/api/request'
import { useRecords } from '@/composables/useRecords'

const USER_KEY = 'cube_timer_user'

function readUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  } catch {
    return null
  }
}

// 模块级状态：App.vue、LoginView 都拿到同一份
const token = ref(getToken())
const user = ref(readUser())

export function useAuth() {
  const isLoggedIn = computed(() => !!token.value)

  /** 登录成功后调用：保存 token + 用户信息（页面立刻刷新） */
  function saveLogin(loginData) {
    setToken(loginData.token)
    token.value = loginData.token
    user.value = {
      id: loginData.id,
      username: loginData.username,
      nickname: loginData.nickname,
    }
    localStorage.setItem(USER_KEY, JSON.stringify(user.value))
  }

  /** 退出登录：清 token + 用户信息 */
  function logout() {
    clearToken()
    token.value = ''
    user.value = null
    localStorage.removeItem(USER_KEY)
    useRecords().reset()   // 清掉成绩缓存，避免串号
  }

  return { token, user, isLoggedIn, saveLogin, logout }
}
