<!--
  提供页面底部信息区，承载联系方式、备案与版权等低频内容。
  模块：前端展示层 / 布局组件。
  约束：默认布局统一注入该组件，避免各页面重复维护底部信息。
-->
<script setup lang="ts">
const currentYear = new Date().getFullYear()
</script>

<template>
  <footer class="life-footer" aria-label="页面底部信息">
    <div class="life-footer__records">
      <span>沪ICP备00000000号-1</span>
      <span>沪公网安备 00000000000000号</span>
    </div>

    <div class="life-footer__support">
      <a href="mailto:support@mobe.app">support@mobe.app</a>
      <span>工作日 9:00-18:00</span>
      <span>© {{ currentYear }} MoBe Life</span>
    </div>
  </footer>
</template>

<style scoped>
.life-footer {
  display: flex;
  flex-shrink: 0;
  height: 46px;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  border-top: 1px solid var(--life-shell-border);
  background: var(--life-shell-bg);
  padding: 0 24px;
  color: var(--life-shell-muted);
  transition:
    background-color 220ms ease,
    border-color 220ms ease,
    color 220ms ease;
}

.life-footer__records,
.life-footer__support {
  display: flex;
  min-width: 0;
  align-items: center;
  gap: 16px;
  color: var(--life-shell-subtle);
  font-size: 12px;
  font-weight: 560;
  white-space: nowrap;
}

.life-footer__records {
  overflow: hidden;
}

.life-footer__records span,
.life-footer__support span,
.life-footer__support a {
  color: inherit;
  text-decoration: none;
}

.life-footer__records span,
.life-footer__support span {
  overflow: hidden;
  text-overflow: ellipsis;
}

.life-footer__records span + span,
.life-footer__support span,
.life-footer__support a + span {
  position: relative;
}

.life-footer__records span + span::before,
.life-footer__support span::before {
  position: absolute;
  top: 50%;
  left: -9px;
  width: 2px;
  height: 2px;
  border-radius: 999px;
  background: currentcolor;
  content: '';
  opacity: 0.55;
  transform: translateY(-50%);
}

.life-footer__support {
  justify-content: flex-end;
}

.life-footer__support a:hover {
  color: #13b981;
}

@media (max-width: 1100px) {
  .life-footer__support span:first-of-type {
    display: none;
  }
}

@media (max-width: 760px) {
  .life-footer {
    gap: 14px;
    padding: 0 16px;
  }

  .life-footer__records {
    display: none;
  }

  .life-footer__support {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
