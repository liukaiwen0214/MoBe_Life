/**
 * 核心职责：定义 Web 端源码的 ESLint 基线规则。
 * 所属业务模块：前端工程化 / 代码质量。
 * 重要依赖关系或外部约束：生成目录和依赖目录必须排除，否则会把框架产物误当成项目源码报错。
 */
import js from '@eslint/js'
import eslintConfigPrettier from 'eslint-config-prettier'

export default [
  {
    ignores: [
      '.nuxt/**',
      '.output/**',
      'dist/**',
      'node_modules/**',
      'coverage/**',
    ],
  },
  js.configs.recommended,
  eslintConfigPrettier,
]
