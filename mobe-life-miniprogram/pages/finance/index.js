const SUB_MAP = {
  flow: {
    title: '流水',
    desc: '这里先放收支明细，后面接账单列表、日期筛选和分类。',
  },
  budget: {
    title: '筹码',
    desc: '这里先放预算或账户总览，后面再接预算池和账户结构。',
  },
  echo: {
    title: '回声',
    desc: '这里先放统计复盘，后面可以接月度趋势和分类占比。',
  },
}

Page({
  data: {
    subKey: 'flow',
    currentInfo: SUB_MAP.flow,
  },

  onLoad(options) {
    this.applySubKey(options?.subKey)
  },

  applySubKey(subKey) {
    const nextKey = SUB_MAP[subKey] ? subKey : 'flow'
    this.setData({
      subKey: nextKey,
      currentInfo: SUB_MAP[nextKey],
    })
  },
})
