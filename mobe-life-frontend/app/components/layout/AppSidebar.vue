<!--
  核心职责：提供主功能模块导航入口，并通过当前路由高亮帮助用户建立位置感。
  所属业务模块：前端展示层 / 布局组件。
  重要依赖关系或外部约束：菜单结构与 `pages` 目录一一对应，新增主模块时需要同步补齐导航项。
-->
<script setup lang="ts">
const route = useRoute();

/**
 * 菜单顺序不是随意排列，而是按用户从总览到执行的常见心智路径组织。
 */
const menus = [
  { name: "首页", path: "/" },
  { name: "目标", path: "/goals" },
  { name: "任务", path: "/tasks" },
  { name: "财务", path: "/finance" },
  { name: "习惯", path: "/habits" },
];
</script>

<template>
  <aside
    class="flex h-full w-56 flex-col border-r border-(--mobe-border) bg-(--mobe-card) px-4 py-6"
  >
    <div class="mb-6 text-sm font-medium text-(--mobe-text-secondary)">
      导航
    </div>

    <nav class="flex flex-col gap-2">
      <NuxtLink
        v-for="item in menus"
        :key="item.path"
        :to="item.path"
        class="rounded-xl px-4 py-2 text-sm transition"
        :class="
          route.path === item.path
            ? 'bg-(--mobe-bg) text-(--mobe-text) font-medium'
            : 'text-(--mobe-text-secondary) hover:bg-(--mobe-bg) hover:text-(--mobe-text)'
        "
      >
        {{ item.name }}
      </NuxtLink>
    </nav>
  </aside>
</template>
