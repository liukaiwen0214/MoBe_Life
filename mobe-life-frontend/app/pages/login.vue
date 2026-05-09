<!--
  承载 Web 登录页：密码登录与邮箱验证码登录双模式，并串接图形验证码。
  模块：前端展示层 / 认证入口。
  约束：layout 关闭以使用全屏登录壳；依赖 useAuth 与后端 /api/auth 契约一致。
-->
<script setup lang="ts">
definePageMeta({
  layout: false,
})

const loginType = ref<'password' | 'code'>('password')
const showWechatModal = ref(false)

const form = reactive({
  account: '',
  password: '',
  remember: false,
})

const showPassword = ref(false)

const switchLoginType = (type: 'password' | 'code') => {
  loginType.value = type
}
const { loading, getCaptcha, passwordLogin, sendLoginEmailCode, codeLogin } =
  useAuth()

const captchaKey = ref('')
const captchaImage = ref('')
const captchaCode = ref('')
const emailCode = ref('')
const codeSending = ref(false)
const codeCountdown = ref(0)

const loadCaptcha = async () => {
  try {
    const data = await getCaptcha()
    captchaKey.value = data.captchaKey
    captchaImage.value = data.captchaImage
    captchaCode.value = ''
  } catch (error) {
    console.error('加载验证码失败', error)
  }
}

const handlePasswordLogin = async () => {
  try {
    await passwordLogin({
      account: form.account,
      password: form.password,
      captchaKey: captchaKey.value,
      captchaCode: captchaCode.value,
    })
    await navigateTo('/')
  } catch (error) {
    console.error('密码登录失败', error)
    await loadCaptcha()
  }
}

const handleCodeLogin = async () => {
  try {
    await codeLogin({
      account: form.account,
      code: emailCode.value,
    })
    await navigateTo('/')
  } catch (error) {
    console.error('验证码登录失败', error)
  }
}

const handleSubmit = async () => {
  if (loginType.value === 'password') {
    await handlePasswordLogin()
    return
  }

  await handleCodeLogin()
}

