<template>
  <div class="services-view">
    <div class="view-header">
      <h2>校园信息服务</h2>
      <p>聚合公告、活动、二手与失物招领</p>
    </div>

    <div class="nav-tabs-wrapper">
      <div class="nav-tabs">
        <div 
          v-for="tab in tabs" 
          :key="tab.key" 
          class="nav-tab" 
          :class="{ active: currentTab === tab.key }"
          @click="currentTab = tab.key"
        >
          {{ tab.label }}
        </div>
      </div>
    </div>

    <div class="content-area">
      <!-- 公告 -->
      <div v-if="currentTab === 'announcement'" class="tab-content">
        <div class="toolbar">
          <el-button @click="loadAnnouncements">刷新列表</el-button>
        </div>
        <div class="card-grid">
          <el-card v-for="item in announcementList" :key="item.id" class="info-card">
            <div class="info-header">
              <span class="info-tag admin">官方公告</span>
              <span class="info-time">{{ formatDateTime(item.createdAt) }}</span>
            </div>
            <h3 class="info-title">{{ item.title }}</h3>
            <p class="info-desc">{{ item.content }}</p>
          </el-card>
        </div>
      </div>

      <!-- 活动 -->
      <div v-if="currentTab === 'activity'" class="tab-content">
        <div class="toolbar">
           <el-select v-model="activityCategoryFilter" placeholder="分类筛选" clearable style="width: 150px;">
              <el-option v-for="c in activityCategories" :key="c" :label="c" :value="c" />
            </el-select>
            <el-button type="primary" @click="activityDialogVisible = true">发布活动</el-button>
        </div>
        <div class="card-grid">
          <el-card v-for="item in activityList" :key="item.id" class="info-card">
            <div class="info-header">
              <span class="info-tag activity">{{ item.category }}</span>
              <span class="info-time">{{ formatDateTime(item.createdAt) }}</span>
            </div>
            <h3 class="info-title">{{ item.title }}</h3>
            <div class="info-meta-row">
              <el-icon><Location /></el-icon> {{ item.location || '线上' }}
            </div>
            <div class="info-meta-row">
              <el-icon><Timer /></el-icon> {{ formatDateTime(item.startTime) }}
            </div>
            <p class="info-desc">{{ item.content }}</p>
            <div class="card-footer" v-if="item.organizerId === user.id">
              <el-button link type="danger" @click="deleteActivity(item)">删除</el-button>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 二手 -->
      <div v-if="currentTab === 'secondhand'" class="tab-content">
        <div class="toolbar">
          <el-select v-model="secondhandCategoryFilter" placeholder="分类筛选" clearable style="width: 150px;">
            <el-option v-for="c in secondhandCategories" :key="c" :label="c" :value="c" />
          </el-select>
          <el-button type="primary" @click="secondhandDialogVisible = true">发布闲置</el-button>
        </div>
        <div class="card-grid">
          <el-card v-for="item in secondhandList" :key="item.id" class="info-card product-card">
            <div class="product-img" v-if="item.imageUrl">
              <el-image :src="item.imageUrl" fit="cover" />
            </div>
            <div class="product-info">
              <div class="info-header">
                <span class="price">￥{{ item.price }}</span>
                <span class="status-tag" :class="item.status">{{ item.status === 'SOLD' ? '已售出' : '在售' }}</span>
              </div>
              <h3 class="info-title">{{ item.title }}</h3>
              <p class="info-desc">{{ item.content }}</p>
              <div class="card-footer">
                <span class="author">发布者: {{ getInfoAuthor(item, 'sellerId') }}</span>
                <el-button link v-if="item.sellerId === user.id" type="danger" @click="deleteSecondhand(item)">删除</el-button>
                <el-button link type="primary" v-else @click="openInfoAuthorDm(item, 'sellerId')">私信</el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>

       <!-- 失物招领 -->
      <div v-if="currentTab === 'lostfound'" class="tab-content">
        <div class="toolbar">
          <el-select v-model="lostfoundCategoryFilter" placeholder="分类筛选" clearable style="width: 150px;">
            <el-option v-for="c in lostfoundCategories" :key="c" :label="c" :value="c" />
          </el-select>
          <el-button type="primary" @click="lostfoundDialogVisible = true">发布信息</el-button>
        </div>
        <div class="card-grid">
          <el-card v-for="item in lostfoundList" :key="item.id" class="info-card">
            <div class="info-header">
               <span class="info-tag" :class="item.status === 'FOUND' ? 'found' : 'lost'">
                 {{ item.status === 'FOUND' ? '招领' : '寻物' }}
               </span>
               <span class="info-tag category">{{ item.category }}</span>
            </div>
            <h3 class="info-title">{{ item.title }}</h3>
            <p class="info-desc">{{ item.content }}</p>
            <div class="info-meta-row" v-if="item.location">
              <el-icon><Location /></el-icon> {{ item.location }}
            </div>
             <div class="card-footer">
                <span class="author">发布者: {{ getInfoAuthor(item, 'reporterId') }}</span>
                <el-button link v-if="item.reporterId === user.id" type="danger" @click="deleteLostFound(item)">删除</el-button>
                <el-button link type="primary" v-else @click="openInfoAuthorDm(item, 'reporterId')">联系TA</el-button>
              </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- Dialogs -->
    <!-- Activity Dialog -->
    <el-dialog v-model="activityDialogVisible" title="发布活动" width="500px">
      <el-form :model="activityForm" label-width="80px">
        <el-form-item label="标题"><el-input v-model="activityForm.title" /></el-form-item>
        <el-form-item label="分类">
           <el-select v-model="activityForm.category"><el-option v-for="c in activityCategories" :key="c" :label="c" :value="c" /></el-select>
        </el-form-item>
        <el-form-item label="地点"><el-input v-model="activityForm.location" /></el-form-item>
        <el-form-item label="时间">
           <el-date-picker v-model="activityForm.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
        <el-form-item label="内容"><el-input v-model="activityForm.content" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="publishActivity">发布</el-button></template>
    </el-dialog>
    
    <!-- Secondhand Dialog -->
    <el-dialog v-model="secondhandDialogVisible" title="发布闲置" width="500px">
      <el-form :model="secondhandForm" label-width="80px">
        <el-form-item label="标题"><el-input v-model="secondhandForm.title" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="secondhandForm.price" /></el-form-item>
        <el-form-item label="分类">
           <el-select v-model="secondhandForm.category"><el-option v-for="c in secondhandCategories" :key="c" :label="c" :value="c" /></el-select>
        </el-form-item>
        <el-form-item label="图片">
           <div style="display: flex; gap: 8px; width: 100%;">
             <el-input v-model="secondhandForm.imageUrl" placeholder="URL" />
             <el-upload action="#" :show-file-list="false" accept="image/*" :http-request="(opt) => handleImageUpload(opt, 'secondhand')">
               <el-button>上传</el-button>
             </el-upload>
           </div>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="secondhandForm.content" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="publishSecondhand">发布</el-button></template>
    </el-dialog>

    <!-- LostFound Dialog -->
    <el-dialog v-model="lostfoundDialogVisible" title="发布失物招领" width="500px">
      <el-form :model="lostfoundForm" label-width="80px">
        <el-form-item label="类型">
           <el-select v-model="lostfoundForm.status"><el-option label="寻物" value="LOST"/><el-option label="招领" value="FOUND"/></el-select>
        </el-form-item>
        <el-form-item label="标题"><el-input v-model="lostfoundForm.title" /></el-form-item>
         <el-form-item label="分类">
           <el-select v-model="lostfoundForm.category"><el-option v-for="c in lostfoundCategories" :key="c" :label="c" :value="c" /></el-select>
        </el-form-item>
        <el-form-item label="地点"><el-input v-model="lostfoundForm.location" /></el-form-item>
        <el-form-item label="图片">
           <div style="display: flex; gap: 8px; width: 100%;">
             <el-input v-model="lostfoundForm.imageUrl" placeholder="URL" />
             <el-upload action="#" :show-file-list="false" accept="image/*" :http-request="(opt) => handleImageUpload(opt, 'lostfound')">
               <el-button>上传</el-button>
             </el-upload>
           </div>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="lostfoundForm.content" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="publishLostFound">发布</el-button></template>
    </el-dialog>

    <!-- DM Dialog -->
    <el-dialog v-model="dmDialogVisible" title="发送私信" width="400px">
      <el-input v-model="dmContent" type="textarea" placeholder="请输入内容..." />
      <template #footer>
        <el-button type="primary" @click="sendDm">发送</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Location, Timer } from '@element-plus/icons-vue'
