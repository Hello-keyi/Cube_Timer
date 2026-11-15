// 用户接口层：登录 / 注册
const BASE_URL = '/api'

/**
 * 登录
 * POST /user/login  body: { username, password }
 * 返回: { id, username, nickname, token }
 * TODO(F9-1)：
 *   参照 api/records.js 的写法：fetch → json → json.code !== 0 抛 Error(json.msg) → return json.data
 *   注意：登录接口不需要 token
 */
export async function login(username, password) {
  throw new Error('TODO(F9-1)：请实现 login(username, password)')
}

/**
 * 注册
 * POST /user/register  body: { username, password, nickname }
 * 成功无 data；失败抛 Error(json.msg)
 * TODO(F9-2)：实现 register({ username, password, nickname })
 */
export async function register({ username, password, nickname }) {
  throw new Error('TODO(F9-2)：请实现 register()')
}
