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
  // TODO(F10-1)：按下面 6 步实现
  //  1) const headers = { ...(options.headers || {}) }
  //  2) const token = getToken(); if (token) headers.Authorization = 'Bearer ' + token
  //  3) const res = await fetch('/api' + url, { ...options, headers })
  //  4) if (res.status === 401) { clearToken(); 跳 '/login'（window.location.href）；再 throw 错误 }
  //  5) const json = await res.json(); if (json.code !== 0) throw new Error(json.msg || '请求失败')
  //  6) return json.data
  throw new Error('TODO(F10-1)：请实现 request()')
}

/** 业务方调用：带 JSON body 的 POST（可选，帮你少写样板） */
export function postJSON(url, data) {
  // TODO(F10-2)：可选实现
  // return request(url, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data) })
  throw new Error('TODO(F10-2)：请实现 postJSON()')
}
