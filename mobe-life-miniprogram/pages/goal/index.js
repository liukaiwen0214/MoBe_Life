const SUB_MAP = {
  mountain: {
    title: '远山',
    desc: '这里先放长期目标，后面适合接年度目标或长期方向。',
  },
  outline: {
    title: '轮廓',
    desc: '这里先放阶段规划，后面适合接里程碑和路线拆解。',
  },
  lead: {
    title: '引线',
    desc: '这里先放推进动作，后面适合接下一步行动和关键提醒。',
  },
}

Page({
  data: {
    subKey: 'mountain',
    currentInfo: SUB_MAP.mountain,
  },

  onLoad(options) {
    this.applySubKey(options?.subKey)
  },

  applySubKey(subKey) {
    const nextKey = SUB_MAP[subKey] ? subKey : 'mountain'
    this.setData({
      subKey: nextKey,
      currentInfo: SUB_MAP[nextKey],
    })
  },
})
