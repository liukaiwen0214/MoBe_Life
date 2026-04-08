App({
  globalData: {
    userInfo: null,
    fontLoaded: false,
  },

  onLaunch() {
    wx.loadFontFace({
      family: 'MoBeFont',
      source: 'url("http://127.0.0.1:8080/fonts/LXGWWenKai-Light.ttf")',
      global: true,
      success: () => {
        console.log('字体加载成功')
        this.globalData.fontLoaded = true
      },
      fail: (err) => {
        console.error('字体加载失败', err)
      },
    })
  },
})