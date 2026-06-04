<script setup lang="ts">
</script>

<template>
  <!-- 使用keep-alive缓存路由组件状态，保留组件实例避免重复销毁重建 -->
  <keep-alive >
    <router-view />
  </keep-alive>
</template>

<style scoped>
</style>
