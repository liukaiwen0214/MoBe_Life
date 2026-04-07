<template>
  <div class="min-h-screen bg-(--mobe-bg) text-(--mobe-text)">
    <LayoutAppHeader />
    <div class="flex h-[calc(100vh-4rem)]">
      <LayoutAppSidebar />
      <main class="flex-1 overflow-auto p-6">
        <slot />
      </main>
    </div>
  </div>
</template>
