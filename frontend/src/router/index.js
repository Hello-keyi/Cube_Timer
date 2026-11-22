import { createRouter, createWebHistory } from 'vue-router'
import TimerView from '@/views/TimerView.vue'
import HistoryView from '@/views/HistoryView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import DetailView from '@/views/DetailView.vue'
import NewRecordView from '@/views/NewRecordView.vue'
import { getToken } from '@/api/request'
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: LoginView },
    { path: '/register', name: 'register', component: RegisterView },
    { path: '/', name: 'timer', component: TimerView, meta: { requiresAuth: true } },
    { path: '/history', name: 'history', component: HistoryView, meta: { requiresAuth: true } },
    // F8：手动批量录入（注意：必须放在 /record/:id 之前，否则 'new' 会被当成 id）
    { path: '/record/new', name: 'recordNew', component: NewRecordView, meta: { requiresAuth: true } },
    // F7：成绩详情页（动态路由参数 :id）
    { path: '/record/:id', name: 'recordDetail', component: DetailView, meta: { requiresAuth: true } },
    // 404 兜底：放在最后，匹配所有没命中的路径
    { path: '/:pathMatch(.*)*', name: 'notFound', component: NotFoundView },
    { path: '/stats', name: 'stats', component: StatsView, meta: { requiresAuth: true } },
    { path: '/upload', name: 'upload', component: UploadView, meta: { requiresAuth: true } },
  ],
})
router.beforeEach((to) => {
  // 检查路由是否需要身份验证
  if (to.meta.requiresAuth && !getToken()) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
});

export default router
