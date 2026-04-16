Page({
  data: {
    projectId: '',
    detail: {
      title: 'MoBe Life 系统开发',
      desc: '用于承接个人生活管理系统的整体设计、开发与迭代。',
      statusText: '进行中',
      executionMode: '流程模式',
      nodeCount: 5,
      taskCount: 12,
      progressText: '6/12',
      templateName: '开发任务流程',
      flowPreviewText: '待开始 → 进行中 → 测试中 → 已完成',
      nodes: [
        {
          id: 'node_1',
          title: '待办模块页面设计',
          taskCount: 3,
        },
        {
          id: 'node_2',
          title: '登录注册联调',
          taskCount: 2,
        },
      ],
      tasks: [
        {
          id: 101,
          title: '整理任务模块接口清单',
          statusText: '进行中',
          timeText: '今天 18:00',
        },
        {
          id: 102,
          title: '确认待办页面布局',
          statusText: '待开始',
          timeText: '今天 22:00',
        },
      ],
      statusList: [
        {
          id: 'status_1',
          name: '待开始',
          attrs: '初始 · 启用',
        },
        {
          id: 'status_2',
          name: '进行中',
          attrs: '启用',
        },
        {
          id: 'status_3',
          name: '测试中',
          attrs: '启用',
        },
        {
          id: 'status_4',
          name: '已完成',
          attrs: '结束 · 启用',
        },
      ],
      logs: [
        {
          id: 'log_1',
          text: '项目状态由待开始变更为进行中',
          timeText: '今天 10:30',
        },
        {
          id: 'log_2',
          text: '新增节点：待办模块页面设计',
          timeText: '昨天 18:20',
        },
        {
          id: 'log_3',
          text: '待办“整理任务模块接口清单”更新为进行中',
          timeText: '昨天 14:10',
        },
      ],
    },
  },

  onLoad(options) {
    this.setData({
      projectId: options?.id || '',
    })
  },

  handleNodeTap(e) {
    const { nodeId } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/node/detail/detail?id=${nodeId}`,
    })
  },

  handleTaskTap(e) {
    const { taskId } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/task/detail/detail?id=${taskId}`,
    })
  },
})