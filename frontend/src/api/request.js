// 统一请求封装（F10 核心）
// 作用：所有接口都经过这里 → 自动带 token、统一解析 Result、统一处理 401
const TOKEN_KEY = 'cube_timer_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

/**
 * 统一请求
 * @param {string} url  以 / 开头，不含 /api 前缀（如 '/record/list'）
 * @param {RequestInit} options
 * @returns {Promise<any>} 后端 Result 里的 data 
 */
export async function request(url, options = {}) {
  //前端拿到请求之后分离出请求头，如果token存在，就在请求头中加上Authorization字段，值为Bearer + token，然后发起请求，如果返回状态码是401，说明token过期或者无效，就清除token并跳转到登录页，并抛出错误；如果返回的json的code不为0，说明请求失败，就抛出错误；最后返回json.data
  const headers = { ...(options.headers || {}) }
  const token = getToken()
  if (token) {
    headers.Authorization = 'Bearer ' + token
  }

  const res = await fetch('/api' + url, { ...options, headers })

  if (res.status === 401) {
    clearToken()
    window.location.href = '/login'
    throw new Error('Unauthorized: Token expired or invalid')
  }

  const json = await res.json()
  if (json.code !== 0) {
    throw new Error(json.msg || '请求失败')
  }

  return json.data
}

/** 业务方调用：带 JSON body 的 POST（可选，帮你少写样板） */
export function postJSON(url, data) {
  return request(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  })
}
