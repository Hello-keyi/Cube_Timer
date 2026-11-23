import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/api/request'

// 路由懒加载：每个页面单独打包，首屏不加载用不到的代码（echarts 等）
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue') },
    { path: '/register', name: 'register', component: () => import('@/views/RegisterView.vue') },
    { path: '/', name: 'timer', component: () => import('@/views/TimerView.vue'), meta: { requiresAuth: true } },
    { path: '/history', name: 'history', component: () => import('@/views/HistoryView.vue'), meta: { requiresAuth: true } },
    // 注意：/record/new 必须在 /record/:id 之前，否则 'new' 会被当成 id
    { path: '/record/new', name: 'recordNew', component: () => import('@/views/NewRecordView.vue'), meta: { requiresAuth: true } },
    { path: '/record/:id', name: 'recordDetail', component: () => import('@/views/DetailView.vue'), meta: { requiresAuth: true } },
    { path: '/stats', name: 'stats', component: () => import('@/views/StatsView.vue'), meta: { requiresAuth: true } },
    { path: '/upload', name: 'upload', component: () => import('@/views/UploadView.vue'), meta: { requiresAuth: true } },
    // 404 兜底必须放最后
    { path: '/:pathMatch(.*)*', name: 'notFound', component: () => import('@/views/NotFoundView.vue') },
  ],
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth && !getToken()) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
})

export default router
