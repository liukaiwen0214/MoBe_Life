<!--
  定义 Nuxt 应用最外层壳子，把 Nuxt UI 容器、布局与路由串联起来。
  模块：前端基础设施 / 应用根组件。
  约束：必须保留 `UApp` 作为 Nuxt UI 组件上下文根节点，否则部分 UI 能力会失效。
-->
<template>
  <UApp>
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </UApp>
</template>
