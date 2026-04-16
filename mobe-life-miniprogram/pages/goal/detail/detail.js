Page({
  data: {
    goalId: '',
    detail: {
      title: '拿到驾驶证',
      desc: '希望在今年内完成驾驶证考试，提升通勤和出行的便利性。',
      statusText: '进行中',
      executionMode: '流程模式',
      nodeCount: 3,
      taskCount: 6,
      progressText: '2/6',
      templateName: '个人成长流程',
      flowPreviewText: '待开始 → 进行中 → 已完成',
      nodes: [
        {
          id: 'node_1',
          title: '科目一准备',
          taskCount: 2,
        },
        {
          id: 'node_2',
          title: '科目二准备',
          taskCount: 2,
        },
      ],
      tasks: [
        {
          id: 201,
          title: '预约科目二练车',
          statusText: '待开始',
          timeText: '本周五 14:00',
        },
        {
          id: 202,
          title: '复习倒车入库点位',
          statusText: '进行中',
          timeText: '今晚 20:00',
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
          name: '已完成',
          attrs: '结束 · 启用',
        },
      ],
      logs: [
        {
          id: 'log_1',
          text: '目标状态由待开始变更为进行中',
          timeText: '今天 09:20',
        },
        {
          id: 'log_2',
          text: '新增节点：科目二准备',
          timeText: '昨天 18:40',
        },
        {
          id: 'log_3',
          text: '待办“复习倒车入库点位”更新为进行中',
          timeText: '昨天 14:30',
        },
      ],
    },
  },

  onLoad(options) {
    this.setData({
      goalId: options?.id || '',
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