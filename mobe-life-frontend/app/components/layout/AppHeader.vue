<!--
  核心职责：提供个人工作台顶部轻操作栏，承载当前页面标题、搜索、通知和用户入口。
  所属业务模块：前端展示层 / 布局组件。
  重要依赖关系或外部约束：侧栏负责模块切换，顶部不承载全量导航。
-->
<script setup lang="ts">
const route = useRoute()
const { currentUser } = useAuth()

/**
 * 页面标题映射集中维护在这里，避免标题字符串散落在布局和页面里双向重复维护。
 */
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
const userName = computed(() => currentUser.value?.nickname || '李明')
const userInitial = computed(() => userName.value.slice(0, 1).toUpperCase())
</script>

<template>
  <header class="life-header">
    <h1 class="life-header__title">{{ pageTitle }}</h1>

    <div class="life-header__actions">
      <label class="life-search">
        <UIcon name="i-lucide-search" class="life-search__icon" />
        <input class="life-search__input" placeholder="搜索..." type="search" />
      </label>

      <button class="life-icon-button" type="button" aria-label="通知">
        <UIcon name="i-lucide-bell" />
        <span class="life-icon-button__dot" />
      </button>

      <button class="life-user" type="button" aria-label="用户">
        {{ userInitial }}
      </button>
    </div>
  </header>
</template>

<style scoped>
.life-header {
  display: flex;
  height: 64px;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #eef1ec;
  background: #ffffff;
  padding: 0 24px 0 28px;
}

.life-header__title {
  margin: 0;
  color: #172018;
  font-size: 20px;
  font-weight: 720;
  line-height: 1;
}

.life-header__actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.life-search {
  display: flex;
  width: 260px;
  height: 38px;
  align-items: center;
  gap: 9px;
  border: 1px solid #e5eae2;
  border-radius: 999px;
  background: #fbfcfa;
  padding: 0 14px;
  color: #8b958d;
}

.life-search__icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.life-search__input {
  min-width: 0;
  flex: 1;
  border: 0;
  outline: 0;
  background: transparent;
  color: #243024;
  font-size: 14px;
}

.life-search__input::placeholder {
  color: #9aa49b;
}

.life-icon-button,
.life-user {
  position: relative;
  display: grid;
  width: 38px;
  height: 38px;
  place-items: center;
  border: 1px solid #e5eae2;
  border-radius: 999px;
  background: #ffffff;
  color: #5d685f;
}

.life-icon-button {
  font-size: 18px;
}

.life-icon-button__dot {
  position: absolute;
  top: 8px;
  right: 9px;
  width: 7px;
  height: 7px;
  border: 2px solid #ffffff;
  border-radius: 999px;
  background: #13b981;
}

.life-user {
  border-color: #172018;
  background: #172018;
  color: #ffffff;
  font-size: 14px;
  font-weight: 700;
}

@media (max-width: 760px) {
  .life-header {
    padding-inline: 18px;
  }

  .life-search {
    width: 180px;
  }
}
</style>