import { listAnnouncementsApi } from '../api/announcement'
import { listActivitiesApi, publishActivityApi, deleteActivityApi } from '../api/activity'
import { listSecondHandApi, publishSecondHandApi, deleteSecondHandApi } from '../api/secondhand'
import { listLostFoundApi, publishLostFoundApi, deleteLostFoundApi } from '../api/lostfound'
import { sendMessageApi } from '../api/message'
import { uploadImageApi } from '../api/upload'

const route = useRoute()
const user = JSON.parse(localStorage.getItem('currentUser') || '{}')

const tabs = [
  { key: 'announcement', label: '公告' },
  { key: 'activity', label: '活动广场' },
  { key: 'secondhand', label: '二手交易' },
  { key: 'lostfound', label: '失物招领' }
]

const currentTab = ref(route.query.tab || 'announcement')

const announcements = ref([])
const activities = ref([])
const secondhandItems = ref([])
const lostfoundItems = ref([])

const activityCategories = ['学术讲座', '社团活动', '比赛报名', '志愿活动', '其他']
const secondhandCategories = ['数码产品', '书籍教材', '生活用品', '代步工具', '其他']
const lostfoundCategories = ['证件卡包', '数码设备', '钥匙', '衣物', '其他']

const activityCategoryFilter = ref('')
const secondhandCategoryFilter = ref('')
const lostfoundCategoryFilter = ref('')

