<script setup lang="ts">
const { currentUser } = useAuth()

const userName = computed(() => currentUser.value?.nickname || '李明')
const userEmail = computed(() => currentUser.value?.email || 'liming@mobe.app')
</script>

<template>
  <section class="mobe-profile">
    <div class="mobe-profile__avatar">
      {{ userName.slice(0, 1).toUpperCase() }}
    </div>
    <div>
      <p class="mobe-profile__eyebrow">个人中心</p>
      <h2>{{ userName }}</h2>
      <p>{{ userEmail }}</p>
    </div>
  </section>
</template>

<style scoped>
.mobe-profile {
  display: flex;
  align-items: center;
  gap: 18px;
  border: 1px solid #edf1eb;
  border-radius: 14px;
  background: #ffffff;
  padding: 28px;
}

.mobe-profile__avatar {
  display: grid;
  width: 54px;
  height: 54px;
  place-items: center;
  border-radius: 999px;
  background: #172018;
  color: #ffffff;
  font-size: 20px;
  font-weight: 760;
}

.mobe-profile__eyebrow {
  margin: 0 0 6px;
  color: #7a867d;
  font-size: 13px;
}

h2 {
  margin: 0 0 6px;
  color: #172018;
  font-size: 24px;
}

p {
  margin: 0;
  color: #647067;
}
</style>
