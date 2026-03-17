<template>
  <div class="home-landing">
    <section class="hero">
      <div class="hero-content">
        <h1>CSocial</h1>
        <p>连接校园生活，发现同频伙伴</p>
        <el-button class="hero-btn" round @click="scrollToFeatures">探索功能</el-button>
      </div>
    </section>

    <section class="features" ref="featuresRef">
      <div class="section-header">
        <h2>核心功能</h2>
        <p>下滑浏览，点击即可进入对应功能页面</p>
      </div>

      <div class="feature-grid">
        <div class="feature-card" v-for="item in featureItems" :key="item.path" @click="go(item.path)">
          <div class="feature-title">{{ item.title }}</div>
          <div class="feature-desc">{{ item.desc }}</div>
          <div class="feature-go">进入 →</div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const featuresRef = ref(null)

const featureItems = [
  { title: '校园铃', desc: '位置共享与心动匹配', path: '/alarm' },
  { title: '校园信息服务', desc: '公告、活动、二手、失物招领', path: '/campus-services' },
  { title: '校园动态', desc: '发布内容，参与热议', path: '/feed' },
  { title: '社群圈子', desc: '加入兴趣社群，组织活动', path: '/community' },
  { title: '校友列表', desc: '发现同学，建立联系', path: '/campus-users' }
]

const scrollToFeatures = () => {
  featuresRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const go = (path) => {
  router.push(path)
}
</script>

<style scoped>
.home-landing {
  min-height: 100vh;
  background: #fff;
}

.hero {
  min-height: 86vh;
  padding-top: 74px;
  background: linear-gradient(180deg, #f9fbff 0%, #f4f7fb 48%, #ffffff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #131824;
  border-bottom: 1px solid #eef1f6;
}

.hero-content {
  text-align: center;
  transform: translateY(-20px);
}

.hero-content h1 {
  margin: 0;
  font-size: 72px;
  letter-spacing: 2px;
}

.hero-content p {
  margin: 14px 0 28px;
  font-size: 22px;
  color: #5f6673;
}

.hero-btn {
  height: 44px;
  padding: 0 28px;
  border: 1px solid #dde3ec;
  background: #fff;
  color: #1a2233;
  font-weight: 700;
}

.hero-btn:hover {
  background: #f6f9fd;
}

.features {
  max-width: 1200px;
  margin: 0 auto;
  padding: 34px 20px 56px;
}

.section-header h2 {
  margin: 0;
  font-size: 40px;
  color: #121827;
}

.section-header p {
  margin: 8px 0 30px;
  color: #68707d;
  font-size: 16px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.feature-card {
  border: 1px solid #eceff5;
  border-radius: 16px;
  padding: 22px;
  background: #fff;
  cursor: pointer;
  transition: all 0.22s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 16px 30px rgba(36, 50, 77, 0.08);
}

.feature-title {
  font-size: 22px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 8px;
}

.feature-desc {
  color: #5f6673;
  min-height: 46px;
  line-height: 1.5;
}

.feature-go {
  margin-top: 16px;
  color: #111827;
  font-weight: 700;
}

@media (max-width: 1024px) {
  .feature-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .hero-content h1 {
    font-size: 58px;
  }
}

@media (max-width: 768px) {
  .hero {
    min-height: 72vh;
    padding-top: 64px;
  }

  .hero-content h1 {
    font-size: 44px;
  }

  .hero-content p {
    font-size: 16px;
  }

  .feature-grid {
    grid-template-columns: 1fr;
  }

  .section-header h2 {
    font-size: 30px;
  }
}
</style>
