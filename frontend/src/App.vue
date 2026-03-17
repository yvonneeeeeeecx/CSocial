<template>
  <div class="app-wrapper">
    <MegaTopNav v-if="showNav" :is-home="isHome" @login-click="handleLoginClick" />
    <router-view v-slot="{ Component }">
      <transition name="page-fade" mode="out-in">
        <component :is="Component" ref="pageRef" />
      </transition>
    </router-view>
    <SiteFooter />
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import MegaTopNav from './components/MegaTopNav.vue'
import SiteFooter from './components/SiteFooter.vue'

const route = useRoute()
const pageRef = ref(null)

const showNav = computed(() => {
  return !route.path.startsWith('/admin')
})

const isHome = computed(() => {
  return route.path === '/' || route.path === '/login'
})

const handleLoginClick = () => {
  if (pageRef.value && typeof pageRef.value.showLoginDialog !== 'undefined') {
    pageRef.value.showLoginDialog = true
  }
}
</script>

<style>
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.3s ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}

:root {
  --primary-color: #000000;
  --secondary-color: #666666;
  --bg-color: #f5f5f7;
  --card-bg: #ffffff;
  --border-radius: 12px;
  --transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  --font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

html, body, #app {
  margin: 0;
  padding: 0;
  min-height: 100%;
  background-color: var(--bg-color);
  color: #1d1d1f;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

body {
  font-family: var(--font-family);
  line-height: 1.5;
}

.app-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-wrapper > :nth-child(2) {
  flex: 1;
}

* {
  box-sizing: border-box;
}

.el-button {
  border-radius: 20px !important;
  font-weight: 500 !important;
  transition: var(--transition) !important;
}

.el-button--primary {
  background-color: var(--primary-color) !important;
  border-color: var(--primary-color) !important;
}

.el-button--primary:hover {
  background-color: #333333 !important;
  border-color: #333333 !important;
  transform: translateY(-1px);
}

.el-card {
  border: none !important;
  border-radius: var(--border-radius) !important;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02) !important;
  transition: var(--transition) !important;
}

.el-card:hover {
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.06) !important;
}

.el-input__inner, .el-textarea__inner {
  border-radius: 8px !important;
  border: 1px solid #d2d2d7 !important;
}

.el-input__inner:focus, .el-textarea__inner:focus {
  border-color: var(--primary-color) !important;
}

.el-tabs__item.is-active {
  color: var(--primary-color) !important;
}

.el-tabs__active-bar {
  background-color: var(--primary-color) !important;
}

::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #d2d2d7;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #86868b;
}
</style>
