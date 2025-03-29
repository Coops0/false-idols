<template>
  <div class="relative group">
    <div class="bg-white/50 backdrop-blur-sm rounded-xl p-6 border border-gray-100/50  transition-all duration-300">
      <div class="space-y-4">
        <div class="flex justify-between items-start">
          <div :class="[
            card.consequence_qualifier === 'POSITIVE' ? 'text-blue-600' :
            card.consequence_qualifier === 'NEGATIVE' ? 'text-red-600' :
            'text-purple-600'
          ]" class="text-2xl font-bold">
            {{ card.consequence > 0 ? '+' : '' }}{{ card.consequence }}
          </div>
        </div>

        <p class="text-gray-900 text-lg">{{ card.description }}</p>

        <div class="flex justify-end">
          <div :class="[
            card.consequence_qualifier === 'POSITIVE' ? 'bg-blue-50 text-blue-600 border-blue-100' :
            card.consequence_qualifier === 'NEGATIVE' ? 'bg-red-50 text-red-600 border-red-100' :
            'bg-purple-50 text-purple-600 border-purple-100'
          ]" class="text-sm font-medium px-3 py-1 rounded-full border">
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