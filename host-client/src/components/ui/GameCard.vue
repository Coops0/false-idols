<template>
  <div class="bg-white/70 rounded-md p-1.5 border border-gray-100/50 shadow-sm w-full h-full">
    <div class="flex flex-col h-full">
      <div class="text-xs font-bold" :class="{
        'text-red-600': card.consequence_qualifier === 'NEGATIVE',
        'text-blue-600': card.consequence_qualifier === 'POSITIVE',
        'text-gray-600': card.consequence_qualifier === 'NEUTRAL'
      }">
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