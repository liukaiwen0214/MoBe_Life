<!--
  承载首页主内容区，把今日执行、专注、财务与动态聚成一屏总览。
  模块：前端展示层 / 首页工作台。
  约束：顶部导航、侧边导航和底部信息由外部布局注入，本页面只负责主内容区域。
-->
<script setup lang="ts">
definePageMeta({
  middleware: 'auth',
})

const { currentUser, fetchCurrentUser } = useAuth()

const todayTasks = [
  { title: '完成 Q2 报表初稿', status: '进行中', tag: '报告', active: true },
  { title: '整理客户方案确认点', status: '待开始', tag: '方案', active: false },
  { title: '准备团队周会材料', status: '待开始', tag: '会议', active: false },
]

const financeItems = [
  { name: '餐饮', amount: '¥186' },
  { name: '交通', amount: '¥45' },
  { name: '其他', amount: '¥0' },
]

const activities = [
  { title: '完成 Q2 报表初稿', meta: '待办 · 今天 10:30', active: true },
  { title: '新增项目会议记录', meta: '笔记 · 昨天 18:12', active: false },
  { title: '记录餐饮支出 ¥186', meta: '财务 · 今天 08:45', active: false },
  { title: '客户方案确认点已整理', meta: '目标 · 昨天 16:20', active: false },
  { title: '团队周会已加入日历', meta: '日历 · 周五 09:30', active: false },
]

onMounted(async () => {
  if (!currentUser.value) {
    try {
      await fetchCurrentUser()
    } catch (error) {
      console.error('获取当前用户失败', error)
    }
  }
})
</script>

