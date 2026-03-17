<template>
  <div class="page">
    <!-- Animated background elements -->
    <div class="bg-shape shape-1"></div>
    <div class="bg-shape shape-2"></div>
    <div class="bg-shape shape-3"></div>

    <div class="glass-container">
      <el-card class="glass-card">
        <div class="brand-header">
          <div class="logo-wrapper">
            <div class="logo-core">
              <el-icon class="logo-icon"><Avatar /></el-icon>
            </div>
            <div class="logo-ring ring-1"></div>
            <div class="logo-ring ring-2"></div>
          </div>
          <h2 class="title">加入 CSocial</h2>
          <p class="subtitle">创建你的校园专属账号</p>
        </div>

        <el-form :model="form" class="register-form" label-position="top">
          <div class="form-row">
            <el-form-item class="field half-width">
              <el-input 
                v-model="form.username" 
                placeholder="用户名 / 学号" 
                :prefix-icon="User"
                size="large"
                clearable 
                class="custom-input"
              />
            </el-form-item>
            <el-form-item class="field half-width">
              <el-input 
                v-model="form.nickname" 
                placeholder="你的昵称" 
                :prefix-icon="EditPen"
                size="large"
                class="custom-input"
              />
            </el-form-item>
          </div>

          <el-form-item class="field">
            <el-input 
              v-model="form.email" 
              placeholder="常用邮箱（可选）" 
              :prefix-icon="Message"
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item class="field">
            <el-input 
              v-model="form.password" 
              type="password" 
              show-password 
              placeholder="设置密码" 
              :prefix-icon="Lock"
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item class="field">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              show-password
              placeholder="确认密码"
              :prefix-icon="CircleCheck"
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <el-button type="primary" class="register-btn" size="large" @click="handleRegister" :loading="loading">
            立即注册
            <el-icon class="btn-icon"><ArrowRight /></el-icon>
          </el-button>
        </el-form>

        <div class="footer">
          <span class="footer-text">已有账号？</span>
          <el-link type="primary" underline="never" class="login-link" @click="$router.push('/login')">返回登录</el-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, Avatar, Message, EditPen, CircleCheck, ArrowRight } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { registerApi } from '../api/user'

const router = useRouter()

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: ''
})

const loading = ref(false)

const handleRegister = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('用户名和密码不能为空')
    return
  }

  if (form.password !== form.confirmPassword) {
    ElMessage.warning('两次密码输入不一致，请检查')
    return
  }

  loading.value = true
  try {
    const { data } = await registerApi({
      username: form.username,
      password: form.password,
      nickname: form.nickname,
      email: form.email
    })
    if (!data.success) {
      ElMessage.error(data.message || '注册失败')
      return
    }
    ElMessage.success({
      message: '账号创建成功，请登录！',
      duration: 2000
    })
    router.push('/login')
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '注册异常，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fdf2f8;
  background-image:

                    radial-gradient(135deg, #f8b1c4 0%, #9edcf8 100%);
  position: relative;
  overflow: hidden;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  padding: 20px 0;
}

/* Animated Background Shapes */
.bg-shape {
  position: absolute;
  filter: blur(60px);
  z-index: 0;
  opacity: 0.6;
  animation: float 10s infinite ease-in-out alternate;
}

.shape-1 {
  width: 450px;
  height: 450px;
  background: rgba(164, 232, 253, 0.58);
  border-radius: 50%;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.shape-2 {
  width: 550px;
  height: 550px;
  background: rgba(164, 232, 253, 0.58);
  border-radius: 50%;
  bottom: -150px;
  right: -100px;
  animation-delay: -3s;
}

.shape-3 {
  width: 350px;
  height: 350px;
  background: #fce7f3;
  border-radius: 70%;
  top: 40%;
  left: 60%;
  animation-delay: -6s;
}

@keyframes float {
  0% {
    transform: translate(0, 0) scale(1);
  }
  100% {
    transform: translate(30px, -50px) scale(1.1);
  }
}

.glass-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 520px;
  padding: 20px;
  animation: slideUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.glass-card {
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.7);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: 0 24px 48px -12px rgba(131, 24, 67, 0.15),
              0 0 0 1px rgba(255, 255, 255, 0.5) inset;
  overflow: hidden;
}

.glass-card :deep(.el-card__body) {
  padding: 40px 48px;
}

/* Brand Header */
.brand-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-wrapper {
  position: relative;
  width: 72px;
  height: 72px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-core {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #f8b1c4 0%, #9edcf8 100%) 0%, #f8b1c4 100%);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12px 24px rgba(255, 117, 140, 0.4);
  z-index: 2;
  transform: rotate(10deg);
  transition: transform 0.3s ease;
}

.logo-wrapper:hover .logo-core {
  transform: rotate(0deg) scale(1.05);
}

.logo-icon {
  font-size: 28px;
  color: #fff;
}

.logo-ring {
  position: absolute;
  border-radius: 22px;
  border: 2px solid rgba(255, 117, 140, 0.3);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
}

.ring-1 {
  width: 68px;
  height: 68px;
  animation: pulse 2.5s infinite ease-in-out;
}

.ring-2 {
  width: 82px;
  height: 82px;
  border-color: rgba(255, 117, 140, 0.15);
  animation: pulse 2.5s infinite ease-in-out 0.5s;
}

@keyframes pulse {
  0% { transform: translate(-50%, -50%) scale(0.8); opacity: 1; }
  100% { transform: translate(-50%, -50%) scale(1.2); opacity: 0; }
}

.title {
  font-size: 26px;
  font-weight: 700;
  color: #4c1d95;
  margin: 0 0 8px;
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
}

/* Form Styles */
.register-form {
  margin-top: 10px;
}

.form-row {
  display: flex;
  gap: 16px;
}

.half-width {
  flex: 1;
}

.field {
  margin-bottom: 20px;
}

::deep(.custom-input .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.7);
  border-radius: 12px;
  box-shadow: 0 0 0 1px rgba(244, 114, 182, 0.3) inset;
  padding: 4px 14px;
  transition: all 0.3s ease;
}

::deep(.custom-input .el-input__wrapper.is-focus),
::deep(.custom-input .el-input__wrapper:hover) {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 0 0 2px #ec4899 inset;
}

::deep(.custom-input .el-input__inner) {
  height: 44px;
  font-size: 15px;
}

::deep(.el-input__prefix-inner) {
  color: #f472b6;
  font-size: 18px;
  margin-right: 4px;
}

.register-btn {
  width: 100%;
  height: 52px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 600;
  border: none;
  background: linear-gradient(135deg, #f8b1c4 0%, #9edcf8 100%));
  color: white;
  box-shadow: 0 10px 20px -10px rgba(255, 117, 140, 0.6);
  transition: all 0.3s ease;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 24px -10px rgba(255, 117, 140, 0.8);
}

.register-btn:active {
  transform: translateY(0);
}

.btn-icon {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.register-btn:hover .btn-icon {
  transform: translateX(4px);
}

/* Footer Styles */
.footer {
  margin-top: 32px;
  text-align: center;
  font-size: 14px;
}

.footer-text {
  color: #6b7280;
  margin-right: 8px;
}

.login-link {
  font-weight: 600;
  color: #ec4899;
}

.login-link:hover {
  color: #db2777;
}
</style>
