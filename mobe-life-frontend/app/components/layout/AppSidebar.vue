<!--
  文件级注释：
  核心职责：渲染侧边导航栏，提供应用主要功能模块的导航入口。
  所属业务模块：前端展示层 - 布局组件。
  重要依赖：
  - Vue Router（useRoute）：获取当前路径，高亮激活的菜单项
  - NuxtLink：Nuxt 提供的导航组件，支持预加载和客户端导航
  设计约束：
  - 固定宽度 14rem（224px），与布局中的侧边栏宽度一致
  - 当前激活菜单项使用不同背景色和文字色区分
-->

<script setup lang="ts">
/**
 * 侧边导航栏组件脚本。
 *
 * 设计初衷：提供应用的核心导航结构，让用户快速在不同功能模块间切换。
 *
 * 在架构中的角色：布局组件（Layout Component），
 * 被 default.vue 布局引用，渲染在每个页面的左侧。
 *
 * 核心功能：
 * - 显示主导航菜单（首页、目标、任务、财务、习惯）
 * - 高亮当前激活的菜单项
 * - 提供悬停反馈增强交互体验
 *
 * 导航数据结构：
 * 每个菜单项包含：
 * - name：显示名称（中文）
 * - path：路由路径（与 pages 目录结构对应）
 */

/**
 * 当前路由对象，用于判断哪个菜单项处于激活状态。
 */
const route = useRoute();

/**
 * 导航菜单配置数组。
 * 设计为常量数组，因为导航结构在应用运行期间不变。
 * 顺序：按照用户使用的频率和逻辑关系排列（首页 → 目标 → 任务 → 财务 → 习惯）。
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
  <!--
    侧边栏容器：
    - h-full：高度填满父容器（由布局中的 flex 控制）
    - w-56：固定宽度 14rem（224px）
    - flex-col：垂直布局（标题在上，菜单在下）
    - border-r：右边框分隔主内容区
    - bg-(--mobe-card)：卡片背景色
    - px-4 py-6：内边距
  -->
  <aside
    class="flex h-full w-56 flex-col border-r border-(--mobe-border) bg-(--mobe-card) px-4 py-6"
  >
    <!-- 导航标题 -->
    <div class="mb-6 text-sm font-medium text-(--mobe-text-secondary)">
      导航
    </div>

    <!-- 导航菜单列表 -->
    <nav class="flex flex-col gap-2">
      <!--
        菜单项循环渲染：
        使用 NuxtLink 而非普通 <a> 标签，获得以下优势：
        - 客户端导航（无页面刷新）
        - 自动预加载（hover 时预取页面资源）
        - 激活状态样式（NuxtLink 自动添加 active class）
      -->
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
