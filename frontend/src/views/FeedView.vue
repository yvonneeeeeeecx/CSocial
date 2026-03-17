<template>
  <div class="feed-view">
    <div class="view-header">
      <h2>校园动态</h2>
      <p>分享你的校园生活</p>
    </div>

    <div class="feed-container">
      <div class="feed-actions">
        <el-button type="primary" size="large" @click="postDialogVisible = true" class="post-btn">
          <el-icon><Edit /></el-icon> 发布动态
        </el-button>
        <el-button @click="loadPosts">刷新</el-button>
      </div>

      <div class="posts-list">
        <el-empty v-if="posts.length === 0" description="暂无动态，来抢沙发吧" />
        
        <el-card v-for="item in posts" :key="item.id" class="post-card">
          <div class="post-header">
            <div class="author-info" @click="$router.push(`/user/${item.userId}`)">
              <el-avatar :src="item.avatar" :size="40">{{ (item.nickname || item.username || 'U')[0] }}</el-avatar>
              <div class="author-meta">
                <span class="author-name">{{ item.nickname || item.username }}</span>
                <span class="post-time">{{ formatDateTime(item.createdAt) }}</span>
              </div>
            </div>
            <div class="post-ops" v-if="item.userId !== user.id">
              <el-button v-if="!item.isFollowing" size="small" round @click="handleFollow(item)">关注</el-button>
              <el-button v-else size="small" round type="info" @click="handleFollow(item)">已关注</el-button>
            </div>
            <el-button v-if="item.userId === user.id" size="small" type="danger" link @click="deletePost(item)">删除</el-button>
          </div>

          <div class="post-content">
            <h3 class="post-title">{{ item.title }}</h3>
            <p class="post-text">{{ item.content }}</p>
            <div class="post-images" v-if="item.imageUrl">
              <el-image 
                :src="item.imageUrl" 
                fit="cover" 
                :preview-src-list="[item.imageUrl]"
                class="post-img"
              />
            </div>
          </div>

          <div class="post-footer">
            <div class="action-item" @click="handleLike(item)">
              <el-icon :class="{ liked: item.isLiked }"><Opportunity /></el-icon>
              <span>{{ item.likeCount || 0 }}</span>
            </div>
            <div class="action-item" @click="toggleComments(item)">
              <el-icon><ChatDotRound /></el-icon>
              <span>{{ item.commentCount || 0 }}</span>
            </div>
            <div class="action-item">
              <el-icon><Share /></el-icon>
            </div>
          </div>

          <!-- Comments Section -->
          <div v-if="item.showComments" class="comments-section">
            <div class="comment-input">
              <el-input v-model="item.newComment" placeholder="写下你的评论..." size="small">
                <template #append>
                  <el-button @click="submitComment(item)">发送</el-button>
                </template>
              </el-input>
            </div>
            <div class="comments-list">
              <div v-for="c in item.comments" :key="c.id" class="comment-item">
                <span class="comment-user">{{ c.nickname || c.username }}:</span>
                <span class="comment-content">{{ c.content }}</span>
              </div>
            </div>
          </div>
        </el-card>

        <el-pagination
          background
          layout="prev, pager, next"
          :total="pager.total"
          :page-size="pager.size"
          @current-change="handlePageChange"
          class="pagination"
        />
      </div>
    </div>

    <!-- Create Post Dialog -->
    <el-dialog v-model="postDialogVisible" title="发布动态" width="500px">
      <el-form :model="postForm">
        <el-form-item label="标题">
          <el-input v-model="postForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="postForm.content" type="textarea" :rows="4" placeholder="分享你的新鲜事..." />
        </el-form-item>
        <el-form-item label="图片">
          <div style="display: flex; gap: 8px; width: 100%;">
            <el-input v-model="postForm.imageUrl" placeholder="图片URL" />
            <el-upload
              action="#"
              :show-file-list="false"
              accept="image/*"
              :http-request="handleImageUpload"
            >
              <el-button type="primary">上传</el-button>
            </el-upload>
          </div>
        </el-form-item>

      </el-form>
      <template #footer>
        <el-button @click="postDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="publishPost">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit, Opportunity, ChatDotRound, Share } from '@element-plus/icons-vue'
import { createPostApi, pagePostApi, likePostApi, commentPostApi, listCommentsApi, deletePostApi } from '../api/post'
import { followUserApi, unfollowUserApi, getFollowingUsersApi, blockUserApi, unblockUserApi, getBlockedUsersApi, getMutualFollowApi } from '../api/relation'
import { uploadImageApi } from '../api/upload'

