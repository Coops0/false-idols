<template>

</template>

<script setup lang="ts">
import type { Card } from '@/App.vue';
import { computed } from 'vue';

const props = defineProps<{
  cards: Card[];
  playersSize: number;
}>();

const negativeCards = computed(() => props.cards.filter(card => card.consequence === 'NEGATIVE'));
const boardVariant = computed<1 | 2 | 3>(() => {
  const s = props.playersSize;
  if (s === 4 || s === 5 || s === 6) {
    return 1;
  }

  if (s === 7 || s === 8) {
    return 2;
  }

  return 3;
});
</script>