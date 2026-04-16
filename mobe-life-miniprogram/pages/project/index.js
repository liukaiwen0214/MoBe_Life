Page({
  data: {
    summaryText: '共有 3 个项目，12 个节点，21 个待办',
    projectList: [
      {
        id: 'project_1',
        title: 'MoBe Life 系统开发',
        nodeCount: 5,
        taskCount: 12,
        progressText: '6/12',
        statusText: '进行中',
        updateText: '今天更新',
      },
      {
        id: 'project_2',
        title: '用户中心重构',
        nodeCount: 3,
        taskCount: 6,
        progressText: '2/6',
        statusText: '待开始',
        updateText: '昨天更新',
      },
      {
        id: 'project_3',
        title: '登录流程优化',
        nodeCount: 4,
        taskCount: 3,
        progressText: '3/3',
        statusText: '已完成',
        updateText: '3 天前更新',
      },
    ],
  },

  handleProjectTap(e) {
    const { projectId } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/project/detail/detail?id=${projectId}`,
    })
  },

  handleCreateProject() {
    wx.showToast({
      title: '进入新建项目',
      icon: 'none',
    })
  },
})