const handleSendCode = async () => {
  if (!form.account || codeSending.value || codeCountdown.value > 0) return

  try {
    codeSending.value = true
    await sendLoginEmailCode(form.account)

    codeCountdown.value = 60
    const timer = setInterval(() => {
      codeCountdown.value -= 1
      if (codeCountdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    console.error('发送验证码失败', error)
  } finally {
    codeSending.value = false
  }
}

onMounted(async () => {
  await loadCaptcha()
})
</script>

<template>
  <div class="login-page">
    <div class="login-left">
      <div class="brand">
        <div class="brand-logo">Ψ</div>
        <div class="brand-text">MoBe</div>
      </div>

      <div class="left-content">
        <h1 class="left-title">把日常慢慢整理成自己想要的样子</h1>

        <p class="left-desc">
          我们总会有很多想做的事，<br />
          很多正在推进的事，<br />
          也有很多来不及整理、却又不想丢下的记录。
        </p>

        <div class="feature-list">
          <div class="feature-item">
            <div class="feature-icon">✦</div>
            <div class="feature-main">
              <div class="feature-title">目标与事项，值得被认真放好</div>
              <div class="feature-desc">
                不是为了把生活变得更紧绷，而是想理出一点清晰的秩序
              </div>
            </div>
          </div>

          <div class="feature-item">
            <div class="feature-icon">✦</div>
            <div class="feature-main">
              <div class="feature-title">正在发生的，都能被接住</div>
              <div class="feature-desc">
                当重要的事能够被看见，很多零散的部分就会清楚起来
              </div>
            </div>
          </div>

          <div class="feature-item">
            <div class="feature-icon">✦</div>
            <div class="feature-main">
              <div class="feature-title">走过的痕迹，都还留在原处</div>
              <div class="feature-desc">
                那些来不及整理、却又不想丢下的记录，都有地方安放
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="left-footer">© 2025 MoBe. All rights reserved.</div>

      <!-- 夜空氛围 -->
      <div class="bg-glow bg-glow-top" />
      <div class="bg-glow bg-glow-bottom" />

      <!-- 三条滑动白线 -->
      <div class="sky-line sky-line-1"><span /></div>
      <div class="sky-line sky-line-2"><span /></div>
      <div class="sky-line sky-line-3"><span /></div>

      <!-- 呼吸星星 -->
      <div class="bg-star bg-star-1">✦</div>
      <div class="bg-star bg-star-2">✦</div>
      <div class="bg-star bg-star-3">✦</div>
      <div class="bg-star bg-star-4">✦</div>

      <!-- 流星 -->
      <div class="bg-shooting shooting-1" />
      <div class="bg-shooting shooting-2" />

      <!-- 轻网格 -->
      <div class="bg-grid" />
    </div>

    <div class="login-right">
      <div class="login-panel">
        <div class="panel-header">
          <h2 class="panel-title">欢迎回来</h2>
          <p class="panel-subtitle">登录你的 MoBe 账号以继续</p>
        </div>

        <div class="login-type-tabs">
          <button
            class="login-type-btn"
            :class="{ 'login-type-btn--active': loginType === 'password' }"
            @click="switchLoginType('password')"
          >
            密码登录
          </button>
          <button
            class="login-type-btn"
            :class="{ 'login-type-btn--active': loginType === 'code' }"
            @click="switchLoginType('code')"
          >
            验证码登录
          </button>
        </div>

        <form class="login-form" @submit.prevent="handleSubmit">
          <div class="form-item">
            <label class="form-label">手机号 / 账号</label>
            <UInput
              v-model="form.account"
              size="xl"
              placeholder="请输入手机号或账号"
              class="form-input"
            />
          </div>

          <div v-if="loginType === 'password'" class="form-item">
            <label class="form-label">密码</label>
            <UInput
              v-model="form.password"
              size="xl"
              :type="showPassword ? 'text' : 'password'"
              placeholder="请输入密码"
              class="form-input"
            >
              <template #trailing>
                <button
                  type="button"
                  class="ghost-btn"
                  @click="showPassword = !showPassword"
                >
                  {{ showPassword ? '隐藏' : '显示' }}
                </button>
              </template>
            </UInput>
            <div v-if="loginType === 'password'" class="form-item">
              <label class="form-label">图形验证码</label>
              <div class="code-row">
                <UInput
                  v-model="captchaCode"
                  size="xl"
                  placeholder="请输入图形验证码"
                  class="form-input code-input"
                />
                <button type="button" class="captcha-box" @click="loadCaptcha">
                  <img
                    v-if="captchaImage"
                    :src="captchaImage"
                    alt="验证码"
                    class="captcha-img"
                  />
                  <span v-else>加载中</span>
                </button>
              </div>
            </div>
          </div>

          <div v-else class="form-item">
            <label class="form-label">验证码</label>
            <div class="code-row">
              <UInput
                v-model="emailCode"
                size="xl"
                placeholder="请输入验证码"
                class="form-input code-input"
              />
              <button type="button" class="code-btn" @click="handleSendCode">
                {{ codeCountdown > 0 ? `${codeCountdown}s` : '获取验证码' }}
              </button>
            </div>
          </div>

          <div class="form-actions">
            <label class="remember-wrap">
              <UCheckbox v-model="form.remember" />
              <span>记住我</span>
            </label>

            <button type="button" class="link-btn">忘记密码?</button>
          </div>

          <UButton type="submit" size="xl" block class="submit-btn">
            登录
            <span class="submit-arrow">→</span>
          </UButton>
        </form>

        <div class="divider">
          <span>或</span>
        </div>

        <div class="wechat-shortcut">
          <button
            class="wechat-btn"
            type="button"
            @click="showWechatModal = true"
          >
            <img
              src="/assets/icons/auth/wechat.svg"
              alt="微信登录"
              class="wechat-btn-icon"
            />
          </button>
        </div>

        <Teleport to="body">
          <Transition name="wechat-modal-fade">
            <div
              v-if="showWechatModal"
              class="wechat-modal-mask"
              @click="showWechatModal = false"
            >
              <div class="wechat-modal" @click.stop>
                <div class="wechat-modal-qrcode-frame">
                  <div class="wechat-modal-qrcode">二维码区域</div>
                  <div class="wechat-modal-tip">请使用微信扫码</div>
                </div>
              </div>
            </div>
          </Transition>
        </Teleport>

        <p class="agreement">
          登录即表示同意
          <a href="javascript:void(0)">服务条款</a>
          和
          <a href="javascript:void(0)">隐私政策</a>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(420px, 0.92fr);
  background: #f8faf7;
}

/* ===== 左侧夜空区 ===== */
.login-left {
  position: relative;
  overflow: hidden;
  background:
    radial-gradient(
      circle at 82% 10%,
      rgba(255, 255, 255, 0.06),
      transparent 18%
    ),
    radial-gradient(
      circle at 10% 78%,
      rgba(255, 255, 255, 0.05),
      transparent 16%
    ),
    linear-gradient(180deg, #050608 0%, #090b0f 52%, #0b0d12 100%);
  color: #fff;
  padding: 40px 48px 32px 84px;
  display: flex;
  flex-direction: column;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
  z-index: 3;
}

.brand-logo {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(180deg, #19dc86 0%, #10c66e 100%);
  color: #fff;
  display: grid;
  place-items: center;
  font-weight: 700;
  box-shadow: 0 10px 30px rgba(25, 220, 134, 0.18);
}

.brand-text {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 0.2px;
}

.left-content {
  position: relative;
  z-index: 3;
  margin-top: 72px;
  margin-left: 22px;
  max-width: 560px;
}

.left-title {
  margin: 0;
  max-width: 620px;
  font-size: 40px;
  line-height: 1.18;
  font-weight: 800;
  letter-spacing: -1px;
  white-space: nowrap;
}

.left-desc {
  margin: 30px 0 0;
  max-width: 520px;
  color: rgba(255, 255, 255, 0.76);
  font-size: 17px;
  line-height: 1.9;
}

.feature-list {
  margin-top: 64px;
  display: flex;
  flex-direction: column;
  gap: 28px;
  max-width: 520px;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 14px 0;
  border-radius: 18px;
  position: relative;
}

.feature-item:nth-child(2) {
  margin-left: 28px;
}

.feature-item:nth-child(3) {
  margin-left: 12px;
}

.feature-icon {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.08),
    rgba(255, 255, 255, 0.04)
  );
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #ffffff;
  display: grid;
  place-items: center;
  font-size: 18px;
  flex-shrink: 0;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.16);
}

.feature-title {
  font-size: 18px;
  font-weight: 700;
  line-height: 1.45;
}

.feature-desc {
  margin-top: 6px;
  color: rgba(255, 255, 255, 0.58);
  font-size: 14px;
  line-height: 1.75;
}

.left-footer {
  position: relative;
  z-index: 3;
  margin-top: auto;
  margin-left: 22px;
  color: rgba(255, 255, 255, 0.36);
  font-size: 14px;
}

/* ===== 呼吸光晕 ===== */
.bg-glow {
  position: absolute;
  border-radius: 999px;
  filter: blur(90px);
  pointer-events: none;
}

.bg-glow-top,
.bg-glow-bottom {
  animation: glowBreath 7s ease-in-out infinite;
}

.bg-glow-top {
  top: -20px;
  right: -40px;
  width: 220px;
  height: 220px;
  background: rgba(255, 255, 255, 0.06);
}

.bg-glow-bottom {
  left: -60px;
  bottom: 60px;
  width: 240px;
  height: 240px;
  background: rgba(255, 255, 255, 0.05);
  animation-delay: 1.8s;
}

@keyframes glowBreath {
  0%,
  100% {
    opacity: 0.45;
    transform: scale(1);
  }
  50% {
    opacity: 0.88;
    transform: scale(1.08);
  }
}

/* ===== 三条白色滑动弧线 ===== */
.sky-line {
  position: absolute;
  width: 380px;
  height: 82px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 50%;
  pointer-events: none;
  overflow: hidden;
  z-index: 1;
}

.sky-line span {
  position: absolute;
  left: -28%;
  bottom: -1px;
  width: 110px;
  height: 2px;
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0),
    rgba(255, 255, 255, 0.95),
    rgba(255, 255, 255, 0)
  );
  filter: blur(0.3px);
}

