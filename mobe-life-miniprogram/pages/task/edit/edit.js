import { getTaskDetail, updateTask } from '../../../api/task'

Page({
  data: {
    taskId: '',
    saving: false,
    form: {
      title: '',
      content: '',
      remark: '',
      planStartDate: '',
      planStartClock: '',
      planEndDate: '',
      planEndClock: '',
      deadlineDate: '',
      deadlineClock: '',
    },
  },

  onLoad(options) {
    const taskId = options?.id || ''
    this.setData({ taskId })

    if (taskId) {
      this.loadTaskDetail(taskId)
    }
  },

  async loadTaskDetail(taskId) {
    try {
      const detail = await getTaskDetail(taskId)

      this.setData({
        form: {
          title: detail?.title || '',
          content: detail?.content || '',
          remark: detail?.remark || '',
          planStartDate: this.formatDateForInput(detail?.planStartTime),
          planStartClock: this.formatClockForInput(detail?.planStartTime),
          planEndDate: this.formatDateForInput(detail?.planEndTime),
          planEndClock: this.formatClockForInput(detail?.planEndTime),
          deadlineDate: this.formatDateForInput(detail?.deadlineTime),
          deadlineClock: this.formatClockForInput(detail?.deadlineTime),
        },
      })
    } catch (error) {
      console.error('加载待办详情失败', error)
      wx.showToast({
        title: '待办详情加载失败',
        icon: 'none',
      })
    }
  },

  formatDateForInput(value) {
    if (!value) {
      return ''
    }

    const date = new Date(value)
    if (Number.isNaN(date.getTime())) {
      return ''
    }

    const year = date.getFullYear()
    const month = `${date.getMonth() + 1}`.padStart(2, '0')
    const day = `${date.getDate()}`.padStart(2, '0')
    return `${year}-${month}-${day}`
  },

  formatClockForInput(value) {
    if (!value) {
      return ''
    }

    const date = new Date(value)
    if (Number.isNaN(date.getTime())) {
      return ''
    }

    const hh = `${date.getHours()}`.padStart(2, '0')
    const mm = `${date.getMinutes()}`.padStart(2, '0')
    return `${hh}:${mm}`
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

  handlePlanStartDateChange(e) {
    this.setData({
      'form.planStartDate': e.detail.value,
    })
  },

  handlePlanStartClockChange(e) {
    this.setData({
      'form.planStartClock': e.detail.value,
    })
  },

  handlePlanEndDateChange(e) {
    this.setData({
      'form.planEndDate': e.detail.value,
    })
  },

  handlePlanEndClockChange(e) {
    this.setData({
      'form.planEndClock': e.detail.value,
    })
  },

  handleDeadlineDateChange(e) {
    this.setData({
      'form.deadlineDate': e.detail.value,
    })
  },

  handleDeadlineClockChange(e) {
    this.setData({
      'form.deadlineClock': e.detail.value,
    })
  },

  buildDateTime(date, clock) {
    if (!date || !clock) {
      return null
    }
    return `${date}T${clock}:00`
  },

  async handleSubmit() {
    const { taskId, form } = this.data

    if (!taskId) {
      return
    }

    if (!form.title || !form.title.trim()) {
      wx.showToast({
        title: '请输入待办名称',
        icon: 'none',
      })
      return
    }

    try {
      this.setData({ saving: true })

      await updateTask(taskId, {
        title: form.title.trim(),
        content: form.content || '',
        remark: form.remark || '',
        planStartTime: this.buildDateTime(form.planStartDate, form.planStartClock),
        planEndTime: this.buildDateTime(form.planEndDate, form.planEndClock),
        deadlineTime: this.buildDateTime(form.deadlineDate, form.deadlineClock),
      })

      wx.showToast({
        title: '修改成功',
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
      console.error('修改待办失败', error)

      const message =
        error?.message ||
        error?.msg ||
        error?.data?.message ||
        '修改失败'

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