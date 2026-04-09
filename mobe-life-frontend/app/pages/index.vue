<!--
  文件级注释：
  核心职责：渲染应用首页（Dashboard），展示系统概览和运行状态。
  所属业务模块：前端展示层 - 页面组件。
  重要依赖：
  - Pinia（useAppStore）：读取应用全局状态
  - Nuxt（useRuntimeConfig）：读取运行时配置
  设计约束：
  - 使用默认布局（layouts/default.vue）
  - 响应式网格布局，适配不同屏幕尺寸
-->

<script setup lang="ts">
/**
 * 首页组件脚本。
 *
 * 设计初衷：作为应用的入口页面，向用户展示系统概览和当前状态，
 * 提供快速了解系统运行情况的 Dashboard 视图。
 *
 * 在架构中的角色：页面组件（Page Component），
 * 对应路由路径 "/"，是用户进入应用后看到的第一个页面。
 *
 * 核心功能：
 * - 展示应用名称和简介
 * - 显示系统运行状态卡片（运行状态、API 地址、UI 框架、状态管理）
 * - 验证全局状态和配置读取是否正常
 *
 * 扩展建议：
 * - 添加用户待办任务概览
 * - 显示今日习惯打卡进度
 * - 展示财务收支摘要
 * - 提供快速操作入口（新建任务、记录支出等）
 */

/**
 * 应用全局状态 Store 实例。
 * 用途：读取应用名称等全局配置，验证 Pinia 状态管理是否正常工作。
 */
const appStore = useAppStore();

/**
 * 运行时配置对象。
 * 用途：读取 API 基础地址等配置，验证运行时配置是否正确注入。
 * 为什么在这里读取：首页作为系统入口，适合展示关键配置信息，便于开发调试。
 */
const config = useRuntimeConfig();
</script>

<template>
  <!-- 页面内容容器：垂直间距 1.5rem（24px） -->
  <div class="space-y-6">
    <!-- 页面标题区域 -->
    <div>
      <!-- 主标题：从全局状态读取应用名称 -->
      <div class="text-3xl font-semibold">{{ appStore.appName }}</div>
      <!-- 副标题：开发提示信息 -->
      <div class="mt-2 text-sm text-(--mobe-text-secondary)">
        当前前端初始化已完成，后续模块可以直接往这个壳子里加
      </div>
    </div>

    <!--
      状态卡片网格：
      - gap-4：卡片间距 1rem（16px）
      - md:grid-cols-2：中等屏幕以上 2 列
      - xl:grid-cols-4：大屏幕以上 4 列
      - 小屏幕默认 1 列（响应式设计）
    -->
    <div class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
      <!-- 卡片 1：运行状态 -->
      <div
        class="rounded-2xl border border-(--mobe-border) bg-(--mobe-card) p-5 shadow-sm"
      >
        <div class="text-sm text-(--mobe-text-secondary)">运行状态</div>
        <div class="mt-3 text-xl font-semibold">正常</div>
      </div>

      <!-- 卡片 2：API 基础地址 -->
      <div
        class="rounded-2xl border border-(--mobe-border) bg-(--mobe-card) p-5 shadow-sm"
      >
        <div class="text-sm text-(--mobe-text-secondary)">API Base</div>
        <!-- break-all：长 URL 自动换行，防止溢出 -->
        <div class="mt-3 break-all text-sm font-medium">
          {{ config.public.apiBase }}
        </div>
      </div>

      <!-- 卡片 3：UI 框架 -->
      <div
        class="rounded-2xl border border-(--mobe-border) bg-(--mobe-card) p-5 shadow-sm"
      >
        <div class="text-sm text-(--mobe-text-secondary)">UI</div>
        <div class="mt-3 text-xl font-semibold">Nuxt UI</div>
      </div>

      <!-- 卡片 4：状态管理 -->
      <div
        class="rounded-2xl border border-(--mobe-border) bg-(--mobe-card) p-5 shadow-sm"
      >
        <div class="text-sm text-(--mobe-text-secondary)">状态管理</div>
        <div class="mt-3 text-xl font-semibold">Pinia</div>
      </div>
    </div>
  </div>
</template>
