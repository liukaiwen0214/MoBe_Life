Page({
  data: {
    settingGroupList: [
      {
        id: 'category',
        title: '分类设置',
        desc: '管理收入与支出分类，用于记账录入和筛选展示。',
        meta: '已配置 12 项',
        iconText: '类',
        accentClass: 'setting-accent--green',
      },
      {
        id: 'budget',
        title: '预算设置',
        desc: '设置本月总预算，后续承接预算规则与预算控制。',
        meta: '本月预算已设置',
        iconText: '预',
        accentClass: 'setting-accent--blue',
      },
      {
        id: 'recurring',
        title: '固定收支',
        desc: '管理周期性收入和支出规则，后续可自动生成账单。',
        meta: '共 6 条规则',
        iconText: '固',
        accentClass: 'setting-accent--orange',
      },
      {
        id: 'notice',
        title: '通知设置',
        desc: '管理预算、固定收支等业务的通知提醒规则。',
        meta: '2 项提醒已开启',
        iconText: '通',
        accentClass: 'setting-accent--purple',
      },
    ],
  },

  handleOpenSetting(e) {
    const { id } = e.currentTarget.dataset

    const map = {
      category: '后续进入分类设置',
      budget: '后续进入预算设置',
      recurring: '后续进入固定收支',
      notice: '后续进入通知设置',
    }

    wx.showToast({
      title: map[id] || '后续补充功能',
      icon: 'none',
    })
  },
})