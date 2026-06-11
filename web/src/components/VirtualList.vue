<template>
  <div ref="scrollerRef" class="virtual-list" :style="{ height: `${height}px` }" @scroll="handleScroll">
    <div class="virtual-list-phantom" :style="{ height: `${totalHeight}px` }"></div>
    <div class="virtual-list-content" :style="{ top: `${offsetY}px` }">
      <div
        v-for="(item, index) in visibleItems"
        :key="startIndex + index"
        class="virtual-list-item"
        :style="{ height: `${itemSize}px` }"
      >
        <slot :item="item" :index="startIndex + index" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, watch } from 'vue'

const props = withDefaults(
  defineProps<{
    items: any[]
    height: number
    itemSize: number
    overscan?: number
  }>(),
  {
    overscan: 4
  }
)

const scrollerRef = ref<HTMLElement | null>(null)
const scrollTop = ref(0)

const safeHeight = computed(() => Math.max(0, props.height || 0))
const safeItemSize = computed(() => Math.max(1, props.itemSize || 1))
const totalHeight = computed(() => props.items.length * safeItemSize.value)
const startIndex = computed(() => Math.max(0, Math.floor(scrollTop.value / safeItemSize.value) - props.overscan))
const visibleCount = computed(() => Math.ceil(safeHeight.value / safeItemSize.value) + props.overscan * 2)
const endIndex = computed(() => Math.min(props.items.length, startIndex.value + visibleCount.value))
const visibleItems = computed(() => props.items.slice(startIndex.value, endIndex.value))
const offsetY = computed(() => startIndex.value * safeItemSize.value)

const handleScroll = (event: Event) => {
  const target = event.target as HTMLElement
  scrollTop.value = target.scrollTop
}

watch(
  () => props.items.length,
  () => {
    const maxScrollTop = Math.max(0, totalHeight.value - safeHeight.value)
    if (scrollTop.value <= maxScrollTop) return
    scrollTop.value = maxScrollTop
    if (scrollerRef.value) {
      scrollerRef.value.scrollTop = maxScrollTop
    }
  }
)
</script>

<style scoped>
.virtual-list {
  position: relative;
  overflow: auto;
  width: 100%;
}

.virtual-list-phantom {
  width: 1px;
  opacity: 0;
  pointer-events: none;
}

.virtual-list-content {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
}

.virtual-list-item {
  box-sizing: border-box;
  width: 100%;
}
</style>
