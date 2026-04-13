const NAV_ITEMS = [
  {
    key: 'writing',
    label: '落笔',
    type: 'parent',
    pagePath: '/pages/task/index',
    icon: '/assets/icons/tabbar/writing.svg',
    children: [
      { key: 'brush', label: '笔触', desc: '待办' },
      { key: 'trace', label: '刻痕', desc: '完成' },
      { key: 'collect', label: '拾遗', desc: '收集' },
    ],
  },
  {
    key: 'scale',
    label: '刻度',
    type: 'parent',
    pagePath: '/pages/finance/index',
    icon: '/assets/icons/tabbar/scale.svg',
    children: [
      { key: 'flow', label: '流水', desc: '明细' },
      { key: 'budget', label: '筹码', desc: '预算' },
      { key: 'echo', label: '回声', desc: '统计' },
    ],
  },
  {
    key: 'now',
    label: '此刻',
    type: 'fixed',
    pagePath: '/pages/index/index',
    icon: '/assets/icons/tabbar/now.svg',
  },
  {
    key: 'guide',
    label: '牵引',
    type: 'parent',
    pagePath: '/pages/goal/index',
    icon: '/assets/icons/tabbar/guide.svg',
    children: [
      { key: 'mountain', label: '远山', desc: '目标' },
      { key: 'outline', label: '轮廓', desc: '规划' },
      { key: 'lead', label: '引线', desc: '推进' },
    ],
  },
  {
    key: 'base',
    label: '底色',
    type: 'fixed',
    pagePath: '/pages/mine/index',
    icon: '/assets/icons/tabbar/base.svg',
  },
]

Component({
  properties: {
    current: {
      type: String,
      value: '',
    },
    currentSub: {
      type: String,
      value: '',
    },
  },

  data: {
    navItems: NAV_ITEMS,
    expandedKey: '',
  },

  methods: {
    handleNavTap(e) {
      const { key } = e.currentTarget.dataset
      const nav = NAV_ITEMS.find((item) => item.key === key)

      if (!nav) return

      if (nav.type === 'fixed') {
        this.setData({ expandedKey: '' })

        if (this.properties.current === nav.key) {
          return
        }

        wx.reLaunch({
          url: nav.pagePath,
        })
        return
      }

      this.setData({
        expandedKey: this.data.expandedKey === nav.key ? '' : nav.key,
      })
    },

    handleMaskTap() {
      this.setData({
        expandedKey: '',
      })
    },

    handleChildTap(e) {
      const { parentKey, subKey } = e.currentTarget.dataset
      const nav = NAV_ITEMS.find((item) => item.key === parentKey)

      if (!nav) return

      this.setData({ expandedKey: '' })

      wx.reLaunch({
        url: `${nav.pagePath}?subKey=${subKey}`,
      })
    },
  },
})