const user = JSON.parse(localStorage.getItem('currentUser') || '{}')
const posts = ref([])
const pager = reactive({ page: 1, size: 10, total: 0 })
const postDialogVisible = ref(false)
const postForm = reactive({ title: '', content: '', imageUrl: '' })
const followingIds = ref([])
const blockedIds = ref([])
const mutualIds = ref([])

onMounted(() => {
  loadData()
})

const formatDateTime = (val) => String(val).replace('T', ' ')

async function loadData() {
  await loadRelations()
  await loadPosts()
}

async function loadRelations() {
  if (!user.id) return
  const [fRes, bRes, mRes] = await Promise.all([
    getFollowingUsersApi(user.id),
    getBlockedUsersApi(user.id),
    getMutualFollowApi(user.id)
  ])
  followingIds.value = fRes.data?.data || []
  blockedIds.value = bRes.data?.data || []
  mutualIds.value = mRes.data?.data || []
}

async function loadPosts() {
  const { data } = await pagePostApi({ page: pager.page, size: pager.size })
  pager.total = data?.total || 0
  posts.value = (data?.data || []).map(p => ({
    ...p,
    showComments: false,
    comments: [],
    isFollowing: followingIds.value.includes(p.userId),
    isBlocked: blockedIds.value.includes(p.userId),
    isMutual: mutualIds.value.includes(p.userId),
    isLiked: false 
  }))
}

async function handlePageChange(page) {
  pager.page = page
  loadPosts()
}

async function handleImageUpload(options) {
  const { file, onSuccess, onError } = options
  try {
    const { data } = await uploadImageApi(file)
    const url = data?.data?.url || data?.url
    if (url) {
      postForm.imageUrl = url
      ElMessage.success('图片上传成功')
      onSuccess(data)
    }
  } catch (e) {
    ElMessage.error('图片上传失败')
    onError(e)
  }
}

async function publishPost() {
  if (!postForm.title || !postForm.content) return ElMessage.warning('请填写标题和内容')
  await createPostApi({ ...postForm, userId: user.id })
  ElMessage.success('发布成功')
  postDialogVisible.value = false
  postForm.title = ''
  postForm.content = ''
  postForm.imageUrl = ''
  loadPosts()
}

async function deletePost(item) {
  if (!confirm('确定删除?')) return
  await deletePostApi(item.id, user.id)
  loadPosts()
}

async function handleLike(item) {
  await likePostApi(item.id, user.id)
  item.likeCount = (item.likeCount || 0) + 1
  item.isLiked = true
}

async function toggleComments(item) {
  item.showComments = !item.showComments
  if (item.showComments && item.comments.length === 0) {
    const { data } = await listCommentsApi(item.id)
    item.comments = data?.data || []
  }
}

async function submitComment(item) {
  if (!item.newComment) return
  await commentPostApi(item.id, { userId: user.id, content: item.newComment })
  item.newComment = ''
  const { data } = await listCommentsApi(item.id)
  item.comments = data?.data || []
  item.commentCount = (item.commentCount || 0) + 1
}

async function handleFollow(item) {
  if (item.isFollowing) {
    await unfollowUserApi(user.id, item.userId)
    item.isFollowing = false
  } else {
    await followUserApi(user.id, item.userId)
    item.isFollowing = true
  }
  loadRelations()
}
</script>


<style scoped>
.feed-view {
  max-width: 800px;
  margin: 0 auto;
  padding: 100px 20px 40px;
  animation: fadeIn 0.5s ease;
}

.view-header {
  text-align: center;
  margin-bottom: 32px;
}

.feed-actions {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
}

.post-btn {
  width: 100%;
  max-width: 200px;
}

.post-card {
  margin-bottom: 24px;
  border-radius: 16px;
  overflow: hidden;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.author-meta {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 600;
  font-size: 15px;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-title {
  margin: 0 0 8px;
  font-size: 18px;
}

.post-text {
  font-size: 15px;
  line-height: 1.6;
  color: #333;
  margin-bottom: 12px;
}

.post-img {
  border-radius: 12px;
  max-height: 400px;
  width: 100%;
  object-fit: cover;
}

.post-footer {
  border-top: 1px solid #f0f0f0;
  margin-top: 16px;
  padding-top: 12px;
  display: flex;
  justify-content: space-around;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
}

.action-item:hover {
  color: #000;
}

.liked {
  color: #ff3b30;
}

.comments-section {
  margin-top: 16px;
  background: #f9f9f9;
  padding: 12px;
  border-radius: 12px;
}

.comment-item {
  font-size: 13px;
  margin-bottom: 6px;
}

.comment-user {
  font-weight: 600;
  margin-right: 6px;
}

.pagination {
  margin-top: 24px;
  justify-content: center;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>