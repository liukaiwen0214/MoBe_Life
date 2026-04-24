import { getTaskDetail, moveTaskToNextStatus, deleteTask } from '../../../api/task'

Page({
  data: {
    taskId: '',
    loading: false,
    needRefresh: false,
    detail: {
      title: '',
      statusText: '',
      timeText: '',
      isOverdue: false,
      belongText: '',
      contentText: '',
      remarkText: '',
      planStartText: '',
      planEndText: '',
      deadlineText: '',
      deadlineIsOverdue: false,
      completedText: '',
      currentStatusCode: '',
      statusFlowList: [],
      statusTimeline: [],
      currentFlowAnchor: '',
    },
    rawDetail: null,
  },

  onLoad(options) {
    const taskId = options?.id || ''
    this.setData({ taskId })

    if (taskId) {
      this.loadTaskDetail(taskId)
    }
  },

  onShow() {
    if (this.data.needRefresh && this.data.taskId) {
      this.setData({ needRefresh: false })
      this.loadTaskDetail(this.data.taskId)
    }
  },

  async loadTaskDetail(taskId) {
    this.setData({ loading: true })

    try {
      const detail = await getTaskDetail(taskId)

      const headerTime = this.buildHeaderTime(detail)
      const planStart = this.buildTimeDisplay(detail?.planStartTime, false, detail?.statusCode)
      const planEnd = this.buildTimeDisplay(detail?.planEndTime, false, detail?.statusCode)
      const deadline = this.buildTimeDisplay(detail?.deadlineTime, true, detail?.statusCode)
      const completed = this.buildTimeDisplay(detail?.completedAt, false, detail?.statusCode)

      const statusFlowList = this.buildStatusFlowList(detail?.statusList, detail?.statusCode)
      const currentStatus = Array.isArray(detail?.statusList)
        ? detail.statusList.find((item) => item.statusCode === detail?.statusCode)
        : null

      const isTerminal = Number(currentStatus?.isTerminal) === 1

      this.setData({
        rawDetail: detail,
        detail: {
          title: detail?.title || '未命名待办',
          statusText: detail?.statusText || '',
          timeText: this.composeTimeText(headerTime.main, headerTime.hint),
          isOverdue: headerTime.isOverdue,
          belongText: this.buildBelongText(detail),
          contentText: detail?.content || '',
          remarkText: detail?.remark || '',
          planStartText: this.composeTimeText(planStart.main, planStart.hint),
          planEndText: this.composeTimeText(planEnd.main, planEnd.hint),
          deadlineText: this.composeTimeText(deadline.main, deadline.hint),
          deadlineIsOverdue: deadline.isOverdue,
          completedText: this.composeTimeText(completed.main, completed.hint),
          currentStatusCode: detail?.statusCode || '',
          statusFlowList,
          currentFlowAnchor: this.buildCurrentFlowAnchor(statusFlowList),
          statusTimeline: this.buildStatusTimeline(detail?.statusChangeLogs),
          isTerminal,
        },
      })
    } catch (error) {
      console.error('加载待办详情失败', error)
      wx.showToast({
        title: '待办详情加载失败',
        icon: 'none',
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  buildCurrentFlowAnchor(statusFlowList) {
    const list = Array.isArray(statusFlowList) ? statusFlowList : []
    const currentIndex = list.findIndex((item) => item.isCurrent)

    if (currentIndex === -1) {
      return ''
    }

    return `flow-step-${currentIndex}`
  },

  buildBelongText(detail) {
    const ownerName = detail?.ownerName || ''
    const ownerType = detail?.directOwnerType || ''

    if (!ownerName) {
      return ''
    }

    if (ownerType === 'PROJECT') {
      return `来自项目 · ${ownerName}`
    }

    if (ownerType === 'GOAL') {
      return `来自目标 · ${ownerName}`
    }

    if (ownerType === 'NODE') {
      return `来自节点 · ${ownerName}`
    }

    if (ownerType === 'INDEPENDENT') {
      return '独立待办'
    }

    return ownerName
  },

  buildHeaderTime(detail) {
    if (detail?.deadlineTime) {
      return this.buildTimeDisplay(detail.deadlineTime, true, detail?.statusCode)
    }

    if (detail?.planEndTime) {
      return this.buildTimeDisplay(detail.planEndTime, false, detail?.statusCode)
    }

    if (detail?.planStartTime) {
      return this.buildTimeDisplay(detail.planStartTime, false, detail?.statusCode)
    }

    return {
      main: '未设置时间',
      hint: '',
      isOverdue: false,
    }
  },

  buildTimeDisplay(time, checkOverdue = false, statusCode = '') {
    if (!time) {
      return {
        main: '未设置时间',
        hint: '',
        isOverdue: false,
      }
    }

    const date = new Date(time)
    if (Number.isNaN(date.getTime())) {
      return {
        main: '未设置时间',
        hint: '',
        isOverdue: false,
      }
    }

    const main = this.formatFullDateTime(date)
    const dayHint = this.getDayHint(date)
    const isOverdue = checkOverdue && statusCode !== 'DONE' && date.getTime() < Date.now()

    let hint = dayHint
    if (isOverdue) {
      hint = hint ? `${hint} · 已延期` : '已延期'
    }

    return {
      main,
      hint,
      isOverdue,
    }
  },

  composeTimeText(main, hint) {
    if (!main || main === '未设置时间') {
      return '未设置时间'
    }

    if (!hint) {
      return main
    }

    return `${main} · ${hint}`
  },

  formatFullDateTime(input) {
    const date = input instanceof Date ? input : new Date(input)
    if (Number.isNaN(date.getTime())) {
      return '未设置时间'
    }

    const year = date.getFullYear()
    const month = `${date.getMonth() + 1}`.padStart(2, '0')
    const day = `${date.getDate()}`.padStart(2, '0')
    const hh = `${date.getHours()}`.padStart(2, '0')
    const mm = `${date.getMinutes()}`.padStart(2, '0')

    return `${year}-${month}-${day} ${hh}:${mm}`
  },

  getDayHint(input) {
    const date = input instanceof Date ? input : new Date(input)
    if (Number.isNaN(date.getTime())) {
      return ''
    }

    const now = new Date()
    const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime()
    const targetStart = new Date(date.getFullYear(), date.getMonth(), date.getDate()).getTime()
    const dayDiff = Math.round((targetStart - todayStart) / (24 * 60 * 60 * 1000))

    if (dayDiff === 0) {
      return '今天'
    }

    if (dayDiff === 1) {
      return '明天'
    }

    if (dayDiff === -1) {
      return '昨天'
    }

    return ''
  },

  buildStatusFlowList(statusList, currentStatusCode) {
    const list = Array.isArray(statusList) ? statusList : []
    const currentIndex = list.findIndex((item) => item.statusCode === currentStatusCode)

    return list.map((item, index) => ({
      id: item.id,
      statusName: item.statusName || '',
      statusCode: item.statusCode || '',
      isCurrent: index === currentIndex,
      isPassed: currentIndex !== -1 && index < currentIndex,
      isFuture: currentIndex !== -1 && index > currentIndex,
      isLast: index === list.length - 1,
    }))
  },

  buildStatusTimeline(logs) {
    const list = Array.isArray(logs) ? [...logs] : []

    return list
      .sort((a, b) => new Date(b.changeTime).getTime() - new Date(a.changeTime).getTime())
      .map((item, index) => ({
        id: item.id,
        title: this.buildTimelineTitle(item),
        timeText: this.formatFullDateTime(item.changeTime),
        remark: item.changeRemark || '',
        isLast: index === list.length - 1,
      }))
  },

  buildTimelineTitle(item) {
    const changeType = item?.changeType || ''
    const fromStatusName = item?.fromStatusName || ''
    const toStatusName = item?.toStatusName || ''

    if (changeType === 'INIT') {
      return `初始化为「${toStatusName || '未命名状态'}」`
    }

    if (fromStatusName && toStatusName) {
      return `从「${fromStatusName}」变为「${toStatusName}」`
    }

    if (toStatusName) {
      return `进入「${toStatusName}」`
    }

    return '状态已更新'
  },

  handleEditTask() {
    const { taskId } = this.data
    if (!taskId) {
      return
    }

    wx.navigateTo({
      url: `/pages/task/edit/edit?id=${taskId}`,
    })
  },

  async handleNextStatus() {
    const { taskId, detail } = this.data
    if (!taskId) {
      return
    }

    if (detail.isTerminal) {
      wx.showToast({
        title: '当前待办已完成',
        icon: 'none',
      })
      return
    }

    try {
      wx.showLoading({
        title: '处理中',
        mask: true,
      })

      await moveTaskToNextStatus(taskId, {})

      wx.hideLoading()
      wx.showToast({
        title: '状态已推进',
        icon: 'success',
      })

      const pages = getCurrentPages()
      const prevPage = pages.length > 1 ? pages[pages.length - 2] : null
      if (prevPage && typeof prevPage.setData === 'function') {
        prevPage.setData({ needRefresh: true })
      }

      this.setData({ needRefresh: true })
      this.loadTaskDetail(taskId)
    } catch (error) {
      wx.hideLoading()
      console.error('推进待办状态失败', error)

      const message =
        error?.message ||
        error?.msg ||
        error?.data?.message ||
        '状态推进失败'

      wx.showModal({
        title: '提示',
        content: message,
        showCancel: false,
      })
    }
  },

  handleDeleteTask() {
    const { taskId } = this.data
    if (!taskId) {
      return
    }

    wx.showModal({
      title: '提示',
      content: '确认删除这个待办吗？',
      success: async (res) => {
        if (!res.confirm) {
          return
        }

        try {
          wx.showLoading({
            title: '处理中',
            mask: true,
          })

          await deleteTask(taskId)

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
          console.error('删除待办失败', error)

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
  },
})