<template>
  <div class="alarm-view">
    <div class="view-header">
      <h2>校园铃 & 互动</h2>
      <p>发现身边的缘分，开启即时互动</p>
    </div>

    <div class="view-content">
      <el-row :gutter="24">
        <!-- 校园铃设置 -->
        <el-col :span="10">
          <el-card class="feature-card alarm-panel">
            <template #header>
              <div class="card-header">
                <el-icon class="header-icon"><Bell /></el-icon>
                <span>校园铃设置</span>
              </div>
            </template>
            
            <div class="alarm-status">
              <div class="status-circle" :class="{ active: alarmEnabled }">
                <el-icon><MapLocation /></el-icon>
              </div>
              <h3>{{ alarmEnabled ? '正在扫描附近...' : '校园铃已关闭' }}</h3>
              <p>当 100m 范围内有心动对象时触发提醒</p>
            </div>

            <el-form label-position="top" class="alarm-form">
              <el-row :gutter="12">
                <el-col :span="12">
                  <el-form-item label="当前纬度">
                    <el-input v-model.number="alarmForm.latitude" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="当前经度">
                    <el-input v-model.number="alarmForm.longitude" />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <div class="switch-row">
                <span>启用功能</span>
                <el-switch v-model="alarmEnabled" @change="toggleAlarmEnabled" />
              </div>
              
              <el-button type="primary" class="action-btn trigger-btn" @click="triggerRing" :loading="scanning">
                立即扫描周边
              </el-button>
            </el-form>

            <div class="ring-result" v-if="ringResult">
              <div class="result-item">
                <span class="label">附近互动人数</span>
                <span class="value">{{ ringResult.nearbyLikeCount || 0 }}</span>
              </div>
              <div class="result-item">
                <span class="label">匹配成功人数</span>
                <span class="value highlight">{{ ringResult.matchedNearbyCount || 0 }}</span>
              </div>
              <p class="result-msg">{{ ringResult.message }}</p>
            </div>
          </el-card>
        </el-col>

        <!-- 互动操作 -->
        <el-col :span="14">
          <el-card class="feature-card interaction-panel">
            <template #header>
              <div class="card-header">
                <el-icon class="header-icon"><Pointer /></el-icon>
                <span>主动出击</span>
              </div>
            </template>

            <el-form label-position="top">
              <el-form-item label="选择互动对象">
                <el-select 
                  v-model="interactionUserId" 
                  filterable 
                  remote
                  :remote-method="searchUsers"
                  :loading="searchLoading"
                  placeholder="搜索并选择校园用户..." 
                  size="large"
                  style="width: 100%;"
                >
                  <el-option
                    v-for="item in userOptions"
                    :key="item.userId"
                    :label="getAuthorName(item)"
                    :value="item.userId"
                  >
                    <span style="float: left">{{ getAuthorName(item) }}</span>
                    <span style="float: right; color: #8492a6; font-size: 13px">{{ item.college }}</span>
                  </el-option>
                </el-select>
              </el-form-item>
              
              <div class="interaction-actions">
                <el-button type="primary" size="large" @click="interactTargetUser" :disabled="!interactionUserId">
                  发送心动信号
                </el-button>
                <el-button size="large" @click="loadMatches">刷新匹配列表</el-button>
              </div>
            </el-form>

            <div class="matches-section">
              <h4>我的匹配 <span class="badge" v-if="matchUsers.length">{{ matchUsers.length }}</span></h4>
              <el-empty v-if="matchUsers.length === 0" description="暂无匹配，快去互动吧" :image-size="80" />
              <div v-else class="match-grid">
                <div v-for="user in matchUsers" :key="user.id" class="match-card" @click="$router.push(`/user/${user.id}`)">
                  <el-avatar :size="48" :src="user.avatar">{{ (user.nickname || user.username || 'U')[0] }}</el-avatar>
                  <span class="match-name">{{ user.nickname || user.username }}</span>
                  <el-button size="small" round>私信</el-button>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Bell, MapLocation, Pointer } from '@element-plus/icons-vue'
import { ringApi } from '../api/alarm'
import { updateAlarmSettingApi, listCampusUsersApi, getUserApi } from '../api/user'
import { likeUserApi, getMatchedUsersApi } from '../api/relation'

const user = JSON.parse(localStorage.getItem('currentUser') || '{}')
const alarmEnabled = ref(user?.alarmEnabled ?? true)
const alarmForm = reactive({
  latitude: 39.9042,
  longitude: 116.4074
})
const ringResult = ref(null)
const scanning = ref(false)

