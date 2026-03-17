<template>
  <div class="landing-page">
    <!-- Navbar -->
    <nav class="navbar" :class="{ 'scrolled': isScrolled }">
      <div class="logo-wrapper">
        <div class="logo-core">
          <el-icon class="logo-icon"><Platform /></el-icon>
        </div>
        <span class="logo-text">CSocial</span>
      </div>
      <div class="nav-actions">
        <el-button class="nav-btn-text" text @click="showLoginDialog = true">登录</el-button>
        <el-button class="nav-btn-primary" round @click="$router.push('/register')">免费注册</el-button>
      </div>
    </nav>

    <!-- Hero Section (参考图三设计) -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">CSocial</h1>
        <p class="hero-subtitle">要将您的状态同步至校园圈吗</p>
        <div class="heart-container">
          <el-icon class="heart-icon"><svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg"><path fill="currentColor" d="M512 896c-18.048 0-35.968-7.552-48.448-22.592L147.136 492.224A247.616 247.616 0 0 1 78.4 316.032C78.4 182.016 186.432 73.088 319.296 73.088c64.576 0 125.696 26.176 170.176 73.728l22.528 24.128 22.528-24.128c44.48-47.552 105.6-73.728 170.176-73.728 132.8 0 240.832 108.928 240.832 242.944 0 65.28-24.896 126.72-70.144 177.6L560.448 873.408A63.36 63.36 0 0 1 512 896z"></path></svg></el-icon>
        </div>
        <el-button class="sync-btn" round size="large" @click="showLoginDialog = true">继续</el-button>
      </div>
    </section>

    <!-- Features Section (参考图二布局，明亮风格) -->
    <section class="features-section">
      <!-- 顶部 Banner -->
      <div class="feature-banner">
        <div class="banner-text">
          <h2 class="banner-title">专属你的<br>校园主场。</h2>
          <p class="banner-desc">智能的校园生活助手，<br>深度的情感连接，<br>以及现代化的互动设计，<br>为您打造和谐的校园圈。</p>
          <el-button round class="learn-more-btn" @click="showLoginDialog = true">了解更多</el-button>
        </div>
        <div class="banner-image">
          <div class="mock-image">
             <el-icon class="mock-icon"><Opportunity /></el-icon>
          </div>
        </div>
      </div>

      <!-- Brand Philosophy -->
      <div class="philosophy-section">
        <h2 class="section-title">平台特色</h2>
        <p class="section-subtitle">智能推荐与安全审核双管齐下，为您构建纯净的校园生活体验。</p>
        
        <div class="philosophy-grid">
          <div class="phil-card">
            <div class="phil-icon-wrapper phil-blue"><el-icon><ChatDotRound /></el-icon></div>
            <h3>校园畅聊<br>即时互动</h3>
          </div>
          <div class="phil-card">
            <div class="phil-icon-wrapper phil-green"><el-icon><Shield /></el-icon></div>
            <h3>实名认证<br>安全可靠</h3>
          </div>
          <div class="phil-card">
            <div class="phil-icon-wrapper phil-pink"><el-icon><Star /></el-icon></div>
            <h3>心动匹配<br>结交知己</h3>
          </div>
          <div class="phil-card">
            <div class="phil-icon-wrapper phil-orange"><el-icon><Calendar /></el-icon></div>
            <h3>活动广场<br>丰富生活</h3>
          </div>
        </div>
      </div>

      <!-- Technology -->
      <div class="tech-section">
        <h2 class="section-title">核心功能</h2>
        <div class="tech-grid">
          <div class="tech-card">
            <div class="tech-img tech-bg-1">
              <el-icon><Promotion /></el-icon>
            </div>
            <p>动态漫游</p>
          </div>
          <div class="tech-card">
            <div class="tech-img tech-bg-2">
               <el-icon><UserFriends /></el-icon>
            </div>
            <p>校园圈子</p>
          </div>
          <div class="tech-card">
            <div class="tech-img tech-bg-3">
               <el-icon><Grid /></el-icon>
            </div>
            <p>模块化聚合</p>
          </div>
        </div>
      </div>
    </section>



    <!-- 登录弹窗 (取代原本的固定登录表单) -->
    <el-dialog 
      v-model="showLoginDialog" 
      width="440px" 
      :show-close="false"
      class="custom-login-dialog"
      align-center
    >
      <div class="glass-card">
        <div class="close-btn" @click="showLoginDialog = false">
          <el-icon><Close /></el-icon>
        </div>
        <div class="brand-header">
          <div class="logo-wrapper-small">
            <div class="logo-core-small">
              <el-icon class="logo-icon-small"><Platform /></el-icon>
            </div>
          </div>
          <h2 class="dialog-title">欢迎登录</h2>
          <p class="dialog-subtitle">连接你的校园生活</p>
        </div>

        <el-form :model="form" class="login-form">
          <el-form-item class="type-switch">
            <el-radio-group v-model="form.loginType">
              <el-radio-button value="USER">学生登录</el-radio-button>
              <el-radio-button value="ADMIN">管理员</el-radio-button>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item class="field">
            <el-input
              v-model="form.username"
              :placeholder="form.loginType === 'ADMIN' ? '请输入管理员账号' : '请输入用户名 / 学号'"
              :prefix-icon="User"
              size="large"
              clearable
              class="custom-input"
            />
          </el-form-item>
          
          <el-form-item class="field">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
              clearable
              class="custom-input"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          
          <div class="form-actions">
            <el-checkbox v-model="rememberMe" class="remember-checkbox">记住我</el-checkbox>
            <el-link type="primary" underline="never" class="forgot-link">忘记密码?</el-link>
          </div>

          <el-button type="primary" class="login-btn" size="large" @click="handleLogin" :loading="loading">
            {{ form.loginType === 'ADMIN' ? '进入后台' : '登录校园空间' }}
            <el-icon class="btn-icon"><ArrowRight /></el-icon>
          </el-button>
        </el-form>

        <div class="dialog-footer">
          <span class="dialog-footer-text">还没有账号？</span>
          <el-link type="primary" underline="never" class="register-link" @click="goToRegister">立即注册加入我们</el-link>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Platform, ChatDotRound, Postcard as Shield, Star, Calendar, 
  Promotion, User as UserFriends, Grid, Opportunity, 
  Close, User, Lock, ArrowRight 
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { loginApi } from '../api/user'

