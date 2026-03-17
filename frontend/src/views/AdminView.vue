<template>
  <div class="container">
    <el-row justify="space-between" align="middle" style="margin-bottom:16px">
      <h2>管理员控制台</h2>
      <el-button @click="logout">退出登录</el-button>
    </el-row>

    <el-row :gutter="12" style="margin-bottom:16px">
      <el-col :span="6"><el-card>用户总量：{{ stats.userCount }}</el-card></el-col>
      <el-col :span="6"><el-card>活跃用户：{{ stats.activeUserCount }}</el-card></el-col>
      <el-col :span="6"><el-card>动态总量：{{ stats.postCount }}</el-card></el-col>
      <el-col :span="6"><el-card>互动总量：{{ stats.interactionCount }}</el-card></el-col>
    </el-row>

    <el-tabs>
      <el-tab-pane label="报表概览">
        <el-row :gutter="12" style="margin-bottom:12px">
          <el-col :span="8"><el-card>今日新增用户：{{ stats.todayNewUsers }}</el-card></el-col>
          <el-col :span="8"><el-card>今日新增动态：{{ stats.todayNewPosts }}</el-card></el-col>
          <el-col :span="8"><el-card>客流指数：{{ stats.trafficIndex }}</el-card></el-col>
        </el-row>
        <el-row :gutter="12" style="margin-bottom:12px">
          <el-col :span="8"><el-card>近7天新增用户：{{ stats.sevenDayNewUsers }}</el-card></el-col>
          <el-col :span="8"><el-card>近7天新增动态：{{ stats.sevenDayNewPosts }}</el-card></el-col>
          <el-col :span="8"><el-card>近7天新增评论：{{ stats.sevenDayComments }}</el-card></el-col>
        </el-row>

        <el-card>
          <template #header>
            <div class="card-header">
              <span>近7日趋势</span>
              <el-button size="small" @click="loadTrend">刷新</el-button>
            </div>
          </template>
          <el-table :data="trendData" border>
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="newUsers" label="新增用户" />
            <el-table-column prop="newPosts" label="新增动态" />
            <el-table-column prop="newComments" label="新增评论" />
            <el-table-column prop="traffic" label="客流指数" />
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="用户管理">
        <el-card style="margin-bottom:12px">
          <el-form :inline="true" :model="userFilters">
            <el-form-item label="关键词">
              <el-input v-model="userFilters.keyword" placeholder="用户名/昵称/邮箱" clearable />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="userFilters.status" placeholder="全部" clearable style="width:120px">
                <el-option label="ACTIVE" value="ACTIVE" />
                <el-option label="DISABLED" value="DISABLED" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadUsers">查询</el-button>
              <el-button @click="resetUserFilters">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-table :data="users" border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="nickname" label="昵称" />
          <el-table-column prop="role" label="角色" width="100" />
          <el-table-column prop="status" label="状态" width="110" />
          <el-table-column label="操作" width="220">
            <template #default="scope">
              <el-button
                size="small"
                type="warning"
                :disabled="scope.row.role === 'ADMIN'"
                @click="changeStatus(scope.row, 'DISABLED')"
              >禁用</el-button>
              <el-button
                size="small"
                type="success"
                :disabled="scope.row.role === 'ADMIN'"
                @click="changeStatus(scope.row, 'ACTIVE')"
              >启用</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="动态管理">
        <el-card style="margin-bottom:12px">
          <el-form :inline="true" :model="postFilters">
            <el-form-item label="发布者ID">
              <el-input-number v-model="postFilters.userId" :min="1" :controls="false" placeholder="可选" />
            </el-form-item>
            <el-form-item label="关键词">
              <el-input v-model="postFilters.keyword" placeholder="标题/内容" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadPosts">查询</el-button>
              <el-button @click="resetPostFilters">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-table :data="posts" border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="userId" label="发布者ID" width="110" />
          <el-table-column prop="likeCount" label="点赞" width="90" />
          <el-table-column prop="commentCount" label="评论" width="90" />
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button size="small" type="danger" @click="removePost(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="公告管理">
        <el-card style="margin-bottom:12px">
          <el-form :model="noticeForm" label-width="60px">
            <el-form-item label="标题">
              <el-input v-model="noticeForm.title" />
            </el-form-item>
            <el-form-item label="内容">
              <el-input v-model="noticeForm.content" type="textarea" :rows="3" />
            </el-form-item>
          </el-form>
          <el-button type="primary" @click="publishNotice">发布公告</el-button>
        </el-card>

        <el-table :data="announcements" border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="content" label="内容" />
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button size="small" type="danger" @click="removeNotice(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  adminDashboardApi,
  adminUsersApi,
  adminUpdateUserStatusApi,
  adminPostsApi,
  adminDeletePostApi,
  adminPublishAnnouncementApi,
  adminDeleteAnnouncementApi,
  adminTrendReportApi
} from '../api/admin'
import { listAnnouncementsApi } from '../api/announcement'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('currentUser') || '{}')

