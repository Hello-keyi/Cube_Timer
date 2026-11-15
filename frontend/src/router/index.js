import { createRouter, createWebHistory } from 'vue-router'
import TimerView from '@/views/TimerView.vue'
import HistoryView from '@/views/HistoryView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import DetailView from '@/views/DetailView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: LoginView },
    { path: '/register', name: 'register', component: RegisterView },
    { path: '/', name: 'timer', component: TimerView, meta: { requiresAuth: true } },
    { path: '/history', name: 'history', component: HistoryView, meta: { requiresAuth: true } },
    // F7：成绩详情页（动态路由参数 :id）
    { path: '/record/:id', name: 'recordDetail', component: DetailView, meta: { requiresAuth: true } },
    // 404 兜底：放在最后，匹配所有没命中的路径
    { path: '/:pathMatch(.*)*', name: 'notFound', component: NotFoundView },
  ],
})


// TODO(F10-3)：路由守卫（未登录访问受限页面 → 跳登录）
// import { getToken } from '@/api/request'
// router.beforeEach((to) => {
//   if (to.meta.requiresAuth && !getToken()) {
//     return { path: '/login', query: { redirect: to.fullPath } }
//   }
// })

export default router