.sky-line-1 {
  left: -40px;
  top: 110px;
  transform: rotate(-8deg);
}

.sky-line-1 span {
  animation: lineFlow 5.2s linear infinite;
}

.sky-line-2 {
  left: 40px;
  bottom: 190px;
  width: 440px;
  height: 96px;
  transform: rotate(4deg);
}

.sky-line-2 span {
  animation: lineFlow 6.4s linear infinite;
  animation-delay: 1.1s;
}

.sky-line-3 {
  left: 220px;
  top: 300px;
  width: 300px;
  height: 72px;
  transform: rotate(-12deg);
}

.sky-line-3 span {
  animation: lineFlow 5.8s linear infinite;
  animation-delay: 2.3s;
}

@keyframes lineFlow {
  0% {
    left: -30%;
    opacity: 0;
  }
  8% {
    opacity: 1;
  }
  92% {
    opacity: 0.9;
  }
  100% {
    left: 100%;
    opacity: 0;
  }
}

/* ===== 白色呼吸星星 ===== */
.bg-star {
  position: absolute;
  color: rgba(255, 255, 255, 0.88);
  text-shadow: 0 0 12px rgba(255, 255, 255, 0.32);
  line-height: 1;
  pointer-events: none;
  z-index: 1;
  animation: starBreath 4.8s ease-in-out infinite;
}

