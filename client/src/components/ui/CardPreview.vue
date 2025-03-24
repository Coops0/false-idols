<template>
  <div :class="cardColor">
    {{ card.description }}
    {{ card.consequence }}
  </div>
</template>

<script lang="ts" setup>
import type { Card } from '@/game/messages.ts';
import { computed } from 'vue';

const props = defineProps<{ card: Card }>();

const cardColor = computed(() => {
  if (props.card.consequence_qualifier === 'NEUTRAL') return 'bg-gray-200';
  if (props.card.consequence_qualifier === 'NEGATIVE') return 'bg-red-200';
  return 'bg-green-200';
});
</script>