Page({
  data: {
    taskId: '',
    detail: {
      title: '整理任务模块接口清单',
      statusText: '进行中',
      timeText: '今天 18:00',
      nodeName: '待办模块页面设计',
      desc: '这里先放待办详情内容，后面再接真实字段和接口。',
    },
  },

  onLoad(options) {
    this.setData({
      taskId: options?.id || '',
    })
  },
})