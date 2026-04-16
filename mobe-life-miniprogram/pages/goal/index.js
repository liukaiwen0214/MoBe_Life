Page({
  data: {
    summaryText: '共有 3 个目标，9 个节点，18 个待办',
    goalList: [
      {
        id: 'goal_1',
        title: '拿到驾驶证',
        nodeCount: 3,
        taskCount: 6,
        progressText: '2/6',
        statusText: '进行中',
        updateText: '今天更新',
      },
      {
        id: 'goal_2',
        title: '完成年度阅读计划',
        nodeCount: 4,
        taskCount: 8,
        progressText: '5/8',
        statusText: '进行中',
        updateText: '昨天更新',
      },
      {
        id: 'goal_3',
        title: '养成早睡习惯',
        nodeCount: 2,
        taskCount: 4,
        progressText: '1/4',
        statusText: '待开始',
        updateText: '3 天前更新',
      },
    ],
  },

  handleGoalTap(e) {
    const { goalId } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/goal/detail/detail?id=${goalId}`,
    })
  },

  handleCreateGoal() {
    wx.showToast({
      title: '进入新建目标',
      icon: 'none',
    })
  },
})