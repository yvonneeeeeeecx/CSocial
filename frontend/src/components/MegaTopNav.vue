<template>
  <nav class="mega-nav" :class="{ scrolled: isScrolled }" @mouseleave="scheduleCloseMega">
    <div class="nav-shell">
      <div class="nav-left" @click="goHome">
        <div class="logo-box">H</div>
        <span class="logo-text">CSocial</span>
      </div>

      <div class="nav-center" @mouseenter="openMega">
        <div
          v-for="item in menuItems"
          :key="item.key"
          class="nav-item"
          :class="{ active: isActive(item) }"
          @click="goMenu(item)"
        >
          {{ item.label }}
        </div>
      </div>

      <div class="nav-right" @mouseenter="openMiniMenu" @mouseleave="scheduleCloseMiniMenu">
        <template v-if="user">
          <div class="mini-trigger" @click="goToProfile">{{ displayName }}</div>
          <transition name="mini-fade">
            <div v-if="showMiniMenu" class="mini-menu" @mouseenter="openMiniMenu" @mouseleave="scheduleCloseMiniMenu">
              <div class="mini-item" @click="goTo('/campus-users')">校园用户列表</div>
              <div class="mini-item" @click="goToProfile">个人主页</div>
              <div class="mini-item danger" @click="handleLogout">退出登录</div>
            </div>
          </transition>
        </template>
        <el-button v-else class="login-btn" round @click="$emit('login-click')">登录</el-button>
      </div>
    </div>

    <transition name="fade">
      <div v-if="showMega" class="mega-panel" @mouseenter="openMega" @mouseleave="scheduleCloseMega">
        <div class="mega-grid">
          <div v-for="item in menuItems" :key="item.key" class="mega-col">
            <div
              v-for="child in item.children"
              :key="child.label"
              class="col-link"
              @click="handleChildClick(item, child)"
            >
              {{ child.label }}
            </div>
          </div>
        </div>
      </div>
    </transition>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const emit = defineEmits(['login-click'])
const router = useRouter()
const route = useRoute()

const isScrolled = ref(false)
const showMega = ref(false)
const closeTimer = ref(null)
const showMiniMenu = ref(false)
const miniCloseTimer = ref(null)

const getCurrentUser = () => {
  try {
    return JSON.parse(localStorage.getItem('currentUser') || 'null')
  } catch {
    return null
  }
}

const user = ref(getCurrentUser())

const displayName = computed(() => {
  if (!user.value) return ''
  return user.value.nickname || user.value.username || '我的主页'
})

const menuItems = [
  {
    key: 'alarm',
    label: '校园铃',
    path: '/alarm',
    children: [
      { label: '位置共享', query: {} },
      { label: '心动匹配', query: {} }
    ]
  },
  {
    key: 'services',
    label: '校园信息服务',
    path: '/campus-services',
    children: [
      { label: '校园公告', query: { tab: 'announcement' } },
      { label: '活动广场', query: { tab: 'activity' } },
      { label: '二手交易', query: { tab: 'secondhand' } },
      { label: '失物招领', query: { tab: 'lostfound' } }
    ]
  },
  {
    key: 'feed',
    label: '校园动态',
    path: '/feed',
    children: [
      { label: '最新动态', query: {} },
      { label: '热门话题', query: {} }
    ]
  },
  {
    key: 'community',
    label: '社群圈子',
    path: '/community',
    children: [
      { label: '社群广场', query: {} },
      { label: '我加入的', query: { tab: 'joined' } },
      { label: '我创建的', query: { tab: 'created' } }
    ]
  }
]

const isActive = (item) => route.path === item.path || route.path.startsWith(`${item.path}/`)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 8
}

const openMega = () => {
  if (!user.value) return
  cancelCloseMega()
  showMega.value = true
}

const scheduleCloseMega = () => {
  cancelCloseMega()
  closeTimer.value = setTimeout(() => {
    showMega.value = false
  }, 120)
}

const cancelCloseMega = () => {
  if (closeTimer.value) {
    clearTimeout(closeTimer.value)
    closeTimer.value = null
  }
}

const goHome = () => {
  router.push(user.value ? '/home' : '/')
}

const goMenu = (item) => {
  if (!user.value) {
    emit('login-click')
    return
  }
  router.push(item.path)
}

const handleChildClick = (parent, child) => {
  if (!user.value) {
    emit('login-click')
    return
  }
  router.push({ path: parent.path, query: child.query || {} })
  showMega.value = false
}

const cancelCloseMiniMenu = () => {
  if (miniCloseTimer.value) {
    clearTimeout(miniCloseTimer.value)
    miniCloseTimer.value = null
  }
}

const openMiniMenu = () => {
  if (!user.value) return
  cancelCloseMiniMenu()
  showMiniMenu.value = true
}

const scheduleCloseMiniMenu = () => {
  cancelCloseMiniMenu()
  miniCloseTimer.value = setTimeout(() => {
    showMiniMenu.value = false
  }, 120)
}

