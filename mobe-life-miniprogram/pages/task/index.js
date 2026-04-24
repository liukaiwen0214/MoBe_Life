/**
 * 核心职责：承载小程序待办页，负责拉取待办列表并按归属对象分组，供“待办”和“节点”两个子视图展示。
 * 所属业务模块：小程序展示层 / 待办页。
 * 重要依赖关系或外部约束：当前真实数据只接通了待办子页签，节点子页签仍是静态占位数据。
 */
import { getTaskList } from '../../api/task'

Page({
  data: {
    subKey: 'todo',
    loading: false,
    summaryText: '今日待办有 0 个，其中 0 个延期，0 个正常',
    nodeSummaryText: '共有 1 个项目，2 个待办；1 个目标，1 个待办',

    todoSections: [],

    nodeSections: [
      {
        key: 'project',
        title: '项目',
        items: [
          {
            id: 'node_1',
            title: '待办模块页面设计',
            ownerName: 'MoBe Life 系统开发',
            taskCount: 3,
            updateText: '今天更新',
          },
          {
            id: 'node_2',
            title: '登录注册联调',
            ownerName: 'MoBe Life 系统开发',
            taskCount: 2,
            updateText: '2 天前更新',
          },
        ],
      },
      {
        key: 'goal',
        title: '目标',
        items: [
          {
            id: 'node_3',
            title: '科目二准备',
            ownerName: '拿到驾驶证',
            taskCount: 2,
            updateText: '昨天更新',
          },
        ],
      },
    ],
  },

  onLoad() {
    this.loadTaskList()
  },

  /**
   * 拉取待办列表并刷新页面数据。
   *
   * 第一版直接请求最多 50 条数据，优先把页面结构跑通；
   * 后续若要支持下拉分页，需要把这里改成增量合并模式。
   */
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

  /**
   * 把扁平待办列表按根归属对象分组成页面区块。
   *
   * @param {Array<Object>} taskList 待办列表。
   * @returns {Array<Object>} 页面展示所需的分组结果。
   */
  buildTodoSections(taskList) {
    const sectionMap = new Map()

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

      if (!sectionMap.has(sectionKey)) {
        sectionMap.set(sectionKey, {
          key: sectionKey,
          title: sectionTitle,
          count: 0,
          summary: '',
          collapsed: sectionKey === 'independent' ? false : true,
          tasks: [],
        })
      }

      const section = sectionMap.get(sectionKey)
      const isOverdue = this.isTaskOverdue(task)

      section.tasks.push({
        id: task.id,
        title: task.title || '未命名待办',
        statusText: task.statusText || '',
        timeText: this.formatTimeText(task.deadlineTime),
        flowMode: 'FLOW',
        statusCode: task.statusCode || '',
        deadlineTime: task.deadlineTime || '',
        belongText: this.buildTaskBelongText(task),
        rootOwnerName: task.rootOwnerName || '',
        nodeName: task.nodeName || '',
        isOverdue,
        overdueText: isOverdue ? '已延期' : '',
      })
      section.count = section.tasks.length
    })

    const independentSection = sectionMap.get('independent')
    const otherSections = Array.from(sectionMap.values()).filter((item) => item.key !== 'independent')

    otherSections.sort((a, b) => a.title.localeCompare(b.title, 'zh-CN'))

    const allSections = independentSection ? [independentSection, ...otherSections] : otherSections

    return allSections.map((section) => ({
      ...section,
      summary: this.buildSectionSummary(section.tasks),
    }))
  },

  /**
   * 生成单个待办的归属展示文案。
   *
   * @param {Object} task 待办对象。
   * @returns {string} 例如“项目名 · 节点名”的文本。
   */
  buildTaskBelongText(task) {
    const rootOwnerName = task.rootOwnerName || ''
    const nodeName = task.nodeName || ''

    if (nodeName) {
      return `${rootOwnerName} · ${nodeName}`
    }

    return rootOwnerName
  },

  /**
   * 生成顶部摘要文案。
   *
   * @param {Array<Object>} taskList 待办列表。
   * @returns {string} 汇总后的简短说明。
   */
  buildSummaryText(taskList) {
    const now = Date.now()

    const expiredCount = taskList.filter((task) => {
      if (!task.deadlineTime) {
        return false
      }

      if (task.statusCode === 'DONE') {
        return false
      }

      const deadline = new Date(task.deadlineTime).getTime()
      return !Number.isNaN(deadline) && deadline < now
    }).length

    const total = taskList.length
    const normalCount = total - expiredCount

    return `今日待办有 ${total} 个，其中 ${expiredCount} 个延期，${normalCount} 个正常`
  },

  /**
   * 把截止时间格式化为适合卡片展示的相对时间。
   *
   * @param {string} deadlineTime ISO 时间字符串。
   * @returns {string} 人类可读的时间文案。
   */
  formatTimeText(deadlineTime) {
    if (!deadlineTime) {
      return '未设置时间'
    }

    const date = new Date(deadlineTime)
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

  /**
   * 判断待办是否已经延期。
   *
   * @param {Object} task 待办对象。
   * @returns {boolean} 已过截止时间且未完成时返回 true。
   */
  isTaskOverdue(task) {
    if (!task.deadlineTime) {
      return false
    }

    if (task.statusCode === 'DONE') {
      return false
    }

    const deadline = new Date(task.deadlineTime).getTime()
    return !Number.isNaN(deadline) && deadline < Date.now()
  },

  /**
   * 生成分组摘要文案。
   *
   * @param {Array<Object>} tasks 当前分组内的待办列表。
   * @returns {string} 用于区块标题旁的统计描述。
   */
  buildSectionSummary(tasks) {
    const overdueCount = tasks.filter((task) => task.isOverdue).length
    return `共 ${tasks.length} 个待办${overdueCount > 0 ? `，${overdueCount} 个延期` : ''}`
  },

  /**
   * 展开或收起某个待办分组。
   *
   * @param {Object} e 小程序事件对象。
   */
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

    this.setData({ todoSections })
  },

  /**
   * 处理待办卡片上的快捷操作。
   *
   * @param {Object} e 小程序事件对象。
   */
  handleTaskAction(e) {
    const { taskId, flowMode } = e.currentTarget.dataset

    if (flowMode === 'FLOW') {
      wx.showToast({
        title: `待办 ${taskId} 进入下一状态`,
        icon: 'none',
      })
      return
    }

    wx.showActionSheet({
      itemList: ['待开始', '进行中', '已完成', '已取消'],
      success: (res) => {
        const selected = ['待开始', '进行中', '已完成', '已取消'][res.tapIndex]
        wx.showToast({
          title: `切换为${selected}`,
          icon: 'none',
        })
      },
    })
  },

  /**
   * 进入待办详情页。
   *
   * @param {Object} e 小程序事件对象。
   */
  handleTaskTap(e) {
    const { taskId } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/task/detail/detail?id=${taskId}`,
    })
  },

  /**
   * 处理新建待办入口点击。
   * 当前仍为占位交互，后续接入真实新建流程即可替换这里。
   */
  handleCreateTask() {
    wx.showToast({
      title: '进入新建待办',
      icon: 'none',
    })
  },

  handleNodeTap(e) {
    const { nodeId } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/node/detail/detail?id=${nodeId}`,
    })
  },
  buildSectionSummary(tasks) {
    const total = tasks.length
    const now = Date.now()

    let inProgressCount = 0
    let expiredCount = 0
    let dueTomorrowCount = 0

    tasks.forEach((task) => {
      if (task.statusCode === 'IN_PROGRESS') {
        inProgressCount += 1
      }

      if (task.deadlineTime) {
        const deadline = new Date(task.deadlineTime).getTime()
        if (!Number.isNaN(deadline)) {
          const todayStart = new Date()
          todayStart.setHours(0, 0, 0, 0)

          const tomorrowStart = todayStart.getTime() + 24 * 60 * 60 * 1000
          const dayAfterTomorrowStart = tomorrowStart + 24 * 60 * 60 * 1000

          if (task.statusCode !== 'DONE' && deadline < now) {
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
  isTaskOverdue(task) {
    if (!task?.deadlineTime) {
      return false
    }
  
    if (task.statusCode === 'DONE') {
      return false
    }
  
    const deadline = new Date(task.deadlineTime).getTime()
    if (Number.isNaN(deadline)) {
      return false
    }
  
    return deadline < Date.now()
  },
})
