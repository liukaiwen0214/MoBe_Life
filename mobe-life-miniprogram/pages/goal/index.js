/**
 * 核心职责：承载小程序页面 `goal` 的交互逻辑和数据流。
 * 所属业务模块：小程序展示层 / 页面逻辑。
 * 重要依赖关系或外部约束：页面脚本通常与同目录的 WXML、WXSS 配合工作，字段命名应与模板绑定保持一致。
 */
import { getGoalList } from '../../api/goal'

Page({
  data: {
    loading: false,
    needRefresh: false,
    statCards: [
      {
        key: 'goal',
        label: '目标',
        activeCount: 0,
        completedCount: 0,
      },
      {
        key: 'node',
        label: '节点',
        activeCount: 0,
        completedCount: 0,
      },
      {
        key: 'task',
        label: '待办',
        activeCount: 0,
        completedCount: 0,
      },
    ],
    activeGoalList: [],
    completedGoalList: [],
  },

  onShow() {
    if (this.data.needRefresh) {
      this.setData({ needRefresh: false })
      this.loadGoalList()
    }
  },

  onLoad() {
    this.loadGoalList()
  },

  async loadGoalList() {
    this.setData({ loading: true })

    try {
      const res = await getGoalList({
        pageNum: 1,
        pageSize: 50,
        includeCompleted: true,
      })

      const rawList = Array.isArray(res?.list) ? res.list : []
      const goalList = rawList.map((item) => this.formatGoalItem(item))
      const grouped = this.groupGoalList(goalList)
      const statCards = this.buildStatCards(goalList)

      this.setData({
        activeGoalList: grouped.activeGoalList,
        completedGoalList: grouped.completedGoalList,
        statCards,
      })
    } catch (error) {
      console.error('加载目标列表失败', error)
      wx.showToast({
        title: '目标加载失败',
        icon: 'none',
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  formatGoalItem(item) {
    const completedCount = item.completedCount || 0
    const totalCount = item.totalCount || 0
    const isCompleted = this.isGoalCompleted(item)

    return {
      id: item.id,
      title: item.title || '未命名目标',
      nodeCount: item.nodeCount || 0,
      taskCount: item.taskCount || 0,
      completedCount,
      totalCount,
      progressText: `${completedCount}/${totalCount}`,
      statusText: isCompleted ? '已完成' : (item.statusText || '进行中'),
      updateText: isCompleted
        ? this.formatCompletedText(item.completedTime || item.updateTime)
        : this.formatUpdateText(item.updateTime),
      isCompleted,
    }
  },

  isGoalCompleted(item) {
    if (item?.isCompleted === 1 || item?.isCompleted === true) {
      return true
    }

    const statusCode = item?.statusCode || ''
    return ['DONE', 'COMPLETED', 'FINISHED'].includes(statusCode)
  },

  groupGoalList(goalList) {
    const activeGoalList = []
    const completedGoalList = []

    goalList.forEach((item) => {
      if (item.isCompleted) {
        completedGoalList.push(item)
      } else {
        activeGoalList.push(item)
      }
    })

    return {
      activeGoalList,
      completedGoalList,
    }
  },

  buildStatCards(goalList) {
    const activeGoalCount = goalList.filter((item) => !item.isCompleted).length
    const completedGoalCount = goalList.filter((item) => item.isCompleted).length

    const activeNodeCount = goalList
      .filter((item) => !item.isCompleted)
      .reduce((sum, item) => sum + (item.nodeCount || 0), 0)

    const completedNodeCount = goalList
      .filter((item) => item.isCompleted)
      .reduce((sum, item) => sum + (item.nodeCount || 0), 0)

    const activeTaskCount = goalList
      .filter((item) => !item.isCompleted)
      .reduce((sum, item) => sum + Math.max((item.taskCount || 0) - (item.completedCount || 0), 0), 0)

    const completedTaskCount = goalList
      .reduce((sum, item) => sum + (item.completedCount || 0), 0)

    return [
      {
        key: 'goal',
        label: '目标',
        activeCount: activeGoalCount,
        completedCount: completedGoalCount,
      },
      {
        key: 'node',
        label: '节点',
        activeCount: activeNodeCount,
        completedCount: completedNodeCount,
      },
      {
        key: 'task',
        label: '待办',
        activeCount: activeTaskCount,
        completedCount: completedTaskCount,
      },
    ]
  },

  formatUpdateText(updateTime) {
    if (!updateTime) {
      return '未更新'
    }

    const date = new Date(updateTime)
    if (Number.isNaN(date.getTime())) {
      return '未更新'
    }

    const now = new Date()
    const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime()
    const targetStart = new Date(date.getFullYear(), date.getMonth(), date.getDate()).getTime()
    const dayDiff = Math.round((todayStart - targetStart) / (24 * 60 * 60 * 1000))

    if (dayDiff === 0) {
      return '今天更新'
    }

    if (dayDiff === 1) {
      return '昨天更新'
    }

    return `${dayDiff} 天前更新`
  },

  formatCompletedText(time) {
    if (!time) {
      return '已完成'
    }

    const date = new Date(time)
    if (Number.isNaN(date.getTime())) {
      return '已完成'
    }

    const year = date.getFullYear()
    const month = `${date.getMonth() + 1}`.padStart(2, '0')
    const day = `${date.getDate()}`.padStart(2, '0')

    return `完成于 ${year}-${month}-${day}`
  },

  handleGoalTap(e) {
    const { goalId } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/goal/detail/detail?id=${goalId}`,
    })
  },

  handleCreateGoal() {
    wx.showToast({
      title: '进入新建目标',
      icon: 'none',
    })
  },
})