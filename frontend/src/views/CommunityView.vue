<template>
  <div class="community-view">
    <div class="view-header">
      <h2>社群管理</h2>
      <p>加入兴趣圈子，找到志同道合的伙伴</p>
    </div>

    <div class="tabs-container">
      <el-tabs v-model="activeTab" class="custom-tabs">
        <el-tab-pane label="社群广场" name="all">
          <div class="tab-content">
             <div class="toolbar">
               <el-button type="primary" @click="dialogVisible = true">创建新社群</el-button>
             </div>
             <div class="community-grid">
               <div v-for="item in communities" :key="item.id" class="community-card">
                 <div class="card-top">
                   <div class="category-badge">{{ item.category }}</div>
                   <h3 class="card-title">{{ item.name }}</h3>
                   <p class="card-desc">{{ item.description }}</p>
                 </div>
                 <div class="card-bottom">
                   <div class="stats">
                     <span>{{ item.memberCount }} 成员</span>
                   </div>
                   <el-button 
                     v-if="!joinedIds.has(item.id) && item.creatorId !== user.id" 
                     type="primary" 
                     round 
                     size="small"
                     @click="joinCommunity(item)"
                   >
                     加入
                   </el-button>
                   <el-button v-else disabled round size="small">{{ item.creatorId === user.id ? '我是群主' : '已加入' }}</el-button>
                 </div>
               </div>
             </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="我创建的" name="created">
          <div class="tab-content">
             <div class="community-grid">
               <div v-for="item in createdList" :key="item.id" class="community-card my-card">
                 <div class="card-top">
                   <h3 class="card-title">{{ item.name }}</h3>
                   <div class="role-tag owner">群主</div>
                 </div>
                 <div class="card-actions">
                   <el-button size="small" @click="manageCommunity(item)">管理成员</el-button>
                   <el-button size="small" type="primary" @click="postAnnouncement(item)">发布公告</el-button>
                 </div>
               </div>
             </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="我加入的" name="joined">
           <div class="tab-content">
             <div class="community-grid">
               <div v-for="item in joinedList" :key="item.id" class="community-card my-card">
                 <div class="card-top">
                   <h3 class="card-title">{{ item.name }}</h3>
                   <div class="role-tag member">成员</div>
                 </div>
                 <div class="card-actions">
                   <el-button size="small" @click="viewAnnouncements(item)">查看公告</el-button>
                   <el-button size="small" type="danger" link @click="leaveCommunity(item)">退出</el-button>
                 </div>
               </div>
             </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- Create Dialog -->
    <el-dialog v-model="dialogVisible" title="创建社群" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类">
           <el-select v-model="form.category">
             <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
           </el-select>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="审核">
           <el-radio-group v-model="form.joinApprovalRequired">
             <el-radio :label="true">需要</el-radio>
             <el-radio :label="false">不需要</el-radio>
           </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createCommunity">创建</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  listCommunitiesApi, listCreatedCommunitiesApi, listJoinedCommunitiesApi, 
  createCommunityApi, joinCommunityApi, leaveCommunityApi 
} from '../api/community'

const user = JSON.parse(localStorage.getItem('currentUser') || '{}')
const activeTab = ref('all')
const communities = ref([])
const createdList = ref([])
const joinedList = ref([])
const categories = ['运动', '学习', '娱乐', '生活', '科技']

const form = reactive({ name: '', description: '', category: '', joinApprovalRequired: true })
const dialogVisible = ref(false)

const joinedIds = computed(() => {
  const ids = new Set()
  createdList.value.forEach(i => ids.add(i.id))
  joinedList.value.forEach(i => ids.add(i.id))
  return ids
})

onMounted(() => {
  loadAll()
})

async function loadAll() {
  const p1 = listCommunitiesApi().then(res => communities.value = res.data.data || [])
  const p2 = listCreatedCommunitiesApi(user.id).then(res => createdList.value = res.data.data || [])
  const p3 = listJoinedCommunitiesApi(user.id).then(res => joinedList.value = res.data.data || [])
  await Promise.all([p1, p2, p3])
}

async function createCommunity() {
  await createCommunityApi({ ...form, creatorId: user.id })
  ElMessage.success('创建成功')
  dialogVisible.value = false
  loadAll()
}

async function joinCommunity(item) {
  await joinCommunityApi(item.id, user.id)
  ElMessage.success('已申请加入')
  loadAll()
}

async function leaveCommunity(item) {
  if(!confirm('确定退出?')) return
  await leaveCommunityApi(item.id, user.id)
  ElMessage.success('已退出')
  loadAll()
}

function manageCommunity(item) {
  ElMessage.info('功能开发中...')
}

function postAnnouncement(item) {
  ElMessage.info('功能开发中...')
}

function viewAnnouncements(item) {
  ElMessage.info('暂无公告')
}
</script>

<style scoped>
.community-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 100px 20px 40px;
  animation: fadeIn 0.5s ease;
}

.view-header { text-align: center; margin-bottom: 32px; }
.toolbar { margin-bottom: 24px; text-align: right; }

.community-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
}

.community-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 200px;
  transition: transform 0.2s;
}

.community-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.08);
}

.category-badge {
  font-size: 12px;
  color: #666;
  background: #f5f5f7;
  display: inline-block;
  padding: 4px 8px;
  border-radius: 6px;
  margin-bottom: 8px;
}

.card-title {
  margin: 0 0 8px;
  font-size: 18px;
}

.card-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats {
  font-size: 13px;
  color: #999;
}

.my-card {
  border-left: 4px solid #000;
}

.role-tag {
  display: inline-block;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  margin-top: 4px;
}

.role-tag.owner { background: #fee2e2; color: #b91c1c; }
.role-tag.member { background: #e0f2fe; color: #0369a1; }

.card-actions {
  display: flex;
  gap: 8px;
}

@media (max-width: 1024px) {
  .community-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .community-grid {
    grid-template-columns: 1fr;
  }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>