<template>
  <footer class="site-footer">
    <div class="footer-inner">
      <div class="footer-col">
        <h4>关于 CSocial</h4>
        <a href="javascript:void(0)">关于我们</a>
        <a href="javascript:void(0)">加入团队</a>
        <a href="javascript:void(0)">隐私政策</a>
        <a href="javascript:void(0)">服务条款</a>
      </div>

      <div class="footer-col">
        <h4>获取帮助</h4>
        <a href="javascript:void(0)">联系我们</a>
        <a href="javascript:void(0)">意见反馈</a>
        <a href="javascript:void(0)">常见问题</a>
        <a href="javascript:void(0)">申诉通道</a>
        <a href="javascript:void(0)">用户指南</a>
      </div>

      <div class="footer-col">
        <h4>快捷链接</h4>
        <a href="javascript:void(0)">校园动态</a>
        <a href="javascript:void(0)">二手市场</a>
        <a href="javascript:void(0)">失物招领</a>
        <a href="javascript:void(0)">社团活动</a>
        <a href="javascript:void(0)">兼职信息</a>
      </div>

      <div class="footer-col">
        <h4>加入讨论群</h4>
        <div class="social-row">
          <span>W</span>
          <span>Q</span>
          <span>B</span>
          <span>D</span>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.site-footer {
  margin-top: 40px;
  background: #fff;
  border-top: 1px solid #ecf0f5;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 42px 20px 28px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 24px;
}

.footer-col h4 {
  margin: 0 0 14px;
  font-size: 31px;
  line-height: 1;
  transform: scale(0.5);
  transform-origin: left center;
  color: #121827;
}

.footer-col a {
  display: block;
  margin-bottom: 8px;
  font-size: 29px;
  line-height: 1.25;
  transform: scale(0.5);
  transform-origin: left center;
  color: #616b7c;
  text-decoration: none;
}

.footer-col a:hover {
  color: #1f2738;
}

.social-row {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.social-row span {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f1f5fa;
  color: #31425b;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 992px) {
  .footer-inner {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .footer-inner {
    grid-template-columns: 1fr;
  }
}
</style>
