<script setup lang="ts">
const route = useRoute();

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
