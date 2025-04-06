<template>
  <div class="flex w-full">
    <img class="object-scale-down" :src="boardImage" />
  </div>
</template>

<script setup lang="ts">
import type { Card } from '@/App.vue';
import { computed } from 'vue';
import Board56Image from '@/assets/board/board-demon-5-6.png';
import Board78Image from '@/assets/board/board-demon-7-8.png';
import Board910Image from '@/assets/board/board-demon-9-10.png';

const props = defineProps<{
  cards: Card[];
  playersSize: number;
}>();

const negativeCards = computed(() => props.cards.filter(card => card.consequence === 'NEGATIVE'));
const boardVariant = computed<1 | 2 | 3>(() => {
  const s = props.playersSize;
  if (s === 4 || s === 5 || s === 6) return 1;
  if (s === 7 || s === 8) return 2;
  return 3;
});

// all 1601 x 539
const boardImage = computed(() => {
  switch (boardVariant.value) {
    case 1:
      return Board56Image;
    case 2:
      return Board78Image;
    case 3:
      return Board910Image;
  }
});
</script>