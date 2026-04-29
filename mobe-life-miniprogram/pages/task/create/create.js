import { createTask } from '../../../api/task'
import { getProjectList } from '../../../api/project'
import { getGoalList } from '../../../api/goal'
import { getNodeList } from '../../../api/node'

Page({
  data: {
    saving: false,
    ownerOptions: [
      { label: '独立', value: 'INDEPENDENT' },
      { label: '项目', value: 'PROJECT' },
      { label: '目标', value: 'GOAL' },
      { label: '节点', value: 'NODE' },
    ],
    ownerTypeIndex: 0,

    projectOptions: [],
    goalOptions: [],
    nodeOptions: [],

    projectIndex: -1,
    goalIndex: -1,
    nodeIndex: -1,

    form: {
      title: '',
      content: '',
      remark: '',
      directOwnerType: 'INDEPENDENT',
      directOwnerId: null,
      planDateStart: '',
      planDateEnd: '',
      deadlineDate: '',
    },
  },

  onLoad() {
    this.loadOwnerOptions()
  },

  async loadOwnerOptions() {
    try {
      const [projectRes, goalRes, nodeRes] = await Promise.allSettled([
        getProjectList({
          pageNum: 1,
          pageSize: 100,
        }),
        getGoalList({
          pageNum: 1,
          pageSize: 100,
        }),
        getNodeList({
          pageNum: 1,
          pageSize: 100,
        }),
      ])

      let projectOptions = []
      let goalOptions = []
      let nodeOptions = []

      if (projectRes.status === 'fulfilled') {
        const projectList = Array.isArray(projectRes.value?.list) ? projectRes.value.list : []
        projectOptions = projectList.map((item) => ({
          label: item.title || '未命名项目',
          value: item.id,
        }))
      }

      if (goalRes.status === 'fulfilled') {
        const goalList = Array.isArray(goalRes.value?.list) ? goalRes.value.list : []
        goalOptions = goalList.map((item) => ({
          label: item.title || '未命名目标',
          value: item.id,
        }))
      }

      if (nodeRes.status === 'fulfilled') {
        const nodeList = Array.isArray(nodeRes.value?.list) ? nodeRes.value.list : []
        nodeOptions = nodeList.map((item) => ({
          label: item.title || '未命名节点',
          value: item.id,
        }))
      }

      this.setData({
        projectOptions,
        goalOptions,
        nodeOptions,
      })
    } catch (error) {
      console.error('加载归属选项失败', error)
    }
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

  handleOwnerTypeChange(e) {
    const ownerTypeIndex = Number(e.detail.value)
    const ownerType = this.data.ownerOptions[ownerTypeIndex]?.value || 'INDEPENDENT'

    this.setData({
      ownerTypeIndex,
      'form.directOwnerType': ownerType,
      'form.directOwnerId': null,
      projectIndex: -1,
      goalIndex: -1,
      nodeIndex: -1,
    })
  },

  handleProjectChange(e) {
    const projectIndex = Number(e.detail.value)
    const project = this.data.projectOptions[projectIndex]

    this.setData({
      projectIndex,
      goalIndex: -1,
      nodeIndex: -1,
      'form.directOwnerId': project?.value || null,
    })
  },

  handleGoalChange(e) {
    const goalIndex = Number(e.detail.value)
    const goal = this.data.goalOptions[goalIndex]

    this.setData({
      goalIndex,
      projectIndex: -1,
      nodeIndex: -1,
      'form.directOwnerId': goal?.value || null,
    })
  },

  handleNodeChange(e) {
    const nodeIndex = Number(e.detail.value)
    const node = this.data.nodeOptions[nodeIndex]

    this.setData({
      nodeIndex,
      projectIndex: -1,
      goalIndex: -1,
      'form.directOwnerId': node?.value || null,
    })
  },

  handlePlanDateStartChange(e) {
    const value = e.detail.value
    this.setData({
      'form.planDateStart': value,
    })
  },

  handlePlanDateEndChange(e) {
    const value = e.detail.value
    this.setData({
      'form.planDateEnd': value,
    })
  },

  handleDeadlineDateChange(e) {
    const value = e.detail.value
    this.setData({
      'form.deadlineDate': value,
    })
  },

  buildPlanStartTime(date) {
    if (!date) {
      return null
    }
    return `${date}T00:00:01`
  },

  buildPlanEndTime(date) {
    if (!date) {
      return null
    }
    return `${date}T23:59:59`
  },

  buildDeadlineTime(date) {
    if (!date) {
      return null
    }
    return `${date}T23:59:59`
  },

  validateForm() {
    const { form } = this.data

    if (!form.title || !form.title.trim()) {
      wx.showToast({
        title: '请输入待办标题',
        icon: 'none',
      })
      return false
    }

    if (form.directOwnerType === 'PROJECT' && !form.directOwnerId) {
      wx.showToast({
        title: '请选择所属项目',
        icon: 'none',
      })
      return false
    }

    if (form.directOwnerType === 'GOAL' && !form.directOwnerId) {
      wx.showToast({
        title: '请选择所属目标',
        icon: 'none',
      })
      return false
    }

    if (form.directOwnerType === 'NODE' && !form.directOwnerId) {
      wx.showToast({
        title: '请选择所属节点',
        icon: 'none',
      })
      return false
    }

    if (form.planDateStart && form.planDateEnd && form.planDateEnd < form.planDateStart) {
      wx.showToast({
        title: '结束日期不能早于开始日期',
        icon: 'none',
      })
      return false
    }

    return true
  },

  async handleSubmit() {
    if (!this.validateForm()) {
      return
    }

    const { form } = this.data

    try {
      this.setData({ saving: true })

      await createTask({
        title: form.title.trim(),
        content: form.content || '',
        remark: form.remark || '',
        directOwnerType: form.directOwnerType,
        directOwnerId: form.directOwnerType === 'INDEPENDENT' ? null : form.directOwnerId,
        planStartTime: this.buildPlanStartTime(form.planDateStart),
        planEndTime: this.buildPlanEndTime(form.planDateEnd),
        deadlineTime: this.buildDeadlineTime(form.deadlineDate),
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
      }, 500)
    } catch (error) {
      console.error('新增待办失败', error)

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
      this.setData({ saving: false })
    }
  },
})