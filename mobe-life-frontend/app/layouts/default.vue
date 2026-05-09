<!--
  定义后台默认布局，统一承载侧栏、顶栏与主内容区。
  模块：前端展示层 / 布局系统。
  约束：该布局被所有未显式指定 layout 的页面复用，因此任何结构调整都会影响全站导航体验。
-->
<script setup lang="ts">
/**
 * 为默认布局提供主题状态，使外壳与主内容区同步切换明暗色。
 */
const appStore = useAppStore()
</script>

<template>
  <div
    class="life-layout"
    :class="{ 'life-layout--dark': appStore.isDarkTheme }"
  >
    <LayoutAppSidebar />

    <div class="life-layout__main">
      <LayoutAppHeader />

      <main class="life-layout__content">
        <slot />
      </main>

      <LayoutAppFooter />
    </div>
  </div>
</template>

<style scoped>
.life-layout {
  --life-shell-bg: #ffffff;
  --life-shell-border: #eef1ec;
  --life-shell-hover: #f4f6f2;
  --life-shell-active: #edf5eb;
  --life-shell-panel: #ffffff;
  --life-shell-text: #172018;
  --life-shell-muted: #657365;
  --life-shell-subtle: #8b968c;
  --life-shell-control: #fbfcfa;
  --life-shell-control-border: #e6ebe3;
  --life-content-bg: #ffffff;
  --life-content-card: #ffffff;
  --life-content-card-soft: #f8fafc;
  --life-content-border: #e2e8f0;
  --life-content-text: #0f172a;
  --life-content-muted: #64748b;
  --life-content-subtle: #94a3b8;
  --life-content-line: #e2e8f0;

  display: flex;
  height: 100vh;
  overflow: hidden;
  background: var(--life-content-bg);
  color: var(--life-shell-text);
  transition:
    background-color 220ms ease,
    color 220ms ease;
}

.life-layout--dark {
  --life-shell-bg: #050505;
  --life-shell-border: #1f2937;
  --life-shell-hover: #18181b;
  --life-shell-active: #14532d;
  --life-shell-panel: #111111;
  --life-shell-text: #ffffff;
  --life-shell-muted: #d4d4d8;
  --life-shell-subtle: #a1a1aa;
  --life-shell-control: #111111;
  --life-shell-control-border: #27272a;
  --life-content-bg: #050505;
  --life-content-card: #111111;
  --life-content-card-soft: #18181b;
  --life-content-border: #27272a;
  --life-content-text: #f8fafc;
  --life-content-muted: #a1a1aa;
  --life-content-subtle: #71717a;
  --life-content-line: #27272a;
}

.life-layout__main {
  display: flex;
  min-width: 0;
  height: 100vh;
  flex: 1;
  flex-direction: column;
  overflow: hidden;
}

.life-layout__content {
  min-width: 0;
  min-height: 0;
  flex: 1;
  overflow: auto;
  background: var(--life-content-bg);
  padding: 24px;
  transition:
    background-color 220ms ease,
    color 220ms ease;
}

.life-layout :deep(.mobe-card) {
  border-color: var(--life-content-border);
  background: var(--life-content-card);
  transition:
    background-color 220ms ease,
    border-color 220ms ease,
    box-shadow 220ms ease;
}

.life-layout--dark :deep(.mobe-card) {
  box-shadow: 0 1px 2px rgb(0 0 0 / 0.24);
}

.life-layout--dark .life-layout__content :deep(.text-slate-900),
.life-layout--dark .life-layout__content :deep(.text-slate-800) {
  color: var(--life-content-text);
}

.life-layout--dark .life-layout__content :deep(.text-slate-500) {
  color: var(--life-content-muted);
}

.life-layout--dark .life-layout__content :deep(.text-slate-400) {
  color: var(--life-content-subtle);
}

.life-layout--dark .life-layout__content :deep(.bg-white),
.life-layout--dark .life-layout__content :deep(.bg-slate-50),
.life-layout--dark .life-layout__content :deep(.bg-slate-100) {
  background-color: var(--life-content-card-soft);
}

.life-layout--dark .life-layout__content :deep(.bg-slate-200) {
  background-color: var(--life-content-line);
}

.life-layout--dark .life-layout__content :deep(.border-slate-200),
.life-layout--dark .life-layout__content :deep(.border-slate-300) {
  border-color: var(--life-content-border);
}

.life-layout .life-layout__content :deep(.text-slate-900),
.life-layout .life-layout__content :deep(.text-slate-800),
.life-layout .life-layout__content :deep(.text-slate-500),
.life-layout .life-layout__content :deep(.text-slate-400),
.life-layout .life-layout__content :deep(.bg-white),
.life-layout .life-layout__content :deep(.bg-slate-50),
.life-layout .life-layout__content :deep(.bg-slate-100),
.life-layout .life-layout__content :deep(.bg-slate-200),
.life-layout .life-layout__content :deep(.border-slate-200),
.life-layout .life-layout__content :deep(.border-slate-300) {
  transition:
    background-color 220ms ease,
    border-color 220ms ease,
    color 220ms ease;
}
</style>
