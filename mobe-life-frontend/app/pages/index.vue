<script setup lang="ts">
definePageMeta({
  middleware: 'auth',
})

const { currentUser, fetchCurrentUser } = useAuth()

onMounted(async () => {
  if (!currentUser.value) {
    try {
      await fetchCurrentUser()
    } catch (error) {
      console.error('获取当前用户失败', error)
    }
  }
})
</script>

<template>
  <div>首页，登录成功后再往下搭</div>
</template>
