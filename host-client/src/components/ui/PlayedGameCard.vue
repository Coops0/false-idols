<template>
  <div class="border-white border-4 overflow-hidden rounded-lg flex justify-center items-center" :class="{
    'bg-red-700': variant === 'demon',
    'bg-blue-600': variant === 'angel'
  }">
    <div class="bg-white text-gray-700 line-clamp-3 h-6/7 w-3/4 text-xs flex text-center justify-center items-center rounded-sm">
      {{ card.description }}
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Card } from '@/game/state.ts';

defineProps<{
  card: Card;
  variant: 'demon' | 'angel';
}>();
</script>