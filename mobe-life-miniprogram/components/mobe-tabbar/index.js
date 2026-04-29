/**
 * 核心职责：实现小程序组件 `index` 的交互逻辑，供多个页面复用。
 * 所属业务模块：小程序展示层 / 组件逻辑。
 * 重要依赖关系或外部约束：组件对外暴露的数据和事件接口应保持稳定，避免影响引用页面。
 */
const GLOBAL_NAV_ITEMS = [
  {
    key: 'writing',
    label: '事项',
    type: 'parent',
    pagePath: '/pages/task/index?subKey=todo',
    icon: '/assets/icons/tabbar/writing.svg',
  },
  {
    key: 'scale',
    label: '财务',
    type: 'parent',
    pagePath: '/pages/finance/index',
    icon: '/assets/icons/tabbar/scale.svg',
  },
  {
    key: 'now',
    label: '主页',
    type: 'fixed',
    pagePath: '/pages/index/index',
    icon: '/assets/icons/tabbar/now.svg',
  },
  {
    key: 'guide',
    label: '目标',
    type: 'fixed',
    pagePath: '/pages/goal/index',
    icon: '/assets/icons/tabbar/guide.svg',
  },
  {
    key: 'base',
    label: '个人',
    type: 'fixed',
    pagePath: '/pages/mine/index',
    icon: '/assets/icons/tabbar/base.svg',
  },
]

const TASK_MODULE_NAV_ITEMS = [
  {
    key: 'todo',
    label: '待办',
    pagePath: '/pages/task/index',
    icon: '/assets/icons/tabbar/tasks/todo.svg',
  },
  {
    key: 'node',
    label: '节点',
    pagePath: '/pages/node/index',
    icon: '/assets/icons/tabbar/tasks/node.svg',
  },
  {
    key: 'goal',
    label: '目标',
    pagePath: '/pages/goal/index',
    icon: '/assets/icons/tabbar/tasks/goal.svg',
  },
  {
    key: 'project',
    label: '项目',
    pagePath: '/pages/project/index',
    icon: '/assets/icons/tabbar/tasks/project.svg',
  },
  {
    key: 'home',
    label: '主页',
    pagePath: '/pages/index/index',
    icon: '/assets/icons/tabbar/now.svg',
  },
]

const FINANCE_MODULE_NAV_ITEMS = [
  {
    key: 'finance',
    label: '财务',
    pagePath: '/pages/finance/index',
    icon: '/assets/icons/tabbar/finance/finance.svg',
  },
  {
    key: 'bill',
    label: '账单',
    pagePath: '/pages/finance/bill/index',
    icon: '/assets/icons/tabbar/finance/bill.svg',
  },
  {
    key: 'budget',
    label: '预算',
    pagePath: '/pages/finance/budget/index',
    icon: '/assets/icons/tabbar/finance/budget.svg',
  },
  {
    key: 'account',
    label: '账户',
    pagePath: '/pages/finance/account/index',
    icon: '/assets/icons/tabbar/finance/account.svg',
  },
  {
    key: 'home',
    label: '首页',
    pagePath: '/pages/index/index',
    icon: '/assets/icons/tabbar/now.svg',
  },
]

Component({
  properties: {
    current: {
      type: String,
      value: '',
    },
    currentSub: {
      type: String,
      value: '',
    },
  },

  data: {
    globalNavItems: GLOBAL_NAV_ITEMS,
    taskModuleNavItems: TASK_MODULE_NAV_ITEMS,
    financeModuleNavItems: FINANCE_MODULE_NAV_ITEMS,
    isTaskModule: false,
    isFinanceModule: false,
  },

  observers: {
    current(current) {
      this.syncModuleState(current)
    },
  },

  lifetimes: {
    attached() {
      this.syncModuleState(this.properties.current)
    },
  },

  methods: {
    syncModuleState(current) {
      this.setData({
        isTaskModule: current === 'writing',
        isFinanceModule: current === 'scale',
      })
    },

    handleGlobalNavTap(e) {
      const { key } = e.currentTarget.dataset
      const nav = GLOBAL_NAV_ITEMS.find((item) => item.key === key)

      if (!nav) {
        return
      }

      if (this.properties.current === nav.key && nav.key !== 'writing' && nav.key !== 'scale') {
        return
      }

      wx.reLaunch({
        url: nav.pagePath,
      })
    },

    handleTaskModuleNavTap(e) {
      const { key } = e.currentTarget.dataset
      const nav = TASK_MODULE_NAV_ITEMS.find((item) => item.key === key)

      if (!nav) {
        return
      }

      if (key === 'home') {
        wx.reLaunch({
          url: nav.pagePath,
        })
        return
      }

      if (this.properties.currentSub === key) {
        return
      }

      wx.reLaunch({
        url: nav.pagePath,
      })
    },

    handleFinanceModuleNavTap(e) {
      const { key } = e.currentTarget.dataset
      const nav = FINANCE_MODULE_NAV_ITEMS.find((item) => item.key === key)

      if (!nav) {
        return
      }

      if (key === 'home') {
        wx.reLaunch({
          url: nav.pagePath,
        })
        return
      }

      if (this.properties.currentSub === key) {
        return
      }

      wx.reLaunch({
        url: nav.pagePath,
      })
    },
  },
})