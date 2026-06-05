<template>
  <div class="dashboard-page">
    <div class="content-grid">
      <div class="grid-col left-col">
        <SnippetCard />
      </div>
      <div class="grid-col right-area">
        <div class="bottom-row">
          <DashboardStats />
        </div>
        <div class="top-row">
          <NoteCard />
          <InterviewCard />
          <SystemCard />
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import SnippetCard from '../components/SnippetCard.vue'
import NoteCard from '../components/NoteCard.vue'
import InterviewCard from '../components/InterviewCard.vue'
import SystemCard from '../components/SystemCard.vue'
import DashboardStats from '../components/DashboardStats.vue'
</script>

<style scoped>
.dashboard-page {
  height: calc(100% - 48px);
  padding: 24px;
  display: flex;
  flex-direction: column;
}

.content-grid {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 24px;
  flex: 1;
  min-height: 0;
}
  

.right-area {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.top-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  height: 400px;
}

.bottom-row {
  flex: 1;
  min-height: 280px;
}

@media (max-width: 1400px) {
  .content-grid {
    grid-template-columns: 280px 1fr;
  }
}
</style>
