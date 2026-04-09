/**
 * 核心职责：放置无业务副作用的通用格式化函数。
 * 所属业务模块：小程序基础设施 / 通用工具。
 */
const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return `${[year, month, day].map(formatNumber).join('/')} ${[hour, minute, second].map(formatNumber).join(':')}`
}

/**
 * 补齐两位数字格式。
 * 这个函数存在的意义不是“补零”本身，而是保证时间字符串在列表和日志里宽度稳定，便于人眼扫描。
 */
const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : `0${n}`
}

module.exports = {
  formatTime
}
