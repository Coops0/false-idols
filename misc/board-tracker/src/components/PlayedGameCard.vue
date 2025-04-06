<template>
  <div class="bg-black/70 overflow-hidden rounded-lg">
    <div class="line-clamp-3">
      {{ card.description }}
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Card } from '@/App.vue';

defineProps<{ card: Card }>();
</script>