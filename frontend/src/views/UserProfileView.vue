<template>
  <div class="page">
    <div class="container">
      <el-card class="card">
        <div class="header">
          <el-button text @click="goBack">返回</el-button>
          <span class="header-title">用户主页</span>

          <div class="header-actions" v-if="status.isSelf">

            <el-button v-if="!editMode" type="primary" @click="startEdit">编辑资料</el-button>
            <div v-else class="edit-actions">
              <el-button type="primary" @click="saveProfile">保存</el-button>
              <el-button @click="cancelEdit">取消</el-button>
            </div>
          </div>
          <div class="header-actions" v-else>
            <el-tag v-if="status.isMutual" type="success">互相关注</el-tag>
            <el-tag v-else-if="status.isFollowing" type="info">已关注</el-tag>
            <el-tag v-else type="warning">未关注</el-tag>
          </div>

        </div>

        <div class="profile">
          <div class="avatar">{{ displayInitial }}</div>
          <div class="profile-info">
            <h2 class="name">{{ profile.nickname || profile.username || '用户' }}</h2>
            <p class="meta">账号：{{ profile.username || '-' }}</p>
            <p class="meta">学院：{{ profile.college || '未填写' }} · 专业：{{ profile.major || '未填写' }}</p>
            <p class="meta">年级：{{ profile.grade || '未填写' }}</p>
          </div>
        </div>

        <div class="stats">
          <div class="stat-item">
            <span class="stat-value">{{ counts.following }}</span>
            <span class="stat-label">关注</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ counts.followers }}</span>
            <span class="stat-label">粉丝</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ counts.mutual }}</span>
            <span class="stat-label">互相关注</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ counts.blocked }}</span>
            <span class="stat-label">黑名单</span>
          </div>
        </div>

        <div class="actions" v-if="!status.isSelf">
          <el-button
            type="primary"
            class="action-btn"
            :disabled="status.isBlocked"
            @click="status.isFollowing ? handleUnfollow() : handleFollow()"
          >
            {{ status.isFollowing ? '取消关注' : '关注用户' }}
          </el-button>
          <el-button
            type="danger"
            class="action-btn"
            @click="status.isBlocked ? handleUnblock() : handleBlock()"
          >
            {{ status.isBlocked ? '取消拉黑' : '加入黑名单' }}
          </el-button>
          <el-button
            class="action-btn"
            :disabled="status.isBlocked"
            @click="openProfileChat"
          >
            私信
          </el-button>
        </div>


        <div class="detail" v-if="!editMode">
          <div class="detail-item">
            <span class="detail-label">个人简介</span>
            <span class="detail-value">{{ profile.profile || '这个人很低调，还没有填写简介。' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">兴趣标签</span>
            <span class="detail-value">{{ profile.interests || '暂无' }}</span>
          </div>
        </div>
        <div class="detail" v-else>
          <el-form class="edit-form" label-width="90px">
            <el-form-item label="昵称">
              <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="个人简介">
              <el-input
                v-model="editForm.profile"
                type="textarea"
                :rows="3"
                placeholder="介绍一下你自己"
              />
            </el-form-item>
            <el-form-item label="兴趣标签">
              <el-input v-model="editForm.interests" placeholder="例如：摄影 / 音乐" />
            </el-form-item>
            <el-form-item label="学院">
              <el-input v-model="editForm.college" placeholder="请输入学院" />
            </el-form-item>
            <el-form-item label="专业">
              <el-input v-model="editForm.major" placeholder="请输入专业" />
            </el-form-item>
            <el-form-item label="年级">
              <el-input-number v-model="editForm.grade" :min="1" :max="8" style="width: 100%" />
            </el-form-item>
          </el-form>
        </div>

        <div class="self-panels" v-if="status.isSelf">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="私信" name="messages">
              <div class="message-panel">
                <div class="conversation-list">
                  <el-empty v-if="conversations.length === 0" description="暂无私信" />
                  <div v-else>
                    <div
                      v-for="item in conversations"
                      :key="item.peerId"
                      class="conversation-item"
                      :class="{ active: activeConversation?.peerId === item.peerId }"
                      @click="selectConversation(item)"
                    >
                      <div class="conversation-name">
                        {{ item.peerNickname || item.peerUsername || ('用户' + item.peerId) }}
                      </div>
                      <div class="conversation-meta">
                        {{ item.lastMessage || '暂无消息' }}
                      </div>
                    </div>
                  </div>
                </div>
                <div class="chat-panel">
                  <div class="chat-header">
                    {{ activeConversation?.peerNickname || activeConversation?.peerUsername || (activeConversation?.peerId ? ('用户' + activeConversation.peerId) : '请选择会话') }}
                  </div>
                  <div class="chat-list">
                    <el-empty v-if="conversationMessages.length === 0" description="暂无聊天记录" />
                    <div v-else>
                      <div
                        v-for="msg in conversationMessages"
                        :key="msg.id"
                        class="chat-message"
                        :class="msg.fromUserId === currentUser.id ? 'self' : 'other'"
                      >
                        <div class="chat-bubble">
                          <div class="chat-content">{{ msg.content }}</div>
                          <div class="chat-time">{{ msg.createdAt }}</div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="chat-input">
                    <el-input v-model="messageContent" type="textarea" :rows="2" placeholder="输入私信内容" />
                    <el-button type="primary" :disabled="!activeConversation" @click="sendMessage">发送</el-button>
                  </div>
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="关注" name="following">
              <div class="user-list">
                <el-empty v-if="followingUsers.length === 0" description="暂无关注" />
                <div v-else>
                  <div v-for="item in followingUsers" :key="item.id" class="user-card" @click="router.push('/user/' + item.id)">
                    <div class="user-avatar">{{ (item.nickname || item.username || 'U').slice(0, 1).toUpperCase() }}</div>
                    <div class="user-info">
                      <div class="user-name">{{ item.nickname || item.username || ('用户' + item.id) }}</div>
                      <div class="user-meta">{{ item.college || '未填写' }} · {{ item.major || '未填写' }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="粉丝" name="followers">
              <div class="user-list">
                <el-empty v-if="followerUsers.length === 0" description="暂无粉丝" />
                <div v-else>
                  <div v-for="item in followerUsers" :key="item.id" class="user-card" @click="router.push('/user/' + item.id)">
                    <div class="user-avatar">{{ (item.nickname || item.username || 'U').slice(0, 1).toUpperCase() }}</div>
                    <div class="user-info">
                      <div class="user-name">{{ item.nickname || item.username || ('用户' + item.id) }}</div>
                      <div class="user-meta">{{ item.college || '未填写' }} · {{ item.major || '未填写' }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="黑名单" name="blocked">
              <div class="user-list">
                <el-empty v-if="blockedUsers.length === 0" description="暂无黑名单" />
                <div v-else>
                  <div v-for="item in blockedUsers" :key="item.id" class="user-card" @click="router.push('/user/' + item.id)">
                    <div class="user-avatar">{{ (item.nickname || item.username || 'U').slice(0, 1).toUpperCase() }}</div>
                    <div class="user-info">
                      <div class="user-name">{{ item.nickname || item.username || ('用户' + item.id) }}</div>
                      <div class="user-meta">{{ item.college || '未填写' }} · {{ item.major || '未填写' }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>

      </el-card>

      <el-dialog v-model="profileChatVisible" title="私信" width="520px" @close="closeProfileChat">
        <div class="chat-list">
          <el-empty v-if="profileChatMessages.length === 0" description="暂无聊天记录" />
          <div v-else>
            <div
              v-for="msg in profileChatMessages"
              :key="msg.id"
              class="chat-message"
              :class="msg.fromUserId === currentUser.id ? 'self' : 'other'"
            >
              <div class="chat-bubble">
                <div class="chat-content">{{ msg.content }}</div>
                <div class="chat-time">{{ msg.createdAt }}</div>
              </div>
            </div>
          </div>
        </div>
        <template #footer>
          <el-input v-model="profileChatContent" type="textarea" :rows="2" placeholder="输入私信内容" />
          <div class="chat-input">
            <el-button @click="closeProfileChat">关闭</el-button>
            <el-button type="primary" @click="sendProfileChat">发送</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </div>
</template>


<script setup>
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { getUserApi, updateUserApi } from '../api/user'
import { getConversationsApi, getConversationApi, sendMessageApi } from '../api/message'


import {
  followUserApi,
  unfollowUserApi,
  blockUserApi,
  unblockUserApi,
  getFollowingUsersApi,
  getFollowersApi,
  getMutualFollowApi,
  getBlockedUsersApi
} from '../api/relation'

const route = useRoute()
const router = useRouter()
const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')

const profile = ref({})
const counts = reactive({
  following: 0,
  followers: 0,
  mutual: 0,
  blocked: 0
})
const status = reactive({
  isSelf: false,
  isFollowing: false,
  isBlocked: false,
  isMutual: false
})

const activeTab = ref('messages')
const followingUsers = ref([])
const followerUsers = ref([])
const blockedUsers = ref([])
const conversations = ref([])
const activeConversation = ref(null)
const conversationMessages = ref([])
const messageContent = ref('')
const pollingTimer = ref(null)

const profileChatVisible = ref(false)
const profileChatMessages = ref([])
const profileChatContent = ref('')
const profileChatTimer = ref(null)

const editMode = ref(false)


const editForm = reactive({
  nickname: '',
  profile: '',
  interests: '',
  college: '',
  major: '',
  grade: null
})

const profileId = computed(() => Number(route.params.id))


const displayInitial = computed(() => {
  const name = profile.value?.nickname || profile.value?.username || 'U'
  return name.slice(0, 1).toUpperCase()
})

const goBack = () => {
  router.push('/home')
}

const syncEditForm = (data) => {
  editForm.nickname = data?.nickname || ''
  editForm.profile = data?.profile || ''
  editForm.interests = data?.interests || ''
  editForm.college = data?.college || ''
  editForm.major = data?.major || ''
  editForm.grade = data?.grade ?? null
}

const loadProfile = async (userId) => {
  const { data } = await getUserApi(userId)
  profile.value = data?.data || {}
  syncEditForm(profile.value)
}


const loadCounts = async (userId) => {
  const [followingResp, followersResp, mutualResp, blockedResp] = await Promise.all([
    getFollowingUsersApi(userId),
    getFollowersApi(userId),
    getMutualFollowApi(userId),
    getBlockedUsersApi(userId)
  ])

  counts.following = followingResp?.data?.count ?? (followingResp?.data?.data || []).length
  counts.followers = followersResp?.data?.count ?? (followersResp?.data?.data || []).length
  counts.mutual = mutualResp?.data?.count ?? (mutualResp?.data?.data || []).length
  counts.blocked = blockedResp?.data?.count ?? (blockedResp?.data?.data || []).length
}

const loadStatus = async (targetId) => {
  status.isSelf = currentUser?.id === targetId
  if (status.isSelf) {
    status.isFollowing = false
    status.isBlocked = false
    status.isMutual = false
    return
  }

  const [followingResp, blockedResp, mutualResp] = await Promise.all([
    getFollowingUsersApi(currentUser.id),
    getBlockedUsersApi(currentUser.id),
    getMutualFollowApi(currentUser.id)
  ])

  const followingIds = followingResp?.data?.data || []
  const blockedIds = blockedResp?.data?.data || []
  const mutualIds = mutualResp?.data?.data || []

  status.isFollowing = followingIds.includes(targetId)
  status.isBlocked = blockedIds.includes(targetId)
  status.isMutual = mutualIds.includes(targetId)
}

const loadUserCards = async (ids) => {
  if (!ids || ids.length === 0) return []
  const items = await Promise.all(ids.map(async (id) => {
    try {
      const { data } = await getUserApi(id)
      return data?.data || null
    } catch {
      return null
    }
  }))
  return items.filter(Boolean)
}

const loadConversationMessages = async (targetId) => {
  if (!currentUser?.id || !targetId) return
  const { data } = await getConversationApi({ userId: currentUser.id, targetId })
  conversationMessages.value = data?.data || []
}

const loadProfileChatMessages = async () => {
  if (!currentUser?.id || !profileId.value) return
  const { data } = await getConversationApi({ userId: currentUser.id, targetId: profileId.value })
  profileChatMessages.value = data?.data || []
}


const loadConversations = async () => {
  if (!currentUser?.id) return
  const { data } = await getConversationsApi({ userId: currentUser.id })
  conversations.value = data?.data || []
}

const loadSelfPanels = async () => {
  if (!currentUser?.id) return
  const [followingResp, followersResp, blockedResp] = await Promise.all([
    getFollowingUsersApi(currentUser.id),
    getFollowersApi(currentUser.id),
    getBlockedUsersApi(currentUser.id)
  ])
  const followingIds = followingResp?.data?.data || []
  const followerIds = followersResp?.data?.data || []
  const blockedIds = blockedResp?.data?.data || []

  const [followingList, followerList, blockedList] = await Promise.all([
    loadUserCards(followingIds),
    loadUserCards(followerIds),
    loadUserCards(blockedIds)
  ])

  followingUsers.value = followingList
  followerUsers.value = followerList
  blockedUsers.value = blockedList

  await loadConversations()
  if (activeConversation.value?.peerId) {
    await loadConversationMessages(activeConversation.value.peerId)
  }
}

const selectConversation = async (item) => {
  activeConversation.value = item
  await loadConversationMessages(item?.peerId)
}

const sendMessage = async () => {
  if (!currentUser?.id || !activeConversation.value?.peerId) return
  if (!messageContent.value || !messageContent.value.trim()) {
    ElMessage.warning('请输入私信内容')
    return
  }
  await sendMessageApi({
    fromUserId: currentUser.id,
    toUserId: activeConversation.value.peerId,
    content: messageContent.value
  })
  messageContent.value = ''
  await Promise.all([
    loadConversationMessages(activeConversation.value.peerId),
    loadConversations()
  ])
}

const startPolling = () => {
  if (pollingTimer.value) return
  pollingTimer.value = setInterval(async () => {
    if (!status.isSelf) return
    await loadSelfPanels()
  }, 5000)
}

const stopPolling = () => {
  if (pollingTimer.value) {
    clearInterval(pollingTimer.value)
    pollingTimer.value = null
  }
}


const startEdit = () => {
  if (!status.isSelf) return
  syncEditForm(profile.value)
  editMode.value = true
}

const cancelEdit = () => {
  syncEditForm(profile.value)
  editMode.value = false
}

const saveProfile = async () => {
  if (!status.isSelf) return
  try {
    const params = {
      nickname: editForm.nickname,
      profile: editForm.profile,
      interests: editForm.interests,
      college: editForm.college,
      major: editForm.major,
      grade: editForm.grade ?? undefined
    }
    const { data } = await updateUserApi(profileId.value, params)
    if (!data?.success) {
      ElMessage.error(data?.message || '保存失败')
      return
    }
    profile.value = data?.data || profile.value
    localStorage.setItem('currentUser', JSON.stringify({
      ...currentUser,
      ...profile.value
    }))
    editMode.value = false
    ElMessage.success('资料已更新')
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '保存失败')
  }
}

const refreshAll = async () => {

  const targetId = profileId.value
  if (!targetId || !currentUser?.id) {
    router.push('/login')
    return
  }

  try {
    await Promise.all([
      loadProfile(targetId),
      loadCounts(targetId),
      loadStatus(targetId)
    ])

    if (status.isSelf) {
      await loadSelfPanels()
      startPolling()
    } else {
      stopPolling()
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '加载用户信息失败')
  }
}


const handleFollow = async () => {
  if (!currentUser?.id) return
  await followUserApi(currentUser.id, profileId.value)
  ElMessage.success('关注成功')
  await Promise.all([loadCounts(profileId.value), loadStatus(profileId.value)])
}

const handleUnfollow = async () => {
  if (!currentUser?.id) return
  await unfollowUserApi(currentUser.id, profileId.value)
  ElMessage.success('已取消关注')
  await Promise.all([loadCounts(profileId.value), loadStatus(profileId.value)])
}

const handleBlock = async () => {
  if (!currentUser?.id) return
  await blockUserApi(currentUser.id, profileId.value)
  ElMessage.success('已加入黑名单')
  await Promise.all([loadCounts(profileId.value), loadStatus(profileId.value)])
}

const handleUnblock = async () => {
  if (!currentUser?.id) return
  await unblockUserApi(currentUser.id, profileId.value)
  ElMessage.success('已移出黑名单')
  await Promise.all([loadCounts(profileId.value), loadStatus(profileId.value)])
}

onMounted(refreshAll)
watch(() => route.params.id, refreshAll)
onUnmounted(() => {
  stopPolling()
  stopProfileChatPolling()
})
</script>



<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #62d2e7 0%, #78cfe7 55%, #9ec7e2 100%);
}

.container {
  max-width: 980px;
  margin: 0 auto;
  padding: 96px 24px 24px;
}

.card {
  border-radius: 24px;
  border: none;
  box-shadow: 0 24px 60px rgba(40, 92, 120, 0.2);
}

.card :deep(.el-card__body) {
  padding: 28px 32px 30px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.header-title {


  font-size: 18px;
  color: #5f7f95;
  font-weight: 600;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.edit-actions {
  display: flex;
  gap: 8px;
}

.profile {

  display: flex;
  gap: 18px;
  align-items: center;
  margin-bottom: 16px;
}

.avatar {
  width: 74px;
  height: 74px;
  border-radius: 50%;
  background: #6fd3e6;
  color: #fff;
  font-size: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 14px 24px rgba(84, 184, 214, 0.35);
}

.profile-info {
  flex: 1;
}

.name {
  margin: 0;
  font-size: 24px;
  color: #3a556a;
}

.meta {
  margin: 4px 0 0;
  color: #7c8aa0;
}

.stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin: 18px 0 12px;
}

.stat-item {
  background: #f5fbff;
  border-radius: 16px;
  padding: 12px;
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 20px;
  color: #5da7c5;
  font-weight: 600;
}

.stat-label {
  color: #7b889b;
}

.actions {
  display: flex;
  gap: 12px;
  margin: 14px 0 4px;
}

.action-btn {
  flex: 1;
  height: 42px;
  border-radius: 999px;
  border: none;
}

.detail {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.edit-form :deep(.el-input__wrapper),
.edit-form :deep(.el-textarea__inner) {
  border-radius: 12px;
}

.detail-item {

  background: #f7fbfe;
  border-radius: 16px;
  padding: 14px 16px;
}

.detail-label {
  display: block;
  font-weight: 600;
  color: #5f7f95;
  margin-bottom: 6px;
}

.detail-value {
  color: #6f7c8a;
}

.card :deep(.el-button--primary) {
  background: linear-gradient(90deg, #5fb6f3, #6fd3e6);
  border: none;
}

.card :deep(.el-button--danger) {
  background: linear-gradient(90deg, #ff8ea8, #ff6b91);
  border: none;
}

.card :deep(.el-button:hover) {
  opacity: 0.95;
}

.self-panels {
  margin-top: 20px;
}

.message-panel {
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 16px;
}

.conversation-list {
  border-radius: 16px;
  background: #f6fbff;
  padding: 12px;
  min-height: 320px;
}

.conversation-item {
  padding: 10px 12px;
  border-radius: 12px;
  cursor: pointer;
  background: #fff;
  box-shadow: 0 8px 18px rgba(132, 160, 190, 0.12);
  margin-bottom: 10px;
}

.conversation-item.active {
  border: 1px solid #6fd3e6;
}

.conversation-name {
  font-weight: 600;
  color: #445566;
}

.conversation-meta {
  color: #8a98a8;
  font-size: 12px;
  margin-top: 4px;
}

.chat-panel {
  border-radius: 16px;
  background: #f9fbfe;
  padding: 12px;
  min-height: 320px;
  display: flex;
  flex-direction: column;
}

.chat-header {
  font-weight: 600;
  color: #445566;
  margin-bottom: 8px;
}

.chat-list {
  flex: 1;
  overflow-y: auto;
  padding: 4px 0 8px;
}

.chat-message {
  display: flex;
  margin-bottom: 8px;
}

.chat-message.self {
  justify-content: flex-end;
}

.chat-message.other {
  justify-content: flex-start;
}

.chat-bubble {
  max-width: 70%;
  padding: 8px 12px;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 8px 18px rgba(156, 171, 200, 0.16);
}

.chat-message.self .chat-bubble {
  background: #e1f5ff;
}

.chat-message.other .chat-bubble {
  background: #f5f2ff;
}

.chat-content {
  white-space: pre-wrap;
  color: #2c3e50;
}

.chat-time {
  margin-top: 4px;
  font-size: 12px;
  color: #9aa3ad;
}

.chat-input {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.user-list {
  padding: 6px 4px;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 8px 18px rgba(132, 160, 190, 0.12);
  margin-bottom: 10px;
  cursor: pointer;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #7cc6ff, #85a4ff);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
}

.user-info {
  flex: 1;
}

.user-name {
  font-weight: 600;
  color: #445566;
}

.user-meta {
  color: #8a98a8;
  font-size: 12px;
  margin-top: 4px;
}
</style>

