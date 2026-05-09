<!--
  核心职责：提供个人使用场景下的轻量左侧导航。
  所属业务模块：前端展示层 / 布局组件。
  重要依赖关系或外部约束：顶部栏负责页面标题和操作，侧栏只负责模块切换。
-->
<script setup lang="ts">
const route = useRoute()
const { currentUser, logout } = useAuth()

const menuGroups = [
  {
    name: '事项',
    icon: 'i-lucide-list-checks',
    items: [
      { name: '待办', path: '/tasks', icon: 'i-lucide-check-square' },
      { name: '节点', path: '/nodes', icon: 'i-lucide-git-commit-horizontal' },
      { name: '目标', path: '/goals', icon: 'i-lucide-target' },
      { name: '项目', path: '/projects', icon: 'i-lucide-folder' },
    ],
  },
  {
    name: '资金',
    icon: 'i-lucide-wallet',
    items: [
      { name: '账单', path: '/bills', icon: 'i-lucide-receipt-text' },
      { name: '账户', path: '/accounts', icon: 'i-lucide-credit-card' },
      { name: '统计', path: '/statistics', icon: 'i-lucide-chart-no-axes-column' },
    ],
  },
  {
    name: '娱乐',
    icon: 'i-lucide-sparkles',
    items: [
      { name: '清单', path: '/lists', icon: 'i-lucide-list' },
      { name: '待加入', icon: 'i-lucide-plus', placeholder: true },
      { name: '待加入', icon: 'i-lucide-plus', placeholder: true },
    ],
  },
  {
    name: '物品',
    icon: 'i-lucide-package',
    items: [
      { name: '库存', path: '/inventory', icon: 'i-lucide-box' },
      { name: '待加入', icon: 'i-lucide-plus', placeholder: true },
      { name: '待加入', icon: 'i-lucide-plus', placeholder: true },
    ],
  },
]

const userName = computed(() => currentUser.value?.nickname || '李明')
const userEmail = computed(() => currentUser.value?.email || 'liming@mobe.app')
const userInitial = computed(() => userName.value.slice(0, 1).toUpperCase())

const isActive = (path: string) => {
  if (path === '/') return route.path === '/'
  return route.path === path || route.path.startsWith(`${path}/`)
}

const groupIsActive = (items: Array<{ path?: string }>) => {
  return items.some((item) => item.path && isActive(item.path))
}
</script>

<template>
  <aside class="life-sidebar">
    <NuxtLink to="/" class="life-brand" aria-label="MoBe 首页">
      <span class="life-brand__mark">M</span>
      <span class="life-brand__text">MoBe</span>
    </NuxtLink>

    <nav class="life-nav" aria-label="侧边导航">
      <NuxtLink
        to="/"
        class="life-home"
        :class="{ 'life-home--active': isActive('/') }"
      >
        <UIcon name="i-lucide-home" class="life-home__icon" />
        <span>首页</span>
      </NuxtLink>

      <section
        v-for="group in menuGroups"
        :key="group.name"
        class="life-group"
        :class="{ 'life-group--active': groupIsActive(group.items) }"
      >
        <div class="life-group__title">
          <UIcon :name="group.icon" class="life-group__icon" />
          <span>{{ group.name }}</span>
        </div>

        <div class="life-group__items">
          <NuxtLink
            v-for="item in group.items.filter((menuItem) => !menuItem.placeholder)"
            :key="item.path"
            :to="item.path"
            class="life-subitem"
            :class="{ 'life-subitem--active': item.path && isActive(item.path) }"
          >
            <UIcon :name="item.icon" class="life-subitem__icon" />
            <span>{{ item.name }}</span>
          </NuxtLink>

          <span
            v-for="(item, index) in group.items.filter((menuItem) => menuItem.placeholder)"
            :key="`${group.name}-placeholder-${index}`"
            class="life-subitem life-subitem--placeholder"
            aria-hidden="true"
          >
            <UIcon :name="item.icon" class="life-subitem__icon" />
            <span>{{ item.name }}</span>
          </span>
        </div>
      </section>
    </nav>

    <div class="life-sidebar__footer">
      <div class="life-sidebar__divider" />

      <div class="life-profile">
        <button class="life-profile__button" type="button">
          <span class="life-profile__avatar">{{ userInitial }}</span>
          <span class="life-profile__meta">
            <span class="life-profile__name">{{ userName }}</span>
            <span class="life-profile__hint">个人信息</span>
          </span>
          <UIcon name="i-lucide-chevron-up" class="life-profile__chevron" />
        </button>

        <div class="life-profile__panel">
          <div class="life-profile__summary">
            <span class="life-profile__avatar life-profile__avatar--large">
              {{ userInitial }}
            </span>
            <span>
              <strong>{{ userName }}</strong>
              <small>{{ userEmail }}</small>
            </span>
          </div>

          <NuxtLink to="/profile" class="life-profile__action">
            <UIcon name="i-lucide-user-round" />
            <span>个人中心</span>
          </NuxtLink>
          <NuxtLink to="/settings" class="life-profile__action">
            <UIcon name="i-lucide-settings" />
            <span>设置</span>
          </NuxtLink>
          <button class="life-profile__action" type="button" @click="logout">
            <UIcon name="i-lucide-log-out" />
            <span>退出</span>
          </button>
        </div>
      </div>
    </div>
  </aside>
