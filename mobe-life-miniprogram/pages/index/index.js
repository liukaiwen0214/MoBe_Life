/**
 * 核心职责：作为小程序首页入口，负责校验本地登录态、拉取当前用户资料，
 * 并承载首页问候、每日一句、天气、聚焦信息和流水统计的展示。
 */
import storage from '../../utils/storage'
import { getCurrentUser } from '../../api/user'

Page({
  data: {
    loading: true,
    userInfo: null,

    greetingText: '',
    greetingDesc: '',

    quoteCard: {
      text: '把今天最重要的一件事，轻轻落下来。',
      from: 'MoBe Life',
      imageUrl: '',
    },

    weatherCard: {
      city: '定位中',
      weatherText: '正在获取天气',
      temperature: '--°',
      extraText: '愿你今天也有一点轻松的呼吸感',
      ready: false,
    },

    focusCards: [
      {
        key: 'writing',
        title: '待办',
        value: '0 项',
        desc: '今天待办还没有开始',
      },
      {
        key: 'guide',
        title: '牵引',
        value: '未设置',
        desc: '先去确定你当前最想推进的方向',
      },
      {
        key: 'scale',
        title: '刻度',
        value: '0 元',
        desc: '今天还没有新的流水记录',
      },
    ],

    flowStats: {
      income: '0.00',
      expense: '0.00',
      balance: '0.00',
      latestRecord: '还没有新的流水记录',
    },
  },

  async onShow() {
    const token = storage.getToken()

    if (!token) {
      wx.redirectTo({
        url: '/pages/login/login',
      })
      return
    }

    this.setData({
      loading: true,
    })

    try {
      const user = await getCurrentUser()

      getApp().globalData.userInfo = user

      this.setData({
        userInfo: user,
        greetingText: this.getGreetingText(),
        greetingDesc: this.getGreetingDesc(),
      })

      // 这里先并行拉首页附加数据，第一版即使失败也不影响首页主结构。
      await Promise.allSettled([
        this.loadDailyQuote(),
        this.loadWeather(),
        this.loadHomeFocusData(),
      ])

      this.setData({
        loading: false,
      })
    } catch (error) {
      console.error('token check failed:', error)
      storage.removeToken()

      wx.redirectTo({
        url: '/pages/login/login',
      })
    }
  },

  getGreetingText() {
    const hour = new Date().getHours()

    if (hour < 6) return '夜深了'
    if (hour < 11) return '早安'
    if (hour < 14) return '中午好'
    if (hour < 18) return '下午好'
    return '晚上好'
  },

  getGreetingDesc() {
    const hour = new Date().getHours()

    if (hour < 11) return '今天也慢慢推进就好。'
    if (hour < 18) return '先把最重要的一件事做好。'
    return '把今天收一收，也很重要。'
  },

  /**
   * 每日一句：
   * 第一版直接请求 ONE，失败则保留默认文案。
   * 后续可以把这段迁到后端的首页聚合接口里。
   */
  loadDailyQuote() {
    return new Promise((resolve) => {
      wx.request({
        // TODO: 上线前不要保留这里的硬编码地址，需统一切到配置模块或公共请求封装。
        // 本地联调用这个
        // url: 'http://127.0.0.1:8080/api/tool/daily-quote',
        // 服务器联调用这个
        url: 'http://39.106.162.13/api/tool/daily-quote',
        method: 'GET',
        success: (res) => {
          try {
            const result = res.data || {}
            const quote = result.data || {}

            if (res.statusCode !== 200 || result.code !== 0 || !quote) {
              resolve()
              return
            }

            this.setData({
              quoteCard: {
                text: quote.text || '把今天最重要的一件事，轻轻落下来。',
                from: quote.from || 'ONE',
                imageUrl: quote.imageUrl || '',
              },
            })
          } catch (error) {
            console.error('parse daily quote error:', error)
          } finally {
            resolve()
          }
        },
        fail: (error) => {
          console.error('load daily quote error:', error)
          resolve()
        },
      })
    })
  },

  /**
   * 天气：
   * 第一版先走小程序定位，再把经纬度交给后端天气接口。
   * 接口占位，避免后面接真实天气时再改页面结构。
   */
  async loadWeather() {
    try {
      const location = await this.getUserLocation()

      if (!location) {
        this.setWeatherFallback('未开启定位', '天气暂不可用')
        return
      }

      const weather = await this.fetchWeatherByLocation({
        latitude: location.latitude,
        longitude: location.longitude,
      })

      if (!weather) {
        this.setWeatherFallback('当前城市', '天气暂不可用')
        return
      }

      this.setData({
        weatherCard: {
          city: weather.city || '当前城市',
          weatherText: weather.weatherText || '天气未知',
          temperature: weather.temperature || '--°',
          extraText: weather.extraText || '愿你今天也有一点轻松的呼吸感',
          ready: true,
        },
      })
    } catch (error) {
      console.error('load weather error:', error)
      this.setWeatherFallback('当前城市', '天气暂不可用')
    }
  },

  getUserLocation() {
    return new Promise((resolve) => {
      wx.getLocation({
        type: 'gcj02',
        success: (res) => {
          resolve({
            latitude: res.latitude,
            longitude: res.longitude,
          })
        },
        fail: (error) => {
          console.error('wx.getLocation fail:', error)
          resolve(null)
        },
      })
    })
  },

  /**
   * 这里先约定后端天气接口返回结构。
   * 后面把后端天气代理接口做好后，直接按这个结构返回就行：
   * {
   *   city: '济南',
   *   weatherText: '多云',
   *   temperature: '21°',
   *   extraText: '空气有点潮，出门记得带伞'
   * }
   */
  fetchWeatherByLocation({ latitude, longitude }) {
    return new Promise((resolve) => {
      const token = storage.getToken()

      wx.request({
        // TODO: 上线前不要保留这里的硬编码地址，需统一切到配置模块或公共请求封装。
        // url: 'http://127.0.0.1:8080/api/tool/weather',
        url: 'http://39.106.162.13/api/tool/weather',
        method: 'GET',
        data: {
          latitude,
          longitude,
        },
        header: {
          Authorization: `Bearer ${token}`,
        },
        success: (res) => {
          const result = res.data || {}

          if (res.statusCode !== 200 || result.code !== 0 || !result.data) {
            resolve(null)
            return
          }

          resolve(result.data)
        },
        fail: (error) => {
          console.error('weather api fail:', error)
          resolve(null)
        },
      })
    })
  },

  setWeatherFallback(city, weatherText) {
    this.setData({
      weatherCard: {
        city,
        weatherText,
        temperature: '--°',
        extraText: '愿你今天也有一点轻松的呼吸感',
        ready: false,
      },
    })
  },

  /**
   * 首页聚焦和流水统计先用占位数据。
   * 等后面 task / finance / goal 真正接起来，再替换成真实接口。
   */
  async loadHomeFocusData() {
    this.setData({
      focusCards: [
        {
          key: 'writing',
          title: '待办',
          value: '3 项',
          desc: '今天还有 2 件事值得先处理',
        },
        {
          key: 'guide',
          title: '牵引',
          value: '账号体系',
          desc: '继续收束用户中心与接口文档',
        },
        {
          key: 'scale',
          title: '刻度',
          value: '26.00',
          desc: '最近一笔支出：午餐',
        },
      ],
      flowStats: {
        income: '0.00',
        expense: '26.00',
        balance: '-26.00',
        latestRecord: '最近记录：午餐 · 支出 26.00',
      },
    })
  },
  handleFocusCardTap(e) {
    const { key } = e.currentTarget.dataset

    if (key === 'writing') {
      wx.navigateTo({
        url: '/pages/task/index?subKey=brush',
      })
      return
    }

    if (key === 'guide') {
      wx.navigateTo({
        url: '/pages/goal/index?subKey=outline',
      })
      return
    }

    if (key === 'scale') {
      wx.navigateTo({
        url: '/pages/finance/index?subKey=flow',
      })
      return
    }
  },
})
