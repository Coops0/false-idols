<template>
  <div class="flex w-full">
    <img class="object-scale-down" :src="BoardImage"/>
  </div>
</template>

<script setup lang="ts">
import type { Card } from '@/App.vue';
import { computed } from 'vue';
import BoardImage from '@/assets/board/board-angel.png';

// 1407x541

const props = defineProps<{
  cards: Card[];
  playersSize: number;
  failedElections: number;
}>();

const positiveCards = computed(() => props.cards.filter(card => card.consequence === 'POSITIVE'));
</script>