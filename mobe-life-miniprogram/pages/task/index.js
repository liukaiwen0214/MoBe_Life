Page({
  data: {
    subKey: 'todo',
    summaryText: '今日待办有 10 个，其中 2 个延期，8 个正常',
    nodeSummaryText: '共有 1 个项目，2 个待办；1 个目标，1 个待办',

    todoSections: [
      {
        key: 'independent',
        title: '独立待办',
        count: 2,
        collapsed: false,
        tasks: [
          {
            id: 1,
            title: '下班买洗衣液',
            statusText: '待开始',
            timeText: '今天 19:30',
            flowMode: 'FLOW',
            nodeName: '',
          },
          {
            id: 2,
            title: '给电脑清理桌面文件',
            statusText: '进行中',
            timeText: '明天 21:00',
            flowMode: 'FREE',
            nodeName: '',
          },
        ],
      },
      {
        key: 'project_1',
        title: 'MoBe Life 系统开发',
        count: 3,
        collapsed: false,
        type: '项目',
        tasks: [
          {
            id: 11,
            title: '整理任务模块接口清单',
            statusText: '进行中',
            timeText: '今天 18:00',
            flowMode: 'FLOW',
            nodeName: '',
          },
          {
            id: 12,
            title: '确认待办页面布局',
            statusText: '待开始',
            timeText: '今天 22:00',
            flowMode: 'FLOW',
            nodeName: '待办模块页面设计',
          },
          {
            id: 13,
            title: '整理节点页展示内容',
            statusText: '待开始',
            timeText: '明天 10:00',
            flowMode: 'FREE',
            nodeName: '待办模块页面设计',
          },
        ],
      },
      {
        key: 'goal_1',
        title: '拿到驾驶证',
        count: 2,
        collapsed: true,
        type: '目标',
        tasks: [
          {
            id: 21,
            title: '预约科目二练车',
            statusText: '待开始',
            timeText: '本周五 14:00',
            flowMode: 'FLOW',
            nodeName: '',
          },
          {
            id: 22,
            title: '复习倒车入库点位',
            statusText: '进行中',
            timeText: '今晚 20:00',
            flowMode: 'FREE',
            nodeName: '科目二准备',
          },
        ],
      },
    ],
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

  onLoad(options) {
   
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

    this.setData({ todoSections })
  },


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
  handleTaskTap(e) {
    const { taskId } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/task/detail/detail?id=${taskId}`,
    })
  },

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
})