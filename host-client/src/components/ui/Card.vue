<template>
  <div :class="cardColor">
    <div v-if="card !== null">
      <p>{{ card.description }}</p>
      <p>{{ card.consequence }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Card } from '@/game/state.ts';
import { computed } from 'vue';

const props = defineProps<{ card: Card | null }>();
const cardColor = computed(() => {
  if (props.card === null || props.card.consequence_qualifier === 'NEUTRAL') {
    return 'bg-gray-200';
  }

  if (props.card.consequence_qualifier === 'NEGATIVE') {
    return 'bg-red-200';
  }

  return 'bg-green-200';
});
</script>