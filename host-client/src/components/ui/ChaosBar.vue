<template>
  <div class="size-full flex items-center justify-center">
    <div class="p-2 size-full flex flex-col">
      <div class="flex items-center gap-1 mb-1">
        <div
            class="w-4 h-4 rounded-md bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center text-white text-xs font-bold">
          ⚠️
        </div>
        <span class="text-gray-900 font-medium text-xs">Failed Elections</span>
      </div>
      <div class="flex-grow flex items-center justify-center">
        <div class="flex items-center gap-2">
          <div v-for="i in 3" :key="i"
               :class="i <= game.failed_elections ? 'bg-gradient-to-br from-red-500 to-red-600 border-red-400/50 shadow-sm' : 'bg-gray-100/50 border-gray-200/50'"
               class="w-5 h-5 rounded-full border transition-all duration-300 flex items-center justify-center text-white text-xs font-bold">
            {{ i }}
          </div>
        </div>
      </div>
      <div v-if="game.failed_elections === 3" class="text-center mt-1">
        <span class="text-xs text-red-600 font-medium">Chaos!</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { InProgressGameState } from '@/game/state.ts';

defineProps<{ game: InProgressGameState }>();
</script>