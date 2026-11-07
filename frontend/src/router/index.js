import { createRouter, createWebHistory } from 'vue-router'
import TimerView from '@/views/TimerView.vue'
import HistoryView from '@/views/HistoryView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import DetailView from '@/views/DetailView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'timer', component: TimerView },
    { path: '/history', name: 'history', component: HistoryView },
    // F7：成绩详情页（动态路由参数 :id）
    { path: '/record/:id', name: 'recordDetail', component: DetailView },
    // 404 兜底：放在最后，匹配所有没命中的路径
    { path: '/:pathMatch(.*)*', name: 'notFound', component: NotFoundView },
  ],
})

export default router