// Forms
const activityForm = reactive({ title: '', content: '', category: '', location: '', startTime: '' })
const secondhandForm = reactive({ title: '', content: '', category: '', price: 0, imageUrl: '', status: 'ON_SALE' })
const lostfoundForm = reactive({ title: '', content: '', category: '', location: '', imageUrl: '', status: 'LOST' })

const activityDialogVisible = ref(false)
const secondhandDialogVisible = ref(false)
const lostfoundDialogVisible = ref(false)
const dmDialogVisible = ref(false)
const dmContent = ref('')
const dmTargetId = ref(null)

// Computed
const toTime = (t) => new Date(t).getTime()
const sortList = (l) => [...(l || [])].sort((a,b) => toTime(b.createdAt) - toTime(a.createdAt))
const announcementList = computed(() => sortList(announcements.value))
const activityList = computed(() => sortList(activities.value))
const secondhandList = computed(() => sortList(secondhandItems.value))
const lostfoundList = computed(() => sortList(lostfoundItems.value))

const formatDateTime = (val) => String(val).replace('T', ' ')
const getInfoAuthor = (item, field) => item.nickname || item.username || `User ${item[field]}`

watch(currentTab, (val) => {
  if (val === 'announcement') loadAnnouncements()
  if (val === 'activity') loadActivities()
  if (val === 'secondhand') loadSecondhand()
  if (val === 'lostfound') loadLostFound()
}, { immediate: true })

async function loadAnnouncements() {
  const { data } = await listAnnouncementsApi()
  announcements.value = data?.data || []
}

async function loadActivities() {
  const params = activityCategoryFilter.value ? { category: activityCategoryFilter.value } : {}
  const { data } = await listActivitiesApi(params)
  activities.value = data?.data || []
}

async function loadSecondhand() {
  const params = secondhandCategoryFilter.value ? { category: secondhandCategoryFilter.value } : {}
  const { data } = await listSecondHandApi(params)
  secondhandItems.value = data?.data || []
}

