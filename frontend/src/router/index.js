import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import HomeView from '../views/HomeView.vue'
import AdminView from '../views/AdminView.vue'
import UserProfileView from '../views/UserProfileView.vue'
import AlarmView from '../views/AlarmView.vue'
import CampusServicesView from '../views/CampusServicesView.vue'
import FeedView from '../views/FeedView.vue'
import CommunityView from '../views/CommunityView.vue'
import CampusUsersView from '../views/CampusUsersView.vue'

const routes = [
  { path: '/', component: LoginView },
  { path: '/login', component: LoginView },
  { path: '/register', component: RegisterView },
  { path: '/home', component: HomeView },
  { path: '/alarm', component: AlarmView },
  { path: '/campus-services', component: CampusServicesView },
  { path: '/feed', component: FeedView },
  { path: '/community', component: CommunityView },
  { path: '/campus-users', component: CampusUsersView },
  { path: '/user/:id', component: UserProfileView },
  { path: '/admin', component: AdminView }
]


const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const user = JSON.parse(localStorage.getItem('currentUser') || 'null')
  
  // Public paths
  const publicPaths = ['/login', '/register', '/']
  if (publicPaths.includes(to.path)) {
    return true
  }

  // Auth check
  if (!user?.id) {
    return '/login'
  }

  if (to.path === '/admin' && user?.role !== 'ADMIN') {
    return '/home'
  }

  if ((to.path === '/home' || to.path.startsWith('/user')) && user?.role === 'ADMIN') {
    return '/admin'
  }


  return true
})

export default router

