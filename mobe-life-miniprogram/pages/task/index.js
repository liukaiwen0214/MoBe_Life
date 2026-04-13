const SUB_MAP = {
  brush: {
    title: '笔触',
    desc: '这里先放待办主内容，后面再接你的任务列表和快速创建。',
  },
  trace: {
    title: '刻痕',
    desc: '这里先放完成记录，后面可以接已完成清单与完成时间轴。',
  },
  collect: {
    title: '拾遗',
    desc: '这里先放收集箱，后面适合承接临时想法和未归类事项。',
  },
}

Page({
  data: {
    subKey: 'brush',
    currentInfo: SUB_MAP.brush,
  },

  onLoad(options) {
    this.applySubKey(options?.subKey)
  },

  applySubKey(subKey) {
    const nextKey = SUB_MAP[subKey] ? subKey : 'brush'
    this.setData({
      subKey: nextKey,
      currentInfo: SUB_MAP[nextKey],
    })
  },
})
