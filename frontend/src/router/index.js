import { createRouter, createWebHistory } from 'vue-router'
import { getToken, getUserInfo } from '../utils/auth'

import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import FomoTestView from '../views/FomoTestView.vue'
import ResultView from '../views/ResultView.vue'
import RecommendationView from '../views/RecommendationView.vue'
import ChatView from '../views/ChatView.vue'
import VisualizationView from '../views/VisualizationView.vue'
import NewsView from '../views/NewsView.vue'
import SbtiView from '../views/SbtiView.vue'
import AdminView from '../views/AdminView.vue'

const routes = [
  { path: '/', component: HomeView },
  { path: '/login', component: LoginView },
  { path: '/register', component: RegisterView },
  { path: '/fomo', component: FomoTestView, meta: { requiresAuth: true } },
  { path: '/result', component: ResultView, meta: { requiresAuth: true } },
  { path: '/recommendation', component: RecommendationView, meta: { requiresAuth: true } },
  { path: '/chat', component: ChatView, meta: { requiresAuth: true } },
  { path: '/visualization', component: VisualizationView, meta: { requiresAuth: true } },
  { path: '/news', component: NewsView },
  { path: '/sbti', component: SbtiView },
  { path: '/admin', component: AdminView, meta: { requiresAuth: true, requiresAdmin: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth && !getToken()) {
    return '/login'
  }
  if (to.meta.requiresAdmin && getUserInfo()?.username !== 'admin') {
    return '/'
  }
})

export default router