.bg-star-1 {
  left: 90px;
  top: 160px;
  font-size: 14px;
}

.bg-star-2 {
  right: 120px;
  top: 130px;
  font-size: 12px;
  animation-delay: 1.2s;
}

.bg-star-3 {
  left: 150px;
  bottom: 150px;
  font-size: 10px;
  animation-delay: 2s;
}

.bg-star-4 {
  right: 80px;
  bottom: 210px;
  font-size: 16px;
  animation-delay: 2.8s;
}

@keyframes starBreath {
  0%,
  100% {
    opacity: 0.28;
    transform: scale(1);
  }
  50% {
    opacity: 0.95;
    transform: scale(1.18);
  }
}

/* ===== 白色流星 ===== */
.bg-shooting {
  position: absolute;
  width: 96px;
  height: 1px;
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0),
    rgba(255, 255, 255, 0.95),
    rgba(255, 255, 255, 0)
  );
  transform: rotate(-28deg);
  opacity: 0;
  pointer-events: none;
  filter: blur(0.15px);
  z-index: 1;
}

.shooting-1 {
  top: 120px;
  right: 110px;
  animation: shootingStar 8s linear infinite;
}

.shooting-2 {
  top: 420px;
  right: 40px;
  animation: shootingStar 10s linear infinite;
  animation-delay: 3.6s;
}

@keyframes shootingStar {
  0%,
  78% {
    opacity: 0;
    transform: translate(0, 0) rotate(-28deg);
  }
  82% {
    opacity: 0.95;
  }
  100% {
    opacity: 0;
    transform: translate(-130px, 86px) rotate(-28deg);
  }
}

/* ===== 轻网格 ===== */
.bg-grid {
  position: absolute;
  right: 56px;
  bottom: 48px;
  width: 120px;
  height: 120px;
  background-image: radial-gradient(
    rgba(255, 255, 255, 0.22) 1.1px,
    transparent 1.1px
  );
  background-size: 20px 20px;
  opacity: 0.45;
  z-index: 1;
}

/* ===== 右侧登录区 ===== */
.login-right {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 64px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(250, 252, 249, 0.98)),
    #f8faf7;
}

.login-panel {
  width: 100%;
  max-width: 388px;
  border: 1px solid #e7eee5;
  border-radius: 22px;
  background: #ffffff;
  padding: 32px;
  box-shadow: 0 18px 52px rgba(38, 53, 42, 0.06);
}

.panel-header {
  margin-bottom: 22px;
}

.panel-title {
  margin: 0;
  font-size: 30px;
  line-height: 1.2;
  font-weight: 720;
  color: #18181b;
  letter-spacing: 0;
}

.panel-subtitle {
  margin: 8px 0 0;
  font-size: 14px;
  color: #899489;
  line-height: 1.6;
}

.login-type-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 22px;
  border: 1px solid #e6ede3;
  border-radius: 12px;
  background: #fbfdfb;
  padding: 3px;
}

.login-type-btn {
  height: 32px;
  flex: 1;
  padding: 0 14px;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: #78857a;
  font-size: 14px;
  cursor: pointer;
}

.login-type-btn--active {
  background: #eef8f2;
  color: #172018;
  font-weight: 600;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 600;
  color: #354238;
}

.form-input :deep(.ui-input) {
  border-radius: 11px;
  background: #ffffff;
  border-color: #dfe8dc;
  box-shadow: none;
}

.form-input :deep(input) {
  height: 38px;
  font-size: 14px;
  color: #1f1f23;
}

.form-input :deep(.ui-input:focus-within) {
  border-color: rgba(20, 201, 111, 0.55);
  box-shadow: 0 0 0 3px rgba(20, 201, 111, 0.08);
}

.ghost-btn {
  border: none;
  background: transparent;
  color: #9a9aa4;
  font-size: 13px;
  cursor: pointer;
}

.code-row {
  display: flex;
  gap: 10px;
}

.code-input {
  flex: 1;
}

.code-btn {
  flex-shrink: 0;
  min-width: 112px;
  height: 38px;
  padding: 0 14px;
  border: 1px solid #dbe8dc;
  border-radius: 11px;
  background: #f8fcf8;
  color: #0f9f68;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.code-btn:hover {
  background: #f1fbf5;
  border-color: #c8ecd3;
}