</template>

<style scoped>
.life-sidebar {
  display: flex;
  width: 212px;
  height: 100%;
  flex-shrink: 0;
  flex-direction: column;
  border-right: 1px solid #eef1ec;
  background: #fbfcfa;
  padding: 22px 12px 14px;
}

.life-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 18px;
  padding: 0 10px;
  color: #172018;
  text-decoration: none;
}

.life-brand__mark {
  display: grid;
  width: 30px;
  height: 30px;
  place-items: center;
  border-radius: 9px;
  background: #13b981;
  color: #ffffff;
  font-size: 13px;
  font-weight: 760;
}

.life-brand__text {
  font-size: 17px;
  font-weight: 760;
}

.life-nav {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-height: 0;
  overflow-y: auto;
  padding-right: 2px;
}

.life-home,
.life-group__title,
.life-subitem,
.life-profile__button,
.life-profile__action {
  display: flex;
  align-items: center;
  text-decoration: none;
}

.life-home {
  height: 38px;
  gap: 10px;
  border-radius: 10px;
  padding: 0 10px;
  color: #4b574e;
  font-size: 14px;
  font-weight: 650;
  transition:
    background-color 140ms ease,
    color 140ms ease;
}

.life-home:hover,
.life-home--active {
  background: #edf5eb;
  color: #172018;
}

.life-home__icon {
  width: 17px;
  height: 17px;
}

.life-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.life-group__title {
  height: 28px;
  gap: 8px;
  padding: 0 10px;
  color: #8b968c;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0;
}

.life-group--active .life-group__title {
  color: #172018;
}

.life-group__icon {
  width: 14px;
  height: 14px;
}

.life-group__items {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.life-subitem {
  height: 34px;
  gap: 9px;
  border-radius: 9px;
  padding: 0 10px 0 28px;
  color: #5f6b61;
  font-size: 13px;
  font-weight: 560;
  transition:
    background-color 140ms ease,
    color 140ms ease,
    opacity 140ms ease;
}

.life-subitem:hover {
  background: #f1f4ef;
  color: #172018;
}

.life-subitem--active {
  background: #e8f4e8;
  color: #1f6c3a;
  font-weight: 700;
}

.life-subitem--placeholder {
  color: #b4beb5;
  cursor: default;
  opacity: 0.58;
}

.life-subitem--placeholder:hover {
  background: transparent;
  color: #b4beb5;
}

.life-subitem__icon {
  width: 15px;
  height: 15px;
}

.life-sidebar__divider {
  height: 1px;
  margin: 0 10px 12px;
  background: #edf1eb;
}

.life-sidebar__footer {
  margin-top: auto;
  padding-top: 14px;
}

.life-profile {
  position: relative;
}

.life-profile__button {
  width: 100%;
  height: 46px;
  gap: 10px;
  border: 0;
  border-radius: 12px;
  background: transparent;
  padding: 0 10px;
  color: #172018;
  text-align: left;
  cursor: default;
}

.life-profile__button:hover {
  background: #f1f4ef;
}

.life-profile__avatar {
  display: grid;
  width: 28px;
  height: 28px;
  flex-shrink: 0;
  place-items: center;
  border-radius: 999px;
  background: #172018;
  color: #ffffff;
  font-size: 12px;
  font-weight: 760;
}

.life-profile__avatar--large {
  width: 34px;
  height: 34px;
}

.life-profile__meta,
.life-profile__summary span {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
}

.life-profile__summary .life-profile__avatar {
  display: grid;
  flex: 0 0 34px;
}

.life-profile__name,
.life-profile__summary strong {
  overflow: hidden;
  color: #172018;
  font-size: 13px;
  font-weight: 720;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.life-profile__hint,
.life-profile__summary small {
  overflow: hidden;
  color: #8b968c;
  font-size: 11px;
  font-weight: 500;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.life-profile__chevron {
  width: 15px;
  height: 15px;
  color: #9aa49b;
}

.life-profile__panel {
  position: absolute;
  bottom: 54px;
  left: 2px;
  z-index: 20;
  display: flex;
  width: 196px;
  flex-direction: column;
  gap: 4px;
  border: 1px solid #e4e9df;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 16px 40px rgb(32 43 34 / 12%);
  opacity: 0;
  padding: 8px;
  pointer-events: none;
  transform: translateY(6px);
  transition:
    opacity 150ms ease,
    transform 150ms ease;
}

.life-profile:hover .life-profile__panel,
.life-profile:focus-within .life-profile__panel {
  opacity: 1;
  pointer-events: auto;
  transform: translateY(0);
}

.life-profile__summary {
  display: flex;
  align-items: center;
  gap: 10px;
  border-bottom: 1px solid #edf1eb;
  margin-bottom: 4px;
  padding: 6px 6px 10px;
}

.life-profile__action {
  width: 100%;
  height: 34px;
  gap: 9px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  padding: 0 8px;
  color: #536057;
  font-size: 13px;
  font-weight: 560;
  cursor: pointer;
}

.life-profile__action:hover {
  background: #f4f6f2;
  color: #172018;
}

.life-profile__action :deep(svg) {
  width: 15px;
  height: 15px;
}

@media (max-width: 900px) {
  .life-sidebar {
    width: 202px;
    padding-inline: 10px;
  }
}
</style>
