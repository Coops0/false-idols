<template>
  <div class="rounded-md p-1.5 border border-gray-100/50 shadow-sm size-full" :class="{
    'bg-red-300/70': card.consequence_qualifier === 'NEGATIVE',
    'bg-blue-300/70': card.consequence_qualifier === 'POSITIVE',
    'bg-gray-300/70': card.consequence_qualifier === 'NEUTRAL',
  }">
    <div class="flex flex-col h-full">
      <div :class="{
        'text-red-600': card.consequence_qualifier === 'NEGATIVE',
        'text-blue-600': card.consequence_qualifier === 'POSITIVE',
        'text-gray-600': card.consequence_qualifier === 'NEUTRAL'
      }" class="text-xs font-bold">
        {{ card.consequence > 0 ? '+' : '' }}{{ card.consequence }}
      </div>

      <div class="flex-grow">
        <p class="text-xs text-gray-900 line-clamp-3">{{ card.description }}</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { type Card } from '@/game/state.ts';

defineProps<{ card: Card }>();
</script>