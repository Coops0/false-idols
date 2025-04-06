<template>
  <div
      :class="{
      'bg-gray-100 border-gray-300': card.consequence === CardConsequence.NEUTRAL,
      'bg-red-100 border-red-300': card.consequence === CardConsequence.NEGATIVE,
      'bg-green-100 border-green-300': card.consequence === CardConsequence.POSITIVE
    }"
      class="w-64 p-4 rounded-lg shadow-md border-2"
  >
    <div class="space-y-2">
      <p class="text-gray-800 text-xs font-medium">{{ card.description }}</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { type Card, CardConsequence } from '@/game/messages.ts';

defineProps<{ card: Card }>();
</script>