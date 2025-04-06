<template>

</template>

<script setup lang="ts">
import type { Card } from '@/App.vue';
import { computed } from 'vue';

const props = defineProps<{
  cards: Card[];
  playersSize: number;
  failedElections: number;
}>();

const positiveCards = computed(() => props.cards.filter(card => card.consequence === 'POSITIVE'));
</script>