.form-actions {
  margin-top: -4px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.remember-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #7f8a80;
  font-size: 13px;
}

.link-btn {
  border: none;
  background: transparent;
  color: #0f9f68;
  font-size: 13px;
  cursor: pointer;
}

.submit-btn {
  height: 46px;
  margin-top: 2px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 700;
  justify-content: center;
}

.submit-arrow {
  margin-left: 8px;
}

.divider {
  margin: 22px 0 16px;
  display: flex;
  align-items: center;
  color: #a5afa6;
  font-size: 13px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e7e7ec;
}

.divider span {
  padding: 0 16px;
}

.wechat-shortcut {
  display: flex;
  justify-content: center;
}

.wechat-btn {
  width: 44px;
  height: 44px;
  border: 1px solid #e1e9de;
  border-radius: 12px;
  background: #ffffff;
  display: grid;
  place-items: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.wechat-btn:hover {
  background: #eef7f2;
}

.wechat-btn-icon {
  width: 21px;
  height: 21px;
  display: block;
}

/* 微信图标建议路径：/public/assets/icons/auth/wechat.svg */

.wechat-modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(180, 186, 195, 0.58);
  backdrop-filter: blur(12px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.wechat-modal {
  width: 360px;
  padding: 0;
  background: transparent;
  box-shadow: none;
}

.wechat-modal-qrcode-frame {
  width: 100%;
  aspect-ratio: 1 / 1;
  background: #ffffff;
  border-radius: 28px;
  padding: 24px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-shadow: 0 28px 90px rgba(0, 0, 0, 0.28);
}

.wechat-modal-qrcode {
  flex: 1;
  width: 100%;
  border-radius: 18px;
  background: #f4f5f7;
  display: grid;
  place-items: center;
  color: #a1a1aa;
  font-size: 15px;
  border: 1px solid #e4e5e8;
  box-sizing: border-box;
}

.wechat-modal-tip {
  margin-top: 18px;
  text-align: center;
  color: rgba(0, 0, 0, 0.88);
  font-size: 15px;
  line-height: 1.5;
}

.wechat-modal-fade-enter-active,
.wechat-modal-fade-leave-active {
  transition: opacity 0.24s ease;
}

.wechat-modal-fade-enter-active .wechat-modal,
.wechat-modal-fade-leave-active .wechat-modal {
  transition:
    transform 0.28s ease,
    opacity 0.28s ease;
}

.wechat-modal-fade-enter-from,
.wechat-modal-fade-leave-to {
  opacity: 0;
}

.wechat-modal-fade-enter-from .wechat-modal,
.wechat-modal-fade-leave-to .wechat-modal {
  transform: translateY(10px) scale(0.96);
  opacity: 0;
}

.agreement {
  margin: 20px 0 0;
  text-align: center;
  color: #9aa49b;
  font-size: 12px;
  line-height: 1.7;
}

.agreement a {
  color: #14c96f;
  text-decoration: none;
}

.captcha-box {
  width: 112px;
  height: 38px;
  border: 1px solid #dbe8dc;
  border-radius: 11px;
  background: #f8fcf8;
  overflow: hidden;
  display: grid;
  place-items: center;
  cursor: pointer;
  padding: 0;
}

.captcha-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

@media (max-width: 1200px) {
  .login-page {
    grid-template-columns: 1fr;
  }

  .login-left {
    min-height: 420px;
    padding-left: 48px;
  }

  .left-title {
    white-space: normal;
    font-size: 42px;
  }

  .left-content,
  .left-footer {
    margin-left: 0;
  }

  .login-right {
    padding: 40px 24px 56px;
  }

  .login-panel {
    max-width: 420px;
  }
}

@media (max-width: 768px) {
  .login-left {
    padding: 28px 24px;
  }

  .left-content {
    margin-top: 48px;
  }

  .left-title {
    font-size: 34px;
    white-space: normal;
  }

  .left-desc {
    font-size: 16px;
    line-height: 1.8;
  }

  .feature-list {
    margin-top: 44px;
    gap: 24px;
  }

  .feature-item:nth-child(2),
  .feature-item:nth-child(3) {
    margin-left: 0;
  }

  .panel-title {
    font-size: 30px;
  }

  .login-panel {
    padding: 28px 22px;
    border-radius: 24px;
  }

  .code-row {
    flex-direction: column;
  }

  .sky-line-3 {
    display: none;
  }

  .wechat-modal {
    width: min(88vw, 360px);
  }
}
</style>
