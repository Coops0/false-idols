<template>
  <div class="relative group">
    <div class="bg-white/50 backdrop-blur-xl rounded-xl p-6 border border-gray-100/50">
      <div class="space-y-4">
        <div class="flex justify-between items-start">
          <div :class="CONSEQUENCE_QUALIFIER_CLASSES[card.consequence_qualifier].text" class="text-2xl font-bold">
            {{ sign(card.consequence) }}{{ card.consequence }}
          </div>
        </div>

        <p class="text-gray-900 text-lg">{{ card.description }}</p>

        <div class="flex justify-end">
          <div :class="CONSEQUENCE_QUALIFIER_CLASSES[card.consequence_qualifier].badge"
               class="text-sm font-medium px-3 py-1 rounded-full border">
            {{ card.consequence_qualifier }}
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { Card } from '@/game/state.ts';

const CONSEQUENCE_QUALIFIER_CLASSES = {
  POSITIVE: {
    text: 'text-blue-600',
    badge: 'bg-blue-50 text-blue-600 border-blue-100'
  } as const,
  NEGATIVE: {
    text: 'text-red-600',
    badge: 'bg-red-50 text-red-600 border-red-100'
  } as const,
  NEUTRAL: {
    text: 'text-gray-600',
    badge: 'bg-gray-50 text-gray-600 border-gray-100'
  } as const
} as const;

defineProps<{ card: Card }>();

const sign = (value: number) => {
  if (value > 0) return '+';
  if (value < 0) return '-';
  return '';
};
</script> 