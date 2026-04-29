Page({
  data: {
    hideAsset: false,

    overview: {
      totalAssetText: '128,450.00',
      monthIncomeText: '25,800.00',
      monthExpenseText: '18,320.00',
      monthBalanceText: '7,480.00',

      budgetUsedRatio: 35.6,
      budgetUsedText: '35.6% 已用',
      budgetRemainText: '7,719.50',
      budgetSpentText: '4,280.50',

      todayExpenseText: '145.00',

      // 今日支出占“已花部分”的尾部比例，先伪造，后续接接口再算
      todayInnerLeft: 82.6,
      todayInnerWidth: 17.4,
    },

    weekSummaryText: '本周共支出 ¥1,310.00',
    currentWeekIndex: 2, // 示例：今天是周三，0=周一

    weekTrendList: [
      { label: '一', amount: 180 },
      { label: '二', amount: 260 },
      { label: '三', amount: 140 },
      { label: '四', amount: 310 },
      { label: '五', amount: 420 },
      { label: '六', amount: 280 },
      { label: '日', amount: 190 },
    ],

    trendList: [],

    rankingList: [
      {
        id: 1,
        name: '餐饮美食',
        amountText: '1,850.00',
        ratioText: '42%',
        progress: 42,
        dotClass: 'rank-dot--orange',
        barClass: 'rank-bar-fill--orange',
      },
      {
        id: 2,
        name: '交通出行',
        amountText: '840.50',
        ratioText: '20%',
        progress: 20,
        dotClass: 'rank-dot--blue',
        barClass: 'rank-bar-fill--blue',
      },
      {
        id: 3,
        name: '休闲购物',
        amountText: '620.00',
        ratioText: '15%',
        progress: 15,
        dotClass: 'rank-dot--purple',
        barClass: 'rank-bar-fill--purple',
      },
      {
        id: 4,
        name: '居家生活',
        amountText: '530.00',
        ratioText: '12%',
        progress: 12,
        dotClass: 'rank-dot--green',
        barClass: 'rank-bar-fill--green',
      },
    ],
  },

  onLoad() {
    this.prepareWeekTrend()
  },

  onShow() {
    if (this.data.trendList && this.data.trendList.length) {
      this.drawWeekTrendCanvas()
    }
  },

  toggleAssetVisible() {
    this.setData({
      hideAsset: !this.data.hideAsset,
    })
  },

  prepareWeekTrend() {
    const list = this.data.weekTrendList || []
    const currentWeekIndex = this.data.currentWeekIndex
  
    const trendList = list.map((item, index) => ({
      ...item,
      isCurrent: index === currentWeekIndex,
      isFuture: index > currentWeekIndex,
      amountLeftPx: 0,
      amountBottomPx: 0,
    }))
  
    this.setData(
      {
        trendList,
      },
      () => {
        this.drawWeekTrendCanvas()
      }
    )
  },

  drawWeekTrendCanvas() {
    const query = wx.createSelectorQuery().in(this)
    query.select('#financeTrendCanvas').fields({ node: true, size: true })
    query.exec((res) => {
      const canvasInfo = res[0]
      if (!canvasInfo || !canvasInfo.node) {
        return
      }

      const canvas = canvasInfo.node
      const ctx = canvas.getContext('2d')
      const dpr = wx.getWindowInfo ? wx.getWindowInfo().pixelRatio : 2

      const width = canvasInfo.width
      const height = canvasInfo.height

      canvas.width = width * dpr
      canvas.height = height * dpr
      ctx.scale(dpr, dpr)

      ctx.clearRect(0, 0, width, height)

      const list = this.data.trendList || []
      if (!list.length) {
        return
      }

      const currentWeekIndex = this.data.currentWeekIndex
      const values = list.map((item) => item.amount)
      const max = Math.max(...values)
      const min = Math.min(...values)
      const range = Math.max(max - min, 1)

      const paddingLeft = 16
      const paddingRight = 16
      const paddingTop = 18
      const paddingBottom = 22

      const chartWidth = width - paddingLeft - paddingRight
      const chartHeight = height - paddingTop - paddingBottom
      const stepX = list.length > 1 ? chartWidth / (list.length - 1) : 0

      const points = list.map((item, index) => {
        const ratio = (item.amount - min) / range
        const x = paddingLeft + stepX * index
        const y = paddingTop + chartHeight - ratio * chartHeight
        return { x, y, ...item }
      })
      const trendListWithPosition = points.map((point, index) => ({
        ...this.data.trendList[index],
        amountLeftPx: point.x,
        amountBottomPx: height - point.y + 10, // 点上方一点点
      }))
      this.setData({
        trendList: trendListWithPosition,
      })

      const drawSmoothPath = (pts, startIndex, endIndex) => {
        if (endIndex - startIndex < 1) return

        ctx.beginPath()
        ctx.moveTo(pts[startIndex].x, pts[startIndex].y)

        for (let i = startIndex; i < endIndex; i++) {
          const current = pts[i]
          const next = pts[i + 1]

          const controlX = (current.x + next.x) / 2
          ctx.bezierCurveTo(
            controlX,
            current.y,
            controlX,
            next.y,
            next.x,
            next.y
          )
        }

        ctx.stroke()
      }

      // 过去到今天：主绿色平滑线
      ctx.strokeStyle = '#35c86a'
      ctx.lineWidth = 2.5
      ctx.lineCap = 'round'
      ctx.lineJoin = 'round'
      drawSmoothPath(points, 0, currentWeekIndex)

      // 今天之后：浅绿色平滑线
      if (currentWeekIndex < points.length - 1) {
        ctx.strokeStyle = '#d7e8da'
        ctx.lineWidth = 2.5
        ctx.lineCap = 'round'
        ctx.lineJoin = 'round'
        drawSmoothPath(points, currentWeekIndex, points.length - 1)
      }

      // 画点
      points.forEach((point, index) => {
        ctx.beginPath()
        ctx.arc(point.x, point.y, 5, 0, Math.PI * 2)

        if (index === currentWeekIndex) {
          ctx.fillStyle = '#35c86a'
          ctx.fill()
        } else if (index > currentWeekIndex) {
          ctx.fillStyle = '#ffffff'
          ctx.fill()
          ctx.strokeStyle = '#cfe2d4'
          ctx.lineWidth = 2
          ctx.stroke()
        } else {
          ctx.fillStyle = '#ffffff'
          ctx.fill()
          ctx.strokeStyle = '#9fd7ae'
          ctx.lineWidth = 2
          ctx.stroke()
        }
      })
    })
  },

  handleGoBillDetail() {
    wx.showToast({
      title: '后续进入账单明细',
      icon: 'none',
    })
  },

  handleGoRankingDetail() {
    wx.showToast({
      title: '后续进入排行详情',
      icon: 'none',
    })
  },
})