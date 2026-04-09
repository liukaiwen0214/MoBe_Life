<!--
  核心职责：承载任务模块的 Web 端入口页。
  所属业务模块：前端展示层 / 任务管理。
  重要依赖关系或外部约束：当前占位页的主要意义是把布局、路由和导航先固定下来，后续才能平滑替换成真实任务列表。
-->
<template>
  <div
    class="rounded-2xl border border-(--mobe-border) bg-(--mobe-card) p-6 shadow-sm"
  >
    <div class="text-2xl font-semibold">任务</div>
    <div class="mt-2 text-sm text-(--mobe-text-secondary)">任务模块占位页</div>
  </div>
</template>