const goTo = (path) => {
  if (!user.value) {
    emit('login-click')
    return
  }
  showMiniMenu.value = false
  router.push(path)
}

const goToProfile = () => {
  if (!user.value) {
    emit('login-click')
    return
  }
  showMiniMenu.value = false
  router.push(`/user/${user.value.id}`)
}

const handleLogout = () => {
  showMiniMenu.value = false
  localStorage.removeItem('currentUser')
  router.push('/login')
  window.location.reload()
}

watch(() => route.fullPath, () => {
  user.value = getCurrentUser()
  showMega.value = false
  showMiniMenu.value = false
})

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  cancelCloseMega()
  cancelCloseMiniMenu()
})
</script>

<style scoped>
.mega-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 3000;
  --menu-width: 760px;
}

.nav-shell {
  height: 74px;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #eaedf3;
  display: grid;
  grid-template-columns: 220px var(--menu-width) auto;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}

.mega-nav.scrolled .nav-shell {
  box-shadow: 0 8px 22px rgba(0, 0, 0, 0.06);
}

.nav-left {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.logo-box {
  width: 32px;
  height: 32px;
  border: 2px solid #c9dc00;
  color: #a4b700;
  font-weight: 800;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.logo-text {
  font-size: 32px;
  font-weight: 700;
  color: #121212;
  line-height: 1;
  transform: scale(0.5);
  transform-origin: left center;
  width: 70px;
}

.nav-center {
  width: var(--menu-width);
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  align-items: center;
}

.nav-item {
  text-align: center;
  color: #9ea3ad;
  font-size: 44px;
  font-weight: 700;
  line-height: 1;
  transform: scale(0.5);
  transform-origin: center;
  cursor: pointer;
  transition: color 0.2s;
}

.nav-item:hover,
.nav-item.active {
  color: #30343c;
}

.nav-right {
  position: relative;
  display: inline-flex;
  align-items: center;
  min-width: 170px;
  justify-content: flex-end;
}

.mini-trigger {
  max-width: 160px;
  color: #30343c;
  background: #fff;
  border: 1px solid #e5eaf1;
  border-radius: 999px;
  padding: 8px 14px;
  font-size: 26px;
  font-weight: 700;
  line-height: 1;
  transform: scale(0.5);
  transform-origin: right center;
  cursor: pointer;
  transition: all 0.2s ease;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mini-trigger:hover {
  border-color: #cfd7e5;
  background: #f9fbff;
}

.mini-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  z-index: 3100;
  min-width: 168px;
  background: rgba(255, 255, 255, 0.98);
  border: 1px solid #e6ebf3;
  border-radius: 14px;
  box-shadow: 0 16px 34px rgba(20, 32, 56, 0.12);
  padding: 8px;
}

.mini-item {
  color: #6f7785;
  font-size: 14px;
  font-weight: 600;
  line-height: 1.2;
  padding: 10px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.mini-item:hover {
  color: #2c3340;
  background: #f4f7fc;
}

.mini-item.danger {
  color: #d94848;
}

.mini-item.danger:hover {
  background: #fff2f2;
  color: #bf3535;
}

.login-btn {
  border: 1px solid #dfe5ee;
  background: #fff;
  color: #2f3642;
  font-weight: 700;
}

.login-btn:hover {
  border-color: #cfd7e5;
  background: #f9fbff;
  color: #1f2633;
}

.mini-fade-enter-active,
.mini-fade-leave-active {
  transition: opacity 0.16s ease, transform 0.16s ease;
}

.mini-fade-enter-from,
.mini-fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

.mega-panel {
  background: #fff;
  border-bottom: 1px solid #eaedf3;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.07);
}

.mega-grid {
  width: var(--menu-width);
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  padding: 10px 0 14px;
}

.mega-col {
  min-height: 94px;
  padding: 0 12px;
  border-right: 1px solid #eff2f6;
}

.mega-col:last-child {
  border-right: none;
}

.col-link {
  color: #6d7380;
  font-size: 26px;
  font-weight: 600;
  line-height: 1.6;
  transform: scale(0.5);
  transform-origin: left center;
  width: fit-content;
  cursor: pointer;
}

.col-link:hover {
  color: #222730;
  text-decoration: underline;
  text-underline-offset: 4px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.16s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 1180px) {
  .nav-shell {
    grid-template-columns: 180px 1fr auto;
    --menu-width: 560px;
  }
}

@media (max-width: 960px) {
  .nav-shell {
    grid-template-columns: auto auto;
    gap: 12px;
  }

  .nav-center,
  .mega-panel {
    display: none;
  }
}

@media (max-width: 768px) {
  .nav-shell {
    height: 64px;
    padding: 0 12px;
  }

  .logo-text {
    display: none;
  }

  .mini-trigger {
    font-size: 22px;
    max-width: 120px;
  }

  .mini-menu {
    min-width: 154px;
  }

  .mini-item {
    font-size: 13px;
  }
}
</style>
