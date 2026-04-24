/**
 * 核心职责：承载小程序页面 `node` 的交互逻辑和数据流。
 * 所属业务模块：小程序展示层 / 页面逻辑。
 * 重要依赖关系或外部约束：页面脚本通常与同目录的 WXML、WXSS 配合工作，字段命名应与模板绑定保持一致。
 */
import { getNodeDetail, updateNode } from '../../../api/node'

Page({
  data: {
    nodeId: '',
    loading: false,
    submitting: false,
    needRefresh: false,
    form: {
      title: '',
      content: '',
      remark: '',
      ownerTypeText: '',
      ownerName: '',
    },
  },
  onShow() {
    if (this.data.needRefresh && this.data.nodeId) {
      this.setData({ needRefresh: false })
      this.loadNodeDetail(this.data.nodeId)
    }
  },
  onLoad(options) {
    const nodeId = options?.id || ''
    this.setData({ nodeId })

    if (nodeId) {
      this.loadNodeDetail(nodeId)
    }
  },

  async loadNodeDetail(nodeId) {
    this.setData({ loading: true })

    try {
      const detail = await getNodeDetail(nodeId)

      this.setData({
        form: {
          title: detail?.title || '',
          content: detail?.content || '',
          remark: detail?.remark || '',
          ownerTypeText: this.formatOwnerType(detail?.ownerType),
          ownerName: detail?.ownerName || '',
        },
      })
    } catch (error) {
      console.error('加载节点详情失败', error)
      wx.showToast({
        title: '节点详情加载失败',
        icon: 'none',
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  formatOwnerType(ownerType) {
    if (ownerType === 'PROJECT') {
      return '项目'
    }
    if (ownerType === 'GOAL') {
      return '目标'
    }
    return ''
  },

  handleTitleInput(e) {
    this.setData({
      'form.title': e.detail.value,
    })
  },

  handleContentInput(e) {
    this.setData({
      'form.content': e.detail.value,
    })
  },

  handleRemarkInput(e) {
    this.setData({
      'form.remark': e.detail.value,
    })
  },

  handleOwnerTap() {
    wx.showToast({
      title: '请前往网页端修改所属项目/目标',
      icon: 'none',
    })
  },

  async handleSubmit() {
    const { nodeId, form, submitting } = this.data
    if (!nodeId || submitting) {
      return
    }

    const title = (form.title || '').trim()
    if (!title) {
      wx.showToast({
        title: '请输入节点名称',
        icon: 'none',
      })
      return
    }

    this.setData({ submitting: true })

    try {
      await updateNode(nodeId, {
        title,
        content: form.content || '',
        remark: form.remark || '',
      })

      wx.showToast({
        title: '保存成功',
        icon: 'success',
      })

      const pages = getCurrentPages()
      const prevPage = pages.length > 1 ? pages[pages.length - 2] : null
      const prevPrevPage = pages.length > 2 ? pages[pages.length - 3] : null

      if (prevPage && typeof prevPage.setData === 'function') {
        prevPage.setData({ needRefresh: true })
      }

      if (prevPrevPage && typeof prevPrevPage.setData === 'function') {
        prevPrevPage.setData({ needRefresh: true })
      }

      setTimeout(() => {
        wx.navigateBack()
      }, 600)
    } catch (error) {
      console.error('编辑节点失败', error)

      const message =
        error?.message ||
        error?.msg ||
        error?.data?.message ||
        '保存失败'

      wx.showModal({
        title: '提示',
        content: message,
        showCancel: false,
      })
    } finally {
      this.setData({ submitting: false })
    }
  },
})