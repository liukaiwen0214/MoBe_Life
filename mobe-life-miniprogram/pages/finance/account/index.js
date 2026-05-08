Page({
  data: {
    overview: {
      totalAssetText: '128,450.00',
      accountCount: 5,
      monthIncomeText: '25,800.00',
      monthExpenseText: '18,320.00',
    },

    accountSectionList: [
      {
        type: 'cash',
        typeName: '日常账户',
        items: [
          {
            id: 1,
            name: '微信账户',
            subText: '零钱 · 常用支付',
            balanceText: '8,235.50',
            monthIncomeText: '2,300.00',
            monthExpenseText: '4,680.20',
            accentClass: 'account-accent--green',
          },
          {
            id: 2,
            name: '支付宝',
            subText: '余额 · 日常消费',
            balanceText: '5,180.00',
            monthIncomeText: '1,200.00',
            monthExpenseText: '2,960.00',
            accentClass: 'account-accent--blue',
          },
          {
            id: 3,
            name: '现金',
            subText: '钱包 · 线下备用',
            balanceText: '860.00',
            monthIncomeText: '0.00',
            monthExpenseText: '420.00',
            accentClass: 'account-accent--orange',
          },
        ],
      },
      {
        type: 'bank',
        typeName: '银行卡',
        items: [
          {
            id: 4,
            name: '招商银行卡',
            subText: '储蓄卡 · 工资卡',
            balanceText: '96,850.00',
            monthIncomeText: '22,000.00',
            monthExpenseText: '8,560.00',
            accentClass: 'account-accent--purple',
          },
        ],
      },
      {
        type: 'credit',
        typeName: '信用账户',
        items: [
          {
            id: 5,
            name: '招商信用卡',
            subText: '信用卡 · 本期待还',
            balanceText: '17,324.50',
            monthIncomeText: '0.00',
            monthExpenseText: '1,700.00',
            accentClass: 'account-accent--slate',
            balanceLabel: '已用额度',
          },
        ],
      },
    ],
  },

  handleCreateAccount() {
    wx.showToast({
      title: '后续进入新增账户',
      icon: 'none',
    })
  },

  handleAccountDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.showToast({
      title: `后续进入账户详情 ${id}`,
      icon: 'none',
    })
  },
})