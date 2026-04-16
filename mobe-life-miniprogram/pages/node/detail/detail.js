Page({
  data: {
    nodeId: '',
    detail: {
      title: '待办模块页面设计',
      ownerName: 'MoBe Life 系统开发',
      ownerType: '项目',
      summaryText: '这个节点下共有 2 个待办',
      tasks: [
        {
          id: 101,
          title: '确认待办页面布局',
          statusText: '待开始',
          timeText: '今天 22:00',
        },
        {
          id: 102,
          title: '整理节点页展示内容',
          statusText: '进行中',
          timeText: '明天 10:00',
        },
      ],
    },
  },

  onLoad(options) {
    this.setData({
      nodeId: options?.id || '',
    })
  },

  handleTaskTap(e) {
    const { taskId } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/task/detail/detail?id=${taskId}`,
    })
  },
})