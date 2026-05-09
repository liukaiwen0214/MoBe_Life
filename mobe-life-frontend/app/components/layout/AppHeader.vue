<!--
  提供工作台顶部轻操作栏，承载当前页标题与消息提示。
  模块：前端展示层 / 布局组件。
  约束：侧栏负责模块切换，顶部不承载全量导航，只负责页面上下文和轻提醒。
-->
<script setup lang="ts">
const route = useRoute()
const appStore = useAppStore()

const titleMap: Record<string, string> = {
  '/': '首页',
  '/tasks': '待办',
  '/nodes': '节点',
  '/goals': '目标',
  '/projects': '项目',
  '/finance': '财务',
  '/bills': '账单',
  '/accounts': '账户',
  '/statistics': '统计',
  '/lists': '清单',
  '/inventory': '库存',
  '/profile': '个人中心',
  '/settings': '设置',
  '/habits': '习惯',
}

const pageTitle = computed(() => titleMap[route.path] || 'MoBe')

const latestMessage = ref('Q2 报表初稿已更新')
const unreadCount = ref(3)
</script>

<template>
  <header class="life-header">
    <div class="life-header__left">
      <h1 class="life-header__title">{{ pageTitle }}</h1>
    </div>

    <div class="life-header__actions">
      <button
        class="life-action-button"
        type="button"
        :aria-label="appStore.isDarkTheme ? '切换为白色主题' : '切换为黑色主题'"
        @click="appStore.toggleTheme"
      >
        <UIcon
          :name="appStore.isDarkTheme ? 'i-lucide-sun' : 'i-lucide-moon'"
          class="life-action-button__icon"
        />
      </button>

      <button class="life-action-button" type="button" :aria-label="latestMessage">
        <span class="life-action-button__icon-wrap">
          <UIcon name="i-lucide-bell" class="life-action-button__icon" />
          <span v-if="unreadCount > 0" class="life-message__badge">
            {{ unreadCount > 99 ? '99+' : unreadCount }}
          </span>
        </span>
      </button>
    </div>
  </header>
</template>

<style scoped>
.life-header {
  display: flex;
  flex-shrink: 0;
  min-height: 54px;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  border-bottom: 1px solid var(--life-shell-border);
  background: var(--life-shell-bg);
  padding: 0 24px 0 28px;
  transition:
    background-color 220ms ease,
    border-color 220ms ease,
    color 220ms ease;
}

.life-header__left {
  display: flex;
  min-width: 0;
  align-items: center;
}

.life-header__title {
  margin: 0;
  color: var(--life-shell-text);
  font-size: 20px;
  font-weight: 720;
  line-height: 1;
  transition: color 220ms ease;
}

.life-header__actions {
  display: flex;
  min-width: 0;
  align-items: center;
  gap: 6px;
}

.life-action-button {
  position: relative;
  display: grid;
  width: 34px;
  height: 34px;
  flex-shrink: 0;
  border: 1px solid var(--life-shell-control-border);
  border-radius: 9px;
  background: transparent;
  color: var(--life-shell-muted);
  place-items: center;
  transition:
    border-color 140ms ease,
    transform 140ms ease,
    background-color 140ms ease,
    color 140ms ease;
}

.life-action-button:hover {
  border-color: var(--life-shell-subtle);
  background: var(--life-shell-hover);
  color: var(--life-shell-text);
}

.life-action-button:active {
  transform: translateY(1px);
}

.life-action-button__icon-wrap {
  position: relative;
  display: grid;
  place-items: center;
}

.life-action-button__icon {
  width: 16px;
  height: 16px;
}

.life-message__badge {
  position: absolute;
  top: -4px;
  right: -7px;
  min-width: 16px;
  height: 16px;
  border: 2px solid var(--life-shell-control);
  border-radius: 999px;
  background: #ef4444;
  padding: 0 4px;
  color: #ffffff;
  font-size: 10px;
  font-weight: 700;
  line-height: 12px;
  text-align: center;
}

@media (max-width: 960px) {
  .life-header {
    padding-inline: 16px;
  }
}

@media (max-width: 760px) {
  .life-header {
    gap: 12px;
  }
}
</style>
