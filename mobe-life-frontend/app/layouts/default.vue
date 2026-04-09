<!--
  文件级注释：
  核心职责：定义应用的默认布局结构，包含顶部导航栏、侧边栏和主内容区域。
  所属业务模块：前端展示层 - 布局系统。
  重要依赖：
  - LayoutAppHeader：顶部导航栏组件
  - LayoutAppSidebar：侧边导航栏组件
  - Nuxt 的 slot：内容注入点，页面内容将渲染在此处
  设计约束：
  - 使用 CSS 变量（--mobe-*）实现主题定制
  - 响应式布局：侧边栏固定宽度，主内容区自适应
-->

<script setup lang="ts">
/**
 * 默认布局组件脚本。
 *
 * 设计初衷：提供应用的标准页面框架，确保所有页面具有一致的导航和视觉结构。
 *
 * 在架构中的角色：布局层（Layout Layer），作为页面组件的包装器，
 * 定义页面在浏览器视口中的整体结构和定位。
 *
 * 布局结构：
 * - 顶部：固定高度 4rem（64px）的导航栏
 * - 左侧：固定宽度 14rem（224px）的侧边栏
 * - 主区域：剩余空间，可滚动
 *
 * 使用方式：
 * - 页面默认使用此布局（Nuxt 约定：layouts/default.vue）
 * - 如需自定义布局，页面中定义 <script setup> definePageMeta({ layout: 'custom' })
 *
 * 主题系统：
 * - 使用 CSS 变量定义颜色，便于主题切换
 * - --mobe-bg：背景色
 * - --mobe-text：主文本色
 * - --mobe-card：卡片背景色
 * - --mobe-border：边框色
 */
</script>

<template>
  <!--
    根容器：最小高度为视口高度，确保页面始终撑满屏幕。
    使用 CSS 变量设置背景和文字颜色，支持主题定制。
  -->
  <div class="min-h-screen bg-(--mobe-bg) text-(--mobe-text)">
    <!-- 顶部导航栏：固定高度，显示页面标题和用户信息 -->
    <LayoutAppHeader />

    <!--
      主布局区域：Flex 布局，高度为视口减去顶部栏高度（calc(100vh - 4rem)）。
      确保侧边栏和主内容区在同一行，且不会超出视口。
    -->
    <div class="flex h-[calc(100vh-4rem)]">
      <!-- 侧边栏：固定宽度，包含主导航菜单 -->
      <LayoutAppSidebar />

      <!--
        主内容区域：
        - flex-1：占据剩余所有宽度
        - overflow-auto：内容超出时显示滚动条
        - p-6：内边距 1.5rem（24px）
      -->
      <main class="flex-1 overflow-auto p-6">
        <!--
          slot：页面内容注入点。
          Nuxt 会自动将匹配当前路由的页面组件渲染在此处。
        -->
        <slot />
      </main>
    </div>
  </div>
</template>