const stats = reactive({
  userCount: 0,
  activeUserCount: 0,
  postCount: 0,
  interactionCount: 0,
  todayNewUsers: 0,
  todayNewPosts: 0,
  sevenDayNewUsers: 0,
  sevenDayNewPosts: 0,
  sevenDayComments: 0,
  trafficIndex: 0
})

const users = ref([])
const posts = ref([])
const announcements = ref([])
const trendData = ref([])

const userFilters = reactive({
  keyword: '',
  status: ''
})

const postFilters = reactive({
  userId: null,
  keyword: ''
})

const noticeForm = reactive({
  title: '',
  content: ''
})

const loadDashboard = async () => {
  const dashboardResp = await adminDashboardApi(user.id)
  Object.assign(stats, dashboardResp?.data?.data || {})
}

const loadTrend = async () => {
  const resp = await adminTrendReportApi(user.id, 7)
  trendData.value = resp?.data?.data || []
}

const loadUsers = async () => {
  const resp = await adminUsersApi(user.id, {
    keyword: userFilters.keyword || undefined,
    status: userFilters.status || undefined
  })
  users.value = resp?.data?.data || []
}

const loadPosts = async () => {
  const resp = await adminPostsApi(user.id, {
    userId: postFilters.userId || undefined,
    keyword: postFilters.keyword || undefined
  })
  posts.value = resp?.data?.data || []
}

const loadAnnouncements = async () => {
  const annResp = await listAnnouncementsApi()
  announcements.value = annResp?.data?.data || []
}

const loadData = async () => {
  await Promise.all([
    loadDashboard(),
    loadUsers(),
    loadPosts(),
    loadAnnouncements(),
    loadTrend()
  ])
}

const resetUserFilters = async () => {
  userFilters.keyword = ''
  userFilters.status = ''
  await loadUsers()
}

const resetPostFilters = async () => {
  postFilters.userId = null
  postFilters.keyword = ''
  await loadPosts()
}

const changeStatus = async (row, status) => {
  await adminUpdateUserStatusApi(user.id, row.id, status)
  ElMessage.success('用户状态已更新')
  await Promise.all([loadUsers(), loadDashboard()])
}

const removePost = async (postId) => {
  await adminDeletePostApi(user.id, postId)
  ElMessage.success('动态已删除')
  await Promise.all([loadPosts(), loadDashboard(), loadTrend()])
}

const publishNotice = async () => {
  if (!noticeForm.title || !noticeForm.content) {
    ElMessage.warning('标题和内容不能为空')
    return
  }

  await adminPublishAnnouncementApi({
    adminUserId: user.id,
    title: noticeForm.title,
    content: noticeForm.content
  })
  ElMessage.success('公告发布成功')
  noticeForm.title = ''
  noticeForm.content = ''
  await Promise.all([loadAnnouncements(), loadDashboard()])
}

const removeNotice = async (announcementId) => {
  await adminDeleteAnnouncementApi(user.id, announcementId)
  ElMessage.success('公告已删除')
  await Promise.all([loadAnnouncements(), loadDashboard()])
}

const logout = () => {
  localStorage.removeItem('currentUser')
  router.push('/login')
}

onMounted(async () => {
  if (user?.role !== 'ADMIN') {
    router.push('/home')
    return
  }
  try {
    await loadData()
  } catch {
    ElMessage.error('加载管理数据失败')
  }
})
</script>

<style scoped>
.container {
  min-height: 100vh;
  width: 100%;
  margin: 0;
  padding: 24px;
  background: linear-gradient(180deg, #ffc7dd 0%, #ece8f7 48%, #b8e9ff 100%);
}

.card-header { display: flex; align-items: center; justify-content: space-between; }

.container :deep(.el-card) {
  border: none;
  border-radius: 18px;
  box-shadow: 0 12px 34px rgba(136, 154, 192, 0.16);
}

.container :deep(.el-tabs__item.is-active) {
  color: #f27fb1;
}

.container :deep(.el-tabs__active-bar) {
  background-color: #9fdaf8;
}

.container :deep(.el-button--primary),
.container :deep(.el-button--success),
.container :deep(.el-button--warning),
.container :deep(.el-button--danger) {
  border: none;
}

.container :deep(.el-button--primary) {
  background: linear-gradient(90deg, #9edfff, #ffb1d3);
}

.container :deep(.el-input__wrapper),
.container :deep(.el-textarea__inner) {
  border-radius: 12px;
}
</style>

