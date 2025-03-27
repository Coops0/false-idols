<template>
  <div
      :class="{
      'bg-gray-100 border-gray-300': card.consequence_qualifier === 'NEUTRAL',
      'bg-red-100 border-red-300': card.consequence_qualifier === 'NEGATIVE',
      'bg-green-100 border-green-300': card.consequence_qualifier === 'POSITIVE'
    }"
      class="w-64 p-4 rounded-lg shadow-md border-2 transition-all duration-200 hover:shadow-lg"
  >
    <div class="space-y-2">
      <p class="text-gray-800 font-medium">{{ card.description }}</p>
      <p
          :class="{
          'text-gray-600': card.consequence_qualifier === 'NEUTRAL',
          'text-red-700': card.consequence_qualifier === 'NEGATIVE',
          'text-green-700': card.consequence_qualifier === 'POSITIVE'
        }"
          class="text-sm"
      >
        {{ card.consequence }}
      </p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { Card } from '@/game/messages.ts';

defineProps<{ card: Card }>();
</script>