const router = useRouter()

const showLoginDialog = ref(false)

defineExpose({
  showLoginDialog
})

const isScrolled = ref(false)
const rememberMe = ref(false)
const loading = ref(false)

const form = reactive({
  loginType: 'USER',
  username: '',
  password: ''
})

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const handleLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  loading.value = true
  try {
    const { data } = await loginApi(form)
    if (!data.success) {
      ElMessage.error(data.message || '登录失败')
      return
    }

    if (form.loginType === 'ADMIN' && data.data.role !== 'ADMIN') {
      ElMessage.error('该账号不是管理员，请切换为学生登录')
      return
    }

    if (form.loginType === 'USER' && data.data.role === 'ADMIN') {
      ElMessage.error('该账号是管理员，请切换为管理员登录')
      return
    }

    localStorage.setItem('currentUser', JSON.stringify(data.data))
    ElMessage.success({
      message: '欢迎回来，登录成功！',
      duration: 2000
    })

    showLoginDialog.value = false

    if (data.data.role === 'ADMIN') {
      router.push('/admin')
    } else {
      router.push('/home')
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '登录异常，请稍后重试')
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  showLoginDialog.value = false
  router.push('/register')
}
</script>

<style scoped>
.landing-page {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background-color: #fafcff;
  min-height: 100vh;
}

