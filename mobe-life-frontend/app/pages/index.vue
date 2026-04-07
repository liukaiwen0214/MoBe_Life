<script setup lang="ts">
const appStore = useAppStore();
const config = useRuntimeConfig();
</script>

<template>
  <div class="space-y-6">
    <div>
      <div class="text-3xl font-semibold">{{ appStore.appName }}</div>
      <div class="mt-2 text-sm text-(--mobe-text-secondary)">
        当前前端初始化已完成，后续模块可以直接往这个壳子里加
      </div>
    </div>

    <div class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
      <div
        class="rounded-2xl border border-(--mobe-border) bg-(--mobe-card) p-5 shadow-sm"
      >
        <div class="text-sm text-(--mobe-text-secondary)">运行状态</div>
        <div class="mt-3 text-xl font-semibold">正常</div>
      </div>

      <div
        class="rounded-2xl border border-(--mobe-border) bg-(--mobe-card) p-5 shadow-sm"
      >
        <div class="text-sm text-(--mobe-text-secondary)">API Base</div>
        <div class="mt-3 break-all text-sm font-medium">
          {{ config.public.apiBase }}
        </div>
      </div>

      <div
        class="rounded-2xl border border-(--mobe-border) bg-(--mobe-card) p-5 shadow-sm"
      >
        <div class="text-sm text-(--mobe-text-secondary)">UI</div>
        <div class="mt-3 text-xl font-semibold">Nuxt UI</div>
      </div>

      <div
        class="rounded-2xl border border-(--mobe-border) bg-(--mobe-card) p-5 shadow-sm"
      >
        <div class="text-sm text-(--mobe-text-secondary)">状态管理</div>
        <div class="mt-3 text-xl font-semibold">Pinia</div>
      </div>
    </div>
  </div>
</template>
