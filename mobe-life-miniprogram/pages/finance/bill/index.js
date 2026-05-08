Page({
  data: {
    summary: {
      monthExpenseText: '18,320.00',
      monthIncomeText: '25,800.00',
      monthBalanceText: '7,480.00',
    },

    filterTabs: [
      { key: 'all', label: '全部' },
      { key: 'expense', label: '支出' },
      { key: 'income', label: '收入' },
      { key: 'transfer', label: '转账' },
    ],
    activeFilter: 'all',

    filterChips: [
      { key: 'time', label: '本月' },
      { key: 'category', label: '分类' },
      { key: 'account', label: '账户' },
    ],

    keyword: '',

    billSectionList: [
      {
        date: '2026-04-29',
        weekText: '周二',
        totalExpenseText: '326.50',
        totalIncomeText: '0.00',
        expanded: true,
        items: [
          {
            id: 1,
            type: 'expense',
            title: '午餐',
            subText: '餐饮美食 · 现金账户 · 12:20',
            amountText: '-26.00',
            accentClass: 'bill-accent--orange',
            amountClass: 'bill-amount--expense',
          },
          {
            id: 2,
            type: 'expense',
            title: '打车去公司',
            subText: '交通出行 · 微信账户 · 09:10',
            amountText: '-38.50',
            accentClass: 'bill-accent--blue',
            amountClass: 'bill-amount--expense',
          },
          {
            id: 3,
            type: 'expense',
            title: '超市购物',
            subText: '居家生活 · 招行储蓄卡 · 20:36',
            amountText: '-262.00',
            accentClass: 'bill-accent--green',
            amountClass: 'bill-amount--expense',
          },
        ],
      },
      {
        date: '2026-04-28',
        weekText: '周一',
        totalExpenseText: '145.00',
        totalIncomeText: '8,000.00',
        expanded: false,
        items: [
          {
            id: 4,
            type: 'income',
            title: '工资入账',
            subText: '工资收入 · 工商银行卡 · 08:30',
            amountText: '+8,000.00',
            accentClass: 'bill-accent--green-strong',
            amountClass: 'bill-amount--income',
          },
          {
            id: 5,
            type: 'expense',
            title: '晚餐',
            subText: '餐饮美食 · 微信账户 · 19:08',
            amountText: '-45.00',
            accentClass: 'bill-accent--orange',
            amountClass: 'bill-amount--expense',
          },
          {
            id: 6,
            type: 'expense',
            title: '咖啡',
            subText: '餐饮美食 · 支付宝账户 · 15:14',
            amountText: '-26.00',
            accentClass: 'bill-accent--orange',
            amountClass: 'bill-amount--expense',
          },
          {
            id: 7,
            type: 'expense',
            title: '文具',
            subText: '学习办公 · 现金账户 · 10:42',
            amountText: '-74.00',
            accentClass: 'bill-accent--purple',
            amountClass: 'bill-amount--expense',
          },
        ],
      },
      {
        date: '2026-04-27',
        weekText: '周日',
        totalExpenseText: '520.00',
        totalIncomeText: '0.00',
        expanded: false,
        items: [
          {
            id: 8,
            type: 'transfer',
            title: '转入微信零钱',
            subText: '招行储蓄卡 → 微信账户 · 11:20',
            amountText: '¥500.00',
            accentClass: 'bill-accent--slate',
            amountClass: 'bill-amount--transfer',
          },
          {
            id: 9,
            type: 'expense',
            title: '买书',
            subText: '休闲购物 · 微信账户 · 16:52',
            amountText: '-20.00',
            accentClass: 'bill-accent--purple',
            amountClass: 'bill-amount--expense',
          },
        ],
      },
    ],

    displaySectionList: [],
  },

  onLoad() {
    this.applyFilters()
  },

  handleFilterTabTap(e) {
    const { key } = e.currentTarget.dataset
    if (!key || key === this.data.activeFilter) {
      return
    }

    this.setData(
      {
        activeFilter: key,
      },
      () => {
        this.applyFilters()
      }
    )
  },

  handleKeywordInput(e) {
    this.setData(
      {
        keyword: e.detail.value || '',
      },
      () => {
        this.applyFilters()
      }
    )
  },

  handleToggleSection(e) {
    const { date } = e.currentTarget.dataset
    if (!date) {
      return
    }

    const nextList = this.data.displaySectionList.map((section) => {
      if (section.date !== date) {
        return section
      }
      return {
        ...section,
        expanded: !section.expanded,
      }
    })

    this.setData({
      displaySectionList: nextList,
    })
  },

  applyFilters() {
    const { billSectionList, activeFilter, keyword, displaySectionList } = this.data
    const normalizedKeyword = (keyword || '').trim().toLowerCase()

    const expandedMap = {}
    ;(displaySectionList || []).forEach((section) => {
      expandedMap[section.date] = section.expanded
    })

    const nextSectionList = billSectionList
      .map((section, index) => {
        const filteredItems = section.items.filter((item) => {
          const matchesType = activeFilter === 'all' ? true : item.type === activeFilter
          const matchesKeyword = !normalizedKeyword
            ? true
            : `${item.title} ${item.subText}`.toLowerCase().includes(normalizedKeyword)

          return matchesType && matchesKeyword
        })

        return {
          ...section,
          expanded:
            expandedMap[section.date] !== undefined
              ? expandedMap[section.date]
              : index === 0,
          items: filteredItems,
        }
      })
      .filter((section) => section.items.length > 0)

    this.setData({
      displaySectionList: nextSectionList,
    })
  },

  handleFakeFilterTap(e) {
    const { key } = e.currentTarget.dataset
    const map = {
      time: '后续打开时间筛选',
      category: '后续打开分类筛选',
      account: '后续打开账户筛选',
    }

    wx.showToast({
      title: map[key] || '后续补充筛选',
      icon: 'none',
    })
  },

  handleCreateBill() {
    wx.showToast({
      title: '后续进入记一笔',
      icon: 'none',
    })
  },

  handleBillDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.showToast({
      title: `后续进入账单详情 ${id}`,
      icon: 'none',
    })
  },
})