<template>
  <section class="mx-auto flex w-full max-w-7xl flex-col gap-6">
    <div class="grid grid-cols-1 gap-6 xl:grid-cols-[1.75fr_1fr_1fr]">
      <article class="mobe-card min-h-48">
        <div class="mb-6 flex items-center justify-between gap-4">
          <h2 class="text-xl font-semibold text-slate-900">今日待办</h2>
          <span class="text-sm font-medium text-slate-500">3 项待完成</span>
        </div>

        <div class="space-y-5">
          <div
            v-for="task in todayTasks"
            :key="task.title"
            class="flex items-center gap-4"
          >
            <span
              class="grid size-5 shrink-0 place-items-center rounded-md border-2"
              :class="task.active ? 'border-emerald-500' : 'border-slate-300'"
            />
            <span class="min-w-0 flex-1 truncate text-base font-semibold text-slate-800">
              {{ task.title }}
            </span>
            <div class="flex shrink-0 items-center gap-3">
              <span
                class="rounded-lg px-3 py-1 text-sm font-semibold"
                :class="
                  task.active
                    ? 'bg-amber-50 text-amber-600'
                    : 'bg-emerald-50 text-emerald-600'
                "
              >
                {{ task.status }}
              </span>
              <span class="rounded-lg bg-indigo-50 px-3 py-1 text-sm font-semibold text-indigo-600">
                {{ task.tag }}
              </span>
            </div>
          </div>
        </div>
      </article>

      <article class="mobe-card">
        <p class="text-sm font-semibold text-slate-500">下一段 · 深度工作</p>
        <h2 class="mt-3 text-3xl font-bold tracking-normal text-slate-900">
          10:30 - 12:00
        </h2>
        <p class="mt-3 text-base font-medium text-slate-500">报表初稿收尾</p>

        <div class="mt-5 flex items-center justify-between rounded-xl border border-slate-200 bg-slate-50 px-5 py-4">
          <span class="text-sm font-medium text-slate-500">距离开始</span>
          <span class="text-2xl font-bold text-emerald-600">24:18</span>
        </div>

        <button class="mt-4 h-11 w-full rounded-xl border border-emerald-300 bg-white text-base font-semibold text-emerald-700 transition hover:border-emerald-500 hover:bg-emerald-50">
          开始
        </button>
      </article>

      <article class="mobe-card relative overflow-hidden">
        <div class="pointer-events-none absolute inset-0 text-emerald-500/90">
          <span class="absolute left-16 top-17 text-3xl">✦</span>
          <span class="absolute right-16 top-16 text-2xl text-slate-500">✦</span>
          <span class="absolute bottom-26 left-28 text-3xl">✧</span>
          <span class="absolute bottom-19 right-28 text-2xl">✦</span>
          <span class="absolute left-1/2 top-20 text-4xl">✧</span>
        </div>

        <div class="relative flex min-h-60 flex-col items-center justify-center text-center">
          <div class="text-5xl font-bold text-emerald-600">6</div>
          <div class="mt-3 text-base font-semibold text-slate-500">灵光点</div>
          <button class="mt-8 h-11 w-full max-w-48 rounded-xl border border-emerald-300 bg-white text-base font-semibold text-emerald-700 transition hover:border-emerald-500 hover:bg-emerald-50">
            + 新增灵光点
          </button>
        </div>
      </article>
    </div>

    <div class="grid grid-cols-1 gap-6 xl:grid-cols-[1fr_1fr]">
      <article class="mobe-card">
        <div class="mb-7 flex items-center justify-between gap-4">
          <h2 class="text-xl font-semibold text-slate-900">财务概览</h2>
          <NuxtLink
            to="/finance"
            class="text-sm font-semibold text-slate-500 transition hover:text-emerald-600"
          >
            查看明细 →
          </NuxtLink>
        </div>

        <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
          <div class="rounded-xl bg-emerald-50 px-5 py-4">
            <div class="text-sm font-semibold text-emerald-700">本月收入</div>
            <div class="mt-2 text-3xl font-bold text-emerald-900">¥25,000</div>
          </div>
          <div class="rounded-xl bg-amber-50 px-5 py-4">
            <div class="text-sm font-semibold text-amber-700">本月支出</div>
            <div class="mt-2 text-3xl font-bold text-amber-900">¥231</div>
          </div>
        </div>

        <div class="mt-7">
          <div class="mb-3 text-base font-semibold text-slate-800">
            预算剩余 ¥2,430 · 使用 18%
          </div>
          <div class="h-2.5 overflow-hidden rounded-full bg-slate-200">
            <div class="h-full w-[18%] rounded-full bg-emerald-500" />
          </div>
        </div>

        <div class="mt-16">
          <h3 class="mb-4 text-base font-semibold text-slate-900">最近支出</h3>
          <div class="space-y-4">
            <div
              v-for="item in financeItems"
              :key="item.name"
              class="flex items-center justify-between text-base"
            >
              <span class="text-slate-500">{{ item.name }}</span>
              <span class="font-semibold text-slate-800">{{ item.amount }}</span>
            </div>
          </div>
        </div>
      </article>

      <article class="mobe-card">
        <div class="mb-7 flex items-center justify-between gap-4">
          <h2 class="text-xl font-semibold text-slate-900">最近动态</h2>
          <span class="rounded-lg bg-slate-100 px-3 py-1 text-sm font-semibold text-slate-500">
            5 条
          </span>
        </div>

        <div class="space-y-0">
          <div
            v-for="(activity, index) in activities"
            :key="activity.title"
            class="grid grid-cols-[1.5rem_1fr] gap-3"
          >
            <div class="flex flex-col items-center">
              <span
                class="mt-1 size-3 rounded-full"
                :class="activity.active ? 'bg-emerald-500' : 'bg-slate-300'"
              />
              <span
                v-if="index < activities.length - 1"
                class="mt-2 h-9 w-px bg-slate-200"
              />
            </div>
            <div class="pb-3">
              <div class="text-base font-semibold text-slate-900">
                {{ activity.title }}
              </div>
              <div class="mt-1 text-sm font-medium text-slate-500">
                {{ activity.meta }}
              </div>
            </div>
          </div>
        </div>

        <NuxtLink
          to="/tasks"
          class="mt-1 inline-flex text-sm font-semibold text-emerald-600 transition hover:text-emerald-700"
        >
          查看全部 →
        </NuxtLink>
      </article>
    </div>
  </section>
</template>

<style scoped>
.mobe-card {
  border: 1px solid rgb(226 232 240);
  border-radius: 12px;
  background: rgb(255 255 255);
  padding: 28px;
  box-shadow: 0 1px 2px rgb(15 23 42 / 0.03);
}

@media (max-width: 640px) {
  .mobe-card {
    padding: 20px;
  }
}
</style>
