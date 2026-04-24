import { createTask } from '../../../api/task'

Page({
  data: {
    saving: false,
    activeDateField: '',
    activeTimeField: '',
    display: {
      planStartText: '未设置',
      planEndText: '未设置',
      deadlineText: '未设置',
    },
    form: {
      title: '',
      content: '',
      remark: '',
      directOwnerType: 'INDEPENDENT',
      directOwnerId: '',
      statusTemplateId: '',
      planStartDate: '',
      planStartClock: '',
      planEndDate: '',
      planEndClock: '',
      deadlineDate: '',
      deadlineClock: '',
    },
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

  handlePickDateTime(e) {
    const { field } = e.currentTarget.dataset
    if (!field) {
      return
    }

    const dateFieldMap = {
      planStart: 'planStartDate',
      planEnd: 'planEndDate',
      deadline: 'deadlineDate',
    }

    const timeFieldMap = {
      planStart: 'planStartClock',
      planEnd: 'planEndClock',
      deadline: 'deadlineClock',
    }

    this.setData({
      activeDateField: dateFieldMap[field],
      activeTimeField: timeFieldMap[field],
    })
  },

  handleHiddenDateChange(e) {
    const { activeDateField } = this.data
    if (!activeDateField) {
      return
    }

    this.setData({
      [`form.${activeDateField}`]: e.detail.value,
    })
  },

  handleHiddenTimeChange(e) {
    const { activeDateField, activeTimeField } = this.data
    if (!activeTimeField) {
      return
    }

    this.setData({
      [`form.${activeTimeField}`]: e.detail.value,
      activeDateField: '',
      activeTimeField: '',
    }, () => {
      this.syncDisplayText()
    })
  },

  handleClearDateTime(e) {
    const { field } = e.currentTarget.dataset
    if (!field) {
      return
    }

    const fieldMap = {
      planStart: ['planStartDate', 'planStartClock'],
      planEnd: ['planEndDate', 'planEndClock'],
      deadline: ['deadlineDate', 'deadlineClock'],
    }

    const targetFields = fieldMap[field]
    if (!targetFields) {
      return
    }

    this.setData({
      [`form.${targetFields[0]}`]: '',
      [`form.${targetFields[1]}`]: '',
    }, () => {
      this.syncDisplayText()
    })
  },

  formatDateTimeText(date, clock) {
    if (!date || !clock) {
      return '未设置'
    }
    return `${date} ${clock}`
  },

  syncDisplayText() {
    const { form } = this.data

    this.setData({
      'display.planStartText': this.formatDateTimeText(form.planStartDate, form.planStartClock),
      'display.planEndText': this.formatDateTimeText(form.planEndDate, form.planEndClock),
      'display.deadlineText': this.formatDateTimeText(form.deadlineDate, form.deadlineClock),
    })
  },

  buildDateTime(date, clock) {
    if (!date || !clock) {
      return null
    }
    return `${date}T${clock}:00`
  },

  async handleSubmit() {
    const { form } = this.data

    if (!form.title || !form.title.trim()) {
      wx.showToast({
        title: '请输入待办名称',
        icon: 'none',
      })
      return
    }

    try {
      this.setData({ saving: true })

      await createTask({
        title: form.title.trim(),
        content: form.content || '',
        remark: form.remark || '',
        directOwnerType: form.directOwnerType || 'INDEPENDENT',
        directOwnerId: form.directOwnerId || null,
        statusTemplateId: form.statusTemplateId || null,
        planStartTime: this.buildDateTime(form.planStartDate, form.planStartClock),
        planEndTime: this.buildDateTime(form.planEndDate, form.planEndClock),
        deadlineTime: this.buildDateTime(form.deadlineDate, form.deadlineClock),
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