/* Navbar */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 80px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 48px;
  z-index: 100;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.navbar.scrolled {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.logo-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-core {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
  transition: all 0.3s;
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  transition: all 0.3s;
}

.navbar.scrolled .logo-core {
  background: linear-gradient(135deg, #f8b1c4 0%, #9edcf8 100%);
  color: #fff;
}

.navbar.scrolled .logo-text {
  color: #1e293b;
}

.nav-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

.nav-btn-text {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  transition: color 0.3s;
}

.nav-btn-text:hover {
  color: rgba(255, 255, 255, 0.8) !important;
  background: transparent !important;
}

.navbar.scrolled .nav-btn-text {
  color: #64748b;
}

.navbar.scrolled .nav-btn-text:hover {
  color: #1e293b !important;
}

.nav-btn-primary {
  color: #9edcf8;
  background: #fff;
  border: none;
  font-weight: 600;
  padding: 10px 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.nav-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.navbar.scrolled .nav-btn-primary {
  background: linear-gradient(135deg, #f8b1c4 0%, #9edcf8 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(158, 220, 248, 0.3);
}

/* Hero Section */
.hero-section {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, #f8b1c4 0%, #9edcf8 100%);
  text-align: center;
  position: relative;
}

.hero-content {
  margin-top: -40px;
}

.hero-title {
  font-size: 80px;
  font-weight: 300;
  color: #8fd5ea;
  margin: 0;
  letter-spacing: 2px;
  text-shadow: 0 4px 20px rgba(255, 255, 255, 0.2);
}

.hero-subtitle {
  font-size: 20px;
  color: #ffffff;
  margin: 16px 0 40px;
  font-weight: 500;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.heart-container {
  margin: 40px 0 60px;
  animation: pulse-heart 2.5s infinite ease-in-out;
}

@keyframes pulse-heart {
  0% { transform: scale(1); }
  50% { transform: scale(1.08); }
  100% { transform: scale(1); }
}

.heart-icon {
  font-size: 140px;
  color: #ffffff;
  filter: drop-shadow(0 10px 20px rgba(0, 0, 0, 0.1));
}

.sync-btn {
  width: 200px;
  height: 54px;
  font-size: 18px;
  color: #8fd5ea;
  font-weight: 600;
  background: #ffffff;
  border: none;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.sync-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
  color: #f8b1c4;
}

/* Features Section */
.features-section {
  background: #fafcff;
  padding: 120px 48px;
}

.feature-banner {
  max-width: 1200px;
  margin: 0 auto 120px;
  display: flex;
  align-items: center;
  gap: 80px;
}

.banner-text {
  flex: 1;
}

.banner-title {
  font-size: 64px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1.1;
  margin-bottom: 24px;
  letter-spacing: -1px;
}

.banner-desc {
  font-size: 20px;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 48px;
  font-weight: 400;
}

.learn-more-btn {
  background: #1e293b;
  color: #fff;
  border: none;
  padding: 12px 36px;
  font-size: 18px;
  height: 54px;
  transition: all 0.3s;
}

.learn-more-btn:hover {
  background: #334155;
  transform: translateY(-2px);
}

.banner-image {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}

.mock-image {
  width: 100%;
  max-width: 480px;
  aspect-ratio: 4/5;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  border-radius: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 30px 60px rgba(30, 41, 59, 0.08);
}

.mock-icon {
  font-size: 120px;
  color: #cbd5e1;
}

/* Philosophy Section */
.philosophy-section {
  max-width: 1200px;
  margin: 0 auto 100px;
}

.section-title {
  font-size: 36px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 16px;
}

.section-subtitle {
  font-size: 18px;
  color: #64748b;
  margin-bottom: 56px;
  max-width: 600px;
  line-height: 1.6;
}

.philosophy-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.phil-card {
  background: #fff;
  border-radius: 24px;
  padding: 36px 28px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.03);
  transition: transform 0.3s, box-shadow 0.3s;
  border: 1px solid #f1f5f9;
}

.phil-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.06);
}

.phil-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-bottom: 28px;
}

.phil-blue { background: #eff6ff; color: #3b82f6; }
.phil-green { background: #ecfdf5; color: #10b981; }
.phil-pink { background: #fdf2f8; color: #ec4899; }
.phil-orange { background: #fff7ed; color: #f97316; }

.phil-card h3 {
  font-size: 20px;
  color: #1e293b;
  line-height: 1.5;
  margin: 0;
  font-weight: 600;
}

/* Tech Section */
.tech-section {
  max-width: 1200px;
  margin: 0 auto;
}

.tech-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
  margin-top: 48px;
}

.tech-card p {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin-top: 24px;
}

.tech-img {
  width: 100%;
  aspect-ratio: 16/10;
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 64px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
  overflow: hidden;
}

.tech-bg-1 { background: #f0fdf4; color: #4ade80; }
.tech-bg-2 { background: #eff6ff; color: #60a5fa; }
.tech-bg-3 { background: #fdf4ff; color: #e879f9; }

.tech-card:hover .tech-img {
  transform: scale(1.02);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08);
}



/* Custom Dialog */
::deep(.custom-login-dialog) {
  background: transparent !important;
  box-shadow: none !important;
}

::deep(.custom-login-dialog .el-dialog__header) {
  display: none;
}

::deep(.custom-login-dialog .el-dialog__body) {
  padding: 0;
}

.glass-card {
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.1);
  padding: 48px 40px 40px;
  position: relative;
}

.close-btn {
  position: absolute;
  top: 24px;
  right: 24px;
  font-size: 22px;
  color: #94a3b8;
  cursor: pointer;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #1e293b;
}

.brand-header {
  text-align: center;
  margin-bottom: 36px;
}

.logo-wrapper-small {
  margin: 0 auto 20px;
  display: flex;
  justify-content: center;
}

.logo-core-small {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #f8b1c4 0%, #9edcf8 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12px 24px rgba(158, 220, 248, 0.4);
}

.logo-icon-small {
  font-size: 28px;
  color: #fff;
}

.dialog-title {
  font-size: 26px;
  font-weight: 700;
  color: #1a2b3c;
  margin: 0 0 8px;
}

.dialog-subtitle {
  font-size: 15px;
  color: #64748b;
  margin: 0;
}

/* Form Inner Styles */
.login-form {
  margin-top: 10px;
}

.type-switch {
  margin-bottom: 24px;
}

::deep(.el-radio-group) {
  display: flex;
  width: 100%;
  background: #f1f5f9;
  padding: 4px;
  border-radius: 12px;
}

::deep(.el-radio-button) {
  flex: 1;
}

::deep(.el-radio-button__inner) {
  width: 100%;
  border: none !important;
  border-radius: 10px !important;
  background: transparent;
  color: #64748b;
  font-weight: 500;
  box-shadow: none !important;
  transition: all 0.3s;
}

::deep(.el-radio-button__inner:hover) {
  color: #9edcf8;
}

::deep(.el-radio-button.is-active .el-radio-button__inner) {
  background: #fff;
  color: #0f172a;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08) !important;
}

.field {
  margin-bottom: 20px;
}

::deep(.custom-input .el-input__wrapper) {
  background: #f8fafc;
  border-radius: 12px;
  box-shadow: 0 0 0 1px #e2e8f0 inset;
  padding: 4px 14px;
  transition: all 0.3s ease;
}

::deep(.custom-input .el-input__wrapper.is-focus),
::deep(.custom-input .el-input__wrapper:hover) {
  background: #fff;
  box-shadow: 0 0 0 2px #9edcf8 inset;
}

::deep(.custom-input .el-input__inner) {
  height: 44px;
  font-size: 15px;
}

::deep(.el-input__prefix-inner) {
  color: #94a3b8;
  font-size: 18px;
  margin-right: 4px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  margin-top: -8px;
}

.remember-checkbox {
  color: #64748b;
}

.forgot-link {
  font-size: 14px;
  color: #64748b;
}

.forgot-link:hover {
  color: #9edcf8;
}

.login-btn {
  width: 100%;
  height: 52px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 600;
  border: none;
  background: linear-gradient(135deg, #f8b1c4 0%, #9edcf8 100%);
  color: white;
  box-shadow: 0 10px 20px -10px rgba(158, 220, 248, 0.6);
  transition: all 0.3s ease;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 24px -10px rgba(158, 220, 248, 0.8);
}

.btn-icon {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.login-btn:hover .btn-icon {
  transform: translateX(4px);
}

.dialog-footer {
  margin-top: 32px;
  text-align: center;
  font-size: 14px;
}

.dialog-footer-text {
  color: #64748b;
  margin-right: 8px;
}

.register-link {
  font-weight: 600;
  color: #8fd5ea;
}

.register-link:hover {
  color: #f8b1c4;
}
</style>
