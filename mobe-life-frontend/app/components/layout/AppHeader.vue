<!--
  核心职责：根据当前路由展示页面上下文，让用户始终知道自己正处在哪个功能模块。
  所属业务模块：前端展示层 / 布局组件。
  重要依赖关系或外部约束：标题映射依赖当前页面路由，如果新增页面而不更新映射，头部会退回通用标题。
-->
<script setup lang="ts">
const route = useRoute();

/**
 * 页面标题映射集中维护在这里，避免标题字符串散落在布局和页面里双向重复维护。
 */
const titleMap: Record<string, string> = {
  "/": "首页",
  "/goals": "目标",
  "/tasks": "任务",
  "/finance": "财务",
  "/habits": "习惯",
};

const pageTitle = computed(() => titleMap[route.path] || "MoBe Life");
</script>

<template>
  <header
    class="flex h-16 items-center justify-between border-b border-(--mobe-border) bg-(--mobe-card) px-6"
  >
    <div>
      <div class="text-lg font-semibold text-(--mobe-text)">
        {{ pageTitle }}
      </div>
      <div class="text-xs text-(--mobe-text-secondary)">MoBe Life 个人系统</div>
    </div>

    <div class="flex items-center gap-3">
      <div
        class="rounded-xl bg-(--mobe-bg) px-3 py-2 text-sm text-(--mobe-text-secondary)"
      >
        开发中
      </div>
    </div>
  </header>
</template>
