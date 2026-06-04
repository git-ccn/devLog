<script setup lang="ts">
</script>

<template>
  <!-- 使用 v-slot 配合 keep-alive 缓存路由组件状态 -->
  <router-view v-slot="{ Component }">
    <keep-alive>
      <component :is="Component" />
    </keep-alive>
  </router-view>
</template>

<style scoped>
</style>
