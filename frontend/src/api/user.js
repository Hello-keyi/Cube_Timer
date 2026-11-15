// 用户接口层：登录 / 注册
const BASE_URL = '/api'

/**
 * 登录
 * POST /user/login  body: { username, password }
 * 返回: { id, username, nickname, token }
 * 前端请求到登录接口后，返回的 json.code !== 0 说明登录失败，抛出错误；否则登录成功，返回 json.data
 */
export async function login(username, password) {

  const res = await fetch(BASE_URL + '/user/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  })

  const json = await res.json()
  if (json.code !== 0) {
    throw new Error(json.msg || '请求失败')
  }
  return json.data
}

/**
 * 注册
 * POST /user/register  body: { username, password, nickname }
 * 成功无 data；失败抛 Error(json.msg)
 * 前端请求到注册接口后，返回的 json.code !== 0 说明注册失败，抛出错误；否则注册成功，无需返回 data
 */
export async function register({ username, password, nickname }) {
  const res = await fetch(BASE_URL + '/user/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password, nickname })
  })

  const json = await res.json()
  if (json.code !== 0) {
    throw new Error(json.msg || '请求失败')
  }
}
