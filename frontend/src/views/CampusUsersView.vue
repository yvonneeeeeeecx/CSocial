<template>
  <div class="users-view">
    <div class="view-header">
      <h2>校园用户列表</h2>
      <p>认识新朋友</p>
    </div>

    <div class="content-wrapper">
      <div class="filter-bar">
        <el-input 
          v-model="keyword" 
          placeholder="搜索昵称、专业、学院..." 
          prefix-icon="Search"
          size="large"
          class="search-input"
          clearable
        />
      </div>

      <div class="users-grid">
        <el-card v-for="user in filteredUsers" :key="user.id" class="user-card">
          <div class="user-avatar-area" @click="goProfile(user)">
            <el-avatar :size="64" :src="user.avatar">{{ (user.nickname || user.username || 'U')[0] }}</el-avatar>
          </div>
          <h3 class="user-name">{{ user.nickname || user.username }}</h3>
          <p class="user-meta">{{ user.college || '未知学院' }} · {{ user.major || '未知专业' }}</p>
          
          <div class="user-tags">
            <el-tag v-if="user.isMutual" size="small" type="success">互相关注</el-tag>
            <el-tag v-else-if="user.isFollowing" size="small" type="info">已关注</el-tag>
          </div>

          <div class="user-actions">
            <el-button type="primary" size="small" round @click="handleFollow(user)">
              {{ user.isFollowing ? '取消关注' : '关注' }}
            </el-button>
            <el-button size="small" round @click="openDm(user)">私信</el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { listCampusUsersApi } from '../api/user'
import { followUserApi, unfollowUserApi, getFollowingUsersApi, getMutualFollowApi } from '../api/relation'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
const users = ref([])
const keyword = ref('')
const followingIds = ref([])
const mutualIds = ref([])

const filteredUsers = computed(() => {
  if (!keyword.value) return users.value
  const k = keyword.value.toLowerCase()
  return users.value.filter(u => 
    (u.nickname || '').toLowerCase().includes(k) ||
    (u.username || '').toLowerCase().includes(k) ||
    (u.college || '').toLowerCase().includes(k) ||
    (u.major || '').toLowerCase().includes(k)
  )
})

onMounted(async () => {
  await loadRelations()
  await loadUsers()
})

async function loadRelations() {
  if (!currentUser.id) return
  const [fRes, mRes] = await Promise.all([
    getFollowingUsersApi(currentUser.id),
    getMutualFollowApi(currentUser.id)
  ])
  followingIds.value = fRes.data?.data || []
  mutualIds.value = mRes.data?.data || []
}

async function loadUsers() {
  const { data } = await listCampusUsersApi({ userId: currentUser.id })
  users.value = (data?.data || []).map(u => ({
    ...u,
    isFollowing: followingIds.value.includes(u.id),
    isMutual: mutualIds.value.includes(u.id)
  }))
}

async function handleFollow(user) {
  if (user.isFollowing) {
    await unfollowUserApi(currentUser.id, user.id)
    user.isFollowing = false
  } else {
    await followUserApi(currentUser.id, user.id)
    user.isFollowing = true
  }
}

function goProfile(user) {
  router.push(`/user/${user.id}`)
}

function openDm(user) {
  // Simple redirect for now or open dialog
  // Ideally use a global DM store or event
  alert(`功能开发中: 发私信给 ${user.username}`)
}
</script>

<style scoped>
.users-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 100px 20px 40px;
  animation: fadeIn 0.5s ease;
}

.view-header { text-align: center; margin-bottom: 32px; }

.filter-bar {
  max-width: 400px;
  margin: 0 auto 40px;
}

.users-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 14px;
}

.user-card {
  text-align: center;
  border-radius: 14px;
  padding: 14px 10px;
}

.user-avatar-area {
  cursor: pointer;
  margin-bottom: 12px;
  transition: transform 0.2s;
}

.user-avatar-area:hover {
  transform: scale(1.05);
}

.user-name {
  margin: 0 0 4px;
  font-size: 14px;
  font-weight: 600;
}

.user-meta {
  font-size: 11px;
  color: #666;
  margin-bottom: 10px;
}

.user-tags {
  height: 22px;
  margin-bottom: 10px;
}

.user-actions {
  display: flex;
  justify-content: center;
  gap: 6px;
}

@media (max-width: 1200px) {
  .users-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

@media (max-width: 992px) {
  .users-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .users-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 480px) {
  .users-grid {
    grid-template-columns: 1fr;
  }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>