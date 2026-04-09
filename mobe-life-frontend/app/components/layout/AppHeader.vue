<!--
  文件级注释：
  核心职责：渲染顶部导航栏，显示当前页面标题和系统状态信息。
  所属业务模块：前端展示层 - 布局组件。
  重要依赖：
  - Vue Router（useRoute）：获取当前路由路径，映射到对应标题
  - Vue 的 computed：响应式计算页面标题
  设计约束：
  - 固定高度 4rem（64px），与布局中的 calc(100vh - 4rem) 对应
  - 使用 CSS 变量实现主题适配
-->

<script setup lang="ts">
/**
 * 顶部导航栏组件脚本。
 *
 * 设计初衷：提供页面顶部的品牌展示和上下文信息，
 * 让用户始终清楚当前所在页面和系统状态。
 *
 * 在架构中的角色：布局组件（Layout Component），
 * 被 default.vue 布局引用，渲染在每个页面的顶部。
 *
 * 核心功能：
 * - 根据当前路由路径动态显示页面标题
 * - 显示系统品牌副标题
 * - 展示系统状态标签（如"开发中"）
 *
 * 路由映射规则：
 * - "/" → "首页"
 * - "/goals" → "目标"
 * - "/tasks" → "任务"
 * - "/finance" → "财务"
 * - "/habits" → "习惯"
 * - 其他路径 → 默认显示 "MoBe Life"
 */

/**
 * 当前路由对象，包含路径、参数、查询字符串等信息。
 * 为什么使用 useRoute：Nuxt/Vue 提供的组合式函数，响应式追踪路由变化。
 */
const route = useRoute();

/**
 * 路由路径到页面标题的映射表。
 * 设计为 Record 类型，确保类型安全（路径字符串 → 标题字符串）。
 * 为什么不直接在模板中判断：集中管理映射关系，便于维护和扩展。
 */
const titleMap: Record<string, string> = {
  "/": "首页",
  "/goals": "目标",
  "/tasks": "任务",
  "/finance": "财务",
  "/habits": "习惯",
};

/**
 * 计算属性：根据当前路由路径获取页面标题。
 * 为什么用 computed：
 * - 响应式：路由变化时自动重新计算
 * - 缓存：路径不变时返回缓存值，避免重复计算
 * 默认值：当路径不在映射表中时，显示应用名称 "MoBe Life"。
 */
const pageTitle = computed(() => titleMap[route.path] || "MoBe Life");
</script>

<template>
  <!--
    顶部导航栏容器：
    - h-16：固定高度 4rem（64px）
    - flex + items-center：垂直居中内部元素
    - justify-between：左右分布（标题在左，状态在右）
    - border-b：底部边框分隔
    - bg-(--mobe-card)：使用 CSS 变量设置背景色
    - px-6：水平内边距 1.5rem（24px）
  -->
  <header
    class="flex h-16 items-center justify-between border-b border-(--mobe-border) bg-(--mobe-card) px-6"
  >
    <!-- 左侧：页面标题和品牌信息 -->
    <div>
      <!-- 主标题：动态显示当前页面名称 -->
      <div class="text-lg font-semibold text-(--mobe-text)">
        {{ pageTitle }}
      </div>
      <!-- 副标题：固定显示系统名称 -->
      <div class="text-xs text-(--mobe-text-secondary)">MoBe Life 个人系统</div>
    </div>

    <!-- 右侧：系统状态展示 -->
    <div class="flex items-center gap-3">
      <!--
        状态标签：显示当前系统状态。
        当前硬编码为"开发中"，未来可扩展为动态状态（如连接状态、通知数量等）。
      -->
      <div
        class="rounded-xl bg-(--mobe-bg) px-3 py-2 text-sm text-(--mobe-text-secondary)"
      >
        开发中
      </div>
    </div>
  </header>
</template>
