import { getNodeList } from '../../api/node'

Page({
  data: {
    loading: false,
    nodeSections: [],
    needRefresh: false,
  },
  onShow() {
    if (this.data.needRefresh) {
      this.setData({ needRefresh: false })
      this.loadNodeList()
    }
  },
  onLoad() {
    this.loadNodeList()
  },

  async loadNodeList() {
    this.setData({ loading: true })

    try {
      const res = await getNodeList({
        pageNum: 1,
        pageSize: 50,
      })

      const nodeList = Array.isArray(res?.list) ? res.list : []
      const nodeSections = this.buildNodeSections(nodeList)

      this.setData({
        nodeSections,
      })
    } catch (error) {
      console.error('加载节点列表失败', error)
      wx.showToast({
        title: '节点加载失败',
        icon: 'none',
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  buildNodeSections(nodeList) {
    const projectItems = []
    const goalItems = []

    nodeList.forEach((item) => {
      const nodeItem = {
        id: item.id,
        title: item.title || '未命名节点',
        ownerName: item.ownerName || '',
        taskCount: item.taskCount || 0,
        updateText: this.formatUpdateText(item.updateTime),
      }

      if (item.ownerType === 'PROJECT') {
        projectItems.push(nodeItem)
      } else if (item.ownerType === 'GOAL') {
        goalItems.push(nodeItem)
      }
    })

    const sections = []

    if (projectItems.length) {
      sections.push({
        key: 'project',
        title: '项目',
        items: projectItems,
      })
    }

    if (goalItems.length) {
      sections.push({
        key: 'goal',
        title: '目标',
        items: goalItems,
      })
    }

    return sections
  },

  buildSummaryText(nodeSections) {
    const projectSection = nodeSections.find((item) => item.key === 'project')
    const goalSection = nodeSections.find((item) => item.key === 'goal')

    const projectCount = projectSection ? projectSection.items.length : 0
    const goalCount = goalSection ? goalSection.items.length : 0

    const projectTaskCount = projectSection
      ? projectSection.items.reduce((sum, item) => sum + (item.taskCount || 0), 0)
      : 0

    const goalTaskCount = goalSection
      ? goalSection.items.reduce((sum, item) => sum + (item.taskCount || 0), 0)
      : 0

    return `共有 ${projectCount} 个项目，${projectTaskCount} 个待办；${goalCount} 个目标，${goalTaskCount} 个待办`
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

  handleNodeTap(e) {
    const { nodeId } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/node/detail/detail?id=${nodeId}`,
    })
  },

  handleCreateNode() {
    wx.showToast({
      title: '进入新建节点',
      icon: 'none',
    })
  },
})