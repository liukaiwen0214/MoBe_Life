Page({
  data: {
    summaryText: '共有 1 个项目，2 个待办；1 个目标，1 个待办',
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