async function loadLostFound() {
  const params = lostfoundCategoryFilter.value ? { category: lostfoundCategoryFilter.value } : {}
  const { data } = await listLostFoundApi(params)
  lostfoundItems.value = data?.data || []
}

async function handleImageUpload(options, type) {
  const { file, onSuccess, onError } = options
  try {
    const { data } = await uploadImageApi(file)
    const url = data?.data?.url || data?.url
    if (url) {
      if (type === 'secondhand') secondhandForm.imageUrl = url
      if (type === 'lostfound') lostfoundForm.imageUrl = url
      ElMessage.success('图片上传成功')
      onSuccess(data)
    }
  } catch (e) {
    ElMessage.error('图片上传失败')
    onError(e)
  }
}

// Publish Actions
async function publishActivity() {
  await publishActivityApi({ ...activityForm, organizerId: user.id })
  ElMessage.success('发布成功')
  activityDialogVisible.value = false
  loadActivities()
}

async function publishSecondhand() {
  await publishSecondHandApi({ ...secondhandForm, sellerId: user.id })
  ElMessage.success('发布成功')
  secondhandDialogVisible.value = false
  loadSecondhand()
}

async function publishLostFound() {
  await publishLostFoundApi({ ...lostfoundForm, reporterId: user.id })
  ElMessage.success('发布成功')
  lostfoundDialogVisible.value = false
  loadLostFound()
}

// Delete Actions
async function deleteActivity(item) {
  if(!confirm('确认删除?')) return
  await deleteActivityApi(item.id, user.id)
  loadActivities()
}
async function deleteSecondhand(item) {
  if(!confirm('确认删除?')) return
  await deleteSecondHandApi(item.id, user.id)
  loadSecondhand()
}
async function deleteLostFound(item) {
  if(!confirm('确认删除?')) return
  await deleteLostFoundApi(item.id, user.id)
  loadLostFound()
}

// DM
function openInfoAuthorDm(item, field) {
  dmTargetId.value = item[field]
  dmDialogVisible.value = true
}

async function sendDm() {
  if (!dmContent.value) return
  await sendMessageApi({ fromUserId: user.id, toUserId: dmTargetId.value, content: dmContent.value })
  ElMessage.success('发送成功')
  dmDialogVisible.value = false
  dmContent.value = ''
}
</script>

<style scoped>
.services-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 100px 20px 40px;
  animation: fadeIn 0.5s ease;
}

.view-header {
  margin-bottom: 32px;
  text-align: center;
}

.view-header h2 { font-size: 32px; font-weight: 700; margin-bottom: 8px; }
.view-header p { color: #666; font-size: 16px; }

.nav-tabs-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 32px;
}

.nav-tabs {
  display: flex;
  background: #f1f1f1;
  padding: 4px;
  border-radius: 12px;
}

.nav-tab {
  padding: 8px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
  color: #666;
}

.nav-tab.active {
  background: #fff;
  color: #000;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.info-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.info-tag {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.info-tag.admin { background: #e0f2fe; color: #0284c7; }
.info-tag.activity { background: #f0fdf4; color: #16a34a; }
.info-tag.found { background: #dcfce7; color: #15803d; }
.info-tag.lost { background: #fee2e2; color: #b91c1c; }
.info-tag.category { background: #f3f4f6; color: #4b5563; }

.info-time {
  font-size: 12px;
  color: #9ca3af;
}

.info-title {
  margin: 0 0 8px;
  font-size: 18px;
  line-height: 1.4;
}

.info-desc {
  color: #4b5563;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.info-meta-row {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #6b7280;
  font-size: 13px;
  margin-bottom: 4px;
}

.card-footer {
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid #f3f4f6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #6b7280;
}

/* Product Card */
.product-img {
  height: 200px;
  background: #f3f4f6;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 12px;
}

.product-info .price {
  font-size: 18px;
  font-weight: 700;
  color: #000;
}

.status-tag.SOLD { color: #9ca3af; }
.status-tag.ON_SALE { color: #10b981; }

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>