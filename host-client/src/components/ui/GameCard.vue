<template>
  <div class="relative group cursor-pointer">
    <div
        class="absolute inset-0 bg-gradient-to-br from-amber-100 to-amber-200 rounded-lg shadow-lg transform transition-all duration-300 group-hover:scale-105 group-hover:shadow-xl"/>
    <div class="relative p-6 border-2 border-amber-300 rounded-lg bg-gradient-to-br from-amber-50 to-amber-100">
      <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-amber-400 to-amber-200"/>
      <div class="absolute bottom-0 left-0 w-full h-1 bg-gradient-to-r from-amber-200 to-amber-400"/>
      <div class="absolute left-0 top-0 bottom-0 w-1 bg-gradient-to-b from-amber-400 to-amber-200"/>
      <div class="absolute right-0 top-0 bottom-0 w-1 bg-gradient-to-b from-amber-200 to-amber-400"/>

      <div class="relative z-10 space-y-4">
        <div class="flex justify-between items-start">
          <div :class="[
            card.consequence_qualifier === 'POSITIVE' ? 'text-blue-600' :
            card.consequence_qualifier === 'NEGATIVE' ? 'text-red-600' :
            'text-amber-600'
          ]" class="text-2xl font-bold">
            {{ card.consequence > 0 ? '+' : '' }}{{ card.consequence }}
          </div>
        </div>

        <p class="text-amber-900 text-lg">{{ card.description }}</p>

        <div class="flex justify-end">
          <div :class="[
            card.consequence_qualifier === 'POSITIVE' ? 'bg-blue-100 text-blue-800' :
            card.consequence_qualifier === 'NEGATIVE' ? 'bg-red-100 text-red-800' :
            'bg-amber-100 text-amber-800'
          ]" class="text-sm font-medium px-3 py-1 rounded-full">
            {{ card.consequence_qualifier }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { Card } from '@/game/state.ts';

defineProps<{ card: Card }>();
</script> 