import {
  getTaskList,
  getTaskFlow,
  moveTaskToNextStatus,
  releaseTaskToStatus,
} from '../../api/task'

Page({
  data: {
    subKey: 'todo',
    loading: false,
    needRefresh: false,
    summaryText: '今日待办有 0 个，其中 0 个延期，0 个正常',
    todoSections: [],
    sectionStateMap: {},
  },
  buildReleaseOptions(flow) {
    return Array.isArray(flow?.releaseOptions) ? flow.releaseOptions : []
  },
  onLoad() {
    this.loadTaskList()
  },

  onShow() {
    if (this.data.needRefresh) {
      this.setData({ needRefresh: false })
      this.loadTaskList()
    }
  },

  async loadTaskList() {
    this.setData({ loading: true })

    try {
      const res = await getTaskList({
        pageNum: 1,
        pageSize: 50,
      })

      const taskList = Array.isArray(res?.list) ? res.list : []
      const todoSections = this.buildTodoSections(taskList)
      const summaryText = this.buildSummaryText(taskList)

      this.setData({
        todoSections,
        summaryText,
      })
    } catch (error) {
      console.error('加载待办列表失败', error)
      wx.showToast({
        title: '待办加载失败',
        icon: 'none',
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  buildTodoSections(taskList) {
    const sectionMap = new Map()
    const stateMap = this.data.sectionStateMap || {}

    taskList.forEach((task) => {
      const rootOwnerType = task.rootOwnerType || 'INDEPENDENT'
      const rootOwnerName = task.rootOwnerName || '未命名分组'

      let sectionKey = ''
      let sectionTitle = ''

      if (rootOwnerType === 'INDEPENDENT') {
        sectionKey = 'independent'
        sectionTitle = '独立待办'
      } else {
        sectionKey = `${rootOwnerType}_${task.rootOwnerId || rootOwnerName}`
        sectionTitle = rootOwnerName
      }

      const savedState = stateMap[sectionKey] || {}

      if (!sectionMap.has(sectionKey)) {
        sectionMap.set(sectionKey, {
          key: sectionKey,
          title: sectionTitle,
          collapsed:
            typeof savedState.collapsed === 'boolean'
              ? savedState.collapsed
              : sectionKey === 'independent'
                ? false
                : true,
          showCompletedAll: !!savedState.showCompletedAll,
          activeTasks: [],
          completedTasks: [],
          summary: '',
        })
      }


      const section = sectionMap.get(sectionKey)
      const isOverdue = this.isTaskOverdue(task)
      const isCompleted = Number(task.isTerminal) === 1

      const taskItem = {
        id: task.id,
        title: task.title || '未命名待办',
        statusText: task.statusText || '',
        statusCode: task.statusCode || '',
        statusColor: task.statusColor || '',
        statusIcon: task.statusIcon || '',
        colorCode: task.colorCode || '',
        isInitial: task.isInitial ?? 0,
        isTerminal: task.isTerminal ?? 0,
        statusSortNo: task.statusSortNo ?? 0,
        timeText: this.formatTimeText(task.deadlineTime),
        deadlineTime: task.deadlineTime || '',
        belongText: this.buildTaskBelongText(task),
        rootOwnerName: task.rootOwnerName || '',
        nodeName: task.nodeName || '',
        isOverdue,
        overdueText: isOverdue ? '已延期' : '',
        isCompleted,
        rowClass: isCompleted ? 'task-row--completed' : 'task-row--active',
      }

      if (isCompleted) {
        section.completedTasks.push(taskItem)
      } else {
        section.activeTasks.push(taskItem)
      }
    })

    const independentSection = sectionMap.get('independent')
    const otherSections = Array.from(sectionMap.values()).filter(
      (item) => item.key !== 'independent'
    )

    otherSections.sort((a, b) => a.title.localeCompare(b.title, 'zh-CN'))

    const allSections = independentSection
      ? [independentSection, ...otherSections]
      : otherSections

    return allSections.map((section) => {
      const visibleCompletedTasks = section.showCompletedAll
        ? section.completedTasks
        : section.completedTasks.slice(0, 3)

      return {
        ...section,
        summary: this.buildSectionSummary([
          ...section.activeTasks,
          ...section.completedTasks,
        ]),
        visibleCompletedTasks,
        completedHiddenCount: Math.max(
          section.completedTasks.length - visibleCompletedTasks.length,
          0
        ),
      }
    })
  },

  buildTaskBelongText(task) {
    const rootOwnerName = task.rootOwnerName || ''
    const nodeName = task.nodeName || ''

    if (nodeName) {
      return `${rootOwnerName} · ${nodeName}`
    }

    return rootOwnerName
  },

  buildSummaryText(taskList) {
    const now = Date.now()

    const expiredCount = taskList.filter((task) => {
      if (!task.deadlineTime) {
        return false
      }

      if (Number(task.isTerminal) === 1) {
        return false
      }

      const deadline = new Date(task.deadlineTime).getTime()
      return !Number.isNaN(deadline) && deadline < now
    }).length

    const total = taskList.length
    const normalCount = total - expiredCount

    return `今日待办有 ${total} 个，其中 ${expiredCount} 个延期，${normalCount} 个正常`
  },

  formatTimeText(deadlineTime) {
    if (!deadlineTime) {
      return '未设置时间'
    }

    const date = new Date(deadlineTime)
    if (Number.isNaN(date.getTime())) {
      return '未设置时间'
    }

    const now = new Date()
    const todayStart = new Date(
      now.getFullYear(),
      now.getMonth(),
      now.getDate()
    ).getTime()
    const targetStart = new Date(
      date.getFullYear(),
      date.getMonth(),
      date.getDate()
    ).getTime()
    const dayDiff = Math.round(
      (targetStart - todayStart) / (24 * 60 * 60 * 1000)
    )

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

  isTaskOverdue(task) {
    if (!task?.deadlineTime) {
      return false
    }

    if (Number(task.isTerminal) === 1) {
      return false
    }

    const deadline = new Date(task.deadlineTime).getTime()
    if (Number.isNaN(deadline)) {
      return false
    }

    return deadline < Date.now()
  },

  buildSectionSummary(tasks) {
    const total = tasks.length
    const now = Date.now()

    let inProgressCount = 0
    let expiredCount = 0
    let dueTomorrowCount = 0

    tasks.forEach((task) => {
      if (Number(task.isTerminal) !== 1 && Number(task.isInitial) !== 1) {
        inProgressCount += 1
      }

      if (task.deadlineTime) {
        const deadline = new Date(task.deadlineTime).getTime()
        if (!Number.isNaN(deadline)) {
          const todayStart = new Date()
          todayStart.setHours(0, 0, 0, 0)

          const tomorrowStart = todayStart.getTime() + 24 * 60 * 60 * 1000
          const dayAfterTomorrowStart = tomorrowStart + 24 * 60 * 60 * 1000

          if (Number(task.isTerminal) !== 1 && deadline < now) {
            expiredCount += 1
          } else if (deadline >= tomorrowStart && deadline < dayAfterTomorrowStart) {
            dueTomorrowCount += 1
          }
        }
      }
    })

    if (expiredCount > 0) {
      return `${total} 个待办 · ${expiredCount} 个已延期`
    }

    if (dueTomorrowCount > 0) {
      return `${total} 个待办 · ${dueTomorrowCount} 个明天到期`
    }

    if (inProgressCount > 0) {
      return `${total} 个待办 · ${inProgressCount} 个进行中`
    }

    return `${total} 个待办`
  },

  handleToggleSection(e) {
    const { key } = e.currentTarget.dataset
    const todoSections = this.data.todoSections.map((item) => {
      if (item.key === key) {
        return {
          ...item,
          collapsed: !item.collapsed,
        }
      }
      return item
    })

    const sectionStateMap = { ...this.data.sectionStateMap }
    const target = todoSections.find((item) => item.key === key)
    if (target) {
      sectionStateMap[key] = {
        ...(sectionStateMap[key] || {}),
        collapsed: target.collapsed,
        showCompletedAll: target.showCompletedAll,
      }
    }

    this.setData({
      todoSections,
      sectionStateMap,
    })
  },
  handleShowMoreCompleted(e) {
    const { key } = e.currentTarget.dataset

    const todoSections = this.data.todoSections.map((item) => {
      if (item.key === key) {
        return {
          ...item,
          showCompletedAll: true,
          visibleCompletedTasks: item.completedTasks,
          completedHiddenCount: 0,
        }
      }
      return item
    })

    const sectionStateMap = { ...this.data.sectionStateMap }
    const target = todoSections.find((item) => item.key === key)
    if (target) {
      sectionStateMap[key] = {
        ...(sectionStateMap[key] || {}),
        collapsed: target.collapsed,
        showCompletedAll: true,
      }
    }

    this.setData({
      todoSections,
      sectionStateMap,
    })
  },

  async handleTaskAction(e) {
    const { taskId, isTerminal } = e.currentTarget.dataset
  
    if (!taskId) {
      return
    }
  
    try {
      wx.showLoading({
        title: '处理中',
        mask: true,
      })
  
      const flow = await getTaskFlow(taskId)
  
      if (Number(isTerminal) === 1) {
        wx.hideLoading()
  
        if (Number(flow?.allowRelease) !== 1) {
          wx.showToast({
            title: '当前终态不允许放出',
            icon: 'none',
          })
          return
        }
  
        const releaseOptions = this.buildReleaseOptions(flow)
        if (!releaseOptions.length) {
          wx.showToast({
            title: '当前没有可放出的状态',
            icon: 'none',
          })
          return
        }
  
        wx.showActionSheet({
          itemList: releaseOptions.map((item) => item.statusName || '未命名状态'),
          success: async (res) => {
            const selected = releaseOptions[res.tapIndex]
            if (!selected) {
              return
            }
  
            try {
              wx.showLoading({
                title: '处理中',
                mask: true,
              })
  
              await releaseTaskToStatus(taskId, {
                targetStatusId: selected.id,
                changeRemark: `从列表页放出到「${selected.statusName}」`,
              })
  
              wx.hideLoading()
              wx.showToast({
                title: `已放出到「${selected.statusName}」`,
                icon: 'success',
              })
  
              this.loadTaskList()
            } catch (error) {
              wx.hideLoading()
              console.error('放出待办失败', error)
  
              const message =
                error?.message ||
                error?.msg ||
                error?.data?.message ||
                '放出失败'
  
              wx.showModal({
                title: '提示',
                content: message,
                showCancel: false,
              })
            }
          },
        })
  
        return
      }
  
      if (Number(flow?.allowNext) !== 1) {
        wx.hideLoading()
        wx.showToast({
          title: '当前待办无法继续推进',
          icon: 'none',
        })
        return
      }
  
      const nextStatusName = flow?.nextStatus?.statusName || ''
  
      await moveTaskToNextStatus(taskId, {})
  
      wx.hideLoading()
      wx.showToast({
        title: nextStatusName ? `进入「${nextStatusName}」` : '状态已推进',
        icon: 'success',
      })
  
      this.loadTaskList()
    } catch (error) {
      wx.hideLoading()
      console.error('处理待办状态失败', error)
  
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

  handleTaskTap(e) {
    const { taskId } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/task/detail/detail?id=${taskId}`,
    })
  },

  handleCreateTask() {
    wx.navigateTo({
      url: '/pages/task/create/create',
    })
  },
})
