import { getGoalDetail, completeGoal, reopenGoal, reopenGoalWithNodes } from '../../../api/goal'

Page({
  data: {
    goalId: '',
    loading: false,
    detail: {
      title: '',
      statusText: '',
      executionMode: '',
      summaryText: '',
      contentText: '',
      remarkText: '',
      nodeCount: 0,
      taskCount: 0,
      completedCount: 0,
      currentStatusCode: '',
      statusFlowList: [],
      nodeSections: [],
      isCompleted: false,
    },
    rawDetail: null,
  },

  onLoad(options) {
    const goalId = options?.id || ''
    this.setData({ goalId })

    if (goalId) {
      this.loadGoalDetail(goalId)
    }
  },
  handleGoalAction() {
    if (this.data.detail.isCompleted) {
      this.handleRecoverGoal()
      return
    }

    this.handleCompleteGoal()
  },
  async loadGoalDetail(goalId) {
    this.setData({ loading: true })
  
    try {
      const detail = await getGoalDetail(goalId)
      const nodeSections = this.buildNodeSections(detail)
  
      this.setData({
        rawDetail: detail,
        detail: {
          title: detail?.title || '未命名目标',
          statusText: detail?.statusText || '',
          executionMode: this.formatExecutionMode(detail?.executionMode),
          summaryText: `共有 ${detail?.nodeCount || 0} 个节点，${detail?.taskCount || 0} 个待办，已完成 ${detail?.completedCount || 0} 个`,
          contentText: detail?.content || '',
          remarkText: detail?.remark || '',
          nodeCount: detail?.nodeCount || 0,
          taskCount: detail?.taskCount || 0,
          completedCount: detail?.completedCount || 0,
          currentStatusCode: detail?.statusCode || '',
          statusFlowList: this.buildStatusFlowList(detail?.statusList, detail?.statusCode),
          nodeSections,
          isCompleted: detail?.isCompleted === 1,
        },
      })
    } catch (error) {
      console.error('加载目标详情失败', error)
      wx.showToast({
        title: '目标详情加载失败',
        icon: 'none',
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  formatExecutionMode(executionMode) {
    if (executionMode === 'FLOW') {
      return '流程模式'
    }
    if (executionMode === 'FREE') {
      return '自由模式'
    }
    return executionMode || ''
  },

  buildStatusAttrs(status) {
    const attrs = []

    if (status?.isInitial === 1) {
      attrs.push('初始')
    }
    if (status?.isTerminal === 1) {
      attrs.push('结束')
    }
    if (status?.isEnabled === 1) {
      attrs.push('启用')
    } else {
      attrs.push('停用')
    }

    return attrs.join(' · ')
  },

  formatTimeText(time) {
    if (!time) {
      return '未设置时间'
    }

    const date = new Date(time)
    if (Number.isNaN(date.getTime())) {
      return '未设置时间'
    }

    const now = new Date()
    const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime()
    const targetStart = new Date(date.getFullYear(), date.getMonth(), date.getDate()).getTime()
    const dayDiff = Math.round((targetStart - todayStart) / (24 * 60 * 60 * 1000))

    const hh = `${date.getHours()}`.padStart(2, '0')
    const mm = `${date.getMinutes()}`.padStart(2, '0')
    const timePart = `${hh}:${mm}`

    if (dayDiff === 0) {
      return `今天 ${timePart}`
    }

    if (dayDiff === 1) {
      return `明天 ${timePart}`
    }

    if (dayDiff === -1) {
      return `昨天 ${timePart}`
    }

    const month = `${date.getMonth() + 1}`.padStart(2, '0')
    const day = `${date.getDate()}`.padStart(2, '0')
    return `${month}-${day} ${timePart}`
  },

  buildStatusFlowList(statusList, currentStatusCode) {
    const list = Array.isArray(statusList) ? statusList : []
    const currentIndex = list.findIndex((item) => item.statusCode === currentStatusCode)

    return list.map((item, index) => ({
      id: item.id,
      name: item.statusName || '',
      code: item.statusCode || '',
      attrs: this.buildStatusAttrs(item),
      isCurrent: index === currentIndex,
      isPassed: currentIndex !== -1 && index < currentIndex,
      isLast: index === list.length - 1,
    }))
  },

  buildNodeSections(detail) {
    const nodeList = Array.isArray(detail?.nodes) ? detail.nodes : []
    const taskList = Array.isArray(detail?.tasks) ? detail.tasks : []

    const nodeTaskMap = new Map()

    taskList.forEach((task) => {
      if (task?.directOwnerType !== 'NODE' || !task?.directOwnerId) {
        return
      }

      if (!nodeTaskMap.has(task.directOwnerId)) {
        nodeTaskMap.set(task.directOwnerId, [])
      }

      nodeTaskMap.get(task.directOwnerId).push({
        id: task.id,
        title: task.title || '未命名待办',
        statusText: task.statusText || '',
        timeText: this.formatTimeText(task.deadlineTime),
      })
    })

    return nodeList.map((node, index) => ({
      id: node.id,
      title: node.title || '未命名节点',
      taskCount: node.taskCount || 0,
      collapsed: index !== 0,
      tasks: nodeTaskMap.get(node.id) || [],
    }))
  },

  handleToggleNodeSection(e) {
    const { nodeId } = e.currentTarget.dataset
    const nodeSections = this.data.detail.nodeSections.map((item) => {
      if (item.id === nodeId) {
        return {
          ...item,
          collapsed: !item.collapsed,
        }
      }
      return item
    })

    this.setData({
      'detail.nodeSections': nodeSections,
    })
  },

  // handleNodeTap(e) {
  //   const { nodeId } = e.currentTarget.dataset
  //   wx.navigateTo({
  //     url: `/pages/node/detail/detail?id=${nodeId}`,
  //   })
  // },

  handleTaskTap(e) {
    const { taskId } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/task/detail/detail?id=${taskId}`,
    })
  },

  handleEditFlow() {
    wx.showToast({
      title: '后续接入编辑流程',
      icon: 'none',
    })
  },

  handleCompleteGoal() {
    const { goalId } = this.data
    if (!goalId) {
      return
    }

    wx.showModal({
      title: '提示',
      content: '确认将这个目标赋予完成态吗？',
      success: async (res) => {
        if (!res.confirm) {
          return
        }

        try {
          wx.showLoading({
            title: '处理中',
            mask: true,
          })

          await completeGoal(goalId)

          wx.hideLoading()
          wx.showToast({
            title: '已设为完成',
            icon: 'success',
          })

          const pages = getCurrentPages()
          const prevPage = pages.length > 1 ? pages[pages.length - 2] : null
          if (prevPage && typeof prevPage.setData === 'function') {
            prevPage.setData({ needRefresh: true })
          }

          setTimeout(() => {
            wx.navigateBack()
          }, 600)
        } catch (error) {
          wx.hideLoading()
          console.error('赋予目标完成态失败', error)

          const message =
            error?.message ||
            error?.msg ||
            error?.data?.message ||
            '操作失败'

          wx.showModal({
            title: '提示',
            content: message,
            showCancel: false,
          })
        }
      },
    })
  },
  handleRecoverGoal() {
    const { goalId } = this.data
    if (!goalId) {
      return
    }
  
    wx.showActionSheet({
      itemList: ['仅恢复目标', '同时恢复节点'],
      success: async (res) => {
        try {
          wx.showLoading({
            title: '处理中',
            mask: true,
          })
  
          if (res.tapIndex === 0) {
            await reopenGoal(goalId)
          } else if (res.tapIndex === 1) {
            await reopenGoalWithNodes(goalId)
          } else {
            wx.hideLoading()
            return
          }
  
          wx.hideLoading()
          wx.showToast({
            title: '已恢复',
            icon: 'success',
          })
  
          const pages = getCurrentPages()
          const prevPage = pages.length > 1 ? pages[pages.length - 2] : null
          if (prevPage && typeof prevPage.setData === 'function') {
            prevPage.setData({ needRefresh: true })
          }
  
          setTimeout(() => {
            wx.navigateBack()
          }, 600)
        } catch (error) {
          wx.hideLoading()
          console.error('恢复目标失败', error)
  
          const message =
            error?.message ||
            error?.msg ||
            error?.data?.message ||
            '操作失败'
  
          wx.showModal({
            title: '提示',
            content: message,
            showCancel: false,
          })
        }
      },
    })
  },
})