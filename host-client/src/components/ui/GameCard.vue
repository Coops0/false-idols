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

      <div class="flex justify-end">
        <div class="text-xs truncate px-1 py-0.5 rounded-full text-xs" :class="{
          'bg-red-50 text-red-600 border-red-100': card.consequence_qualifier === 'NEGATIVE',
          'bg-blue-50 text-blue-600 border-blue-100': card.consequence_qualifier === 'POSITIVE',
          'bg-gray-50 text-gray-600 border-gray-100': card.consequence_qualifier === 'NEUTRAL'
        }">
          {{ card.consequence_qualifier }}
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { type Card } from '@/game/state.ts';

defineProps<{ card: Card }>();
</script>