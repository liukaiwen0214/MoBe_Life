/**
 * 核心职责：承载小程序页面 `finance` 的交互逻辑和数据流。
 * 所属业务模块：小程序展示层 / 页面逻辑。
 * 重要依赖关系或外部约束：页面脚本通常与同目录的 WXML、WXSS 配合工作，字段命名应与模板绑定保持一致。
 */
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
