/**
 * 核心职责：承载小程序页面 `node` 的交互逻辑和数据流。
 * 所属业务模块：小程序展示层 / 页面逻辑。
 * 重要依赖关系或外部约束：页面脚本通常与同目录的 WXML、WXSS 配合工作，字段命名应与模板绑定保持一致。
 */
import { createNode } from '../../../api/node'
import { getProjectList } from '../../../api/project'
import { getGoalList } from '../../../api/goal'

Page({
  data: {
    loading: false,
    submitting: false,
    ownerTypeOptions: ['项目', '目标'],
    ownerTypeValue: 'PROJECT',
    ownerTypeIndex: 0,
    ownerOptions: [],
    ownerIndex: -1,
    ownerId: '',
    form: {
      title: '',
      content: '',
      remark: '',
    },
  },

  onLoad() {
    this.loadOwnerOptions()
  },

  async loadOwnerOptions() {
    this.setData({ loading: true })

    try {
      if (this.data.ownerTypeValue === 'PROJECT') {
        const res = await getProjectList({
          pageNum: 1,
          pageSize: 100,
          includeCompleted: false,
        })

        const list = Array.isArray(res?.list) ? res.list : []
        this.setData({
          ownerOptions: list.map((item) => ({
            id: item.id,
            name: item.title || '未命名项目',
          })),
          ownerIndex: list.length ? 0 : -1,
          ownerId: list.length ? list[0].id : '',
        })
        return
      }

      const res = await getGoalList({
        pageNum: 1,
        pageSize: 100,
        includeCompleted: false,
      })

      const list = Array.isArray(res?.list) ? res.list : []
      this.setData({
        ownerOptions: list.map((item) => ({
          id: item.id,
          name: item.title || '未命名目标',
        })),
        ownerIndex: list.length ? 0 : -1,
        ownerId: list.length ? list[0].id : '',
      })
    } catch (error) {
      console.error('加载所属对象失败', error)
      wx.showToast({
        title: '所属对象加载失败',
        icon: 'none',
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  handleOwnerTypeChange(e) {
    const index = Number(e.detail.value)
    const ownerTypeValue = index === 0 ? 'PROJECT' : 'GOAL'

    this.setData({
      ownerTypeIndex: index,
      ownerTypeValue,
      ownerOptions: [],
      ownerIndex: -1,
      ownerId: '',
    })

    this.loadOwnerOptions()
  },

  handleOwnerChange(e) {
    const index = Number(e.detail.value)
    const current = this.data.ownerOptions[index]

    this.setData({
      ownerIndex: index,
      ownerId: current ? current.id : '',
    })
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

  async handleSubmit() {
    const { submitting, ownerTypeValue, ownerId, form } = this.data
    if (submitting) {
      return
    }

    const title = (form.title || '').trim()
    if (!ownerId) {
      wx.showToast({
        title: '请选择所属对象',
        icon: 'none',
      })
      return
    }

    if (!title) {
      wx.showToast({
        title: '请输入节点名称',
        icon: 'none',
      })
      return
    }

    this.setData({ submitting: true })

    try {
      await createNode({
        ownerType: ownerTypeValue,
        ownerId,
        title,
        content: form.content || '',
        remark: form.remark || '',
      })

      wx.showToast({
        title: '新增成功',
        icon: 'success',
      })

      const pages = getCurrentPages()
      const prevPage = pages.length > 1 ? pages[pages.length - 2] : null
      if (prevPage && typeof prevPage.setData === 'function') {
        prevPage.setData({ needRefresh: true })
      }

      setTimeout(() => {
        wx.navigateBack()
      }, 600)
    } catch (error) {
      console.error('新增节点失败', error)

      const message =
        error?.message ||
        error?.msg ||
        error?.data?.message ||
        '新增失败'

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