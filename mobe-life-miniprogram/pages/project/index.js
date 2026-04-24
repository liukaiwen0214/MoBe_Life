/**
 * 核心职责：承载小程序页面 `project` 的交互逻辑和数据流。
 * 所属业务模块：小程序展示层 / 页面逻辑。
 * 重要依赖关系或外部约束：页面脚本通常与同目录的 WXML、WXSS 配合工作，字段命名应与模板绑定保持一致。
 */
import { getProjectList } from '../../api/project'

Page({
  data: {
    loading: false,
    needRefresh: false,
    statCards: [
      {
        key: 'project',
        label: '项目',
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
    activeProjectList: [],
    completedProjectList: [],
  },

  onShow() {
    if (this.data.needRefresh) {
      this.setData({ needRefresh: false })
      this.loadProjectList()
    }
  },

  onLoad() {
    this.loadProjectList()
  },

  async loadProjectList() {
    this.setData({ loading: true })

    try {
      const res = await getProjectList({
        pageNum: 1,
        pageSize: 50,
        includeCompleted: true,
      })

      const rawList = Array.isArray(res?.list) ? res.list : []
      const projectList = rawList.map((item) => this.formatProjectItem(item))
      const grouped = this.groupProjectList(projectList)
      const statCards = this.buildStatCards(projectList)

      this.setData({
        activeProjectList: grouped.activeProjectList,
        completedProjectList: grouped.completedProjectList,
        statCards,
      })
    } catch (error) {
      console.error('加载项目列表失败', error)
      wx.showToast({
        title: '项目加载失败',
        icon: 'none',
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  formatProjectItem(item) {
    const completedCount = item.completedCount || 0
    const totalCount = item.totalCount || 0
    const isCompleted = this.isProjectCompleted(item)

    return {
      id: item.id,
      title: item.title || '未命名项目',
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

  isProjectCompleted(item) {
    if (item?.isCompleted === 1 || item?.isCompleted === true) {
      return true
    }

    const statusCode = item?.statusCode || ''
    return ['DONE', 'COMPLETED', 'FINISHED'].includes(statusCode)
  },

  groupProjectList(projectList) {
    const activeProjectList = []
    const completedProjectList = []

    projectList.forEach((item) => {
      if (item.isCompleted) {
        completedProjectList.push(item)
      } else {
        activeProjectList.push(item)
      }
    })

    return {
      activeProjectList,
      completedProjectList,
    }
  },

  buildStatCards(projectList) {
    const activeProjectCount = projectList.filter((item) => !item.isCompleted).length
    const completedProjectCount = projectList.filter((item) => item.isCompleted).length

    const activeNodeCount = projectList
      .filter((item) => !item.isCompleted)
      .reduce((sum, item) => sum + (item.nodeCount || 0), 0)

    const completedNodeCount = projectList
      .filter((item) => item.isCompleted)
      .reduce((sum, item) => sum + (item.nodeCount || 0), 0)

    const activeTaskCount = projectList
      .filter((item) => !item.isCompleted)
      .reduce((sum, item) => sum + Math.max((item.taskCount || 0) - (item.completedCount || 0), 0), 0)

    const completedTaskCount = projectList
      .reduce((sum, item) => sum + (item.completedCount || 0), 0)

    return [
      {
        key: 'project',
        label: '项目',
        activeCount: activeProjectCount,
        completedCount: completedProjectCount,
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

  handleProjectTap(e) {
    const { projectId } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/project/detail/detail?id=${projectId}`,
    })
  },

  handleCreateProject() {
    wx.showToast({
      title: '进入新建项目',
      icon: 'none',
    })
  },
})