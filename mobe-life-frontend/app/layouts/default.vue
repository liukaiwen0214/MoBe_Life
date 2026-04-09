<!--
  核心职责：定义后台式默认布局，统一承载顶部栏、侧边导航和主内容区。
  所属业务模块：前端展示层 / 布局系统。
  重要依赖关系或外部约束：该布局被所有未显式指定 layout 的页面复用，因此任何结构调整都会影响全站导航体验。
-->
<script setup lang="ts">
/**
 * 默认布局本身没有复杂状态。
 * 这里保留空脚本块，是为了让布局的设计意图和未来扩展入口都集中在同一文件中。
 */
</script>

<template>
  <div class="min-h-screen bg-(--mobe-bg) text-(--mobe-text)">
    <LayoutAppHeader />

    <div class="flex h-[calc(100vh-4rem)]">
      <LayoutAppSidebar />

      <!-- 主内容区单独滚动，避免顶部栏和侧边栏跟随页面内容一起抖动。 -->
      <main class="flex-1 overflow-auto p-6">
        <slot />
      </main>
    </div>
  </div>
</template>
