/**
 * 核心职责：承载小程序页面 `node` 的交互逻辑和数据流。
 * 所属业务模块：小程序展示层 / 页面逻辑。
 * 重要依赖关系或外部约束：页面脚本通常与同目录的 WXML、WXSS 配合工作，字段命名应与模板绑定保持一致。
 */
import { getNodeDetail, completeNode, deleteNode } from '../../../api/node'

Page({
  data: {
    nodeId: '',
    loading: false,
    needRefresh: false,
    detail: {
      title: '',
      ownerName: '',
      ownerType: '',
      summaryText: '',
      contentText: '',
      remarkText: '',
      updateText: '',
      tasks: [],
    },
    rawDetail: null,
  },

  onLoad(options) {
    const nodeId = options?.id || ''
    this.setData({
      nodeId,
    })

    if (nodeId) {
      this.loadNodeDetail(nodeId)
    }
  },

  onShow() {
    if (this.data.needRefresh && this.data.nodeId) {
      this.setData({ needRefresh: false })
      this.loadNodeDetail(this.data.nodeId)
    }
  },

  async loadNodeDetail(nodeId) {
    this.setData({ loading: true })

    try {
      const detail = await getNodeDetail(nodeId)
      const tasks = Array.isArray(detail?.tasks) ? detail.tasks : []

      this.setData({
        rawDetail: detail,
        detail: {
          title: detail?.title || '未命名节点',
          ownerName: detail?.ownerName || '',
          ownerType: this.formatOwnerType(detail?.ownerType),
          summaryText: `这个节点下共有 ${detail?.taskCount || 0} 个待办`,
          contentText: detail?.content || '',
          remarkText: detail?.remark || '',
          updateText: this.formatUpdateText(detail?.updateTime),
          tasks: tasks.map((task) => ({
            id: task.id,
            title: task.title || '未命名待办',
            statusText: task.statusText || '',
            timeText: this.formatTimeText(task.deadlineTime),
          })),
        },
      })
    } catch (error) {
      console.error('加载节点详情失败', error)
      wx.showToast({
        title: '节点详情加载失败',
        icon: 'none',
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  formatOwnerType(ownerType) {
    if (ownerType === 'PROJECT') {
      return '项目'
    }
    if (ownerType === 'GOAL') {
      return '目标'
    }
    return ''
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

  handleOwnerTap() {
    wx.showToast({
      title: '请前往网页端修改所属项目/目标',
      icon: 'none',
    })
  },

  handleEditNode() {
    const { nodeId } = this.data
    if (!nodeId) {
      return
    }

    wx.navigateTo({
      url: `/pages/node/edit/edit?id=${nodeId}`,
      fail: (err) => {
        console.error('navigateTo edit node fail =', err)
      },
    })
  },

  handleTaskTap(e) {
    const { taskId } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/task/detail/detail?id=${taskId}`,
    })
  },

  handleCompleteNode() {
    const { nodeId } = this.data
    if (!nodeId) {
      return
    }

    wx.showModal({
      title: '提示',
      content: '确认将这个节点赋予完成态吗？',
      success: async (res) => {
        if (!res.confirm) {
          return
        }

        try {
          wx.showLoading({
            title: '处理中',
            mask: true,
          })

          await completeNode(nodeId)

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
          console.error('赋予节点完成态失败', error)

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

  handleDeleteNode() {
    const { nodeId, detail } = this.data
    if (!nodeId) {
      return
    }

    if (!detail.tasks || !detail.tasks.length) {
      wx.showModal({
        title: '提示',
        content: '确认删除这个节点吗？',
        success: async (res) => {
          if (!res.confirm) {
            return
          }
          await this.submitDeleteNode()
        },
      })
      return
    }

    wx.showActionSheet({
      itemList: ['完成并删除', '整体删除'],
      alertText: '当前节点中存在未完成的待办',
      success: async (res) => {
        if (res.tapIndex === 0) {
          await this.submitDeleteNode('COMPLETE_TASKS')
          return
        }

        if (res.tapIndex === 1) {
          await this.submitDeleteNode('DELETE_TASKS')
        }
      },
    })
  },

  async submitDeleteNode(deleteMode) {
    const { nodeId } = this.data

    try {
      wx.showLoading({
        title: '处理中',
        mask: true,
      })

      if (deleteMode) {
        await deleteNode(nodeId, { deleteMode })
      } else {
        await deleteNode(nodeId)
      }

      wx.hideLoading()
      wx.showToast({
        title: '删除成功',
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
      console.error('删除节点失败', error)

      const message =
        error?.message ||
        error?.msg ||
        error?.data?.message ||
        '删除失败'

      wx.showModal({
        title: '提示',
        content: message,
        showCancel: false,
      })
    }
  },
})