<script setup lang="ts">
const route = useRoute();

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