const interactionUserId = ref(null)
const userOptions = ref([])
const searchLoading = ref(false)
const matchUsers = ref([])

onMounted(() => {
  loadMatches()
  searchUsers('') // initial load
})

const toggleAlarmEnabled = async (value) => {
  if (!user?.id) return
  await updateAlarmSettingApi(user.id, { alarmEnabled: value })
  alarmEnabled.value = !!value
  const newUser = { ...user, alarmEnabled: alarmEnabled.value }
  localStorage.setItem('currentUser', JSON.stringify(newUser))
  ElMessage.success(alarmEnabled.value ? '校园铃已开启' : '校园铃已关闭')
}

const triggerRing = async () => {
  if (!user?.id) return
  scanning.value = true
  try {
    const { data } = await ringApi({
      userId: user.id,
      latitude: alarmForm.latitude,
      longitude: alarmForm.longitude
    })
    ringResult.value = data
    ElMessage.success(data.message || '已触发扫描')
  } catch (e) {
    ElMessage.error('触发失败')
  } finally {
    scanning.value = false
  }
}

const searchUsers = async (query) => {
  if (!user?.id) return
  searchLoading.value = true
  try {
    // Ideally backend supports search, here we fetch all and filter locally for demo
    const { data } = await listCampusUsersApi({ userId: user.id })
    const list = data?.data || []
    if (!query) {
      userOptions.value = list.slice(0, 20)
    } else {
      userOptions.value = list.filter(u => {
        const name = u.nickname || u.username || ''
        return name.toLowerCase().includes(query.toLowerCase())
      })
    }
  } finally {
    searchLoading.value = false
  }
}

const getAuthorName = (item) => {
  if (!item) return '用户'
  return item.nickname || item.username || (item.userId ? `用户${item.userId}` : '用户')
}

const interactTargetUser = async () => {
  if (!interactionUserId.value) return
  try {
    const { data } = await likeUserApi(user.id, interactionUserId.value)
    ElMessage.success(data.message || '操作成功')
    loadMatches()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const loadMatches = async () => {
  if (!user?.id) return
  try {
    const { data } = await getMatchedUsersApi(user.id)
    const matchedIds = data?.data || []
    if (matchedIds.length > 0) {
      const userPromises = matchedIds.map(async (userId) => {
        try {
          const { data: userResp } = await getUserApi(userId)
          return userResp?.data || null
        } catch {
          return null
        }
      })
      matchUsers.value = (await Promise.all(userPromises)).filter(Boolean)
    } else {
      matchUsers.value = []
    }
  } catch (e) {
    console.error(e)
  }
}
</script>

<style scoped>
.alarm-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 100px 20px 40px; /* Top padding for fixed nav */
  animation: fadeIn 0.5s ease;
}

.view-header {
  margin-bottom: 32px;
  text-align: center;
}

.view-header h2 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
}

.view-header p {
  color: #666;
  font-size: 16px;
}

.feature-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 16px;
}

.alarm-status {
  text-align: center;
  padding: 24px 0;
  background: #f9fafb;
  border-radius: 12px;
  margin-bottom: 24px;
}

.status-circle {
  width: 64px;
  height: 64px;
  background: #e5e7eb;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  font-size: 24px;
  color: #9ca3af;
  transition: all 0.5s;
}

.status-circle.active {
  background: #fee2e2;
  color: #ef4444;
  box-shadow: 0 0 0 8px #fef2f2;
}

.alarm-status h3 {
  margin: 0 0 4px;
  font-size: 18px;
}

.alarm-status p {
  margin: 0;
  color: #6b7280;
  font-size: 13px;
}

.switch-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 16px 0 24px;
  padding: 12px;
  background: #f3f4f6;
  border-radius: 8px;
}

.trigger-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
}

.ring-result {
  margin-top: 24px;
  padding: 16px;
  background: #eff6ff;
  border-radius: 12px;
  border: 1px solid #dbeafe;
}

.result-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.value.highlight {
  color: #2563eb;
  font-weight: 700;
}

.result-msg {
  margin: 12px 0 0;
  font-size: 13px;
  color: #3b82f6;
  text-align: center;
}

.interaction-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 32px;
}

.matches-section h4 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.badge {
  background: #ef4444;
  color: #fff;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.match-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 16px;
}

.match-card {
  background: #f9fafb;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.match-card:hover {
  background: #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  transform: translateY(-2px);
}

.match-name {
  font-weight: 600;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>